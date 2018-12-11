package com.github.manolo8.simplecraft.module.plot;

import com.github.manolo8.simplecraft.core.commands.line.*;
import com.github.manolo8.simplecraft.core.commands.line.annotation.*;
import com.github.manolo8.simplecraft.core.commands.line.inf.Supplier;
import com.github.manolo8.simplecraft.core.service.ContainerService;
import com.github.manolo8.simplecraft.core.world.WorldInfo;
import com.github.manolo8.simplecraft.core.world.WorldService;
import com.github.manolo8.simplecraft.core.world.container.Container;
import com.github.manolo8.simplecraft.core.world.container.IContainer;
import com.github.manolo8.simplecraft.module.plot.member.PlotMember;
import com.github.manolo8.simplecraft.module.plot.user.PlotUser;
import com.github.manolo8.simplecraft.module.plot.view.PlotMembersView;
import com.github.manolo8.simplecraft.module.plot.view.PlotView;
import com.github.manolo8.simplecraft.module.user.MessageType;
import com.github.manolo8.simplecraft.module.user.User;
import com.github.manolo8.simplecraft.module.user.identity.Identity;
import com.github.manolo8.simplecraft.utils.def.Flag;
import com.github.manolo8.simplecraft.utils.def.Matcher;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Set;

@SuppressWarnings("unused")
public class PlotService extends ContainerService<Plot, PlotRepository> {

    private final WorldService worldService;

    public PlotService(WorldService worldService, PlotRepository repository) {
        super(repository, 1);
        this.worldService = worldService;
    }

    @Override
    public PlotProvider initProvider(WorldInfo worldInfo) {
        return new PlotProvider(worldInfo, this);
    }

    @Override
    public Matcher<Container> matcher() {
        return entity -> entity.getClass() == Plot.class;
    }

    public PlotUser findPlotUser(String name) throws SQLException {
        return repository.getPlotUserRepository().findOneByIdentity(name);
    }

    public Plot create(User user, String name, int worldId) throws SQLException {
        return repository.create(user, name, worldId);
    }

    public Plot create(User user, String name, int worldId, int x, int z) throws SQLException {
        return repository.create(user, name, worldId, x, z);
    }

    //======================================================
    //=======================COMMANDS=======================
    //======================================================
    @CmdInfo("plot")
    public void addInfo(Command command) {
        command.setAliases(Arrays.asList("p", "terreno"));
        command.setDescription("Comandos úteis do plot");
    }

    @CmdMapping("plot auto <?plot-name>")
    @CmdDescription("Reivindica um plot aleatório")
    @CmdPermission("simplecraft.user")
    @CmdChecker("canClaimMore")
    @CmdParams(@Param(value = PlotProvider.class, global = true))
    @CmdOptions(pvp = false)
    public void plotAutoClaim(User user, String name, PlotProvider provider) throws SQLException {

        if (!user.money().withdrawCoins(1000)) {
            user.sendMessage(MessageType.ERROR, "Você precisa ter 1000 coins para claimar!");
        } else {
            Plot plot = provider.autoClaim(user, name);
            user.teleport(plot);
            user.sendMessage(MessageType.SUCCESS, "O plot '" + plot.getName() + "' foi auto claimado!");
        }
    }

    @CmdMapping("plot claim <?plot-name>")
    @CmdDescription("Reinvindica o plot atual")
    @CmdPermission("simplecraft.user")
    @CmdChecker("canClaimMore")
    @CmdParams(@Param(PlotProvider.class))
    public void plotClaim(User user, String name, PlotProvider provider) throws SQLException {

        if (!user.money().hasCoins(1000)) {
            user.sendMessage(MessageType.ERROR, "Você precisa ter 1000 coins para claimar!");
        } else {
            Plot plot = provider.claim(user, name);

            if (plot == null) {
                user.sendMessage(MessageType.ERROR, "Plot indisponível!");
            } else {
                user.money().withdrawCoins(1000);
                user.sendMessage(MessageType.SUCCESS, "O plot " + plot.getName() + " foi claimado!");
            }
        }
    }


    @CmdMapping("plot set nome <plot-name>")
    @CmdDescription("Altera o nome do PLOT")
    @CmdPermission("simplecraft.user")
    @CmdChecker("isOwner")
    @CmdParams(@Param(Plot.class))
    public void plotSetName(User user, String name, Plot plot) {

        plot.setName(name);
        user.sendMessage(MessageType.SUCCESS, "O nome do plot foi alterado!");

    }

