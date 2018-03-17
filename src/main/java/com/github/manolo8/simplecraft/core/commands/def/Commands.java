package com.github.manolo8.simplecraft.core.commands.def;

import com.github.manolo8.simplecraft.core.commands.def.annotation.CommandMapping;
import com.github.manolo8.simplecraft.core.protection.Protection;
import com.github.manolo8.simplecraft.core.world.WorldService;
import com.github.manolo8.simplecraft.domain.group.Group;
import com.github.manolo8.simplecraft.domain.group.GroupService;
import com.github.manolo8.simplecraft.domain.plot.Plot;
import com.github.manolo8.simplecraft.domain.plot.PlotService;
import com.github.manolo8.simplecraft.domain.plot.PlotView;
import com.github.manolo8.simplecraft.domain.plot.data.PlotInfo;
import com.github.manolo8.simplecraft.domain.region.Region;
import com.github.manolo8.simplecraft.domain.region.RegionService;
import com.github.manolo8.simplecraft.domain.user.User;
import com.github.manolo8.simplecraft.domain.user.UserService;
import com.github.manolo8.simplecraft.domain.user.UserView;
import com.github.manolo8.simplecraft.domain.warp.Warp;
import com.github.manolo8.simplecraft.domain.warp.WarpService;
import com.github.manolo8.simplecraft.utils.RecursiveInformation;
import com.github.manolo8.simplecraft.utils.location.SimpleArea;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.List;

/**
 * @author Willian
 */
@SuppressWarnings("unused")
public class Commands {

    private final UserService userService;
    private final GroupService groupService;
    private final RegionService regionService;
    private final PlotService plotService;
    private final WorldService worldService;
    private final WarpService warpService;

    public Commands(UserService userService,
                    GroupService groupService,
                    RegionService regionService,
                    PlotService plotService,
                    WorldService worldService,
                    WarpService warpService) {
        this.userService = userService;
        this.groupService = groupService;
        this.regionService = regionService;
        this.plotService = plotService;
        this.worldService = worldService;
        this.warpService = warpService;
    }

    //--------------------------------------------------
    //=====> USER COMMANDS
    //--------------------------------------------------
    @CommandMapping(command = "status",
            args = {0, 1},
            permission = "simplecraft.user",
            usage = "§c/status </user>")
    public void status(User user, String[] args) {

        User target = user;

        if (args.length == 1) target = userService.getOfflineUser(args[0]);

        if (target == null) {
            user.sendMessage("§cO jogador não foi encontrado...");
            return;
        }

        user.createView(new UserView(target));
    }
    //--------------------------------------------------
    //=====> USER COMMANDS
    //--------------------------------------------------

    //--------------------------------------------------
    //=====> WARP COMMANDS
    //--------------------------------------------------
    @CommandMapping(command = "warp",
            args = 0,
            permission = "simplecraft.warp",
            usage = "§c/warp")
    public void warp(User user, String[] args) {
        user.createView(warpService);
    }

    @CommandMapping(command = "warpadmin",
            subCommand = "create",
            args = 2,
            permission = "simplecraft.warp.admin",
            usage = "§c/warpadmin create <name>")
    public void warpCreate(User user, String[] args) {
        warpService.createWarp(user, args[1]);
        user.sendMessage("§sWarp criado com sucesso!");
    }

    @CommandMapping(command = "warpadmin",
            subCommand = "setname",
            args = 3,
            permission = "simplecraft.warp.admin",
            usage = "§c/warpadmin setname <name> <newName>")
    public void warpSetName(User user, String[] args) {
        Warp warp = warpService.findWarp(args[1]);

        if (warp == null) {
            user.sendMessage("§cWarp não encontrado!");
            return;
        }

        warp.setName(args[2]);
    }

    @CommandMapping(command = "warpadmin",
            subCommand = "setdisplay",
            args = 3,
            permission = "simplecraft.warp.admin",
            usage = "§c/warpadmin setdisplay <name> <newDisplay>")
    public void warpSetDisplayName(User user, String[] args) {
        Warp warp = warpService.findWarp(args[1]);

        if (warp == null) {
            user.sendMessage("§cWarp não encontrado!");
            return;
        }

        warp.setDisplayName(args[2].replaceAll("&", "§"));
    }

