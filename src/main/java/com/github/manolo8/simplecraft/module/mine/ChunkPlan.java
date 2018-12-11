package com.github.manolo8.simplecraft.module.mine;

import com.github.manolo8.simplecraft.core.world.container.Area;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;


public class ChunkPlan {

    protected int x;
    protected int z;
    private Mine mine;
    private int maxCX;
    private int minCX;
    private int maxCZ;
    private int minCZ;


    public ChunkPlan(int x, int z) {
        this.x = x;
        this.z = z;
    }

    public void merge(Mine mine) {
        this.mine = mine;

        Area area = mine.getArea();

        maxCX = (calcMax(x, area.maxX));
        maxCZ = (calcMax(z, area.maxZ));
        minCX = (calcMin(x, area.minX));
        minCZ = (calcMin(z, area.minZ));
    }

    private int calcMax(int pos, int max) {
        pos = pos * 16 + 16;

        return max >= pos ? 15 : max % 16;
    }

    private int calcMin(int pos, int min) {
        pos = pos * 16;

        return min > pos ? min - pos : 0;
    }

    public void reset() {
        Chunk chunk = mine.getWorldInfo().getWorld().getChunkAt(x, z);

        Entity[] entities = chunk.getEntities();

        int maxY = mine.getArea().maxY;
        int minY = mine.getArea().minY;

        Location back = new Location(chunk.getWorld(), chunk.getX() * 16 + maxCX, maxY + 1, chunk.getZ() * 16 + maxCZ);

        for (Entity entity : entities) {
            entity.teleport(back);
        }

        for (int y = minY; y <= maxY; y++)
            for (int x = minCX; x <= maxCX; x++)
                for (int z = minCZ; z <= maxCZ; z++) {
                    Block block = chunk.getBlock(x, y, z);
                    Material next = mine.next();
                    if (block.getType() != next)
                        block.setType(mine.next());
                }
    }
}