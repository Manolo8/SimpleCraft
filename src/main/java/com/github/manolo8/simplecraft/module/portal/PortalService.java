package com.github.manolo8.simplecraft.module.portal;

import com.github.manolo8.simplecraft.core.commands.line.*;
import com.github.manolo8.simplecraft.core.commands.line.annotation.*;
import com.github.manolo8.simplecraft.core.commands.line.inf.Supplier;
import com.github.manolo8.simplecraft.core.service.ContainerService;
import com.github.manolo8.simplecraft.core.world.WorldInfo;
import com.github.manolo8.simplecraft.core.world.container.Container;
import com.github.manolo8.simplecraft.module.user.MessageType;
import com.github.manolo8.simplecraft.module.user.User;
import com.github.manolo8.simplecraft.utils.def.Matcher;
import com.github.manolo8.simplecraft.utils.location.SimpleArea;
import com.github.manolo8.simplecraft.utils.location.SimpleLocation;

import java.sql.SQLException;

@SuppressWarnings("unused")
public class PortalService extends ContainerService<Portal, PortalRepository> {

    public PortalService(PortalRepository repository) {
        super(repository, 3);
    }

    @Override
    public PortalProvider initProvider(WorldInfo worldInfo) {
        return new PortalProvider(worldInfo, this);
    }

    @Override
    public Matcher<Container> matcher() {
        return entity -> entity.getClass() == Portal.class;
    }

    //======================================================
    //=======================COMMANDS=======================
    //======================================================
    @CmdInfo("portal")
    public void addInfo(Command command) {
        command.setDescription("Comandos úteis dos portais");
    }

    @CmdMapping("portal create <name>")
    @CmdDescription("Cria um portal")
    @CmdPermission("simplecraft.admin")
    @CmdParams({@Param(PortalProvider.class), @Param(SimpleArea.class), @Param(SimpleLocation.class)})
    public void portalCreate(User user, String name, PortalProvider provider, SimpleArea area, SimpleLocation location) throws SQLException {

        if (provider.exists(name)) {
            user.sendMessage(MessageType.ERROR, "Um portal com o mesmo nome já existe!");
        } else if (!provider.canAdd(area)) {
            user.sendMessage(MessageType.ERROR, "Não foi possível criar o portal nesta posição!");
        } else {

            Portal portal = provider.create(area, name);
            portal.setLocation(location);

            user.sendMessage(MessageType.SUCCESS, "O portal foi criado!");
        }

    }

    @CmdMapping("portal -e <portal> set message <message...>")
    @CmdDescription("Seta a mensagem de um portal")
    @CmdPermission("simplecraft.admin")
    public void portalSetMessage(Sender sender, Portal portal, String message) {

        portal.setMessage(message);
        sender.sendMessage(MessageType.SUCCESS, "A mensagem foi alterada!");

    }

    @CmdMapping("portal -e <portal> set target")
    @CmdDescription("Seta a posição de teleporte do portal")
    @CmdPermission("simplecraft.admin")
    @CmdParams({@Param(SimpleLocation.class)})
    public void portalSetTartet(User user, Portal portal, SimpleLocation location) {

        portal.setLocation(location);
        user.sendMessage(MessageType.SUCCESS, "O local de teleporte foi alterado!");

    }

    @CmdMapping("portal -e <portal> update")
    @CmdDescription("Atualiza a area do portal")
    @CmdPermission("simplecraft.admin")
    @CmdParams({@Param(PortalProvider.class), @Param(SimpleArea.class)})
    public void portalSetArea(User user, Portal portal, PortalProvider provider, SimpleArea area) {

        if (!provider.canUpdate(portal, area)) {
            user.sendMessage(MessageType.ERROR, "Não foi possível alterar a area!");
        } else {
            provider.updateArea(portal, area);
            user.sendMessage(MessageType.SUCCESS, "A area do portal foi alterada!");
        }

    }

    @CmdMapping("portal -e <portal> clear")
    @CmdDescription("Remove um portal")
    @CmdPermission("simplecraft.admin")
    public void portalRemove(Sender sender, Portal portal) {

        portal.remove();
        sender.sendMessage(MessageType.SUCCESS, "O portal foi removido!");

    }

    @CmdMapping("portal -e <portal> tp")
    @CmdDescription("Teleporta para um portal")
    @CmdPermission("simplecraft.admin")
    public void portalTp(User user, Portal portal) {

        if (user.teleport(portal)) {
            user.sendMessage(MessageType.SUCCESS, "Teleportando...");
        }

    }
    //======================================================
    //======================_COMMANDS=======================
    //======================================================

    @SupplierOptions("portal")
    class PortalConverter implements Supplier.Convert<Portal> {

        @Override
        public void tabComplete(TabArguments arguments) throws SQLException {
            arguments.offerSafe(repository.findNames(arguments.getComplete()));
        }

        @Override
        public Result<Portal> convert(ParameterBuilder builder, Sender sender, String value) throws SQLException {
            Portal portal = findByName(value);

            if (portal == null) return new Result.Error("O portal com o nome '" + value + "' não foi encontrado!");

            return new Result(portal);
        }
    }
}
