package com.moco.system.service;

import java.util.List;
import com.moco.system.domain.FinHoldingAccount;

public interface IFinHoldingAccountService
{
    public List<FinHoldingAccount> selectHoldingAccountList(FinHoldingAccount account);

    public List<FinHoldingAccount> selectHoldingAccountOptions(Long familyId, Long memberId);

    public FinHoldingAccount selectHoldingAccountById(Long accountId);

    public int insertHoldingAccount(FinHoldingAccount account);

    public int updateHoldingAccount(FinHoldingAccount account);

    public int deleteHoldingAccountByIds(Long[] accountIds);
}
