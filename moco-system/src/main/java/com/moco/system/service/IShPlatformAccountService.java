package com.moco.system.service;

import java.util.Map;
import com.moco.system.domain.ShPlatformAccount;

public interface IShPlatformAccountService
{
    public ShPlatformAccount getPlatformAccount();

    public int savePlatformAccount(ShPlatformAccount account, String operator);

    public Map<String, Object> startLocalQrLogin();

    public Map<String, Object> checkLocalQrLogin(String sessionId);

    public Map<String, Object> testConnection();

    public Map<String, Object> syncFull(String operator);

    public Map<String, Object> syncDeviceStatus(String triggerMode);
}
