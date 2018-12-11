package com.github.manolo8.simplecraft.module.plot;

import com.github.manolo8.simplecraft.utils.def.Flag;

import java.util.ArrayList;
import java.util.List;

public class PlotFlag extends Flag {

    public static List<Toggle> togglers;

    static {
        togglers = new ArrayList<>();
        togglers.add(new Toggle("pvp", "ativar pvp", 0));
        togglers.add(new Toggle("entrar", "não-membros podem entrar no plot", 1));
    }

    public PlotFlag(byte[] data) {
        super(data);
    }

    public boolean isPvpOn() {
        return has(0);
    }

    public boolean canEntry() {
        return has(1);
    }

    @Override
    public List<Toggle> getTogglers() {
        return togglers;
    }
}
