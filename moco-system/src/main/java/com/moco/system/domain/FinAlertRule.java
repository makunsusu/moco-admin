package com.moco.system.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.moco.common.core.domain.BaseEntity;

public class FinAlertRule extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long ruleId;
    private Long familyId;
    private String familyName;
    private Long memberId;
    private String memberName;
    private Long assetId;
    private String assetName;
    private String ruleName;
    private String ruleType;
    private BigDecimal thresholdValue;
    private BigDecimal secondThresholdValue;
    private String enabled;
    private String suggestionText;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastTriggeredTime;
    private String delFlag;

    public Long getRuleId()
    {
        return ruleId;
    }

    public void setRuleId(Long ruleId)
    {
        this.ruleId = ruleId;
    }

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

    @NotBlank(message = "规则名称不能为空")
    public String getRuleName()
    {
        return ruleName;
    }

    public void setRuleName(String ruleName)
    {
        this.ruleName = ruleName;
    }

    @NotBlank(message = "规则类型不能为空")
    public String getRuleType()
    {
        return ruleType;
    }

    public void setRuleType(String ruleType)
    {
        this.ruleType = ruleType;
    }

    public BigDecimal getThresholdValue()
    {
        return thresholdValue;
    }

    public void setThresholdValue(BigDecimal thresholdValue)
    {
        this.thresholdValue = thresholdValue;
    }

    public BigDecimal getSecondThresholdValue()
    {
        return secondThresholdValue;
    }

    public void setSecondThresholdValue(BigDecimal secondThresholdValue)
    {
        this.secondThresholdValue = secondThresholdValue;
    }

    public String getEnabled()
    {
        return enabled;
    }

    public void setEnabled(String enabled)
    {
        this.enabled = enabled;
    }

    public String getSuggestionText()
    {
        return suggestionText;
    }

    public void setSuggestionText(String suggestionText)
    {
        this.suggestionText = suggestionText;
    }

    public Date getLastTriggeredTime()
    {
        return lastTriggeredTime;
    }

    public void setLastTriggeredTime(Date lastTriggeredTime)
    {
        this.lastTriggeredTime = lastTriggeredTime;
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
            .append("ruleId", getRuleId())
            .append("ruleName", getRuleName())
            .append("ruleType", getRuleType())
            .append("thresholdValue", getThresholdValue())
            .append("secondThresholdValue", getSecondThresholdValue())
            .append("enabled", getEnabled())
            .toString();
    }
}
