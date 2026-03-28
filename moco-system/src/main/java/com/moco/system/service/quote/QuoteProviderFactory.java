package com.moco.system.service.quote;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.moco.system.domain.FinAsset;

@Component
public class QuoteProviderFactory
{
    @Autowired
    private List<QuoteProvider> providers;

    public QuoteProvider getProvider(FinAsset asset)
    {
        for (QuoteProvider provider : providers)
        {
            if (provider.supports(asset))
            {
                return provider;
            }
        }
        return null;
    }
}
