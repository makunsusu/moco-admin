package com.moco.system.domain;

import java.util.Date;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.moco.common.core.domain.BaseEntity;

public class ShDeviceProperty extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long propertyId;

    private Long deviceId;

    private String propertyKey;

    private String propertyValue;

    private String propertyType;

    private Date snapshotTime;

    public Long getPropertyId()
    {
        return propertyId;
    }

    public void setPropertyId(Long propertyId)
    {
        this.propertyId = propertyId;
    }

    public Long getDeviceId()
    {
        return deviceId;
    }

    public void setDeviceId(Long deviceId)
    {
        this.deviceId = deviceId;
    }

    public String getPropertyKey()
    {
        return propertyKey;
    }

    public void setPropertyKey(String propertyKey)
    {
        this.propertyKey = propertyKey;
    }

    public String getPropertyValue()
    {
        return propertyValue;
    }

    public void setPropertyValue(String propertyValue)
    {
        this.propertyValue = propertyValue;
    }

    public String getPropertyType()
    {
        return propertyType;
    }

    public void setPropertyType(String propertyType)
    {
        this.propertyType = propertyType;
    }

    public Date getSnapshotTime()
    {
        return snapshotTime;
    }

    public void setSnapshotTime(Date snapshotTime)
    {
        this.snapshotTime = snapshotTime;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("propertyId", getPropertyId())
            .append("deviceId", getDeviceId())
            .append("propertyKey", getPropertyKey())
            .append("propertyValue", getPropertyValue())
            .append("propertyType", getPropertyType())
            .append("snapshotTime", getSnapshotTime())
            .toString();
    }
}
