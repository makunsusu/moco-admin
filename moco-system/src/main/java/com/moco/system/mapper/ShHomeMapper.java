package com.moco.system.mapper;

import java.util.List;
import com.moco.system.domain.ShHome;

public interface ShHomeMapper
{
    public List<ShHome> selectHomeList(ShHome home);

    public int insertHome(ShHome home);

    public int updateHome(ShHome home);

    public ShHome selectHomeByCloudHomeId(String cloudHomeId);

    public int deleteAllHomes();
}
