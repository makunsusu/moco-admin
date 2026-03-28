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
import com.moco.system.domain.FinAsset;
import com.moco.system.service.IFinAssetService;

@RestController
@RequestMapping("/finance/asset")
public class FinAssetController extends BaseController
{
    @Autowired
    private IFinAssetService assetService;

    @PreAuthorize("@ss.hasPermi('finance:asset:list')")
    @GetMapping("/list")
    public TableDataInfo list(FinAsset asset)
    {
        startPage();
        return getDataTable(assetService.selectAssetList(asset));
    }

    @PreAuthorize("@ss.hasPermi('finance:asset:query')")
    @GetMapping("/options")
    public AjaxResult options()
    {
        return success(assetService.selectAssetOptions());
    }

    @PreAuthorize("@ss.hasPermi('finance:asset:query')")
    @GetMapping("/{assetId}")
    public AjaxResult getInfo(@PathVariable Long assetId)
    {
        return success(assetService.selectAssetById(assetId));
    }

    @PreAuthorize("@ss.hasPermi('finance:asset:query')")
    @GetMapping("/detail/{assetId}")
    public AjaxResult getDetail(@PathVariable Long assetId, Integer klineLimit)
    {
        return success(assetService.selectAssetDetail(assetId, klineLimit));
    }

    @PreAuthorize("@ss.hasPermi('finance:asset:add')")
    @Log(title = "资产标的管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody FinAsset asset)
    {
        if (!assetService.checkAssetUnique(asset))
        {
            return error("新增资产失败，同一市场下资产编码已存在");
        }
        asset.setCreateBy(getUsername());
        return toAjax(assetService.insertAsset(asset));
    }

    @PreAuthorize("@ss.hasPermi('finance:asset:edit')")
    @Log(title = "资产标的管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody FinAsset asset)
    {
        if (!assetService.checkAssetUnique(asset))
        {
            return error("修改资产失败，同一市场下资产编码已存在");
        }
        asset.setUpdateBy(getUsername());
        return toAjax(assetService.updateAsset(asset));
    }

    @PreAuthorize("@ss.hasPermi('finance:asset:remove')")
    @Log(title = "资产标的管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{assetIds}")
    public AjaxResult remove(@PathVariable Long[] assetIds)
    {
        return toAjax(assetService.deleteAssetByIds(assetIds));
    }
}
