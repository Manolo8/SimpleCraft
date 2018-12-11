package com.github.manolo8.simplecraft.core.commands.inventory;

import org.bukkit.inventory.ItemStack;

public class ClickInfo {

    private ItemStack cursor;
    private boolean left;

    public ClickInfo(ItemStack cursor, boolean left) {
        this.cursor = cursor;
        this.left = left;
    }

    public ItemStack getCursor() {
        return cursor;
    }

    public boolean isLeft() {
        return left;
    }

    public boolean isRight() {
        return !left;
    }
}
