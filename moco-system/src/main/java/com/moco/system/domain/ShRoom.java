package com.moco.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.moco.common.annotation.Excel;
import com.moco.common.annotation.Excel.ColumnType;
import com.moco.common.core.domain.BaseEntity;

public class ShRoom extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    @Excel(name = "房间ID", cellType = ColumnType.NUMERIC)
    private Long roomId;

    private Long homeId;

    @Excel(name = "家庭名称")
    private String homeName;

    @Excel(name = "云端房间ID")
    private String cloudRoomId;

    @Excel(name = "房间名称")
    private String roomName;

    @Excel(name = "设备数")
    private Integer deviceCount;

    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    private String status;

    private java.util.Date lastSyncTime;

    public Long getRoomId()
    {
        return roomId;
    }

    public void setRoomId(Long roomId)
    {
        this.roomId = roomId;
    }

    public Long getHomeId()
    {
        return homeId;
    }

    public void setHomeId(Long homeId)
    {
        this.homeId = homeId;
    }

    public String getHomeName()
    {
        return homeName;
    }

    public void setHomeName(String homeName)
    {
        this.homeName = homeName;
    }

    public String getCloudRoomId()
    {
        return cloudRoomId;
    }

    public void setCloudRoomId(String cloudRoomId)
    {
        this.cloudRoomId = cloudRoomId;
    }

    public String getRoomName()
    {
        return roomName;
    }

    public void setRoomName(String roomName)
    {
        this.roomName = roomName;
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
            .append("roomId", getRoomId())
            .append("homeId", getHomeId())
            .append("homeName", getHomeName())
            .append("cloudRoomId", getCloudRoomId())
            .append("roomName", getRoomName())
            .append("deviceCount", getDeviceCount())
            .append("status", getStatus())
            .append("lastSyncTime", getLastSyncTime())
            .toString();
    }
}
