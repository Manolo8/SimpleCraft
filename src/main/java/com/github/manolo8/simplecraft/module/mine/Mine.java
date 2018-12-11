package com.github.manolo8.simplecraft.module.mine;

import com.github.manolo8.simplecraft.core.world.WorldService;
import com.github.manolo8.simplecraft.core.world.container.Area;
import com.github.manolo8.simplecraft.core.world.container.Container;
import com.github.manolo8.simplecraft.interfaces.BlockBreak;
import com.github.manolo8.simplecraft.interfaces.BlockPlace;
import com.github.manolo8.simplecraft.interfaces.Tickable;
import com.github.manolo8.simplecraft.module.mine.block.MineBlock;
import com.github.manolo8.simplecraft.module.mine.block.MineBlockRepository;
import com.github.manolo8.simplecraft.module.mine.block.drops.BlockDrop;
import com.github.manolo8.simplecraft.module.user.User;
import com.github.manolo8.simplecraft.utils.def.RandomUtils;
import com.github.manolo8.simplecraft.utils.mc.InventoryUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Mine extends Container.CustomProtection implements BlockBreak, BlockPlace, Tickable {

    private final MineBlockRepository blockRepository;

    private final Random random;
    private int totalOres;
    private int resetOres;
    private int currentOres;
    private List<ChunkPlan> plans;
    private List<MineBlock> blocks;
    private boolean reset;
    private int i;

    public Mine(MineBlockRepository blockRepository) {
        this.blockRepository = blockRepository;

        random = new Random();
    }

    //======================================================
    //=====================ENCAPSULATION====================
    //======================================================
    public int getCurrentOres() {
        return currentOres;
    }

    public void setCurrentOres(int currentOres) {
        this.currentOres = currentOres;
    }

    public List<MineBlock> getBlocks() {
        return blocks;
    }

    public void setBlocks(List<MineBlock> blocks) {
        this.blocks = blocks;
        recalculate();
    }
    //======================================================
    //====================_ENCAPSULATION====================
    //======================================================


    //======================================================
    //=======================METHODS========================
    //======================================================
    public void addBlock(Material material, double percent) throws SQLException {
        blocks.add(blockRepository.create(this, material, percent));
        recalculate();
    }

    public void removeBlock(MineBlock mineBlock) {
        blocks.removeIf(mineBlock1 -> mineBlock == mineBlock1);
        mineBlock.remove();
        recalculate();
    }

    public MineBlock getBlock(Material material) {
        for (MineBlock block : blocks)
            if (block.getMaterial() == material)
                return block;

        return null;
    }

    public void recalculate() {
        blocks.sort((o1, o2) -> Double.compare(o2.getPercent(), o1.getPercent()));

        double current = 0;

        for (MineBlock block : blocks) {
            current += block.getPercent();
            block.setCalculatedPercent(current);
        }
    }

    public double availablePercent() {
        double percent = 0;

        for (MineBlock mineBlock : blocks) percent += mineBlock.getPercent();

        return 1 - percent;
    }

    public Material next() {
        double rnd = random.nextDouble();

        for (MineBlock mineBlock : blocks)
            if (mineBlock.getCalculatedPercent() > rnd)
                return mineBlock.getMaterial();

        return Material.AIR;
    }

    public void forceReset() {
        reset = true;
        currentOres = 0;
        i = 0;
    }

    public void build() {
        this.plans = new ArrayList<>();
        int maxCX = area.maxX >> 4;
        int maxCZ = area.maxZ >> 4;
        int minCX = area.minX >> 4;
        int minCZ = area.minZ >> 4;
        updateTotalOres();

        for (int x = minCX; x <= maxCX; x++) {
            for (int z = minCZ; z <= maxCZ; z++) {
                ChunkPlan plan = new ChunkPlan(x, z);
                plan.merge(this);
                plans.add(plan);
            }
        }
    }

    //======================================================
    //=======================METHODS========================
    //======================================================
    @Override
    public void setArea(Area area) {
        super.setArea(area);
        build();
        updateTotalOres();
        i = 0;
    }

    private void updateTotalOres() {
        totalOres = add(area.maxX - area.minX) * add(area.maxY - area.minY) * add(area.maxZ - area.minZ);
        if (totalOres < 0) totalOres *= -1;
        resetOres = totalOres / 3;
    }

    private int add(int value) {
        return value < 0 ? value - 1 : value + 1;
    }

    private boolean chunkPlanAlreadyReseted(int x, int z) {

        x >>= 4;
        z >>= 4;

        for (int i = 0; i < plans.size(); i++) {
            ChunkPlan plan = plans.get(i);

            if (plan.x == x && plan.z == z) {
                return this.i >= i;
            }
        }

        return false;
    }

    //======================================================
    //=======================OVERRIDE=======================
    //======================================================
//    @Override
//    public List<OLDBoardItem> providerTo(User user) {
//        List<OLDBoardItem> data = new ArrayList<>();
//
//        data.add(new BaseOLDBoardItem(15, "§6Mina" + getName() + ":", true));
//        data.add(new ProvidedOLDBoardItem(16, currentOreProvider, true));
//        data.add(new ProvidedOLDBoardItem(17, percentOreProvider, true));
//        data.add(new ProvidedOLDBoardItem(18, playerSizeProvider, true));
//        data.add(new BaseOLDBoardItem(19, " ", true));
//
//        return data;
//    }

    @Override
    public boolean teleport(User user) {

        int x = RandomUtils.randomBetween(random, area.maxX, area.minX);
        int y = area.maxY + 1;
        int z = RandomUtils.randomBetween(random, area.maxZ, area.minZ);

        return user.teleport(new Location(worldInfo.getWorld(), x, y, z));
    }
    //======================================================
    //======================_OVERRIDE=======================
    //======================================================

    //======================================================
    //======================_METHODS========================
    //======================================================


    //======================================================
    //======================CONTAINER=======================
    //======================================================
    @Override
    public void onBreak(User user, BlockBreakEvent event) {
        if (event.isCancelled()) return;

        if (reset) {

            Block block = event.getBlock();

            if (chunkPlanAlreadyReseted(block.getX(), block.getZ())) {
                currentOres++;
            }

        } else {
            currentOres++;
        }

        Material material = event.getBlock().getType();

        MineBlock block = getBlock(material);

        if (block == null) return;

        event.setDropItems(false);
        event.setExpToDrop(0);

        //LOOT
        ItemStack hand = user.base().getInventory().getItemInMainHand();
        int loot = hand.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS);

        double exp = 0;

        Inventory inventory = user.base().getInventory();

        boolean full = false;

        do {
            BlockDrop drop = block.next();
            if (drop != null) {
                exp += (0.12D / drop.getChance());
                if (InventoryUtils.addItemTo(inventory, drop.getDrop()) != 0) full = true;
            }
        } while (loot != 0 && random.nextInt(loot) != 0);

        if (exp != 0) {
            user.base().giveExp((int) exp);
            if (full) user.sendAction("§cSeu inventário está cheio!");
        }
    }

    @Override
    public void onBlockPlace(User user, BlockPlaceEvent event) {
        currentOres--;
    }

    @Override
    public void tick() {
        if (reset) {
            if (WorldService.tick % 40 == 1) {
                if (i < plans.size()) {
                    ChunkPlan plan = plans.get(i);
                    plan.reset();
                    i++;
                    modified();
                } else {
                    reset = false;
                }
            }
        } else {
            if (currentOres > resetOres) {
                forceReset();
            } else if (currentOres > 500 && visiblePlayers == 0) {
                forceReset();
            }
        }
    }

    @Override
    protected void unloaded() {
        modified();
    }

    //======================================================
    //=====================_CONTAINER=======================
    //======================================================


    //======================================================
    //=================PROTECTION PROXY=====================
    //======================================================
    @Override
    public boolean canBreak(User user, Material type) {
        return true;
    }
    //======================================================
    //================_PROTECTION PROXY=====================
    //======================================================

    //======================================================
    //=======================ENTITY=========================
    //======================================================
    @Override
    public void remove() {
        super.remove();
        for (MineBlock block : blocks) block.remove();
    }
    //======================================================
    //======================_ENTITY=========================
    //======================================================
}