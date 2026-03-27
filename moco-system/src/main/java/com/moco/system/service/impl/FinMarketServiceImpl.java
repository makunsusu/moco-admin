package com.moco.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.moco.common.constant.UserConstants;
import com.moco.common.utils.StringUtils;
import com.moco.system.domain.FinMarket;
import com.moco.system.mapper.FinMarketMapper;
import com.moco.system.service.IFinMarketService;

@Service
public class FinMarketServiceImpl implements IFinMarketService
{
    @Autowired
    private FinMarketMapper marketMapper;

    @Override
    public List<FinMarket> selectMarketList(FinMarket market)
    {
        return marketMapper.selectMarketList(market);
    }

    @Override
    public FinMarket selectMarketById(Long marketId)
    {
        return marketMapper.selectMarketById(marketId);
    }

    @Override
    public boolean checkMarketCodeUnique(FinMarket market)
    {
        Long marketId = StringUtils.isNull(market.getMarketId()) ? -1L : market.getMarketId();
        FinMarket info = marketMapper.checkMarketCodeUnique(market.getMarketCode());
        if (StringUtils.isNotNull(info) && info.getMarketId().longValue() != marketId.longValue())
        {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    @Override
    public boolean checkMarketNameUnique(FinMarket market)
    {
        Long marketId = StringUtils.isNull(market.getMarketId()) ? -1L : market.getMarketId();
        FinMarket info = marketMapper.checkMarketNameUnique(market.getMarketName());
        if (StringUtils.isNotNull(info) && info.getMarketId().longValue() != marketId.longValue())
        {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    @Override
    public int insertMarket(FinMarket market)
    {
        return marketMapper.insertMarket(market);
    }

    @Override
    public int updateMarket(FinMarket market)
    {
        return marketMapper.updateMarket(market);
    }

    @Override
    public int deleteMarketByIds(Long[] marketIds)
    {
        return marketMapper.deleteMarketByIds(marketIds);
    }
}
