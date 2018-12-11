package com.github.manolo8.simplecraft.interfaces;

import com.github.manolo8.simplecraft.module.user.User;
import org.bukkit.event.block.BlockPlaceEvent;

public interface BlockPlace {

    void onBlockPlace(User user, BlockPlaceEvent event);
}
