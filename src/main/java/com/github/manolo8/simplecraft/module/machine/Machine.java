package com.github.manolo8.simplecraft.module.machine;

import com.github.manolo8.simplecraft.core.world.WorldService;
import com.github.manolo8.simplecraft.core.world.container.Container;
import com.github.manolo8.simplecraft.interfaces.BlockBreak;
import com.github.manolo8.simplecraft.interfaces.BlockInteract;
import com.github.manolo8.simplecraft.interfaces.Tickable;
import com.github.manolo8.simplecraft.module.hologram.Hologram;
import com.github.manolo8.simplecraft.module.machine.fuel.Fuel;
import com.github.manolo8.simplecraft.module.machine.type.MachineType;
import com.github.manolo8.simplecraft.module.machine.type.drop.MachineDrop;
import com.github.manolo8.simplecraft.module.user.User;
import net.minecraft.server.v1_13_R2.EntityItem;
import net.minecraft.server.v1_13_R2.IChatBaseComponent;
import net.minecraft.server.v1_13_R2.WorldServer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_13_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_13_R2.inventory.CraftItemStack;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

import static com.github.manolo8.simplecraft.utils.mc.InventoryUtils.addItemTo;

public class Machine extends Container implements BlockInteract, BlockBreak, Tickable {

    protected static final Random random = new Random();

    protected MachineType type;

    protected int time;
    protected double stored;

    protected Fuel fuel;

    protected int direction;
    protected boolean auto;

    protected Hologram hologram;

    protected WorldServer worldServer;
    protected Location location;

    //======================================================
    //=====================ENCAPSULATION====================
    //======================================================

    public MachineType getType() {
        return type;
    }

    public void setType(MachineType type) {
        this.type = type;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public double getStored() {
        return stored;
    }

    public void setStored(double stored) {
        this.stored = stored;
    }

    public Fuel getFuel() {
        return fuel;
    }

    public void setFuel(Fuel fuel) {
        this.fuel = fuel;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public boolean isAuto() {
        return auto;
    }

    public void setAuto(boolean auto) {
        this.auto = auto;
    }

    //======================================================
    //====================_ENCAPSULATION====================
    //======================================================


    //======================================================
    //======================METHODS=========================
    //======================================================

    public void giveTo(User user) {
        giveDrops(user);
    }

    //AUTO
    private void dropItems() {
        final int[] items = new int[type.getDrops().size()];

        int interaction = 0;

        while (stored > 0 && ++interaction < 10) {
            stored--;

            for (int i = 0; i < items.length; i++) {
                MachineDrop drop = type.getDrops().get(i);
                if (random.nextInt(drop.getRarity()) == 0)
                    items[i] += drop.getItem().getAmount();
            }
        }

        for (int i = 0; i < items.length; i++)
            if (items[i] != 0) createEntity(items[i], type.getDrops().get(i).getItem());
    }

    private void createEntity(int amount, ItemStack item) {
        EntityItem entity = new EntityItem(worldServer, location.getX(), location.getY(), location.getZ(), CraftItemStack.asNMSCopy(item));
        entity.setCustomName(IChatBaseComponent.ChatSerializer.a(String.valueOf(amount)));
        entity.setCustomNameVisible(true);
        worldServer.addEntity(entity, CreatureSpawnEvent.SpawnReason.CUSTOM);
        entity.motX = 0;
        entity.motY = 0;
        entity.motZ = 0;
        entity.velocityChanged = true;
        entity.pickupDelay = 10;
    }
    //AUTO

    //MANUAL
    private void giveDrops(User user) {
        Inventory inventory = user.base().getInventory();

        int interaction = 0;

        main:
        while (stored > 0 && ++interaction < 200) {
            stored--;

            for (MachineDrop drop : type.getDrops())
                if (random.nextInt(drop.getRarity()) == 0)
                    if ((addItemTo(inventory, drop.getItem())) != 0) break main;
        }
    }
    //MANUAL

    public void updateBlock() {
        Block block = worldInfo.getWorld().getBlockAt(area.maxX, area.maxY, area.maxZ);

        block.setType(type.getMaterial());

        updateLines();
    }


    private void updateLines() {
        hologram.updateLine(0, "§e-=[ §a" + type.getName() + " §e]=-");
        hologram.updateLine(1, "§eFUEL " + time);
    }

    //======================================================
    //=====================_METHODS=========================
    //======================================================


    //======================================================
    //======================CONTAINER=======================
    //======================================================
    @Override
    public void refreshDefaults() {
        super.refreshDefaults();

        location = new Location(worldInfo.getWorld(), area.maxX + 0.5, area.maxY + 1, area.maxZ + 0.5);
        worldServer = ((CraftWorld) worldInfo.getWorld()).getHandle();

        hologram = new Hologram();

        hologram.setWorldInfo(worldInfo);

        hologram.setArea(area.add(0, 1, 0, 0, 1, 0));

        hologram.addLine("§e-=[ §a" + type.getName() + " §e]=-");
        hologram.addLine("§eFUEL" + time);
        hologram.addLine("§eQTD: " + stored);

        worldInfo.getContainer().addContainer(hologram);
    }

    @Override
    public void onBlockInteract(User user, PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (!user.base().isSneaking()) {
                event.setCancelled(true);
//                user.createView(new SpawnerView(this));
            }
        }
    }

    @Override
    public void onBreak(User user, BlockBreakEvent event) {
        ItemStack hand = user.base().getItemInHand();

        if (!user.iContainer()
                .update(event.getBlock())
                .protection()
                .canRemoveSpecials(user)) {
            user.sendAction("§cVocê não pode quebrar máquinas!");
            event.setCancelled(true);
        } else if (hand == null || hand.getType() != Material.GOLDEN_PICKAXE) {
            user.sendAction("§cUse uma picareta de ouro para quebrar!");
            event.setCancelled(true);
        } else {
            worldInfo.getWorld().dropItem(location, MachineRepository.toItemStack(this));
        }
    }

    @Override
    public void tick() {

        final boolean toggler = WorldService.tick % 20 == 0;

        if (stored != type.getLimit() && time != 0) {

            final double current = (1 * (type.getAmplifier() * fuel.getAmplifier()));

            if ((time -= Math.ceil(current)) < 0) {
                time = 0;
            } else if ((stored += current) >= type.getLimit()) {
                stored = type.getLimit();
            }

            if (toggler) {
                hologram.updateLine(1, "§eFUEL " + time);
                hologram.updateLine(2, "§eQTD: " + stored);
            }

        }

        if (toggler && auto && stored > 0) {
            dropItems();
        }
    }

    @Override
    public void unloaded() {
        modified();
    }

    @Override
    public void unAttachAndRemove() {
        hologram.remove();
        super.unAttachAndRemove();
    }
    //======================================================
    //======================_CONTAINER======================
    //======================================================
}
