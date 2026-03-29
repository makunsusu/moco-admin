package com.moco.web.controller.smarthome;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.moco.common.core.controller.BaseController;
import com.moco.common.core.page.TableDataInfo;
import com.moco.system.domain.ShSyncLog;
import com.moco.system.service.IShSyncLogService;

@RestController
@RequestMapping("/smarthome/sync-log")
public class ShSyncLogController extends BaseController
{
    @Autowired
    private IShSyncLogService syncLogService;

    @PreAuthorize("@ss.hasPermi('smarthome:log:list')")
    @GetMapping("/list")
    public TableDataInfo list(ShSyncLog log)
    {
        startPage();
        return getDataTable(syncLogService.selectSyncLogList(log));
    }
}
