package com.github.manolo8.simplecraft.domain.plot.generator;

import org.bukkit.WorldCreator;
import org.bukkit.generator.ChunkGenerator;

import java.util.Random;

public class PlotGenerator extends WorldCreator {

    private final Random random;

    /**
     * Creates an empty WorldCreationOptions for the given world name
     *
     * @param name Name of the world that will be created
     */
    public PlotGenerator(String name) {
        super(name);
        this.random = new Random(seed());
    }

    @Override
    public ChunkGenerator generator() {
        return new PlotChunkGenerator(random);
    }
}
