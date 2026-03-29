package com.moco.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.moco.common.annotation.Excel;
import com.moco.common.annotation.Excel.ColumnType;
import com.moco.common.core.domain.BaseEntity;

public class ShDevice extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    @Excel(name = "设备ID", cellType = ColumnType.NUMERIC)
    private Long deviceId;

    private Long homeId;

    private Long roomId;

    @Excel(name = "家庭名称")
    private String homeName;

    @Excel(name = "房间名称")
    private String roomName;

    @Excel(name = "设备DID")
    private String did;

    private String uid;

    @Excel(name = "设备名称")
    private String deviceName;

    @Excel(name = "设备型号")
    private String model;

    @Excel(name = "设备类型")
    private String deviceType;

    @Excel(name = "在线状态", readConverterExp = "1=在线,0=离线")
    private String onlineStatus;

    @Excel(name = "开关状态")
    private String powerStatus;

    @Excel(name = "地区")
    private String region;

    private String rawPayload;

    private java.util.Date lastSyncTime;

    public Long getDeviceId()
    {
        return deviceId;
    }

    public void setDeviceId(Long deviceId)
    {
        this.deviceId = deviceId;
    }

    public Long getHomeId()
    {
        return homeId;
    }

    public void setHomeId(Long homeId)
    {
        this.homeId = homeId;
    }

    public Long getRoomId()
    {
        return roomId;
    }

    public void setRoomId(Long roomId)
    {
        this.roomId = roomId;
    }

    public String getHomeName()
    {
        return homeName;
    }

    public void setHomeName(String homeName)
    {
        this.homeName = homeName;
    }

    public String getRoomName()
    {
        return roomName;
    }

    public void setRoomName(String roomName)
    {
        this.roomName = roomName;
    }

    public String getDid()
    {
        return did;
    }

    public void setDid(String did)
    {
        this.did = did;
    }

    public String getUid()
    {
        return uid;
    }

    public void setUid(String uid)
    {
        this.uid = uid;
    }

    public String getDeviceName()
    {
        return deviceName;
    }

    public void setDeviceName(String deviceName)
    {
        this.deviceName = deviceName;
    }

    public String getModel()
    {
        return model;
    }

    public void setModel(String model)
    {
        this.model = model;
    }

    public String getDeviceType()
    {
        return deviceType;
    }

    public void setDeviceType(String deviceType)
    {
        this.deviceType = deviceType;
    }

    public String getOnlineStatus()
    {
        return onlineStatus;
    }

    public void setOnlineStatus(String onlineStatus)
    {
        this.onlineStatus = onlineStatus;
    }

    public String getPowerStatus()
    {
        return powerStatus;
    }

    public void setPowerStatus(String powerStatus)
    {
        this.powerStatus = powerStatus;
    }

    public String getRegion()
    {
        return region;
    }

    public void setRegion(String region)
    {
        this.region = region;
    }

    public String getRawPayload()
    {
        return rawPayload;
    }

    public void setRawPayload(String rawPayload)
    {
        this.rawPayload = rawPayload;
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
            .append("deviceId", getDeviceId())
            .append("homeId", getHomeId())
            .append("roomId", getRoomId())
            .append("did", getDid())
            .append("uid", getUid())
            .append("deviceName", getDeviceName())
            .append("model", getModel())
            .append("deviceType", getDeviceType())
            .append("onlineStatus", getOnlineStatus())
            .append("powerStatus", getPowerStatus())
            .append("region", getRegion())
            .append("lastSyncTime", getLastSyncTime())
            .toString();
    }
}
