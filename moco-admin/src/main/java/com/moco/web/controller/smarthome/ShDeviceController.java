package com.moco.web.controller.smarthome;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.moco.common.annotation.Log;
import com.moco.common.core.controller.BaseController;
import com.moco.common.core.domain.AjaxResult;
import com.moco.common.core.page.TableDataInfo;
import com.moco.common.enums.BusinessType;
import com.moco.system.domain.ShDevice;
import com.moco.system.service.IShDeviceService;

@RestController
@RequestMapping("/smarthome/device")
public class ShDeviceController extends BaseController
{
    @Autowired
    private IShDeviceService deviceService;

    @PreAuthorize("@ss.hasPermi('smarthome:device:list')")
    @GetMapping("/list")
    public TableDataInfo list(ShDevice device)
    {
        startPage();
        return getDataTable(deviceService.selectDeviceList(device));
    }

    @PreAuthorize("@ss.hasPermi('smarthome:device:query')")
    @GetMapping("/{deviceId}")
    public AjaxResult getInfo(@PathVariable Long deviceId)
    {
        return success(deviceService.selectDeviceDetail(deviceId));
    }

    @PreAuthorize("@ss.hasPermi('smarthome:platform:sync')")
    @Log(title = "智能家居设备", businessType = BusinessType.UPDATE)
    @PostMapping("/{deviceId}/power/{action}")
    public AjaxResult controlPower(@PathVariable Long deviceId, @PathVariable String action)
    {
        boolean powerOn = "on".equalsIgnoreCase(action);
        if (!powerOn && !"off".equalsIgnoreCase(action))
        {
            return error("仅支持 on / off 控制动作");
        }
        return success(deviceService.controlDevicePower(deviceId, powerOn, getUsername()));
    }
}
