package com.github.manolo8.simplecraft.module.mine.block;

import com.github.manolo8.simplecraft.core.data.model.base.BaseEntity;
import com.github.manolo8.simplecraft.module.mine.block.drops.BlockDrop;
import com.github.manolo8.simplecraft.module.mine.block.drops.BlockDropRepository;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class MineBlock extends BaseEntity {

    private final BlockDropRepository dropRepository;
    private Material material;

    private double percent;
    private double calculatedPercent;

    private List<BlockDrop> drops;

    private Random random;


    public MineBlock(BlockDropRepository dropRepository) {
        this.dropRepository = dropRepository;
        random = new Random();
    }

    //======================================================
    //=====================ENCAPSULATION====================
    //======================================================
    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public double getPercent() {
        return percent;
    }

    public void setPercent(double percent) {
        this.percent = percent;
        modified();
    }

    public double getCalculatedPercent() {
        return calculatedPercent;
    }

    public void setCalculatedPercent(double calculatedPercent) {
        this.calculatedPercent = calculatedPercent;
    }

    public List<BlockDrop> getDrops() {
        return drops;
    }

    public void setDrops(List<BlockDrop> drops) {
        this.drops = drops;
        recalculate();
    }
    //======================================================
    //====================_ENCAPSULATION====================
    //======================================================


    //======================================================
    //=======================METHODS========================
    //======================================================
    public void addDrop(ItemStack item, double chance) throws SQLException {
        drops.add(dropRepository.create(this, item, chance));

        recalculate();
    }

    public BlockDrop getDrop(ItemStack item) {
        for (BlockDrop drop : drops) {
            if (drop.getDrop().isSimilar(item)) return drop;
        }

        return null;
    }

    public BlockDrop getDrop(String item) {
        for (BlockDrop drop : drops) {
            if (drop.getDrop().getType().name().equals(item))
                return drop;
        }

        return null;
    }

    public boolean removeDrop(BlockDrop drop) {
        drops.removeIf(drop1 -> drop1 == drop);
        recalculate();
        drop.remove();

        return false;
    }

    public void recalculate() {
        drops.sort(Comparator.comparingDouble(BlockDrop::getChance));

        double current = 0;

        for (BlockDrop drop : drops) {
            current += drop.getChance();
            drop.setCalculatedChance(current);
        }
    }

    public double availableChance() {
        double chance = 0;

        for (BlockDrop drop : drops) chance += drop.getChance();

        return 1 - chance;
    }

    public BlockDrop next() {
        double rnd = random.nextDouble();

        for (BlockDrop drop : drops)
            if (drop.getCalculatedChance() >= rnd)
                return drop;

        return null;
    }
    //======================================================
    //=======================METHODS========================
    //======================================================


    //======================================================
    //=======================ENTITY=========================
    //======================================================
    @Override
    public void remove() {
        super.remove();
        for (BlockDrop drop : drops) drop.remove();
    }
    //======================================================
    //======================_ENTITY=========================
    //======================================================
}
