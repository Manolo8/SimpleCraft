package com.github.manolo8.simplecraft.module.group;

import com.github.manolo8.simplecraft.core.commands.line.*;
import com.github.manolo8.simplecraft.core.commands.line.annotation.*;
import com.github.manolo8.simplecraft.core.commands.line.inf.Supplier;
import com.github.manolo8.simplecraft.core.placeholder.PlaceHolder;
import com.github.manolo8.simplecraft.core.placeholder.PlaceHolderBuilder;
import com.github.manolo8.simplecraft.core.placeholder.annotation.PlaceHolderMapping;
import com.github.manolo8.simplecraft.core.service.RepositoryService;
import com.github.manolo8.simplecraft.module.group.permission.Permission;
import com.github.manolo8.simplecraft.module.group.user.GroupUser;
import com.github.manolo8.simplecraft.module.user.MessageType;
import com.github.manolo8.simplecraft.module.user.User;
import com.github.manolo8.simplecraft.module.user.UserService;
import com.github.manolo8.simplecraft.utils.def.StringUtils;

import java.sql.SQLException;

@SuppressWarnings("unused")
public class GroupService extends RepositoryService<GroupRepository> {

    private static GroupService instance;

    public GroupService(GroupRepository repository) {
        super(repository);
        instance = this;
    }

    public static Group findVipGroup() throws SQLException {
        Group group = instance.findByName("vip");

        if (group == null) group = instance.repository.findDefault();

        return group;
    }

    public Group create(String name) throws SQLException {
        return repository.create(name, name, false);
    }

    public Group findByName(String name) throws SQLException {
        return repository.findByName(name);
    }

    public boolean exists(String name) throws SQLException {
        return repository.findByName(name) != null;
    }

    //======================================================
    //=======================COMMANDS=======================
    //======================================================
    @CmdInfo("group")
    public void addInfo(Command command) {
        command.setDescription("Comandos úteis dos grupos");
    }

    @CmdMapping("group create <name>")
    @CmdDescription("Cria um grupo")
    @CmdPermission("simplecraft.admin")
    public void groupCreate(Sender sender, String name) throws SQLException {

        if (exists(name)) {
            sender.sendMessage(MessageType.ERROR, "Já existe um grupo com o mesmo nome!");
        } else {
            create(name);
            sender.sendMessage(MessageType.SUCCESS, "O grupo foi criado com sucesso!");
        }

    }

    @CmdMapping("group -e <group> clear")
    @CmdDescription("Remove um grupo")
    @CmdPermission("simplecraft.admin")
    public void groupRemove(Sender sender, Group group) {

        if (group.isDefault()) {
            sender.sendMessage(MessageType.ERROR, "Não é possível remover o grupo DEFAULT!");
        } else {
            group.remove();
            sender.sendMessage(MessageType.SUCCESS, "O grupo foi removido!");
        }

    }


    @CmdMapping("group user <user>")
    @CmdDescription("Ver informações do grupo atual de um jogador")
    @CmdPermission("simplecraft.admin")
    public void groupUser(Sender sender, GroupUser user) {

        sender.sendMessage(MessageType.TITLE, "Grupo atual do jogador:");
        sender.sendMessage("§aNome: §r" + user.get().getName());
        sender.sendMessage("§aTAG: §r" + user.get().getTag());
        sender.sendMessage("§aPeríodo: §r" + (user.getExpiration() == 0 ? "Permanente" : StringUtils.longTimeToString(user.getExpiration() - System.currentTimeMillis())));

    }

    @CmdMapping("group user <user> set <group> <?time> <?boolean>")
    @CmdDescription("Seta um jogador em um determinado grupo")
    @CmdPermission("simplecraft.admin")
    public void groupSetUser(Sender sender, GroupUser user, Group group, long time, boolean broadcast) {

        user.changeGroup(group, time);

        sender.sendMessage(MessageType.SUCCESS, "Grupo setado" + (time == 0 ? "!" : " por " + StringUtils.longTimeToString(time) + "!"));

        if (broadcast) {
            UserService.broadcastTitle("§a" + user.getIdentity().getName(), "§eAgora é " + group.getName() + "§e!");
        }
    }

    @CmdMapping("group -e <group>")
    @CmdDescription("Informações de um grupo")
    @CmdPermission("simplecraft.admin")
    public void groupInfo(Sender sender, Group group) {

        sender.sendMessage(MessageType.TITLE, "Informações do grupo " + group.getName());
        sender.sendMessage("§aNome: §r" + group.getName());
        sender.sendMessage("§aTAG: §r" + group.getTag());
        sender.sendMessage("§aPermissões §r" + group.getPermissions().size());
        sender.sendMessage("§aParente: §r" + (group.getParent() == null ? "não" : group.getParent().getName()));

    }

    @CmdMapping("group -e <group> set parent <group>")
    @CmdDescription("Seta um parente para um grupo")
    @CmdPermission("simplecraft.admin")
    public void groupSetParent(Sender sender, Group group, Group parent) {

        if (!group.checkHierarchy(parent)) {
            sender.sendMessage(MessageType.ERROR, "Você quer dar um stack overflow? '-'");
        } else {
            group.changeParent(parent);
            sender.sendMessage(MessageType.SUCCESS, "O parente foi setado com sucesso!");
        }

    }

