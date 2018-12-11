package com.github.manolo8.simplecraft.utils.mc;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class InventoryUtils {

    public static int addItemTo(Inventory inventory, ItemStack item) {
        return addItemTo(inventory, item, item.getAmount());
    }

    public static int addItemTo(Inventory inventory, ItemStack item, int amount) {

        int stackSize = item.getMaxStackSize();
        int max = (inventory instanceof PlayerInventory) ? 36 : inventory.getSize();

        ItemStack clone = item.clone();

        for (int i = 0; i < max; i++) {
            ItemStack stack = inventory.getItem(i);

            if (stack == null) {
                if (amount >= stackSize) {
                    clone.setAmount(stackSize);
                    inventory.setItem(i, clone);
                    amount -= stackSize;
                    continue;
                }
                clone.setAmount(amount);
                inventory.setItem(i, clone);
                return 0;
            } else if (stack.isSimilar(item)) {
                int stackAmount = stack.getAmount();
                int free = stackSize - stackAmount;
                if (amount > free) {
                    stack.setAmount(stackAmount + free);
                    amount -= free;
                    continue;
                }
                stack.setAmount(stackAmount + amount);
                return 0;
            }
        }

        return amount;
    }

    public static boolean isFull(Inventory inventory, ItemStack is) {
        if (is == null) is = new ItemStack(Material.AIR);

        int stackSize = is.getMaxStackSize();

        int max = inventory.getSize();

        for (int i = 0; i < max; i++) {
            ItemStack stack = inventory.getItem(i);
            if (stack == null) return false;
            else if (stack.isSimilar(is) && (stackSize - stack.getAmount()) > 0) return false;
        }

        return true;
    }

    public static int getFreeSpace(Inventory inventory, ItemStack item) {

        int stackSize = item.getMaxStackSize();
        int max = (inventory instanceof PlayerInventory) ? 36 : inventory.getSize();
        int available = 0;

        for (int i = 0; i < max; i++) {
            ItemStack loop = inventory.getItem(i);
            if (loop == null) {
                available += stackSize;
            } else if (loop.isSimilar(item)) {
                available += stackSize - loop.getAmount();
            }
        }

        return available;
    }

    public static int getItemQuantity(Inventory inventory, ItemStack item) {

        int quantity = 0;
        int max = inventory.getSize();

        for (int i = 0; i < max; i++) {
            ItemStack loop = inventory.getItem(i);
            if (loop == null) continue;
            if (loop.isSimilar(item)) {
                quantity += loop.getAmount();
            }
        }

        return quantity;
    }

    public static void removeItems(Inventory inventory, ItemStack item, int amount) {

        int max = inventory.getSize();

        for (int i = 0; i < max; i++) {
            ItemStack loop = inventory.getItem(i);
            if (loop == null || !loop.isSimilar(item)) continue;
            int haveAmount = loop.getAmount();
            if (haveAmount > amount) {
                loop.setAmount(haveAmount - amount);
                break;
            } else {
                inventory.clear(i);
                amount -= haveAmount;
            }
        }
    }

    public static void removeItems(Inventory inventory, ItemStack[] items) {

        if (inventory == null) return;

        int[] quantity = new int[items.length];

        for (int i = 0; i < items.length; i++)
            quantity[i] = items[i].getAmount();

        int max = inventory.getSize();

        for (int a = 0; a < max; a++) {
            ItemStack stack = inventory.getItem(a);

            if (stack == null) continue;

            for (int i = 0; i < items.length; i++) {
                ItemStack loop = items[i];
                if (quantity[i] != 0) {
                    if (stack.isSimilar(loop)) {
                        int haveAmount = stack.getAmount();
                        if (haveAmount > quantity[i]) {
                            stack.setAmount(haveAmount - quantity[i]);
                            quantity[i] = 0;
                        } else {
                            //FIX
                            stack.setAmount(0);
                            quantity[i] = quantity[i] - haveAmount;
                        }
                    }
                }
            }
        }
    }

    public static boolean hasItems(Inventory inventory, ItemStack[] items) {
        if (inventory == null) return false;

        int[] quantity = new int[items.length];

        for (int i = 0; i < items.length; i++) {
            quantity[i] = items[i].getAmount();
        }

        int max = inventory.getSize();

        for (int c = 0; c < max; c++) {
            ItemStack stack = inventory.getItem(c);
            if (stack == null) continue;

            for (int i = 0; i < items.length; i++) {
                ItemStack loop = items[i];
                if (!stack.isSimilar(loop)) continue;
                if (quantity[i] == 0) continue;
                int haveAmount = stack.getAmount();
                int amount = loop.getAmount();
                if (haveAmount > amount) {
                    quantity[i] = 0;
                } else {
                    quantity[i] = amount - haveAmount;
                }
            }
        }

        for (int i : quantity) if (i > 0) return false;

        return true;
    }

    public static void dropItems(Location location, Inventory inventory) {
        World world = location.getWorld();

        ItemStack[] items = inventory.getContents();

        for (ItemStack stack : items) {
            if (stack == null) continue;
            world.dropItemNaturally(location, stack);
            stack.setAmount(0);
        }
    }
}
