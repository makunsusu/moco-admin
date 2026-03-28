package com.moco.system.domain.vo;

import java.math.BigDecimal;

public class FinOverviewAssetVO
{
    private Long assetId;
    private String assetName;
    private String assetCode;
    private String assetType;
    private BigDecimal holdingQty;
    private BigDecimal lastPrice;
    private BigDecimal marketValue;
    private BigDecimal profitAmount;
    private BigDecimal profitRate;

    public Long getAssetId()
    {
        return assetId;
    }

    public void setAssetId(Long assetId)
    {
        this.assetId = assetId;
    }

    public String getAssetName()
    {
        return assetName;
    }

    public void setAssetName(String assetName)
    {
        this.assetName = assetName;
    }

    public String getAssetCode()
    {
        return assetCode;
    }

    public void setAssetCode(String assetCode)
    {
        this.assetCode = assetCode;
    }

    public String getAssetType()
    {
        return assetType;
    }

    public void setAssetType(String assetType)
    {
        this.assetType = assetType;
    }

    public BigDecimal getHoldingQty()
    {
        return holdingQty;
    }

    public void setHoldingQty(BigDecimal holdingQty)
    {
        this.holdingQty = holdingQty;
    }

    public BigDecimal getLastPrice()
    {
        return lastPrice;
    }

    public void setLastPrice(BigDecimal lastPrice)
    {
        this.lastPrice = lastPrice;
    }

    public BigDecimal getMarketValue()
    {
        return marketValue;
    }

    public void setMarketValue(BigDecimal marketValue)
    {
        this.marketValue = marketValue;
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
}
