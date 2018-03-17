package com.github.manolo8.simplecraft.utils;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class InventoryUtils {

    public static void addItems(Inventory inventory, ItemStack is, int amount) {
        int stackSize = is.getMaxStackSize();
        ItemStack[] contents = inventory.getContents();
        int i = (inventory instanceof PlayerInventory ? 9 : 0);

        for (; i < contents.length; i++) {
            ItemStack stack = contents[i];

            if (stack == null) {
                if (amount > stackSize) {
                    ItemStack temp = is.clone();
                    temp.setAmount(stackSize);
                    inventory.setItem(i, temp);
                    amount -= stackSize;
                    continue;
                }
                ItemStack temp = is.clone();
                temp.setAmount(amount);
                inventory.setItem(i, temp);
                return;
            }

            int stackAmount = stack.getAmount();
            int free = stackSize - stackAmount;

            if (stack.isSimilar(is) && free > 0) {
                if (amount > free) {
                    stack.setAmount(stackAmount + free);
                    amount -= free;
                    continue;
                }
                stack.setAmount(stackAmount + amount);
                return;
            }
        }
        return;
    }

    public static int getFreeSpace(Inventory inventory, ItemStack itemStack) {
        int stackSize = itemStack.getMaxStackSize();
        int available = 0;
        int i = (inventory instanceof PlayerInventory ? 9 : 0);

        ItemStack[] contents = inventory.getContents();
        for (; i < contents.length; i++) {
            ItemStack loop = contents[i];
            if (loop == null) {
                available += stackSize;
                continue;
            }
            if (loop.isSimilar(itemStack)) {
                available += stackSize - itemStack.getAmount();
            }
        }

        return available;
    }

    public static int getQuantity(Inventory inventory, ItemStack itemStack) {
        int quantity = 0;
        ItemStack[] contents = inventory.getContents();
        int i = (inventory instanceof PlayerInventory ? 9 : 0);

        for (; i < contents.length; i++) {
            ItemStack loop = contents[i];
            if (loop == null) continue;
            if (loop.isSimilar(itemStack)) {
                quantity += loop.getAmount();
            }
        }

        return quantity;
    }

    public static void removeItems(Inventory inventory, ItemStack itemStack, int amount) {
        ItemStack[] contents = inventory.getContents();
        int i = (inventory instanceof PlayerInventory ? 9 : 0);

        for (; i < contents.length; i++) {
            ItemStack loop = contents[i];
            if (loop == null || !loop.isSimilar(itemStack)) continue;
            int haveAmount = loop.getAmount();
            if (haveAmount > amount) {
                loop.setAmount(haveAmount - amount);
                amount = 0;
                break;
            } else {
                inventory.clear(i);
                amount -= haveAmount;
            }
        }
    }
}
