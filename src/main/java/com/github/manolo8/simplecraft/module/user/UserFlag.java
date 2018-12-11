package com.github.manolo8.simplecraft.module.user;

import com.github.manolo8.simplecraft.utils.def.Flag;

import java.util.ArrayList;
import java.util.List;

public class UserFlag extends Flag {

    public static final Toggle localChat = new Toggle("localchat", "Bloquear chat local", 0);
    public static final Toggle globalChat = new Toggle("globalchat", "Bloquear chat global", 1);
    public static final Toggle clanChat = new Toggle("globalchat", "Bloquear chat do clan", 2);
    public static final Toggle tell = new Toggle("tell", "Bloquear tell", 3);
    public static final Toggle shiftSell = new Toggle("shiftsell", "Ativar venda pelo SHIFT", 4);
    private static List<Toggle> togglers;

    static {
        togglers = new ArrayList<>();
        togglers.add(localChat);
        togglers.add(globalChat);
        togglers.add(clanChat);
        togglers.add(tell);
        togglers.add(shiftSell);
    }

    public UserFlag(byte[] data) {
        super(data);
    }

    public boolean disableLocalChat() {
        return has(0);
    }

    public boolean disableGlobalChat() {
        return has(1);
    }

    public boolean disableClanChat() {
        return has(2);
    }

    public boolean disableTell() {
        return has(3);
    }

    public boolean allowShiftSell() {
        return has(4);
    }

    @Override
    public List<Toggle> getTogglers() {
        return togglers;
    }
}
