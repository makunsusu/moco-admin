package com.moco.system.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.moco.common.core.domain.BaseEntity;

public class FinAlertEvent extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long eventId;
    private Long ruleId;
    private String ruleName;
    private Long familyId;
    private String familyName;
    private Long memberId;
    private String memberName;
    private Long assetId;
    private String assetName;
    private String eventTitle;
    private String ruleType;
    private BigDecimal triggerValue;
    private BigDecimal thresholdValue;
    private BigDecimal secondThresholdValue;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date triggerTime;
    private String status;
    private String suggestionText;
    private String detailText;

    public Long getEventId()
    {
        return eventId;
    }

    public void setEventId(Long eventId)
    {
        this.eventId = eventId;
    }

    public Long getRuleId()
    {
        return ruleId;
    }

    public void setRuleId(Long ruleId)
    {
        this.ruleId = ruleId;
    }

    public String getRuleName()
    {
        return ruleName;
    }

    public void setRuleName(String ruleName)
    {
        this.ruleName = ruleName;
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

    public String getEventTitle()
    {
        return eventTitle;
    }

    public void setEventTitle(String eventTitle)
    {
        this.eventTitle = eventTitle;
    }

    public String getRuleType()
    {
        return ruleType;
    }

    public void setRuleType(String ruleType)
    {
        this.ruleType = ruleType;
    }

    public BigDecimal getTriggerValue()
    {
        return triggerValue;
    }

    public void setTriggerValue(BigDecimal triggerValue)
    {
        this.triggerValue = triggerValue;
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

    public Date getTriggerTime()
    {
        return triggerTime;
    }

    public void setTriggerTime(Date triggerTime)
    {
        this.triggerTime = triggerTime;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getSuggestionText()
    {
        return suggestionText;
    }

    public void setSuggestionText(String suggestionText)
    {
        this.suggestionText = suggestionText;
    }

    public String getDetailText()
    {
        return detailText;
    }

    public void setDetailText(String detailText)
    {
        this.detailText = detailText;
    }
}
