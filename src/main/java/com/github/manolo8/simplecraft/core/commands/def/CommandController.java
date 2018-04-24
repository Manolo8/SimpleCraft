package com.github.manolo8.simplecraft.core.commands.def;

import com.github.manolo8.simplecraft.core.commands.def.annotation.CommandMapping;
import com.github.manolo8.simplecraft.modules.user.User;
import com.github.manolo8.simplecraft.modules.user.UserService;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Willian
 */
public class CommandController implements CommandExecutor {

    private final UserService userService;
    private final Object commands;
    private final List<Method> methods;

    public CommandController(UserService userService, Object object) {
        this.userService = userService;
        this.methods = new ArrayList<>();
        this.commands = object;

        for (Method method : object.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(CommandMapping.class)) {
                methods.add(method);
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String string, String[] args) {
        if (!(cs instanceof Player)) {
            return true;
        }

        User user = userService.getOnlineUser((Player) cs);

        String command = cmd.getName().toLowerCase();

        for (Method method : methods) {
            CommandMapping annotation = method.getAnnotation(CommandMapping.class);

            if (!annotation.command().equals(command))
                continue;

            if (args.length != 0 && !args[0].toLowerCase().equals(annotation.subCommand())) {
                continue;
            }

            if (!ArrayUtils.contains(annotation.args(), args.length)) {
                user.sendMessage(annotation.usage());
                return true;
            }

//            if (!(!ArrayUtils.contains(annotation.args(), args.length)
//                    || !(ArrayUtils.contains(annotation.args(), -1)
//                    && annotation.args().length == 2
//                    && args.length > annotation.args()[1]))) {
//                user.sendMessage(annotation.usage());
//                return true;
//            }
//
//
//            if (!(ArrayUtils.contains(annotation.args(), 0)))
//                if (args.length == 0 || !annotation.subCommand().equals(args[0].toLowerCase())
//                        && !annotation.subCommand().isEmpty())
//                    continue;

            if (!user.hasPermission(annotation.permission())) {
                user.sendMessage(annotation.permissionMessage());
                return true;
            }

            try {
                Object object = method.invoke(commands, user, args);
                if (object instanceof Boolean && !(Boolean) object)
                    user.sendMessage(annotation.usage());
                return true;
            } catch (Exception e) {
                user.sendMessage("§cAn internal error occurred");
                e.printStackTrace();
            }
        }
        sendHelp(user, command);
        return true;
    }

    private void sendHelp(User user, String cmd) {
        StringBuilder builder = new StringBuilder();

        for (Method method : methods) {
            CommandMapping mapping = method.getAnnotation(CommandMapping.class);
            if (!mapping.command().equals(cmd)) continue;
            builder.append(mapping.usage()).append("\n");
        }
        user.sendMessage(builder.toString());
    }
}
