package com.github.manolo8.simplecraft.module.user;

import com.github.manolo8.simplecraft.core.chat.Chat;
import com.github.manolo8.simplecraft.core.commands.line.*;
import com.github.manolo8.simplecraft.core.commands.line.annotation.*;
import com.github.manolo8.simplecraft.core.commands.line.inf.Supplier;
import com.github.manolo8.simplecraft.core.placeholder.PlaceHolder;
import com.github.manolo8.simplecraft.core.placeholder.PlaceHolderBuilder;
import com.github.manolo8.simplecraft.core.placeholder.annotation.PlaceHolderMapping;
import com.github.manolo8.simplecraft.core.service.HolderService;
import com.github.manolo8.simplecraft.core.world.WorldInfo;
import com.github.manolo8.simplecraft.core.world.WorldService;
import com.github.manolo8.simplecraft.listener.MainListener;
import com.github.manolo8.simplecraft.module.board.BoardService;
import com.github.manolo8.simplecraft.module.group.user.GroupUser;
import com.github.manolo8.simplecraft.module.skin.SkinService;
import com.github.manolo8.simplecraft.module.tag.TagUser;
import com.github.manolo8.simplecraft.module.user.games.SnakeView;
import com.github.manolo8.simplecraft.module.user.identity.Identity;
import com.github.manolo8.simplecraft.module.user.view.PreferencesView;
import com.github.manolo8.simplecraft.module.user.view.SellListView;
import com.github.manolo8.simplecraft.module.warp.Warp;
import com.github.manolo8.simplecraft.module.warp.WarpService;
import com.github.manolo8.simplecraft.utils.bot.IpUtils;
import com.github.manolo8.simplecraft.utils.def.Executer;
import com.github.manolo8.simplecraft.utils.def.StringUtils;
import com.github.manolo8.simplecraft.utils.entity.EntityUtils;
import com.github.manolo8.simplecraft.utils.mc.ItemStackUtils;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.sql.SQLException;
import java.util.*;

@SuppressWarnings("unused")
public class UserService extends HolderService<User, UserRepository> {

    public static int tick;
    public static int atick;

    private static UserService instance;

    private final WorldService worldService;
    private final SkinService skinService;
    private final BoardService boardService;
    private final HashMap<UUID, User> userMap;
    private final Chat chat;

    public UserService(WorldService worldService,
                       UserRepository repository,
                       SkinService skinService,
                       BoardService boardService) {
        super(repository);

        this.worldService = worldService;
        this.skinService = skinService;
        this.boardService = boardService;
        this.chat = new Chat(this);
        this.userMap = new HashMap();

        instance = this;
    }

    //======================================================
    //========================STATIC========================
    //======================================================

    public static void broadcastChat(Object message) {
        instance.eachExecute(user -> user.sendMessage(message));
    }

    public static void broadcastAction(Object message) {
        instance.eachExecute(user -> user.sendAction(message));
    }

    public static void broadcastTitle(Object title, Object message) {
        instance.eachExecute(user -> user.sendTitle(title, message));
    }

    public static int countAddress(long address) {
        synchronized (instance.entities) {
            int i = 0;

            for (User user : instance.entities)
                if (user.getAddress() == address)
                    i++;

            return i;
        }
    }

    public static void eachExecuteStatic(Executer<User> executer) {
        instance.eachExecute(executer);
    }

    //======================================================
    //=======================METHODS========================
    //======================================================

    public void eachExecute(Executer<User> executer) {
        for (User user : entities) {
            if (user.isAuthenticated()) executer.execute(user);
        }
    }

    public Chat getChat() {
        return chat;
    }

    //======================================================
    //=======================_STATIC========================
    //======================================================

