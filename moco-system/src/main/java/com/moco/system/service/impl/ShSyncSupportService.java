package com.moco.system.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.moco.common.utils.StringUtils;
import com.moco.system.domain.ShDevice;
import com.moco.system.domain.ShDeviceProperty;
import com.moco.system.domain.ShHome;
import com.moco.system.domain.ShRoom;
import com.moco.system.mapper.ShDeviceMapper;
import com.moco.system.mapper.ShDevicePropertyMapper;
import com.moco.system.mapper.ShHomeMapper;
import com.moco.system.mapper.ShRoomMapper;
import com.moco.system.service.smarthome.MijiaDeviceRecord;

@Service
public class ShSyncSupportService
{
    @Autowired
    private ShHomeMapper homeMapper;

    @Autowired
    private ShRoomMapper roomMapper;

    @Autowired
    private ShDeviceMapper deviceMapper;

    @Autowired
    private ShDevicePropertyMapper propertyMapper;

    @Transactional(rollbackFor = Exception.class)
    public Map<String, Integer> replaceSnapshot(String region, List<MijiaDeviceRecord> records, boolean fullSync)
    {
        Date now = new Date();
        Map<String, ShHome> homeMap = new LinkedHashMap<>();
        Map<String, ShRoom> roomMap = new LinkedHashMap<>();
        Map<String, ShDevice> deviceMap = new LinkedHashMap<>();
        Map<String, List<ShDeviceProperty>> propertyMap = new LinkedHashMap<>();

        for (MijiaDeviceRecord record : records)
        {
            String homeKey = StringUtils.defaultIfBlank(record.getHomeId(), "DEFAULT_HOME");
            ShHome home = homeMap.computeIfAbsent(homeKey, key -> {
                ShHome item = new ShHome();
                item.setPlatformCode("MIJIA");
                item.setCloudHomeId(StringUtils.defaultIfBlank(record.getHomeId(), "default-home"));
                item.setHomeName(StringUtils.defaultIfBlank(record.getHomeName(), "默认家庭"));
                item.setRegion(region);
                item.setDeviceCount(0);
                item.setRoomCount(0);
                item.setStatus("0");
                item.setLastSyncTime(now);
                return item;
            });
            home.setDeviceCount(home.getDeviceCount() + 1);

            String roomKey = homeKey + ":" + StringUtils.defaultIfBlank(record.getRoomId(), "DEFAULT_ROOM");
            ShRoom room = roomMap.computeIfAbsent(roomKey, key -> {
                ShRoom item = new ShRoom();
                item.setCloudRoomId(StringUtils.defaultIfBlank(record.getRoomId(), "default-room"));
                item.setRoomName(StringUtils.defaultIfBlank(record.getRoomName(), "未分配房间"));
                item.setHomeName(home.getHomeName());
                item.setDeviceCount(0);
                item.setStatus("0");
                item.setLastSyncTime(now);
                return item;
            });
            room.setDeviceCount(room.getDeviceCount() + 1);

            ShDevice device = new ShDevice();
            device.setDid(record.getDid());
            device.setUid(record.getUid());
            device.setDeviceName(StringUtils.defaultIfBlank(record.getDeviceName(), record.getDid()));
            device.setModel(record.getModel());
            device.setDeviceType(record.getDeviceType());
            device.setHomeName(home.getHomeName());
            device.setRoomName(room.getRoomName());
            device.setOnlineStatus(record.getOnlineStatus());
            device.setPowerStatus(record.getPowerStatus());
            device.setRegion(region);
            device.setRawPayload(record.getRawPayload());
            device.setLastSyncTime(now);
            deviceMap.put(record.getDid(), device);

            List<ShDeviceProperty> propertyList = new ArrayList<>();
            for (Map.Entry<String, String> entry : record.getProperties().entrySet())
            {
                ShDeviceProperty property = new ShDeviceProperty();
                property.setPropertyKey(entry.getKey());
                property.setPropertyValue(entry.getValue());
                property.setPropertyType("STRING");
                property.setSnapshotTime(now);
                propertyList.add(property);
            }
            propertyMap.put(record.getDid(), propertyList);
        }

        for (ShHome home : homeMap.values())
        {
            home.setRoomCount((int) roomMap.values().stream().filter(room -> room.getHomeName().equals(home.getHomeName())).count());
        }

        if (fullSync)
        {
            propertyMapper.deleteAllProperties();
            deviceMapper.deleteAllDevices();
            roomMapper.deleteAllRooms();
            homeMapper.deleteAllHomes();
        }

        Map<String, Long> homeIdMap = new LinkedHashMap<>();
        for (ShHome home : homeMap.values())
        {
            ShHome exists = homeMapper.selectHomeByCloudHomeId(home.getCloudHomeId());
            if (exists == null)
            {
                homeMapper.insertHome(home);
                exists = home;
            }
            else
            {
                home.setHomeId(exists.getHomeId());
                homeMapper.updateHome(home);
                exists = homeMapper.selectHomeByCloudHomeId(home.getCloudHomeId());
            }
            homeIdMap.put(home.getCloudHomeId(), exists.getHomeId());
        }

        Map<String, Long> roomIdMap = new LinkedHashMap<>();
        for (Map.Entry<String, ShRoom> entry : roomMap.entrySet())
        {
            ShRoom room = entry.getValue();
            String homeId = entry.getKey().split(":")[0];
            room.setHomeId(homeIdMap.get(homeId));
            ShRoom exists = roomMapper.selectRoomByCloudRoomId(room.getCloudRoomId());
            if (exists == null)
            {
                roomMapper.insertRoom(room);
                exists = room;
            }
            else
            {
                room.setRoomId(exists.getRoomId());
                roomMapper.updateRoom(room);
                exists = roomMapper.selectRoomByCloudRoomId(room.getCloudRoomId());
            }
            roomIdMap.put(entry.getKey(), exists.getRoomId());
        }

        for (MijiaDeviceRecord record : records)
        {
            ShDevice device = deviceMap.get(record.getDid());
            if (device == null)
            {
                continue;
            }
            String roomKey = StringUtils.defaultIfBlank(record.getHomeId(), "DEFAULT_HOME") + ":" + StringUtils.defaultIfBlank(record.getRoomId(), "DEFAULT_ROOM");
            device.setHomeId(homeIdMap.get(StringUtils.defaultIfBlank(record.getHomeId(), "DEFAULT_HOME")));
            device.setRoomId(roomIdMap.get(roomKey));
            ShDevice exists = deviceMapper.selectDeviceByDid(device.getDid());
            if (exists == null)
            {
                deviceMapper.insertDevice(device);
                exists = deviceMapper.selectDeviceByDid(device.getDid());
            }
            else
            {
                device.setDeviceId(exists.getDeviceId());
                deviceMapper.updateDevice(device);
                exists = deviceMapper.selectDeviceByDid(device.getDid());
            }
            propertyMapper.deletePropertyByDeviceId(exists.getDeviceId());
            for (ShDeviceProperty property : propertyMap.getOrDefault(device.getDid(), new ArrayList<>()))
            {
                property.setDeviceId(exists.getDeviceId());
                propertyMapper.insertDeviceProperty(property);
            }
        }

        Map<String, Integer> result = new LinkedHashMap<>();
        result.put("homeCount", homeMap.size());
        result.put("roomCount", roomMap.size());
        result.put("deviceCount", records.size());
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    public Map<String, Integer> refreshRuntimeState(List<MijiaDeviceRecord> records, String operator)
    {
        Date now = new Date();
        int matchedCount = 0;
        int changedCount = 0;
        int resolvedCount = 0;
        for (MijiaDeviceRecord record : records)
        {
            if (StringUtils.isBlank(record.getDid()))
            {
                continue;
            }
            ShDevice exists = deviceMapper.selectDeviceByDid(record.getDid());
            if (exists == null)
            {
                continue;
            }
            matchedCount++;
            boolean changed = !StringUtils.equals(StringUtils.defaultString(exists.getOnlineStatus()), StringUtils.defaultString(record.getOnlineStatus()))
                || !StringUtils.equals(StringUtils.defaultString(exists.getPowerStatus()), StringUtils.defaultString(record.getPowerStatus()))
                || !StringUtils.equals(StringUtils.defaultString(exists.getDeviceName()), StringUtils.defaultString(record.getDeviceName()))
                || !StringUtils.equals(StringUtils.defaultString(exists.getHomeName()), StringUtils.defaultString(record.getHomeName()))
                || !StringUtils.equals(StringUtils.defaultString(exists.getRoomName()), StringUtils.defaultString(record.getRoomName()))
                || !StringUtils.equals(StringUtils.defaultString(exists.getModel()), StringUtils.defaultString(record.getModel()));
            if ("ON".equals(record.getPowerStatus()) || "OFF".equals(record.getPowerStatus()))
            {
                resolvedCount++;
            }
            ShDevice runtime = new ShDevice();
            runtime.setDid(record.getDid());
            runtime.setUid(record.getUid());
            runtime.setHomeName(record.getHomeName());
            runtime.setRoomName(record.getRoomName());
            runtime.setDeviceName(record.getDeviceName());
            runtime.setModel(record.getModel());
            runtime.setDeviceType(record.getDeviceType());
            runtime.setOnlineStatus(record.getOnlineStatus());
            runtime.setPowerStatus(record.getPowerStatus());
            runtime.setRawPayload(record.getRawPayload());
            runtime.setLastSyncTime(now);
            runtime.setUpdateBy(operator);
            deviceMapper.updateRuntimeStateByDid(runtime);
            if (changed)
            {
                changedCount++;
            }
        }
        Map<String, Integer> result = new LinkedHashMap<>();
        result.put("deviceCount", records.size());
        result.put("matchedCount", matchedCount);
        result.put("changedCount", changedCount);
        result.put("resolvedCount", resolvedCount);
        return result;
    }
}
