package com.moco.system.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.moco.system.domain.FinTransaction;
import com.moco.system.mapper.FinTransactionMapper;
import com.moco.system.service.IFinTransactionService;

@Service
public class FinTransactionServiceImpl implements IFinTransactionService
{
    @Autowired
    private FinTransactionMapper transactionMapper;

    @Autowired
    private FinPositionSupportService positionSupportService;

    @Override
    public List<FinTransaction> selectTransactionList(FinTransaction transaction)
    {
        return transactionMapper.selectTransactionList(transaction);
    }

    @Override
    public FinTransaction selectTransactionById(Long transactionId)
    {
        return transactionMapper.selectTransactionById(transactionId);
    }

    @Override
    public int insertTransaction(FinTransaction transaction)
    {
        fillAmount(transaction);
        positionSupportService.validateTransaction(transaction, null);
        return transactionMapper.insertTransaction(transaction);
    }

    @Override
    public int updateTransaction(FinTransaction transaction)
    {
        fillAmount(transaction);
        positionSupportService.validateTransaction(transaction, transaction.getTransactionId());
        return transactionMapper.updateTransaction(transaction);
    }

    @Override
    public int deleteTransactionByIds(Long[] transactionIds)
    {
        return transactionMapper.deleteTransactionByIds(transactionIds);
    }

    private void fillAmount(FinTransaction transaction)
    {
        if (transaction.getAmount() == null && transaction.getPrice() != null && transaction.getQuantity() != null)
        {
            transaction.setAmount(transaction.getPrice().multiply(transaction.getQuantity()).setScale(2, RoundingMode.HALF_UP));
        }
        if (transaction.getFee() == null)
        {
            transaction.setFee(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
        }
    }
}
