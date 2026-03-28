package com.moco.system.domain.vo;

import java.math.BigDecimal;

public class FinOverviewMemberVO
{
    private Long memberId;
    private String memberName;
    private BigDecimal cashBalance;
    private BigDecimal marketValue;
    private BigDecimal totalAssets;
    private BigDecimal profitAmount;

    public Long getMemberId()
    {
        return memberId;
    }

    public void setMemberId(Long memberId)
    {
        this.memberId = memberId;
    }

    public String getMemberName()
    {
        return memberName;
    }

    public void setMemberName(String memberName)
    {
        this.memberName = memberName;
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

    public BigDecimal getTotalAssets()
    {
        return totalAssets;
    }

    public void setTotalAssets(BigDecimal totalAssets)
    {
        this.totalAssets = totalAssets;
    }

    public BigDecimal getProfitAmount()
    {
        return profitAmount;
    }

    public void setProfitAmount(BigDecimal profitAmount)
    {
        this.profitAmount = profitAmount;
    }
}
