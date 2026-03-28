package com.moco.system.domain.vo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class FinOverviewVO
{
    private BigDecimal totalAssets;
    private BigDecimal cashBalance;
    private BigDecimal marketValue;
    private BigDecimal investedAmount;
    private BigDecimal totalCostAmount;
    private BigDecimal floatingProfitAmount;
    private BigDecimal realizedProfitAmount;
    private BigDecimal totalProfitAmount;
    private BigDecimal dailyProfitAmount;
    private List<FinOverviewMemberVO> memberList = new ArrayList<>();
    private List<FinOverviewAssetVO> assetList = new ArrayList<>();

    public BigDecimal getTotalAssets()
    {
        return totalAssets;
    }

    public void setTotalAssets(BigDecimal totalAssets)
    {
        this.totalAssets = totalAssets;
    }

    public BigDecimal getCashBalance()
    {
        return cashBalance;
    }

    public void setCashBalance(BigDecimal cashBalance)
    {
        this.cashBalance = cashBalance;
    }

    public BigDecimal getMarketValue()
    {
        return marketValue;
    }

    public void setMarketValue(BigDecimal marketValue)
    {
        this.marketValue = marketValue;
    }

    public BigDecimal getInvestedAmount()
    {
        return investedAmount;
    }

    public void setInvestedAmount(BigDecimal investedAmount)
    {
        this.investedAmount = investedAmount;
    }

    public BigDecimal getTotalCostAmount()
    {
        return totalCostAmount;
    }

    public void setTotalCostAmount(BigDecimal totalCostAmount)
    {
        this.totalCostAmount = totalCostAmount;
    }

    public BigDecimal getFloatingProfitAmount()
    {
        return floatingProfitAmount;
    }

    public void setFloatingProfitAmount(BigDecimal floatingProfitAmount)
    {
        this.floatingProfitAmount = floatingProfitAmount;
    }

    public BigDecimal getRealizedProfitAmount()
    {
        return realizedProfitAmount;
    }

    public void setRealizedProfitAmount(BigDecimal realizedProfitAmount)
    {
        this.realizedProfitAmount = realizedProfitAmount;
    }

    public BigDecimal getTotalProfitAmount()
    {
        return totalProfitAmount;
    }

    public void setTotalProfitAmount(BigDecimal totalProfitAmount)
    {
        this.totalProfitAmount = totalProfitAmount;
    }

    public BigDecimal getDailyProfitAmount()
    {
        return dailyProfitAmount;
    }

    public void setDailyProfitAmount(BigDecimal dailyProfitAmount)
    {
        this.dailyProfitAmount = dailyProfitAmount;
    }

    public List<FinOverviewMemberVO> getMemberList()
    {
        return memberList;
    }

    public void setMemberList(List<FinOverviewMemberVO> memberList)
    {
        this.memberList = memberList;
    }

    public List<FinOverviewAssetVO> getAssetList()
    {
        return assetList;
    }

    public void setAssetList(List<FinOverviewAssetVO> assetList)
    {
        this.assetList = assetList;
    }
}
