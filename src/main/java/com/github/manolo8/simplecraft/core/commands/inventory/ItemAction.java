package com.github.manolo8.simplecraft.core.commands.inventory;

import org.bukkit.inventory.ItemStack;

public abstract class ItemAction {

    public boolean state;

    public abstract int index();

    public abstract ItemAction setIndex(int index);

    public ItemAction setIndex(int y, int x) {
        setIndex(((--y * 9) + --x));
        return this;
    }

    public abstract  ItemStack getItemStack();

    public abstract Action getAction();
}