    @CmdMapping("plot set flag2 <plot-flag2> <boolean>")
    @CmdDescription("Altera uma flag2 do PLOT")
    @CmdPermission("simplecraft.user")
    @CmdChecker("isOwner")
    @CmdParams(@Param(Plot.class))
    public void plotSetFlag(User user, Flag.Toggle toggler, boolean value, Plot plot) {

        for (User inPlot : plot.getUsers()) {
            if (inPlot == user || inPlot.isAdmin()) continue;
            inPlot.teleport(plot);
            inPlot.sendAction("§cAlguma FLAG do plot foi alterada! Voltando...");
        }

        toggler.set(plot, Plot::getPlotFlag, value);
        user.sendMessage(MessageType.SUCCESS, "A flag2 " + toggler.getName() + " foi alterada para " + value + "!");
    }

    @CmdMapping("plot add <user>")
    @CmdDescription("Adiciona um jogador ao PLOT")
    @CmdPermission("simplecraft.user")
    @CmdChecker("isOwner")
    @CmdParams(@Param(value = Plot.class))
    public void plotAdd(User user, Identity identity, Plot plot) throws SQLException {

        if (identity == plot.getIdentity()) {
            user.sendMessage(MessageType.ERROR, "O jogador é dono!");
        } else if (plot.getMembers().size() >= 8) {
            user.sendMessage(MessageType.ERROR, "O limite de membros é 8!");
        } else if (plot.isMember(identity)) {
            user.sendMessage(MessageType.ERROR, "O jogador já é membro!");
        } else {
            plot.addMember(identity);
            user.createView(new PlotMembersView(plot));
            user.sendMessage(MessageType.SUCCESS, "O membro foi adicionado!");
        }
    }

    @CmdMapping("plot clear <user>")
    @CmdDescription("Remove um jogador do PLOT")
    @CmdPermission("simplecraft.user")
    @CmdChecker("isOwner")
    @CmdParams(@Param(value = Plot.class))
    public void plotRemove(User user, PlotMember member, Plot plot) {

        plot.removeMember(member);
        user.sendMessage(MessageType.SUCCESS, "O membro foi removido!");

    }

    @CmdMapping("plot kick <userNoPlot>")
    @CmdDescription("Kica um jogador que está no plot")
    @CmdPermission("simplecraft.user")
    @CmdChecker("isOwner")
    @CmdParams(@Param(Plot.class))
    public void plotKick(User user, User target, Plot plot) {

        target.teleport(plot);
        user.sendMessage(MessageType.SUCCESS, "O jogador foi kicado!");

    }

    @CmdMapping("plot membros")
    @CmdDescription("Ver a lista de membros do PLOT")
    @CmdPermission("simplecraft.user")
    @CmdChecker("isOwner")
    @CmdParams(@Param(value = Plot.class))
    public void plotMembersGUI(User user, Plot plot) {

        user.createView(new PlotMembersView(plot));

    }

    @CmdMapping("plot home <?user> <?value>")
    @CmdDescription("Ver a lista de todas as plots")
    @CmdPermission("simplecraft.user")
    @CmdOptions(pvp = false)
    public void plotHome(User user, PlotUser plotUser, int index) {

        PlotUser target = plotUser == null ? user.plot() : plotUser;

        if (target.getPlots().size() == 1) {
            user.teleport(target.getPlots().get(0));
        } else if (index != 0 && target.getPlots().size() >= index) {
            user.teleport(target.getPlots().get(index - 1));
        } else {
            user.createView(new PlotView(target));
        }

    }

    @CmdMapping("plot tp <plot>")
    @CmdDescription("Teleporta para um plot pelo nome")
    @CmdPermission("simplecraft.user")
    @CmdOptions(pvp = false)
    public void plotTp(User user, Plot plot) {

        if (user.teleport(plot)) {
            user.sendMessage(MessageType.SUCCESS, "Teleportado");
        }

    }

    //======================================================
    //=========================TOOLS========================
    //======================================================

    @CheckerOptions("isOwner")
    public boolean isOwner(User user) {
        Plot plot = user.iContainer().getClosest(Plot.class);

        return plot != null && plot.getIdentity() == user.identity();
    }

    @CheckerOptions("canClaimMore")
    public boolean canClaimMore(User user) {
        if (user.isAdmin()) return true;

        int limit = user.getPermissionQuantity("simplecraft.plot.limit");

        return user.plot().getQuantity() < limit;
    }

    @SupplierOptions("plot-name")
    class PlotNameConverter implements Supplier.Convert<String> {

        @Override
        public String defaultValue() {
            return null;
        }

