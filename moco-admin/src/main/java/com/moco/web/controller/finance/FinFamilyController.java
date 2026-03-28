package com.moco.web.controller.finance;

import java.util.List;
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
import com.moco.system.domain.FinFamily;
import com.moco.system.domain.FinMemberAccount;
import com.moco.system.domain.vo.FinOverviewVO;
import com.moco.system.service.IFinFamilyService;
import com.moco.system.service.IFinOverviewService;

@RestController
@RequestMapping("/finance/family")
public class FinFamilyController extends BaseController
{
    @Autowired
    private IFinFamilyService familyService;

    @Autowired
    private IFinOverviewService overviewService;

    @PreAuthorize("@ss.hasPermi('finance:family:list')")
    @GetMapping("/list")
    public TableDataInfo list(FinFamily family)
    {
        startPage();
        return getDataTable(familyService.selectFamilyList(family));
    }

    @PreAuthorize("@ss.hasPermi('finance:family:query')")
    @GetMapping("/options")
    public AjaxResult options()
    {
        return success(familyService.selectFamilyOptions());
    }

    @PreAuthorize("@ss.hasPermi('finance:family:query')")
    @GetMapping("/{familyId}")
    public AjaxResult getInfo(@PathVariable Long familyId)
    {
        return success(familyService.selectFamilyById(familyId));
    }

    @PreAuthorize("@ss.hasPermi('finance:family:add')")
    @Log(title = "家庭资产管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody FinFamily family)
    {
        if (!familyService.checkFamilyCodeUnique(family))
        {
            return error("新增家庭失败，家庭编码已存在");
        }
        family.setCreateBy(getUsername());
        return toAjax(familyService.insertFamily(family));
    }

    @PreAuthorize("@ss.hasPermi('finance:family:edit')")
    @Log(title = "家庭资产管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody FinFamily family)
    {
        if (!familyService.checkFamilyCodeUnique(family))
        {
            return error("修改家庭失败，家庭编码已存在");
        }
        family.setUpdateBy(getUsername());
        return toAjax(familyService.updateFamily(family));
    }

    @PreAuthorize("@ss.hasPermi('finance:family:remove')")
    @Log(title = "家庭资产管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{familyIds}")
    public AjaxResult remove(@PathVariable Long[] familyIds)
    {
        return toAjax(familyService.deleteFamilyByIds(familyIds));
    }

    @PreAuthorize("@ss.hasPermi('finance:family:query')")
    @GetMapping("/member/list")
    public TableDataInfo memberList(FinMemberAccount member)
    {
        startPage();
        return getDataTable(familyService.selectMemberList(member));
    }

    @PreAuthorize("@ss.hasPermi('finance:family:query')")
    @GetMapping("/member/options/{familyId}")
    public AjaxResult memberOptions(@PathVariable Long familyId)
    {
        return success(familyService.selectMemberOptions(familyId));
    }

    @PreAuthorize("@ss.hasPermi('finance:family:add')")
    @PostMapping("/member")
    public AjaxResult addMember(@Validated @RequestBody FinMemberAccount member)
    {
        member.setCreateBy(getUsername());
        return toAjax(familyService.insertMember(member));
    }

    @PreAuthorize("@ss.hasPermi('finance:family:edit')")
    @PutMapping("/member")
    public AjaxResult editMember(@Validated @RequestBody FinMemberAccount member)
    {
        member.setUpdateBy(getUsername());
        return toAjax(familyService.updateMember(member));
    }

    @PreAuthorize("@ss.hasPermi('finance:family:remove')")
    @DeleteMapping("/member/{memberIds}")
    public AjaxResult removeMember(@PathVariable Long[] memberIds)
    {
        return toAjax(familyService.deleteMemberByIds(memberIds));
    }

    @PreAuthorize("@ss.hasPermi('finance:family:query')")
    @GetMapping("/overview")
    public AjaxResult overview(Long familyId, Long memberId, Long accountId)
    {
        FinOverviewVO overview = overviewService.getOverview(familyId, memberId, accountId);
        return success(overview);
    }
}
