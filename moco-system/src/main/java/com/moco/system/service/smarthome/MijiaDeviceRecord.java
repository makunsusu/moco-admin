package com.moco.system.service.smarthome;

import java.util.LinkedHashMap;
import java.util.Map;

public class MijiaDeviceRecord
{
    private String did;
    private String uid;
    private String homeId;
    private String homeName;
    private String roomId;
    private String roomName;
    private String deviceName;
    private String model;
    private String deviceType;
    private String onlineStatus;
    private String powerStatus;
    private String rawPayload;
    private Map<String, String> properties = new LinkedHashMap<>();

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

    public String getHomeId()
    {
        return homeId;
    }

    public void setHomeId(String homeId)
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

    public String getRoomId()
    {
        return roomId;
    }

    public void setRoomId(String roomId)
    {
        this.roomId = roomId;
    }

    public String getRoomName()
    {
        return roomName;
    }

    public void setRoomName(String roomName)
    {
        this.roomName = roomName;
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

    public String getRawPayload()
    {
        return rawPayload;
    }

    public void setRawPayload(String rawPayload)
    {
        this.rawPayload = rawPayload;
    }

    public Map<String, String> getProperties()
    {
        return properties;
    }

    public void setProperties(Map<String, String> properties)
    {
        this.properties = properties;
    }
}
