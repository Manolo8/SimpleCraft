package com.github.manolo8.simplecraft.module.machine.fuel;

import com.github.manolo8.simplecraft.core.data.model.named.NamedEntity;

public class Fuel extends NamedEntity {

    private int level;
    private double amplifier;
    private int time;

    //======================================================
    //=====================ENCAPSULATION====================
    //======================================================

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
        modified();
    }

    public double getAmplifier() {
        return amplifier;
    }

    public void setAmplifier(double amplifier) {
        this.amplifier = amplifier;
        modified();
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
        modified();
    }

    //======================================================
    //====================_ENCAPSULATION====================
    //======================================================
}
