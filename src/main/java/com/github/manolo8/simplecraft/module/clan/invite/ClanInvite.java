package com.github.manolo8.simplecraft.module.clan.invite;

import com.github.manolo8.simplecraft.core.data.model.base.BaseEntity;
import com.github.manolo8.simplecraft.module.clan.Clan;
import com.github.manolo8.simplecraft.module.user.identity.Identity;

public class ClanInvite extends BaseEntity {

    Clan clan;
    Identity invited;

    //======================================================
    //====================ENCAPSULATION=====================
    //======================================================
    public Clan getClan() {
        return clan;
    }

    public Identity getInvited() {
        return invited;
    }
    //======================================================
    //===================_ENCAPSULATION=====================
    //======================================================
}
