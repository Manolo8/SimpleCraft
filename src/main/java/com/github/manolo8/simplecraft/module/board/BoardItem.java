package com.github.manolo8.simplecraft.module.board;

import com.github.manolo8.simplecraft.core.data.model.base.BaseEntity;
import com.github.manolo8.simplecraft.core.placeholder.StringProvider;

public class BoardItem extends BaseEntity implements StringProvider {

    int priority;
    String value;

    //======================================================
    //=====================ENCAPSULATION====================
    //======================================================
    public int getPriority() {
        return priority;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
        modified();
    }
    //======================================================
    //====================_ENCAPSULATION====================
    //======================================================


    //======================================================
    //========================METHODS=======================
    //======================================================
    @Override
    public long lastModified() {
        return getLastModified();
    }

    @Override
    public String value() {
        return value;
    }

    //======================================================
    //=======================_METHODS=======================
    //======================================================
}
