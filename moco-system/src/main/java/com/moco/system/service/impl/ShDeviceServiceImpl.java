package com.moco.system.service.impl;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.moco.system.domain.ShPlatformAccount;
import com.moco.system.domain.ShDevice;
import com.moco.system.domain.vo.ShDeviceDetailVO;
import com.moco.system.mapper.ShDeviceMapper;
import com.moco.system.mapper.ShDevicePropertyMapper;
import com.moco.system.mapper.ShPlatformAccountMapper;
import com.moco.system.mapper.ShSyncLogMapper;
import com.moco.system.service.IShDeviceService;
import com.moco.system.service.smarthome.MijiaClient;
import com.moco.system.service.smarthome.MijiaClientException;

@Service
public class ShDeviceServiceImpl implements IShDeviceService
{
    @Autowired
    private ShDeviceMapper deviceMapper;

    @Autowired
    private ShDevicePropertyMapper propertyMapper;

    @Autowired
    private ShSyncLogMapper syncLogMapper;

    @Autowired
    private ShPlatformAccountMapper platformAccountMapper;

    @Autowired
    private MijiaClient mijiaClient;

    @Override
    public List<ShDevice> selectDeviceList(ShDevice device)
    {
        return deviceMapper.selectDeviceList(device);
    }

    @Override
    public ShDeviceDetailVO selectDeviceDetail(Long deviceId)
    {
        ShDeviceDetailVO detail = new ShDeviceDetailVO();
        detail.setDevice(deviceMapper.selectDeviceById(deviceId));
        detail.setPropertyList(propertyMapper.selectPropertyListByDeviceId(deviceId));
        detail.setRecentLogs(syncLogMapper.selectRecentLogs());
        return detail;
    }

    @Override
    public Map<String, Object> controlDevicePower(Long deviceId, boolean powerOn, String operator)
    {
        ShDevice device = deviceMapper.selectDeviceById(deviceId);
        if (device == null)
        {
            throw new MijiaClientException("设备不存在或已删除");
        }
        ShPlatformAccount account = platformAccountMapper.selectPlatformAccount();
        if (account == null)
        {
            throw new MijiaClientException("请先完成米家平台接入");
        }
        Map<String, Object> result = mijiaClient.setDevicePower(account, device.getDid(), powerOn);
        List<com.moco.system.service.smarthome.MijiaDeviceRecord> records = mijiaClient.fetchDeviceStates(account);
        com.moco.system.service.smarthome.MijiaDeviceRecord runtime = records.stream()
            .filter(item -> device.getDid().equals(item.getDid()))
            .findFirst()
            .orElse(null);
        ShDevice update = new ShDevice();
        update.setDid(device.getDid());
        update.setUpdateBy(operator);
        update.setLastSyncTime(new java.util.Date());
        if (runtime != null)
        {
            update.setOnlineStatus(runtime.getOnlineStatus());
            update.setPowerStatus(runtime.getPowerStatus());
            update.setRawPayload(runtime.getRawPayload());
            result.put("powerStatus", runtime.getPowerStatus());
            result.put("onlineStatus", runtime.getOnlineStatus());
        }
        else
        {
            update.setPowerStatus(powerOn ? "ON" : "OFF");
            result.put("powerStatus", powerOn ? "ON" : "OFF");
        }
        deviceMapper.updateRuntimeStateByDid(update);
        result.put("deviceId", deviceId);
        result.put("did", device.getDid());
        result.put("message", (powerOn ? "开启" : "关闭") + "指令已发送");
        return result;
    }
}