    @CommandMapping(command = "warpadmin",
            subCommand = "setindex",
            args = 3,
            permission = "simplecraft.warp.admin",
            usage = "§c/warpadmin setindex <name> <index>")
    public void warpSetIndex(User user, String[] args) {
        Warp warp = warpService.findWarp(args[1]);

        if (warp == null) {
            user.sendMessage("§cWarp não encontrado!");
            return;
        }

        if (!NumberUtils.isNumber(args[2])) {
            user.sendMessage("§cO valor não é numérico");
            return;
        }

        int number = NumberUtils.toInt(args[2]) - 1;
        if (number < 0 || number > 53) number = 0;

        warp.setIndex(number);
    }

    @CommandMapping(command = "warpadmin",
            subCommand = "addlore",
            args = {-1, 3},
            permission = "simplecraft.warp.admin",
            usage = "§c/warpadmin addlore <name> <lore>")
    public void warpAddLore(User user, String[] args) {
        Warp warp = warpService.findWarp(args[1]);

        if (warp == null) {
            user.sendMessage("§cWarp não encontrado!");
            return;
        }

        StringBuilder builder = new StringBuilder();
        for(int i = 2; i < args.length; i++) {
            builder.append(args[i]).append(" ");
        }

        warp.addLore(builder.toString().replaceAll("&", "§"));
    }

    @CommandMapping(command = "warpadmin",
            subCommand = "clearlore",
            args = 2,
            permission = "simplecraft.warp.admin",
            usage = "§c/warpadmin clearlore <name>")
    public void warpClearLore(User user, String[] args) {
        Warp warp = warpService.findWarp(args[1]);

        if (warp == null) {
            user.sendMessage("§cWarp não encontrado!");
            return;
        }

        warp.clearLore();
    }

    @CommandMapping(command = "warpadmin",
            subCommand = "settype",
            args = 3,
            permission = "simplecraft.warp.admin",
            usage = "§c/warpadmin settype <name> <material>")
    public void warpSetType(User user, String[] args) {
        Warp warp = warpService.findWarp(args[1]);

        if (warp == null) {
            user.sendMessage("§cWarp não encontrado!");
            return;
        }

        Material material = Material.getMaterial(args[2].toUpperCase());

        if (material == null) {
            user.sendMessage("§cMaterial não encontrado!");
            return;
        }

        warp.setMaterial(material);
    }
    //--------------------------------------------------
    //=====> WARP COMMANDS
    //--------------------------------------------------


    //--------------------------------------------------
    //=====> ECONOMY COMMANDS
    //--------------------------------------------------
    @CommandMapping(command = "money",
            args = {0, 1},
            permission = "simplecraft.user",
            usage = "§c/money </user>")
    public void money(User user, String[] args) {

        User target = user;

        if (args.length == 1) target = userService.getOfflineUser(args[0]);

        if (target == null) {
            user.sendMessage("§cO jogador não foi encontrado...");
            return;
        }

        user.sendMessage("§aBalanço de " + target.getName() + " §b" + target.getMoney());
    }

    @CommandMapping(command = "pay",
            args = 2,
            permission = "simplecraft.user",
            usage = "§c/pay <user> <value>")
    public void pay(User user, String[] args) {

        User target = userService.getOfflineUser(args[0]);

        if (target == null) {
            user.sendMessage("§cO jogador não foi encontrado...");
            return;
        }

        if (!NumberUtils.isNumber(args[1])) {
            user.sendMessage("§cO valor " + args[1] + " não é um número...");
            return;
        }

        double quantity = NumberUtils.toDouble(args[1]);

        if (quantity <= 0) {
            user.sendMessage("§cO valor tem que ser maior que 0!");
            return;
        }

        if (!user.withdraw(quantity)) {
            user.sendMessage("§cVocê não tem money suficiente!");
            return;
        }

        user.sendMessage("§aVocê depositou com sucesso " + quantity + " para" + user.getName() + ".");

        target.deposit(quantity);
    }
    //--------------------------------------------------
    //=====> END OF ECONOMY COMMANDS
    //--------------------------------------------------


