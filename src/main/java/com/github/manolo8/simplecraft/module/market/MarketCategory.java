package com.github.manolo8.simplecraft.module.market;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum MarketCategory {

    SWORDS(0, Material.GOLDEN_SWORD, "Espadas"),
    PICK_AXE(1, Material.GOLDEN_PICKAXE, "Picaretas"),
    HELMET(2, Material.GOLDEN_HELMET, "Helmos"),
    CHESTPLATE(3, Material.GOLDEN_CHESTPLATE, "Peitoral"),
    LEGGINGS(4, Material.GOLDEN_LEGGINGS, "Calças"),
    BOOTS(5, Material.GOLDEN_BOOTS, "Botas"),
    EGGS(6, Material.CHICKEN_SPAWN_EGG, "Ovos"),
    ENCHANTED(7, Material.ENCHANTED_BOOK, "Encantamentos"),
    FUEL(8, Material.COAL_BLOCK, "Combustíveis"),
    PRECIOUS(9, Material.DIAMOND, "Preciosos"),
    INGOT(10, Material.IRON_INGOT, "Barras"),
    BLOCKS(11, Material.END_STONE, "Blocos importantes"),
    MODULES(12, Material.REDSTONE, "Módulos"),
    OTHERS(13, Material.SUGAR, "Outros");

    public final int id;
    public final Material material;
    public final String name;

    MarketCategory(int id, Material material, String name) {
        this.id = id;
        this.material = material;
        this.name = name;
    }

    public static MarketCategory findById(int id) {
        for (MarketCategory category : values()) {
            if (category.id == id)
                return category;
        }

        return null;
    }

    public static MarketCategory findByItemStack(ItemStack item) {

        Material material = item.getType();

        switch (material) {

            case DIAMOND_SWORD:
            case IRON_SWORD:
            case GOLDEN_SWORD:
            case STONE_SWORD:
            case WOODEN_SWORD:
                return SWORDS;
            case DIAMOND_PICKAXE:
            case IRON_PICKAXE:
            case GOLDEN_PICKAXE:
            case STONE_PICKAXE:
            case WOODEN_PICKAXE:
                return PICK_AXE;
            case ENCHANTED_BOOK:
                return ENCHANTED;
            case CHAINMAIL_HELMET:
            case DIAMOND_HELMET:
            case IRON_HELMET:
            case GOLDEN_HELMET:
            case LEATHER_HELMET:
                return HELMET;
            case CHAINMAIL_CHESTPLATE:
            case DIAMOND_CHESTPLATE:
            case IRON_CHESTPLATE:
            case GOLDEN_CHESTPLATE:
            case LEATHER_CHESTPLATE:
                return CHESTPLATE;
            case CHAINMAIL_LEGGINGS:
            case DIAMOND_LEGGINGS:
            case IRON_LEGGINGS:
            case GOLDEN_LEGGINGS:
            case LEATHER_LEGGINGS:
                return LEGGINGS;
            case CHAINMAIL_BOOTS:
            case DIAMOND_BOOTS:
            case IRON_BOOTS:
            case GOLDEN_BOOTS:
            case LEATHER_BOOTS:
                return BOOTS;
            case CHICKEN_SPAWN_EGG:
            case COW_SPAWN_EGG:
            case SLIME_SPAWN_EGG:
                return EGGS;
            case COAL:
            case COAL_BLOCK:
            case COAL_ORE:
                return FUEL;
            case ENDER_PEARL:
            case CHORUS_FRUIT:
            case PRISMARINE_CRYSTALS:
            case EMERALD:
            case EMERALD_ORE:
            case EMERALD_BLOCK:
            case DIAMOND:
            case DIAMOND_ORE:
            case DIAMOND_BLOCK:
                return PRECIOUS;
            case GOLDEN_APPLE:
                if (item.getData().getData() == 1) return PRECIOUS;
                else return OTHERS;
            case GOLD_BLOCK:
            case GOLD_INGOT:
            case GOLD_ORE:
            case IRON_BLOCK:
            case IRON_INGOT:
            case IRON_ORE:
                return INGOT;
            case PRISMARINE:
            case NETHERRACK:
            case SAND:
            case END_STONE:
                return BLOCKS;
            default:
                return OTHERS;
        }
    }

    public MarketCategory next() {
        return id + 1 == values().length ? values()[0] : values()[id + 1];
    }

    public MarketCategory back() {
        return id == 0 ? values()[values().length - 1] : values()[id - 1];
    }
}
