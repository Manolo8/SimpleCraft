package com.github.manolo8.simplecraft.modules.action;

import org.bukkit.entity.Player;

public class NMS1_8 extends SendAction {

    @Override
    public boolean support(String version) {
        return version.equals("v1_8_r2")
                || version.equals("v1_8_r3");
    }

    @Override
    public void sendAction(Player player, String msg) throws Exception {
        Object icbc = getNmsClass("IChatBaseComponent$ChatSerializer").getMethod("a", new Class[]{String.class}).invoke(null, "{'text': '" + msg + "'}");
        Object ppoc = getNmsClass("PacketPlayOutChat").getConstructor(getNmsClass("IChatBaseComponent"), Byte.TYPE).newInstance(icbc, (byte) 2);
        Object nmsp = player.getClass().getMethod("getHandle", new Class[0]).invoke(player);
        Object pcon = nmsp.getClass().getField("playerConnection").get(nmsp);
        pcon.getClass().getMethod("sendPacket", new Class[]{getNmsClass("Packet")}).invoke(pcon, ppoc);
    }
}
