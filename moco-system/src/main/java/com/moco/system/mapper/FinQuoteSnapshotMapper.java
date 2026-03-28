package com.moco.system.mapper;

import java.math.BigDecimal;
import java.util.List;
import com.moco.system.domain.FinQuoteSnapshot;

public interface FinQuoteSnapshotMapper
{
    public int insertQuoteSnapshot(FinQuoteSnapshot snapshot);

    public List<FinQuoteSnapshot> selectLatestQuotes();

    public FinQuoteSnapshot selectLatestQuoteByAssetId(Long assetId);

    public BigDecimal selectMaxLastPriceByAssetId(Long assetId);
}
