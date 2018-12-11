package com.github.manolo8.simplecraft.module.warp;

import com.github.manolo8.simplecraft.core.commands.line.ParameterBuilder;
import com.github.manolo8.simplecraft.core.commands.line.Result;
import com.github.manolo8.simplecraft.core.commands.line.Sender;
import com.github.manolo8.simplecraft.core.commands.line.TabArguments;
import com.github.manolo8.simplecraft.core.commands.line.annotation.*;
import com.github.manolo8.simplecraft.core.commands.line.inf.Supplier;
import com.github.manolo8.simplecraft.core.service.NamedHolderService;
import com.github.manolo8.simplecraft.module.user.MessageType;
import com.github.manolo8.simplecraft.module.user.User;
import com.github.manolo8.simplecraft.utils.location.SimpleLocation;
import org.bukkit.Material;

import java.sql.SQLException;

@SuppressWarnings("unused")
public class WarpService extends NamedHolderService<Warp, WarpRepository> {

    private static WarpService instance;

    public WarpService(WarpRepository repository) {
        super(repository);
        instance = this;
    }

    public static Warp findSpawn() {
        return instance.findByName("spawn");
    }

    //======================================================
    //=======================COMMANDS=======================
    //======================================================

    @CmdMapping("warp")
    @CmdDescription("Abre a gui dos warps")
    @CmdPermission("simplecraft.user")
    @CmdOptions(pvp = false)
    public void warp(User user) {
        user.createView(new WarpView(this));
    }

    @CmdMapping("warp <warp>")
    @CmdDescription("Teleporta para um warp")
    @CmdPermission("simplecraft.user")
    @CmdOptions(pvp = false)
    public void warp(User user, Warp warp) {

        if (user.rank().get() < warp.getMinRank()) {
            user.sendMessage(MessageType.ERROR, "Seu RANK é insuficiente!");
        } else if (user.teleport(warp)) {
            user.sendMessage(MessageType.SUCCESS, "Teleportando!");
        } else {
            user.sendMessage(MessageType.ERROR, "Não foi possível!");
        }
    }

    @CmdMapping("warp create <name>")
    @CmdDescription("Cria um warp no seu local atual")
    @CmdPermission("simplecraft.admin")
    @CmdParams({@Param(SimpleLocation.class)})
    public void warpCreate(User user, String name, SimpleLocation location) throws SQLException {

        if (exists(name)) {
            user.sendMessage(MessageType.ERROR, "Já existe um WARP com esse nome =/");
        } else {
            Warp warp = create(name);
            warp.setWorldInfo(user.worldInfo());
            warp.setIcon(Material.STONE);
            warp.setLocation(location);
            user.sendMessage(MessageType.SUCCESS, "Warp criado!");
        }
    }

    @CmdMapping("warp <warp> set rank <value>")
    @CmdDescription("Seta o rank minimo para usar o WARP")
    @CmdPermission("simplecraft.admin")
    public void warpSetRank(Sender sender, Warp warp, int rank) {

        warp.setMinRank(rank);
        sender.sendMessage(MessageType.SUCCESS, "O rank foi alterado!");
    }

    @CmdMapping("warp <warp> clear")
    @CmdDescription("Remove um WARP")
    @CmdPermission("simplecraft.admin")
    public void warpRemove(Sender sender, Warp warp) {

        remove(warp);
        sender.sendMessage(MessageType.SUCCESS, "O warp foi removido!");
    }

    @CmdMapping("warp <warp> set slot <value>")
    @CmdDescription("Seta o slot da gui do warp")
    @CmdPermission("simplecraft.admin")
    public void warpSetSlot(Sender sender, Warp warp, int slot) {

        warp.setSlot(slot);
        sender.sendMessage(MessageType.SUCCESS, "O slot foi alterado!");
    }

    @CmdMapping("warp <warp> set icon <material>")
    @CmdDescription("Seta o icone do warp")
    @CmdPermission("simplecraft.admin")
    public void warpSetIcon(Sender sender, Warp warp, Material material) {
        warp.setIcon(material);
        sender.sendMessage(MessageType.SUCCESS, "O icone foi alterado!");
    }

    @CmdMapping("warp <warp> set location")
    @CmdDescription("Seta o local de teleporte")
    @CmdPermission("simplecraft.admin")
    @CmdParams(@Param(SimpleLocation.class))
    public void warpSetLocation(User user, Warp warp, SimpleLocation location) {

        warp.setWorldInfo(user.worldInfo());
        warp.setLocation(location);
        user.sendMessage(MessageType.SUCCESS, "O local foi atualizado!");
    }

    @SupplierOptions("warp")
    class WarpConverter implements Supplier.Convert<Warp> {

        @Override
        public void tabComplete(TabArguments arguments) throws SQLException {
            if (arguments.sender().isConsole()) {
                arguments.offerSafe(repository.findNames(arguments.getComplete()));
            } else {
                int rank = arguments.sender().user().rank().get();

                for (Warp warp : entities) {
                    if (warp.getMinRank() <= rank) {
                        arguments.offer(warp.getFastName());
                    }
                }
            }
        }

        @Override
        public Result<Warp> convert(ParameterBuilder builder, Sender sender, String value) {
            Warp warp = findByName(value);

            if (warp == null) return new Result.Error("O warp '" + value + "' não foi encontrado!");

            return new Result<>(warp);
        }
    }
    //======================================================
    //=======================COMMANDS=======================
    //======================================================

}
