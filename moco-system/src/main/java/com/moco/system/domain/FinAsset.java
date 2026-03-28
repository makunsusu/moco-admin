package com.moco.system.domain;

import java.math.BigDecimal;
import java.util.Date;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.moco.common.annotation.Excel;
import com.moco.common.annotation.Excel.ColumnType;
import com.moco.common.core.domain.BaseEntity;

public class FinAsset extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    @Excel(name = "资产ID", cellType = ColumnType.NUMERIC)
    private Long assetId;

    @Excel(name = "市场ID", cellType = ColumnType.NUMERIC)
    private Long marketId;

    @Excel(name = "市场编码")
    private String marketCode;

    @Excel(name = "市场名称")
    private String marketName;

    @Excel(name = "资产编码")
    private String assetCode;

    @Excel(name = "资产名称")
    private String assetName;

    @Excel(name = "资产类型")
    private String assetType;

    @Excel(name = "行情代码")
    private String quoteCode;

    @Excel(name = "启用行情", readConverterExp = "1=是,0=否")
    private String quoteEnabled;

    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    private String status;

    private String delFlag;

    private BigDecimal lastPrice;

    private BigDecimal changeRate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date quoteTime;

    private BigDecimal holdingQty;

    private BigDecimal costAmount;

    private BigDecimal marketValue;

    private BigDecimal profitAmount;

    private BigDecimal profitRate;

    private Long familyId;

    private Long memberId;

    private Long accountId;

    public Long getAssetId()
    {
        return assetId;
    }

    public void setAssetId(Long assetId)
    {
        this.assetId = assetId;
    }

    @NotNull(message = "所属市场不能为空")
    public Long getMarketId()
    {
        return marketId;
    }

    public void setMarketId(Long marketId)
    {
        this.marketId = marketId;
    }

    public String getMarketCode()
    {
        return marketCode;
    }

    public void setMarketCode(String marketCode)
    {
        this.marketCode = marketCode;
    }

    public String getMarketName()
    {
        return marketName;
    }

    public void setMarketName(String marketName)
    {
        this.marketName = marketName;
    }

    @NotBlank(message = "资产编码不能为空")
    @Size(max = 32, message = "资产编码长度不能超过32个字符")
    public String getAssetCode()
    {
        return assetCode;
    }

    public void setAssetCode(String assetCode)
    {
        this.assetCode = assetCode;
    }

    @NotBlank(message = "资产名称不能为空")
    @Size(max = 100, message = "资产名称长度不能超过100个字符")
    public String getAssetName()
    {
        return assetName;
    }

    public void setAssetName(String assetName)
    {
        this.assetName = assetName;
    }

    @NotBlank(message = "资产类型不能为空")
    @Size(max = 20, message = "资产类型长度不能超过20个字符")
    public String getAssetType()
    {
        return assetType;
    }

    public void setAssetType(String assetType)
    {
        this.assetType = assetType;
    }

    @Size(max = 32, message = "行情代码长度不能超过32个字符")
    public String getQuoteCode()
    {
        return quoteCode;
    }

    public void setQuoteCode(String quoteCode)
    {
        this.quoteCode = quoteCode;
    }

    public String getQuoteEnabled()
    {
        return quoteEnabled;
    }

    public void setQuoteEnabled(String quoteEnabled)
    {
        this.quoteEnabled = quoteEnabled;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getDelFlag()
    {
        return delFlag;
    }

    public void setDelFlag(String delFlag)
    {
        this.delFlag = delFlag;
    }

    public BigDecimal getLastPrice()
    {
        return lastPrice;
    }

    public void setLastPrice(BigDecimal lastPrice)
    {
        this.lastPrice = lastPrice;
    }

    public BigDecimal getChangeRate()
    {
        return changeRate;
    }

    public void setChangeRate(BigDecimal changeRate)
    {
        this.changeRate = changeRate;
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

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("assetId", getAssetId())
            .append("marketId", getMarketId())
            .append("assetCode", getAssetCode())
            .append("assetName", getAssetName())
            .append("assetType", getAssetType())
            .append("quoteCode", getQuoteCode())
            .append("quoteEnabled", getQuoteEnabled())
            .append("status", getStatus())
            .append("delFlag", getDelFlag())
            .append("remark", getRemark())
            .toString();
    }
}
