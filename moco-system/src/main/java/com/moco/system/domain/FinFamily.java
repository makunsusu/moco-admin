package com.moco.system.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.moco.common.annotation.Excel;
import com.moco.common.annotation.Excel.ColumnType;
import com.moco.common.core.domain.BaseEntity;

public class FinFamily extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    @Excel(name = "家庭ID", cellType = ColumnType.NUMERIC)
    private Long familyId;

    @Excel(name = "家庭编码")
    private String familyCode;

    @Excel(name = "家庭名称")
    private String familyName;

    @Excel(name = "家庭负责人")
    private String ownerName;

    @Excel(name = "排序")
    private Integer familySort;

    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    private String status;

    private String delFlag;

    public Long getFamilyId()
    {
        return familyId;
    }

    public void setFamilyId(Long familyId)
    {
        this.familyId = familyId;
    }

    @NotBlank(message = "家庭编码不能为空")
    @Size(max = 32, message = "家庭编码长度不能超过32个字符")
    public String getFamilyCode()
    {
        return familyCode;
    }

    public void setFamilyCode(String familyCode)
    {
        this.familyCode = familyCode;
    }

    @NotBlank(message = "家庭名称不能为空")
    @Size(max = 100, message = "家庭名称长度不能超过100个字符")
    public String getFamilyName()
    {
        return familyName;
    }

    public void setFamilyName(String familyName)
    {
        this.familyName = familyName;
    }

    @Size(max = 50, message = "家庭负责人长度不能超过50个字符")
    public String getOwnerName()
    {
        return ownerName;
    }

    public void setOwnerName(String ownerName)
    {
        this.ownerName = ownerName;
    }

    public Integer getFamilySort()
    {
        return familySort;
    }

    public void setFamilySort(Integer familySort)
    {
        this.familySort = familySort;
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
            .append("familyId", getFamilyId())
            .append("familyCode", getFamilyCode())
            .append("familyName", getFamilyName())
            .append("ownerName", getOwnerName())
            .append("familySort", getFamilySort())
            .append("status", getStatus())
            .append("delFlag", getDelFlag())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
