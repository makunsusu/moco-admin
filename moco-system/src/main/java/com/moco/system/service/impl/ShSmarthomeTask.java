package com.moco.system.service.impl;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("smarthomeTask")
public class ShSmarthomeTask
{
    private static final Logger log = LoggerFactory.getLogger(ShSmarthomeTask.class);

    @Autowired
    private ShPlatformAccountServiceImpl platformAccountService;

    public void syncDeviceStatus()
    {
        execute("SCHEDULED", "设备状态刷新");
    }

    public void syncFullSnapshot()
    {
        execute("SCHEDULED", "全量快照同步");
    }

    private void execute(String triggerMode, String taskName)
    {
        try
        {
            Map<String, Object> result = "全量快照同步".equals(taskName)
                ? platformAccountService.syncFull("task")
                : platformAccountService.syncDeviceStatus(triggerMode);
            log.info("{}完成: {}", taskName, result);
        }
        catch (Exception e)
        {
            log.error(taskName + "失败", e);
        }
    }
}
