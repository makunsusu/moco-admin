package com.moco.system.domain;

import java.util.Date;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.moco.common.annotation.Excel;
import com.moco.common.annotation.Excel.ColumnType;
import com.moco.common.core.domain.BaseEntity;

public class ShSyncLog extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    @Excel(name = "日志ID", cellType = ColumnType.NUMERIC)
    private Long logId;

    @Excel(name = "平台编码")
    private String platformCode;

    @Excel(name = "同步类型")
    private String syncType;

    @Excel(name = "触发方式")
    private String triggerMode;

    @Excel(name = "执行状态")
    private String syncStatus;

    @Excel(name = "成功数量")
    private Integer successCount;

    @Excel(name = "失败数量")
    private Integer failCount;

    @Excel(name = "错误摘要")
    private String errorMessage;

    private String detailJson;

    private Date startTime;

    private Date endTime;

    public Long getLogId()
    {
        return logId;
    }

    public void setLogId(Long logId)
    {
        this.logId = logId;
    }

    public String getPlatformCode()
    {
        return platformCode;
    }

    public void setPlatformCode(String platformCode)
    {
        this.platformCode = platformCode;
    }

    public String getSyncType()
    {
        return syncType;
    }

    public void setSyncType(String syncType)
    {
        this.syncType = syncType;
    }

    public String getTriggerMode()
    {
        return triggerMode;
    }

    public void setTriggerMode(String triggerMode)
    {
        this.triggerMode = triggerMode;
    }

    public String getSyncStatus()
    {
        return syncStatus;
    }

    public void setSyncStatus(String syncStatus)
    {
        this.syncStatus = syncStatus;
    }

    public Integer getSuccessCount()
    {
        return successCount;
    }

    public void setSuccessCount(Integer successCount)
    {
        this.successCount = successCount;
    }

    public Integer getFailCount()
    {
        return failCount;
    }

    public void setFailCount(Integer failCount)
    {
        this.failCount = failCount;
    }

    public String getErrorMessage()
    {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage)
    {
        this.errorMessage = errorMessage;
    }

    public String getDetailJson()
    {
        return detailJson;
    }

    public void setDetailJson(String detailJson)
    {
        this.detailJson = detailJson;
    }

    public Date getStartTime()
    {
        return startTime;
    }

    public void setStartTime(Date startTime)
    {
        this.startTime = startTime;
    }

    public Date getEndTime()
    {
        return endTime;
    }

    public void setEndTime(Date endTime)
    {
        this.endTime = endTime;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("logId", getLogId())
            .append("platformCode", getPlatformCode())
            .append("syncType", getSyncType())
            .append("triggerMode", getTriggerMode())
            .append("syncStatus", getSyncStatus())
            .append("successCount", getSuccessCount())
            .append("failCount", getFailCount())
            .append("errorMessage", getErrorMessage())
            .append("startTime", getStartTime())
            .append("endTime", getEndTime())
            .toString();
    }
}
