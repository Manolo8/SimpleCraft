package com.github.manolo8.simplecraft.commands;

import com.github.manolo8.simplecraft.commands.annotation.CommandMapping;
import com.github.manolo8.simplecraft.domain.group.Group;
import com.github.manolo8.simplecraft.domain.group.GroupService;
import com.github.manolo8.simplecraft.domain.user.User;
import com.github.manolo8.simplecraft.domain.user.UserService;
import com.github.manolo8.simplecraft.utils.RecursiveInformation;
import org.apache.commons.lang.math.NumberUtils;

/**
 * @author Willian
 */
@SuppressWarnings("unused")
public class Commands {

    private final UserService userService;
    private final GroupService groupService;

    public Commands(UserService userService,
                    GroupService groupService) {
        this.userService = userService;
        this.groupService = groupService;
    }

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

        if (!user.withDraw(quantity)) {
            user.sendMessage("§cVocê não tem money suficiente!");
            return;
        }

        user.sendMessage("§aVocê depositou com sucesso " + quantity + " para" + user.getName() + ".");

        target.deposit(quantity);
    }

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
}
