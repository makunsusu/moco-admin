package com.moco.system.service.impl;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.alibaba.fastjson2.JSON;
import com.moco.common.utils.StringUtils;
import com.moco.system.domain.ShPlatformAccount;
import com.moco.system.domain.ShSyncLog;
import com.moco.system.mapper.ShPlatformAccountMapper;
import com.moco.system.mapper.ShSyncLogMapper;
import com.moco.system.service.IShPlatformAccountService;
import com.moco.system.service.smarthome.MijiaClient;
import com.moco.system.service.smarthome.MijiaClientException;
import com.moco.system.service.smarthome.MijiaCloudClient;
import com.moco.system.service.smarthome.MijiaCryptoUtils;
import com.moco.system.service.smarthome.MijiaDeviceRecord;

@Service
public class ShPlatformAccountServiceImpl implements IShPlatformAccountService
{
    private static final String LOCAL_PYTHON = "python3.10";

    private static final String LOCAL_QR_START_SCRIPT = Paths.get("tools", "mijia-local-bridge", "start_qr_login.py").toAbsolutePath().toString();

    private static final String LOCAL_QR_POLL_SCRIPT = Paths.get("tools", "mijia-local-bridge", "poll_qr_login.py").toAbsolutePath().toString();

    private static final String OAUTH_AUTH_URL = "https://account.xiaomi.com/oauth2/authorize";

    private static final Map<String, PendingOauthContext> PENDING_OAUTH = new ConcurrentHashMap<>();

    @Autowired
    private ShPlatformAccountMapper accountMapper;

    @Autowired
    private ShSyncLogMapper syncLogMapper;

    @Autowired
    private ShSyncSupportService syncSupportService;

    @Autowired
    private MijiaClient mijiaClient;

