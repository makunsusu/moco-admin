package com.moco.system.service;

import com.moco.system.domain.vo.FinOverviewVO;

public interface IFinOverviewService
{
    public FinOverviewVO getOverview(Long familyId, Long memberId, Long accountId);
}
