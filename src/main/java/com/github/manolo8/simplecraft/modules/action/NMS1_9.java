package com.github.manolo8.simplecraft.modules.action;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class NMS1_9 extends SendAction {

    @Override
    public boolean support(String version) {
        return version.equals("v1_9_r1")
                || version.equals("v1_9_r2")
                || version.equals("v1_10_r1")
                || version.equals("v1_11_r1");
    }

    @Override
    public void sendAction(Player player, String msg) throws Exception {
        Object icbc = getNmsClass("ChatComponentText").getConstructor(String.class).newInstance(ChatColor.translateAlternateColorCodes('&', msg));
        Object ppoc = getNmsClass("PacketPlayOutChat").getConstructor(getNmsClass("IChatBaseComponent"), Byte.TYPE).newInstance(icbc, (byte) 2);
        Object nmsp = player.getClass().getMethod("getHandle", new Class[0]).invoke(player);
        Object pcon = nmsp.getClass().getField("playerConnection").get(nmsp);
        pcon.getClass().getMethod("sendPacket", getNmsClass("Packet")).invoke(pcon, ppoc);
    }
}
