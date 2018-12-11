package com.github.manolo8.simplecraft.module.skill.tools;

import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;

public interface BlockFarm {

    void onFarm(BlockBreakEvent event);

}
