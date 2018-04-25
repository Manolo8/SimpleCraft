package com.github.manolo8.simplecraft.modules.skill;

public class Level<T extends Skill> implements Cloneable {

    protected String[] info;
    protected T skill;
    private int upgradeAmount;

    public Level(int upgradeAmount) {
        this.upgradeAmount = upgradeAmount;
    }

    public String[] getInfo() {
        return info;
    }

    public int getUpgradeAmount() {
        return upgradeAmount;
    }

    public void setSkill(T skill) {
        this.skill = skill;
    }

    @Override
    public Level clone() {
        try {
            return (Level) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }
}
