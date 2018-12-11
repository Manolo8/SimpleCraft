package com.github.manolo8.simplecraft.utils.location;

import com.github.manolo8.simplecraft.core.world.container.Area;
import com.github.manolo8.simplecraft.core.world.container.Container;
import com.github.manolo8.simplecraft.utils.def.RandomUtils;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.List;
import java.util.Random;

public class LocationUtils {

    private static final Random random = new Random();

    public static Location findAvailableLocation(Container container, int attempts) {
        World world = container.getWorldInfo().getWorld();

        Area area = container.getArea();

        if (attempts <= 0) return null;

        while (attempts != 0) {
            int x = RandomUtils.randomBetween(random, area.maxZ, area.minX);
            int z = RandomUtils.randomBetween(random, area.maxZ, area.minZ);

            for (int y = area.minY; y < area.maxY; y++) {
                Block block = world.getBlockAt(x, y, z);

                if (isAvailableAndInside(block))
                    return block.getRelative(0, 2, 0).getLocation().add(0.5, 0, 0.5);
            }

            attempts--;
        }

        return null;
    }

    /**
     * @param attempts tentativas
     * @return caso não execeda as tentativas, retorna um
     * local disponível para spawn
     */
    public static Location findAvailableLocationLoaded(Container container, int attempts) {
        List<Chunk> chunks = container.getChunks();

        Area area = container.getArea();

        if (chunks.size() == 0 || attempts <= 0) return null;

        while (attempts != 0) {
            Chunk chunk = container.getChunks().get(random.nextInt(chunks.size()));

            int x = random.nextInt(15);
            int z = random.nextInt(15);

            for (int y = area.minY; y < area.maxY; y++) {
                Block block = chunk.getBlock(x, y, z);

                if (isAvailableAndInside(block)) return block.getRelative(0, 2, 0).getLocation().add(0.5, 0, 0.5);
            }

            attempts--;
        }

        return null;
    }

    /**
     * @param block bloco
     * @return true caso o local esteja disponível para spawn
     * de stored
     */
    private static boolean isAvailableAndInside(Block block) {
        if (!block.getType().isSolid()) return false;

//        if (container.update(block) != container) return false;

        Block relative = block.getRelative(0, 1, 0);
        if (!(relative.getType() == Material.AIR || relative.getType().isTransparent())) return false;

        relative = relative.getRelative(0, 1, 0);
        return (relative.getType() == Material.AIR || relative.getType().isTransparent());
    }
}