    //--------------------------------------------------
    //=====> PERMISSION COMMANDS
    //--------------------------------------------------
    @CommandMapping(command = "group",
            subCommand = "create",
            args = 2,
            usage = "§c/group create <name>")
    public void groupCreate(User user, String[] args) {
        Group group = groupService.create(args[1]);
        user.sendMessage("§aO grupo " + group.getName() + " foi criado! ");
    }

    @CommandMapping(command = "user",
            subCommand = "info",
            args = {1, 2},
            usage = "§c/user info <user/>")
    public void groupInfo(User user, String[] args) {

        User target = user;

        if (args.length == 2) target = userService.getOfflineUser(args[0]);

        if (target == null) {
            user.sendMessage("§cO jogador não foi encontrado");
            return;
        }

        if (target.getGroup() == null) {
            user.sendMessage("§cO jogador não tem um grupo.");
            return;
        }

        user.sendMessage("§aGrupo do jogador: §c" + target.getGroup().getName());
    }

    @CommandMapping(command = "group",
            subCommand = "info",
            args = 2,
            usage = "§c/group info <group>")
    public void groupPermissions(User user, String[] args) {
        Group group = groupService.findOne(args[1]);

        if (group == null) {
            user.sendMessage("§cO grupo não foi encontrado");
            return;
        }

        StringBuilder builder = new StringBuilder();
        builder.append("§a--- Lista de permissões --- \n");

        RecursiveInformation.buildPermissionsDetails(group, builder);

        user.sendMessage(builder.toString());
    }

    @CommandMapping(command = "group",
            subCommand = "user",
            args = 4,
            usage = "§c/group user <name> set <group>")
    public boolean groupUserSet(User user, String[] args) {
        if (!args[2].equals("set")) return false;

        User target = userService.getOfflineUser(args[1]);

        if (target == null) {
            user.sendMessage("§cO jogador não foi encontrado.");
            return true;
        }

        Group group = groupService.findOne(args[3]);

        if (group == null) {
            user.sendMessage("§cO grupo não foi encontrado");
            return true;
        }

        target.setGroup(group);
        return true;
    }

    @CommandMapping(command = "group",
            subCommand = "permission",
            args = 4,
            usage = "§c/group permission <group> add <permission>")
    public boolean groupPermissionAdd(User user, String[] args) {
        if (!args[2].equals("add")) return false;

        Group group = groupService.findOne(args[1]);

        if (group == null) {
            user.sendMessage("§cO grupo não foi encontrado");
            return true;
        }

        if (group.addPermission(args[3]))
            user.sendMessage("§aPermissão adicionada!");
        else user.sendMessage("§aO grupo já tem essa permissão!");

        return true;
    }

    @CommandMapping(command = "group",
            subCommand = "setdefault",
            args = 2,
            usage = "§c/group setdefault <group>")
    public boolean groupSetDefault(User user, String[] args) {
        if (!args[2].equals("add")) return false;

        Group group = groupService.findOne(args[1]);

        if (group == null) {
            user.sendMessage("§cO grupo não foi encontrado");
            return true;
        }

        groupService.setDefault(group);

        return true;
    }
    //--------------------------------------------------
    //=====> END OF PERMISSION COMMANDS
    //--------------------------------------------------


    //--------------------------------------------------
    //=====> REGION COMMANDS
    //--------------------------------------------------
    @CommandMapping(command = "region",
            subCommand = "create",
            args = 2,
            usage = "§c/region create <name>")
    public void regionCreate(User user, String[] args) {
        if (user.getPos1() == null || user.getPos2() == null) {
            user.sendMessage("§aMarque as posições primeiro!");
            return;
        }

        Region region = regionService.create(user, args[1]);

        if (region == null) {
            user.sendMessage("§cUma região com esse nome já existe ou você não está em " +
                    "um mundo protegido pelo sistema de região!");
            return;
        }

        region.setArea(new SimpleArea(user.getPos1(), user.getPos2()));
        user.sendMessage("§aRegião criada com sucesso!");
    }

