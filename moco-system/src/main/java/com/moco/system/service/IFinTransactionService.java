package com.moco.system.service;

import java.util.List;
import com.moco.system.domain.FinTransaction;

public interface IFinTransactionService
{
    public List<FinTransaction> selectTransactionList(FinTransaction transaction);

    public FinTransaction selectTransactionById(Long transactionId);

    public int insertTransaction(FinTransaction transaction);

    public int updateTransaction(FinTransaction transaction);

    public int deleteTransactionByIds(Long[] transactionIds);
}
