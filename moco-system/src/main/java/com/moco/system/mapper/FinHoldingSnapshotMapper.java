package com.moco.system.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.moco.system.domain.FinHoldingSnapshot;

public interface FinHoldingSnapshotMapper
{
    public int insertHoldingSnapshot(FinHoldingSnapshot snapshot);

    public List<FinHoldingSnapshot> selectLatestHoldingSnapshots(@Param("familyId") Long familyId, @Param("memberId") Long memberId, @Param("accountId") Long accountId);
}
