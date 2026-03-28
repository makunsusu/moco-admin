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
import com.moco.system.domain.FinHoldingAccount;
import com.moco.system.service.IFinHoldingAccountService;

@RestController
@RequestMapping("/finance/account")
public class FinHoldingAccountController extends BaseController
{
    @Autowired
    private IFinHoldingAccountService holdingAccountService;

    @PreAuthorize("@ss.hasPermi('finance:account:list')")
    @GetMapping("/list")
    public TableDataInfo list(FinHoldingAccount account)
    {
        startPage();
        return getDataTable(holdingAccountService.selectHoldingAccountList(account));
    }

    @PreAuthorize("@ss.hasPermi('finance:account:query')")
    @GetMapping("/options")
    public AjaxResult options(Long familyId, Long memberId)
    {
        return success(holdingAccountService.selectHoldingAccountOptions(familyId, memberId));
    }

    @PreAuthorize("@ss.hasPermi('finance:account:query')")
    @GetMapping("/{accountId}")
    public AjaxResult getInfo(@PathVariable Long accountId)
    {
        return success(holdingAccountService.selectHoldingAccountById(accountId));
    }

    @PreAuthorize("@ss.hasPermi('finance:account:add')")
    @Log(title = "持仓账户管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody FinHoldingAccount account)
    {
        account.setCreateBy(getUsername());
        return toAjax(holdingAccountService.insertHoldingAccount(account));
    }

    @PreAuthorize("@ss.hasPermi('finance:account:edit')")
    @Log(title = "持仓账户管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody FinHoldingAccount account)
    {
        account.setUpdateBy(getUsername());
        return toAjax(holdingAccountService.updateHoldingAccount(account));
    }

    @PreAuthorize("@ss.hasPermi('finance:account:remove')")
    @Log(title = "持仓账户管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{accountIds}")
    public AjaxResult remove(@PathVariable Long[] accountIds)
    {
        return toAjax(holdingAccountService.deleteHoldingAccountByIds(accountIds));
    }
}
