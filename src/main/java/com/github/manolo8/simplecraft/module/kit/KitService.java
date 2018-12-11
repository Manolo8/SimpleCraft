package com.github.manolo8.simplecraft.module.kit;

import com.github.manolo8.simplecraft.core.commands.line.*;
import com.github.manolo8.simplecraft.core.commands.line.annotation.*;
import com.github.manolo8.simplecraft.core.commands.line.inf.Supplier;
import com.github.manolo8.simplecraft.core.service.NamedHolderService;
import com.github.manolo8.simplecraft.module.kit.user.delay.KitDelay;
import com.github.manolo8.simplecraft.module.kit.view.KitView;
import com.github.manolo8.simplecraft.module.user.MessageType;
import com.github.manolo8.simplecraft.module.user.User;
import com.github.manolo8.simplecraft.utils.def.StringUtils;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;
import java.util.Collections;

@SuppressWarnings("unused")
public class KitService extends NamedHolderService<Kit, KitRepository> {

    public KitService(KitRepository kitRepository) {
        super(kitRepository);
    }

    //======================================================
    //=======================COMMANDS=======================
    //======================================================
    @CmdInfo("kit")
    public void addInfo(Command command) {
        command.setAliases(Collections.singletonList("kits"));
        command.setDescription("Comandos úteis dos kits");
    }

    @CmdMapping("kit")
    @CmdDescription("Abrir a GUI dos kits")
    @CmdPermission("simplecraft.user")
    public void kit(User user) {
        user.createView(new KitView(this));
    }

    @CmdMapping("kit <kit>")
    @CmdDescription("Usar um kit")
    @CmdPermission("simplecraft.user")
    public void kitUse(User user, Kit kit) throws SQLException {

        if (kit.canUse(user)) {
            KitDelay delay = user.kit().getDelay(kit);

            if (delay.use()) {
                delay.getKit().giveTo(user);
                user.sendMessage(MessageType.SUCCESS, "Kit recebido!");
            } else {
                user.sendMessage(MessageType.ERROR, "Espere " + StringUtils.longTimeToString(delay.getWaitTime()));
            }

        } else {
            user.sendMessage(MessageType.ERROR, "Você não pode usar este KIT!");
        }
    }

    @CmdMapping("kit <kit> clear")
    @CmdDescription("Remove um kit")
    @CmdPermission("simplecraft.admin")
    public void kitRemove(Sender sender, Kit kit) {

        remove(kit);
        sender.sendMessage(MessageType.SUCCESS, "O kit foi removido!");
    }

    @CmdMapping("kit create <name>")
    @CmdDescription("Cria um kit")
    @CmdPermission("simplecraft.admin")
    public void kitCreate(Sender sender, String name) throws SQLException {

        if (exists(name)) {
            sender.sendMessage(MessageType.ERROR, "Esse kit já existe!");
        } else {
            create(name);
            sender.sendMessage(MessageType.SUCCESS, "O kit foi criado com sucesso!");
        }
    }

    @CmdMapping("kit <kit> add <item> <?quantity>")
    @CmdDescription("Adiciona um item ao kit")
    @CmdPermission("simplecraft.admin")
    public void kitAdd(Sender sender, Kit kit, ItemStack item, int quantity) throws SQLException {

        if (quantity != 0) item.setAmount(quantity);

        kit.addItem(item);

        sender.sendMessage(MessageType.SUCCESS, "O item foi adicionado com sucesso!");
    }

    @CmdMapping("kit <kit> clear")
    @CmdDescription("Remove todos os itens do kit")
    @CmdPermission("simplecraft.admin")
    public void kitClear(Sender sender, Kit kit) {

        kit.clearItems();
        sender.sendMessage(MessageType.SUCCESS, "O os itens do kit foram removidos");
    }

    @CmdMapping("kit <kit> set delay <time>")
    @CmdDescription("Seta o tempo de espera de um kit")
    @CmdPermission("simplecraft.admin")
    public void kitSetDelay(Sender sender, Kit kit, long delay) {

        kit.setDelay(delay);
        sender.sendMessage(MessageType.SUCCESS, "O delay foi setado para " + StringUtils.longTimeToString(delay) + " com sucesso!");
    }

    @CmdMapping("kit <kit> set slot <value>")
    @CmdDescription("Seta o slot de um kit na gui")
    @CmdPermission("simplecraft.admin")
    public void kitSetSlot(Sender sender, Kit kit, int slot) {

        kit.setSlot(slot);
        sender.sendMessage(MessageType.SUCCESS, "O slot foi setado com sucesso!");
    }

    @CmdMapping("kit <kit> set rank <value>")
    @CmdDescription("Seta o rank minímo de um kit")
    @CmdPermission("simplecraft.admin")
    public void setKitRank(Sender sender, Kit kit, int rank) {

        kit.setRank(rank);
        sender.sendMessage(MessageType.SUCCESS, "O rank foi setado com sucesso!");
    }

    //======================================================
    //=========================TOOLS========================
    //======================================================

    @SupplierOptions("kit")
    class KitDelayConverter implements Supplier.Convert<Kit> {

        @Override
        public void tabComplete(TabArguments arguments) throws SQLException {
            if (arguments.sender().isConsole()) {
                arguments.offerSafe(repository.findNames(arguments.getComplete()));
            } else {
                User user = arguments.sender().user();

                for(Kit kit : entities) {
                    if (kit.canUse(user) || user.isAdmin()) arguments.offer(kit.getName());
                }
            }
        }

        @Override
        public Result<Kit> convert(ParameterBuilder builder, Sender sender, String value) {
            if (sender.isConsole()) return new Result.Error("Você é o console '-'");

            Kit kit = findByName(value);

            if (kit == null) return new Result.Error("Kit não encontrado");

            return new Result(kit);
        }
    }
    //======================================================
    //========================_TOOLS========================
    //======================================================


    //======================================================
    //======================_COMMANDS=======================
    //======================================================
}