    @CommandMapping(command = "region",
            subCommand = "update",
            args = 2,
            usage = "§c/region update <name>")
    public void regionUpdate(User user, String[] args) {
        if (user.getPos1() == null || user.getPos2() == null) {
            user.sendMessage("§aMarque as posições primeiro!");
            return;
        }

        Region region = regionService.findOne(args[1]);

        if (region == null) {
            user.sendMessage("§cA região não foi encontrada.");
            return;
        }

        if (region.getWorldId() != user.getWorldId()) {
            user.sendMessage("§cVocê não está no mesmo mundo dessa região!");
            return;
        }

        region.setArea(new SimpleArea(user.getPos1(), user.getPos2()));
        user.sendMessage("§aRegião alterada com sucesso!");
    }

    @CommandMapping(command = "region",
            subCommand = "flag",
            args = 4,
            permission = "region.set.flag",
            usage = "§c/region flag <name> <flag> <true/false>")
    public void regionFlag(User user, String[] args) {

        args[3] = args[3].toLowerCase();

        boolean value;

        if (args[3].equals("true")) {
            value = true;
        } else if (args[3].equals("false")) {
            value = false;
        } else {
            user.sendMessage("§cUse true or false");
            return;
        }

        Region region = regionService.findOne(args[1]);

        if (region == null) {
            user.sendMessage("§cA região não foi encontrada.");
            return;
        }

        switch (args[2].toLowerCase()) {
            case "pvp":
                region.setPvpOn(value);
                break;
            case "pvpanimal":
                region.setPvpAnimalOn(value);
                break;
            case "spread":
                region.setCanSpread(value);
                break;
            case "pistonwork":
                region.setCanPistonWork(value);
                break;
            case "explode":
                region.setCanExplode(value);
                break;
            case "place":
                region.setCanPlace(value);
                break;
            case "break":
                region.setCanBreak(value);
                break;
            case "interact":
                region.setCanInteract(value);
                break;
            default:
                user.sendMessage("§cFlag não encontrada! Flags disponíveis:" +
                        "\npvp, pvpanimal, spread, pistonwork, explode, place, interact.");
                return;
        }

        user.sendMessage("§aA flag " + args[2] + " foi setada para " + value + " com sucesso!");
    }

    @CommandMapping(command = "region",
            subCommand = "info",
            args = 1,
            permission = "region.info",
            usage = "§c/region info")
    public void regionInfo(User user, String[] args) {

        Protection protection = user.getCurrentChecker()
                .getLocationProtection(user.getBase().getLocation());


        if (!(protection instanceof Region)) {
            user.sendMessage("§cVocê não está em uma região!");
            return;
        }

        Region region = (Region) protection;
        user.sendMessage("§aNome da região: " + region.getName());
    }
    //--------------------------------------------------
    //=====> END OF REGION COMMANDS
    //--------------------------------------------------


    //--------------------------------------------------
    //=====> PLOT COMMANDS
    //--------------------------------------------------
    @CommandMapping(command = "plot",
            subCommand = "auto",
            args = 1,
            permission = "plot.create",
            usage = "§c/group permission <group> add <permission>")
    public void plotAuto(User user, String[] args) {

        Plot plot = plotService.autoCreatePlot(user);

        if (plot == null) {
            user.sendMessage("§cNão foi possível criar o plot :(");
            return;
        }

        user.sendMessage("§aA plot foi criado com sucesso!" +
                "\nO ID dele é §b" + plot.getId());

        World world = worldService.getWorldByWorldId(plot.getWorldId());

        user.teleport(plot);
    }

    @CommandMapping(command = "plot",
            subCommand = "claim",
            args = 1,
            permission = "plot.create",
            usage = "§c/plot create")
    public void plotClaim(User user, String[] args) {

        Plot plot = plotService.createPlot(user);

        if (plot == null) {
            user.sendMessage("§cNão foi possível criar o plot :(");
            return;
        }

        user.sendMessage("§aA plot foi criado com sucesso!" +
                "\nO ID dele é §b" + plot.getId());
    }

