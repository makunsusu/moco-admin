package com.moco.web.controller.smarthome;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.moco.common.annotation.Log;
import com.moco.common.core.controller.BaseController;
import com.moco.common.core.domain.AjaxResult;
import com.moco.common.enums.BusinessType;
import com.moco.system.domain.ShPlatformAccount;
import com.moco.system.service.IShPlatformAccountService;

@RestController
@RequestMapping("/smarthome/platform")
public class ShPlatformAccountController extends BaseController
{
    @Autowired
    private IShPlatformAccountService platformAccountService;

    @PreAuthorize("@ss.hasPermi('smarthome:platform:query')")
    @GetMapping("/account")
    public AjaxResult getAccount()
    {
        return success(platformAccountService.getPlatformAccount());
    }

    @PreAuthorize("@ss.hasPermi('smarthome:platform:edit')")
    @Log(title = "智能家居平台接入", businessType = BusinessType.UPDATE)
    @PutMapping("/account")
    public AjaxResult updateAccount(@Validated @RequestBody ShPlatformAccount account)
    {
        return toAjax(platformAccountService.savePlatformAccount(account, getUsername()));
    }

    @PreAuthorize("@ss.hasPermi('smarthome:platform:edit')")
    @PostMapping("/qr/start")
    public AjaxResult startLocalQrLogin()
    {
        return success(platformAccountService.startLocalQrLogin());
    }

    @PreAuthorize("@ss.hasPermi('smarthome:platform:edit')")
    @GetMapping("/qr/status/{sessionId}")
    public AjaxResult checkLocalQrLogin(@PathVariable String sessionId)
    {
        return success(platformAccountService.checkLocalQrLogin(sessionId));
    }

    @PreAuthorize("@ss.hasPermi('smarthome:platform:sync')")
    @PostMapping("/test")
    public AjaxResult testConnection()
    {
        return success(platformAccountService.testConnection());
    }

    @PreAuthorize("@ss.hasPermi('smarthome:platform:sync')")
    @Log(title = "智能家居平台接入", businessType = BusinessType.OTHER)
    @PostMapping("/sync/full")
    public AjaxResult syncFull()
    {
        return success(platformAccountService.syncFull(getUsername()));
    }

    @PreAuthorize("@ss.hasPermi('smarthome:platform:sync')")
    @Log(title = "智能家居平台接入", businessType = BusinessType.OTHER)
    @PostMapping("/sync/status")
    public AjaxResult syncStatus()
    {
        return success(platformAccountService.syncDeviceStatus("MANUAL"));
    }
}
