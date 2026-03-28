package com.moco.system.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.moco.system.domain.FinAsset;
import com.moco.system.domain.FinHoldingSnapshot;
import com.moco.system.domain.FinQuoteSnapshot;
import com.moco.system.domain.vo.FinPositionSummary;
import com.moco.system.mapper.FinHoldingSnapshotMapper;
import com.moco.system.mapper.FinQuoteSnapshotMapper;
import com.moco.system.service.IFinAssetService;
import com.moco.system.service.IFinQuoteService;
import com.moco.system.service.quote.QuoteProvider;
import com.moco.system.service.quote.QuoteProviderFactory;

@Service
public class FinQuoteServiceImpl implements IFinQuoteService
{
    @Autowired
    private IFinAssetService assetService;

    @Autowired
    private FinQuoteSnapshotMapper quoteSnapshotMapper;

    @Autowired
    private FinHoldingSnapshotMapper holdingSnapshotMapper;

    @Autowired
    private QuoteProviderFactory quoteProviderFactory;

    @Autowired
    private FinPositionSupportService positionSupportService;

    @Autowired
    private FinAlertEvaluationService alertEvaluationService;

    @Override
    public Map<String, Object> refreshQuotes(Long[] assetIds)
    {
        List<FinAsset> assets = assetIds == null || assetIds.length == 0
            ? assetService.selectQuoteEnabledAssets()
            : filterAssets(assetIds);
        int successCount = 0;
        int failCount = 0;
        List<String> errors = new ArrayList<>();

        for (FinAsset asset : assets)
        {
            try
            {
                QuoteProvider provider = quoteProviderFactory.getProvider(asset);
                if (provider == null)
                {
                    throw new IllegalStateException("未找到可用行情源");
                }
                FinQuoteSnapshot snapshot = provider.query(asset);
                quoteSnapshotMapper.insertQuoteSnapshot(snapshot);
                successCount++;
            }
            catch (Exception e)
            {
                failCount++;
                errors.add(asset.getAssetName() + "：" + e.getMessage());
            }
        }

        rebuildHoldingSnapshots();
        alertEvaluationService.evaluateAll();

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("successCount", successCount);
        result.put("failCount", failCount);
        result.put("errors", errors);
        return result;
    }

    @Override
    public List<FinQuoteSnapshot> selectLatestQuotes()
    {
        return quoteSnapshotMapper.selectLatestQuotes();
    }

    private List<FinAsset> filterAssets(Long[] assetIds)
    {
        List<FinAsset> result = new ArrayList<>();
        Map<Long, Boolean> required = new HashMap<>();
        for (Long assetId : assetIds)
        {
            required.put(assetId, Boolean.TRUE);
        }
        for (FinAsset asset : assetService.selectQuoteEnabledAssets())
        {
            if (required.containsKey(asset.getAssetId()))
            {
                result.add(asset);
            }
        }
        return result;
    }

    private void rebuildHoldingSnapshots()
    {
        Map<Long, FinQuoteSnapshot> quoteMap = new HashMap<>();
        for (FinQuoteSnapshot quote : quoteSnapshotMapper.selectLatestQuotes())
        {
            quoteMap.put(quote.getAssetId(), quote);
        }
        List<FinPositionSummary> summaries = positionSupportService.listPositionSummaries(null, null, null, null);
        Map<Long, BigDecimal> familyMarketValueMap = new HashMap<>();
        Map<Long, BigDecimal> memberMarketValueMap = new HashMap<>();
        for (FinPositionSummary summary : summaries)
        {
            FinQuoteSnapshot quote = quoteMap.get(summary.getAssetId());
            if (quote == null)
            {
                continue;
            }
            BigDecimal marketValue = quote.getLastPrice().multiply(summary.getHoldingQty()).setScale(2, RoundingMode.HALF_UP);
            familyMarketValueMap.merge(summary.getFamilyId(), marketValue, BigDecimal::add);
            memberMarketValueMap.merge(summary.getMemberId(), marketValue, BigDecimal::add);
        }
        for (FinPositionSummary summary : summaries)
        {
            FinQuoteSnapshot quote = quoteMap.get(summary.getAssetId());
            if (quote == null)
            {
                continue;
            }
            BigDecimal marketValue = quote.getLastPrice().multiply(summary.getHoldingQty()).setScale(2, RoundingMode.HALF_UP);
            BigDecimal floatingProfit = marketValue.subtract(summary.getCostAmount()).setScale(2, RoundingMode.HALF_UP);
            FinHoldingSnapshot snapshot = new FinHoldingSnapshot();
            snapshot.setFamilyId(summary.getFamilyId());
            snapshot.setMemberId(summary.getMemberId());
            snapshot.setAccountId(summary.getAccountId());
            snapshot.setAssetId(summary.getAssetId());
            snapshot.setSnapshotDate(new Date());
            snapshot.setQuoteTime(quote.getQuoteTime() == null ? new Date() : quote.getQuoteTime());
            snapshot.setHoldingQty(summary.getHoldingQty());
            snapshot.setCostAmount(summary.getCostAmount());
            snapshot.setMarketValue(marketValue);
            snapshot.setFloatingProfitAmount(floatingProfit);
            snapshot.setRealizedProfitAmount(summary.getRealizedProfitAmount());
            snapshot.setProfitAmount(floatingProfit.add(summary.getRealizedProfitAmount()).setScale(2, RoundingMode.HALF_UP));
            snapshot.setProfitRate(summary.getCostAmount().compareTo(BigDecimal.ZERO) > 0
                ? snapshot.getProfitAmount().divide(summary.getCostAmount(), 4, RoundingMode.HALF_UP)
                : BigDecimal.ZERO);
            BigDecimal totalMarketValue = memberMarketValueMap.getOrDefault(summary.getMemberId(), BigDecimal.ZERO);
            if (totalMarketValue.compareTo(BigDecimal.ZERO) <= 0)
            {
                totalMarketValue = familyMarketValueMap.getOrDefault(summary.getFamilyId(), BigDecimal.ZERO);
            }
            snapshot.setPositionRatio(totalMarketValue.compareTo(BigDecimal.ZERO) > 0
                ? marketValue.divide(totalMarketValue, 4, RoundingMode.HALF_UP)
                : BigDecimal.ZERO);
            holdingSnapshotMapper.insertHoldingSnapshot(snapshot);
        }
    }
}
