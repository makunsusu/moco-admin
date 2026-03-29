package com.moco.system.service.smarthome;

import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.moco.common.utils.StringUtils;
import com.moco.system.domain.ShPlatformAccount;

@Component
public class MijiaCloudClient implements MijiaClient
{
    private static final String LOGIN_PREFIX = "&&&START&&&";

    private static final String SID = "xiaomiio";

    private static final String USER_AGENT = "Android-7.1.1-1.0.0-ONEPLUS A3010-136-" + SID + "-APP/xiaomi.smarthome APPV/62830";

    private static final String OAUTH_HOST = "ha.api.io.mi.com";

    private static final Pattern JSON_STRING_PATTERN = Pattern.compile("\"%s\"\\s*:\\s*\"((?:\\\\.|[^\"\\\\])*)\"");

    @Autowired
    private MijiaBridgeSupport bridgeSupport;

    @Override
    public void testConnection(ShPlatformAccount account)
    {
        List<MijiaDeviceRecord> records = fetchDevices(account);
        if (records.isEmpty())
        {
            throw new MijiaClientException("连接成功，但未查询到任何设备");
        }
    }

    @Override
    public List<MijiaDeviceRecord> fetchDevices(ShPlatformAccount account)
    {
        if (account == null)
        {
            throw new MijiaClientException("米家账号配置不完整");
        }
        if ("OAUTH".equals(account.getAuthMode()))
        {
            return fetchOfficialDevices(account);
        }
        if ("MIJIA_API".equals(account.getAuthMode()))
        {
            return fetchDevicesByLocalBridge();
        }
        XiaomiSession session = buildSession(account);
        JSONObject result = requestDeviceList(account.getRegion(), session);
        JSONArray list = result.getJSONArray("list");
        if (list == null)
        {
            list = result.getJSONArray("device_info");
        }
        List<MijiaDeviceRecord> records = new ArrayList<>();
        if (list == null)
        {
            return records;
        }
        for (int i = 0; i < list.size(); i++)
        {
            JSONObject item = list.getJSONObject(i);
            if (item == null)
            {
                continue;
            }
            MijiaDeviceRecord record = new MijiaDeviceRecord();
            record.setDid(item.getString("did"));
            record.setUid(item.getString("uid"));
            record.setHomeId(stringValue(item, "home_id", "homeId"));
            record.setHomeName(stringValue(item, "home_name", "homeName", "ssid"));
            record.setRoomId(stringValue(item, "room_id", "roomId"));
            record.setRoomName(stringValue(item, "room_name", "roomName"));
            record.setDeviceName(stringValue(item, "name", "device_name", "deviceName"));
            record.setModel(item.getString("model"));
            record.setDeviceType(resolveDeviceType(item.getString("model")));
            record.setOnlineStatus(resolveOnlineStatus(item));
            record.setPowerStatus(resolvePowerStatus(item));
            record.setRawPayload(JSON.toJSONString(item));
            record.setProperties(flattenProperties(item));
            records.add(record);
        }
        return records;
    }

    @Override
    public List<MijiaDeviceRecord> fetchDeviceStates(ShPlatformAccount account)
    {
        if (account == null)
        {
            throw new MijiaClientException("米家账号配置不完整");
        }
        if ("MIJIA_API".equals(account.getAuthMode()))
        {
            return fetchDevicesByLocalBridge("poll_device_state.py");
        }
        return fetchDevices(account);
    }

