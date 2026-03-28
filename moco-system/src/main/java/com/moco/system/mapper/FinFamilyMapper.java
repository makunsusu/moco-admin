package com.moco.system.mapper;

import java.util.List;
import com.moco.system.domain.FinFamily;
import com.moco.system.domain.FinMemberAccount;

public interface FinFamilyMapper
{
    public List<FinFamily> selectFamilyList(FinFamily family);

    public List<FinFamily> selectFamilyOptions();

    public FinFamily selectFamilyById(Long familyId);

    public FinFamily checkFamilyCodeUnique(String familyCode);

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
