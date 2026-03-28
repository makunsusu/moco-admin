package com.moco.system.mapper;

import java.util.Date;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.moco.system.domain.FinKlineSnapshot;

public interface FinKlineSnapshotMapper
{
    public int batchUpsertKlineSnapshots(@Param("list") List<FinKlineSnapshot> list);

    public List<FinKlineSnapshot> selectLatestKlinesByAssetId(@Param("assetId") Long assetId, @Param("limit") Integer limit);

    public Date selectMaxTradeDateByAssetId(Long assetId);
}
