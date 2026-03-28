package com.moco.system.service;

import java.util.List;
import com.moco.system.domain.FinAlertEvent;
import com.moco.system.domain.FinAlertRule;

public interface IFinAlertService
{
    public List<FinAlertRule> selectAlertRuleList(FinAlertRule rule);

    public FinAlertRule selectAlertRuleById(Long ruleId);

    public int insertAlertRule(FinAlertRule rule);

    public int updateAlertRule(FinAlertRule rule);

    public int deleteAlertRuleByIds(Long[] ruleIds);

    public List<FinAlertEvent> selectAlertEventList(FinAlertEvent event);

    public int updateAlertEventStatus(Long eventId, String status);
}
