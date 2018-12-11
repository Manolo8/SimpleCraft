package com.github.manolo8.simplecraft.utils.def;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class MachineUtils {

    public static List<String> getUpdateFormatted(List<ItemStack> cost) {
        List<String> list = new ArrayList<>();

        for (ItemStack stack : cost) {
            list.add(stack.getType().name() + " Quantia: " + stack.getAmount());
        }

        return list;
    }
}
