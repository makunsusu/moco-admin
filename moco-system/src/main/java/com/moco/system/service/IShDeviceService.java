package com.moco.system.service;

import java.util.List;
import java.util.Map;
import com.moco.system.domain.ShDevice;
import com.moco.system.domain.vo.ShDeviceDetailVO;

public interface IShDeviceService
{
    public List<ShDevice> selectDeviceList(ShDevice device);

    public ShDeviceDetailVO selectDeviceDetail(Long deviceId);

    public Map<String, Object> controlDevicePower(Long deviceId, boolean powerOn, String operator);
}
