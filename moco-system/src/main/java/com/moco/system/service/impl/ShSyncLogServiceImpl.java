package com.moco.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.moco.system.domain.ShSyncLog;
import com.moco.system.mapper.ShSyncLogMapper;
import com.moco.system.service.IShSyncLogService;

@Service
public class ShSyncLogServiceImpl implements IShSyncLogService
{
    @Autowired
    private ShSyncLogMapper syncLogMapper;

    @Override
    public List<ShSyncLog> selectSyncLogList(ShSyncLog log)
    {
        return syncLogMapper.selectSyncLogList(log);
    }
}