    @CmdMapping("group -e <group> set tag <tag>")
    @CmdDescription("Seta a tag de um grupo")
    @CmdPermission("simplecraft.admin")
    public void groupSetTag(Sender sender, Group group, String tag) {

        if (tag.equals("null")) {
            group.setTag("");
        } else {
            group.setTag(tag);
        }

        sender.sendMessage(MessageType.SUCCESS, "A tag do grupo foi alterada!");
    }

    @CmdMapping("group -e <group> set name <name>")
    @CmdDescription("Seta a tag de um grupo")
    @CmdPermission("simplecraft.admin")
    public void groupSetName(Sender sender, Group group, String name) {

        group.setName(name);

        sender.sendMessage(MessageType.SUCCESS, "O nome do grupo foi alterado!");
    }

    @CmdMapping("group -e <group> permission add <name> <?value>")
    @CmdDescription("Adiciona uma permissão a um grupo")
    @CmdPermission("simplecraft.admin")
    public void groupAddPermission(Sender sender, Group group, String permission, int value) throws SQLException {

        if (StringUtils.hasAnyUpperCase(permission)) {
            sender.sendMessage(MessageType.ERROR, "Permissões só usam letras minúsculas");
        } else if (group.getPermission(permission) != null) {
            sender.sendMessage(MessageType.ERROR, "Este grupo já tem essa permissão!");
        } else {
            group.addPermission(permission, value == 0 ? 1 : value);
            sender.sendMessage(MessageType.SUCCESS, "A permissão foi adicionada!");
        }

    }

    @CmdMapping("group -e <group> permission clear <permission>")
    @CmdDescription("Remove uma permissão de um grupo")
    @CmdPermission("simplecraft.admin")
    public void groupRemovePermission(Sender sender, Group group, Permission permission) {

        group.removePermission(permission);

        sender.sendMessage(MessageType.SUCCESS, "A permissão foi removida!");
    }

    @CmdMapping("group -e <group> permission set <permission> <value>")
    @CmdDescription("Altera o valor de uma permissão")
    @CmdPermission("simplecraft.admin")
    public void groupChangePermission(Sender sender, Group group, Permission permission, int value) {

        permission.setValue(value);

        sender.sendMessage(MessageType.SUCCESS, "O valor foi alterado com sucesso!");
    }

    //======================================================
    //=======================REGISTRY=======================
    //======================================================

    @SupplierOptions("group")
    class GroupConverter implements Supplier.Convert<Group> {

        @Override
        public void tabComplete(TabArguments arguments) throws SQLException {
            arguments.offerSafe(repository.findNames(arguments.getComplete()));
        }

        @Override
        public Result<Group> convert(ParameterBuilder builder, Sender sender, String value) throws SQLException {
            Group group = findByName(value);

            if (group == null) return new Result.Error("O grupo não foi encontrado");

            return new Result(group);
        }
    }

    @SupplierOptions("user")
    class GroupUserConverter implements Supplier.Convert<GroupUser> {

        @Override
        public void tabComplete(TabArguments arguments) throws SQLException {
            arguments.offerSafe(repository.getGroupUserRepository().getIdentityRepository().findNames(arguments.getComplete()));
        }

        @Override
        public Result<GroupUser> convert(ParameterBuilder builder, Sender sender, String value) throws SQLException {
            GroupUser user = repository.getGroupUserRepository().findOneByIdentity(value);

            if (user == null) return new Result.Error("O jogador não foi encontrado");

            return new Result(user);
        }
    }

    @SupplierOptions("permission")
    class PermissionConverter implements Supplier.Convert<Permission> {

        @Override
        public void tabComplete(TabArguments arguments) {
            Group group = arguments.parameters().getByType(Group.class);

            if (group != null) {

                for (String permission : group.getPermissions()) {
                    arguments.offer(permission);
                }

            }
        }

        @Override
        public Result<Permission> convert(ParameterBuilder builder, Sender sender, String value) throws SQLException {
            Group group = builder.getByType(Group.class);

            Permission permission = group.getPermission(value);

            if (permission == null) return new Result.Error("A permissão não foi encontrada!");

            return new Result<>(permission);
        }
    }
    //======================================================
    //======================_REGISTRY=======================
    //======================================================


    //======================================================
    //======================_COMMANDS=======================
    //======================================================


    //======================================================
    //====================PLACE_HOLDERS=====================
    //======================================================

    @PlaceHolderMapping("group")
    class GroupPlaceHolder implements PlaceHolderBuilder<User> {

        @Override
        public PlaceHolder build(User target) {
            return new PlaceHolder() {

                private final GroupUser user = target.group();

                @Override
                public String value() {
                    return user.get().getTag();
                }

                @Override
                public long lastModified() {
                    return user.getLastModified() + user.get().getLastModified();
                }
            };
        }
    }

    //======================================================
    //===================_PLACE_HOLDERS=====================
    //======================================================
}
