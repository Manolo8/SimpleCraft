package com.github.manolo8.simplecraft.core.commands.line;

import com.github.manolo8.simplecraft.module.user.MessageType;
import com.github.manolo8.simplecraft.module.user.User;
import org.bukkit.command.CommandSender;

public class Sender {

    private User user;
    private CommandSender sender;

    public Sender(User user) {
        this.user = user;
    }

    public Sender(CommandSender sender) {
        this.sender = sender;
    }

    public boolean isConsole() {
        return user == null;
    }

    public void sendMessage(Object message) {
        if (isConsole()) {
            sender.sendMessage(String.valueOf(message));
        } else {
            user.sendMessage(message);
        }
    }

    public void sendMessage(MessageType type, Object message) {
        if (isConsole()) {
            sender.sendMessage(type.format(message));
        } else {
            user.sendMessage(type, message);
        }
    }

    public boolean hasPermission(String permission) {
        return isConsole() || user.hasPermission(permission);
    }

    public boolean hasPermission(CommandSection section) {
        return isConsole() || section.hasPermission(user);
    }

    public User user() {
        return user;
    }
}
