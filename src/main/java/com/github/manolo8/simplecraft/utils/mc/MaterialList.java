package com.github.manolo8.simplecraft.utils.mc;

import org.bukkit.Material;

import java.util.HashSet;
import java.util.Set;

public class MaterialList {

    private final static Set<Material> interactables;

    static {

        interactables = new HashSet<>();

        interactables.add(Material.CHEST);
        interactables.add(Material.ENDER_CHEST);
        interactables.add(Material.TRAPPED_CHEST);

        interactables.add(Material.DARK_OAK_DOOR);
        interactables.add(Material.ACACIA_DOOR);
        interactables.add(Material.BIRCH_DOOR);
        interactables.add(Material.IRON_DOOR);
        interactables.add(Material.JUNGLE_DOOR);
        interactables.add(Material.OAK_DOOR);
        interactables.add(Material.SPRUCE_DOOR);

        interactables.add(Material.BIRCH_BUTTON);
        interactables.add(Material.ACACIA_BUTTON);
        interactables.add(Material.DARK_OAK_BUTTON);
        interactables.add(Material.JUNGLE_BUTTON);
        interactables.add(Material.OAK_BUTTON);
        interactables.add(Material.SPRUCE_BUTTON);
        interactables.add(Material.STONE_BUTTON);

        interactables.add(Material.ACACIA_FENCE_GATE);
        interactables.add(Material.BIRCH_FENCE_GATE);
        interactables.add(Material.JUNGLE_FENCE_GATE);
        interactables.add(Material.OAK_FENCE_GATE);
        interactables.add(Material.SPRUCE_FENCE_GATE);
        interactables.add(Material.DARK_OAK_FENCE_GATE);

        interactables.add(Material.ACACIA_TRAPDOOR);
        interactables.add(Material.BIRCH_TRAPDOOR);
        interactables.add(Material.DARK_OAK_TRAPDOOR);
        interactables.add(Material.JUNGLE_TRAPDOOR);
        interactables.add(Material.SPRUCE_TRAPDOOR);

        interactables.add(Material.DRAGON_EGG);
        interactables.add(Material.BEACON);
        interactables.add(Material.DROPPER);
        interactables.add(Material.FURNACE);
        interactables.add(Material.BREWING_STAND);
        interactables.add(Material.ANVIL);
        interactables.add(Material.CHIPPED_ANVIL);
        interactables.add(Material.DAMAGED_ANVIL);
        interactables.add(Material.HOPPER);
        interactables.add(Material.JUKEBOX);
        interactables.add(Material.NOTE_BLOCK);
        interactables.add(Material.TNT);
        interactables.add(Material.CRAFTING_TABLE);
        interactables.add(Material.LEVER);

        interactables.add(Material.BLACK_SHULKER_BOX);
        interactables.add(Material.BLUE_SHULKER_BOX);
        interactables.add(Material.BROWN_SHULKER_BOX);
        interactables.add(Material.CYAN_SHULKER_BOX);
        interactables.add(Material.GRAY_SHULKER_BOX);
        interactables.add(Material.GREEN_SHULKER_BOX);
        interactables.add(Material.LIGHT_BLUE_SHULKER_BOX);
        interactables.add(Material.LIGHT_GRAY_SHULKER_BOX);
        interactables.add(Material.LIME_SHULKER_BOX);
        interactables.add(Material.MAGENTA_SHULKER_BOX);
        interactables.add(Material.ORANGE_SHULKER_BOX);
        interactables.add(Material.PINK_SHULKER_BOX);
        interactables.add(Material.PURPLE_SHULKER_BOX);
        interactables.add(Material.RED_SHULKER_BOX);
        interactables.add(Material.SHULKER_BOX);
        interactables.add(Material.WHITE_SHULKER_BOX);
        interactables.add(Material.YELLOW_SHULKER_BOX);

    }

    public static boolean isInteractable(Material material) {
        return interactables.contains(material);
    }

    public static Material fromId(int id) {
        return Material.values()[id];
    }

    public static int toId(Material material) {
        Material[] values = Material.values();

        for (int i = 0; i < values.length; i++) {
            if (values[i] == material) return i;
        }

        return 0;
    }
}
