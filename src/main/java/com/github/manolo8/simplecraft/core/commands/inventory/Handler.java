package com.github.manolo8.simplecraft.core.commands.inventory;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public interface Handler<E extends View> {

    void prepare(InventoryView main, E view);

    /**
     * @param index
     * @param cursor
     * @return true to cancel click
     */
    void click(int index, ItemStack cursor, boolean left);

    void update(boolean items, boolean pagination);

    void open();
}
