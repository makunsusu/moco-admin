package com.moco.system.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.moco.system.domain.FinHoldingAccount;
import com.moco.system.domain.FinQuoteSnapshot;
import com.moco.system.domain.vo.FinOverviewAssetVO;
import com.moco.system.domain.vo.FinOverviewMemberVO;
import com.moco.system.domain.vo.FinOverviewVO;
import com.moco.system.domain.vo.FinPositionSummary;
import com.moco.system.mapper.FinHoldingAccountMapper;
import com.moco.system.mapper.FinQuoteSnapshotMapper;
import com.moco.system.service.IFinOverviewService;

@Service
public class FinOverviewServiceImpl implements IFinOverviewService
{
    @Autowired
    private FinHoldingAccountMapper accountMapper;

    @Autowired
    private FinQuoteSnapshotMapper quoteSnapshotMapper;

    @Autowired
    private FinPositionSupportService positionSupportService;

    @Override
    public FinOverviewVO getOverview(Long familyId, Long memberId, Long accountId)
    {
        FinOverviewVO overview = new FinOverviewVO();
        overview.setTotalAssets(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
        overview.setCashBalance(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
        overview.setMarketValue(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
        overview.setInvestedAmount(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
        overview.setTotalCostAmount(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
        overview.setFloatingProfitAmount(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
        overview.setRealizedProfitAmount(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
        overview.setTotalProfitAmount(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
        overview.setDailyProfitAmount(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));

        Map<Long, FinQuoteSnapshot> quoteMap = new HashMap<>();
        for (FinQuoteSnapshot snapshot : quoteSnapshotMapper.selectLatestQuotes())
        {
            quoteMap.put(snapshot.getAssetId(), snapshot);
        }

        Map<Long, FinOverviewMemberVO> memberMap = new HashMap<>();
        FinHoldingAccount query = new FinHoldingAccount();
        query.setFamilyId(familyId);
        query.setMemberId(memberId);
        query.setAccountId(accountId);
        for (FinHoldingAccount account : accountMapper.selectHoldingAccountList(query))
        {
            BigDecimal cash = account.getCashBalance() == null ? BigDecimal.ZERO : account.getCashBalance();
            overview.setCashBalance(overview.getCashBalance().add(cash));
            FinOverviewMemberVO memberItem = memberMap.computeIfAbsent(account.getMemberId(), k -> {
                FinOverviewMemberVO vo = new FinOverviewMemberVO();
                vo.setMemberId(account.getMemberId());
                vo.setMemberName(account.getMemberName());
                vo.setCashBalance(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
                vo.setMarketValue(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
                vo.setTotalAssets(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
                vo.setProfitAmount(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
                return vo;
            });
            memberItem.setCashBalance(memberItem.getCashBalance().add(cash));
            memberItem.setTotalAssets(memberItem.getTotalAssets().add(cash));
        }

        Map<Long, FinOverviewAssetVO> assetMap = new HashMap<>();
        List<FinPositionSummary> positions = positionSupportService.listPositionSummaries(familyId, memberId, accountId, null);
        for (FinPositionSummary position : positions)
        {
            FinQuoteSnapshot quote = quoteMap.get(position.getAssetId());
            if (quote == null)
            {
                continue;
            }
            BigDecimal marketValue = quote.getLastPrice().multiply(position.getHoldingQty()).setScale(2, RoundingMode.HALF_UP);
            BigDecimal floatingProfit = marketValue.subtract(position.getCostAmount()).setScale(2, RoundingMode.HALF_UP);
            BigDecimal totalProfit = floatingProfit.add(position.getRealizedProfitAmount()).setScale(2, RoundingMode.HALF_UP);
            BigDecimal dailyProfit = quote.getChangeAmount() == null ? BigDecimal.ZERO
                : quote.getChangeAmount().multiply(position.getHoldingQty()).setScale(2, RoundingMode.HALF_UP);

            overview.setMarketValue(overview.getMarketValue().add(marketValue));
            overview.setInvestedAmount(overview.getInvestedAmount().add(position.getInvestedAmount()));
            overview.setTotalCostAmount(overview.getTotalCostAmount().add(position.getCostAmount()));
            overview.setFloatingProfitAmount(overview.getFloatingProfitAmount().add(floatingProfit));
            overview.setRealizedProfitAmount(overview.getRealizedProfitAmount().add(position.getRealizedProfitAmount()));
            overview.setTotalProfitAmount(overview.getTotalProfitAmount().add(totalProfit));
            overview.setDailyProfitAmount(overview.getDailyProfitAmount().add(dailyProfit));

            FinOverviewMemberVO memberItem = memberMap.get(position.getMemberId());
            if (memberItem != null)
            {
                memberItem.setMarketValue(memberItem.getMarketValue().add(marketValue));
                memberItem.setTotalAssets(memberItem.getTotalAssets().add(marketValue));
                memberItem.setProfitAmount(memberItem.getProfitAmount().add(totalProfit));
            }

            FinOverviewAssetVO assetItem = assetMap.computeIfAbsent(position.getAssetId(), k -> {
                FinOverviewAssetVO vo = new FinOverviewAssetVO();
                vo.setAssetId(position.getAssetId());
                vo.setHoldingQty(BigDecimal.ZERO.setScale(4, RoundingMode.HALF_UP));
                vo.setMarketValue(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
                vo.setProfitAmount(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
                vo.setProfitRate(BigDecimal.ZERO.setScale(4, RoundingMode.HALF_UP));
                return vo;
            });
            assetItem.setHoldingQty(assetItem.getHoldingQty().add(position.getHoldingQty()));
            assetItem.setLastPrice(quote.getLastPrice());
            assetItem.setMarketValue(assetItem.getMarketValue().add(marketValue));
            assetItem.setProfitAmount(assetItem.getProfitAmount().add(totalProfit));
        }

        for (FinQuoteSnapshot snapshot : quoteMap.values())
        {
            FinOverviewAssetVO item = assetMap.get(snapshot.getAssetId());
            if (item != null)
            {
                item.setAssetCode(snapshot.getAssetCode());
                item.setAssetName(snapshot.getAssetName());
            }
        }
        for (FinOverviewAssetVO item : assetMap.values())
        {
            BigDecimal cost = BigDecimal.ZERO;
            for (FinPositionSummary position : positions)
            {
                if (item.getAssetId().equals(position.getAssetId()))
                {
                    cost = cost.add(position.getCostAmount());
                }
            }
            if (cost.compareTo(BigDecimal.ZERO) > 0)
            {
                item.setProfitRate(item.getProfitAmount().divide(cost, 4, RoundingMode.HALF_UP));
            }
        }

        overview.setTotalAssets(overview.getCashBalance().add(overview.getMarketValue()));
        overview.getMemberList().addAll(memberMap.values());
        overview.getAssetList().addAll(assetMap.values());
        return overview;
    }
}
