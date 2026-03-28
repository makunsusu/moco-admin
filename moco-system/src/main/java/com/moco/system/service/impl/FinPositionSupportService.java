package com.moco.system.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.moco.common.exception.ServiceException;
import com.moco.system.domain.FinTransaction;
import com.moco.system.domain.vo.FinPositionSummary;
import com.moco.system.mapper.FinTransactionMapper;

@Service
public class FinPositionSupportService
{
    private static final BigDecimal ZERO = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);

    @Autowired
    private FinTransactionMapper transactionMapper;

    public List<FinPositionSummary> listPositionSummaries(Long familyId, Long memberId, Long accountId, Long assetId)
    {
        FinTransaction query = new FinTransaction();
        query.setFamilyId(familyId);
        query.setMemberId(memberId);
        query.setAccountId(accountId);
        query.setAssetId(assetId);
        List<FinTransaction> transactions = transactionMapper.selectTransactionsForCalc(query);
        return calculatePositions(transactions);
    }

    public void validateTransaction(FinTransaction transaction, Long excludeTransactionId)
    {
        FinTransaction query = new FinTransaction();
        query.setFamilyId(transaction.getFamilyId());
        query.setMemberId(transaction.getMemberId());
        query.setAccountId(transaction.getAccountId());
        query.setAssetId(transaction.getAssetId());
        List<FinTransaction> transactions = transactionMapper.selectTransactionsForCalc(query);
        List<FinTransaction> merged = new ArrayList<>();
        for (FinTransaction item : transactions)
        {
            if (excludeTransactionId != null && excludeTransactionId.equals(item.getTransactionId()))
            {
                continue;
            }
            merged.add(item);
        }
        merged.add(transaction);
        merged.sort(Comparator.comparing(FinTransaction::getTradeDate).thenComparing(item ->
            item.getTransactionId() == null ? Long.MAX_VALUE : item.getTransactionId()));
        calculatePositions(merged);
    }

    private List<FinPositionSummary> calculatePositions(List<FinTransaction> transactions)
    {
        Map<String, FinPositionSummary> summaryMap = new LinkedHashMap<>();
        for (FinTransaction transaction : transactions)
        {
            String key = transaction.getFamilyId() + "_" + transaction.getMemberId() + "_" + transaction.getAccountId() + "_" + transaction.getAssetId();
            FinPositionSummary summary = summaryMap.computeIfAbsent(key, k -> {
                FinPositionSummary item = new FinPositionSummary();
                item.setFamilyId(transaction.getFamilyId());
                item.setMemberId(transaction.getMemberId());
                item.setAccountId(transaction.getAccountId());
                item.setAssetId(transaction.getAssetId());
                item.setHoldingQty(BigDecimal.ZERO);
                item.setCostAmount(ZERO);
                item.setRealizedProfitAmount(ZERO);
                item.setInvestedAmount(ZERO);
                item.setNetCashOutflow(ZERO);
                return item;
            });
            applyTransaction(summary, transaction);
        }
        return new ArrayList<>(summaryMap.values());
    }

    private void applyTransaction(FinPositionSummary summary, FinTransaction transaction)
    {
        BigDecimal quantity = safeScale(transaction.getQuantity(), 4);
        BigDecimal amount = transaction.getAmount() == null
            ? safeScale(safeScale(transaction.getPrice(), 4).multiply(quantity), 2)
            : safeScale(transaction.getAmount(), 2);
        BigDecimal fee = safeScale(transaction.getFee(), 2);
        String type = transaction.getTransactionType();

        if ("BUY".equals(type) || "TRANSFER_IN".equals(type))
        {
            summary.setHoldingQty(safeScale(summary.getHoldingQty().add(quantity), 4));
            summary.setCostAmount(safeScale(summary.getCostAmount().add(amount).add(fee), 2));
            summary.setInvestedAmount(safeScale(summary.getInvestedAmount().add(amount).add(fee), 2));
            summary.setNetCashOutflow(safeScale(summary.getNetCashOutflow().add(amount).add(fee), 2));
            return;
        }

        if ("SELL".equals(type) || "TRANSFER_OUT".equals(type))
        {
            if (summary.getHoldingQty().compareTo(quantity) < 0)
            {
                throw new ServiceException("卖出或转出数量不能大于当前持仓数量");
            }
            BigDecimal avgCost = summary.getHoldingQty().compareTo(BigDecimal.ZERO) > 0
                ? summary.getCostAmount().divide(summary.getHoldingQty(), 8, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
            BigDecimal reduceCost = safeScale(avgCost.multiply(quantity), 2);
            BigDecimal proceeds = safeScale(amount.subtract(fee), 2);
            summary.setHoldingQty(safeScale(summary.getHoldingQty().subtract(quantity), 4));
            summary.setCostAmount(safeScale(summary.getCostAmount().subtract(reduceCost), 2));
            summary.setRealizedProfitAmount(safeScale(summary.getRealizedProfitAmount().add(proceeds.subtract(reduceCost)), 2));
            summary.setNetCashOutflow(safeScale(summary.getNetCashOutflow().subtract(proceeds), 2));
            if (summary.getHoldingQty().compareTo(BigDecimal.ZERO) == 0)
            {
                summary.setCostAmount(ZERO);
            }
            return;
        }

        if ("DIVIDEND".equals(type))
        {
            summary.setRealizedProfitAmount(safeScale(summary.getRealizedProfitAmount().add(amount), 2));
            summary.setNetCashOutflow(safeScale(summary.getNetCashOutflow().subtract(amount), 2));
        }
    }

    private BigDecimal safeScale(BigDecimal value, int scale)
    {
        return (value == null ? BigDecimal.ZERO : value).setScale(scale, RoundingMode.HALF_UP);
    }
}
