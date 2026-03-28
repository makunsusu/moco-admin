package com.moco.system.mapper;

import java.util.List;
import com.moco.system.domain.FinTransaction;

public interface FinTransactionMapper
{
    public List<FinTransaction> selectTransactionList(FinTransaction transaction);

    public FinTransaction selectTransactionById(Long transactionId);

    public List<FinTransaction> selectTransactionsForCalc(FinTransaction transaction);

    public int insertTransaction(FinTransaction transaction);

    public int updateTransaction(FinTransaction transaction);

    public int deleteTransactionByIds(Long[] transactionIds);
}
