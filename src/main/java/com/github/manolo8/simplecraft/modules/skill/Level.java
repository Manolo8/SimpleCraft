package com.github.manolo8.simplecraft.modules.skill;

public class Level {

    protected String[] info;
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
}
