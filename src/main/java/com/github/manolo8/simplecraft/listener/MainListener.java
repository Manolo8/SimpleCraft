package com.github.manolo8.simplecraft.listener;

import com.github.manolo8.simplecraft.core.chat.Chat;
import com.github.manolo8.simplecraft.core.commands.inventory.InventoryView;
import com.github.manolo8.simplecraft.core.protection.ProtectionController;
import com.github.manolo8.simplecraft.modules.mob.MobService;
import com.github.manolo8.simplecraft.modules.portal.PortalService;
import com.github.manolo8.simplecraft.modules.shop.ShopController;
import com.github.manolo8.simplecraft.modules.skill.SkillMagic;
import com.github.manolo8.simplecraft.modules.skill.tools.*;
import com.github.manolo8.simplecraft.modules.user.User;
import com.github.manolo8.simplecraft.modules.user.UserService;
import com.github.manolo8.simplecraft.utils.location.SimpleLocation;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.projectiles.ProjectileSource;

import java.util.List;

@SuppressWarnings("unused")
public class MainListener implements Listener {

    private final UserService userService;
    private final ProtectionController protectionController;
    private final ShopController shopController;
    private final MobService mobService;
    private final Chat chat;
    private final PortalService portalService;

    public MainListener(UserService userService,
                        ProtectionController protectionController,
                        ShopController shopController,
                        MobService mobService,
                        Chat chat,
                        PortalService portalService) {
        this.userService = userService;
        this.protectionController = protectionController;
        this.shopController = shopController;
        this.mobService = mobService;
        this.chat = chat;
        this.portalService = portalService;
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
    public void inventoryClose(InventoryCloseEvent event) {
        Inventory inventory = event.getInventory();

        if (!inventory.getName().startsWith("§aMenu - ")) return;

        User user = userService.getOnlineUser((Player) event.getPlayer());
        user.getInventoryView().close(false);
        user.setInventoryView(null);
    }

    @EventHandler
    public void entityDeath(EntityDeathEvent event) {
        mobService.entityDeath(event);
    }

    @EventHandler
    public void inventoryClick(InventoryClickEvent event) {

        User user = userService.getOnlineUser((Player) event.getWhoClicked());

        InventoryView view = user.getInventoryView();

        if (view == null) return;

        event.setCancelled(true);

        InventoryAction action = event.getAction();

        if (event.getInventory() instanceof PlayerInventory) {
            if (action == InventoryAction.PLACE_ALL || action == InventoryAction.PICKUP_ALL) event.setCancelled(false);
            return;
        }


        if (user.getInventoryView() == null) return;

        if (action != InventoryAction.PICKUP_ALL) return;

        view.handleClick(event.getSlot());
    }

    @EventHandler
    public void chat(PlayerCommandPreprocessEvent event) {
        if (!event.getMessage().startsWith("/g ")) return;
        event.setCancelled(true);
        chat.userGlobalMessage(userService.getOnlineUser(event.getPlayer()), event.getMessage().substring(3));
    }

    @EventHandler
    public void playerWorldChange(PlayerChangedWorldEvent event) {
        protectionController.changeWorld(userService.getOnlineUser(event.getPlayer()));
    }

    @EventHandler(ignoreCancelled = true)
    public void playerBreakBlock(BlockBreakEvent event) {

        Material material = event.getPlayer().getInventory().getItemInMainHand().getType();

        if (material == Material.DIAMOND_SWORD
                || material == Material.STONE_SWORD
                || material == Material.GOLD_SWORD
                || material == Material.IRON_SWORD
                || material == Material.WOOD_SWORD) {
            event.setCancelled(true);
            return;
        }

        User user = userService.getOnlineUser(event.getPlayer());

        boolean result = protectionController.breakBlock(user, event.getBlock());

        event.setCancelled(!result);
    }

    @EventHandler
    public void playerPlaceBlock(BlockPlaceEvent event) {
        User user = userService.getOnlineUser(event.getPlayer());

        boolean result = protectionController.placeBlock(user, event.getBlock());

        event.setCancelled(!result);
    }

    @EventHandler
    public void playerTeleport(PlayerTeleportEvent event) {
        User user = userService.getOnlineUser(event.getPlayer());
        protectionController.updateUserProtection(user);
    }

    @EventHandler
    public void playerChangeSign(SignChangeEvent event) {
        User user = userService.getOnlineUser(event.getPlayer());

        shopController.userCreateSign(user, event);
    }

    @EventHandler
    public void projectileShoot(ProjectileHitEvent event) {
        ProjectileSource source = event.getEntity().getShooter();

        if (!(source instanceof Player)) return;

        Projectile projectile = event.getEntity();

        User user = userService.getOnlineUser((Player) source);

        List<ProjectileHit> list = user.getByType(ProjectileHit.class);

        for (ProjectileHit hit : list)
            hit.onProjectileHit(user, projectile);
    }

    @EventHandler
    public void entityDamage(EntityDamageEvent event) {

        if (!(event.getEntity() instanceof Player)) return;

        if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
            User user = userService.getOnlineUser((Player) event.getEntity());

            List<FallDamage> list = user.getByType(FallDamage.class);

            DamageResult result = new DamageResult(event.getDamage());

            for (FallDamage damage : list) {
                damage.onFallDamage(user, result);
            }

            event.setDamage(result.getDamage());
            event.setCancelled(result.isCancelled());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void interact(PlayerInteractEvent event) {
        User user = userService.getOnlineUser(event.getPlayer());

        if (event.getItem() != null) {
            ItemStack wand = event.getItem();
            List<Interactable> interactables = user.getByType(Interactable.class);
            ItemStack item = event.getItem();
            Action action = event.getAction();

            for (Interactable interactable : interactables) {
                if (!interactable.match(item, action)) continue;

                interactable.onInteract(user);

                break;
            }
        }

        Action action = event.getAction();

        if (action == Action.RIGHT_CLICK_BLOCK) {
            boolean result = protectionController.canInteract(user, event.getClickedBlock());
            event.setCancelled(!result);
        }

        if (event.getItem() != null
                && user.hasPermission("admin")
                && Material.WOOD_AXE.equals(event.getItem().getType())) {
            if (action == Action.LEFT_CLICK_BLOCK)
                user.setPos1(new SimpleLocation(event.getClickedBlock().getLocation()));
            else if (action == Action.RIGHT_CLICK_BLOCK)
                user.setPos2(new SimpleLocation(event.getClickedBlock().getLocation()));
        }

        Block block = event.getClickedBlock();
        if (block == null || !(block.getState() instanceof Sign)) return;
        shopController.userClickSign(user, block);
    }


    @EventHandler
    public void entityDamageEntity(EntityDamageByEntityEvent event) {

        Entity damager = event.getDamager();
        User uDamager = null;
        Entity victim = event.getEntity();
        User uVictim = null;

        if (victim instanceof Player) {
            Location location = victim.getLocation();
            uDamager = userService.getOnlineUser((Player) victim);

            boolean cancel = protectionController.isPvpOn(uDamager, location);

            if (cancel && damager instanceof Player) uDamager.updatePvp();

            event.setCancelled(!cancel);
        }

        if (damager instanceof Player) {
            uVictim = userService.getOnlineUser((Player) damager);
        } else if (damager instanceof Arrow) {
            Projectile projectile = (Arrow) damager;

            if (projectile.getShooter() instanceof Player)
                uVictim = userService.getOnlineUser((Player) projectile.getShooter());
        }

        if (uVictim != null) {
            Location location = damager.getLocation();

            boolean cancel = protectionController.isAnimalPvpOn(uVictim, location);

            if (cancel && victim instanceof Player) uVictim.updatePvp();

            event.setCancelled(!cancel);
        }

        if (!event.isCancelled()) {
            DamageResult damageResult = new DamageResult(event.getDamage());

            if (uDamager != null && damager instanceof LivingEntity) {
                //Handle receiveDamage
                List<ReceiveDamage> list = uDamager.getByType(ReceiveDamage.class);
                for (ReceiveDamage receive : list) receive.onReceiveDamage((LivingEntity) damager, uDamager, damageResult);
            }

            if (uVictim != null && victim instanceof LivingEntity) {
                //Handle giveDamage
                List<GiveDamage> list = uVictim.getByType(GiveDamage.class);
                for (GiveDamage give : list) give.onGiveDamage(uVictim, (LivingEntity) victim, damageResult);
            }

            event.setDamage(damageResult.getDamage());
            event.setCancelled(damageResult.isCancelled());

            mobService.entityDamage(victim, damager);
        }
    }

    @EventHandler
    public void waterLavaSpread(BlockFromToEvent event) {

        boolean result = protectionController.canSpread(event.getBlock());

        event.setCancelled(!result);
    }

    @EventHandler
    public void fireSpread(BlockSpreadEvent event) {

        boolean result = protectionController.canSpread(event.getBlock());

        event.setCancelled(!result);
    }

    @EventHandler
    public void pistonEvent(BlockPistonExtendEvent event) {

        boolean result = protectionController.canPistonWork(event.getBlocks());

        event.setCancelled(!result);
    }

    @EventHandler
    public void pistonEvent(BlockPistonRetractEvent event) {

        boolean result = protectionController.canPistonWork(event.getBlocks());

        event.setCancelled(!result);
    }

    @EventHandler
    public void blockExplode(BlockExplodeEvent event) {

        boolean result = protectionController.blockExplode(event.blockList());

        event.setCancelled(!result);
    }

    @EventHandler
    public void blockExplode(EntityExplodeEvent event) {

        boolean result = protectionController.blockExplode(event.blockList());

        event.setCancelled(!result);
    }

    @EventHandler
    public void chunkLoadEvent(ChunkLoadEvent event) {

        Chunk chunk = event.getChunk();

        protectionController.chunkLoad(chunk.getX(), chunk.getZ(), event.getWorld());
        mobService.chunkLoad(chunk);
    }

    @EventHandler
    public void chunkUnloadEvent(ChunkUnloadEvent event) {
        Chunk chunk = event.getChunk();

        protectionController.chunkUnload(chunk.getX(), chunk.getZ(), event.getWorld());
        mobService.chunkUnload(event.getChunk());
    }

    @EventHandler
    public void onPortal(PlayerPortalEvent event) {
        User user = userService.getOnlineUser(event.getPlayer());

        Location to = portalService.getPortalDestination(user, event.getFrom());

        if (to != null) {
            Location old = event.getPlayer().getLocation();

            old.setX(to.getX());
            old.setY(to.getY() + 1);
            old.setZ(to.getZ());

            event.getPlayer().teleport(old);
        }
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        protectionController.worldLoad(event.getWorld());
    }
}
