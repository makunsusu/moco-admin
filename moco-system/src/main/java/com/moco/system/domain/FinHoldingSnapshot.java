package com.moco.system.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.moco.common.core.domain.BaseEntity;

public class FinHoldingSnapshot extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long snapshotId;
    private Long familyId;
    private String familyName;
    private Long memberId;
    private String memberName;
    private Long accountId;
    private String accountName;
    private Long assetId;
    private String assetName;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date snapshotDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date quoteTime;
    private BigDecimal holdingQty;
    private BigDecimal costAmount;
    private BigDecimal marketValue;
    private BigDecimal floatingProfitAmount;
    private BigDecimal realizedProfitAmount;
    private BigDecimal profitAmount;
    private BigDecimal profitRate;
    private BigDecimal positionRatio;

    public Long getSnapshotId()
    {
        return snapshotId;
    }

    public void setSnapshotId(Long snapshotId)
    {
        this.snapshotId = snapshotId;
    }

    public Long getFamilyId()
    {
        return familyId;
    }

    public void setFamilyId(Long familyId)
    {
        this.familyId = familyId;
    }

    public String getFamilyName()
    {
        return familyName;
    }

    public void setFamilyName(String familyName)
    {
        this.familyName = familyName;
    }

    public Long getMemberId()
    {
        return memberId;
    }

    public void setMemberId(Long memberId)
    {
        this.memberId = memberId;
    }

    public String getMemberName()
    {
        return memberName;
    }

    public void setMemberName(String memberName)
    {
        this.memberName = memberName;
    }

    public Long getAccountId()
    {
        return accountId;
    }

    public void setAccountId(Long accountId)
    {
        this.accountId = accountId;
    }

    public String getAccountName()
    {
        return accountName;
    }

    public void setAccountName(String accountName)
    {
        this.accountName = accountName;
    }

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

    public Date getSnapshotDate()
    {
        return snapshotDate;
    }

    public void setSnapshotDate(Date snapshotDate)
    {
        this.snapshotDate = snapshotDate;
    }

    public Date getQuoteTime()
    {
        return quoteTime;
    }

    public void setQuoteTime(Date quoteTime)
    {
        this.quoteTime = quoteTime;
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

    public BigDecimal getPositionRatio()
    {
        return positionRatio;
    }

    public void setPositionRatio(BigDecimal positionRatio)
    {
        this.positionRatio = positionRatio;
    }
}
