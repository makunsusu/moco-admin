package com.moco.system.mapper;

import com.moco.system.domain.ShPlatformAccount;

public interface ShPlatformAccountMapper
{
    public ShPlatformAccount selectPlatformAccount();

    public int insertPlatformAccount(ShPlatformAccount account);

    public int updatePlatformAccount(ShPlatformAccount account);
}
