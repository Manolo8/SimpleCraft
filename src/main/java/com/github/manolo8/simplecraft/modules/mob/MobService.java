package com.github.manolo8.simplecraft.modules.mob;

import com.github.manolo8.simplecraft.core.world.WorldService;
import com.github.manolo8.simplecraft.modules.mob.types.*;
import com.github.manolo8.simplecraft.modules.region.Region;
import com.github.manolo8.simplecraft.modules.user.UserService;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MobService implements Runnable {

    private UserService userService;
    private List<MobFiller> mobFillers;
    private List<Mob> mobs;

    public MobService(Random random, UserService userService) {
        this.userService = userService;
        mobFillers = new ArrayList<>();
        mobs = new ArrayList<>();
        MobBabyZombie babyZombie = new MobBabyZombie(random);
        MobZombie zombie = new MobZombie(random);
        MobBossZombie bossZombie = new MobBossZombie(random, zombie);
        MobPigZombie pigZombie = new MobPigZombie(random);
        MobVindicator vindicator = new MobVindicator(random);
        MobWitherSkeleton witherSkeleton = new MobWitherSkeleton(random);

        mobs.add(babyZombie);
        mobs.add(zombie);
        mobs.add(bossZombie);
        mobs.add(pigZombie);
        mobs.add(vindicator);
        mobs.add(witherSkeleton);

        MobFiller r1d1 = new MobFiller();
        r1d1.setName("1-1");
        r1d1.setRandom(random);
        r1d1.addMobInfo(new MobInfo(babyZombie, 1));
        r1d1.addMobInfo(new MobInfo(zombie, 220));
        r1d1.addMobInfo(new MobInfo(bossZombie, 10));

        MobFiller r1d2 = new MobFiller();
        r1d2.setName("1-2");
        r1d2.setRandom(random);
        r1d2.addMobInfo(new MobInfo(babyZombie, 20));
        r1d2.addMobInfo(new MobInfo(zombie, 120));
        r1d2.addMobInfo(new MobInfo(pigZombie, 20));
        r1d2.addMobInfo(new MobInfo(bossZombie, 40));

        MobFiller r1d3 = new MobFiller();
        r1d3.setName("1-3");
        r1d3.setRandom(random);
        r1d3.addMobInfo(new MobInfo(babyZombie, 8));
        r1d3.addMobInfo(new MobInfo(bossZombie, 30));
        r1d3.addMobInfo(new MobInfo(pigZombie, 40));
        r1d3.addMobInfo(new MobInfo(vindicator, 120));
        r1d3.addMobInfo(new MobInfo(witherSkeleton, 12));


        mobFillers.add(r1d1);
        mobFillers.add(r1d2);
        mobFillers.add(r1d3);
    }

    public void entityDeath(EntityDeathEvent event) {
        Mob mob = getMob(event.getEntity());

        if (mob == null) return;

        Player killer = event.getEntity().getKiller();

        if (killer != null) mob.playerKill(userService.getOnlineUser(killer));

        event.setDroppedExp(1);
        event.getDrops().clear();
        event.getDrops().addAll(mob.getDrops());
    }

    public void entityDamage(Entity target, Entity damager) {

        if (damager instanceof Player) {
            Player player = (Player) damager;
            Mob mob = getMob(target);

            if (mob == null) return;

            mob.attacked(target, player);
            return;
        }

        if (target instanceof Player) {
            Player player = (Player) target;
            Mob mob = getMob(damager);

            if (mob == null) return;

            mob.attack(damager, player);
        }
    }

    public void regionLoad(Region region) {
        MobFiller filler = getMobFiller(region);

        if (filler == null) return;

        World world = WorldService.instance.getWorldByWorldId(region.getWorldId());
        filler.setRegion(region);
        filler.setWorld(world);

        for (Chunk chunk : world.getLoadedChunks()) {
            if (filler.isChunkInArea(chunk)) filler.addChunk(chunk);
        }

        filler.setRunning(true);
    }

    public void regionUnload(Region region) {
        MobFiller filler = getMobFiller(region);

        if (filler == null) return;
        filler.setRegion(null);
        filler.setRunning(false);
    }

    public void chunkLoad(Chunk chunk) {
        for (MobFiller filler : mobFillers) {
            if (filler.isChunkInArea(chunk)) filler.addChunk(chunk);
        }
    }

    public void chunkUnload(Chunk chunk) {
        for (MobFiller filler : mobFillers) {
            if (filler.isChunkInArea(chunk)) filler.removeChunk(chunk);
        }
    }

    private Mob getMob(Entity entity) {
        for (Mob mob : mobs) {
            if (mob.match(entity)) {
                return mob;
            }
        }
        return null;
    }

    private MobFiller getMobFiller(Region region) {
        for (MobFiller filler : mobFillers) {
            if (region.getName().equals(filler.getName())) {
                return filler;
            }
        }
        return null;
    }

    @Override
    public void run() {
        for (MobFiller filler : mobFillers) if (filler.isRunning()) filler.respawnMobs();
    }
}
