package com.moco.system.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.moco.common.core.domain.BaseEntity;

public class FinQuoteSnapshot extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long quoteId;
    private Long assetId;
    private String assetCode;
    private String assetName;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date snapshotDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date quoteTime;
    private String providerCode;
    private BigDecimal lastPrice;
    private BigDecimal openPrice;
    private BigDecimal prevClosePrice;
    private BigDecimal changeAmount;
    private BigDecimal changeRate;
    private String rawPayload;

    public Long getQuoteId()
    {
        return quoteId;
    }

    public void setQuoteId(Long quoteId)
    {
        this.quoteId = quoteId;
    }

    public Long getAssetId()
    {
        return assetId;
    }

    public void setAssetId(Long assetId)
    {
        this.assetId = assetId;
    }

    public String getAssetCode()
    {
        return assetCode;
    }

    public void setAssetCode(String assetCode)
    {
        this.assetCode = assetCode;
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

    public String getProviderCode()
    {
        return providerCode;
    }

    public void setProviderCode(String providerCode)
    {
        this.providerCode = providerCode;
    }

    public BigDecimal getLastPrice()
    {
        return lastPrice;
    }

    public void setLastPrice(BigDecimal lastPrice)
    {
        this.lastPrice = lastPrice;
    }

    public BigDecimal getOpenPrice()
    {
        return openPrice;
    }

    public void setOpenPrice(BigDecimal openPrice)
    {
        this.openPrice = openPrice;
    }

    public BigDecimal getPrevClosePrice()
    {
        return prevClosePrice;
    }

    public void setPrevClosePrice(BigDecimal prevClosePrice)
    {
        this.prevClosePrice = prevClosePrice;
    }

    public BigDecimal getChangeAmount()
    {
        return changeAmount;
    }

    public void setChangeAmount(BigDecimal changeAmount)
    {
        this.changeAmount = changeAmount;
    }

    public BigDecimal getChangeRate()
    {
        return changeRate;
    }

    public void setChangeRate(BigDecimal changeRate)
    {
        this.changeRate = changeRate;
    }

    public String getRawPayload()
    {
        return rawPayload;
    }

    public void setRawPayload(String rawPayload)
    {
        this.rawPayload = rawPayload;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("quoteId", getQuoteId())
            .append("assetId", getAssetId())
            .append("quoteTime", getQuoteTime())
            .append("providerCode", getProviderCode())
            .append("lastPrice", getLastPrice())
            .append("changeRate", getChangeRate())
            .toString();
    }
}
