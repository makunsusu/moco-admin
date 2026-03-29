package com.moco.system.mapper;

import java.util.List;
import com.moco.system.domain.ShRoom;

public interface ShRoomMapper
{
    public List<ShRoom> selectRoomList(ShRoom room);

    public int insertRoom(ShRoom room);

    public int updateRoom(ShRoom room);

    public ShRoom selectRoomByCloudRoomId(String cloudRoomId);

    public int deleteAllRooms();
}
