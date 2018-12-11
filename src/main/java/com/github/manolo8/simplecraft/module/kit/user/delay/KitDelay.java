package com.github.manolo8.simplecraft.module.kit.user.delay;

import com.github.manolo8.simplecraft.core.data.model.base.BaseEntity;
import com.github.manolo8.simplecraft.module.kit.Kit;
import com.github.manolo8.simplecraft.module.user.identity.Identity;

public class KitDelay extends BaseEntity {

    private Identity owner;
    private Kit kit;
    private long lastUse;

    //======================================================
    //=====================ENCAPSULATION====================
    //======================================================
    public Identity getOwner() {
        return owner;
    }

    public void setOwner(Identity owner) {
        this.owner = owner;
    }

    public Kit getKit() {
        return kit;
    }

    public void setKit(Kit kit) {
        this.kit = kit;
    }

    public long getLastUse() {
        return lastUse;
    }

    public void setLastUse(long lastUse) {
        this.lastUse = lastUse;
    }
    //======================================================
    //====================_ENCAPSULATION====================
    //======================================================


    //======================================================
    //========================METHODS=======================
    //======================================================
    public long getWaitTime() {
        return (kit.getDelay() - (System.currentTimeMillis() - lastUse));
    }

    public boolean canUse() {
        return System.currentTimeMillis() - lastUse > kit.getDelay();
    }

    public boolean use() {
        if (canUse()) {
            lastUse = System.currentTimeMillis();
            modified();
            return true;
        }

        return false;
    }
    //======================================================
    //=======================_METHODS=======================
    //======================================================
}