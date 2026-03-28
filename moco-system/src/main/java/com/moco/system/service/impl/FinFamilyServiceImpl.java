package com.moco.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.moco.common.constant.UserConstants;
import com.moco.common.utils.StringUtils;
import com.moco.system.domain.FinFamily;
import com.moco.system.domain.FinMemberAccount;
import com.moco.system.mapper.FinFamilyMapper;
import com.moco.system.service.IFinFamilyService;

@Service
public class FinFamilyServiceImpl implements IFinFamilyService
{
    @Autowired
    private FinFamilyMapper familyMapper;

    @Override
    public List<FinFamily> selectFamilyList(FinFamily family)
    {
        return familyMapper.selectFamilyList(family);
    }

    @Override
    public List<FinFamily> selectFamilyOptions()
    {
        return familyMapper.selectFamilyOptions();
    }

    @Override
    public FinFamily selectFamilyById(Long familyId)
    {
        return familyMapper.selectFamilyById(familyId);
    }

    @Override
    public boolean checkFamilyCodeUnique(FinFamily family)
    {
        Long familyId = StringUtils.isNull(family.getFamilyId()) ? -1L : family.getFamilyId();
        FinFamily info = familyMapper.checkFamilyCodeUnique(family.getFamilyCode());
        if (StringUtils.isNotNull(info) && info.getFamilyId().longValue() != familyId.longValue())
        {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    @Override
    public int insertFamily(FinFamily family)
    {
        return familyMapper.insertFamily(family);
    }

    @Override
    public int updateFamily(FinFamily family)
    {
        return familyMapper.updateFamily(family);
    }

    @Override
    public int deleteFamilyByIds(Long[] familyIds)
    {
        return familyMapper.deleteFamilyByIds(familyIds);
    }

    @Override
    public List<FinMemberAccount> selectMemberList(FinMemberAccount member)
    {
        return familyMapper.selectMemberList(member);
    }

    @Override
    public List<FinMemberAccount> selectMemberOptions(Long familyId)
    {
        return familyMapper.selectMemberOptions(familyId);
    }

    @Override
    public FinMemberAccount selectMemberById(Long memberId)
    {
        return familyMapper.selectMemberById(memberId);
    }

    @Override
    public int insertMember(FinMemberAccount member)
    {
        return familyMapper.insertMember(member);
    }

    @Override
    public int updateMember(FinMemberAccount member)
    {
        return familyMapper.updateMember(member);
    }

    @Override
    public int deleteMemberByIds(Long[] memberIds)
    {
        return familyMapper.deleteMemberByIds(memberIds);
    }
}
