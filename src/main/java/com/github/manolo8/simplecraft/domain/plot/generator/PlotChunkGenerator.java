package com.github.manolo8.simplecraft.domain.plot.generator;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.material.MaterialData;

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
                    if(y < 3) data.setBlock(xx,y,zz, Material.BEDROCK);
                    else if (y == 63) data.setBlock(xx, y, zz, new MaterialData(Material.DIRT, (byte) 2));
                    else data.setBlock(xx, y, zz, Material.DIRT);
            }
    }

    private void fillStreet(ChunkData data, int x, int z) {
        for (int xx = 0; xx < 16; xx++)
            for (int zz = 0; zz < 16; zz++) {
                for (int y = 0; y < 64; y++) {
                    Material material;
                    if(y < 3) material = Material.BEDROCK;
                    else if (y != 63) material = Material.DIRT;
                    else {
                        material = Material.GRASS;


                        boolean mx = x % 4 == 0;
                        boolean mz = z % 4 == 0;

                        boolean cx = xx % 16 == 0 || xx % 16 == 15;
                        boolean cz = zz % 16 == 0 || zz % 16 == 15;

                        MaterialData sp = null;

                        if (mx && mz && cx && cz) sp = new MaterialData(Material.LEAVES);
                        else if (mx && cx && !mz) sp = new MaterialData(Material.LEAVES);
                        else if (mz && cz && !mx) sp = new MaterialData(Material.LEAVES);
                        else {
                            int i = random.nextInt(10);
                            if (i == 0 || i == 1 || i == 2) sp = new MaterialData(31, (byte) 1);
                            else if (i == 3) sp = new MaterialData(31, (byte) 2);
                            else if (i == 4) sp = new MaterialData(38, (byte) 4);
                            else sp = new MaterialData(Material.AIR);
                        }
                        data.setBlock(xx, y + 1, zz, sp);
                    }

                    data.setBlock(xx, y, zz, material);
                }

            }
    }
}
