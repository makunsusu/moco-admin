package com.moco.system.service;

import java.util.List;
import com.moco.system.domain.ShHome;
import com.moco.system.domain.ShRoom;

public interface IShHomeService
{
    public List<ShHome> selectHomeList(ShHome home);

    public List<ShRoom> selectRoomList(ShRoom room);
}
