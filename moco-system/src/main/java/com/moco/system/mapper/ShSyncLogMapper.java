package com.moco.system.mapper;

import java.util.List;
import com.moco.system.domain.ShSyncLog;

public interface ShSyncLogMapper
{
    public List<ShSyncLog> selectSyncLogList(ShSyncLog log);

    public List<ShSyncLog> selectRecentLogs();

    public int insertSyncLog(ShSyncLog log);
}
