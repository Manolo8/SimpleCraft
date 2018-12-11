package com.github.manolo8.simplecraft.module.mobarea;

import com.github.manolo8.simplecraft.SimpleCraft;
import com.github.manolo8.simplecraft.core.world.WorldService;
import com.github.manolo8.simplecraft.core.world.container.Container;
import com.github.manolo8.simplecraft.interfaces.BoardContainer;
import com.github.manolo8.simplecraft.interfaces.EntityDamage;
import com.github.manolo8.simplecraft.interfaces.EntityDeath;
import com.github.manolo8.simplecraft.interfaces.Tickable;
import com.github.manolo8.simplecraft.module.mobarea.mobs.Mob;
import com.github.manolo8.simplecraft.module.mobarea.mobs.MobInfo;
import com.github.manolo8.simplecraft.module.mobarea.mobs.MobInfoRepository;
import com.github.manolo8.simplecraft.module.user.User;
import com.github.manolo8.simplecraft.utils.location.LocationUtils;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Slime;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MobArea extends Container implements EntityDeath, EntityDamage, Tickable, BoardContainer {

    private final MobInfoRepository repository;
    private List<MobInfo> mobInfos;

    public MobArea(MobInfoRepository repository) {
        this.repository = repository;
        mobInfos = new ArrayList<>();
    }


    //======================================================
    //====================ENCAPSULATION=====================
    //======================================================
    public List<MobInfo> getMobInfos() {
        return mobInfos;
    }

    public void setMobInfos(List<MobInfo> mobInfos) {
        this.mobInfos = mobInfos;
    }

    //======================================================
    //===================_ENCAPSULATION=====================
    //======================================================


    //======================================================
    //=======================METHODS========================
    //======================================================

    public void create(Mob mob, int quantity) throws SQLException {
        mobInfos.add(repository.create(this, mob, quantity));
    }

    public MobInfo getMob(String mob) {
        for (MobInfo info : mobInfos)
            if (info.match(mob))
                return info;

        return null;
    }

    public boolean hasMob(Mob mob) {
        return getMob(mob.getName()) != null;
    }

    public void removeMob(MobInfo info) {
        mobInfos.removeIf(info1 -> info == info1);
        info.remove();
    }

    /**
     * @return retorna uma relação entre as chunks carregadas
     * e não carregadas
     */
    public double percentLoaded() {
        return ((double) chunks.size()) / getTotalChunks();
    }

    /**
     * Respawna todos os stored nas chunks carregadas para
     * essa area de stored
     */
    public void respawnMobs(boolean killExistent) {

        //Reseta
        for (MobInfo mobInfo : mobInfos) mobInfo.reset(percentLoaded());

        //Calcula
        for (Chunk chunk : chunks) {
            entityLoop:
            for (Entity entity : chunk.getEntities()) {
                if ((entity instanceof Creature || entity instanceof Slime)) {
                    for (MobInfo mobInfo : mobInfos) {
                        if (mobInfo.match((LivingEntity) entity)) {
                            if (mobInfo.overflow() || killExistent) {
                                entity.remove();
                            } else mobInfo.addCurrent();
                            continue entityLoop;
                        }
                    }
                    //Se não tiver, clear
                    entity.remove();
                }
            }
        }


        for (MobInfo mobInfo : mobInfos) spawnMobs(mobInfo);
    }

    /**
     * Spawna stored baseado em um MobInfo, limitado
     * as chunks carregadas / total de chunks
     *
     * @param mobInfo que sera spawnado
     */
    private void spawnMobs(MobInfo mobInfo) {
        for (int i = 0; mobInfo.getMissing() > i; i++) {

            Location loc = LocationUtils.findAvailableLocationLoaded(this, 10);

            if (loc != null) {
                mobInfo.spawn(loc);
            }

        }
    }

    private void killMobs(Chunk chunk) {
        entityLoop:
        for (Entity entity : chunk.getEntities()) {
            if (entity instanceof Creature || entity instanceof Slime) {
                for (MobInfo info : mobInfos)
                    if (info.match((LivingEntity) entity))
                        continue entityLoop;

                entity.remove();
            }
        }
    }

    //======================================================
    //======================OVERRIDE========================
    //======================================================
    @Override
    public void onEntityDeath(User user, Entity entity, List<ItemStack> drops) {
        if (entity instanceof LivingEntity) {

            LivingEntity living = (LivingEntity) entity;

            for (MobInfo info : mobInfos) {
                if (info.match(living)) {
                    info.death(user, drops);
                    break;
                }
            }

        }
    }

    @Override
    public void onEntityReceiveDamage(User user, LivingEntity entity) {
        for (MobInfo info : mobInfos) {
            if (info.match(entity)) {
                Bukkit.getScheduler().runTaskLater(SimpleCraft.instance, () -> {
                    entity.setCustomName(info.getDisplayName() + " §c♥ " + ((int) entity.getHealth()));
                }, 5);
                break;
            }
        }
    }

    @Override
    public void tick() {
        if (WorldService.tick % 400 == 1) {
            respawnMobs(false);
        }
    }
    //======================================================
    //=====================_OVERRIDE========================
    //======================================================

    //======================================================
    //=======================METHODS========================
    //======================================================


    //======================================================
    //=======================ENTITY=========================
    //======================================================
    @Override
    public void remove() {
        super.remove();
        for (MobInfo info : mobInfos) info.remove();
    }
    //======================================================
    //======================_ENTITY=========================
    //======================================================
}
