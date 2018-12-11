package com.github.manolo8.simplecraft.module.skill.tools;

public interface ItemDamage {

    default void onReceive(DamageItemResult result) {
    }

    default void onGive(DamageItemResult result) {
    }

}
