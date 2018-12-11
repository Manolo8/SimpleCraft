package com.github.manolo8.simplecraft.interfaces;

import com.github.manolo8.simplecraft.module.user.User;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;

public interface BlockBreak {

    void onBreak(User user, BlockBreakEvent event);
}