    @Override
    public Map<String, Object> setDevicePower(ShPlatformAccount account, String did, boolean powerOn)
    {
        if (!"MIJIA_API".equals(account.getAuthMode()))
        {
            throw new MijiaClientException("当前仅支持本机扫码登录模式下的设备开关控制");
        }
        try
        {
            ProcessBuilder processBuilder = new ProcessBuilder(
                bridgeSupport.getLocalPython(),
                bridgeSupport.resolveScript("set_power_state.py"),
                did,
                powerOn ? "on" : "off"
            );
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();
            String output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            int exitCode = process.waitFor();
            JSONObject json = JSON.parseObject(output);
            if (json == null)
            {
                throw new MijiaClientException("本机设备控制桥接返回内容无法解析");
            }
            if (!"success".equalsIgnoreCase(json.getString("status")) || exitCode != 0)
            {
                throw new MijiaClientException(StringUtils.defaultIfBlank(json.getString("message"), "本机设备控制桥接执行失败"));
            }
            return new LinkedHashMap<>(json);
        }
        catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
            throw new MijiaClientException("执行本机设备控制桥接被中断", e);
        }
        catch (IOException e)
        {
            throw new MijiaClientException("执行本机设备控制桥接失败", e);
        }
    }

    private List<MijiaDeviceRecord> fetchDevicesByLocalBridge()
    {
        return fetchDevicesByLocalBridge("list_devices.py");
    }

    private List<MijiaDeviceRecord> fetchDevicesByLocalBridge(String scriptName)
    {
        try
        {
            ProcessBuilder processBuilder = new ProcessBuilder(
                bridgeSupport.getLocalPython(),
                bridgeSupport.resolveScript(scriptName)
            );
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();
            String output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            int exitCode = process.waitFor();
            JSONObject json = JSON.parseObject(output);
            if (json == null)
            {
                throw new MijiaClientException("本机米家桥接脚本返回内容无法解析");
            }
            if (!"success".equalsIgnoreCase(json.getString("status")))
            {
                String message = StringUtils.defaultIfBlank(json.getString("message"), "本机米家桥接脚本执行失败");
                String loginCommand = json.getString("loginCommand");
                String installCommand = json.getString("installCommand");
                if (StringUtils.isNotBlank(loginCommand))
                {
                    message += " 请先执行：" + loginCommand;
                }
                if (StringUtils.isNotBlank(installCommand))
                {
                    message += " 请先执行：" + installCommand;
                }
                throw new MijiaClientException(message);
            }
            if (exitCode != 0)
            {
                throw new MijiaClientException("本机米家桥接脚本执行失败，退出码=" + exitCode);
            }
            JSONArray devices = json.getJSONArray("devices");
            List<MijiaDeviceRecord> records = new ArrayList<>();
            if (devices == null)
            {
                return records;
            }
            for (int i = 0; i < devices.size(); i++)
            {
                JSONObject item = devices.getJSONObject(i);
                if (item == null)
                {
                    continue;
                }
                MijiaDeviceRecord record = new MijiaDeviceRecord();
                record.setDid(item.getString("did"));
                record.setUid(item.getString("uid"));
                record.setHomeId(item.getString("homeId"));
                record.setHomeName(item.getString("homeName"));
                record.setRoomId(item.getString("roomId"));
                record.setRoomName(item.getString("roomName"));
                record.setDeviceName(item.getString("deviceName"));
                record.setModel(item.getString("model"));
                record.setDeviceType(resolveDeviceType(item.getString("model")));
                record.setOnlineStatus(StringUtils.defaultIfBlank(item.getString("onlineStatus"), "0"));
                record.setPowerStatus(StringUtils.defaultIfBlank(item.getString("powerStatus"), "UNKNOWN"));
                record.setRawPayload(JSON.toJSONString(item.get("rawPayload")));
                JSONObject properties = item.getJSONObject("properties");
                if (properties != null)
                {
                    Map<String, String> propertyMap = new LinkedHashMap<>();
                    for (Map.Entry<String, Object> entry : properties.entrySet())
                    {
                        propertyMap.put(entry.getKey(), entry.getValue() == null ? "" : String.valueOf(entry.getValue()));
                    }
                    record.setProperties(propertyMap);
                }
                records.add(record);
            }
            return records;
        }
        catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
            throw new MijiaClientException("执行本机米家桥接脚本被中断", e);
        }
        catch (IOException e)
        {
            throw new MijiaClientException("执行本机米家桥接脚本失败", e);
        }
    }

    private XiaomiSession buildSession(ShPlatformAccount account)
    {
        if ("TOKEN".equals(account.getAuthMode()))
        {
            return tokenSession(account);
        }
        if (StringUtils.isBlank(account.getUsername()) || StringUtils.isBlank(account.getEncryptedPassword()))
        {
            throw new MijiaClientException("米家账号配置不完整");
        }
        return login(account);
    }

    private XiaomiSession tokenSession(ShPlatformAccount account)
    {
        if (StringUtils.isBlank(account.getMijiaUserId())
            || StringUtils.isBlank(account.getEncryptedSsecurity())
            || StringUtils.isBlank(account.getEncryptedServiceToken()))
        {
            throw new MijiaClientException("米家 Token 配置不完整");
        }
        XiaomiSession session = new XiaomiSession();
        session.client = buildHttpClient(null);
        session.userId = account.getMijiaUserId();
        session.ssecurity = MijiaCryptoUtils.decrypt(account.getEncryptedSsecurity());
        session.serviceToken = MijiaCryptoUtils.decrypt(account.getEncryptedServiceToken());
        return session;
    }

    private XiaomiSession login(ShPlatformAccount account)
    {
        CookieManager cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        HttpClient client = buildHttpClient(cookieManager, HttpClient.Redirect.NEVER);
        try
        {
            String deviceId = randomDeviceId();
            JSONObject signResponse = parseLoginResponse(send(client, HttpRequest.newBuilder()
                .uri(URI.create("https://account.xiaomi.com/pass/serviceLogin?sid=" + SID + "&_json=true&_locale=zh_CN"))
                .timeout(Duration.ofSeconds(20))
                .header("User-Agent", USER_AGENT)
                .header("Cookie", loginCookie(deviceId, account.getUsername()))
                .GET()
                .build()));
            Map<String, String> loginParams = new LinkedHashMap<>();
            loginParams.put("sid", SID);
            loginParams.put("qs", StringUtils.defaultIfBlank(signResponse.getString("qs"), "%3Fsid%3Dxiaomiio%26_json%3Dtrue"));
            loginParams.put("callback", StringUtils.defaultIfBlank(signResponse.getString("callback"), "https://sts.api.io.mi.com/sts"));
            loginParams.put("_sign", signResponse.getString("_sign"));
            loginParams.put("_json", "true");
            loginParams.put("_dc", String.valueOf(System.currentTimeMillis()));
            loginParams.put("user", account.getUsername());
            loginParams.put("hash", MijiaCryptoUtils.md5Upper(MijiaCryptoUtils.decrypt(account.getEncryptedPassword())));
            String form = formEncode(loginParams);
            HttpResponse<String> authHttpResponse = sendForResponse(client, HttpRequest.newBuilder()
                .uri(URI.create("https://account.xiaomi.com/pass/serviceLoginAuth2"))
                .timeout(Duration.ofSeconds(20))
                .header("User-Agent", USER_AGENT)
                .header("Cookie", loginCookie(deviceId, account.getUsername()))
                .header("Accept", "application/json, text/plain, */*")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(form))
                .build());
            String authRaw = authHttpResponse.body();
            JSONObject authResponse = parseLoginResponse(authRaw);
            Integer code = authResponse.getInteger("code");
            if (code != null && code.intValue() != 0)
            {
                throw new MijiaClientException(buildLoginError(authResponse));
            }
            String location = firstNonBlankRaw(authRaw, "location", "Location");
            String callback = firstNonBlankRaw(authRaw, "callback");
            if (StringUtils.isBlank(location))
            {
                throw new MijiaClientException(buildMissingLocationMessage(authResponse));
            }
            HttpResponse<String> authEndResponse = sendForResponse(client, HttpRequest.newBuilder()
                .uri(URI.create(location))
                .timeout(Duration.ofSeconds(20))
                .header("User-Agent", USER_AGENT)
                .header("Cookie", loginCookie(deviceId, account.getUsername()))
                .GET()
                .build());
            XiaomiSession session = new XiaomiSession();
            session.client = buildHttpClient(null);
            session.userId = StringUtils.defaultIfBlank(firstNonBlankRaw(authRaw, "userId"), authResponse.getString("userId"));
            session.ssecurity = StringUtils.defaultIfBlank(firstNonBlankRaw(authRaw, "ssecurity"), authResponse.getString("ssecurity"));
            if (StringUtils.isBlank(session.ssecurity))
            {
                session.ssecurity = extractSsecurity(authEndResponse);
            }
            String stsLocation = authEndResponse.headers().firstValue("Location").orElse(null);
            if (StringUtils.isBlank(stsLocation))
            {
                stsLocation = firstNonBlankRaw(authEndResponse.body(), "location");
            }
            if (StringUtils.isBlank(stsLocation))
            {
                stsLocation = callback;
            }
            if (StringUtils.isBlank(extractCookie(cookieManager, "serviceToken")) && StringUtils.isNotBlank(stsLocation))
            {
                followStsRedirect(client, stsLocation, deviceId, account.getUsername(), 3);
            }
            session.serviceToken = extractCookie(cookieManager, "serviceToken");
            if (StringUtils.isBlank(session.serviceToken) || StringUtils.isBlank(session.ssecurity))
            {
                throw new MijiaClientException("米家登录失败，未获取到有效会话");
            }
            return session;
        }
        catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
            throw new MijiaClientException("连接米家服务失败", e);
        }
        catch (IOException e)
        {
            throw new MijiaClientException("连接米家服务失败", e);
        }
    }

    private HttpClient buildHttpClient(CookieManager cookieManager)
    {
        return buildHttpClient(cookieManager, HttpClient.Redirect.NORMAL);
    }

    private HttpClient buildHttpClient(CookieManager cookieManager, HttpClient.Redirect redirectPolicy)
    {
        HttpClient.Builder builder = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(15))
            .followRedirects(redirectPolicy);
        if (cookieManager != null)
        {
            builder.cookieHandler(cookieManager);
        }
        return builder.build();
    }

    private JSONObject requestDeviceList(String region, XiaomiSession session)
    {
        try
        {
            String host = "cn".equalsIgnoreCase(region) ? "https://api.io.mi.com" : "https://" + region + ".api.io.mi.com";
            String path = "/app/home/device_list";
            String data = "{\"getVirtualModel\":true,\"getHuamiDevices\":1}";
            String nonce = createNonce();
            String signedNonce = createSignedNonce(session.ssecurity, nonce);
            Map<String, String> params = buildEncryptedRequestParams(path, signedNonce, nonce, session.ssecurity, data);
            String response = send(session.client, HttpRequest.newBuilder()
                .uri(URI.create(host + path))
                .timeout(Duration.ofSeconds(20))
                .header("Accept-Encoding", "identity")
                .header("User-Agent", USER_AGENT)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("x-xiaomi-protocal-flag-cli", "PROTOCAL-HTTP2")
                .header("MIOT-ENCRYPT-ALGORITHM", "ENCRYPT-RC4")
                .header("Cookie", apiCookie(session))
                .POST(HttpRequest.BodyPublishers.ofString(formEncode(params)))
                .build());
            String decrypted = decryptRc4Response(signedNonce, response);
            JSONObject json = JSON.parseObject(decrypted);
            if (json == null)
            {
                throw new MijiaClientException("米家设备列表返回为空");
            }
            Integer code = json.getInteger("code");
            if (code != null && code.intValue() != 0)
            {
                throw new MijiaClientException(StringUtils.defaultIfBlank(json.getString("message"), "米家设备列表请求失败"));
            }
            JSONObject result = json.getJSONObject("result");
            return result == null ? new JSONObject() : result;
        }
        catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
            throw new MijiaClientException("拉取米家设备列表失败", e);
        }
        catch (IOException e)
        {
            throw new MijiaClientException("拉取米家设备列表失败", e);
        }
    }

    public static Map<String, Object> exchangeOfficialOauthCode(
        String clientId, String redirectUri, String region, String deviceId, String code)
    {
        return getOfficialOauthToken(clientId, redirectUri, region, new LinkedHashMap<String, Object>() {{
            put("client_id", parseClientIdStrict(clientId));
            put("redirect_uri", redirectUri);
            put("code", code);
            put("device_id", deviceId);
        }});
    }

    private List<MijiaDeviceRecord> fetchOfficialDevices(ShPlatformAccount account)
    {
        try
        {
            String accessToken = ensureOauthAccessToken(account);
            HttpClient client = buildHttpClient(null);
            String region = StringUtils.defaultIfBlank(account.getRegion(), "cn");
            String host = "cn".equalsIgnoreCase(region) ? OAUTH_HOST : region + "." + OAUTH_HOST;
            String clientId = StringUtils.trimToEmpty(account.getOauthClientId());
            if (StringUtils.isBlank(clientId))
            {
                throw new MijiaClientException("OAuth Client ID 未配置");
            }
            JSONObject homeResult = officialPost(client, host, "/app/v2/homeroom/gethome", accessToken, clientId, new LinkedHashMap<String, Object>() {{
                put("limit", 150);
                put("fetch_share", true);
                put("fetch_share_dev", true);
                put("plat_form", 0);
                put("app_ver", 9);
            }});
            JSONObject deviceResult = fetchOfficialDeviceList(client, host, accessToken, clientId, null);
            Map<String, String> homeNames = new HashMap<>();
            Map<String, String> roomNames = new HashMap<>();
            Map<String, String> roomHomeIds = new HashMap<>();
            if (homeResult != null)
            {
                fillHomeMappings(homeResult, homeNames, roomNames, roomHomeIds);
            }
            JSONArray list = deviceResult == null ? null : deviceResult.getJSONArray("list");
            List<MijiaDeviceRecord> records = new ArrayList<>();
            if (list == null)
            {
                return records;
            }
            for (int i = 0; i < list.size(); i++)
            {
                JSONObject item = list.getJSONObject(i);
                if (item == null)
                {
                    continue;
                }
                MijiaDeviceRecord record = new MijiaDeviceRecord();
                String roomId = stringValue(item, "room_id", "roomId");
                String homeId = stringValue(item, "home_id", "homeId");
                if (StringUtils.isBlank(homeId) && StringUtils.isNotBlank(roomId))
                {
                    homeId = roomHomeIds.get(roomId);
                }
                record.setDid(item.getString("did"));
                record.setUid(stringValue(item, "uid"));
                record.setHomeId(homeId);
                record.setHomeName(StringUtils.defaultIfBlank(stringValue(item, "home_name", "homeName"), homeNames.get(homeId)));
                record.setRoomId(roomId);
                record.setRoomName(StringUtils.defaultIfBlank(stringValue(item, "room_name", "roomName"), roomNames.get(roomId)));
                record.setDeviceName(stringValue(item, "name", "device_name", "deviceName"));
                record.setModel(item.getString("model"));
                record.setDeviceType(resolveDeviceType(item.getString("model")));
                record.setOnlineStatus(resolveOnlineStatus(item));
                record.setPowerStatus(resolvePowerStatus(item));
                record.setRawPayload(JSON.toJSONString(item));
                record.setProperties(flattenProperties(item));
                records.add(record);
            }
            return records;
        }
        catch (IOException | InterruptedException e)
        {
            Thread.currentThread().interrupt();
            throw new MijiaClientException("拉取 Xiaomi OAuth 设备列表失败", e);
        }
    }

    private void fillHomeMappings(JSONObject homeResult, Map<String, String> homeNames, Map<String, String> roomNames, Map<String, String> roomHomeIds)
    {
        for (String sourceKey : new String[] { "homelist", "share_home_list" })
        {
            JSONArray homes = homeResult.getJSONArray(sourceKey);
            if (homes == null)
            {
                continue;
            }
            for (int i = 0; i < homes.size(); i++)
            {
                JSONObject home = homes.getJSONObject(i);
                if (home == null || home.get("id") == null)
                {
                    continue;
                }
                String homeId = String.valueOf(home.get("id"));
                homeNames.put(homeId, home.getString("name"));
                JSONArray rooms = home.getJSONArray("roomlist");
                if (rooms == null)
                {
                    continue;
                }
                for (int j = 0; j < rooms.size(); j++)
                {
                    JSONObject room = rooms.getJSONObject(j);
                    if (room == null || room.get("id") == null)
                    {
                        continue;
                    }
                    String roomId = String.valueOf(room.get("id"));
                    roomNames.put(roomId, room.getString("name"));
                    roomHomeIds.put(roomId, homeId);
                }
            }
        }
    }

    private JSONObject fetchOfficialDeviceList(HttpClient client, String host, String accessToken, String clientId, String startDid)
        throws IOException, InterruptedException
    {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("limit", 200);
        data.put("get_split_device", true);
        data.put("get_third_device", true);
        data.put("dids", new ArrayList<>());
        if (StringUtils.isNotBlank(startDid))
        {
            data.put("start_did", startDid);
        }
        JSONObject result = officialPost(client, host, "/app/v2/home/device_list_page", accessToken, clientId, data);
        if (result != null && Boolean.TRUE.equals(result.getBoolean("has_more")) && StringUtils.isNotBlank(result.getString("next_start_did")))
        {
            JSONObject next = fetchOfficialDeviceList(client, host, accessToken, clientId, result.getString("next_start_did"));
            JSONArray currentList = result.getJSONArray("list");
            JSONArray nextList = next == null ? null : next.getJSONArray("list");
            if (currentList != null && nextList != null)
            {
                currentList.addAll(nextList);
            }
        }
        return result;
    }

    private JSONObject officialPost(HttpClient client, String host, String path, String accessToken, String clientId, Map<String, Object> body)
        throws IOException, InterruptedException
    {
        HttpResponse<String> response = sendForResponse(client, HttpRequest.newBuilder()
            .uri(URI.create("https://" + host + path))
            .timeout(Duration.ofSeconds(20))
            .header("Host", host)
            .header("X-Client-BizId", "haapi")
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer " + accessToken)
            .header("X-Client-AppId", clientId)
            .POST(HttpRequest.BodyPublishers.ofString(JSON.toJSONString(body)))
            .build());
        if (response.statusCode() == 401)
        {
            throw new MijiaClientException("Xiaomi OAuth 授权已失效，请重新完成授权");
        }
        if (response.statusCode() != 200)
        {
            throw new MijiaClientException("米家官方接口请求失败，HTTP " + response.statusCode());
        }
        JSONObject json = JSON.parseObject(response.body());
        if (json == null)
        {
            throw new MijiaClientException("米家官方接口返回为空");
        }
        if (json.getIntValue("code") != 0)
        {
            throw new MijiaClientException(StringUtils.defaultIfBlank(json.getString("message"), "米家官方接口返回异常"));
        }
        return json.getJSONObject("result");
    }

    private String ensureOauthAccessToken(ShPlatformAccount account) throws IOException, InterruptedException
    {
        if (StringUtils.isBlank(account.getEncryptedAccessToken()) || StringUtils.isBlank(account.getEncryptedRefreshToken()))
        {
            throw new MijiaClientException("请先完成 Xiaomi OAuth 应用授权");
        }
        String clientId = StringUtils.trimToEmpty(account.getOauthClientId());
        if (StringUtils.isBlank(clientId))
        {
            throw new MijiaClientException("OAuth Client ID 未配置");
        }
        String redirectUri = StringUtils.defaultIfBlank(
            account.getOauthRedirectUri(),
            "http://127.0.0.1:8080/smarthome/platform/oauth/callback");
        long now = System.currentTimeMillis() / 1000;
        if (account.getOauthExpiresTs() != null && account.getOauthExpiresTs() > now + 300)
        {
            return MijiaCryptoUtils.decrypt(account.getEncryptedAccessToken());
        }
        Map<String, Object> authInfo = getOfficialOauthToken(
            clientId,
            redirectUri,
            StringUtils.defaultIfBlank(account.getRegion(), "cn"),
            new LinkedHashMap<String, Object>() {{
                put("client_id", parseClientId(clientId));
                put("redirect_uri", redirectUri);
                put("refresh_token", MijiaCryptoUtils.decrypt(account.getEncryptedRefreshToken()));
            }});
        account.setEncryptedAccessToken(MijiaCryptoUtils.encrypt(String.valueOf(authInfo.get("accessToken"))));
        account.setEncryptedRefreshToken(MijiaCryptoUtils.encrypt(String.valueOf(authInfo.get("refreshToken"))));
        account.setOauthExpiresTs(((Number) authInfo.get("expiresTs")).longValue());
        account.setOauthClientId(String.valueOf(authInfo.get("clientId")));
        account.setOauthRedirectUri(String.valueOf(authInfo.get("redirectUri")));
        return String.valueOf(authInfo.get("accessToken"));
    }

    private Long parseClientId(String clientId)
    {
        try
        {
            return Long.parseLong(clientId);
        }
        catch (NumberFormatException e)
        {
            throw new MijiaClientException("OAuth Client ID 格式无效");
        }
    }

    private static Long parseClientIdStrict(String clientId)
    {
        try
        {
            return Long.parseLong(clientId);
        }
        catch (NumberFormatException e)
        {
            throw new MijiaClientException("OAuth Client ID 格式无效");
        }
    }

    private static Map<String, Object> getOfficialOauthToken(String clientId, String redirectUri, String region, Map<String, Object> data)
    {
        try
        {
            HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(15)).followRedirects(HttpClient.Redirect.NORMAL).build();
            String host = "cn".equalsIgnoreCase(region) ? OAUTH_HOST : region + "." + OAUTH_HOST;
            String query = URLEncoder.encode(JSON.toJSONString(data), StandardCharsets.UTF_8);
            HttpResponse<String> response = client.send(HttpRequest.newBuilder()
                .uri(URI.create("https://" + host + "/app/v2/ha/oauth/get_token?data=" + query))
                .timeout(Duration.ofSeconds(20))
                .header("content-type", "application/x-www-form-urlencoded")
                .GET()
                .build(), HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (response.statusCode() == 401)
            {
                throw new MijiaClientException("Xiaomi OAuth 授权无效或已过期");
            }
            JSONObject json = JSON.parseObject(response.body());
            JSONObject result = json == null ? null : json.getJSONObject("result");
            if (json == null || json.getIntValue("code") != 0 || result == null)
            {
                throw new MijiaClientException("Xiaomi OAuth 换取 Token 失败");
            }
            Map<String, Object> authInfo = new LinkedHashMap<>();
            authInfo.put("accessToken", result.getString("access_token"));
            authInfo.put("refreshToken", result.getString("refresh_token"));
            authInfo.put("expiresTs", System.currentTimeMillis() / 1000 + (long) (result.getLongValue("expires_in") * 0.7));
            authInfo.put("redirectUri", redirectUri);
            authInfo.put("clientId", clientId);
            return authInfo;
        }
        catch (IOException | InterruptedException e)
        {
            Thread.currentThread().interrupt();
            throw new MijiaClientException("Xiaomi OAuth 换取 Token 失败", e);
        }
    }

    private String send(HttpClient client, HttpRequest request) throws IOException, InterruptedException
    {
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        return response.body();
    }

    private HttpResponse<String> sendForResponse(HttpClient client, HttpRequest request) throws IOException, InterruptedException
    {
        return client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
    }

    private JSONObject parseLoginResponse(String raw)
    {
        String content = raw != null && raw.startsWith(LOGIN_PREFIX) ? raw.substring(LOGIN_PREFIX.length()) : raw;
        JSONObject json = JSON.parseObject(content);
        if (json == null)
        {
            throw new MijiaClientException("米家登录返回解析失败");
        }
        return json;
    }

    private String extractCookie(CookieManager cookieManager, String name)
    {
        return cookieManager.getCookieStore().getCookies().stream()
            .filter(cookie -> name.equals(cookie.getName()))
            .map(cookie -> cookie.getValue())
            .findFirst()
            .orElse(null);
    }

    private String formEncode(Map<String, String> params)
    {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet())
        {
            if (builder.length() > 0)
            {
                builder.append('&');
            }
            builder.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8));
            builder.append('=');
            builder.append(URLEncoder.encode(StringUtils.defaultString(entry.getValue()), StandardCharsets.UTF_8));
        }
        return builder.toString();
    }

    private String loginCookie(String deviceId, String username)
    {
        StringBuilder builder = new StringBuilder();
        builder.append("sdkVersion=accountsdk-18.8.15; deviceId=").append(deviceId).append("; userSpaceId=0;");
        if (StringUtils.isNotBlank(username))
        {
            builder.append(" userId=").append(username).append(';');
        }
        return builder.toString();
    }

    private String firstNonBlank(JSONObject json, String... keys)
    {
        for (String key : keys)
        {
            String value = json.getString(key);
            if (StringUtils.isNotBlank(value))
            {
                return value;
            }
        }
        return null;
    }

    private String firstNonBlankRaw(String raw, String... keys)
    {
        for (String key : keys)
        {
            String value = extractJsonStringValue(raw, key);
            if (StringUtils.isNotBlank(value))
            {
                return value;
            }
        }
        return null;
    }

    private String extractJsonStringValue(String raw, String key)
    {
        if (StringUtils.isBlank(raw) || StringUtils.isBlank(key))
        {
            return null;
        }
        Pattern pattern = Pattern.compile(String.format(JSON_STRING_PATTERN.pattern(), Pattern.quote(key)));
        Matcher matcher = pattern.matcher(raw);
        while (matcher.find())
        {
            String escaped = matcher.group(1);
            String value = JSON.parseObject("{\"v\":\"" + escaped + "\"}").getString("v");
            if (StringUtils.isNotBlank(value))
            {
                return value;
            }
        }
        return null;
    }

    private String extractSsecurity(HttpResponse<String> authEndResponse)
    {
        String extensionPragma = authEndResponse.headers().firstValue("extension-pragma").orElse(null);
        String ssecurity = firstNonBlankRaw(extensionPragma, "ssecurity");
        if (StringUtils.isNotBlank(ssecurity))
        {
            return ssecurity;
        }
        return firstNonBlankRaw(authEndResponse.body(), "ssecurity");
    }

    private void followStsRedirect(HttpClient client, String url, String deviceId, String username, int remainingHops)
        throws IOException, InterruptedException
    {
        if (StringUtils.isBlank(url) || remainingHops <= 0)
        {
            return;
        }
        HttpResponse<String> response = sendForResponse(client, HttpRequest.newBuilder()
            .uri(URI.create(url))
            .timeout(Duration.ofSeconds(20))
            .header("User-Agent", USER_AGENT)
            .header("Cookie", loginCookie(deviceId, username))
            .GET()
            .build());
        String next = response.headers().firstValue("Location").orElse(null);
        if (StringUtils.isBlank(next))
        {
            next = firstNonBlankRaw(response.body(), "location");
        }
        if (StringUtils.isNotBlank(next))
        {
            followStsRedirect(client, next, deviceId, username, remainingHops - 1);
        }
    }

    private String buildLoginError(JSONObject authResponse)
    {
        String desc = firstNonBlank(authResponse, "desc", "description");
        String notificationUrl = firstNonBlank(authResponse, "notificationUrl", "notification_url");
        String captchaUrl = firstNonBlank(authResponse, "captchaUrl", "captcha_url");
        Integer code = authResponse.getInteger("code");
        if (StringUtils.isNotBlank(captchaUrl))
        {
            return "米家登录失败，账号触发验证码或风控校验，请先在米家 App 内完成验证后再重试。验证码地址："
                + StringUtils.substring(captchaUrl, 0, 240);
        }
        if (StringUtils.isNotBlank(notificationUrl))
        {
            return "米家登录失败，账号需要二次验证或安全确认，请先在米家 App/小米账号中心完成验证后再重试。验证地址："
                + StringUtils.substring(notificationUrl, 0, 240);
        }
        String message = StringUtils.isNotBlank(desc) ? desc : "米家登录失败";
        return code == null ? message : message + "（code=" + code + "）";
    }

    private String buildMissingLocationMessage(JSONObject authResponse)
    {
        String notificationUrl = firstNonBlank(authResponse, "notificationUrl", "notification_url");
        String captchaUrl = firstNonBlank(authResponse, "captchaUrl", "captcha_url");
        String desc = firstNonBlank(authResponse, "desc", "description");
        Integer code = authResponse.getInteger("code");
        if (StringUtils.isNotBlank(captchaUrl))
        {
            return "米家登录未返回授权地址，当前账号触发验证码或风险校验，请先在米家 App 内完成验证。验证码地址："
                + StringUtils.substring(captchaUrl, 0, 240);
        }
        if (StringUtils.isNotBlank(notificationUrl))
        {
            return "米家登录未返回授权地址，当前账号需要二次验证或安全确认，请先在米家 App/小米账号中心完成验证。验证地址："
                + StringUtils.substring(notificationUrl, 0, 240);
        }
        if (StringUtils.isNotBlank(desc))
        {
            return code == null ? "米家登录未返回授权地址：" + desc : "米家登录未返回授权地址：" + desc + "（code=" + code + "）";
        }
        String summary = authResponse.toJSONString();
        summary = StringUtils.substring(summary, 0, 220);
        return "米家登录未返回授权地址，返回内容摘要：" + summary;
    }

    private String randomDeviceId()
    {
        String alphabet = "abcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 6; i++)
        {
            builder.append(alphabet.charAt(ThreadLocalRandom.current().nextInt(alphabet.length())));
        }
        return builder.toString();
    }

    private String createNonce()
    {
        byte[] random = new byte[8];
        ThreadLocalRandom.current().nextBytes(random);
        int minutes = (int) (System.currentTimeMillis() / 60000L);
        ByteBuffer buffer = ByteBuffer.allocate(12);
        buffer.put(random);
        buffer.putInt(minutes);
        return Base64.getEncoder().encodeToString(buffer.array());
    }

    private String createSignedNonce(String ssecurity, String nonce)
    {
        try
        {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(Base64.getDecoder().decode(ssecurity));
            digest.update(Base64.getDecoder().decode(nonce));
            return Base64.getEncoder().encodeToString(digest.digest());
        }
        catch (Exception e)
        {
            throw new MijiaClientException("生成米家签名失败", e);
        }
    }

    private Map<String, String> buildEncryptedRequestParams(String path, String signedNonce, String nonce, String ssecurity, String data)
    {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("data", data);
        params.put("rc4_hash__", createRc4Signature("POST", path, signedNonce, params));
        Map<String, String> encrypted = new LinkedHashMap<>();
        for (Map.Entry<String, String> entry : params.entrySet())
        {
            encrypted.put(entry.getKey(), encryptRc4(signedNonce, entry.getValue()));
        }
        encrypted.put("signature", createRc4Signature("POST", path, signedNonce, encrypted));
        encrypted.put("ssecurity", ssecurity);
        encrypted.put("_nonce", nonce);
        return encrypted;
    }

    private String apiCookie(XiaomiSession session)
    {
        return "userId=" + session.userId
            + "; yetAnotherServiceToken=" + session.serviceToken
            + "; serviceToken=" + session.serviceToken
            + "; locale=zh_CN; timezone=GMT+08:00; is_daylight=0; dst_offset=0; channel=MI_APP_STORE";
    }

    private String decryptRc4Response(String signedNonce, String payload)
    {
        try
        {
            return new String(rc4Crypt(Base64.getDecoder().decode(signedNonce), Base64.getDecoder().decode(payload), true), StandardCharsets.UTF_8);
        }
        catch (Exception e)
        {
            throw new MijiaClientException("解密米家设备列表响应失败", e);
        }
    }

    private String encryptRc4(String signedNonce, String plain)
    {
        try
        {
            byte[] encrypted = rc4Crypt(Base64.getDecoder().decode(signedNonce), plain.getBytes(StandardCharsets.UTF_8), true);
            return Base64.getEncoder().encodeToString(encrypted);
        }
        catch (Exception e)
        {
            throw new MijiaClientException("加密米家请求参数失败", e);
        }
    }

    private byte[] rc4Crypt(byte[] key, byte[] data, boolean skip1024)
    {
        int[] s = new int[256];
        for (int i = 0; i < 256; i++)
        {
            s[i] = i;
        }
        int j = 0;
        for (int i = 0; i < 256; i++)
        {
            j = (j + s[i] + (key[i % key.length] & 0xFF)) & 255;
            int tmp = s[i];
            s[i] = s[j];
            s[j] = tmp;
        }
        int i = 0;
        j = 0;
        if (skip1024)
        {
            for (int n = 0; n < 1024; n++)
            {
                i = (i + 1) & 255;
                j = (j + s[i]) & 255;
                int tmp = s[i];
                s[i] = s[j];
                s[j] = tmp;
            }
        }
        byte[] out = new byte[data.length];
        for (int n = 0; n < data.length; n++)
        {
            i = (i + 1) & 255;
            j = (j + s[i]) & 255;
            int tmp = s[i];
            s[i] = s[j];
            s[j] = tmp;
            out[n] = (byte) (data[n] ^ s[(s[i] + s[j]) & 255]);
        }
        return out;
    }

    private String createRc4Signature(String method, String path, String signedNonce, Map<String, String> params)
    {
        try
        {
            String normalizedPath = path.replace("/app/", "/");
            StringBuilder builder = new StringBuilder()
                .append(method.toUpperCase())
                .append('&')
                .append(normalizedPath);
            for (Map.Entry<String, String> entry : params.entrySet())
            {
                builder.append('&').append(entry.getKey()).append('=').append(entry.getValue());
            }
            builder.append('&').append(signedNonce);
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            return Base64.getEncoder().encodeToString(digest.digest(builder.toString().getBytes(StandardCharsets.UTF_8)));
        }
        catch (Exception e)
        {
            throw new MijiaClientException("生成米家 RC4 签名失败", e);
        }
    }

    private String sign(String path, String signedNonce, String nonce, String data)
    {
        try
        {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(Base64.getDecoder().decode(signedNonce), "HmacSHA256"));
            String payload = path + "&" + signedNonce + "&" + nonce + "&data=" + data;
            return Base64.getEncoder().encodeToString(mac.doFinal(payload.getBytes(StandardCharsets.UTF_8)));
        }
        catch (Exception e)
        {
            throw new MijiaClientException("生成米家请求签名失败", e);
        }
    }

    private String resolveDeviceType(String model)
    {
        if (StringUtils.isBlank(model))
        {
            return "UNKNOWN";
        }
        String prefix = model.split("\\.")[0].toUpperCase();
        if (prefix.contains("LIGHT") || prefix.contains("YEELIGHT"))
        {
            return "LIGHT";
        }
        if (prefix.contains("PLUG") || prefix.contains("OUTLET"))
        {
            return "SWITCH";
        }
        return prefix;
    }

    private String resolveOnlineStatus(JSONObject item)
    {
        Boolean online = item.getBoolean("isOnline");
        if (online == null)
        {
            online = item.getBoolean("online");
        }
        return Boolean.TRUE.equals(online) ? "1" : "0";
    }

    private String resolvePowerStatus(JSONObject item)
    {
        Map<String, String> props = flattenProperties(item);
        String[] candidates = { "prop.power", "prop.switch_status", "prop.on", "props.power", "props.on", "status" };
        for (String key : candidates)
        {
            String value = props.get(key);
            if (StringUtils.isNotBlank(value))
            {
                return normalizePowerValue(value);
            }
        }
        return "UNKNOWN";
    }

    private String normalizePowerValue(String value)
    {
        String text = value.trim().toLowerCase();
        if ("1".equals(text) || "true".equals(text) || "on".equals(text))
        {
            return "ON";
        }
        if ("0".equals(text) || "false".equals(text) || "off".equals(text))
        {
            return "OFF";
        }
        return value;
    }

    private String stringValue(JSONObject item, String... keys)
    {
        for (String key : keys)
        {
            String value = item.getString(key);
            if (StringUtils.isNotBlank(value))
            {
                return value;
            }
        }
        return null;
    }

    private Map<String, String> flattenProperties(JSONObject item)
    {
        Map<String, String> result = new LinkedHashMap<>();
        flatten("", item, result);
        return result;
    }

    @SuppressWarnings("unchecked")
    private void flatten(String prefix, Object value, Map<String, String> result)
    {
        if (value == null)
        {
            return;
        }
        if (value instanceof JSONObject jsonObject)
        {
            for (Map.Entry<String, Object> entry : jsonObject.entrySet())
            {
                flatten(prefix(entry.getKey(), prefix), entry.getValue(), result);
            }
            return;
        }
        if (value instanceof Map<?, ?> map)
        {
            for (Map.Entry<?, ?> entry : map.entrySet())
            {
                flatten(prefix(String.valueOf(entry.getKey()), prefix), entry.getValue(), result);
            }
            return;
        }
        if (value instanceof JSONArray array)
        {
            result.put(prefix, JSON.toJSONString(array));
            return;
        }
        result.put(prefix, String.valueOf(value));
    }

    private String prefix(String key, String prefix)
    {
        return StringUtils.isBlank(prefix) ? key : prefix + "." + key;
    }

    private static class XiaomiSession
    {
        private HttpClient client;
        private String userId;
        private String serviceToken;
        private String ssecurity;
    }
}
