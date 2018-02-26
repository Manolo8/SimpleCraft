package com.github.manolo8.simplecraft.listener;

import com.github.manolo8.simplecraft.domain.chat.Chat;
import com.github.manolo8.simplecraft.core.protection.ProtectionController;
import com.github.manolo8.simplecraft.domain.user.User;
import com.github.manolo8.simplecraft.domain.user.UserService;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.*;

@SuppressWarnings("unused")
public class MainListener implements Listener {

    private final UserService userService;
    private final ProtectionController protectionController;
    private final Chat chat;

    public MainListener(UserService userService,
                        ProtectionController protectionController,
                        Chat chat) {
        this.userService = userService;
        this.protectionController = protectionController;
        this.chat = chat;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        User user = userService.playerJoin(event.getPlayer());

        protectionController.userJoin(user);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        userService.playerQuit(event.getPlayer());
    }

    @EventHandler
    public void chat(AsyncPlayerChatEvent event) {
        event.setCancelled(true);
        chat.userChatMessage(userService.getOnlineUser(event.getPlayer()), event.getMessage());
    }

    @EventHandler
    public void chat(PlayerCommandPreprocessEvent event) {
        if (!event.getMessage().startsWith("/g ")) return;
        event.setCancelled(true);
        chat.userGlobalMessage(userService.getOnlineUser(event.getPlayer()), event.getMessage().substring(3));
    }

    @EventHandler
    public void playerWorldChang(PlayerChangedWorldEvent event) {
        protectionController.changeWorld(userService.getOnlineUser(event.getPlayer()));
    }

    @EventHandler
    public void playerBreakBlock(BlockBreakEvent event) {
        User user = userService.getOnlineUser(event.getPlayer());
        Location location = event.getBlock().getLocation();

        boolean result = protectionController.breakBlock(user, location);

        event.setCancelled(!result);
    }

    @EventHandler
    public void playerPlaceBlock(BlockPlaceEvent event) {
        User user = userService.getOnlineUser(event.getPlayer());
        Location location = event.getBlock().getLocation();

        boolean result = protectionController.placeBlock(user, location);

        event.setCancelled(!result);
    }

    @EventHandler
    public void entityDamageEntity(EntityDamageByEntityEvent event) {

        Entity damager = event.getDamager();
        Entity victim = event.getEntity();


        if (victim instanceof Player) {
            Location location = victim.getLocation();
            User user = userService.getOnlineUser((Player) victim);

            boolean result = protectionController.isPvpOn(user, location);
            event.setCancelled(!result);
            return;
        }

        User user = null;

        if (damager instanceof Player) {
            user = userService.getOnlineUser((Player) damager);
        } else if (damager instanceof Arrow) {
            Projectile projectile = (Arrow) damager;

            if (projectile.getShooter() instanceof Player)
                user = userService.getOnlineUser((Player) projectile.getShooter());
        }

        if (user == null) return;

        Location location = damager.getLocation();

        boolean result = protectionController.isAnimalPvpOn(user, location);

        event.setCancelled(!result);
    }
}
