package com.github.manolo8.simplecraft.listener;

import com.github.manolo8.simplecraft.core.chat.Chat;
import com.github.manolo8.simplecraft.core.commands.inventory.InventoryView;
import com.github.manolo8.simplecraft.core.world.WorldInfo;
import com.github.manolo8.simplecraft.core.world.WorldService;
import com.github.manolo8.simplecraft.core.world.container.IContainer;
import com.github.manolo8.simplecraft.interfaces.*;
import com.github.manolo8.simplecraft.module.clan.Clan;
import com.github.manolo8.simplecraft.module.crate.Crate;
import com.github.manolo8.simplecraft.module.shop.ShopProvider;
import com.github.manolo8.simplecraft.module.skill.tools.*;
import com.github.manolo8.simplecraft.module.machine.MachineProvider;
import com.github.manolo8.simplecraft.module.user.MessageType;
import com.github.manolo8.simplecraft.module.user.User;
import com.github.manolo8.simplecraft.module.user.UserFlag;
import com.github.manolo8.simplecraft.module.user.UserService;
import com.github.manolo8.simplecraft.module.user.identity.Identity;
import com.github.manolo8.simplecraft.module.warp.WarpService;
import com.github.manolo8.simplecraft.utils.bot.BotDetector;
import com.github.manolo8.simplecraft.utils.calculator.MoneyCalculator;
import com.github.manolo8.simplecraft.utils.def.StringUtils;
import com.github.manolo8.simplecraft.utils.mc.InventoryUtils;
import com.github.manolo8.simplecraft.utils.mc.MaterialList;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.*;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.event.server.TabCompleteEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Sign;
import org.bukkit.projectiles.ProjectileSource;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import static org.apache.commons.lang.math.NumberUtils.toInt;

@SuppressWarnings("unused")
public class MainListener implements Listener {

    private final static BlockFace[] faces;
    private final static Pattern pattern;
    public static boolean locker;
    public static long disabledRegister;

    static {
        faces = new BlockFace[5];
        faces[0] = BlockFace.SOUTH;
        faces[3] = BlockFace.NORTH;
        faces[1] = BlockFace.WEST;
        faces[2] = BlockFace.EAST;
        faces[4] = BlockFace.UP;

        pattern = Pattern.compile("[A-Za-z0-9_]{3,20}");
    }

    private final WorldService worldService;
    private final UserService userService;
    private final Chat chat;
    private final BotDetector botDetector;

    public MainListener(WorldService worldService,
                        UserService userService) {
        this.worldService = worldService;
        this.userService = userService;
        this.chat = userService.getChat();
        this.botDetector = new BotDetector();
    }

