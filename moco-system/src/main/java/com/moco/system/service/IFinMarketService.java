package com.moco.system.service;

import java.util.List;
import com.moco.system.domain.FinMarket;

public interface IFinMarketService
{
    public List<FinMarket> selectMarketList(FinMarket market);

    public FinMarket selectMarketById(Long marketId);

    public boolean checkMarketCodeUnique(FinMarket market);

    public boolean checkMarketNameUnique(FinMarket market);

    public int insertMarket(FinMarket market);

    public int updateMarket(FinMarket market);

    public int deleteMarketByIds(Long[] marketIds);
}
