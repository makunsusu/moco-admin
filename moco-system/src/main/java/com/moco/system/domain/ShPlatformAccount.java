package com.moco.system.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.moco.common.annotation.Excel;
import com.moco.common.annotation.Excel.ColumnType;
import com.moco.common.core.domain.BaseEntity;

public class ShPlatformAccount extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    @Excel(name = "平台账号ID", cellType = ColumnType.NUMERIC)
    private Long accountId;

    @Excel(name = "平台编码")
    private String platformCode;

    @Excel(name = "平台名称")
    private String platformName;

    @Excel(name = "账号")
    private String username;

    private String authMode;

    @JsonIgnore
    private String encryptedPassword;

    private String platformPassword;

    private Boolean passwordConfigured;

    private String mijiaUserId;

    @JsonIgnore
    private String encryptedSsecurity;

    private String mijiaSsecurity;

    @JsonIgnore
    private String encryptedServiceToken;

    private String mijiaServiceToken;

    private Boolean tokenConfigured;

    @JsonIgnore
    private String encryptedAccessToken;

    @JsonIgnore
    private String encryptedRefreshToken;

    private Long oauthExpiresTs;

    private Boolean oauthAuthorized;

    private String oauthClientId;

    private String oauthRedirectUri;

    @Excel(name = "地区")
    private String region;

    @Excel(name = "同步开关", readConverterExp = "1=启用,0=停用")
    private String syncEnabled;

    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    private String status;

    private java.util.Date lastSyncTime;

    private String lastSyncStatus;

    private String lastSyncMessage;

    public Long getAccountId()
    {
        return accountId;
    }

    public void setAccountId(Long accountId)
    {
        this.accountId = accountId;
    }

    public String getPlatformCode()
    {
        return platformCode;
    }

    public void setPlatformCode(String platformCode)
    {
        this.platformCode = platformCode;
    }

    public String getPlatformName()
    {
        return platformName;
    }

    public void setPlatformName(String platformName)
    {
        this.platformName = platformName;
    }

    @Size(max = 100, message = "米家账号长度不能超过100个字符")
    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getAuthMode()
    {
        return authMode;
    }

    public void setAuthMode(String authMode)
    {
        this.authMode = authMode;
    }

    public String getEncryptedPassword()
    {
        return encryptedPassword;
    }

    public void setEncryptedPassword(String encryptedPassword)
    {
        this.encryptedPassword = encryptedPassword;
    }

    @Size(max = 100, message = "密码长度不能超过100个字符")
    public String getPlatformPassword()
    {
        return platformPassword;
    }

    public void setPlatformPassword(String platformPassword)
    {
        this.platformPassword = platformPassword;
    }

    public Boolean getPasswordConfigured()
    {
        return passwordConfigured;
    }

    public void setPasswordConfigured(Boolean passwordConfigured)
    {
        this.passwordConfigured = passwordConfigured;
    }

    @Size(max = 100, message = "米家 userId 长度不能超过100个字符")
    public String getMijiaUserId()
    {
        return mijiaUserId;
    }

    public void setMijiaUserId(String mijiaUserId)
    {
        this.mijiaUserId = mijiaUserId;
    }

    public String getEncryptedSsecurity()
    {
        return encryptedSsecurity;
    }

    public void setEncryptedSsecurity(String encryptedSsecurity)
    {
        this.encryptedSsecurity = encryptedSsecurity;
    }

    @Size(max = 500, message = "ssecurity 长度不能超过500个字符")
    public String getMijiaSsecurity()
    {
        return mijiaSsecurity;
    }

    public void setMijiaSsecurity(String mijiaSsecurity)
    {
        this.mijiaSsecurity = mijiaSsecurity;
    }

    public String getEncryptedServiceToken()
    {
        return encryptedServiceToken;
    }

    public void setEncryptedServiceToken(String encryptedServiceToken)
    {
        this.encryptedServiceToken = encryptedServiceToken;
    }

    @Size(max = 1000, message = "serviceToken 长度不能超过1000个字符")
    public String getMijiaServiceToken()
    {
        return mijiaServiceToken;
    }

    public void setMijiaServiceToken(String mijiaServiceToken)
    {
        this.mijiaServiceToken = mijiaServiceToken;
    }

    public Boolean getTokenConfigured()
    {
        return tokenConfigured;
    }

    public void setTokenConfigured(Boolean tokenConfigured)
    {
        this.tokenConfigured = tokenConfigured;
    }

    public String getEncryptedAccessToken()
    {
        return encryptedAccessToken;
    }

    public void setEncryptedAccessToken(String encryptedAccessToken)
    {
        this.encryptedAccessToken = encryptedAccessToken;
    }

    public String getEncryptedRefreshToken()
    {
        return encryptedRefreshToken;
    }

    public void setEncryptedRefreshToken(String encryptedRefreshToken)
    {
        this.encryptedRefreshToken = encryptedRefreshToken;
    }

    public Long getOauthExpiresTs()
    {
        return oauthExpiresTs;
    }

    public void setOauthExpiresTs(Long oauthExpiresTs)
    {
        this.oauthExpiresTs = oauthExpiresTs;
    }

    public Boolean getOauthAuthorized()
    {
        return oauthAuthorized;
    }

    public void setOauthAuthorized(Boolean oauthAuthorized)
    {
        this.oauthAuthorized = oauthAuthorized;
    }

    @Size(max = 100, message = "OAuth Client ID 长度不能超过100个字符")
    public String getOauthClientId()
    {
        return oauthClientId;
    }

    public void setOauthClientId(String oauthClientId)
    {
        this.oauthClientId = oauthClientId;
    }

    @Size(max = 500, message = "OAuth 回调地址长度不能超过500个字符")
    public String getOauthRedirectUri()
    {
        return oauthRedirectUri;
    }

    public void setOauthRedirectUri(String oauthRedirectUri)
    {
        this.oauthRedirectUri = oauthRedirectUri;
    }

    @NotBlank(message = "地区不能为空")
    @Size(max = 20, message = "地区长度不能超过20个字符")
    public String getRegion()
    {
        return region;
    }

    public void setRegion(String region)
    {
        this.region = region;
    }

    public String getSyncEnabled()
    {
        return syncEnabled;
    }

    public void setSyncEnabled(String syncEnabled)
    {
        this.syncEnabled = syncEnabled;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public java.util.Date getLastSyncTime()
    {
        return lastSyncTime;
    }

    public void setLastSyncTime(java.util.Date lastSyncTime)
    {
        this.lastSyncTime = lastSyncTime;
    }

    public String getLastSyncStatus()
    {
        return lastSyncStatus;
    }

    public void setLastSyncStatus(String lastSyncStatus)
    {
        this.lastSyncStatus = lastSyncStatus;
    }

    public String getLastSyncMessage()
    {
        return lastSyncMessage;
    }

    public void setLastSyncMessage(String lastSyncMessage)
    {
        this.lastSyncMessage = lastSyncMessage;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("accountId", getAccountId())
            .append("platformCode", getPlatformCode())
            .append("platformName", getPlatformName())
            .append("username", getUsername())
            .append("authMode", getAuthMode())
            .append("mijiaUserId", getMijiaUserId())
            .append("oauthClientId", getOauthClientId())
            .append("oauthRedirectUri", getOauthRedirectUri())
            .append("region", getRegion())
            .append("syncEnabled", getSyncEnabled())
            .append("status", getStatus())
            .append("lastSyncTime", getLastSyncTime())
            .append("lastSyncStatus", getLastSyncStatus())
            .append("lastSyncMessage", getLastSyncMessage())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
