package com.github.manolo8.simplecraft.utils.mc;

import com.github.manolo8.simplecraft.module.skin.Skin;
import com.github.manolo8.simplecraft.module.skin.SkinService;
import com.github.manolo8.simplecraft.module.user.identity.Identity;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ItemStackUtils {

    private static Field skullProfileField;
    private static HashMap<String, Enchantment> enchantmentsMap;

    static {
        enchantmentsMap = new HashMap<>();

        enchantmentsMap.put("eff", Enchantment.DIG_SPEED);
        enchantmentsMap.put("unb", Enchantment.DURABILITY);
        enchantmentsMap.put("fte", Enchantment.LOOT_BONUS_BLOCKS);
        enchantmentsMap.put("lot", Enchantment.LOOT_BONUS_MOBS);
        enchantmentsMap.put("pro", Enchantment.PROTECTION_ENVIRONMENTAL);
        enchantmentsMap.put("srp", Enchantment.DAMAGE_ALL);

    }

    public static ItemStack of(Material material) {
        return new ItemStack(material);
    }

    public static ItemStack of(Material material, int quantity) {
        return new ItemStack(material, quantity);
    }

    public static ItemStack changeLore(ItemStack item, String... lore) {

        ItemMeta meta = item.getItemMeta();
        meta.setLore(Arrays.asList(lore));

        item.setItemMeta(meta);

        return item;
    }

    public static ItemStack create(Material material, String title) {
        return create(material, title, 1);
    }

    public static ItemStack create(Material material, String title, int quantity) {
        return create(material, title, quantity, (byte) 0);
    }

    public static ItemStack create(Material material, String title, int quantity, byte data) {
        ItemStack itemStack = new ItemStack(material, quantity, data);
        ItemMeta meta = itemStack.getItemMeta();
        if (title != null) meta.setDisplayName(title);
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public static ItemStack create(Material material, int amount, String title, List<String> lore) {
        ItemStack itemStack = new ItemStack(material, amount);

        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(title);
        meta.setLore(lore);

        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public static ItemStack createMonsterEgg(String title, EntityType type, String... lore) {
        return createMonsterEgg(title, type, 1, lore);
    }

    public static ItemStack createMonsterEgg(String title, EntityType type, int quantity, String... lore) {
        ItemStack egg = new ItemStack(Material.CREEPER_SPAWN_EGG, quantity);

        ItemMeta meta = egg.getItemMeta();
        meta.setDisplayName(title);
        if (lore.length != 0) meta.setLore(Arrays.asList(lore));
        egg.setItemMeta(meta);

        return egg;
    }

    public static ItemStack createEnchanted(Material material, String title, String enchantments) {
        ItemStack stack = create(material, title);
        String[] enchants = enchantments.split(":");

        if (material == Material.ENCHANTED_BOOK) {
            EnchantmentStorageMeta meta = (EnchantmentStorageMeta) stack.getItemMeta();

            for (String str : enchants) {
                String[] type = str.split("-");
                meta.addStoredEnchant(enchantmentsMap.get(type[0]), NumberUtils.toInt(type[1]), true);
            }

            stack.setItemMeta(meta);
        } else {
            for (String str : enchants) {
                String[] type = str.split("-");
                stack.addUnsafeEnchantment(enchantmentsMap.get(type[0]), NumberUtils.toInt(type[1]));
            }
        }

        return stack;
    }

    public static int extractEnchantment(ItemStack itemStack, Enchantment enchantment) {
        if (itemStack.getType() == Material.ENCHANTED_BOOK && itemStack.hasItemMeta()) {
            EnchantmentStorageMeta meta = (EnchantmentStorageMeta) itemStack.getItemMeta();
            return meta.getStoredEnchantLevel(enchantment);
        } else {
            return itemStack.getEnchantmentLevel(enchantment);
        }
    }

    public static ItemStack create(Material material, String title, String... info) {
        return create(material, 1, title, Arrays.asList(info));
    }

    public static ItemStack create(Material material, int amount, String title, String... info) {
        return create(material, amount, title, Arrays.asList(info));
    }

    public static ItemStack createSkullByIdentity(Identity identity, String title, String... lore) {
        return createSkullBySkin(identity.getSkin(), title, lore);
    }

    public static ItemStack createSkullByName(String name, String title, String... lore) {
        try {
            return createSkullBySkin(SkinService.instance.findByName(name), title, lore);
        } catch (SQLException e) {
            return createSkullBySkin(null, title, lore);
        }
    }

    public static ItemStack createSkullBySkin(Skin skin, String title, String... lore) {
        return createSkullByBase64(skin == null ? null : skin.getValue(), title, lore);
    }


    public static ItemStack createSkullByBase64(String base64, String title, String... lore) {
        return createSkullByBase64(base64, title, lore == null || lore.length == 0 ? null : Arrays.asList(lore));
    }

    public static ItemStack createSkullByBase64(String base64, String title, List<String> lore) {
        ItemStack item = new ItemStack(Material.SKELETON_SKULL, 1, (byte) 3);

        ItemMeta meta = item.getItemMeta();

        if (base64 != null) {
            GameProfile profile = new GameProfile(UUID.randomUUID(), null);
            PropertyMap propertyMap = profile.getProperties();

            propertyMap.put("textures", new Property("textures", base64));

            if (skullProfileField == null) {
                try {
                    skullProfileField = meta.getClass().getDeclaredField("profile");
                    skullProfileField.setAccessible(true);
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                }
            }

            try {
                skullProfileField.set(meta, profile);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        if (title != null) meta.setDisplayName(title);
        if (lore != null) meta.setLore(lore);

        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack create(Material material, String title, List<String> lore) {
        return create(material, 1, title, lore);
    }

}
