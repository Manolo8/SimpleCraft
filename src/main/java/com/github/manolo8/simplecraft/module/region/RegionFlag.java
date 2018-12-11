package com.github.manolo8.simplecraft.module.region;

import com.github.manolo8.simplecraft.utils.def.Flag;

import java.util.ArrayList;
import java.util.List;

public class RegionFlag extends Flag {

    public static List<Toggle> togglers;

    static {
        togglers = new ArrayList<>();
        togglers.add(new Toggle("place", "Pode construir", 0));
        togglers.add(new Toggle("break", "Pode quebrar", 1));
        togglers.add(new Toggle("pvp", "Pode lutar", 2));
        togglers.add(new Toggle("pvpanimal", "Pode matar animais", 3));
        togglers.add(new Toggle("interact", "Pode interagir", 4));
        togglers.add(new Toggle("piston", "Pistões funcionam", 5));
        togglers.add(new Toggle("firespread", "Fogo se espalha (lava também)", 6));
        togglers.add(new Toggle("waterspread", "Água espalha", 7));
        togglers.add(new Toggle("explode", "Explosão", 8));
        togglers.add(new Toggle("skills", "Pode usar skills", 9));
        togglers.add(new Toggle("fly", "Pode voar (para vips)", 10));
    }

    public RegionFlag(byte[] data) {
        super(data);
    }

    public boolean canPlace() {
        return has(0);
    }

    public boolean canBreak() {
        return has(1);
    }

    public boolean canPvp() {
        return has(2);
    }

    public boolean canPvpAnimal() {
        return has(3);
    }

    public boolean canInteract() {
        return has(4);
    }

    public boolean canPistonWork() {
        return has(5);
    }

    public boolean canFireSpread() {
        return has(6);
    }

    public boolean canWaterSpread() {
        return has(7);
    }

    public boolean canExplode() {
        return has(8);
    }

    public boolean canUseSkills() {
        return has(9);
    }

    public boolean clanFly() {
        return has(10);
    }

    @Override
    public List<Toggle> getTogglers() {
        return togglers;
    }
}