        @Override
        public Result<String> convert(ParameterBuilder builder, Sender sender, String value) throws SQLException {
            if (value.toLowerCase().startsWith("terreno")) {
                return new Result.Error("O nome não pode começar com 'terreno'!");
            } else if (value.length() > 16) {
                return new Result.Error("O nome do plot deve ter no máximo 16 caracteres");
            } else if (exists(value)) {
                return new Result.Error("O nome '" + value + "' não está disponível!");
            } else {
                return new Result<>(value);
            }
        }
    }

    @SupplierOptions("plot-flag2")
    class PlotFlagToggleConverter implements Supplier.Convert<Flag.Toggle> {

        @Override
        public void tabComplete(TabArguments arguments) {
            for (Flag.Toggle toggle : PlotFlag.togglers) {
                arguments.offer(toggle.getName());
            }
        }

        @Override
        public Result<Flag.Toggle> convert(ParameterBuilder builder, Sender sender, String value) {

            for (Flag.Toggle toggle : PlotFlag.togglers) {
                if (toggle.getName().equals(value)) return new Result<>(toggle);
            }

            return new Result.Error("A flag2 '" + value + "' não foi encontrada!");
        }
    }

    @SupplierOptions("plot")
    class PlotConverter implements Supplier.Convert<Plot> {

        @Override
        public void tabComplete(TabArguments arguments) throws SQLException {
            arguments.offerSafe(repository.findNames(arguments.getComplete()));
        }

        @Override
        public Result<Plot> convert(ParameterBuilder builder, Sender sender, String value) throws SQLException {
            Plot plot = findByName(value);

            if (plot == null) return new Result.Error("O plot '" + value + "' não foi encontrado");

            return new Result<>(plot);
        }
    }

    @SupplierOptions("user")
    class PlotUserConverter implements Supplier.Convert<PlotUser> {

        @Override
        public void tabComplete(TabArguments arguments) throws SQLException {
            arguments.offerSafe(repository.getPlotUserRepository().getIdentityRepository().findNames(arguments.getComplete()));
        }

        @Override
        public Result<PlotUser> convert(ParameterBuilder builder, Sender sender, String value) throws SQLException {
            PlotUser user = repository.getPlotUserRepository().findOneByIdentity(value);

            if (user == null) return new Result.Error("O jogador não foi encontrado");

            return new Result(user);
        }
    }

    @SupplierOptions("user")
    class PlotMemberConverter implements Supplier.Convert<PlotMember> {

        @Override
        public void tabComplete(TabArguments arguments) {
            Plot plot = arguments.parameters().getByType(Plot.class);

            for (PlotMember member : plot.getMembers()) {
                arguments.offer(member.getIdentity().getName());
            }
        }

        @Override
        public Result<PlotMember> convert(ParameterBuilder builder, Sender sender, String value) {
            Plot plot = builder.getByType(Plot.class);

            PlotMember member = plot.getMember(value.toLowerCase());

            if (member == null) return new Result.Error("O membro não foi encontrado!");

            return new Result<>(member);
        }
    }

    @SupplierOptions("userNoPlot")
    class UserInPlotConverter implements Supplier.Convert<User> {

        @Override
        public void tabComplete(TabArguments arguments) {
            Plot plot = arguments.parameters().getByType(Plot.class);

            for (User user : plot.getUsers()) {
                if (user.isAdmin()) continue;
                arguments.offer(user.identity().getName());
            }
        }

        @Override
        public Result<User> convert(ParameterBuilder builder, Sender sender, String value) {
            Plot plot = builder.getByType(Plot.class);

            value = value.toLowerCase();

            Set<User> users = plot.getUsers();

            for (User user : users) {
                if (user.isAdmin()) continue;
                if (user.identity().match(value))
                    return new Result<>(user);
            }

            return new Result.Error("O jogador '" + value + "' não foi encontrado!");
        }
    }

    @SupplierOptions
    class BasicPlotSupplier implements Supplier.Basic<Plot> {

        @Override
        public Result<Plot> provide(Sender sender, Class clazz) {
            User user = sender.user();

            IContainer iContainer = user.iContainer();

            Plot plot = user.iContainer().getClosest(Plot.class);

            if (plot != null && plot.getIdentity() == user.identity()) {
                return new Result<>(plot);
            } else {
                return new Result.Error("O IContainer '" + iContainer.getClass().getSimpleName() + "' não é um PLOT!");
            }
        }

    }
    //======================================================
    //========================_TOOLS========================
    //======================================================


    //======================================================
    //======================_COMMANDS=======================
    //======================================================

}
