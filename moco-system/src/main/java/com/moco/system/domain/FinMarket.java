package com.moco.system.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.moco.common.annotation.Excel;
import com.moco.common.annotation.Excel.ColumnType;
import com.moco.common.core.domain.BaseEntity;

public class FinMarket extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    @Excel(name = "市场ID", cellType = ColumnType.NUMERIC)
    private Long marketId;

    @Excel(name = "市场编码")
    private String marketCode;

    @Excel(name = "市场名称")
    private String marketName;

    @Excel(name = "市场简称")
    private String marketShortName;

    @Excel(name = "市场类型")
    private String marketType;

    @Excel(name = "所属地区")
    private String marketRegion;

    @Excel(name = "官网地址")
    private String website;

    @Excel(name = "排序")
    private Integer marketSort;

    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    private String status;

    public Long getMarketId()
    {
        return marketId;
    }

    public void setMarketId(Long marketId)
    {
        this.marketId = marketId;
    }

    @NotBlank(message = "市场编码不能为空")
    @Size(max = 32, message = "市场编码长度不能超过32个字符")
    public String getMarketCode()
    {
        return marketCode;
    }

    public void setMarketCode(String marketCode)
    {
        this.marketCode = marketCode;
    }

    @NotBlank(message = "市场名称不能为空")
    @Size(max = 100, message = "市场名称长度不能超过100个字符")
    public String getMarketName()
    {
        return marketName;
    }

    public void setMarketName(String marketName)
    {
        this.marketName = marketName;
    }

    @Size(max = 50, message = "市场简称长度不能超过50个字符")
    public String getMarketShortName()
    {
        return marketShortName;
    }

    public void setMarketShortName(String marketShortName)
    {
        this.marketShortName = marketShortName;
    }

    @NotBlank(message = "市场类型不能为空")
    @Size(max = 50, message = "市场类型长度不能超过50个字符")
    public String getMarketType()
    {
        return marketType;
    }

    public void setMarketType(String marketType)
    {
        this.marketType = marketType;
    }

    @Size(max = 50, message = "所属地区长度不能超过50个字符")
    public String getMarketRegion()
    {
        return marketRegion;
    }

    public void setMarketRegion(String marketRegion)
    {
        this.marketRegion = marketRegion;
    }

    @Size(max = 255, message = "官网地址长度不能超过255个字符")
    public String getWebsite()
    {
        return website;
    }

    public void setWebsite(String website)
    {
        this.website = website;
    }

    @NotNull(message = "显示顺序不能为空")
    public Integer getMarketSort()
    {
        return marketSort;
    }

    public void setMarketSort(Integer marketSort)
    {
        this.marketSort = marketSort;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("marketId", getMarketId())
            .append("marketCode", getMarketCode())
            .append("marketName", getMarketName())
            .append("marketShortName", getMarketShortName())
            .append("marketType", getMarketType())
            .append("marketRegion", getMarketRegion())
            .append("website", getWebsite())
            .append("marketSort", getMarketSort())
            .append("status", getStatus())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
