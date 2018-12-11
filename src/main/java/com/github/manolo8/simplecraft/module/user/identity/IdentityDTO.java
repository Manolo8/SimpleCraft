package com.github.manolo8.simplecraft.module.user.identity;

import com.github.manolo8.simplecraft.core.data.model.named.NamedDTO;
import com.github.manolo8.simplecraft.core.data.table.OnlyInsert;

public class IdentityDTO extends NamedDTO {

    @OnlyInsert
    public long mostSigBits;
    @OnlyInsert
    public long leastSigBits;
    @OnlyInsert
    public long firstLogin;

    public long mute;
    public long ban;
    public long onlineAllTime;
    public long lastLogin;
    public int skinId;
}
