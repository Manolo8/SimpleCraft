package com.github.manolo8.simplecraft.module.plot.member;

import com.github.manolo8.simplecraft.utils.def.Flag;

import java.util.ArrayList;
import java.util.List;

public class MemberFlag extends Flag {

    private static List<Toggle> togglers;

    static {
        togglers = new ArrayList<>();
        togglers.add(new Toggle("build", "Pode construir", 0));
        togglers.add(new Toggle("break", "Pode quebrar blocos", 1));
        togglers.add(new Toggle("interact", "Pode interagir", 2));
        togglers.add(new Toggle("specials", "Pode tirar shop/máquinas/spawners", 3));
    }

    public MemberFlag(byte[] data) {
        super(data);
    }

    public static List<Toggle> getTogglersStatic() {
        return togglers;
    }

    public boolean canPlace() {
        return has(0);
    }

    public boolean canBreak() {
        return has(1);
    }

    public boolean canInteract() {
        return has(2);
    }

    public boolean canSpecials() {
        return has(3);
    }

    @Override
    public List<Toggle> getTogglers() {
        return togglers;
    }
}