    @Override
    public ShPlatformAccount getPlatformAccount()
    {
        ShPlatformAccount account = accountMapper.selectPlatformAccount();
        if (account == null)
        {
            account = new ShPlatformAccount();
            account.setPlatformCode("MIJIA");
            account.setPlatformName("米家");
            account.setAuthMode("MIJIA_API");
            account.setRegion("cn");
            account.setSyncEnabled("0");
            account.setStatus("0");
            account.setPasswordConfigured(Boolean.FALSE);
            account.setTokenConfigured(Boolean.FALSE);
            account.setOauthAuthorized(Boolean.FALSE);
            return account;
        }
        account.setAuthMode(StringUtils.defaultIfBlank(account.getAuthMode(), "MIJIA_API"));
        account.setPlatformPassword(null);
        account.setMijiaSsecurity(null);
        account.setMijiaServiceToken(null);
        account.setPasswordConfigured(StringUtils.isNotBlank(account.getEncryptedPassword()));
        account.setTokenConfigured(StringUtils.isNotBlank(account.getMijiaUserId())
            && StringUtils.isNotBlank(account.getEncryptedSsecurity())
            && StringUtils.isNotBlank(account.getEncryptedServiceToken()));
        account.setOauthAuthorized(StringUtils.isNotBlank(account.getEncryptedAccessToken())
            && StringUtils.isNotBlank(account.getEncryptedRefreshToken())
            && account.getOauthExpiresTs() != null);
        return account;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int savePlatformAccount(ShPlatformAccount account, String operator)
    {
        ShPlatformAccount exists = accountMapper.selectPlatformAccount();
        account.setAuthMode(StringUtils.defaultIfBlank(account.getAuthMode(), "MIJIA_API"));
        validateAccountConfig(account, exists);
        boolean oauthAppChanged = oauthAppChanged(account, exists);
        if (exists != null)
        {
            account.setAccountId(exists.getAccountId());
            account.setPlatformCode("MIJIA");
            account.setPlatformName("米家");
            fillSecretFields(account, exists);
            if (oauthAppChanged)
            {
                resetOauthAuthorization(account);
                account.setLastSyncMessage("OAuth 应用配置已更新，请重新完成授权");
                account.setLastSyncStatus("PENDING");
            }
            account.setUpdateBy(operator);
            return accountMapper.updatePlatformAccount(account);
        }
        account.setPlatformCode("MIJIA");
        account.setPlatformName("米家");
        fillSecretFields(account, null);
        if ("OAUTH".equals(account.getAuthMode()))
        {
            resetOauthAuthorization(account);
        }
        account.setCreateBy(operator);
        return accountMapper.insertPlatformAccount(account);
    }

    @Override
    public Map<String, Object> startOfficialOauth(String operator, String baseUrl)
    {
        ShPlatformAccount current = getPlatformAccount();
        String authMode = "OAUTH";
        String clientId = StringUtils.trimToEmpty(current.getOauthClientId());
        String redirectUri = StringUtils.trimToEmpty(current.getOauthRedirectUri());
        if (StringUtils.isBlank(clientId))
        {
            throw new MijiaClientException("请先填写你自己的 Xiaomi OAuth Client ID");
        }
        if (StringUtils.isBlank(redirectUri))
        {
            throw new MijiaClientException("请先填写已在 Xiaomi OAuth 应用中登记的 Redirect URI");
        }
        String deviceId = "moco." + UUID.randomUUID().toString().replace("-", "");
        String state = UUID.randomUUID().toString().replace("-", "");
        PendingOauthContext context = new PendingOauthContext();
        context.setOperator(operator);
        context.setRegion(StringUtils.defaultIfBlank(current.getRegion(), "cn"));
        context.setClientId(clientId);
        context.setRedirectUri(redirectUri);
        context.setDeviceId(deviceId);
        PENDING_OAUTH.put(state, context);

        Map<String, String> params = new LinkedHashMap<>();
        params.put("redirect_uri", redirectUri);
        params.put("client_id", clientId);
        params.put("response_type", "code");
        params.put("device_id", deviceId);
        params.put("state", state);
        params.put("skip_confirm", "false");

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("authMode", authMode);
        result.put("state", state);
        result.put("authUrl", OAUTH_AUTH_URL + "?" + formEncode(params));
        result.put("redirectUri", redirectUri);
        result.put("clientId", clientId);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String handleOfficialOauthCallback(String code, String state)
    {
        PendingOauthContext context = PENDING_OAUTH.remove(state);
        if (context == null || StringUtils.isBlank(code))
        {
            return buildOauthCallbackHtml(false, "授权回调无效或已过期，请返回平台接入页重新发起 OAuth 应用授权。");
        }
        ShPlatformAccount current = accountMapper.selectPlatformAccount();
        if (current == null)
        {
            current = new ShPlatformAccount();
            current.setPlatformCode("MIJIA");
            current.setPlatformName("米家");
            current.setStatus("0");
            current.setSyncEnabled("0");
        }
        current.setAuthMode("OAUTH");
        current.setPlatformCode("MIJIA");
        current.setPlatformName("米家");
        current.setRegion(StringUtils.defaultIfBlank(current.getRegion(), context.getRegion()));
        current.setOauthClientId(context.getClientId());
        current.setOauthRedirectUri(context.getRedirectUri());
        current.setUpdateBy(context.getOperator());
        try
        {
            Map<String, Object> authInfo = MijiaCloudClient.exchangeOfficialOauthCode(
                context.getClientId(), context.getRedirectUri(), context.getRegion(), context.getDeviceId(), code);
            current.setEncryptedAccessToken(MijiaCryptoUtils.encrypt(String.valueOf(authInfo.get("accessToken"))));
            current.setEncryptedRefreshToken(MijiaCryptoUtils.encrypt(String.valueOf(authInfo.get("refreshToken"))));
            current.setOauthExpiresTs(((Number) authInfo.get("expiresTs")).longValue());
            current.setOauthClientId(String.valueOf(authInfo.get("clientId")));
            current.setOauthRedirectUri(String.valueOf(authInfo.get("redirectUri")));
            current.setLastSyncStatus("SUCCESS");
            current.setLastSyncMessage("OAuth 应用授权成功");
            current.setLastSyncTime(new Date());
            if (current.getAccountId() == null)
            {
                current.setCreateBy(context.getOperator());
                accountMapper.insertPlatformAccount(current);
            }
            else
            {
                accountMapper.updatePlatformAccount(current);
            }
            return buildOauthCallbackHtml(true, "Xiaomi OAuth 授权成功，已写入平台接入配置。你可以关闭当前窗口并回到系统继续测试连接。");
        }
        catch (Exception e)
        {
            return buildOauthCallbackHtml(false, "Xiaomi OAuth 授权失败：" + StringUtils.substring(e.getMessage(), 0, 300));
        }
    }

    @Override
    public Map<String, Object> startLocalQrLogin()
    {
        return runLocalQrScript(LOCAL_QR_START_SCRIPT);
    }

    @Override
    public Map<String, Object> checkLocalQrLogin(String sessionId)
    {
        if (StringUtils.isBlank(sessionId))
        {
            throw new MijiaClientException("二维码会话标识不能为空");
        }
        Map<String, Object> result = runLocalQrScript(LOCAL_QR_POLL_SCRIPT, sessionId.trim());
        if ("success".equals(String.valueOf(result.get("status"))))
        {
            ShPlatformAccount account = accountMapper.selectPlatformAccount();
            if (account != null && account.getAccountId() != null)
            {
                updateAccountSyncStatus(account, "SUCCESS", StringUtils.defaultIfBlank((String) result.get("message"), "本机扫码登录成功"), "qr-login");
            }
        }
        return result;
    }

    @Override
    public Map<String, Object> testConnection()
    {
        ShPlatformAccount account = accountMapper.selectPlatformAccount();
        if (account == null)
        {
            throw new MijiaClientException("请先配置米家平台账号");
        }
        account.setAuthMode(StringUtils.defaultIfBlank(account.getAuthMode(), "MIJIA_API"));
        if ("TOKEN".equals(account.getAuthMode()) && !isTokenReady(account))
        {
            return buildTokenDiagnosis(account);
        }
        if ("MIJIA_API".equals(account.getAuthMode()))
        {
            try
            {
                account = requireConfiguredAccount();
                mijiaClient.testConnection(account);
                updateAccountSyncStatus(account, "SUCCESS", "本机扫码登录可用", "test");
                Map<String, Object> result = new LinkedHashMap<>();
                result.put("region", account.getRegion());
                result.put("message", "本机扫码登录可用");
                result.put("ready", Boolean.TRUE);
                result.put("recommendation", "如果后续登录态失效，请回到平台接入页重新生成二维码并扫码登录。");
                return result;
            }
            catch (MijiaClientException e)
            {
                if (account != null && account.getAccountId() != null)
                {
                    updateAccountSyncStatus(account, "FAIL", e.getMessage(), "test");
                }
                return buildAuthFailureDiagnosis(account, e.getMessage());
            }
        }
        try
        {
            account = requireConfiguredAccount();
            mijiaClient.testConnection(account);
            persistRuntimeSecrets(account);
            updateAccountSyncStatus(account, "SUCCESS", "连接成功", "test");
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("region", account.getRegion());
            result.put("message", "连接成功");
            result.put("ready", Boolean.TRUE);
            return result;
        }
        catch (MijiaClientException e)
        {
            if (account != null && account.getAccountId() != null)
            {
                updateAccountSyncStatus(account, "FAIL", e.getMessage(), "test");
            }
            return buildAuthFailureDiagnosis(account, e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> syncFull(String operator)
    {
        ShPlatformAccount account = requireConfiguredAccount();
        ShSyncLog log = buildLog("FULL_SYNC", "MANUAL");
        try
        {
            List<MijiaDeviceRecord> records = mijiaClient.fetchDevices(account);
            persistRuntimeSecrets(account);
            Map<String, Integer> counts = syncSupportService.replaceSnapshot(account.getRegion(), records, true);
            log.setSyncStatus("SUCCESS");
            log.setSuccessCount(counts.get("deviceCount"));
            log.setFailCount(0);
            log.setDetailJson(JSON.toJSONString(counts));
            updateAccountSyncStatus(account, "SUCCESS", "同步完成", operator);
            return new LinkedHashMap<>(counts);
        }
        catch (MijiaClientException e)
        {
            log.setSyncStatus("FAIL");
            log.setSuccessCount(0);
            log.setFailCount(1);
            log.setErrorMessage(e.getMessage());
            updateAccountSyncStatus(account, "FAIL", e.getMessage(), operator);
            throw e;
        }
        finally
        {
            log.setEndTime(new Date());
            syncLogMapper.insertSyncLog(log);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Map<String, Object> syncDeviceStatus(String triggerMode)
    {
        boolean scheduledTrigger = "SCHEDULED".equalsIgnoreCase(triggerMode);
        ShPlatformAccount account = scheduledTrigger ? requireScheduledAccount() : requireConfiguredAccount();
        ShSyncLog log = buildLog("STATUS_SYNC", triggerMode);
        try
        {
            List<MijiaDeviceRecord> records = mijiaClient.fetchDeviceStates(account);
            persistRuntimeSecrets(account);
            String operator = scheduledTrigger ? "task" : "manual";
            Map<String, Integer> counts = syncSupportService.refreshRuntimeState(records, operator);
            log.setSyncStatus("SUCCESS");
            log.setSuccessCount(counts.get("matchedCount"));
            log.setFailCount(0);
            log.setDetailJson(JSON.toJSONString(counts));
            updateAccountSyncStatus(account, "SUCCESS", "状态刷新完成，变更设备 " + counts.get("changedCount") + " 台", operator);
            return new LinkedHashMap<>(counts);
        }
        catch (MijiaClientException e)
        {
            log.setSyncStatus("FAIL");
            log.setSuccessCount(0);
            log.setFailCount(1);
            log.setErrorMessage(e.getMessage());
            updateAccountSyncStatus(account, "FAIL", e.getMessage(), scheduledTrigger ? "task" : "manual");
            throw e;
        }
        finally
        {
            log.setEndTime(new Date());
            syncLogMapper.insertSyncLog(log);
        }
    }

    private ShPlatformAccount requireConfiguredAccount()
    {
        ShPlatformAccount account = accountMapper.selectPlatformAccount();
        if (account == null)
        {
            throw new MijiaClientException("请先配置米家平台账号");
        }
        account.setAuthMode(StringUtils.defaultIfBlank(account.getAuthMode(), "MIJIA_API"));
        if (!"0".equals(account.getStatus()))
        {
            throw new MijiaClientException("米家平台接入已停用");
        }
        if ("TOKEN".equals(account.getAuthMode()))
        {
            if (StringUtils.isBlank(account.getMijiaUserId())
                || StringUtils.isBlank(account.getEncryptedSsecurity())
                || StringUtils.isBlank(account.getEncryptedServiceToken()))
            {
                throw new MijiaClientException("请先补充米家会话信息或 Token");
            }
            return account;
        }
        if ("MIJIA_API".equals(account.getAuthMode()))
        {
            return account;
        }
        if ("OAUTH".equals(account.getAuthMode()))
        {
            if (StringUtils.isBlank(account.getOauthClientId()) || StringUtils.isBlank(account.getOauthRedirectUri()))
            {
                throw new MijiaClientException("请先填写 OAuth Client ID 和 Redirect URI");
            }
            if (StringUtils.isBlank(account.getEncryptedAccessToken()) || StringUtils.isBlank(account.getEncryptedRefreshToken()))
            {
                throw new MijiaClientException("请先完成 Xiaomi OAuth 应用授权");
            }
            return account;
        }
        if (StringUtils.isBlank(account.getUsername()) || StringUtils.isBlank(account.getEncryptedPassword()))
        {
            throw new MijiaClientException("请先补充米家账号密码");
        }
        return account;
    }

    private Map<String, Object> runLocalQrScript(String scriptPath, String... args)
    {
        try
        {
            List<String> command = new java.util.ArrayList<>();
            command.add(LOCAL_PYTHON);
            command.add(scriptPath);
            if (args != null)
            {
                for (String arg : args)
                {
                    if (StringUtils.isNotBlank(arg))
                    {
                        command.add(arg);
                    }
                }
            }
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();
            String output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            int exitCode = process.waitFor();
            Map<String, Object> result = JSON.parseObject(output);
            if (result == null)
            {
                throw new MijiaClientException("二维码登录桥接返回内容无法解析");
            }
            if (exitCode != 0 && !"pending".equals(String.valueOf(result.get("status"))))
            {
                throw new MijiaClientException(StringUtils.defaultIfBlank((String) result.get("message"),
                    "二维码登录桥接执行失败，退出码=" + exitCode));
            }
            return result;
        }
        catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
            throw new MijiaClientException("二维码登录桥接执行被中断", e);
        }
        catch (Exception e)
        {
            if (e instanceof MijiaClientException)
            {
                throw (MijiaClientException) e;
            }
            throw new MijiaClientException("二维码登录桥接执行失败", e);
        }
    }

    private ShPlatformAccount requireScheduledAccount()
    {
        ShPlatformAccount account = requireConfiguredAccount();
        if (!"1".equals(account.getSyncEnabled()))
        {
            throw new MijiaClientException("米家同步开关未启用");
        }
        return account;
    }

    private void updateAccountSyncStatus(ShPlatformAccount account, String status, String message, String operator)
    {
        account.setLastSyncStatus(status);
        account.setLastSyncMessage(StringUtils.substring(message, 0, 500));
        account.setLastSyncTime(new Date());
        account.setUpdateBy(operator);
        accountMapper.updatePlatformAccount(account);
    }

    private ShSyncLog buildLog(String syncType, String triggerMode)
    {
        ShSyncLog log = new ShSyncLog();
        log.setPlatformCode("MIJIA");
        log.setSyncType(syncType);
        log.setTriggerMode(triggerMode);
        log.setStartTime(new Date());
        log.setEndTime(new Date());
        return log;
    }

    private void fillSecretFields(ShPlatformAccount target, ShPlatformAccount exists)
    {
        if (StringUtils.isBlank(target.getPlatformPassword()))
        {
            target.setEncryptedPassword(exists == null ? null : exists.getEncryptedPassword());
        }
        else
        {
            target.setEncryptedPassword(MijiaCryptoUtils.encrypt(target.getPlatformPassword()));
        }
        if (StringUtils.isBlank(target.getMijiaUserId()) && exists != null)
        {
            target.setMijiaUserId(exists.getMijiaUserId());
        }
        if (StringUtils.isBlank(target.getMijiaSsecurity()))
        {
            target.setEncryptedSsecurity(exists == null ? null : exists.getEncryptedSsecurity());
        }
        else
        {
            target.setEncryptedSsecurity(MijiaCryptoUtils.encrypt(target.getMijiaSsecurity()));
        }
        if (StringUtils.isBlank(target.getMijiaServiceToken()))
        {
            target.setEncryptedServiceToken(exists == null ? null : exists.getEncryptedServiceToken());
        }
        else
        {
            target.setEncryptedServiceToken(MijiaCryptoUtils.encrypt(target.getMijiaServiceToken()));
        }
        if (StringUtils.isBlank(target.getEncryptedAccessToken()) && exists != null)
        {
            target.setEncryptedAccessToken(exists.getEncryptedAccessToken());
        }
        if (StringUtils.isBlank(target.getEncryptedRefreshToken()) && exists != null)
        {
            target.setEncryptedRefreshToken(exists.getEncryptedRefreshToken());
        }
        if (target.getOauthExpiresTs() == null && exists != null)
        {
            target.setOauthExpiresTs(exists.getOauthExpiresTs());
        }
        if (StringUtils.isBlank(target.getOauthClientId()) && exists != null)
        {
            target.setOauthClientId(exists.getOauthClientId());
        }
        if (StringUtils.isBlank(target.getOauthRedirectUri()) && exists != null)
        {
            target.setOauthRedirectUri(exists.getOauthRedirectUri());
        }
    }

    private void validateAccountConfig(ShPlatformAccount account, ShPlatformAccount exists)
    {
        if ("OAUTH".equals(account.getAuthMode()))
        {
            if (StringUtils.isBlank(account.getOauthClientId()))
            {
                throw new MijiaClientException("OAuth 模式下，请填写 Xiaomi OAuth Client ID");
            }
            if (StringUtils.isBlank(account.getOauthRedirectUri()))
            {
                throw new MijiaClientException("OAuth 模式下，请填写已登记的 Redirect URI");
            }
            boolean hasExisting = exists != null
                && StringUtils.isNotBlank(exists.getEncryptedAccessToken())
                && StringUtils.isNotBlank(exists.getEncryptedRefreshToken());
            if (!hasExisting && StringUtils.isBlank(account.getEncryptedAccessToken()))
            {
                return;
            }
            return;
        }
        if ("TOKEN".equals(account.getAuthMode()))
        {
            boolean hasExisting = exists != null
                && StringUtils.isNotBlank(exists.getMijiaUserId())
                && StringUtils.isNotBlank(exists.getEncryptedServiceToken());
            if (StringUtils.isBlank(account.getMijiaUserId()) && !hasExisting)
            {
                throw new MijiaClientException("Token 模式下，米家 userId 不能为空");
            }
            if (StringUtils.isBlank(account.getMijiaServiceToken()) && !hasExisting)
            {
                throw new MijiaClientException("Token 模式下，serviceToken 不能为空");
            }
            return;
        }
        if ("MIJIA_API".equals(account.getAuthMode()))
        {
            return;
        }
        if (StringUtils.isBlank(account.getUsername()))
        {
            throw new MijiaClientException("账号密码模式下，米家账号不能为空");
        }
        if (StringUtils.isBlank(account.getPlatformPassword()) && (exists == null || StringUtils.isBlank(exists.getEncryptedPassword())))
        {
            throw new MijiaClientException("账号密码模式下，米家密码不能为空");
        }
    }

    private void persistRuntimeSecrets(ShPlatformAccount account)
    {
        if (!"OAUTH".equals(account.getAuthMode()))
        {
            return;
        }
        if (account.getAccountId() == null)
        {
            return;
        }
        account.setUpdateBy("system");
        accountMapper.updatePlatformAccount(account);
    }

    private boolean isTokenReady(ShPlatformAccount account)
    {
        return StringUtils.isNotBlank(account.getMijiaUserId())
            && StringUtils.isNotBlank(account.getEncryptedSsecurity())
            && StringUtils.isNotBlank(account.getEncryptedServiceToken());
    }

    private Map<String, Object> buildTokenDiagnosis(ShPlatformAccount account)
    {
        Map<String, Object> result = new LinkedHashMap<>();
        java.util.List<String> missing = new java.util.ArrayList<>();
        if (StringUtils.isBlank(account.getMijiaUserId()))
        {
            missing.add("userId");
        }
        if (StringUtils.isBlank(account.getEncryptedSsecurity()))
        {
            missing.add("ssecurity");
        }
        if (StringUtils.isBlank(account.getEncryptedServiceToken()))
        {
            missing.add("serviceToken");
        }
        String rawServiceToken = StringUtils.isNotBlank(account.getEncryptedServiceToken())
            ? MijiaCryptoUtils.decrypt(account.getEncryptedServiceToken())
            : "";
        String tokenSid = extractTokenSid(rawServiceToken);
        StringBuilder message = new StringBuilder("当前 Token 信息还不完整，缺少：")
            .append(String.join("、", missing));
        if (StringUtils.isNotBlank(tokenSid))
        {
            message.append("。当前 serviceToken sid=").append(tokenSid);
            if (!"V1_xiaomiio".equals(tokenSid))
            {
                message.append("，这不是目标米家接口 sid（V1_xiaomiio）");
            }
        }
        result.put("ready", Boolean.FALSE);
        result.put("region", account.getRegion());
        result.put("missingFields", missing);
        result.put("tokenSid", tokenSid);
        result.put("message", message.toString());
        return result;
    }

    private Map<String, Object> buildAuthFailureDiagnosis(ShPlatformAccount account, String message)
    {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("ready", Boolean.FALSE);
        result.put("region", account == null ? null : account.getRegion());
        result.put("authMode", account == null ? null : account.getAuthMode());
        result.put("message", StringUtils.defaultIfBlank(message, "连接失败"));
        if (account != null && "PASSWORD".equals(account.getAuthMode()))
        {
            String recommendation = "如果该账号频繁触发二次验证或风控，建议切换到 Token 模式，或先在米家 App 完成一次安全验证后再试。";
            if (StringUtils.contains(message, "验证地址") || StringUtils.contains(message, "验证码地址"))
            {
                recommendation = "请先在浏览器打开上面的验证地址完成校验，再回到系统重试；如果仍然反复触发验证，建议改用 Token 模式。";
            }
            result.put("recommendation", recommendation);
        }
        else if (account != null && "MIJIA_API".equals(account.getAuthMode()))
        {
            result.put("recommendation", "请先确认本机已安装 `python3.10 -m pip install mijiaAPI==3.0.5`，然后回到平台接入页重新生成二维码并用米家 App 扫码。");
        }
        else if (account != null && "TOKEN".equals(account.getAuthMode()))
        {
            String rawServiceToken = StringUtils.isNotBlank(account.getEncryptedServiceToken())
                ? MijiaCryptoUtils.decrypt(account.getEncryptedServiceToken())
                : "";
            result.put("tokenSid", extractTokenSid(rawServiceToken));
            result.put("recommendation", "建议确认 serviceToken 的 sid 是否为 V1_xiaomiio，并补齐 ssecurity 后再测试。");
        }
        else if (account != null && "OAUTH".equals(account.getAuthMode()))
        {
            result.put("recommendation", "建议检查 OAuth Client ID、Redirect URI，以及 Xiaomi 授权是否仍然有效。");
        }
        return result;
    }

    private String extractTokenSid(String serviceToken)
    {
        if (StringUtils.isBlank(serviceToken) || !serviceToken.startsWith("2.0&"))
        {
            return "";
        }
        String[] parts = serviceToken.split("&");
        return parts.length > 1 ? parts[1] : "";
    }

    private boolean oauthAppChanged(ShPlatformAccount account, ShPlatformAccount exists)
    {
        if (exists == null || !"OAUTH".equals(account.getAuthMode()))
        {
            return false;
        }
        return !StringUtils.equals(StringUtils.trimToEmpty(account.getOauthClientId()), StringUtils.trimToEmpty(exists.getOauthClientId()))
            || !StringUtils.equals(StringUtils.trimToEmpty(account.getOauthRedirectUri()), StringUtils.trimToEmpty(exists.getOauthRedirectUri()));
    }

    private void resetOauthAuthorization(ShPlatformAccount account)
    {
        account.setEncryptedAccessToken("");
        account.setEncryptedRefreshToken("");
        account.setOauthExpiresTs(null);
    }

    private String buildOauthCallbackHtml(boolean success, String message)
    {
        String title = success ? "Xiaomi OAuth 授权成功" : "Xiaomi OAuth 授权失败";
        String color = success ? "#2f855a" : "#c53030";
        return "<!doctype html><html><head><meta charset='utf-8'><title>" + title + "</title></head>"
            + "<body style='font-family:-apple-system,BlinkMacSystemFont,\"Segoe UI\",sans-serif;background:#f5f7fb;padding:48px;'>"
            + "<div style='max-width:680px;margin:0 auto;background:#fff;border-radius:16px;padding:32px;box-shadow:0 12px 40px rgba(15,23,42,.08);'>"
            + "<h2 style='margin:0 0 16px;color:" + color + ";'>" + title + "</h2>"
            + "<p style='margin:0;color:#334155;line-height:1.8;'>" + message + "</p>"
            + "<p style='margin:20px 0 0;color:#64748b;'>可以关闭当前页面，回到系统继续操作。</p>"
            + "</div></body></html>";
    }

    private String formEncode(Map<String, String> params)
    {
        List<String> pairs = new java.util.ArrayList<>();
        for (Map.Entry<String, String> entry : params.entrySet())
        {
            pairs.add(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8)
                + "=" + URLEncoder.encode(StringUtils.defaultString(entry.getValue()), StandardCharsets.UTF_8));
        }
        return String.join("&", pairs);
    }

    private static class PendingOauthContext
    {
        private String operator;

        private String region;

        private String clientId;

        private String redirectUri;

        private String deviceId;

        public String getOperator()
        {
            return operator;
        }

        public void setOperator(String operator)
        {
            this.operator = operator;
        }

        public String getRegion()
        {
            return region;
        }

        public void setRegion(String region)
        {
            this.region = region;
        }

        public String getClientId()
        {
            return clientId;
        }

        public void setClientId(String clientId)
        {
            this.clientId = clientId;
        }

        public String getRedirectUri()
        {
            return redirectUri;
        }

        public void setRedirectUri(String redirectUri)
        {
            this.redirectUri = redirectUri;
        }

        public String getDeviceId()
        {
            return deviceId;
        }

        public void setDeviceId(String deviceId)
        {
            this.deviceId = deviceId;
        }
    }
}
