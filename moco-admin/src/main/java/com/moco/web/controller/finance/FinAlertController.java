package com.moco.web.controller.finance;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
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
import com.moco.common.core.page.TableDataInfo;
import com.moco.common.enums.BusinessType;
import com.moco.system.domain.FinAlertEvent;
import com.moco.system.domain.FinAlertRule;
import com.moco.system.service.IFinAlertService;

@RestController
@RequestMapping("/finance/alert")
public class FinAlertController extends BaseController
{
    @Autowired
    private IFinAlertService alertService;

    @PreAuthorize("@ss.hasPermi('finance:alert:list')")
    @GetMapping("/rule/list")
    public TableDataInfo ruleList(FinAlertRule rule)
    {
        startPage();
        return getDataTable(alertService.selectAlertRuleList(rule));
    }

    @PreAuthorize("@ss.hasPermi('finance:alert:query')")
    @GetMapping("/rule/{ruleId}")
    public AjaxResult getRule(@PathVariable Long ruleId)
    {
        return success(alertService.selectAlertRuleById(ruleId));
    }

    @PreAuthorize("@ss.hasPermi('finance:alert:add')")
    @Log(title = "提醒规则管理", businessType = BusinessType.INSERT)
    @PostMapping("/rule")
    public AjaxResult addRule(@Validated @RequestBody FinAlertRule rule)
    {
        rule.setCreateBy(getUsername());
        return toAjax(alertService.insertAlertRule(rule));
    }

    @PreAuthorize("@ss.hasPermi('finance:alert:edit')")
    @Log(title = "提醒规则管理", businessType = BusinessType.UPDATE)
    @PutMapping("/rule")
    public AjaxResult editRule(@Validated @RequestBody FinAlertRule rule)
    {
        rule.setUpdateBy(getUsername());
        return toAjax(alertService.updateAlertRule(rule));
    }

    @PreAuthorize("@ss.hasPermi('finance:alert:remove')")
    @Log(title = "提醒规则管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/rule/{ruleIds}")
    public AjaxResult removeRule(@PathVariable Long[] ruleIds)
    {
        return toAjax(alertService.deleteAlertRuleByIds(ruleIds));
    }

    @PreAuthorize("@ss.hasPermi('finance:alert:query')")
    @GetMapping("/event/list")
    public TableDataInfo eventList(FinAlertEvent event)
    {
        startPage();
        return getDataTable(alertService.selectAlertEventList(event));
    }

    @PreAuthorize("@ss.hasPermi('finance:alert:edit')")
    @PutMapping("/event/{eventId}/status/{status}")
    public AjaxResult updateEventStatus(@PathVariable Long eventId, @PathVariable String status)
    {
        return toAjax(alertService.updateAlertEventStatus(eventId, status));
    }
}
