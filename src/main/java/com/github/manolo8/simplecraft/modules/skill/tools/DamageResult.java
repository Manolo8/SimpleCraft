package com.github.manolo8.simplecraft.modules.skill.tools;

public class DamageResult {

    private double damage;

    public double getDamage() {
        return damage;
    }

    public void setDamage(double damage) {
        this.damage = damage;
    }

    public void add(double quantity) {
        this.damage += quantity;
    }

    public void subtract(double quantity) {
        this.damage -= quantity;
    }

    public void multiply(double multiply) {
        this.damage *= multiply;
    }
}
