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
import com.moco.system.domain.FinMarket;
import com.moco.system.service.IFinMarketService;

@RestController
@RequestMapping("/finance/market")
public class FinMarketController extends BaseController
{
    @Autowired
    private IFinMarketService marketService;

    @PreAuthorize("@ss.hasPermi('finance:market:list')")
    @GetMapping("/list")
    public TableDataInfo list(FinMarket market)
    {
        startPage();
        List<FinMarket> list = marketService.selectMarketList(market);
        return getDataTable(list);
    }

    @Log(title = "交易市场管理", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('finance:market:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, FinMarket market)
    {
        List<FinMarket> list = marketService.selectMarketList(market);
        ExcelUtil<FinMarket> util = new ExcelUtil<FinMarket>(FinMarket.class);
        util.exportExcel(response, list, "交易市场数据");
    }

    @PreAuthorize("@ss.hasPermi('finance:market:query')")
    @GetMapping("/{marketId}")
    public AjaxResult getInfo(@PathVariable Long marketId)
    {
        return success(marketService.selectMarketById(marketId));
    }

    @PreAuthorize("@ss.hasPermi('finance:market:add')")
    @Log(title = "交易市场管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody FinMarket market)
    {
        if (!marketService.checkMarketCodeUnique(market))
        {
            return error("新增交易市场'" + market.getMarketName() + "'失败，市场编码已存在");
        }
        if (!marketService.checkMarketNameUnique(market))
        {
            return error("新增交易市场'" + market.getMarketName() + "'失败，市场名称已存在");
        }
        market.setCreateBy(getUsername());
        return toAjax(marketService.insertMarket(market));
    }

    @PreAuthorize("@ss.hasPermi('finance:market:edit')")
    @Log(title = "交易市场管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody FinMarket market)
    {
        if (!marketService.checkMarketCodeUnique(market))
        {
            return error("修改交易市场'" + market.getMarketName() + "'失败，市场编码已存在");
        }
        if (!marketService.checkMarketNameUnique(market))
        {
            return error("修改交易市场'" + market.getMarketName() + "'失败，市场名称已存在");
        }
        market.setUpdateBy(getUsername());
        return toAjax(marketService.updateMarket(market));
    }

    @PreAuthorize("@ss.hasPermi('finance:market:remove')")
    @Log(title = "交易市场管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{marketIds}")
    public AjaxResult remove(@PathVariable Long[] marketIds)
    {
        return toAjax(marketService.deleteMarketByIds(marketIds));
    }
}
