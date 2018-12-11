package com.github.manolo8.simplecraft.module.clan.clanarea;

import com.github.manolo8.simplecraft.core.commands.line.ParameterBuilder;
import com.github.manolo8.simplecraft.core.commands.line.Result;
import com.github.manolo8.simplecraft.core.commands.line.Sender;
import com.github.manolo8.simplecraft.core.commands.line.TabArguments;
import com.github.manolo8.simplecraft.core.commands.line.annotation.*;
import com.github.manolo8.simplecraft.core.commands.line.inf.Supplier;
import com.github.manolo8.simplecraft.core.service.ContainerService;
import com.github.manolo8.simplecraft.core.world.WorldInfo;
import com.github.manolo8.simplecraft.core.world.container.Container;
import com.github.manolo8.simplecraft.module.clan.Clan;
import com.github.manolo8.simplecraft.module.user.MessageType;
import com.github.manolo8.simplecraft.module.user.User;
import com.github.manolo8.simplecraft.utils.def.Matcher;
import com.github.manolo8.simplecraft.utils.def.PageableList;
import com.github.manolo8.simplecraft.utils.def.StringUtils;
import com.github.manolo8.simplecraft.utils.location.SimpleArea;

import java.sql.SQLException;

@SuppressWarnings("unused")
public class ClanAreaService extends ContainerService<ClanArea, ClanAreaRepository> {

    public ClanAreaService(ClanAreaRepository repository) {
        super(repository, 5);
    }

    @Override
    public ClanAreaProvider initProvider(WorldInfo worldInfo) {
        return new ClanAreaProvider(worldInfo, this);
    }

    @Override
    public Matcher<Container> matcher() {
        return entity -> entity.getClass() == ClanArea.class;
    }

    //======================================================
    //=======================COMMANDS=======================
    //======================================================
    @CmdMapping("clanarea create <name>")
    @CmdDescription("Cria uma area")
    @CmdPermission("simplecraft.admin")
    @CmdParams({@Param(ClanAreaProvider.class), @Param(SimpleArea.class)})
    public void clanAreaCreate(User user, String name, ClanAreaProvider provider, SimpleArea area) throws SQLException {

        if (provider.exists(name)) {
            user.sendMessage(MessageType.ERROR, "Já existe uma area com esse nome!");
            return;
        }

        if (!provider.canAdd(area)) {
            user.sendMessage(MessageType.ERROR, "Não foi possível criar a area neste local");
            return;
        }

        provider.create(user.points().asSimpleArea(), name);
        user.sendMessage(MessageType.SUCCESS, "A area foi criada!");
    }

    @CmdMapping("clanarea list <?page>")
    @CmdDescription("Mostra a lista de todas as areas de clan")
    @CmdPermission("simplecraft.admin")
    public void clanAreaList(Sender sender, int pg) throws SQLException {
        PageableList<ClanArea> page = new PageableList(findAll(), "§c->  Lista de todas as areas de clan", pg, 10);

        sender.sendMessage(page.build((item, builder, current) -> {
            builder.append("§7");
            StringUtils.fill(true, 100, current, ' ', builder);
            builder.append("º  §r");
            StringUtils.fill(false, 400, item.getName(), '.', builder);
            builder.append(" §7Dominador: ").append(item.getClan() == null ? "Ninguém" : item.getClan().getColoredTag()).append("\n");
        }));
    }

    @CmdMapping("clanarea -e <area> tp")
    @CmdDescription("Teleporta para um local disponível na area")
    @CmdPermission("simplecraft.admin")
    public void clanAreaTp(User user, ClanArea clanArea) {
        if (user.teleport(clanArea)) {
            user.sendMessage(MessageType.SUCCESS, "Teleportando...");
        }
    }

    @CmdMapping("clanarea -e <area> owner set <clan>")
    @CmdDescription("Seta o clan dominador de uma área")
    @CmdPermission("simplecraft.admin")
    public void clanAreaOwnerSet(Sender sender, ClanArea area, Clan clan) {

        area.changeClan(clan, true);
        sender.sendMessage(MessageType.SUCCESS, "O dominador foi alterado!");
    }

    @CmdMapping("clanarea -e <area> owner clear")
    @CmdDescription("Remove o clan dominador de uma área")
    @CmdPermission("simplecraft.admin")
    public void clanAreaOwnerRemove(Sender sender, ClanArea area) {

        area.changeClan(null, true);
        sender.sendMessage(MessageType.SUCCESS, "O dominador foi removido!");
    }

    @CmdMapping("clanarea -e <area> clear")
    @CmdDescription("Remove uma area")
    @CmdPermission("simplecraft.admin")
    public void clanAreaRemove(Sender sender, ClanArea clanArea) {

        clanArea.remove();
        sender.sendMessage(MessageType.SUCCESS, "A area foi removida com sucesso!");

    }

    @CmdMapping("clanarea -e <area> update")
    @CmdDescription("Atualiza a area do clan")
    @CmdPermission("simplecraft.admin")
    @CmdParams({@Param(ClanAreaProvider.class), @Param(SimpleArea.class)})
    public void clanAreaSetArea(User user, ClanArea clanArea, ClanAreaProvider provider, SimpleArea area) {

        if (!provider.canUpdate(clanArea, area)) {
            user.sendMessage(MessageType.ERROR, "Não foi possível alterar a area!");
            return;
        }

        provider.updateArea(clanArea, area);
        user.sendMessage(MessageType.SUCCESS, "A area foi alterada!");
    }

    //======================================================
    //=========================TOOLS========================
    //======================================================

    @SupplierOptions("area")
    class ClanAreaConverter implements Supplier.Convert<ClanArea> {

        @Override
        public void tabComplete(TabArguments arguments) throws SQLException {
            arguments.offerSafe(repository.findNames(arguments.getComplete()));
        }

        @Override
        public Result<ClanArea> convert(ParameterBuilder builder, Sender sender, String value) throws SQLException {
            ClanArea area = findByName(value);

            if (area == null) return new Result.Error("A area com o nome '" + value + "' não foi encontrado!");

            return new Result<>(area);
        }
    }
    //======================================================
    //========================_TOOLS========================
    //======================================================

    //======================================================
    //======================_COMMANDS=======================
    //======================================================

}
