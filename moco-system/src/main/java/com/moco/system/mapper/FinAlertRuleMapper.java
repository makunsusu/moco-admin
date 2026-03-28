package com.moco.system.mapper;

import java.util.List;
import com.moco.system.domain.FinAlertRule;

public interface FinAlertRuleMapper
{
    public List<FinAlertRule> selectAlertRuleList(FinAlertRule rule);

    public List<FinAlertRule> selectEnabledAlertRules();

    public FinAlertRule selectAlertRuleById(Long ruleId);

    public int insertAlertRule(FinAlertRule rule);

    public int updateAlertRule(FinAlertRule rule);

    public int deleteAlertRuleByIds(Long[] ruleIds);

    public int updateLastTriggeredTime(Long ruleId);
}
