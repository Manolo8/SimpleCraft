package com.github.manolo8.simplecraft.utils.calculator;

import com.github.manolo8.simplecraft.utils.def.IntegerList;
import com.github.manolo8.simplecraft.utils.mc.ItemStackUtils;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Iterator;
import java.util.List;

public class MoneyCalculator {

    public static final MaterialValue[] materials;

    static {
        materials = new MaterialValue[30];

        materials[0] = new MaterialValue(0, Material.SAND, 4);
        materials[1] = new MaterialValue(1, Material.LAPIS_LAZULI, 0.8);
        materials[2] = new MaterialValue(2, Material.LAPIS_ORE, 2.69);
        materials[3] = new MaterialValue(3, Material.COAL_ORE, 5.38);
        materials[4] = new MaterialValue(4, Material.NETHERRACK, 1);
        materials[5] = new MaterialValue(5, Material.PRISMARINE, 16);
        materials[6] = new MaterialValue(6, Material.MUTTON, 8);
        materials[7] = new MaterialValue(7, Material.CHICKEN, 2);
        materials[8] = new MaterialValue(8, Material.IRON_INGOT, 20);
        materials[9] = new MaterialValue(9, Material.BEEF, 6);
        materials[10] = new MaterialValue(10, Material.FEATHER, 8);
        materials[11] = new MaterialValue(11, Material.WHITE_WOOL, 8);
        materials[12] = new MaterialValue(12, Material.LEATHER, 8);
        materials[13] = new MaterialValue(13, Material.COOKED_CHICKEN, 12);
        materials[14] = new MaterialValue(14, Material.ROTTEN_FLESH, 16);
        materials[15] = new MaterialValue(15, Material.COOKED_BEEF, 24);
        materials[16] = new MaterialValue(16, Material.COOKED_MUTTON, 60);
        materials[17] = new MaterialValue(17, Material.BONE, 256);
        materials[18] = new MaterialValue(18, Material.END_STONE, 64);
        materials[21] = new MaterialValue(21, Material.SPIDER_EYE, 64);
        materials[20] = new MaterialValue(20, Material.PRISMARINE_CRYSTALS, 128);
        materials[19] = new MaterialValue(19, Material.GOLD_INGOT, 160);
        materials[22] = new MaterialValue(22, Material.EMERALD_ORE, 256);
        materials[23] = new MaterialValue(23, Material.BLAZE_ROD, 256);
        materials[24] = new MaterialValue(24, Material.GHAST_TEAR, 256);
        materials[25] = new MaterialValue(25, Material.CHORUS_FRUIT, 512);
        materials[26] = new MaterialValue(26, Material.ENDER_PEARL, 1024);
        materials[27] = new MaterialValue(27, Material.DIAMOND, 2048);
        materials[28] = new MaterialValue(28, Material.ENDER_EYE, 2048);
        materials[29] = new MaterialValue(29, Material.MAGMA_CREAM, 3072);
    }

    public static void process(List<ItemStack> drops, double multiplier) {
        Iterator<ItemStack> i = drops.iterator();

        double calc = 0;

        while (i.hasNext()) {
            ItemStack drop = i.next();

            MaterialValue material = getMaterialValue(drop.getType(), drop.getData().getData());

            //Se não tiver o material, mantem o DROP
            if (material == null) {
                if (drop.getType() == Material.STICK) i.remove();
            } else {

                i.remove();

                calc += material.cost * drop.getAmount();
            }

        }

        drops.add(generateOneCoin(Math.ceil(calc * multiplier)));
    }

    public static double extractCoins(IntegerList flag, Inventory inventory) {

        double coins = 0;

        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack item = inventory.getItem(i);
            if (item != null) {
                MaterialValue material = getMaterialValue(item.getType(), item.getData().getData());
                if (material != null && !flag.contains(material.id)) {
                    inventory.setItem(i, null);
                    coins += material.cost * item.getAmount();
                }
            }
        }

        return coins;
    }

    public static ItemStack generateOneCoin(double value) {
        return ItemStackUtils.create(Material.GOLD_NUGGET, "§c" + value + " coins");
    }

    private static MaterialValue getMaterialValue(Material type, byte data) {
        //Busca pela data idêntica
        if (data != 0)
            for (MaterialValue value : materials)
                if (value.material == type && (value.data == data))
                    return value;

        //Caso não encontre, agora busque por qualquer material igual
        for (MaterialValue value : materials)
            if (value.material == type && value.data == 0)
                return value;

        return null;
    }

    public static class MaterialValue {

        public int id;
        public Material material;
        public byte data;
        public double cost;

        public MaterialValue(int id, Material material, double cost) {
            this.id = id;
            this.material = material;
            this.data = 0;
            this.cost = cost;
        }

        public MaterialValue(int id, Material material, byte data, double cost) {
            this.id = id;
            this.material = material;
            this.data = data;
            this.cost = cost;
        }
    }
}
