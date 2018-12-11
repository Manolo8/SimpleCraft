package com.github.manolo8.simplecraft.interfaces;

import com.github.manolo8.simplecraft.module.user.User;
import org.bukkit.event.player.PlayerInteractEvent;

public interface AnyInteraction {

    void onInteract(User user, PlayerInteractEvent event);

}
