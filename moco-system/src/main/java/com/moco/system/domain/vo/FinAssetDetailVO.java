package com.moco.system.domain.vo;

import java.math.BigDecimal;
import java.util.List;
import com.moco.system.domain.FinAsset;
import com.moco.system.domain.FinKlineSnapshot;
import com.moco.system.domain.FinQuoteSnapshot;
import com.moco.system.domain.FinTransaction;

public class FinAssetDetailVO
{
    private FinAsset asset;
    private FinQuoteSnapshot latestQuote;
    private BigDecimal holdingQty;
    private BigDecimal costAmount;
    private BigDecimal marketValue;
    private BigDecimal floatingProfitAmount;
    private BigDecimal realizedProfitAmount;
    private BigDecimal profitAmount;
    private BigDecimal profitRate;
    private Boolean klineSupported;
    private String klineMessage;
    private List<FinKlineSnapshot> klineList;
    private List<FinTransaction> recentTransactions;

    public FinAsset getAsset()
    {
        return asset;
    }

    public void setAsset(FinAsset asset)
    {
        this.asset = asset;
    }

    public FinQuoteSnapshot getLatestQuote()
    {
        return latestQuote;
    }

    public void setLatestQuote(FinQuoteSnapshot latestQuote)
    {
        this.latestQuote = latestQuote;
    }

    public BigDecimal getHoldingQty()
    {
        return holdingQty;
    }

    public void setHoldingQty(BigDecimal holdingQty)
    {
        this.holdingQty = holdingQty;
    }

    public BigDecimal getCostAmount()
    {
        return costAmount;
    }

    public void setCostAmount(BigDecimal costAmount)
    {
        this.costAmount = costAmount;
    }

    public BigDecimal getMarketValue()
    {
        return marketValue;
    }

    public void setMarketValue(BigDecimal marketValue)
    {
        this.marketValue = marketValue;
    }

    public BigDecimal getFloatingProfitAmount()
    {
        return floatingProfitAmount;
    }

    public void setFloatingProfitAmount(BigDecimal floatingProfitAmount)
    {
        this.floatingProfitAmount = floatingProfitAmount;
    }

    public BigDecimal getRealizedProfitAmount()
    {
        return realizedProfitAmount;
    }

    public void setRealizedProfitAmount(BigDecimal realizedProfitAmount)
    {
        this.realizedProfitAmount = realizedProfitAmount;
    }

    public BigDecimal getProfitAmount()
    {
        return profitAmount;
    }

    public void setProfitAmount(BigDecimal profitAmount)
    {
        this.profitAmount = profitAmount;
    }

    public BigDecimal getProfitRate()
    {
        return profitRate;
    }

    public void setProfitRate(BigDecimal profitRate)
    {
        this.profitRate = profitRate;
    }

    public Boolean getKlineSupported()
    {
        return klineSupported;
    }

    public void setKlineSupported(Boolean klineSupported)
    {
        this.klineSupported = klineSupported;
    }

    public String getKlineMessage()
    {
        return klineMessage;
    }

    public void setKlineMessage(String klineMessage)
    {
        this.klineMessage = klineMessage;
    }

    public List<FinKlineSnapshot> getKlineList()
    {
        return klineList;
    }

    public void setKlineList(List<FinKlineSnapshot> klineList)
    {
        this.klineList = klineList;
    }

    public List<FinTransaction> getRecentTransactions()
    {
        return recentTransactions;
    }

    public void setRecentTransactions(List<FinTransaction> recentTransactions)
    {
        this.recentTransactions = recentTransactions;
    }
}
