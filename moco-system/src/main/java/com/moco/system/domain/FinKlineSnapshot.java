package com.moco.system.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.moco.common.core.domain.BaseEntity;

public class FinKlineSnapshot extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long klineId;
    private Long assetId;
    private String assetCode;
    private String assetName;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date tradeDate;
    private BigDecimal openPrice;
    private BigDecimal closePrice;
    private BigDecimal highPrice;
    private BigDecimal lowPrice;
    private BigDecimal volume;
    private BigDecimal turnoverAmount;
    private BigDecimal amplitude;
    private BigDecimal changeRate;
    private BigDecimal changeAmount;
    private BigDecimal turnoverRate;
    private String providerCode;
    private String rawPayload;

    public Long getKlineId()
    {
        return klineId;
    }

    public void setKlineId(Long klineId)
    {
        this.klineId = klineId;
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

    public Date getTradeDate()
    {
        return tradeDate;
    }

    public void setTradeDate(Date tradeDate)
    {
        this.tradeDate = tradeDate;
    }

    public BigDecimal getOpenPrice()
    {
        return openPrice;
    }

    public void setOpenPrice(BigDecimal openPrice)
    {
        this.openPrice = openPrice;
    }

    public BigDecimal getClosePrice()
    {
        return closePrice;
    }

    public void setClosePrice(BigDecimal closePrice)
    {
        this.closePrice = closePrice;
    }

    public BigDecimal getHighPrice()
    {
        return highPrice;
    }

    public void setHighPrice(BigDecimal highPrice)
    {
        this.highPrice = highPrice;
    }

    public BigDecimal getLowPrice()
    {
        return lowPrice;
    }

    public void setLowPrice(BigDecimal lowPrice)
    {
        this.lowPrice = lowPrice;
    }

    public BigDecimal getVolume()
    {
        return volume;
    }

    public void setVolume(BigDecimal volume)
    {
        this.volume = volume;
    }

    public BigDecimal getTurnoverAmount()
    {
        return turnoverAmount;
    }

    public void setTurnoverAmount(BigDecimal turnoverAmount)
    {
        this.turnoverAmount = turnoverAmount;
    }

    public BigDecimal getAmplitude()
    {
        return amplitude;
    }

    public void setAmplitude(BigDecimal amplitude)
    {
        this.amplitude = amplitude;
    }

    public BigDecimal getChangeRate()
    {
        return changeRate;
    }

    public void setChangeRate(BigDecimal changeRate)
    {
        this.changeRate = changeRate;
    }

    public BigDecimal getChangeAmount()
    {
        return changeAmount;
    }

    public void setChangeAmount(BigDecimal changeAmount)
    {
        this.changeAmount = changeAmount;
    }

    public BigDecimal getTurnoverRate()
    {
        return turnoverRate;
    }

    public void setTurnoverRate(BigDecimal turnoverRate)
    {
        this.turnoverRate = turnoverRate;
    }

    public String getProviderCode()
    {
        return providerCode;
    }

    public void setProviderCode(String providerCode)
    {
        this.providerCode = providerCode;
    }

    public String getRawPayload()
    {
        return rawPayload;
    }

    public void setRawPayload(String rawPayload)
    {
        this.rawPayload = rawPayload;
    }
}
