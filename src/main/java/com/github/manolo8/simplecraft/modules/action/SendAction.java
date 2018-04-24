package com.github.manolo8.simplecraft.modules.action;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public abstract class SendAction {

    protected String pkg;

    public SendAction() {
        String[] pkgs = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",");
        if (pkgs.length > 3) pkg = pkgs[3];
        else pkg = "UNKNOWN";
    }

    public abstract boolean support(String version);

    public abstract void sendAction(Player player, String string) throws Exception;

    Class<?> getNmsClass(String clazz)
            throws ClassNotFoundException {
        return Class.forName("net.minecraft.server." + this.pkg + "." + clazz);
    }
}
