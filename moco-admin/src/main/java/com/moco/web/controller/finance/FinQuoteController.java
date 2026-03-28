package com.moco.web.controller.finance;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.moco.common.annotation.Log;
import com.moco.common.core.controller.BaseController;
import com.moco.common.core.domain.AjaxResult;
import com.moco.common.enums.BusinessType;
import com.moco.system.service.IFinQuoteService;

@RestController
@RequestMapping("/finance/quote")
public class FinQuoteController extends BaseController
{
    @Autowired
    private IFinQuoteService quoteService;

    @PreAuthorize("@ss.hasPermi('finance:quote:query')")
    @GetMapping("/latest")
    public AjaxResult latest()
    {
        return success(quoteService.selectLatestQuotes());
    }

    @PreAuthorize("@ss.hasPermi('finance:quote:refresh')")
    @Log(title = "行情刷新", businessType = BusinessType.UPDATE)
    @PostMapping("/refresh")
    public AjaxResult refresh(@RequestBody(required = false) Long[] assetIds)
    {
        return success(quoteService.refreshQuotes(assetIds));
    }
}
