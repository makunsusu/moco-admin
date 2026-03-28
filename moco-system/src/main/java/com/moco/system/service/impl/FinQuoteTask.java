package com.moco.system.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.moco.system.service.IFinQuoteService;

@Component("finQuoteTask")
public class FinQuoteTask
{
    @Autowired
    private IFinQuoteService quoteService;

    public void refreshLatestQuotes()
    {
        quoteService.refreshQuotes(null);
    }
}
