package com.github.manolo8.simplecraft.domain.chat;

import com.github.manolo8.simplecraft.domain.user.User;
import com.github.manolo8.simplecraft.domain.user.UserService;
import com.github.manolo8.simplecraft.utils.replace.Replace;

public class Chat {

    private UserService userService;
    private double globalCost;
    private double distance;
    private Replace format;

    public Chat(UserService userService) {
        this.userService = userService;
        this.format = new Replace("§7[{channel}] §7[§r{group}§7]§r {username}: §r{message}").compile();
        this.globalCost = 10;
        this.distance = 100;
    }

    public void userChatMessage(User user, String message) {
        message = format(user, message, "L");

        for (User target : userService.getLogged()) {
            double distance = target.distance(user);
            if (distance == -1 || distance > this.distance) continue;


            target.sendMessage(message);
        }
    }

    public void userGlobalMessage(User user, String message) {
        if (!user.withDraw(globalCost)) {
            user.sendMessage("§cVocê precisa de " + globalCost + " para falar no global!");
            return;
        }

        message = format(user, message, "G");

        for (User target : userService.getLogged()) target.sendMessage(message);
    }

    private String format(User user, String message, String channel) {
        String group = user.getGroup() == null ? "ERROR" : user.getGroup().getTag();

        return format
                .setValue("channel", channel)
                .setValue("group", group)
                .setValue("username", user.getName())
                .setValue("message", message).build();
    }
}
