package com.github.manolo8.simplecraft.modules.skill;

import com.github.manolo8.simplecraft.data.model.BaseEntity;

public abstract class Skill extends BaseEntity implements Cloneable {

    protected int level;

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
        setNeedSave(true);
    }

    public abstract int getType();

    public abstract Level getLevelHandler();

    public abstract boolean hasNextLevel();
}
