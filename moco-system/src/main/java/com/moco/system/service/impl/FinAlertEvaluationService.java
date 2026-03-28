package com.moco.system.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.moco.system.domain.FinAlertEvent;
import com.moco.system.domain.FinAlertRule;
import com.moco.system.domain.FinHoldingAccount;
import com.moco.system.domain.FinQuoteSnapshot;
import com.moco.system.domain.vo.FinPositionSummary;
import com.moco.system.mapper.FinAlertEventMapper;
import com.moco.system.mapper.FinAlertRuleMapper;
import com.moco.system.mapper.FinHoldingAccountMapper;
import com.moco.system.mapper.FinQuoteSnapshotMapper;

@Service
public class FinAlertEvaluationService
{
    @Autowired
    private FinAlertRuleMapper alertRuleMapper;

    @Autowired
    private FinAlertEventMapper alertEventMapper;

    @Autowired
    private FinQuoteSnapshotMapper quoteSnapshotMapper;

    @Autowired
    private FinHoldingAccountMapper accountMapper;

    @Autowired
    private FinPositionSupportService positionSupportService;

    public void evaluateAll()
    {
        Map<Long, FinQuoteSnapshot> quoteMap = new HashMap<>();
        for (FinQuoteSnapshot quote : quoteSnapshotMapper.selectLatestQuotes())
        {
            quoteMap.put(quote.getAssetId(), quote);
        }

        List<FinPositionSummary> positions = positionSupportService.listPositionSummaries(null, null, null, null);
        Map<String, FinPositionSummary> positionMap = new HashMap<>();
        Map<Long, BigDecimal> familyMarketValueMap = new HashMap<>();
        Map<Long, BigDecimal> memberMarketValueMap = new HashMap<>();
        for (FinPositionSummary position : positions)
        {
            positionMap.put(position.getFamilyId() + "_" + position.getMemberId() + "_" + position.getAssetId(), position);
            FinQuoteSnapshot quote = quoteMap.get(position.getAssetId());
            if (quote == null)
            {
                continue;
            }
            BigDecimal marketValue = quote.getLastPrice().multiply(position.getHoldingQty()).setScale(2, RoundingMode.HALF_UP);
            familyMarketValueMap.merge(position.getFamilyId(), marketValue, BigDecimal::add);
            memberMarketValueMap.merge(position.getMemberId(), marketValue, BigDecimal::add);
        }

        Map<Long, BigDecimal> familyCashMap = new HashMap<>();
        Map<Long, BigDecimal> memberCashMap = new HashMap<>();
        for (FinHoldingAccount account : accountMapper.selectHoldingAccountList(new FinHoldingAccount()))
        {
            BigDecimal cash = account.getCashBalance() == null ? BigDecimal.ZERO : account.getCashBalance();
            familyCashMap.merge(account.getFamilyId(), cash, BigDecimal::add);
            memberCashMap.merge(account.getMemberId(), cash, BigDecimal::add);
        }

        for (FinAlertRule rule : alertRuleMapper.selectEnabledAlertRules())
        {
            if (alertEventMapper.countTodayEventByRuleId(rule.getRuleId()) > 0)
            {
                continue;
            }
            FinQuoteSnapshot quote = rule.getAssetId() == null ? null : quoteMap.get(rule.getAssetId());
            FinPositionSummary position = resolvePosition(positionMap, rule);
            BigDecimal triggerValue = resolveTriggerValue(rule, quote, position, familyMarketValueMap, memberMarketValueMap, familyCashMap, memberCashMap);
            if (triggerValue == null || !isTriggered(rule, triggerValue))
            {
                continue;
            }
            FinAlertEvent event = new FinAlertEvent();
            event.setRuleId(rule.getRuleId());
            event.setRuleName(rule.getRuleName());
            event.setFamilyId(rule.getFamilyId());
            event.setMemberId(rule.getMemberId());
            event.setAssetId(rule.getAssetId());
            event.setRuleType(rule.getRuleType());
            event.setTriggerValue(triggerValue);
            event.setThresholdValue(rule.getThresholdValue());
            event.setSecondThresholdValue(rule.getSecondThresholdValue());
            event.setTriggerTime(new Date());
            event.setStatus("0");
            event.setSuggestionText(rule.getSuggestionText());
            event.setEventTitle(buildEventTitle(rule));
            event.setDetailText(buildDetailText(rule, triggerValue));
            alertEventMapper.insertAlertEvent(event);
            alertRuleMapper.updateLastTriggeredTime(rule.getRuleId());
        }
    }

