package com.github.manolo8.simplecraft.module.user.model.identity;

import com.github.manolo8.simplecraft.core.data.model.base.DTO;
import com.github.manolo8.simplecraft.core.data.table.OnlyInsert;

public class BaseIdentityDTO extends DTO {

    @OnlyInsert
    private int identityId;

    public int getIdentityId() {
        return identityId;
    }

    public void setIdentityId(int identityId) {
        this.identityId = identityId;
    }
}