    @CommandMapping(command = "plot",
            subCommand = "addfriend",
            args = 2,
            permission = "plot.add.friend",
            usage = "§c/plot addfriend <name>")
    public void plotAddFriend(User user, String[] args) {

        Protection protection = user.getCurrentChecker().getLocationProtection(user.getBase().getLocation());

        if (!(protection instanceof Plot)) {
            user.sendMessage("§cVocê não está em um plot :(");
            return;
        }

        Plot plot = (Plot) protection;

        if (plot.getOwner() != user.getId()) {
            user.sendMessage("§cVocê não é dono desse plot!");
            return;
        }

        User target = userService.getOfflineUser(args[1]);

        if (target == null) {
            user.sendMessage("§cO jogador não foi encontrado.");
            return;
        }

        if (plot.hasPermission(target)) {
            user.sendMessage("§cO jogador já tem permissão!");
            return;
        }

        if (plot.addFriend(target)) user.sendMessage("§aO jogador " + target.getName() + "foi adicionado com sucesso!");
        else user.sendMessage("§cO jogador já tem permissão no plot!");
    }

    @CommandMapping(command = "plot",
            subCommand = "friends",
            args = 2,
            permission = "plot.clear.friend",
            usage = "§c/plot friends clear")
    public boolean plotClearFriends(User user, String[] args) {
        if (!args[1].equalsIgnoreCase("clear")) return false;

        Protection protection = user.getCurrentChecker().getLocationProtection(user.getBase().getLocation());

        if (!(protection instanceof Plot)) {
            user.sendMessage("§cVocê não está em um plot :(");
            return true;
        }

        Plot plot = (Plot) protection;

        if (plot.getOwner() != user.getId()) {
            user.sendMessage("§cVocê não é dono desse plot!");
            return true;
        }

        if (plot.clearFriends()) user.sendMessage("§aAgora só você tem permissão nesse plot!");
        else user.sendMessage("§cSó você tem permissão nesse plot.");
        return true;
    }

    @CommandMapping(command = "plot",
            subCommand = "home",
            args = {1, 2},
            permission = "plot.home",
            usage = "§c/plot home </user>")
    public void plotHome(User user, String[] args) {
        User target = user;

        if (args.length == 2) target = userService.getOfflineUser(args[1]);

        if (target == null) {
            user.sendMessage("§cO jogador não foi encontrado.");
            return;
        }

        List<PlotInfo> plots = target.getPlots();

        if (plots.size() == 0) {
            if (user.equals(target)) user.sendMessage("§cVocê não tem plots! Use /plot claim ou /plot auto!");
            else user.sendMessage("§cO jogador não tem plots!");
            return;
        }

        if (plots.size() > 1) {
            user.createView(new PlotView(target));
            return;
        }

        user.teleport(plots.get(0));
    }
    //--------------------------------------------------
    //=====> END OF PLOT COMMANDS
    //--------------------------------------------------


    //--------------------------------------------------
    //=====> WORLD COMMANDS
    //--------------------------------------------------
    @CommandMapping(command = "world",
            subCommand = "tp",
            args = 2,
            permission = "world.tp",
            usage = "§c/world tp <name>")
    public void worldTp(User user, String[] args) {

        World world = Bukkit.getWorld(args[1]);

        if (world == null) {
            user.sendMessage("§cMundo não encontrado");
            return;
        }

        user.sendMessage("§aTeleportando...");
        user.teleport(world.getSpawnLocation());
    }

    @CommandMapping(command = "world"
            , subCommand = "setspawn",
            permission = "world.setspawn",
            usage = "§c/world setspawn")
    public void worldSetSpawn(User user, String[] args) {

        Location location = user.getBase().getLocation();

        location.getWorld().setSpawnLocation(location.getBlockX(), location.getBlockY(), location.getBlockZ());

        user.sendMessage("§aO spawn do mundo foi atualizado!");
    }
    //--------------------------------------------------
    //=====> END OF WORLD COMMANDS
    //--------------------------------------------------
}
