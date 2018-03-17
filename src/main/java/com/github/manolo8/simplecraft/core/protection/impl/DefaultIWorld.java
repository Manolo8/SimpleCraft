package com.github.manolo8.simplecraft.core.protection.impl;

import com.github.manolo8.simplecraft.core.protection.ProtectionChecker;
import com.github.manolo8.simplecraft.core.world.IWorld;
import org.bukkit.Chunk;
import org.bukkit.World;

public class DefaultIWorld implements IWorld {

    private int worldId;
    private ProtectionChecker checker;

    public DefaultIWorld(int worldId,
                         ProtectionChecker checker) {
        this.worldId = worldId;
        this.checker = checker;
    }

    @Override
    public boolean match(int worldId) {
        return this.worldId == worldId;
    }

    @Override
    public void chunkLoad(int x, int z) {
        //Não vamos usar isso no default
    }

    @Override
    public void chunkUnload(int x, int z, Chunk[] chunks) {
        //Não vamos usar isso no default
    }

    @Override
    public ProtectionChecker getChecker() {
        return checker;
    }
}
