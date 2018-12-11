package com.github.manolo8.simplecraft.core.world;

import com.github.manolo8.simplecraft.utils.def.Flag;
import com.github.manolo8.simplecraft.utils.mc.ItemStackUtils;
import org.bukkit.Material;
import org.bukkit.WorldCreator;
import org.bukkit.inventory.ItemStack;

public abstract class WorldBuilder {

    private final int id;
    private final String name;
    private final ItemStack representation;
    private final byte[] flags;

    public WorldBuilder(int id, String name, Material representation, int... def) {
        this.id = id;
        this.name = name;
        this.representation = ItemStackUtils.create(representation, "§e" + name.toUpperCase());

        if (def.length != 0) {

            Flag flag = new Flag.Basic();

            for (int i : def) {
                flag.set(i, true);
            }

            flags = flag.get();
        } else {
            flags = new byte[0];
        }
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public byte[] flags() {
        return flags;
    }

    abstract WorldCreator create(String name);
}
