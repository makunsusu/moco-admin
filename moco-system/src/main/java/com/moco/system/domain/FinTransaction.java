package com.moco.system.domain;

import java.math.BigDecimal;
import java.util.Date;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.moco.common.annotation.Excel;
import com.moco.common.annotation.Excel.ColumnType;
import com.moco.common.core.domain.BaseEntity;

public class FinTransaction extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    @Excel(name = "流水ID", cellType = ColumnType.NUMERIC)
    private Long transactionId;

    private Long familyId;

    private String familyName;

    private Long memberId;

    private String memberName;

    private Long accountId;

    private String accountName;

    private Long assetId;

    private String assetName;

    @Excel(name = "交易类型")
    private String transactionType;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "交易时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date tradeDate;

    @Excel(name = "份额")
    private BigDecimal quantity;

    @Excel(name = "价格")
    private BigDecimal price;

    @Excel(name = "手续费")
    private BigDecimal fee;

    @Excel(name = "成交金额")
    private BigDecimal amount;

    private String delFlag;

    public Long getTransactionId()
    {
        return transactionId;
    }

    public void setTransactionId(Long transactionId)
    {
        this.transactionId = transactionId;
    }

    @NotNull(message = "所属家庭不能为空")
    public Long getFamilyId()
    {
        return familyId;
    }

    public void setFamilyId(Long familyId)
    {
        this.familyId = familyId;
    }

    public String getFamilyName()
    {
        return familyName;
    }

    public void setFamilyName(String familyName)
    {
        this.familyName = familyName;
    }

    @NotNull(message = "所属成员不能为空")
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

    @NotNull(message = "所属账户不能为空")
    public Long getAccountId()
    {
        return accountId;
    }

    public void setAccountId(Long accountId)
    {
        this.accountId = accountId;
    }

    public String getAccountName()
    {
        return accountName;
    }

    public void setAccountName(String accountName)
    {
        this.accountName = accountName;
    }

    @NotNull(message = "资产不能为空")
    public Long getAssetId()
    {
        return assetId;
    }

    public void setAssetId(Long assetId)
    {
        this.assetId = assetId;
    }

    public String getAssetName()
    {
        return assetName;
    }

    public void setAssetName(String assetName)
    {
        this.assetName = assetName;
    }

    @NotBlank(message = "交易类型不能为空")
    public String getTransactionType()
    {
        return transactionType;
    }

    public void setTransactionType(String transactionType)
    {
        this.transactionType = transactionType;
    }

    @NotNull(message = "交易时间不能为空")
    public Date getTradeDate()
    {
        return tradeDate;
    }

    public void setTradeDate(Date tradeDate)
    {
        this.tradeDate = tradeDate;
    }

    @NotNull(message = "交易份额不能为空")
    public BigDecimal getQuantity()
    {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity)
    {
        this.quantity = quantity;
    }

    public BigDecimal getPrice()
    {
        return price;
    }

    public void setPrice(BigDecimal price)
    {
        this.price = price;
    }

    public BigDecimal getFee()
    {
        return fee;
    }

    public void setFee(BigDecimal fee)
    {
        this.fee = fee;
    }

    public BigDecimal getAmount()
    {
        return amount;
    }

    public void setAmount(BigDecimal amount)
    {
        this.amount = amount;
    }

    public String getDelFlag()
    {
        return delFlag;
    }

    public void setDelFlag(String delFlag)
    {
        this.delFlag = delFlag;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("transactionId", getTransactionId())
            .append("familyId", getFamilyId())
            .append("memberId", getMemberId())
            .append("accountId", getAccountId())
            .append("assetId", getAssetId())
            .append("transactionType", getTransactionType())
            .append("tradeDate", getTradeDate())
            .append("quantity", getQuantity())
            .append("price", getPrice())
            .append("fee", getFee())
            .append("amount", getAmount())
            .append("remark", getRemark())
            .toString();
    }
}
