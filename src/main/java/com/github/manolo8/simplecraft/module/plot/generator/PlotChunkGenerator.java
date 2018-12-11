package com.github.manolo8.simplecraft.module.plot.generator;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.data.BlockData;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;

import java.util.List;
import java.util.Random;

public class PlotChunkGenerator extends ChunkGenerator {

    private final Random random;

    public PlotChunkGenerator(Random random) {
        this.random = random;
    }

    @Override
    public List<BlockPopulator> getDefaultPopulators(World world) {
        return super.getDefaultPopulators(world);
    }

    @Override
    public ChunkData generateChunkData(World world, Random random, int x, int z, BiomeGrid biome) {
        ChunkData data = createChunkData(world);


        for (int i = 0; i < 16; i++) {
            for (int ii = 0; ii < 16; ii++) {
                biome.setBiome(i, ii, Biome.FOREST);
            }
        }

        if (isStreet(x, z)) fillStreet(data, x, z);
        else fillPlot(data);


        return data;
    }

    private boolean isStreet(int x, int z) {
        return (z % 4 == 0 || x % 4 == 0);
    }

    private void fillPlot(ChunkData data) {
        for (int xx = 0; xx < 16; xx++)
            for (int zz = 0; zz < 16; zz++) {
                for (int y = 0; y < 64; y++)
                    if (y < 3) data.setBlock(xx, y, zz, Material.BEDROCK);
                    else if (y == 63) data.setBlock(xx, y, zz, Bukkit.createBlockData(Material.PODZOL));
                    else data.setBlock(xx, y, zz, Material.DIRT);
            }
    }

    private void fillStreet(ChunkData data, int x, int z) {
        for (int xx = 0; xx < 16; xx++)
            for (int zz = 0; zz < 16; zz++) {
                for (int y = 0; y < 64; y++) {
                    Material material;
                    if (y < 3) material = Material.BEDROCK;
                    else if (y != 63) material = Material.DIRT;
                    else {
                        material = Material.GRASS_BLOCK;

                        boolean mx = x % 4 == 0;
                        boolean mz = z % 4 == 0;

                        boolean cx = xx % 16 == 0 || xx % 16 == 15;
                        boolean cz = zz % 16 == 0 || zz % 16 == 15;

                        BlockData sp;

                        if (mx && mz && cx && cz) sp = Bukkit.createBlockData(Material.ACACIA_LEAVES);
                        else if (mx && cx && !mz) sp = Bukkit.createBlockData(Material.ACACIA_LEAVES);
                        else if (mz && cz && !mx) sp = Bukkit.createBlockData(Material.ACACIA_LEAVES);
                        else {
                            int i = random.nextInt(10);
                            if (i < 3) sp = Bukkit.createBlockData(Material.GRASS);
                            else if (i == 3) sp = Bukkit.createBlockData(Material.TALL_GRASS);
                            else sp = Bukkit.createBlockData(Material.AIR);
                        }
                        data.setBlock(xx, y + 1, zz, sp);
                    }

                    data.setBlock(xx, y, zz, material);
                }

            }
    }
}
