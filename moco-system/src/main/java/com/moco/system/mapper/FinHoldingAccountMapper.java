package com.moco.system.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.moco.system.domain.FinHoldingAccount;

public interface FinHoldingAccountMapper
{
    public List<FinHoldingAccount> selectHoldingAccountList(FinHoldingAccount account);

    public List<FinHoldingAccount> selectHoldingAccountOptions(@Param("familyId") Long familyId, @Param("memberId") Long memberId);

    public FinHoldingAccount selectHoldingAccountById(Long accountId);

    public int insertHoldingAccount(FinHoldingAccount account);

    public int updateHoldingAccount(FinHoldingAccount account);

    public int deleteHoldingAccountByIds(Long[] accountIds);
}
