package com.moco.system.mapper;

import java.util.List;
import com.moco.system.domain.FinMarket;

public interface FinMarketMapper
{
    public List<FinMarket> selectMarketList(FinMarket market);

    public FinMarket selectMarketById(Long marketId);

    public FinMarket checkMarketCodeUnique(String marketCode);

    public FinMarket checkMarketNameUnique(String marketName);

    public int insertMarket(FinMarket market);

    public int updateMarket(FinMarket market);

    public int deleteMarketByIds(Long[] marketIds);
}
