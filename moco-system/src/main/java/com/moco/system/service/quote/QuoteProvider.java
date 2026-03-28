package com.moco.system.service.quote;

import java.util.Collections;
import java.util.List;
import com.moco.system.domain.FinAsset;
import com.moco.system.domain.FinKlineSnapshot;
import com.moco.system.domain.FinQuoteSnapshot;

public interface QuoteProvider
{
    public String getProviderCode();

    public boolean supports(FinAsset asset);

    public FinQuoteSnapshot query(FinAsset asset);

    default boolean supportsKline(FinAsset asset)
    {
        return supports(asset);
    }

    default List<FinKlineSnapshot> queryDailyKlines(FinAsset asset, int limit)
    {
        return Collections.emptyList();
    }
}
