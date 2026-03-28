package com.moco.system.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.moco.common.annotation.Excel;
import com.moco.common.annotation.Excel.ColumnType;
import com.moco.common.core.domain.BaseEntity;

public class FinMemberAccount extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    @Excel(name = "成员ID", cellType = ColumnType.NUMERIC)
    private Long memberId;

    @Excel(name = "家庭ID", cellType = ColumnType.NUMERIC)
    private Long familyId;

    @Excel(name = "家庭名称")
    private String familyName;

    @Excel(name = "成员名称")
    private String memberName;

    @Excel(name = "成员角色")
    private String memberRole;

    @Excel(name = "风险偏好")
    private String riskLevel;

    @Excel(name = "手机号")
    private String contactPhone;

    @Excel(name = "排序")
    private Integer memberSort;

    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    private String status;

    private String delFlag;

    public Long getMemberId()
    {
        return memberId;
    }

    public void setMemberId(Long memberId)
    {
        this.memberId = memberId;
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

    @NotBlank(message = "成员名称不能为空")
    @Size(max = 50, message = "成员名称长度不能超过50个字符")
    public String getMemberName()
    {
        return memberName;
    }

    public void setMemberName(String memberName)
    {
        this.memberName = memberName;
    }

    @Size(max = 30, message = "成员角色长度不能超过30个字符")
    public String getMemberRole()
    {
        return memberRole;
    }

    public void setMemberRole(String memberRole)
    {
        this.memberRole = memberRole;
    }

    @Size(max = 30, message = "风险偏好长度不能超过30个字符")
    public String getRiskLevel()
    {
        return riskLevel;
    }

    public void setRiskLevel(String riskLevel)
    {
        this.riskLevel = riskLevel;
    }

    @Size(max = 20, message = "手机号长度不能超过20个字符")
    public String getContactPhone()
    {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone)
    {
        this.contactPhone = contactPhone;
    }

    public Integer getMemberSort()
    {
        return memberSort;
    }

    public void setMemberSort(Integer memberSort)
    {
        this.memberSort = memberSort;
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
            .append("memberId", getMemberId())
            .append("familyId", getFamilyId())
            .append("memberName", getMemberName())
            .append("memberRole", getMemberRole())
            .append("riskLevel", getRiskLevel())
            .append("contactPhone", getContactPhone())
            .append("memberSort", getMemberSort())
            .append("status", getStatus())
            .append("delFlag", getDelFlag())
            .append("remark", getRemark())
            .toString();
    }
}
