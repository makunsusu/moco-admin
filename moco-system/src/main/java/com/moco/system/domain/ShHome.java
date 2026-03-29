package com.moco.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.moco.common.annotation.Excel;
import com.moco.common.annotation.Excel.ColumnType;
import com.moco.common.core.domain.BaseEntity;

public class ShHome extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    @Excel(name = "家庭ID", cellType = ColumnType.NUMERIC)
    private Long homeId;

    private String platformCode;

    @Excel(name = "云端家庭ID")
    private String cloudHomeId;

    @Excel(name = "家庭名称")
    private String homeName;

    @Excel(name = "地区")
    private String region;

    @Excel(name = "房间数")
    private Integer roomCount;

    @Excel(name = "设备数")
    private Integer deviceCount;

    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    private String status;

    private java.util.Date lastSyncTime;

    public Long getHomeId()
    {
        return homeId;
    }

    public void setHomeId(Long homeId)
    {
        this.homeId = homeId;
    }

    public String getPlatformCode()
    {
        return platformCode;
    }

    public void setPlatformCode(String platformCode)
    {
        this.platformCode = platformCode;
    }

    public String getCloudHomeId()
    {
        return cloudHomeId;
    }

    public void setCloudHomeId(String cloudHomeId)
    {
        this.cloudHomeId = cloudHomeId;
    }

    public String getHomeName()
    {
        return homeName;
    }

    public void setHomeName(String homeName)
    {
        this.homeName = homeName;
    }

    public String getRegion()
    {
        return region;
    }

    public void setRegion(String region)
    {
        this.region = region;
    }

    public Integer getRoomCount()
    {
        return roomCount;
    }

    public void setRoomCount(Integer roomCount)
    {
        this.roomCount = roomCount;
    }

    public Integer getDeviceCount()
    {
        return deviceCount;
    }

    public void setDeviceCount(Integer deviceCount)
    {
        this.deviceCount = deviceCount;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public java.util.Date getLastSyncTime()
    {
        return lastSyncTime;
    }

    public void setLastSyncTime(java.util.Date lastSyncTime)
    {
        this.lastSyncTime = lastSyncTime;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("homeId", getHomeId())
            .append("platformCode", getPlatformCode())
            .append("cloudHomeId", getCloudHomeId())
            .append("homeName", getHomeName())
            .append("region", getRegion())
            .append("roomCount", getRoomCount())
            .append("deviceCount", getDeviceCount())
            .append("status", getStatus())
            .append("lastSyncTime", getLastSyncTime())
            .toString();
    }
}
