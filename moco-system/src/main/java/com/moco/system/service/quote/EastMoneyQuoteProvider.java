package com.moco.system.service.quote;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Component;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.moco.common.exception.ServiceException;
import com.moco.common.utils.StringUtils;
import com.moco.common.utils.http.HttpUtils;
import com.moco.system.domain.FinAsset;
import com.moco.system.domain.FinKlineSnapshot;
import com.moco.system.domain.FinQuoteSnapshot;

@Component
public class EastMoneyQuoteProvider implements QuoteProvider
{
    private static final SimpleDateFormat KLINE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);

    @Override
    public String getProviderCode()
    {
        return "eastmoney";
    }

    @Override
    public boolean supports(FinAsset asset)
    {
        return "STOCK".equalsIgnoreCase(asset.getAssetType()) || "FUND".equalsIgnoreCase(asset.getAssetType());
    }

    @Override
    public FinQuoteSnapshot query(FinAsset asset)
    {
        if ("FUND".equalsIgnoreCase(asset.getAssetType()))
        {
            return queryFund(asset);
        }
        return queryStock(asset);
    }

    @Override
    public boolean supportsKline(FinAsset asset)
    {
        return "STOCK".equalsIgnoreCase(asset.getAssetType()) || "FUND".equalsIgnoreCase(asset.getAssetType());
    }

    @Override
    public List<FinKlineSnapshot> queryDailyKlines(FinAsset asset, int limit)
    {
        String code = StringUtils.isNotBlank(asset.getQuoteCode()) ? asset.getQuoteCode() : asset.getAssetCode();
        String response = HttpUtils.sendGet("https://push2his.eastmoney.com/api/qt/stock/kline/get",
            "secid=" + resolveSecid(asset, code)
                + "&fields1=f1,f2,f3,f4,f5,f6"
                + "&fields2=f51,f52,f53,f54,f55,f56,f57,f58,f59,f60,f61"
                + "&klt=101&fqt=1&beg=20200101&end=20500101");
        if (StringUtils.isBlank(response))
        {
            return new ArrayList<>();
        }
        JSONObject root = JSON.parseObject(response);
        JSONObject data = root == null ? null : root.getJSONObject("data");
        if (data == null || data.getJSONArray("klines") == null)
        {
            return new ArrayList<>();
        }
        List<String> rawKlines = data.getJSONArray("klines").toJavaList(String.class);
        int fromIndex = Math.max(rawKlines.size() - Math.max(limit, 1), 0);
        List<FinKlineSnapshot> result = new ArrayList<>();
        for (String raw : rawKlines.subList(fromIndex, rawKlines.size()))
        {
            String[] fields = raw.split(",");
            if (fields.length < 11)
            {
                continue;
            }
            FinKlineSnapshot snapshot = new FinKlineSnapshot();
            snapshot.setAssetId(asset.getAssetId());
            snapshot.setAssetCode(asset.getAssetCode());
            snapshot.setAssetName(asset.getAssetName());
            snapshot.setTradeDate(parseTradeDate(fields[0]));
            snapshot.setOpenPrice(scale(parseDecimal(fields[1]), 4));
            snapshot.setClosePrice(scale(parseDecimal(fields[2]), 4));
            snapshot.setHighPrice(scale(parseDecimal(fields[3]), 4));
            snapshot.setLowPrice(scale(parseDecimal(fields[4]), 4));
            snapshot.setVolume(scale(parseDecimal(fields[5]), 4));
            snapshot.setTurnoverAmount(scale(parseDecimal(fields[6]), 2));
            snapshot.setAmplitude(scale(percentToDecimal(fields[7]), 4));
            snapshot.setChangeRate(scale(percentToDecimal(fields[8]), 4));
            snapshot.setChangeAmount(scale(parseDecimal(fields[9]), 4));
            snapshot.setTurnoverRate(scale(percentToDecimal(fields[10]), 4));
            snapshot.setProviderCode(getProviderCode());
            snapshot.setRawPayload(raw);
            result.add(snapshot);
        }
        return result;
    }

    private FinQuoteSnapshot queryFund(FinAsset asset)
    {
        String code = StringUtils.isNotBlank(asset.getQuoteCode()) ? asset.getQuoteCode() : asset.getAssetCode();
        String response = HttpUtils.sendGet("https://fundgz.1234567.com.cn/js/" + code + ".js");
        if (StringUtils.isBlank(response))
        {
            throw new ServiceException("基金行情获取失败");
        }
        String json = StringUtils.substringBetween(response, "(", ")");
        JSONObject obj = JSON.parseObject(json);
        if (obj == null)
        {
            throw new ServiceException("基金行情解析失败");
        }
        BigDecimal lastPrice = obj.getBigDecimal("gsz");
        BigDecimal prevClose = obj.getBigDecimal("dwjz");
        BigDecimal changeRate = obj.getBigDecimal("gszzl");
        BigDecimal changeAmount = lastPrice.subtract(prevClose).setScale(4, RoundingMode.HALF_UP);
        FinQuoteSnapshot snapshot = baseSnapshot(asset);
        snapshot.setLastPrice(scale(lastPrice, 4));
        snapshot.setPrevClosePrice(scale(prevClose, 4));
        snapshot.setOpenPrice(scale(prevClose, 4));
        snapshot.setChangeRate(scale(changeRate.divide(new BigDecimal("100"), 6, RoundingMode.HALF_UP), 4));
        snapshot.setChangeAmount(scale(changeAmount, 4));
        snapshot.setRawPayload(response);
        return snapshot;
    }

    private FinQuoteSnapshot queryStock(FinAsset asset)
    {
        String code = StringUtils.isNotBlank(asset.getQuoteCode()) ? asset.getQuoteCode() : asset.getAssetCode();
        String secid = resolveSecid(asset, code);
        String response = HttpUtils.sendGet("https://push2.eastmoney.com/api/qt/stock/get",
            "secid=" + secid + "&fields=f43,f46,f57,f58,f60,f169,f170");
        if (StringUtils.isBlank(response))
        {
            throw new ServiceException("股票行情获取失败");
        }
        JSONObject root = JSON.parseObject(response);
        JSONObject data = root == null ? null : root.getJSONObject("data");
        if (data == null)
        {
            throw new ServiceException("股票行情解析失败");
        }
        FinQuoteSnapshot snapshot = baseSnapshot(asset);
        snapshot.setLastPrice(scale(data.getBigDecimal("f43").divide(new BigDecimal("100"), 6, RoundingMode.HALF_UP), 4));
        snapshot.setOpenPrice(scale(data.getBigDecimal("f46").divide(new BigDecimal("100"), 6, RoundingMode.HALF_UP), 4));
        snapshot.setPrevClosePrice(scale(data.getBigDecimal("f60").divide(new BigDecimal("100"), 6, RoundingMode.HALF_UP), 4));
        snapshot.setChangeAmount(scale(data.getBigDecimal("f169").divide(new BigDecimal("100"), 6, RoundingMode.HALF_UP), 4));
        snapshot.setChangeRate(scale(data.getBigDecimal("f170").divide(new BigDecimal("100"), 6, RoundingMode.HALF_UP), 4));
        snapshot.setRawPayload(response);
        return snapshot;
    }

    private FinQuoteSnapshot baseSnapshot(FinAsset asset)
    {
        FinQuoteSnapshot snapshot = new FinQuoteSnapshot();
        snapshot.setAssetId(asset.getAssetId());
        snapshot.setAssetCode(asset.getAssetCode());
        snapshot.setAssetName(asset.getAssetName());
        snapshot.setSnapshotDate(new Date());
        snapshot.setQuoteTime(new Date());
        snapshot.setProviderCode(getProviderCode());
        return snapshot;
    }

    private String resolveSecid(FinAsset asset, String code)
    {
        if ("SSE".equalsIgnoreCase(asset.getMarketCode()) || code.startsWith("5") || code.startsWith("6") || code.startsWith("9"))
        {
            return "1." + code;
        }
        if ("BSE".equalsIgnoreCase(asset.getMarketCode()))
        {
            return "0." + code;
        }
        return "0." + code;
    }

    private BigDecimal scale(BigDecimal value, int scale)
    {
        return (value == null ? BigDecimal.ZERO : value).setScale(scale, RoundingMode.HALF_UP);
    }

    private BigDecimal parseDecimal(String value)
    {
        if (StringUtils.isBlank(value) || "-".equals(value))
        {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(value);
    }

    private BigDecimal percentToDecimal(String value)
    {
        return parseDecimal(value).divide(new BigDecimal("100"), 6, RoundingMode.HALF_UP);
    }

    private Date parseTradeDate(String value)
    {
        try
        {
            return KLINE_DATE_FORMAT.parse(value);
        }
        catch (ParseException e)
        {
            throw new ServiceException("K线日期解析失败");
        }
    }
}
