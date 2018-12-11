package com.github.manolo8.simplecraft.module.user.model.identity;

import com.github.manolo8.simplecraft.core.data.model.base.BaseEntity;
import com.github.manolo8.simplecraft.module.user.identity.Identity;

public class BaseIdentity extends BaseEntity {

    protected Identity identity;

    //======================================================
    //====================ENCAPSULATION=====================
    //======================================================
    public Identity getIdentity() {
        return identity;
    }

    public void setIdentity(Identity identity) {
        this.identity = identity;
    }
    //======================================================
    //===================_ENCAPSULATION=====================
    //======================================================
}
