package com.github.manolo8.simplecraft.modules.action;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class NMS1_12 extends SendAction {

    @Override
    public boolean support(String version) {
        return version.equals("v1_12_r1")
                || version.equals("v1_12_r2");
    }

    @Override
    public void sendAction(Player player, String msg) throws Exception {
        Object icbc = getNmsClass("ChatComponentText").getConstructor(String.class).newInstance(ChatColor.translateAlternateColorCodes('&', msg));
        Object cmt = getNmsClass("ChatMessageType").getField("GAME_INFO").get(null);
        Object ppoc = getNmsClass("PacketPlayOutChat").getConstructor(getNmsClass("IChatBaseComponent"), getNmsClass("ChatMessageType")).newInstance(icbc, cmt);
        Object nmsp = player.getClass().getMethod("getHandle", new Class[0]).invoke(player);
        Object pcon = nmsp.getClass().getField("playerConnection").get(nmsp);
        pcon.getClass().getMethod("sendPacket", new Class[]{getNmsClass("Packet")}).invoke(pcon, ppoc);
    }
}
