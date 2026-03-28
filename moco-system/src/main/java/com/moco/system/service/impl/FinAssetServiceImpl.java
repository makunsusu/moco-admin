package com.moco.system.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.moco.common.constant.UserConstants;
import com.moco.common.utils.StringUtils;
import com.moco.system.domain.FinAsset;
import com.moco.system.domain.FinQuoteSnapshot;
import com.moco.system.domain.vo.FinPositionSummary;
import com.moco.system.mapper.FinAssetMapper;
import com.moco.system.mapper.FinQuoteSnapshotMapper;
import com.moco.system.service.IFinAssetService;

@Service
public class FinAssetServiceImpl implements IFinAssetService
{
    @Autowired
    private FinAssetMapper assetMapper;

    @Autowired
    private FinQuoteSnapshotMapper quoteSnapshotMapper;

    @Autowired
    private FinPositionSupportService positionSupportService;

    @Override
    public List<FinAsset> selectAssetList(FinAsset asset)
    {
        List<FinAsset> list = assetMapper.selectAssetList(asset);
        Map<Long, FinQuoteSnapshot> quoteMap = new HashMap<>();
        for (FinQuoteSnapshot snapshot : quoteSnapshotMapper.selectLatestQuotes())
        {
            quoteMap.put(snapshot.getAssetId(), snapshot);
        }
        Map<Long, FinPositionSummary> positionMap = new HashMap<>();
        for (FinPositionSummary summary : positionSupportService.listPositionSummaries(asset.getFamilyId(), asset.getMemberId(), asset.getAccountId(), null))
        {
            FinPositionSummary old = positionMap.get(summary.getAssetId());
            if (old == null)
            {
                positionMap.put(summary.getAssetId(), summary);
            }
            else
            {
                old.setHoldingQty(old.getHoldingQty().add(summary.getHoldingQty()));
                old.setCostAmount(old.getCostAmount().add(summary.getCostAmount()));
                old.setRealizedProfitAmount(old.getRealizedProfitAmount().add(summary.getRealizedProfitAmount()));
                old.setInvestedAmount(old.getInvestedAmount().add(summary.getInvestedAmount()));
                old.setNetCashOutflow(old.getNetCashOutflow().add(summary.getNetCashOutflow()));
            }
        }

        for (FinAsset item : list)
        {
            FinQuoteSnapshot snapshot = quoteMap.get(item.getAssetId());
            if (snapshot != null)
            {
                item.setLastPrice(snapshot.getLastPrice());
                item.setChangeRate(snapshot.getChangeRate());
                item.setQuoteTime(snapshot.getQuoteTime());
            }
            FinPositionSummary summary = positionMap.get(item.getAssetId());
            if (summary != null)
            {
                item.setHoldingQty(summary.getHoldingQty());
                item.setCostAmount(summary.getCostAmount());
                if (snapshot != null)
                {
                    BigDecimal marketValue = snapshot.getLastPrice().multiply(summary.getHoldingQty()).setScale(2, RoundingMode.HALF_UP);
                    BigDecimal profitAmount = marketValue.subtract(summary.getCostAmount()).add(summary.getRealizedProfitAmount()).setScale(2, RoundingMode.HALF_UP);
                    item.setMarketValue(marketValue);
                    item.setProfitAmount(profitAmount);
                    item.setProfitRate(summary.getCostAmount().compareTo(BigDecimal.ZERO) > 0
                        ? profitAmount.divide(summary.getCostAmount(), 4, RoundingMode.HALF_UP)
                        : BigDecimal.ZERO);
                }
            }
        }
        return list;
    }

    @Override
    public List<FinAsset> selectAssetOptions()
    {
        return assetMapper.selectAssetOptions();
    }

    @Override
    public List<FinAsset> selectQuoteEnabledAssets()
    {
        return assetMapper.selectQuoteEnabledAssets();
    }

    @Override
    public FinAsset selectAssetById(Long assetId)
    {
        return assetMapper.selectAssetById(assetId);
    }

    @Override
    public boolean checkAssetUnique(FinAsset asset)
    {
        Long assetId = StringUtils.isNull(asset.getAssetId()) ? -1L : asset.getAssetId();
        FinAsset info = assetMapper.checkAssetUnique(asset);
        if (StringUtils.isNotNull(info) && info.getAssetId().longValue() != assetId.longValue())
        {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    @Override
    public int insertAsset(FinAsset asset)
    {
        return assetMapper.insertAsset(asset);
    }

    @Override
    public int updateAsset(FinAsset asset)
    {
        return assetMapper.updateAsset(asset);
    }

    @Override
    public int deleteAssetByIds(Long[] assetIds)
    {
        return assetMapper.deleteAssetByIds(assetIds);
    }
}
