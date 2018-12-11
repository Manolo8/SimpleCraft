package com.github.manolo8.simplecraft.module.skill.tools;

import java.util.Random;

public class DamageItemResult {

    private static Random random = new Random();
    private int original;
    private int added;
    private double chance;
    private int unbreaking;

    public DamageItemResult(int original) {
        this.original = original;
    }

    public void setChance(double chance) {
        this.chance = chance;
    }

    public void setUnbreaking(int unbreaking) {
        this.unbreaking = unbreaking;
    }

    public void increase(int quantity) {
        this.added += quantity;
    }

    public int buildDamage() {
        int damage = 0;

        //Passa pelo unbreaking (O original já passou pelo unbreaking)
        //E e passar pelo random, então adiciona para o original
        while (added > 0) {
            if (random.nextFloat() > 0.6f && random.nextInt(1 + unbreaking) == 0)
                original++;

            added--;
        }

        while (original > 0) {
            if (chance == 0 || random.nextDouble() > chance) {
                damage++;
            }
            original--;
        }

        return damage;
    }
}