    private FinPositionSummary resolvePosition(Map<String, FinPositionSummary> positionMap, FinAlertRule rule)
    {
        if (rule.getAssetId() == null)
        {
            return null;
        }
        if (rule.getMemberId() != null)
        {
            return positionMap.get(rule.getFamilyId() + "_" + rule.getMemberId() + "_" + rule.getAssetId());
        }
        for (FinPositionSummary summary : positionMap.values())
        {
            if (rule.getFamilyId() != null && !rule.getFamilyId().equals(summary.getFamilyId()))
            {
                continue;
            }
            if (rule.getAssetId().equals(summary.getAssetId()))
            {
                return summary;
            }
        }
        return null;
    }

    private BigDecimal resolveTriggerValue(FinAlertRule rule, FinQuoteSnapshot quote, FinPositionSummary position,
            Map<Long, BigDecimal> familyMarketValueMap, Map<Long, BigDecimal> memberMarketValueMap,
            Map<Long, BigDecimal> familyCashMap, Map<Long, BigDecimal> memberCashMap)
    {
        String type = rule.getRuleType();
        if ("PRICE_CHANGE_RATE".equals(type) && quote != null)
        {
            return quote.getChangeRate();
        }
        if ("TARGET_PROFIT_RATE".equals(type) && quote != null && position != null && position.getCostAmount().compareTo(BigDecimal.ZERO) > 0)
        {
            BigDecimal marketValue = quote.getLastPrice().multiply(position.getHoldingQty()).setScale(2, RoundingMode.HALF_UP);
            return marketValue.subtract(position.getCostAmount()).add(position.getRealizedProfitAmount())
                .divide(position.getCostAmount(), 4, RoundingMode.HALF_UP);
        }
        if ("DRAWDOWN_RATE".equals(type) && quote != null)
        {
            BigDecimal maxPrice = quoteSnapshotMapper.selectMaxLastPriceByAssetId(rule.getAssetId());
            if (maxPrice == null || maxPrice.compareTo(BigDecimal.ZERO) <= 0)
            {
                return null;
            }
            return maxPrice.subtract(quote.getLastPrice()).divide(maxPrice, 4, RoundingMode.HALF_UP);
        }
        if ("POSITION_RATIO".equals(type) && quote != null && position != null)
        {
            BigDecimal marketValue = quote.getLastPrice().multiply(position.getHoldingQty()).setScale(2, RoundingMode.HALF_UP);
            BigDecimal total = rule.getMemberId() != null
                ? memberMarketValueMap.getOrDefault(rule.getMemberId(), BigDecimal.ZERO)
                : familyMarketValueMap.getOrDefault(rule.getFamilyId(), BigDecimal.ZERO);
            return total.compareTo(BigDecimal.ZERO) > 0 ? marketValue.divide(total, 4, RoundingMode.HALF_UP) : BigDecimal.ZERO;
        }
        if ("TARGET_PRICE_RANGE".equals(type) && quote != null)
        {
            return quote.getLastPrice();
        }
        if ("CASH_RATIO_LOW".equals(type))
        {
            BigDecimal cash = rule.getMemberId() != null
                ? memberCashMap.getOrDefault(rule.getMemberId(), BigDecimal.ZERO)
                : familyCashMap.getOrDefault(rule.getFamilyId(), BigDecimal.ZERO);
            BigDecimal market = rule.getMemberId() != null
                ? memberMarketValueMap.getOrDefault(rule.getMemberId(), BigDecimal.ZERO)
                : familyMarketValueMap.getOrDefault(rule.getFamilyId(), BigDecimal.ZERO);
            BigDecimal total = cash.add(market);
            return total.compareTo(BigDecimal.ZERO) > 0 ? cash.divide(total, 4, RoundingMode.HALF_UP) : BigDecimal.ZERO;
        }
        return null;
    }

    private boolean isTriggered(FinAlertRule rule, BigDecimal triggerValue)
    {
        if ("TARGET_PRICE_RANGE".equals(rule.getRuleType()))
        {
            if (rule.getSecondThresholdValue() == null)
            {
                return triggerValue.compareTo(rule.getThresholdValue()) >= 0;
            }
            BigDecimal min = rule.getThresholdValue().min(rule.getSecondThresholdValue());
            BigDecimal max = rule.getThresholdValue().max(rule.getSecondThresholdValue());
            return triggerValue.compareTo(min) >= 0 && triggerValue.compareTo(max) <= 0;
        }
        return triggerValue.compareTo(rule.getThresholdValue()) >= 0;
    }

    private String buildEventTitle(FinAlertRule rule)
    {
        return "规则命中：" + rule.getRuleName();
    }

    private String buildDetailText(FinAlertRule rule, BigDecimal triggerValue)
    {
        return "规则[" + rule.getRuleName() + "]已触发，当前值为" + triggerValue.stripTrailingZeros().toPlainString()
            + "，阈值为" + rule.getThresholdValue().stripTrailingZeros().toPlainString();
    }
}
