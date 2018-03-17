package com.github.manolo8.simplecraft.core.protection.impl;

import com.github.manolo8.simplecraft.core.world.IWorld;
import com.github.manolo8.simplecraft.core.world.IWorldProducer;
import org.bukkit.World;

public class DefaultIWorldProducer implements IWorldProducer {

    private final DefaultChecker defaultChecker;

    public DefaultIWorldProducer() {
        this.defaultChecker = new DefaultChecker();
    }

    @Override
    public int getType() {
        return 0;
    }

    @Override
    public IWorld produce(int worldId) {
        return new DefaultIWorld(worldId, defaultChecker);
    }

    @Override
    public void unload(IWorld iWorld) {
        //Não faz nada no default...
    }
}
