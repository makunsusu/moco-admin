package com.moco.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.moco.system.domain.FinAlertEvent;
import com.moco.system.domain.FinAlertRule;
import com.moco.system.mapper.FinAlertEventMapper;
import com.moco.system.mapper.FinAlertRuleMapper;
import com.moco.system.service.IFinAlertService;

@Service
public class FinAlertServiceImpl implements IFinAlertService
{
    @Autowired
    private FinAlertRuleMapper alertRuleMapper;

    @Autowired
    private FinAlertEventMapper alertEventMapper;

    @Override
    public List<FinAlertRule> selectAlertRuleList(FinAlertRule rule)
    {
        return alertRuleMapper.selectAlertRuleList(rule);
    }

    @Override
    public FinAlertRule selectAlertRuleById(Long ruleId)
    {
        return alertRuleMapper.selectAlertRuleById(ruleId);
    }

    @Override
    public int insertAlertRule(FinAlertRule rule)
    {
        return alertRuleMapper.insertAlertRule(rule);
    }

    @Override
    public int updateAlertRule(FinAlertRule rule)
    {
        return alertRuleMapper.updateAlertRule(rule);
    }

    @Override
    public int deleteAlertRuleByIds(Long[] ruleIds)
    {
        return alertRuleMapper.deleteAlertRuleByIds(ruleIds);
    }

    @Override
    public List<FinAlertEvent> selectAlertEventList(FinAlertEvent event)
    {
        return alertEventMapper.selectAlertEventList(event);
    }

    @Override
    public int updateAlertEventStatus(Long eventId, String status)
    {
        return alertEventMapper.updateAlertEventStatus(eventId, status);
    }
}
