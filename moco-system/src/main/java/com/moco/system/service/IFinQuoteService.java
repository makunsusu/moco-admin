package com.moco.system.service;

import java.util.List;
import java.util.Map;
import com.moco.system.domain.FinQuoteSnapshot;

public interface IFinQuoteService
{
    public Map<String, Object> refreshQuotes(Long[] assetIds);

    public List<FinQuoteSnapshot> selectLatestQuotes();
}
