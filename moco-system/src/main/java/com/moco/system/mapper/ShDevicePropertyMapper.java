package com.moco.system.mapper;

import java.util.List;
import com.moco.system.domain.ShDeviceProperty;

public interface ShDevicePropertyMapper
{
    public List<ShDeviceProperty> selectPropertyListByDeviceId(Long deviceId);

    public int insertDeviceProperty(ShDeviceProperty property);

    public int deletePropertyByDeviceId(Long deviceId);

    public int deleteAllProperties();
}
