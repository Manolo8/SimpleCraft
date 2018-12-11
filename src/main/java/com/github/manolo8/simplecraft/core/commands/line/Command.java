package com.github.manolo8.simplecraft.core.commands.line;

import com.github.manolo8.simplecraft.module.user.MessageType;
import com.github.manolo8.simplecraft.module.user.User;
import com.github.manolo8.simplecraft.module.user.UserService;
import com.github.manolo8.simplecraft.utils.def.ArrayUtils;
import com.github.manolo8.simplecraft.utils.def.PageableList;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Command extends BukkitCommand {

    private final UserService userService;
    private final CommandSection commandSection;

    protected Command(UserService userService, String name) {
        super(name);

        this.userService = userService;
        this.commandSection = new CommandSection(name);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args) {

        Sender sender;

        if (commandSender instanceof Player) {
            sender = new Sender(userService.getLogged((Player) commandSender));
        } else {
            sender = new Sender(commandSender);
        }

        args = ArrayUtils.addFirst(args, getName());

        //HELP HANDLER
        if (args.length == 1 && !commandSection.hasOptionalHandler() && sender.hasPermission(commandSection)) {

            sender.sendMessage(MessageType.INFO, "Para ajuda use /" + getName() + " ajuda <?página>");
            return true;

        } else if (args.length > 1 && args[1].equals("ajuda")) {

            int page = 0;

            if (args.length == 3) {
                page = NumberUtils.toInt(args[2]);

                if (page < 0 || page > 100) page = 0;
            }

            sendHelp(sender, page);
            return true;
        }
        //HELP HANDLER

        CommandSection section = commandSection.getOrUpdate(args);

        try {
            section.handle(sender, args);
        } catch (Exception e) {
            sender.sendMessage(MessageType.ERROR, "Houve um erro interno. Contate um ADMIN!");
            e.printStackTrace();
        }

        return true;
    }

    private void sendHelp(Sender sender, int pg) {
        List<String> messages = commandSection.createHelp(sender);

        if (messages.isEmpty()) {
            sender.sendMessage(MessageType.ERROR, "Você não tem permissão para isso!");
            return;
        }

        PageableList<String> page = new PageableList(messages, "§c->  Ajuda para o comando " + getName(), pg, 5);

        sender.sendMessage(page.build((item, builder, current) -> {
            builder.append(item).append('\n');
        }));
    }

    @Override
    public List<String> tabComplete(CommandSender commandSender, String alias, String[] args, Location location) throws IllegalArgumentException {
        List<String> strings = new ArrayList<>();

        Sender sender;

        if (commandSender instanceof Player) {
            sender = new Sender(userService.getLogged((Player) commandSender));
        } else {
            sender = new Sender(commandSender);
        }

        args = ArrayUtils.addFirst(args, getName());

        //HELP HANDLER
        if (args.length > 1 && args[1].length() > 0 && "ajuda".startsWith(args[1])) {
            strings.add("ajuda");
            if (args[1].equals("ajuda")) {
                return strings;
            }
        }
        //HELP HANDLER

        CommandSection section = commandSection.getOrUpdate(args);

        try {
            section.handleTab(sender, args, strings);
        } catch (Exception e) {
            sender.sendMessage(MessageType.ERROR, "Houve um erro interno. Contate um ADMIN!");
            e.printStackTrace();
        }

        return strings;
    }

    @Override
    public boolean testPermission(CommandSender target) {
        return testPermissionSilent(target);
    }

    @Override
    public boolean testPermissionSilent(CommandSender target) {
        if (target instanceof Player) {

            User user = userService.getLogged((Player) target);

            //1.13 FIX
            if (user == null) {

                //WHEN IS LOGGIN, that always happen, nice --'

                try {
                    userService.join((Player) target);
                } catch (SQLException e) {
                    e.printStackTrace();
                    ((Player) target).kickPlayer("Houve um erro '-'");
                    return false;
                }

                user = userService.getLogged((Player) target);
            }
            //1.13 FIX

            return commandSection.hasPermission(user);

        } else return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        return tabComplete(sender, alias, args, null);
    }

    protected void createHandler(OptionsRegistry converter, Object object, Method method, String[] args) {
        this.commandSection.build(converter, args, new CommandHandler(object, method));
    }

    public CommandSection getSection() {

        return commandSection;
    }
}
