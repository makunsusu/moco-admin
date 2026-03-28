package com.moco.system.domain.vo;

import java.math.BigDecimal;

public class FinPositionSummary
{
    private Long familyId;
    private Long memberId;
    private Long accountId;
    private Long assetId;
    private BigDecimal holdingQty;
    private BigDecimal costAmount;
    private BigDecimal realizedProfitAmount;
    private BigDecimal investedAmount;
    private BigDecimal netCashOutflow;

    public Long getFamilyId()
    {
        return familyId;
    }

    public void setFamilyId(Long familyId)
    {
        this.familyId = familyId;
    }

    public Long getMemberId()
    {
        return memberId;
    }

    public void setMemberId(Long memberId)
    {
        this.memberId = memberId;
    }

    public Long getAccountId()
    {
        return accountId;
    }

    public void setAccountId(Long accountId)
    {
        this.accountId = accountId;
    }

    public Long getAssetId()
    {
        return assetId;
    }

    public void setAssetId(Long assetId)
    {
        this.assetId = assetId;
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

    public BigDecimal getRealizedProfitAmount()
    {
        return realizedProfitAmount;
    }

    public void setRealizedProfitAmount(BigDecimal realizedProfitAmount)
    {
        this.realizedProfitAmount = realizedProfitAmount;
    }

    public BigDecimal getInvestedAmount()
    {
        return investedAmount;
    }

    public void setInvestedAmount(BigDecimal investedAmount)
    {
        this.investedAmount = investedAmount;
    }

    public BigDecimal getNetCashOutflow()
    {
        return netCashOutflow;
    }

    public void setNetCashOutflow(BigDecimal netCashOutflow)
    {
        this.netCashOutflow = netCashOutflow;
    }
}
