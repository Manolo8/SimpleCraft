package com.github.manolo8.simplecraft.utils;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public class ItemStackUtils {

    public static ItemStack create(Material material, DyeColor color, String title) {
        ItemStack itemStack = new ItemStack(material, (short) 1, color.getWoolData());
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(title);
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public static ItemStack create(Material material, String title) {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(title);
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public static ItemStack create(Material material, String title, String lore) {
        return create(material.getId(), title, lore);
    }

    public static ItemStack create(int material, String title, String lore) {
        return create(Material.getMaterial(material), title, Arrays.asList(lore.split(",")));
    }

    public static ItemStack create(Material material, String title, List<String> lore) {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(title);
        meta.setLore(lore);
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public static String loreToString(List<String> lore) {
        if (lore == null) return "";
        StringBuilder builder = new StringBuilder();
        for (String string : lore) builder.append(string).append(",");

        builder.setLength(builder.length() - 1);

        return builder.toString();
    }

    public static ItemStack create(Material material, String title, String[] info) {
        return create(material, title, Arrays.asList(info));
    }
}
