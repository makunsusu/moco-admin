package com.moco.system.domain.vo;

import java.util.ArrayList;
import java.util.List;
import com.moco.system.domain.ShDevice;
import com.moco.system.domain.ShDeviceProperty;
import com.moco.system.domain.ShSyncLog;

public class ShDeviceDetailVO
{
    private ShDevice device;

    private List<ShDeviceProperty> propertyList = new ArrayList<>();

    private List<ShSyncLog> recentLogs = new ArrayList<>();

    public ShDevice getDevice()
    {
        return device;
    }

    public void setDevice(ShDevice device)
    {
        this.device = device;
    }

    public List<ShDeviceProperty> getPropertyList()
    {
        return propertyList;
    }

    public void setPropertyList(List<ShDeviceProperty> propertyList)
    {
        this.propertyList = propertyList;
    }

    public List<ShSyncLog> getRecentLogs()
    {
        return recentLogs;
    }

    public void setRecentLogs(List<ShSyncLog> recentLogs)
    {
        this.recentLogs = recentLogs;
    }
}
