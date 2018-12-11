package com.github.manolo8.simplecraft.interfaces;

import com.github.manolo8.simplecraft.module.user.User;
import org.bukkit.event.player.PlayerInteractEvent;

public interface BlockInteract {

    void onBlockInteract(User user, PlayerInteractEvent event);
}
