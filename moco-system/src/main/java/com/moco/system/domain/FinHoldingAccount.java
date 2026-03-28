package com.moco.system.domain;

import java.math.BigDecimal;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.moco.common.annotation.Excel;
import com.moco.common.annotation.Excel.ColumnType;
import com.moco.common.core.domain.BaseEntity;

public class FinHoldingAccount extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    @Excel(name = "账户ID", cellType = ColumnType.NUMERIC)
    private Long accountId;

    private Long familyId;

    @Excel(name = "家庭名称")
    private String familyName;

    private Long memberId;

    @Excel(name = "成员名称")
    private String memberName;

    @Excel(name = "账户名称")
    private String accountName;

    @Excel(name = "账户类型")
    private String accountType;

    @Excel(name = "所属平台")
    private String brokerName;

    @Excel(name = "现金余额")
    private BigDecimal cashBalance;

    @Excel(name = "排序")
    private Integer accountSort;

    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    private String status;

    private String delFlag;

    public Long getAccountId()
    {
        return accountId;
    }

    public void setAccountId(Long accountId)
    {
        this.accountId = accountId;
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

    @NotBlank(message = "账户名称不能为空")
    @Size(max = 100, message = "账户名称长度不能超过100个字符")
    public String getAccountName()
    {
        return accountName;
    }

    public void setAccountName(String accountName)
    {
        this.accountName = accountName;
    }

    @NotBlank(message = "账户类型不能为空")
    @Size(max = 30, message = "账户类型长度不能超过30个字符")
    public String getAccountType()
    {
        return accountType;
    }

    public void setAccountType(String accountType)
    {
        this.accountType = accountType;
    }

    @Size(max = 100, message = "所属平台长度不能超过100个字符")
    public String getBrokerName()
    {
        return brokerName;
    }

    public void setBrokerName(String brokerName)
    {
        this.brokerName = brokerName;
    }

    public BigDecimal getCashBalance()
    {
        return cashBalance;
    }

    public void setCashBalance(BigDecimal cashBalance)
    {
        this.cashBalance = cashBalance;
    }

    public Integer getAccountSort()
    {
        return accountSort;
    }

    public void setAccountSort(Integer accountSort)
    {
        this.accountSort = accountSort;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
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
            .append("accountId", getAccountId())
            .append("familyId", getFamilyId())
            .append("memberId", getMemberId())
            .append("accountName", getAccountName())
            .append("accountType", getAccountType())
            .append("brokerName", getBrokerName())
            .append("cashBalance", getCashBalance())
            .append("accountSort", getAccountSort())
            .append("status", getStatus())
            .append("delFlag", getDelFlag())
            .toString();
    }
}
