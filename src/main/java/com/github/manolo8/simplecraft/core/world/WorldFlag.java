package com.github.manolo8.simplecraft.core.world;

import com.github.manolo8.simplecraft.utils.def.Flag;

import java.util.ArrayList;
import java.util.List;

public class WorldFlag extends Flag {

    public static List<Toggle> togglers;

    static {
        togglers = new ArrayList<>();
        togglers.add(new Toggle("region", "Usar o sistema de region", 0));
//        togglers.add(new Toggle("plot", "Usar o sistema de plot", 1)); -> não é editável!
        togglers.add(new Toggle("minas", "Usar o sistema de minas", 2));
        togglers.add(new Toggle("portal", "Usar o sistema de portal", 3));
        togglers.add(new Toggle("mobarea", "Usar o sistema de mobarea", 4));
        togglers.add(new Toggle("clanarea", "Usar o sistema de clanarea", 5));
        togglers.add(new Toggle("hologram", "Usar o sistema de hologramas", 6));
    }

    public WorldFlag(byte[] data) {
        super(data);
    }

    @Override
    public List<Toggle> getTogglers() {
        return new ArrayList<>();
    }
}
