package com.github.manolo8.simplecraft.core.chat;

import com.github.manolo8.simplecraft.module.clan.Clan;
import com.github.manolo8.simplecraft.module.clan.user.ClanUser;
import com.github.manolo8.simplecraft.module.user.MessageType;
import com.github.manolo8.simplecraft.module.user.User;
import com.github.manolo8.simplecraft.module.user.UserService;
import com.github.manolo8.simplecraft.module.user.identity.Identity;
import com.github.manolo8.simplecraft.utils.calculator.FontWidthCalculator;
import com.github.manolo8.simplecraft.utils.def.StringUtils;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class Chat {

    private UserService userService;
    private int distance;
    private PrefixBuilder builder;
    private List<Pattern> patterns = new ArrayList<>();

    public Chat(UserService userService) {
        this.userService = userService;
        this.builder = new PrefixBuilder();
        this.distance = 200 * 200;


        patterns.add(Pattern.compile("(?i)([ .]+f+d+p+[ .])"));
        patterns.add(Pattern.compile("(?i)([. ]c+[úÚu]+)[ .]"));
        patterns.add(Pattern.compile("(?i)([. ]p[uúÚ]+t+[áÁãÃàÀa4]+)[ .]"));
        patterns.add(Pattern.compile("(?i)([. ]s+[áÁãÃàÀa4]+c+o)[ .]"));
        patterns.add(Pattern.compile("(?i)([. ]o+t+[áÁãÃàÀa4]+r+i+[oóÓòÒõÔ0])[ .]"));
        patterns.add(Pattern.compile("(?i)([. ]c+[áÁãÃàÀa4]+r+[áÁãÃàÀa4]+l+h+[oóÓòÒõÔ0]+)[ .]"));
        patterns.add(Pattern.compile("(?i)([. ]p+[óÓòÒõÔo0]+r+[áÁãÃàÀa4][ .])"));

        //IP ADDRESS
        patterns.add(Pattern.compile("^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])[.,]){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])"));
    }

    public void userChatMessage(User user, String message) {
        if (user.timeChecker(0, 1500)) {
            user.sendMessage(MessageType.ERROR, "Espere " + user.timeNeed(0, 1500) + " milissegundos!");
        } else if (user.flags().disableLocalChat()) {
            user.sendMessage(MessageType.ERROR, "Chat local desabilitado! Ative com /preferencias.");
        } else if (user.identity().isMuted()) {
            user.sendMessage(MessageType.ERROR, "Você está mutado por " + user.identity().getMuteTime() + "!");
        } else {

            message = format(user, clear(message), "L");

            int quantity = 0;

            synchronized (userService.getEntities()) {
                for (User target : userService.getEntities()) {
                    double distance = target.distanceSquared(user);
                    if (distance == -1 || distance > this.distance || user == target || target.flags().disableLocalChat()) continue;
                    if (!target.sendMessage(message)) continue;
                    if (!target.isHidden()) quantity++;
                }
            }

            user.sendMessage(message);
            Bukkit.getLogger().info(StringUtils.removeColors(message));
            user.sendAction("§aMensagem enviada para " + (quantity == 0 ? "ninguém" : quantity == 1 ? "1 jogador" : quantity + " jogadores!"));
        }
    }

    public void userGlobalMessage(User user, String message) {
        if (user.timeChecker(1, 4000)) {
            user.sendMessage(MessageType.ERROR, "Espere " + user.timeNeed(1, 4000) + " milissegundos!");
        } else if (user.flags().disableGlobalChat()) {
            user.sendMessage(MessageType.ERROR, "Chat global desabilitado! Ative com /preferencias.");
        } else if (user.identity().isMuted()) {
            user.sendMessage(MessageType.ERROR, "Você está mutado por " + user.identity().getMuteTime() + "!");
        } else {

            message = format(user, clear(message), "G");

            int quantity = 0;

            synchronized (userService.getEntities()) {
                for (User target : userService.getEntities()) {
                    if (user == target || target.flags().disableGlobalChat()) continue;
                    if (!target.sendMessage(message)) continue;
                    if (!target.isHidden()) quantity++;
                }
            }

            user.sendMessage(message);
            Bukkit.getLogger().info(StringUtils.removeColors(message));
            user.sendAction("§aMensagem enviada para " + (quantity == 0 ? "ninguém" : quantity == 1 ? "1 jogador!" : quantity + " jogadores!"));
        }
    }

    public void userClanMessage(User user, String message, Clan clan) {
        if (user.timeChecker(2, 1000)) {
            user.sendMessage(MessageType.ERROR, "Espere " + user.timeNeed(2, 1000) + " milissegundos!");
        } else if (user.flags().disableClanChat()) {
            user.sendMessage(MessageType.ERROR, "Chat do clan desabilitado! Ative com /preferencias.");
        } else if (user.identity().isMuted()) {
            user.sendMessage(MessageType.ERROR, "Você está mutado por " + user.identity().getMuteTime() + "!");
        } else {
            int quantity = 0;

            message = "§e[" + clan.getColoredTag() + "§e] §f" + user.identity().getName() + "§e: " + clear(message);

            for (ClanUser member : clan.getMembers()) {
                Identity target = member.getIdentity();

                if (!target.isOnline() || user.identity() == target || target.user().flags().disableClanChat()) continue;
                if (target.user().sendMessage(message)) quantity++;
            }

            user.sendMessage(message);
            Bukkit.getLogger().info(StringUtils.removeColors(message));
            user.sendAction("§aMensagem enviada para " + (quantity == 0 ? "ninguém" : quantity == 1 ? "1 jogador!" : quantity + " jogadores!"));
        }
    }

    public void userTellMessage(User user, User target, String message) {
        if (user.timeChecker(3, 2000)) {
            user.sendMessage(MessageType.ERROR, "Espere " + user.timeNeed(3, 2000) + " milissegundos!");
        } else if (user.flags().disableTell()) {
            user.sendMessage(MessageType.ERROR, "Tell desabilitado! Ative com /preferencias.");
        } else if (target.flags().disableTell()) {
            user.sendMessage(MessageType.ERROR, "O jogador desabilitou o tell!");
        } else if (user.identity().isMuted()) {
            user.sendMessage(MessageType.ERROR, "Você está mutado por " + user.identity().getMuteTime() + "!");
        } else {
            message = clear(message);

            Bukkit.getLogger().info("[MSG] " + user.identity().getName() + "-> " + target.identity().getName() + ": " + StringUtils.removeColors(message));

            if (target.sendMessage("§e[MSG]§r de " + user.identity().getName() + "§7: " + message)) {
                user.sendMessage("§e[MSG]§r para " + target.identity().getName() + "§7: " + message);
            } else {
                user.sendMessage(MessageType.ERROR, "Mensagem não enviada (Jogador fazendo login?)");
            }
        }
    }

    public void userAnnounceMessage(User user, String message) {
        if (user.timeChecker(5, 60000)) {
            user.sendMessage(MessageType.ERROR, "Espere " + user.timeNeed(5, 60000) + " milissegundos!");
        } else if (user.identity().isMuted()) {
            user.sendMessage(MessageType.ERROR, "Você está mutado por " + user.identity().getMuteTime() + "!");
        } else {

            StringBuilder builder = new StringBuilder();

            builder.append("§d========= ANÚNCIO [§e").append(user.identity().getName()).append("§d] =========\n");

            int width = (FontWidthCalculator.getStringWidth(builder.toString()));

            int eq = FontWidthCalculator.getCharWidth('=');

            builder.append(clear(message)).append("\n§d");

            for (int i = 0; i < width; i += eq) builder.append('=');

            message = builder.toString();

            int quantity = 0;

            synchronized (userService.getEntities()) {
                for (User target : userService.getEntities()) {
                    if (user == target || target.flags().disableGlobalChat()) continue;
                    if (!target.sendMessage(message)) continue;
                    if (!target.isHidden()) quantity++;
                }
            }

            user.sendMessage(message);
            Bukkit.getLogger().info(StringUtils.removeColors(message));
            user.sendAction("§aAnúncio enviado para " + (quantity == 0 ? "ninguém" : quantity == 1 ? "1 jogador!" : quantity + " jogadores!"));
        }
    }

    private String clear(String message) {

        for (Pattern pattern : patterns) {
            message = pattern.matcher(message).replaceAll("***");
        }

        return message;
    }

    private String format(User user, String message, String channel) {

        builder.addValueBorder(channel)
                .addValue(user.group().get().getTag())
                .addValue(user.rank().getTag());

        if (user.clan().isIn()) builder.addValue(user.clan().get().getColoredTag());

        builder.addValue(user.identity().getName());

        return builder.addMessageAndBuild(user.hasPermission("simplecraft.chat.color") ? StringUtils.toStringWithColors(message) : message, channel.equals("L"));

    }
}
