package com.github.manolo8.simplecraft.module.board.packet;

import com.github.manolo8.simplecraft.utils.def.PacketAccessor;
import net.minecraft.server.v1_13_R2.PacketPlayOutScoreboardDisplayObjective;

import java.lang.reflect.Field;

public class PacketObjective extends PacketAccessor<PacketPlayOutScoreboardDisplayObjective> {

    private Field fieldSlot;
    private Field fieldName;

    public PacketObjective() {
        super(PacketPlayOutScoreboardDisplayObjective.class);

        fieldSlot = getField("a");
        fieldName = getField("b");
    }

    public PacketObjectiveBuilder create(int slot, String name) {
        return new PacketObjectiveBuilder(slot, name);
    }

    public class PacketObjectiveBuilder extends PacketAccessor.PacketBuilder {

        public PacketObjectiveBuilder(int slot, String name) {
            try {

                fieldSlot.set(packet, slot);
                fieldName.set(packet, name);

            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}
