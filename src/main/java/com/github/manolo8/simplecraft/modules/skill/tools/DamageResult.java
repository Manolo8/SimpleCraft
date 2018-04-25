package com.github.manolo8.simplecraft.modules.skill.tools;

public class DamageResult {

    private double damage;
    private boolean cancelled;

    public DamageResult(double damage) {
        this.damage = damage;
        this.cancelled = false;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

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
