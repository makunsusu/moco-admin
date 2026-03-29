package com.moco.system.mapper;

import java.util.List;
import com.moco.system.domain.ShDevice;

public interface ShDeviceMapper
{
    public List<ShDevice> selectDeviceList(ShDevice device);

    public ShDevice selectDeviceById(Long deviceId);

    public ShDevice selectDeviceByDid(String did);

    public int insertDevice(ShDevice device);

    public int updateDevice(ShDevice device);

    public int updateRuntimeStateByDid(ShDevice device);

    public int deleteAllDevices();
}
