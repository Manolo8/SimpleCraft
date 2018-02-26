package com.github.manolo8.simplecraft.core.world;

import org.bukkit.World;

public interface IWorldProducer {

    int getType();

    IWorld produce(World world);
}
