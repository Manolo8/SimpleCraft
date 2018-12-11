package com.github.manolo8.simplecraft.module.clan;

import com.github.manolo8.simplecraft.utils.def.Flag;

import java.util.ArrayList;
import java.util.List;

public class ClanFlag extends Flag {


    public static final Toggle leader = new Toggle("leader", "É lider", 0);
    public static final Toggle staff = new Toggle("staff", "É staff", 1);
    private static List<Toggle> togglers;

    static {
        togglers = new ArrayList<>();
        togglers.add(leader);
        togglers.add(staff);
    }

    public ClanFlag(byte[] data) {
        super(data);
    }

    public boolean isLeader() {
        return has(0);
    }

    public boolean isStaff() {
        return has(1) || isLeader();
    }

    @Override
    public List<Toggle> getTogglers() {
        return togglers;
    }
}