    //======================================================
    //======================OVERRIDE========================
    //======================================================
    @Override
    public void init() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            try {
                join(player);
            } catch (SQLException e) {
                e.printStackTrace();
                player.kickPlayer("§cHouve um erro interno. Contate um ADMIN!");
            }
        }
    }


    public void stop() {
        List<User> copy = new ArrayList<>(entities);
        for (User user : copy) {
            unload(user);
        }
    }

    @Override
    protected void load(User entity) {
        super.load(entity);

        userMap.put(entity.base().getUniqueId(), entity);

        entity.setAuthenticated(false);
        entity.identity().setUser(entity);
        entity.setPoints(new Points());
        entity.updateWorldInfo(worldService.findByWorld(entity.base().getWorld()));
        entity.setAddress(IpUtils.ipToLong(entity.base().getAddress().toString()));
        entity.setBoard(boardService.handler(entity));
        entity.setTag(new TagUser(entity));
        entity.setHidden(false);

        entity.base().getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(12);
        entity.base().setHealthScaled(true);
        entity.setAuthenticated(true);

        skinService.refreshSkin(entity);

        refreshHidden(entity);
    }

    @Override
    protected void unload(User entity) {
        super.unload(entity);

        userMap.remove(entity.base().getUniqueId());

        entity.setAuthenticated(false);
        entity.updateWorldInfo(null);
        entity.setPoints(null);
        entity.tag().quit();
        entity.setTag(null);
        boardService.remove(entity.board());
        entity.board().clear();
        entity.setBoard(null);
        entity.setBase(null);
        entity.identity().setUser(null);
        entity.setHidden(false);

    }
    //======================================================
    //=====================_OVERRIDE========================
    //======================================================

    //======================================================
    //=====================_OVERRIDE========================
    //======================================================

    public void join(Player player) throws SQLException {

        //COMMAND --'
        if (userMap.containsKey(player.getUniqueId())) {
            return;
        }

        User user = repository.findOrCreate(player);

        user.setBase(player);

        boolean isNew = user.identity().getLastLogin() == 0;

        load(user);

        if (isNew) {
            Warp spawn = WarpService.findSpawn();

            if (spawn != null) {
                user.teleport(spawn);
            }
        }
    }

    public void quit(Player player) {
        unload(getLogged(player));
    }

    public User getLogged(Player player) {
        return userMap.get(player.getUniqueId());
    }

    public Identity findIdentity(String name) throws SQLException {
        return repository.getIdentityRepository().findByName(name);
    }

    public GroupUser findIdentityGroup(Identity identity) throws SQLException {
        return repository.findIdentityGroup(identity);
    }

    private void hiddenToggle(User user) {

//        user.iContainer().updateVisiblePlayersRecursive();

        synchronized (entities) {
            for (User loop : entities) {
                if (user.isHidden() && !loop.isAdmin()) {
                    loop.base().hidePlayer(user.base());
                } else {
                    loop.base().showPlayer(user.base());
                }
            }
        }
    }

    private void refreshHidden(User user) {

        if (user.isAdmin()) return;

        synchronized (entities) {
            for (User loop : entities) {
                if (loop.isHidden()) {
                    user.base().hidePlayer(loop.base());
                }
            }
        }
    }

    //======================================================
    //=========================TASK=========================
    //======================================================
    public void runSync() {
        tick++;

        synchronized (entities) {
            for (User user : entities) user.tickSync();
        }
    }

    public void runAsync() {
        atick++;
        synchronized (entities) {
            for (User user : entities) user.tickAsync();
        }
    }
    //======================================================
    //========================_TASK=========================
    //======================================================

    //======================================================
    //======================_METHODS========================
    //======================================================


    //======================================================
    //==================DEFAULT COMMANDS====================
    //======================================================
    @CmdInfo("msg")
    public void addInfo(Command command) {
        command.setAliases(Arrays.asList("tell", "pm"));
        command.setDescription("Mensagens privadas");
    }

    @CmdInfo("vender")
    public void addSellInfo(Command command) {
        command.setAliases(Arrays.asList("autosell", "sell"));
        command.setDescription("Venda de itens pressionando SHIFT");
    }

    @CmdInfo("bau")
    public void addEnderchestInfo(Command command) {
        command.setAliases(Arrays.asList("echest", "enderchest"));
        command.setDescription("Ver baú virtual");
    }

    @CmdInfo("preferencias")
    public void addPreferencesInfo(Command command) {
        command.setDescription("Ver suas preferências");
    }

    @CmdMapping("tpa <?message>")
    @CmdDescription("Comando desabilitado")
    @CmdPermission("simplecraft.user")
    public void tpa(User user, String ignore) {

        user.sendMessage(MessageType.ERROR, "Infelizmente este comando está desabilitado!");

    }

    @CmdMapping("game snake")
    @CmdDescription("Jogo snake")
    @CmdPermission("simplecraft.user")
    public void snake(User user) {
        user.createView(new SnakeView());
    }

    @CmdMapping("preferencias")
    @CmdDescription("Opções")
    @CmdPermission("simplecraft.user")
    public void preferences(User user) {
        user.createView(new PreferencesView());
    }

    @CmdMapping("vender")
    @CmdDescription("Ativar venda por shift")
    @CmdPermission("simplecraft.user")
    public void shiftSell(User user) {
        boolean enabled = !user.flags().allowShiftSell();

        user.sendMessage(MessageType.SUCCESS, "Shift-vender " + (enabled ? "ativado" : "desativado"));
        user.sendMessage(MessageType.INFO, "Você pode alterar os itens vendidos com /vender filtro");
        UserFlag.shiftSell.set(user, User::flags, enabled);
    }

    @CmdMapping("vender filtro")
    @CmdDescription("Alterar itens vendidos")
    @CmdPermission("simplecraft.user")
    public void shiftSellItems(User user) {
        user.createView(new SellListView());
    }

    @CmdMapping("g <message...>")
    @CmdDescription("Envia uma mensagem global")
    @CmdPermission("simplecraft.user")
    public void chatGlobal(User user, String message) {

        if (user.money().getCoins() >= 100) {
            chat.userGlobalMessage(user, message);
        } else {
            user.sendMessage(MessageType.ERROR, "Você precisa ter 100 coins para começar a falar no global!");
        }

    }

    @CmdMapping("msg <user> <message...>")
    @CmdDescription("Envia uma mensagem para outro jogador")
    @CmdPermission("simplecraft.user")
    public void tell(User user, User target, String message) {

        chat.userTellMessage(user, target, message);

    }

    @CmdMapping("vipgratis")
    @CmdDescription("VipGratis")
    @CmdPermission("simplecraft.user")
    public void vipGratis(User user) {

        if (user.hasPermission("simplecraft.vip")) {
            user.sendMessage(MessageType.ERROR, "Comando indisponível");
        } else {
            Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), "group user " + user.identity().getName() + " set vip 7d true");
        }

    }

    @CmdMapping("spawn <?world>")
    @CmdDescription("Vai para o SPAWN")
    @CmdPermission("simplecraft.user")
    @CmdOptions(pvp = false)
    public void spawn(User user, World world) {

        if (world.getName().equals("world") && WarpService.findSpawn() != null) {
            user.teleport(WarpService.findSpawn());
        } else user.teleport(world.getSpawnLocation());
        user.sendMessage(MessageType.SUCCESS, "Teleportando!");

    }

    @CmdMapping("setspawn")
    @CmdDescription("Seta o spawn do mundo")
    @CmdPermission("simplecraft.admin")
    public void setSpawn(User user) {

        user.worldInfo().getWorld().setSpawnLocation(user.base().getLocation());
        user.sendMessage(MessageType.SUCCESS, "Spawn setado!");

    }

    @CmdMapping("login <senha>")
    @CmdDescription("Logar no servidor")
    @CmdPermission("simplecraft.user")
    @CmdChecker("isNotLogged")
    public void login(User user, String password) {
        Player base = user.base();

        if (user.isAuthenticated()) {
            user.sendMessage(MessageType.ERROR, "Você já está autenticado!");
        } else if (user.getPassword() == null) {
            user.base().sendMessage(MessageType.ERROR.format("§cVocê não está registrado ainda!"));
        } else if (!user.getPassword().equals(password)) {
            user.base().sendMessage(MessageType.ERROR.format("§cSenha incorreta!"));
        } else {
            user.setAuthenticated(true);
            user.sendTitle("SimpleCraft", "§aBem vindo " + user.identity().getName() + "!");
            user.sendMessage(MessageType.SUCCESS, "Bem vindo " + user.identity().getName() + "!");
            user.sendMessage(MessageType.TITLE, "Comandos úteis:");

            user.sendMessage("§e/warp - ver as warps");
            user.sendMessage("§e/kit - ver os kits");
            user.sendMessage("§e/skill - ver suas skills");
            user.sendMessage("§e/ajuda - ver a lista de comandos");
        }

    }

    @CmdMapping("register <senha> <senha>")
    @CmdDescription("Registrar no servidor")
    @CmdPermission("simplecraft.user")
    @CmdChecker("isNotRegistered")
    public void register(User user, String password, String repeat) {

        if (!repeat.equals(password)) {
            user.base().sendMessage(MessageType.ERROR.format("§cAs senhas não se combinam!"));
        } else {

            user.setPassword(password);

            UserService.broadcastAction("§e+ §a" + user.identity().getName() + " entrou pela primeira vez!");

            sendBook(user.base().getInventory());

            user.base().sendTitle("SimpleCraft", "§aConta criada! Use /login <senha>", 20, 40, 20);
            user.base().sendMessage(MessageType.SUCCESS.format("§aConta criada! Use /login <senha>"));
        }

    }

    @CmdMapping("tutorial")
    @CmdDescription("tutorial")
    @CmdPermission("simplecraft.user")
    public void tutorial(User user) {
        sendBook(user.base().getInventory());
    }

    private void sendBook(Inventory inventory) {
        ItemStack itemStack = new ItemStack(Material.WRITTEN_BOOK);

        BookMeta bookMeta = (BookMeta) itemStack.getItemMeta();

        bookMeta.setTitle("§eSimpleCraft");
        bookMeta.setAuthor("SimpleCraft");
        bookMeta.addPage(
                "§a===================\n" +
                        "§a=== §bCOMO COMEÇAR §a===\n" +
                        "§a===================§0\n" +
                        "1. Comece minerando\n" +
                        "na /warp minalapis\n\n" +
                        "2. Encante uma espada\n" +
                        "de pedra com pilhagem\n\n" +
                        "3. Va na /warp stored e\n" +
                        "e mate os stored\n\n" +
                        "4. Venda os drops no\n" +
                        "/warp lojamobs",
                "5. Compre spawners na\n" +
                        "/warp lojaespecial\n\n" +
                        "6. Evolua seu machine\n" +
                        "e consiga novos ovos!\n\n" +
                        "7. Crie máquinas!\n" +
                        "(use /maquina)\n\n" +
                        "8. §dDIVIRTA-SE!");

        itemStack.setItemMeta(bookMeta);

        inventory.addItem(itemStack);
    }

    @CmdMapping("trocarsenha <antigaSenha> <novaSenha> <novaSenha>")
    @CmdDescription("Trocar a senha")
    @CmdPermission("simplecraft.user")
    public void changePassword(User user, String oldPassword, String newPassword, String repeat) {

        if (!user.getPassword().equals(oldPassword)) {
            user.sendMessage(MessageType.ERROR, "Senha incorreta!");
        } else if (!repeat.equals(newPassword)) {
            user.sendMessage(MessageType.ERROR, "As novas senhas não se combinam!");
        } else {
            user.setPassword(newPassword);
            user.sendMessage(MessageType.SUCCESS, "Senha trocada!");
        }

    }

    @CmdMapping("discord")
    @CmdDescription("Ver o discord do servidor")
    @CmdPermission("simplecraft.user")
    public void discord(User user) {

        user.sendMessage(MessageType.INFO, "Link: https://discord.gg/EcKN9FD");

    }

    //======================================================
    //================DEFAULT COMMANDS VIP==================
    //======================================================

    @CmdMapping("vip")
    @CmdDescription("Informações do VIP")
    @CmdPermission(value = "simplecraft.vip", message = "Comando liberado apenas para VIP!")
    public void vip(User user) {
        GroupUser groupUser = user.group();

        user.sendMessage(MessageType.TITLE, "Grupo atual do jogador:");
        user.sendMessage("§aNome: §r" + groupUser.get().getName());
        user.sendMessage("§aTAG: §r" + groupUser.get().getTag());
        user.sendMessage("§aPeríodo: §r" + (groupUser.getExpiration() == 0 ? "Permanente" : StringUtils.longTimeToString(groupUser.getExpiration() - System.currentTimeMillis())));

    }

    @CmdMapping("bau")
    @CmdDescription("Ver o seu enderchest")
    @CmdPermission(value = "simplecraft.vip", message = "Comando liberado apenas para VIP!")
    @CmdOptions(pvp = false)
    public void enderchest(User user) {

        user.base().openInventory(user.base().getEnderChest());

    }

    @CmdMapping("anunciar <message...>")
    @CmdDescription("Anunciar uma mensagem")
    @CmdPermission(value = "simplecraft.vip", message = "Comando liberado apenas para VIP!")
    public void announce(User user, String message) {

        chat.userAnnounceMessage(user, message);

    }

    @CmdMapping("fly")
    @CmdDescription("Voar")
    @CmdPermission(value = "simplecraft.vip", message = "Comando liberado apenas para VIP!")
    @CmdChecker("canFly")
    public void fly(User user) {
        Player base = user.base();

        boolean fly = !base.getAllowFlight();

        base.setAllowFlight(fly);
        user.sendMessage(MessageType.SUCCESS, "Modo voar " + (fly ? "ativado" : "desativado"));
    }

    //======================================================
    //===============_DEFAULT COMMANDS VIP==================
    //======================================================


    //======================================================
    //================DEFAULT COMMANDS AJD==================
    //======================================================
    @CmdMapping("kick <user> <?message>")
    @CmdDescription("Kica um jogador")
    @CmdPermission("simplecraft.mod")
    public void kick(Sender sender, User target, String message) {

        if (target.isAdmin() && !sender.hasPermission("simplecraft.admin")) {

            sender.sendMessage(MessageType.ERROR, "Você não tem permissão para kicar este jogador!");

        } else {
            target.base().kickPlayer((message == null ? "§cVocê foi kicado!" : message));
            sender.sendMessage(MessageType.SUCCESS, "Jogador kicado!");
        }
    }

    @CmdMapping("tp <user>")
    @CmdDescription("Se teletransporta para um jogador")
    @CmdPermission("simplecraft.mod")
    public void tp(User user, User target) {
        user.teleport(target.base().getLocation());
        user.sendMessage(MessageType.SUCCESS, "Teleportado!");
    }

    @CmdMapping("mute <user> <time>")
    @CmdDescription("Muta um jogador")
    @CmdPermission("simplecraft.mod")
    public void mute(Sender sender, Identity target, long time) {

        target.mute(time);

        if (target.isOnline()) {
            target.sendAction("§cVocê foi mutado por " + target.getMuteTime() + "!");
        }

        sender.sendMessage(MessageType.SUCCESS, "O jogador foi mutado!");
    }

    @CmdMapping("unmute <user>")
    @CmdDescription("Desmuta um jogador")
    @CmdPermission("simplecraft.mod")
    public void unmute(Sender sender, Identity target) {

        if (target.isMuted()) {
            target.unmute();
            sender.sendMessage(MessageType.SUCCESS, "O jogador foi desmutado!");
        } else {
            sender.sendMessage(MessageType.ERROR, "O jogador não está mutado!");
        }

    }

    //======================================================
    //================DEFAULT COMMANDS AJD==================
    //======================================================

    //======================================================
    //================DEFAULT COMMANDS MOD==================
    //======================================================

    @CmdMapping("tpc <x> <y> <z>")
    @CmdDescription("Se teletransporta para as coordenadas")
    @CmdPermission("simplecraft.mod")
    public void tp(User user, int x, int y, int z) {
        user.teleport(new Location(user.worldInfo().getWorld(), x, y, z));
        user.sendMessage(MessageType.SUCCESS, "Teleportado!");
    }

    @CmdMapping("clear stored")
    @CmdDescription("Remove todos os stored do mundo atual!")
    @CmdPermission("simplecraft.mod")
    public void clearMobs(User user) {

        int counter = EntityUtils.removeEntities(user.worldInfo(), entity -> entity instanceof Creature || entity instanceof Slime);

        user.sendMessage(MessageType.SUCCESS, "Foram removidos " + counter + " stored!");
    }

    @CmdMapping("clear data")
    @CmdDescription("Remove todos os itens do chão do mundo atual!")
    @CmdPermission("simplecraft.mod")
    public void clearItems(User user) {

        int counter = EntityUtils.removeEntities(user.worldInfo(), entity -> entity instanceof Item);

        user.sendMessage(MessageType.SUCCESS, "Foram removidos " + counter + " itens!");
    }

    @CmdMapping("clear data all")
    @CmdDescription("Remove todos os data de todos os mundos!")
    @CmdPermission("simplecraft.mod")
    public void clearItems(Sender sender) {

        int counter = 0;

        for (WorldInfo info : worldService.getEntities()) {
            counter += EntityUtils.removeEntities(info, entity -> entity instanceof Item);
        }

        sender.sendMessage(MessageType.SUCCESS, "Foram removidos " + counter + " itens!");
    }

    @CmdMapping("ban <user> <time> <?message>")
    @CmdDescription("Bane um jogador")
    @CmdPermission("simplecraft.mod")
    public void ban(Sender sender, Identity target, long time, String message) {

        target.ban(time);

        if (target.isOnline()) {
            target.user().base().kickPlayer(message);
        }

        sender.sendMessage(MessageType.SUCCESS, "O jogador foi banido!");
    }

    @CmdMapping("unban <user>")
    @CmdDescription("Desbane um jogador")
    @CmdPermission("simplecraft.mod")
    public void unban(Sender sender, Identity target) {

        if (target.isBanned()) {
            target.unban();
            sender.sendMessage(MessageType.SUCCESS, "O jogador foi desbanido!");
        } else {
            sender.sendMessage(MessageType.ERROR, "O jogador não está banido!");
        }

    }

    @CmdMapping("vanish")
    @CmdDescription("Ficar invisivel")
    @CmdPermission("simplecraft.mod")
    public void vanish(User user) {

        user.setHidden(!user.isHidden());

        hiddenToggle(user);

        user.sendMessage(MessageType.SUCCESS, "Agora você está " + (user.isHidden() ? "invisível" : "visível"));

    }

    //======================================================
    //===============_DEFAULT COMMANDS MOD==================
    //======================================================


    //======================================================
    //================DEFAULT COMMANDS ADM==================
    //======================================================

    @CmdMapping("invsee <user>")
    @CmdDescription("Ver o inventário de um jogador")
    @CmdPermission("simplecraft.admin")
    public void invSee(User user, User target) {

        user.base().openInventory(target.base().getInventory());

    }

    @CmdMapping("bau <user>")
    @CmdDescription("Ver o enderchest de outro jogador")
    @CmdPermission("simplecraft.admin")
    public void enderchestOther(User user, User target) {

        user.base().openInventory(target.base().getEnderChest());

    }

    @CmdMapping("skull <name> <?text>")
    @CmdDescription("Cria uma cabeça")
    @CmdPermission("simplecraft.admin")
    @CmdOptions(sync = false)
    public void skull(User user, String name, String text) {

        user.base().getInventory().addItem(ItemStackUtils.createSkullByName(name, text == null ? name : text));
        user.sendMessage(MessageType.SUCCESS, "Cabeça criada '-'");

    }

    @CmdMapping("skull base64 <string> <?title> <?text>")
    @CmdDescription("Cria uma cabeça")
    @CmdPermission("simplecraft.admin")
    @CmdOptions(sync = false)
    public void skullBase64(User user, String base64, String title, String text) {

        user.base().getInventory().addItem(ItemStackUtils.createSkullByBase64(base64, title, text));
        user.sendMessage(MessageType.SUCCESS, "Cabeça criada '-'");

    }

    @CmdMapping("gamemode <gamemode>")
    @CmdDescription("Altera o gamemode")
    @CmdPermission("simplecraft.admin")
    public void gameMode(User user, GameMode gameMode) {

        user.base().setGameMode(gameMode);
        user.sendMessage(MessageType.SUCCESS, "O seu modo de jogo foi alterado para §c" + gameMode.name());

    }

    @CmdMapping("gamemode <gamemode> <user>")
    @CmdDescription("Altera o gamemode de um jogador")
    @CmdPermission("simplecraft.admin")
    public void gameMode(Sender sender, GameMode gameMode, User user) {

        user.base().setGameMode(gameMode);
        sender.sendMessage(MessageType.SUCCESS, "O modo de jogo do jogador foi alterado para " + gameMode.name());

    }

    @CmdMapping("tp <user> <user>")
    @CmdDescription("Teleporta um jogador para outro jogador")
    @CmdPermission("simplecraft.admin")
    public void tp(Sender sender, User target, User target2) {

        target.teleport(target2.base().getLocation());
        sender.sendMessage(MessageType.SUCCESS, "Jogadores teletransportados!");

    }

    @CmdMapping("broadcast <message...>")
    @CmdDescription("Envia uma mensagem para todos os jogadores")
    @CmdPermission("simplecraft.admin")
    public void broadcastChat(Sender sender, String message) {

        broadcastChat("§c[BROADCAST] §r" + message);

    }

    @CmdMapping("broadcast action <message...>")
    @CmdDescription("Envia uma mensagem na actionbar para todos os jogadores")
    @CmdPermission("simplecraft.admin")
    public void broadcastAction(Sender sender, String message) {

        UserService.broadcastAction(message);

    }

    @CmdMapping("broadcast title <title> <message...>")
    @CmdDescription("Envia uma mensagem na actionbar para todos os jogadores")
    @CmdPermission("simplecraft.admin")
    public void broadcastAction(Sender sender, String title, String message) {

        UserService.broadcastTitle(title, message);

    }

    @CmdMapping("rain <boolean>")
    @CmdDescription("Ativa/desativa a chuva")
    @CmdPermission("simplecraft.admin")
    public void toggleDownFall(User user, boolean value) {

        MainListener.locker = true;
        user.worldInfo().getWorld().setStorm(value);
        MainListener.locker = false;

    }

    @CmdMapping("time set <time>")
    @CmdDescription("Seta o tempo do jogo")
    @CmdPermission("simplecraft.admin")
    public void timeSet(User user, long time) {

        user.worldInfo().getWorld().setTime(time / 1000);

        user.sendMessage(MessageType.SUCCESS, "O tempo foi alterado!");

    }

    @CmdMapping("enchant <enchant> <value>")
    @CmdDescription("Encanta um item")
    @CmdPermission("simplecraft.admin")
    public void enchant(User user, Enchantment enchantment, int level) {

        ItemStack hand = user.base().getInventory().getItemInMainHand();

        if (hand.getType() == Material.AIR) {
            user.sendMessage(MessageType.ERROR, "Não é possível encantar o AR '-'");
        } else if (level <= 0) {
            hand.removeEnchantment(enchantment);
            user.sendMessage(MessageType.SUCCESS, "Encantamento removido!");
        } else if (level > 32767) {
            user.sendMessage(MessageType.ERROR, "O nível máximo é 32767");
        } else {

            hand.addUnsafeEnchantment(enchantment, level);
            user.sendMessage(MessageType.SUCCESS, "Encantamento adicionado!");

        }

    }

    @CmdMapping("disable register <time>")
    @CmdDescription("Desabilita o registro de novas contas por x tempo")
    @CmdPermission("simplecraft.admin")
    public void disableLogin(Sender sender, long time) {

        MainListener.disabledRegister = System.currentTimeMillis() + time;
        sender.sendMessage(MessageType.SUCCESS, "Registro de novas contas bloqueados!");

    }

    //======================================================
    //===============_DEFAULT COMMANDS ADM==================
    //======================================================

    @CheckerOptions(value = "isMuted", reverseMessage = "Você está mutado!")
    public boolean isMuted(User user) {
        return user.identity().isMuted();
    }

    @CheckerOptions(value = "canFly", message = "Você não pode voar nesse local!")
    public boolean canFly(User user) {
        return user.iContainer().protection().canFly();
    }

    @CheckerOptions(value = "isNotLogged", message = "Você já está autenticado!")
    public boolean isNotLogged(User user) {
        return !user.isAuthenticated();
    }

    @CheckerOptions(value = "isPvpArea", reverseMessage = "Comando desativado em area com PVP")
    public boolean isPvpArea(User user) {
        return user.iContainer().protection().isPvpOn();
    }

    @CheckerOptions(value = "isNotRegistered", message = "Você já está registrado!")
    public boolean isNotRegistered(User user) {
        return user.getPassword() == null;
    }

    @SupplierOptions("gamemode")
    class GameModeConvert implements Supplier.Convert<GameMode> {

        @Override
        public void tabComplete(TabArguments arguments) {
            for (GameMode gameMode : GameMode.values()) {
                arguments.offer(gameMode.name());
            }
        }

        @Override
        public Result<GameMode> convert(ParameterBuilder builder, Sender sender, String value) {

            GameMode gameMode = null;

            for (GameMode loop : GameMode.values()) {
                if (loop.name().equalsIgnoreCase(value)) {
                    gameMode = loop;
                    break;
                }
            }

            if (gameMode == null) {
                try {
                    int id = Integer.parseInt(value);
                    gameMode = GameMode.getByValue(id);
                } catch (NumberFormatException ignored) {
                }
            }

            if (gameMode == null) return new Result.Error("O modo de jogo para '" + value + "' não foi encontrado!");

            return new Result<>(gameMode);
        }
    }

    @SupplierOptions({"senha", "novaSenha", "antigaSenha"})
    class PasswordConverter implements Supplier.Convert<String> {

        @Override
        public Result<String> convert(ParameterBuilder builder, Sender sender, String value) {
            if (value.length() < 3) return new Result.Error("A senha deve ter 3 ou mais caracteres");
            else if (value.length() > 15) return new Result.Error("A senha não pode ter mais de 15 caracteres");
            return new Result(value);
        }
    }

    @SupplierOptions("user")
    class IdentityConvert implements Supplier.Convert<Identity> {

        @Override
        public void tabComplete(TabArguments arguments) throws SQLException {
            arguments.offerSafe(repository.getIdentityRepository().findNames(arguments.getComplete()));
        }

        @Override
        public Result<Identity> convert(ParameterBuilder builder, Sender sender, String value) throws SQLException {
            Identity identity = findIdentity(value);

            if (identity == null) return new Result.Error("O jogador não foi encontrado!");

            return new Result(identity);
        }
    }

    @SupplierOptions("user")
    class UserConvert implements Supplier.Convert<User> {

        @Override
        public void tabComplete(TabArguments arguments) {
            for (User user : entities) arguments.offer(user.identity().getFastName());
        }

        @Override
        public Result<User> convert(ParameterBuilder builder, Sender sender, String value) throws SQLException {

            Identity identity = findIdentity(value);

            if (identity != null) {

                if (identity.isOnline() && (!identity.user().isHidden() || (sender.isConsole() || sender.user().isAdmin()))) {
                    return new Result<>(identity.user());
                } else {
                    return new Result.Error("O jogador '" + value + "' não está logado!");
                }

            } else {
                return new Result.Error("O jogador '" + value + "' não existe!");
            }
        }
    }

    @SupplierOptions("enchant")
    class EnchantConverter implements Supplier.Convert<Enchantment> {

        @Override
        public void tabComplete(TabArguments arguments) throws SQLException {
            for (Enchantment enchantment : Enchantment.values()) {
                arguments.offer(enchantment.getName());
            }
        }

        @Override
        public Result<Enchantment> convert(ParameterBuilder builder, Sender sender, String value) throws SQLException {
            Enchantment enchantment = Enchantment.getByName(value.toUpperCase());

            if (enchantment == null) return new Result.Error("O encantamento '" + value + "' não foi encontrado!");

            return new Result<>(enchantment);
        }
    }
    //======================================================
    //=================_DEFAULT COMMANDS====================
    //======================================================

    //======================================================
    //====================PLACE_HOLDERS=====================
    //======================================================

    @PlaceHolderMapping("online")
    class OnlinePlaceHolder implements PlaceHolder {

        /**
         * @return the value holder value
         */
        @Override
        public String value() {
            return String.valueOf(userMap.size());
        }

        /**
         * @return the last modification time
         */
        @Override
        public long lastModified() {
            return userMap.size();
        }
    }

    @PlaceHolderMapping("name")
    class NamePlaceHolder implements PlaceHolderBuilder<User> {

        @Override
        public PlaceHolder build(User target) {
            return new PlaceHolder() {

                private final Identity identity = target.identity();

                @Override
                public String value() {
                    return identity.getName();
                }

                @Override
                public long lastModified() {
                    return 0;
                }
            };
        }
    }

    @PlaceHolderMapping("container_name")
    class ContainerNamePlaceHolder implements PlaceHolderBuilder<User> {

        @Override
        public PlaceHolder build(User target) {
            return new PlaceHolder() {

                private final User user = target;

                @Override
                public String value() {
                    return user.container() == null || user.container().getName() == null ? "???" : user.container().getName();
                }

                @Override
                public long lastModified() {
                    return user.container() == null ? user.getModifier() : user.container().getLastModified();
                }
            };
        }
    }

    @PlaceHolderMapping("container_players")
    class ContainerPlayersPlaceHolder implements PlaceHolderBuilder<User> {

        @Override
        public PlaceHolder build(User target) {
            return new PlaceHolder() {

                private final User user = target;

                @Override
                public String value() {
                    return user.container() == null ? "0" : String.valueOf(user.container().getUsers().size());
                }

                @Override
                public long lastModified() {
                    return user.container() == null ? user.getModifier() : user.container().getUsers().size();
                }
            };
        }
    }

    @PlaceHolderMapping("container_pvp")
    class ContainerPvpPlaceHolder implements PlaceHolderBuilder<User> {

        @Override
        public PlaceHolder build(User target) {
            return new PlaceHolder() {

                private final User user = target;

                @Override
                public String value() {
                    return user.iContainer().protection().isPvpOn() ? "sim" : "não";
                }

                @Override
                public long lastModified() {
                    return user.iContainer().protection().isPvpOn() ? 1 : 0;
                }
            };
        }
    }

    @PlaceHolderMapping("container_pve")
    class ContainerPvePlaceHolder implements PlaceHolderBuilder<User> {

        @Override
        public PlaceHolder build(User target) {
            return new PlaceHolder() {

                private final User user = target;

                @Override
                public String value() {
                    return user.iContainer().protection().isPveOn(user) ? "sim" : "não";
                }

                @Override
                public long lastModified() {
                    return user.iContainer().protection().isPveOn(user) ? 1 : 0;
                }
            };
        }
    }

    //======================================================
    //===================_PLACE_HOLDERS=====================
    //======================================================
}
