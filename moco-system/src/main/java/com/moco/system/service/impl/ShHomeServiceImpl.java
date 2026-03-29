package com.moco.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.moco.system.domain.ShHome;
import com.moco.system.domain.ShRoom;
import com.moco.system.mapper.ShHomeMapper;
import com.moco.system.mapper.ShRoomMapper;
import com.moco.system.service.IShHomeService;

@Service
public class ShHomeServiceImpl implements IShHomeService
{
    @Autowired
    private ShHomeMapper homeMapper;

    @Autowired
    private ShRoomMapper roomMapper;

    @Override
    public List<ShHome> selectHomeList(ShHome home)
    {
        return homeMapper.selectHomeList(home);
    }

    @Override
    public List<ShRoom> selectRoomList(ShRoom room)
    {
        return roomMapper.selectRoomList(room);
    }
}
