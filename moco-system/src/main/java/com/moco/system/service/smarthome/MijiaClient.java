package com.moco.system.service.smarthome;

import java.util.List;
import java.util.Map;
import com.moco.system.domain.ShPlatformAccount;

public interface MijiaClient
{
    public void testConnection(ShPlatformAccount account);

    public List<MijiaDeviceRecord> fetchDevices(ShPlatformAccount account);

    public List<MijiaDeviceRecord> fetchDeviceStates(ShPlatformAccount account);

    public Map<String, Object> setDevicePower(ShPlatformAccount account, String did, boolean powerOn);
}
