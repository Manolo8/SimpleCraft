package com.github.manolo8.simplecraft.module.plot.member;

import com.github.manolo8.simplecraft.module.user.model.identity.BaseIdentity;

public class PlotMember extends BaseIdentity {

    private MemberFlag flags;

    public MemberFlag flags() {
        return flags;
    }

    public void setFlags(MemberFlag flags) {
        this.flags = flags;
    }
}
