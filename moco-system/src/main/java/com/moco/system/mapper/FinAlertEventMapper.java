package com.moco.system.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.moco.system.domain.FinAlertEvent;

public interface FinAlertEventMapper
{
    public List<FinAlertEvent> selectAlertEventList(FinAlertEvent event);

    public int insertAlertEvent(FinAlertEvent event);

    public int countTodayEventByRuleId(Long ruleId);

    public int updateAlertEventStatus(@Param("eventId") Long eventId, @Param("status") String status);
}
