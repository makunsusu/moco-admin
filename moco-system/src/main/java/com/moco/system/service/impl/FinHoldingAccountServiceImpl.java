package com.moco.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.moco.system.domain.FinHoldingAccount;
import com.moco.system.mapper.FinHoldingAccountMapper;
import com.moco.system.service.IFinHoldingAccountService;

@Service
public class FinHoldingAccountServiceImpl implements IFinHoldingAccountService
{
    @Autowired
    private FinHoldingAccountMapper accountMapper;

    @Override
    public List<FinHoldingAccount> selectHoldingAccountList(FinHoldingAccount account)
    {
        return accountMapper.selectHoldingAccountList(account);
    }

    @Override
    public List<FinHoldingAccount> selectHoldingAccountOptions(Long familyId, Long memberId)
    {
        return accountMapper.selectHoldingAccountOptions(familyId, memberId);
    }

    @Override
    public FinHoldingAccount selectHoldingAccountById(Long accountId)
    {
        return accountMapper.selectHoldingAccountById(accountId);
    }

    @Override
    public int insertHoldingAccount(FinHoldingAccount account)
    {
        return accountMapper.insertHoldingAccount(account);
    }

    @Override
    public int updateHoldingAccount(FinHoldingAccount account)
    {
        return accountMapper.updateHoldingAccount(account);
    }

    @Override
    public int deleteHoldingAccountByIds(Long[] accountIds)
    {
        return accountMapper.deleteHoldingAccountByIds(accountIds);
    }
}
