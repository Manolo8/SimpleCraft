package com.github.manolo8.simplecraft.modules.mob;

import com.github.manolo8.simplecraft.modules.region.Region;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MobFiller {

    private String name;
    private World world;
    private Random random;
    private Region region;
    private List<Chunk> chunks;
    private List<MobInfo> mobInfos;
    private boolean running;

    public MobFiller() {
        this.chunks = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isChunkInArea(Chunk chunk) {
        return world.equals(chunk.getWorld()) && region.getArea().isInside(chunk);
    }

    public void addChunk(Chunk chunk) {
        chunks.add(chunk);

        for (Entity entity : chunk.getEntities()) {
            if (entity instanceof Creature) entity.remove();
        }
    }

    public void removeChunk(Chunk chunk) {
        chunks.remove(chunk);
    }

    public World getWorld() {
        return world;
    }

    public void setWorld(World world) {
        this.world = world;
    }

    public void setRandom(Random random) {
        this.random = random;
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public void addMobInfo(MobInfo mobInfo) {
        if (mobInfos == null) mobInfos = new ArrayList();
        this.mobInfos.add(mobInfo);
    }

    public boolean isRunning() {
        if (!running) return false;
        return chunks.size() != 0;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public void respawnMobs() {

        List<LivingEntity> entities = world.getLivingEntities();

        for (MobInfo mobInfo : mobInfos) {
            int quantity = 0;

            for (LivingEntity entity : entities) {
                if (mobInfo.match(entity) && region.isInArea(entity.getLocation())) quantity++;
            }

            spawnMobs(mobInfo, quantity);
        }
    }

    private void spawnMobs(MobInfo mobInfo, int quantity) {
        for (; mobInfo.getQuantity() >= quantity; quantity++) {
            Location loc = findAvailableLocation();

            if (loc == null) {
                continue;
            }

            mobInfo.spawn(loc);
        }
    }

    private Location findAvailableLocation() {
        return findAvailableLocation(0);
    }

    private Location findAvailableLocation(int attempts) {
        if (chunks.size() == 0) return null;

        Chunk chunk = chunks.get(random.nextInt(chunks.size()));

        int x = random(0, 15);
        int z = random(0, 15);

        for (int y = 1; y < 40; y++) {
            Block block = chunk.getBlock(x, y, z);

            if (isOk(block)) return block.getRelative(0, 2, 0).getLocation();
        }

        attempts++;

        if (attempts == 5) {
            return null;
        }

        return findAvailableLocation(attempts);
    }

    private boolean isOk(Block block) {
        if (!block.getType().isSolid()) return false;

        Block relative = block.getRelative(0, 1, 0);
        if (!(relative.getType() == Material.AIR || relative.getType().isTransparent())) return false;

        relative = relative.getRelative(0, 1, 0);
        return (relative.getType() == Material.AIR || relative.getType().isTransparent());
    }

    private int random(int a, int b) {
        int max = Math.max(a, b);
        int min = Math.min(a, b);

        return random.nextInt(max - min) + min;
    }
}
