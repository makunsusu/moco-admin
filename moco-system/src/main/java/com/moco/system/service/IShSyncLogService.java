package com.moco.system.service;

import java.util.List;
import com.moco.system.domain.ShSyncLog;

public interface IShSyncLogService
{
    public List<ShSyncLog> selectSyncLogList(ShSyncLog log);
}