    @EventHandler
    public void ping(ServerListPingEvent event) {
        System.out.println(event.getAddress().toString());

        Iterator<Player> i = event.iterator();

        while (i.hasNext()) {
            Player player = i.next();

            User user = userService.getLogged(player);

            if (!user.isAuthenticated() || user.isHidden()) {
                i.remove();
            }

        }

        botDetector.onIpPing(event.getAddress().toString());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void preLogin(AsyncPlayerPreLoginEvent event) {

        Identity identity = null;

        try {
            identity = userService.findIdentity(event.getName());
        } catch (Exception e) {
            e.printStackTrace();
            event.setKickMessage("§cHouve um erro interno. Contate um ADMIN!");
        }

        String address = event.getAddress().toString();

        if (botDetector.cantConnect(address)) {

            event.disallow(Result.KICK_OTHER, "§cVocê atingiu o limite de IP's simultâneos! (3)");

        } else if (identity == null) {

            if (!botDetector.isAddedToServerList(address)) {
                event.disallow(Result.KICK_OTHER, "§cAdicione o servidor a lista de servidores!");
            } else if (botDetector.disallow(event.getName(), address)) {
                event.disallow(Result.KICK_OTHER, "§cLogin bloqueado. Tente novamente mais tarde");
            } else if (!pattern.matcher(event.getName()).matches()) {
                event.disallow(Result.KICK_OTHER, "§cCaracteres permitidos: (A-Z, a-z, 0-9 e _) Entre 4 e 20 caracteres.");
            } else if (disabledRegister > System.currentTimeMillis()) {
                event.disallow(Result.KICK_OTHER, "§cRegistro de novas contas bloqueado temporariamente.");
            }

        } else if (!identity.getName().equals(event.getName())) {
            event.disallow(Result.KICK_OTHER, "§cVocê entrou com um nick um pouco diferente! " +
                    "\nPrecisa ser exatamente §a" + identity.getName() + "§c ao invez de §a" + event.getName());
        } else if (identity.isBanned()) {
            event.disallow(Result.KICK_BANNED, "§cVocê está banido por " + identity.getBanTime() + "!");
        } else if (identity.isOnline() && identity.user().base().isOnline()) {
            User user = identity.user();

            if (user.isAuthenticated()) {
                event.disallow(Result.KICK_OTHER, "§cO jogador já esta logado!");
            } else {
                int trying = (int) (user.getCurrentOnlineTime() / 1000);
                event.disallow(Result.KICK_OTHER, "§cO jogador está tentando se conectar! Tente novamente em " + (60 - trying) + "s");
            }

        }
    }

    @EventHandler
    public void playerJoin(PlayerJoinEvent event) {

        try {
            final Player player = event.getPlayer();

            userService.join(player);

            if (player.getGameMode() == GameMode.CREATIVE && !player.isOp()) {
                player.setGameMode(GameMode.SURVIVAL);
            }

        } catch (SQLException e) {
            event.getPlayer().kickPlayer("§cHouve um erro");
            e.printStackTrace();
        }

        event.setJoinMessage(null);
    }

    @EventHandler
    public void playerQuit(PlayerQuitEvent event) {

        final User user = userService.getLogged(event.getPlayer());

        if (user.isInPvp() && user.iContainer().protection().isPvpOn()) {

            user.base().setHealth(0);

            UserService.broadcastAction("§c" + user.identity().getName() + " fugiu em batalha!");
        }

        userService.quit(event.getPlayer());

        event.setQuitMessage(null);
    }

    @EventHandler
    public void respawn(PlayerRespawnEvent event) {

        if (!event.isBedSpawn() && WarpService.findSpawn() != null) {
            event.setRespawnLocation(WarpService.findSpawn().asLocation());
        }

    }

    @EventHandler
    public void worldLoad(WorldLoadEvent event) {
        worldService.worldLoad(event.getWorld());
    }

    @EventHandler
    public void worldUnload(WorldUnloadEvent event) {
        worldService.worldUnload(event.getWorld());
    }

    @EventHandler
    public void chunkLoad(ChunkLoadEvent event) {
        worldService.chunkLoad(event.getChunk());
    }

    @EventHandler
    public void chunkUnload(ChunkUnloadEvent event) {
        worldService.chunkUnload(event.getChunk());
    }

    @EventHandler
    public void chat(PlayerCommandPreprocessEvent event) {
        User user = userService.getLogged(event.getPlayer());

        //LOGIN HANDLE
        String msg = event.getMessage().toLowerCase();

        if (msg.startsWith("/register") || msg.startsWith("/login")) {
            event.setCancelled(false);
            return;
        }

        cantContinue(user, event);
        //LOGIN HANDLE
    }

    @EventHandler
    public void onTabComplete(TabCompleteEvent event) {

        CommandSender sender = event.getSender();

        if ((sender instanceof Player)) {
            User user = userService.getLogged((Player) sender);

            //LOGIN HANDLE
            cantContinue(user, event);
            //LOGIN HANDLE
        }
    }

    @EventHandler
    public void chat(AsyncPlayerChatEvent event) {
        event.setCancelled(true);

        User user = userService.getLogged(event.getPlayer());

        //LOGIN HANDLE
        if (cantContinue(user, event)) return;
        //LOGIN HANDLE

        chat.userChatMessage(user, event.getMessage());
    }

    @EventHandler
    public void move(PlayerMoveEvent event) {
        final User user = userService.getLogged(event.getPlayer());

        final Location to = event.getTo();

        final int x = to.getBlockX();
        final int y = to.getBlockY();
        final int z = to.getBlockZ();

        //LOGIN HANDLE
        if (cantContinue(user, event)) {

            final Location from = event.getFrom();

            if (from.getBlockX() == x && from.getBlockZ() == z && from.getBlockY() >= y) {
                event.setCancelled(false);
            }

        } else {
            event.setCancelled(user.iContainer().refreshUser(user, x, y, z, false));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void teleport(PlayerTeleportEvent event) {
        User user = userService.getLogged(event.getPlayer());

        WorldInfo info = user.worldInfo();

        if (event.getTo().getWorld() != event.getFrom().getWorld()) {
            info = worldService.findByWorld(event.getTo().getWorld());
        }

        if (!info.getContainer().update(event.getTo()).protection().canEnter(user)) {
            event.setCancelled(true);
        } else {

            if (info != user.worldInfo()) {
                user.updateWorldInfo(info);
            }

        }
    }

    @EventHandler
    public void playerWorldChange(PlayerChangedWorldEvent event) {
        User user = userService.getLogged(event.getPlayer());

        WorldInfo info = worldService.findByWorld(event.getPlayer().getWorld());

        if (info != user.worldInfo()) {
            System.out.println("Error on player change world event (not handled by teleport event... why?)");
            user.updateWorldInfo(info);
        }

    }

    @EventHandler
    public void shift(PlayerToggleSneakEvent event) {
        User user = userService.getLogged(event.getPlayer());

        //LOGIN HANDLE
        if (cantContinue(user, event)) return;
        //LOGIN HANDLE

        if (event.isSneaking() && user.flags().allowShiftSell()) {

            if (user.iContainer().protection().isPvpOn()) {

                user.sendAction("§cPVP Ativo! Desativando auto-sell.");
                UserFlag.shiftSell.set(user, User::flags, false);

            } else {

                double coins = MoneyCalculator.extractCoins(user.getSellItems(), user.base().getInventory());

                if (coins != 0) {
                    if (user.hasPermission("simplecraft.vip")) coins *= 1.3;
                    user.sendAction("§a+ §e" + StringUtils.doubleToString(coins) + " §acoins");
                    user.money().depositCoins(coins);
                }

            }
        }
    }

    @EventHandler
    public void inventoryOpen(InventoryOpenEvent event) {
        Inventory inventory = event.getInventory();

        if (inventory instanceof EnchantingInventory) {
            EnchantingInventory enchanting = (EnchantingInventory) inventory;

            enchanting.setSecondary(new ItemStack(Material.LAPIS_LAZULI, 64));
        }
    }

    @EventHandler
    public void inventoryClose(InventoryCloseEvent event) {
        Inventory inventory = event.getInventory();

        User user = userService.getLogged((Player) event.getPlayer());

        if (inventory instanceof EnchantingInventory) {
            EnchantingInventory enchanting = (EnchantingInventory) inventory;

            enchanting.setSecondary(new ItemStack(Material.AIR));
        }


        InventoryView view = user.getInventoryView();

        if (view == null || view.opening) return;

        user.getInventoryView().close(false);
        user.setInventoryView(null);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void inventoryDrag(InventoryDragEvent event) {

        User user = userService.getLogged((Player) event.getWhoClicked());

        //LOGIN HANDLE
        if (cantContinue(user, event)) return;
        //LOGIN HANDLE

        InventoryView view = user.getInventoryView();

        if (view != null) event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void inventoryClick(InventoryClickEvent event) {

        User user = userService.getLogged((Player) event.getWhoClicked());

        //LOGIN HANDLE
        if (cantContinue(user, event)) return;
        //LOGIN HANDLE

        InventoryView view = user.getInventoryView();

        ItemStack cursor = event.getCursor();
        InventoryAction action = event.getAction();

        Inventory inventory = event.getInventory();
        Inventory clicked = event.getClickedInventory();

        if (view == null) {

            if (clicked == null) {
                ItemStack item = clicked.getItem(event.getSlot());
                if (item != null && item.getType() == Material.LAPIS_LAZULI && (clicked instanceof EnchantingInventory || inventory instanceof EnchantingInventory)) {
                    event.setCancelled(true);
                }
            }

            //Repair item handle
            if (cursor.getType() == Material.GRAY_GLAZED_TERRACOTTA
                    && cursor.hasItemMeta()
                    && cursor.getItemMeta().getDisplayName().equals("§6Reparador de ítens")) {

                if (clicked != null) {
                    ItemStack item = clicked.getItem(event.getSlot());
                    if (item != null && item.getType().getMaxDurability() != 0) {
                        if (user.isInPvp()) {
                            user.playSound(Sound.BLOCK_ANVIL_LAND, 20, 20, false);
                        } else {
                            user.base().updateInventory();
                            item.setDurability((short) 0);
                            cursor.setAmount(cursor.getAmount() - 1);
                            event.setCancelled(true);
                        }
                    }
                }
            }
            //Repair item handle

            //InventoryChangeHandler
            inventoryChange(clicked);

            if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
                inventoryChange(inventory);
            }
            //InventoryChangeHandler

        } else {

            event.setCancelled(true);

            if (user.timeChecker(4, 200)) return;

            if (clicked instanceof PlayerInventory) {
                if (action == InventoryAction.PLACE_ALL
                        || action == InventoryAction.PICKUP_ALL
                        || action == InventoryAction.PICKUP_ONE
                        || action == InventoryAction.PLACE_ONE) event.setCancelled(false);

                return;
            }

            view.handleClick(event.getSlot(), cursor, event.isLeftClick());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void chest(InventoryMoveItemEvent event) {
        inventoryChange(event.getDestination());
        inventoryChange(event.getInitiator());
    }


    private void inventoryChange(Inventory inventory) {
        if (inventory == null) return;

        InventoryHolder holder = inventory.getHolder();

        if (holder == null) return;

        Location loc = null;

        if (holder instanceof DoubleChest) {
            loc = ((DoubleChest) holder).getLocation();
        } else if (holder instanceof Chest) {
            loc = ((Chest) holder).getLocation();
        }

        if (loc != null) {
            IContainer iContainer = worldService.findByWorld(loc.getWorld()).getContainer().update(loc);

            iContainer.doInClosest(InventoryChange.class, InventoryChange::onInventoryChange);
        }
    }

    @EventHandler
    public void projectile(ProjectileLaunchEvent event) {

        ProjectileSource source = event.getEntity().getShooter();

        if (source instanceof Player) {
            User user = userService.getLogged((Player) source);

            //LOGIN HANDLE
            if (cantContinue(user, event)) return;
            //LOGIN HANDLE

            if (event.getEntity().getType() == EntityType.ENDER_PEARL) {
                user.sendAction("§cFuncionalidade desabilitada!");
                event.setCancelled(true);
            } else {
                IContainer iContainer = user.iContainer().update(user.base().getLocation());

                if (!(iContainer.protection().isPvpOn() || iContainer.protection().isPveOn(user))) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void projectileShoot(ProjectileHitEvent event) {
        ProjectileSource source = event.getEntity().getShooter();

        if (source instanceof Player) {
            User user = userService.getLogged((Player) source);

            Projectile projectile = event.getEntity();

            user.skill().getByType(Damage.class).forEach(damage -> damage.onProjectileHit(projectile));

            if (user.iContainer() instanceof ProjectileHit) {
                ((ProjectileHit) user.iContainer()).onProjectileHit(user, event);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void entityInteract(PlayerInteractEntityEvent event) {

        User user = userService.getLogged(event.getPlayer());

        Entity entity = event.getRightClicked();

        EntityType type = entity.getType();

        Material clicked = type == EntityType.ITEM_FRAME ? Material.ITEM_FRAME
                : type == EntityType.ARMOR_STAND ? Material.ARMOR_STAND
                : type == EntityType.MINECART_CHEST ? Material.CHEST_MINECART
                : type == EntityType.MINECART_HOPPER ? Material.HOPPER_MINECART
                : type == EntityType.PAINTING ? Material.PAINTING
                : null;

        if (clicked != null) {
            IContainer iContainer = user
                    .iContainer()
                    .update(entity.getLocation());

            boolean result = iContainer.protection().canInteract(user, clicked);

            event.setCancelled(!result);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void breakEntity(HangingBreakEvent event) {

        if (event.getCause() != HangingBreakEvent.RemoveCause.EXPLOSION) return;

        Entity entity = event.getEntity();

        IContainer iContainer = worldService
                .findByWorld(entity.getWorld())
                .getContainer()
                .update(entity.getLocation());

        boolean result = iContainer.protection().canExplode();

        event.setCancelled(!result);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void breakEntity(HangingBreakByEntityEvent event) {

        Entity remover = event.getRemover();

        if (remover instanceof Player) {

            User user = userService.getLogged((Player) remover);

            //LOGIN HANDLE
            if (cantContinue(user, event)) return;
            //LOGIN HANDLE

            IContainer iContainer = user.iContainer().update(event.getEntity().getLocation());

            boolean result = iContainer.protection().canBreak(user, Material.ITEM_FRAME);

            event.setCancelled(!result);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void placeEntity(HangingPlaceEvent event) {

        User user = userService.getLogged(event.getPlayer());

        //LOGIN HANDLE
        if (cantContinue(user, event)) return;
        //LOGIN HANDLE

        IContainer iContainer = user
                .iContainer()
                .update(event.getBlock());

        //item frame é quadro vai dar na mesma
        if (!iContainer.protection().canPlace(user, Material.ITEM_FRAME)) {
            event.setCancelled(true);
        }
    }

    /**
     * Handle:
     * IContainer permission
     * Magic skills
     * IContainer interaction
     *
     * @param event
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void interact(PlayerInteractEvent event) {

        User user = userService.getLogged(event.getPlayer());

        //LOGIN HANDLE
        if (cantContinue(user, event)) return;
        //LOGIN HANDLE

        ItemStack item = event.getItem();
        Material hand = item == null ? Material.AIR : item.getType();
        Action action = event.getAction();

        if (hand == Material.STONE) {
            user.worldInfo().getContainer().addContainer(new Crate(user));
        }

        if (event.getHand() != EquipmentSlot.HAND) {
            event.setCancelled(true);
        } else {

            //make that better
            if (hand == Material.STICK) {
                ItemStack wand = event.getItem();
                List<Interactable> interactables = user.skill().getByType(Interactable.class);

                boolean has = false;

                for (Interactable interactable : interactables) {
                    if (interactable.match(item, action)) {
                        interactable.onInteract();
                        has = true;
                    }
                }

                if (has) {
                    event.setCancelled(true);
                    return;
                }
            }


            Block block = event.getClickedBlock();

            IContainer iContainer = user.iContainer();

            if (block == null) {

                iContainer.doInClosest(AnyInteraction.class, anyInteraction -> anyInteraction.onInteract(user, event));

            } else if (!event.isCancelled()) {

                iContainer = iContainer.update(block);

                iContainer.doInClosest(AnyInteraction.class, anyInteraction -> anyInteraction.onInteract(user, event));

                Material material = block.getType();

                if (action == Action.RIGHT_CLICK_BLOCK) {

                    boolean interactable = iContainer.hasClosestType(BlockInteract.class);

                    if (interactable || MaterialList.isInteractable(material)) {

                        if (!iContainer.protection().canInteract(user, material)) {
                            event.setCancelled(true);
                            return;
                        }

                    }

                    if (interactable) {
                        iContainer.doInClosest(BlockInteract.class, blockInteract -> blockInteract.onBlockInteract(user, event));
                    }

                }
            }

            if (hand == Material.WOODEN_AXE && user.isAdmin()) {
                if (action == Action.LEFT_CLICK_BLOCK) {
                    user.points().setOne(event.getClickedBlock().getLocation());
                } else if (action == Action.RIGHT_CLICK_BLOCK) {
                    user.points().setTwo(event.getClickedBlock().getLocation());
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void playerPlaceBlock(BlockPlaceEvent event) {

        User user = userService.getLogged(event.getPlayer());

        //LOGIN HANDLE
        if (cantContinue(user, event)) return;
        //LOGIN HANDLE

        Block block = event.getBlock();

        IContainer iContainer = user.iContainer().update(block);

        if (iContainer.protection().canPlace(user, block.getType())) {

            iContainer.doInClosest(BlockPlace.class, blockPlace -> blockPlace.onBlockPlace(user, event));

            Material material = event.getBlockPlaced().getType();

            if (material == Material.SPAWNER) {
                MachineProvider provider = user.worldInfo().getProvider(MachineProvider.class);
                if (provider != null) provider.onPlace(user, event);
            }

        } else {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void playerBreakBlock(BlockBreakEvent event) {
        Material hand = event.getPlayer().getInventory().getItemInMainHand().getType();

        if (hand == Material.DIAMOND_SWORD
                || hand == Material.STONE_SWORD
                || hand == Material.GOLDEN_SWORD
                || hand == Material.IRON_SWORD
                || hand == Material.WOODEN_SWORD) {

            event.setCancelled(true);

        } else {

            User user = userService.getLogged(event.getPlayer());

            //LOGIN HANDLE
            if (cantContinue(user, event)) return;
            //LOGIN HANDLE

            Block block = event.getBlock();

            IContainer iContainer = user.iContainer().update(block);

            if (iContainer.protection().canBreak(user, block.getType())) {

                iContainer.doInClosest(BlockBreak.class, blockBreak -> blockBreak.onBreak(user, event));

                if (event.isCancelled()) return;

                if (hand == Material.DIAMOND_HOE ||
                        hand == Material.IRON_HOE ||
                        hand == Material.GOLDEN_HOE ||
                        hand == Material.STONE_HOE ||
                        hand == Material.WOODEN_HOE) {

                    user.skill().getByType(BlockFarm.class).forEach(blockFarm -> blockFarm.onFarm(event));

                }

                //Check faces
                for (BlockFace face : faces) {
                    Block relative = block.getRelative(face);
                    if (relative.getType() == Material.WALL_SIGN || (face == BlockFace.UP && relative.getType() == Material.SIGN)) {

                        iContainer = iContainer.update(relative);

                        Sign sign = (Sign) relative.getState().getData();

                        if (!block.equals(relative.getRelative(sign.getAttachedFace()))) continue;

                        if (iContainer.protection().canBreak(user, Material.SIGN)) {
                            iContainer.doInClosest(BlockBreak.class, blockBreak -> blockBreak.onBreak(user, event));
                        } else {
                            event.setCancelled(true);
                        }
                    }
                }
            } else {
                event.setCancelled(true);
            }

        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void entityDamage(EntityDamageByEntityEvent event) {

        Entity damager = event.getDamager();
        User userDamager = null;
        Entity victim = event.getEntity();
        User userVictim = null;

        if (damager instanceof Player) {
            userDamager = userService.getLogged((Player) damager);
        } else if (damager instanceof Projectile) {
            Projectile projectile = (Projectile) damager;

            if (projectile.getShooter() instanceof Player) {
                userDamager = userService.getLogged((Player) projectile.getShooter());
            }

        }

        if (victim instanceof Player) {
            userVictim = userService.getLogged((Player) victim);
        }

        DamageResult result = new DamageResult(event.getDamage());
        result.setCancelled(true);

        if (userVictim != null && userDamager != null) {
            //Player VS Player

            //Não da update por que é atualizado no PlayerMoveEvent
            if (userVictim.iContainer().protection().isPvpOn() && userDamager.iContainer().protection().isPvpOn()) {

                Clan one = userVictim.clan().get();
                Clan two = userDamager.clan().get();

                //Verifica se os clans não estão null e se é o mesmo clan
                //Caso seja, verifica se friendlyfire está ativo
                if (one == null || one != two || one.isFriendlyFire()) {
                    result.setCancelled(false);

                    userVictim.updateLastDamage();
                    userDamager.updateLastDamage();

                    userDamager.skill().getByType(Damage.class)
                            .forEach(damage -> damage.onGive((LivingEntity) victim, result));

                    final LivingEntity realDamager = userDamager.base();

                    userVictim.skill().getByType(Damage.class)
                            .forEach(damage -> damage.onReceive(realDamager, result));

                    userVictim.setLastDamage(userDamager);
                }

            }

        } else if (userDamager != null) {
            //Player VS mob

            IContainer iContainer = userDamager.iContainer().update(victim.getLocation());

            if (victim instanceof Hanging) {
                result.setCancelled(!iContainer.protection().canBreak(userDamager, Material.ITEM_FRAME));
            } else if (iContainer.protection().isPveOn(userDamager)) {

                result.setCancelled(false);

                if (victim instanceof LivingEntity) {

                    userDamager.skill().getByType(Damage.class)
                            .forEach(damage -> damage.onGive((LivingEntity) victim, result));

                    User finalUserDamager = userDamager;

                    iContainer.doInClosest(EntityDamage.class, entityDamage -> entityDamage.onEntityReceiveDamage(finalUserDamager, (LivingEntity) victim));
                }
            }

        } else if (userVictim != null) {
            //mob VS Player

            IContainer iContainer = userVictim.iContainer().update(damager.getLocation());

            if (iContainer.protection().isPveOn(userVictim)) {

                result.setCancelled(false);
                userVictim.setLastDamage(null);

                if (damager instanceof LivingEntity) userVictim.skill().getByType(Damage.class)
                        .forEach(damage -> damage.onReceive((LivingEntity) damager, result));
            }

        } else result.setCancelled(true); /* mob vs mob */

        event.setDamage(result.getDamage());
        event.setCancelled(result.isCancelled());
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void itemDamage(PlayerItemDamageEvent event) {

        User user = userService.getLogged(event.getPlayer());

        //LOGIN HANDLE
        if (cantContinue(user, event)) return;
        //LOGIN HANDLE

        ItemStack item = event.getItem();

        int id = item.getType().getId();

        //ARMOR
        if (id >= 298 && id <= 317) {
            DamageItemResult result = new DamageItemResult(event.getDamage());

            result.setUnbreaking(item.getEnchantmentLevel(Enchantment.DURABILITY));

            if (user.getLastDamage() != null && user.getLastDamage().skill() != null) {
                user.getLastDamage().skill().getByType(ItemDamage.class)
                        .forEach(damage -> damage.onGive(result));
            }

            user.skill().getByType(ItemDamage.class)
                    .forEach(damage -> damage.onReceive(result));

            event.setDamage(result.buildDamage());
        }
    }

    @EventHandler
    public void entityTarget(EntityTargetEvent event) {
        if (event.getTarget() instanceof Player) {

            User user = userService.getLogged((Player) event.getTarget());

            IContainer iContainer = user.iContainer();

            if (!iContainer.protection().isPveOn(user)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void entityDamage(EntityDamageEvent event) {

        if (!(event.getEntity() instanceof Player)) return;

        User user = userService.getLogged((Player) event.getEntity());

        //LOGIN HANDLE
        if (cantContinue(user, event)) return;
        //LOGIN HANDLE

        Protection protection = user.iContainer().protection();

        if (protection.isPveOn(user) || protection.isPvpOn()) {
            if (event.getCause() == DamageCause.FALL) {

                DamageResult result = new DamageResult(event.getDamage());

                user.skill().getByType(Damage.class).forEach(damage -> damage.onFall(result));

                event.setDamage(result.getDamage());
                event.setCancelled(result.isCancelled());
            }
        } else if (event.getCause() == DamageCause.VOID) {
            user.base().teleport(user.base().getWorld().getSpawnLocation());
            user.sendAction("§aSalvo pelo GONGO! =)");
        } else {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void food(FoodLevelChangeEvent event) {

        if (event.getEntity() instanceof Player) {
            User user = userService.getLogged((Player) event.getEntity());

            //LOGIN HANDLE
            if (cantContinue(user, event)) return;
            //LOGIN HANDLE

            Protection protection = user.iContainer().protection();

            if (!(protection.isPveOn(user) || protection.isPvpOn())) {
                event.setFoodLevel(20);
            }
        }
    }

    @EventHandler
    public void playerDeath(PlayerDeathEvent event) {
        event.setDeathMessage(null);

        User user = userService.getLogged(event.getEntity());
        IContainer iContainer = user.iContainer();

        if (iContainer.protection().isPvpOn()) {
            Player player = event.getEntity().getKiller();

            User killer = player == null ? null : userService.getLogged(player);

            if (killer == null) {
                MoneyCalculator.process(event.getDrops(), user.hasPermission("simplecraft.vip") ? 1.3D : 1D);
            } else {
                killer.clan().onKill(user.clan());
                MoneyCalculator.process(event.getDrops(), killer.hasPermission("simplecraft.vip") ? 1.3D : 1D);
            }

        } else {
            //Se o PVP não estiver ON, mantem o inventário
            event.setKeepLevel(true);
            event.setKeepInventory(true);

            event.setDroppedExp(0);

            user.sendAction("§cVocê morreu em um local com PVP desativado! Itens mantidos.");
        }
    }

    @EventHandler
    public void entityDeath(EntityDeathEvent event) {

        Entity entity = event.getEntity();

        if (entity instanceof Creature || entity instanceof Slime) {

            event.getDrops().clear();

            Player killer = ((LivingEntity) entity).getKiller();

            if (killer != null) {

                User user = userService.getLogged(killer);

                IContainer iContainer = user.iContainer();

                iContainer.doInClosest(EntityDeath.class, entityDeath -> entityDeath.onEntityDeath(user, entity, event.getDrops()));

            }
        }

    }

    @EventHandler
    public void itemSpawn(ItemSpawnEvent event) {

        Item one = event.getEntity();

        List<Entity> nearby = one.getNearbyEntities(3, 3, 3);

        ItemStack stack = one.getItemStack();

        for (Entity entity : nearby) {
            if (entity instanceof Item) {

                Item found = (Item) entity;

                if (found.getItemStack().isSimilar(stack)) {
                    checkCanMerge(found, one);
                    event.setCancelled(true);
                    return;
                }

            }
        }

    }

    @EventHandler
    public void itemMergeEvent(ItemMergeEvent event) {
        checkCanMerge(event.getEntity(), event.getTarget());
        event.setCancelled(true);
    }

    private void checkCanMerge(Item one, Item two) {
        if (one.getCustomName() != null && two.getCustomName() != null) {
            one.setCustomName(String.valueOf(toInt(one.getCustomName()) + toInt(two.getCustomName())));
            two.remove();
        } else if (one.getCustomName() != null && two.getCustomName() == null) {
            one.setCustomName(String.valueOf(toInt(one.getCustomName()) + two.getItemStack().getAmount()));
            two.remove();
        } else if (one.getCustomName() == null && two.getCustomName() != null) {
            one.setCustomName(String.valueOf(toInt(two.getCustomName()) + one.getItemStack().getAmount()));
            one.setCustomNameVisible(true);
            two.remove();
        } else {
            one.setCustomName(String.valueOf(one.getItemStack().getAmount() + two.getItemStack().getAmount()));
            one.getItemStack().setAmount(1);
            one.setCustomNameVisible(true);
            two.remove();
        }
    }

    @EventHandler
    public void itePickUp(InventoryPickupItemEvent event) {

        Item item = event.getItem();
        ItemStack stack = item.getItemStack();

        int quantity = 0;
        if (item.getCustomName() != null) quantity = NumberUtils.toInt(item.getCustomName());

        if (quantity != 0) {

            Inventory inventory = event.getInventory();

            event.setCancelled(true);

            quantity = InventoryUtils.addItemTo(inventory, item.getItemStack(), quantity);

            if (quantity == 0) {
                item.remove();
            } else {
                item.setCustomName(String.valueOf(quantity));
            }

        }
    }

    @EventHandler
    public void itemPickUp(EntityPickupItemEvent event) {
        if (event.getEntity() instanceof Player) {

            User user = userService.getLogged((Player) event.getEntity());

            //LOGIN HANDLE
            if (cantContinue(user, event)) return;
            //LOGIN HANDLE

            Item item = event.getItem();
            ItemStack stack = item.getItemStack();

            int quantity = 0;
            if (item.getCustomName() != null) quantity = NumberUtils.toInt(item.getCustomName());


            if (stack.getType() == Material.GOLD_NUGGET && stack.hasItemMeta()) {
                ItemMeta meta = stack.getItemMeta();

                String display = meta.getDisplayName();

                if (display != null && display.startsWith("§") && display.endsWith(" coins")) {
                    double coins = NumberUtils.toDouble(display.substring(2, display.length() - 6));

                    int amount = quantity == 0 ? stack.getAmount() : quantity;

                    user.money().depositCoins(coins * amount);

                    item.remove();

                    event.setCancelled(true);
                }

            } else if (quantity != 0) {
                Inventory inventory = ((Player) event.getEntity()).getInventory();

                event.setCancelled(true);

                quantity = InventoryUtils.addItemTo(inventory, item.getItemStack(), quantity);

                if (quantity == 0) {
                    item.remove();
                } else {
                    item.setCustomName(String.valueOf(quantity));
                }

            }
        }
    }

    @EventHandler
    public void playerChangeSign(SignChangeEvent event) {
        User user = userService.getLogged(event.getPlayer());

        //LOGIN HANDLE
        if (cantContinue(user, event)) return;
        //LOGIN HANDLE

        ShopProvider provider = user.worldInfo().getProvider(ShopProvider.class);

        if (provider != null) provider.signCreateEvent(user, event);

        if (user.hasPermission("simplecraft.vip")) {
            String[] lines = event.getLines();
            for (int i = 0, linesLength = lines.length; i < linesLength; i++) {
                String line = lines[i];

                event.setLine(i, StringUtils.toStringWithColors(lines[i]));
            }
        }
    }

    @EventHandler
    public void weather(WeatherChangeEvent event) {
        if (!locker) event.setCancelled(true);
    }

    @EventHandler
    public void leafDecay(LeavesDecayEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void waterLavaSpread(BlockFromToEvent event) {

        Block block = event.getBlock();

        if (!worldService.findByWorld(block.getWorld()).getContainer().update(block).protection().canSpread(block.getType())) {
            event.setCancelled(true);
        }

    }

    @EventHandler
    public void fireSpread(BlockSpreadEvent event) {

        Block block = event.getBlock();

        if (!worldService.findByWorld(block.getWorld()).getContainer().update(block).protection().canSpread(block.getType())) {
            event.setCancelled(true);
        }

    }

    @EventHandler
    public void pistonEvent(BlockPistonExtendEvent event) {
        Block block = event.getBlock();

        IContainer iContainer = worldService.findByWorld(block.getWorld()).getContainer().update(block);

        for (Block affected : event.getBlocks()) {

            if (!iContainer.update(affected).protection().canPistonWork()) {
                event.setCancelled(true);
                return;
            }

        }
    }

    @EventHandler
    public void pistonEvent(BlockPistonRetractEvent event) {
        Block block = event.getBlock();

        IContainer iContainer = worldService.findByWorld(block.getWorld()).getContainer().update(block);

        for (Block affected : event.getBlocks()) {

            if (!iContainer.update(affected).protection().canPistonWork()) {
                event.setCancelled(true);
                return;
            }

        }
    }

    @EventHandler
    public void blockExplode(BlockExplodeEvent event) {
        Block block = event.getBlock();

        IContainer iContainer = worldService.findByWorld(block.getWorld()).getContainer().update(block);

        for (Block affected : event.blockList()) {

            if (!iContainer.update(affected).protection().canExplode()) {
                event.setCancelled(true);
                return;
            }

        }
    }

    @EventHandler
    public void entityExplode(EntityExplodeEvent event) {
        Location location = event.getLocation();

        IContainer iContainer = worldService.findByWorld(location.getWorld()).getContainer().update(location);

        for (Block affected : event.blockList()) {

            if (!iContainer.update(affected).protection().canExplode()) {
                event.setCancelled(true);
                return;
            }

        }
    }

    private boolean cantContinue(User user, Cancellable cancellable) {
        if (user == null || user.base() == null) {
            cancellable.setCancelled(true);
            return true;
        } else if (user.isAuthenticated()) {
            return false;
        } else {

            cancellable.setCancelled(true);

            if (user.getCurrentOnlineTime() > 60000) {
                user.base().kickPlayer("§cVocê está tentando fazer login a 1 minuto...");
            } else {
                if (user.lastInfo(0, 6000)) {
                    if (user.getPassword() == null) {
                        user.base().sendTitle("§aSimpleCraft", "§eUse /register senha senha :)", 10, 40, 10);
                        user.base().sendMessage(MessageType.INFO.format("Use /register senha senha"));
                    } else {
                        user.base().sendTitle("§aSimpleCraft", "§eUse /login senha", 10, 40, 10);
                        user.base().sendMessage(MessageType.INFO.format("Use /login senha"));
                    }
                }
            }

            return true;
        }
    }
}
