package com.github.manolo8.simplecraft.module.region;

import com.github.manolo8.simplecraft.core.commands.line.*;
import com.github.manolo8.simplecraft.core.commands.line.annotation.*;
import com.github.manolo8.simplecraft.core.commands.line.inf.Supplier;
import com.github.manolo8.simplecraft.core.service.ContainerService;
import com.github.manolo8.simplecraft.core.world.WorldInfo;
import com.github.manolo8.simplecraft.core.world.container.Container;
import com.github.manolo8.simplecraft.module.user.MessageType;
import com.github.manolo8.simplecraft.module.user.User;
import com.github.manolo8.simplecraft.utils.def.Flag;
import com.github.manolo8.simplecraft.utils.def.Matcher;
import com.github.manolo8.simplecraft.utils.location.SimpleArea;

import java.sql.SQLException;

@SuppressWarnings("unused")
public class RegionService
        extends ContainerService<Region, RegionRepository> {

    public RegionService(RegionRepository repository) {
        super(repository, 0);
    }

    @Override
    public RegionProvider initProvider(WorldInfo worldInfo) {
        return new RegionProvider(worldInfo, this);
    }

    @Override
    public Matcher<Container> matcher() {
        return entity -> entity.getClass() == Region.class;
    }

    //======================================================
    //=======================COMMANDS=======================
    //======================================================
    @CmdInfo("region")
    public void addInfo(Command command) {
        command.setDescription("Comandos úteis das regiões");
    }

    @CmdMapping("region create <name>")
    @CmdDescription("Cria uma região")
    @CmdPermission("simplecraft.admin")
    @CmdParams({@Param(RegionProvider.class), @Param(SimpleArea.class)})
    public void regionCreate(User user, String name, RegionProvider provider, SimpleArea area) throws SQLException {

        if (provider.exists(name)) {
            user.sendMessage(MessageType.ERROR, "Já existe uma regiao com esse nome!");
        } else if (!provider.canAdd(area)) {
            user.sendMessage(MessageType.ERROR, "Não foi possível criar a regiao neste local");
        } else {
            provider.create(user.points().asSimpleArea(), name);
            user.sendMessage(MessageType.SUCCESS, "A regiao foi criada!");
        }

    }

    @CmdMapping("region -e <region> tp")
    @CmdDescription("Teleporta para um local disponível na region")
    @CmdPermission("simplecraft.admin")
    public void regionTp(User user, Region region) {
        if (user.teleport(region)) {
            user.sendMessage(MessageType.SUCCESS, "Teleportando...");
        }
    }

    @CmdMapping("region -e <region> set flag2 <region-flag2> <boolean>")
    @CmdDescription("Altera uma flag2 da região")
    @CmdPermission("simplecraft.admin")
    public void regionFlag(Sender sender, Region region, Flag.Toggle toggler, boolean value) {

        toggler.set(region, Region::flags, value);
        sender.sendMessage(MessageType.SUCCESS, "A flag2 " + toggler.getName() + " foi alterada para " + value + "!");

    }

    @CmdMapping("region -e <region> set rank <value>")
    @CmdDescription("Seta o rank mínimo")
    @CmdPermission("simplecraft.admin")
    public void regionSetMinRank(User user, Region region, int rank) {

        region.setMinRank(rank);
        user.sendMessage(MessageType.SUCCESS, "A rank mínimo foi alterado!");
    }

    @CmdMapping("region -e <region> clear")
    @CmdDescription("Remove uma region")
    @CmdPermission("simplecraft.admin")
    public void regionRemove(Sender sender, Region region) {

        region.remove();
        sender.sendMessage(MessageType.SUCCESS, "A região foi removida com sucesso!");

    }

    @CmdMapping("region -e <region> update")
    @CmdDescription("Atualiza a região")
    @CmdPermission("simplecraft.admin")
    @CmdParams({@Param(RegionProvider.class), @Param(SimpleArea.class)})
    public void regionUpdateArea(User user, Region region, RegionProvider provider, SimpleArea area) {

        if (!provider.canUpdate(region, area)) {
            user.sendMessage(MessageType.ERROR, "Não foi possível alterar a região!");
        } else {

            provider.updateArea(region, area);
            user.sendMessage(MessageType.SUCCESS, "A região foi alterada!");
        }

    }

    //======================================================
    //=========================TOOLS========================
    //======================================================

    @SupplierOptions("region")
    class RegionConverter implements Supplier.Convert<Region> {

        @Override
        public void tabComplete(TabArguments arguments) throws SQLException {
            arguments.offerSafe(repository.findNames(arguments.getComplete()));
        }

        @Override
        public Result<Region> convert(ParameterBuilder builder, Sender sender, String value) throws SQLException {
            Region region = findByName(value);

            if (region == null) return new Result.Error("A regiao com o nome '" + value + "' não foi encontrada!");

            return new Result<>(region);
        }
    }

    @SupplierOptions("region-flag2")
    class PlotFlagToggleConverter implements Supplier.Convert<Flag.Toggle> {

        @Override
        public void tabComplete(TabArguments arguments) {
            for (Flag.Toggle toggle : RegionFlag.togglers) {
                arguments.offer(toggle.getName());
            }
        }

        @Override
        public Result<Flag.Toggle> convert(ParameterBuilder builder, Sender sender, String value) {

            for (Flag.Toggle toggle : RegionFlag.togglers) {
                if (toggle.getName().equals(value)) return new Result<>(toggle);
            }

            return new Result.Error("A flag2 '" + value + "' não foi encontrada!");
        }
    }

    //======================================================
    //========================_TOOLS========================
    //======================================================


    //======================================================
    //======================_COMMANDS=======================
    //======================================================
}
