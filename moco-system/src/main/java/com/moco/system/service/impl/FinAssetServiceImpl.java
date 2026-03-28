package com.moco.system.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.moco.common.constant.UserConstants;
import com.moco.common.exception.ServiceException;
import com.moco.common.utils.StringUtils;
import com.moco.system.domain.FinAsset;
import com.moco.system.domain.FinKlineSnapshot;
import com.moco.system.domain.FinQuoteSnapshot;
import com.moco.system.domain.FinTransaction;
import com.moco.system.domain.vo.FinAssetDetailVO;
import com.moco.system.domain.vo.FinPositionSummary;
import com.moco.system.mapper.FinAssetMapper;
import com.moco.system.mapper.FinKlineSnapshotMapper;
import com.moco.system.mapper.FinQuoteSnapshotMapper;
import com.moco.system.mapper.FinTransactionMapper;
import com.moco.system.service.IFinAssetService;
import com.moco.system.service.quote.QuoteProvider;
import com.moco.system.service.quote.QuoteProviderFactory;

@Service
public class FinAssetServiceImpl implements IFinAssetService
{
    private static final int DEFAULT_KLINE_LIMIT = 120;

    @Autowired
    private FinAssetMapper assetMapper;

    @Autowired
    private FinQuoteSnapshotMapper quoteSnapshotMapper;

    @Autowired
    private FinKlineSnapshotMapper klineSnapshotMapper;

    @Autowired
    private FinTransactionMapper transactionMapper;

    @Autowired
    private FinPositionSupportService positionSupportService;

    @Autowired
    private QuoteProviderFactory quoteProviderFactory;

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
    public FinAssetDetailVO selectAssetDetail(Long assetId, Integer klineLimit)
    {
        FinAsset asset = assetMapper.selectAssetById(assetId);
        if (asset == null)
        {
            throw new ServiceException("资产不存在");
        }
        FinAssetDetailVO detail = new FinAssetDetailVO();
        detail.setAsset(asset);
        detail.setLatestQuote(quoteSnapshotMapper.selectLatestQuoteByAssetId(assetId));
        fillPositionOverview(detail, assetId);
        fillRecentTransactions(detail, assetId);
        fillKlines(detail, asset, klineLimit);
        return detail;
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

    private void fillPositionOverview(FinAssetDetailVO detail, Long assetId)
    {
        BigDecimal holdingQty = BigDecimal.ZERO.setScale(4, RoundingMode.HALF_UP);
        BigDecimal costAmount = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        BigDecimal realizedProfitAmount = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        for (FinPositionSummary summary : positionSupportService.listPositionSummaries(null, null, null, assetId))
        {
            holdingQty = holdingQty.add(summary.getHoldingQty()).setScale(4, RoundingMode.HALF_UP);
            costAmount = costAmount.add(summary.getCostAmount()).setScale(2, RoundingMode.HALF_UP);
            realizedProfitAmount = realizedProfitAmount.add(summary.getRealizedProfitAmount()).setScale(2, RoundingMode.HALF_UP);
        }
        detail.setHoldingQty(holdingQty);
        detail.setCostAmount(costAmount);
        detail.setRealizedProfitAmount(realizedProfitAmount);
        FinQuoteSnapshot latestQuote = detail.getLatestQuote();
        if (latestQuote == null)
        {
            detail.setMarketValue(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
            detail.setFloatingProfitAmount(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
            detail.setProfitAmount(realizedProfitAmount);
            detail.setProfitRate(BigDecimal.ZERO.setScale(4, RoundingMode.HALF_UP));
            return;
        }
        BigDecimal marketValue = latestQuote.getLastPrice().multiply(holdingQty).setScale(2, RoundingMode.HALF_UP);
        BigDecimal floatingProfitAmount = marketValue.subtract(costAmount).setScale(2, RoundingMode.HALF_UP);
        BigDecimal profitAmount = floatingProfitAmount.add(realizedProfitAmount).setScale(2, RoundingMode.HALF_UP);
        detail.setMarketValue(marketValue);
        detail.setFloatingProfitAmount(floatingProfitAmount);
        detail.setProfitAmount(profitAmount);
        detail.setProfitRate(costAmount.compareTo(BigDecimal.ZERO) > 0
            ? profitAmount.divide(costAmount, 4, RoundingMode.HALF_UP)
            : BigDecimal.ZERO.setScale(4, RoundingMode.HALF_UP));
    }

    private void fillRecentTransactions(FinAssetDetailVO detail, Long assetId)
    {
        FinTransaction query = new FinTransaction();
        query.setAssetId(assetId);
        List<FinTransaction> transactions = transactionMapper.selectTransactionList(query);
        detail.setRecentTransactions(new ArrayList<>(transactions.subList(0, Math.min(transactions.size(), 10))));
    }

    private void fillKlines(FinAssetDetailVO detail, FinAsset asset, Integer klineLimit)
    {
        int limit = klineLimit == null || klineLimit <= 0 ? DEFAULT_KLINE_LIMIT : Math.min(klineLimit, 240);
        QuoteProvider provider = quoteProviderFactory.getProvider(asset);
        boolean supported = provider != null && provider.supportsKline(asset);
        List<FinKlineSnapshot> klineList = klineSnapshotMapper.selectLatestKlinesByAssetId(asset.getAssetId(), limit);
        detail.setKlineSupported(supported);
        detail.setKlineList(klineList);
        if (!supported)
        {
            detail.setKlineMessage("当前资产暂不支持自动同步日K线。");
            return;
        }
        if (klineList.isEmpty())
        {
            detail.setKlineMessage("当前资产还没有本地K线数据，执行“刷新行情”或等待定时任务后可查看。");
            return;
        }
        detail.setKlineMessage("K线数据来自本地数据库，由定时任务同步。");
    }
}
