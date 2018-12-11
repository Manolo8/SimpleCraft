package com.github.manolo8.simplecraft.module.group.permission;

import com.github.manolo8.simplecraft.core.data.model.base.BaseEntity;

public class Permission extends BaseEntity {

    private String key;
    private int value;

    public boolean match(String other) {
        return key.equals(other);
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        modified();
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}