package com.moco.system.service;

import java.util.List;
import com.moco.system.domain.FinAsset;

public interface IFinAssetService
{
    public List<FinAsset> selectAssetList(FinAsset asset);

    public List<FinAsset> selectAssetOptions();

    public List<FinAsset> selectQuoteEnabledAssets();

    public FinAsset selectAssetById(Long assetId);

    public boolean checkAssetUnique(FinAsset asset);

    public int insertAsset(FinAsset asset);

    public int updateAsset(FinAsset asset);

    public int deleteAssetByIds(Long[] assetIds);
}
