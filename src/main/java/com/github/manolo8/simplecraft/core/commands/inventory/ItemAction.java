package com.github.manolo8.simplecraft.core.commands.inventory;

import org.bukkit.inventory.ItemStack;

public interface ItemAction {

    int getIndex();

    void setIndex(int index);

    ItemStack getItemStack();

    Action getAction();
}
