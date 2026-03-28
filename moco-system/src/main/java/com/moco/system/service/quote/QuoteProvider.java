package com.moco.system.service.quote;

import com.moco.system.domain.FinAsset;
import com.moco.system.domain.FinQuoteSnapshot;

public interface QuoteProvider
{
    public String getProviderCode();

    public boolean supports(FinAsset asset);

    public FinQuoteSnapshot query(FinAsset asset);
}
