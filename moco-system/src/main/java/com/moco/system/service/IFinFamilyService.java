package com.moco.system.service;

import java.util.List;
import com.moco.system.domain.FinFamily;
import com.moco.system.domain.FinMemberAccount;

public interface IFinFamilyService
{
    public List<FinFamily> selectFamilyList(FinFamily family);

    public List<FinFamily> selectFamilyOptions();

    public FinFamily selectFamilyById(Long familyId);

    public boolean checkFamilyCodeUnique(FinFamily family);

    public int insertFamily(FinFamily family);

    public int updateFamily(FinFamily family);

    public int deleteFamilyByIds(Long[] familyIds);

    public List<FinMemberAccount> selectMemberList(FinMemberAccount member);

    public List<FinMemberAccount> selectMemberOptions(Long familyId);

    public FinMemberAccount selectMemberById(Long memberId);

    public int insertMember(FinMemberAccount member);

    public int updateMember(FinMemberAccount member);

    public int deleteMemberByIds(Long[] memberIds);
}
