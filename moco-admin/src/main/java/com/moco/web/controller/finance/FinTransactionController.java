package com.moco.web.controller.finance;

import java.util.List;
import jakarta.servlet.http.HttpServletResponse;
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
import com.moco.common.utils.poi.ExcelUtil;
import com.moco.system.domain.FinTransaction;
import com.moco.system.service.IFinTransactionService;

@RestController
@RequestMapping("/finance/transaction")
public class FinTransactionController extends BaseController
{
    @Autowired
    private IFinTransactionService transactionService;

    @PreAuthorize("@ss.hasPermi('finance:transaction:list')")
    @GetMapping("/list")
    public TableDataInfo list(FinTransaction transaction)
    {
        startPage();
        return getDataTable(transactionService.selectTransactionList(transaction));
    }

    @PreAuthorize("@ss.hasPermi('finance:transaction:export')")
    @Log(title = "交易流水管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, FinTransaction transaction)
    {
        List<FinTransaction> list = transactionService.selectTransactionList(transaction);
        ExcelUtil<FinTransaction> util = new ExcelUtil<>(FinTransaction.class);
        util.exportExcel(response, list, "交易流水");
    }

    @PreAuthorize("@ss.hasPermi('finance:transaction:query')")
    @GetMapping("/{transactionId}")
    public AjaxResult getInfo(@PathVariable Long transactionId)
    {
        return success(transactionService.selectTransactionById(transactionId));
    }

    @PreAuthorize("@ss.hasPermi('finance:transaction:add')")
    @Log(title = "交易流水管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody FinTransaction transaction)
    {
        transaction.setCreateBy(getUsername());
        return toAjax(transactionService.insertTransaction(transaction));
    }

    @PreAuthorize("@ss.hasPermi('finance:transaction:edit')")
    @Log(title = "交易流水管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody FinTransaction transaction)
    {
        transaction.setUpdateBy(getUsername());
        return toAjax(transactionService.updateTransaction(transaction));
    }

    @PreAuthorize("@ss.hasPermi('finance:transaction:remove')")
    @Log(title = "交易流水管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{transactionIds}")
    public AjaxResult remove(@PathVariable Long[] transactionIds)
    {
        return toAjax(transactionService.deleteTransactionByIds(transactionIds));
    }
}
