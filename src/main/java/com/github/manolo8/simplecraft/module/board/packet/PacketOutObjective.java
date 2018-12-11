package com.github.manolo8.simplecraft.module.board.packet;

import com.github.manolo8.simplecraft.utils.def.PacketAccessor;
import net.minecraft.server.v1_13_R2.ChatComponentText;
import net.minecraft.server.v1_13_R2.IScoreboardCriteria.EnumScoreboardHealthDisplay;
import net.minecraft.server.v1_13_R2.PacketPlayOutScoreboardObjective;

import java.lang.reflect.Field;

public class PacketOutObjective extends PacketAccessor<PacketPlayOutScoreboardObjective> {

    private Field fieldName;
    private Field fieldDisplayName;
    private Field fieldCriteria;
    private Field fieldType;

    public PacketOutObjective() {
        super(PacketPlayOutScoreboardObjective.class);
        fieldName = getField("a");
        fieldDisplayName = getField("b");
        fieldCriteria = getField("c");
        fieldType = getField("d");
    }

    public PacketObjectiveOutBuilder create(String objective, String displayName, EnumScoreboardHealthDisplay criteria, int type) {
        return new PacketObjectiveOutBuilder(objective, displayName, criteria, type);
    }

    public class PacketObjectiveOutBuilder extends PacketAccessor.PacketBuilder {

        public PacketObjectiveOutBuilder(String objective, String displayName, EnumScoreboardHealthDisplay criteria, int type) {
            try {

                fieldName.set(packet, objective);
                fieldDisplayName.set(packet, new ChatComponentText(displayName));
                fieldCriteria.set(packet, criteria);
                fieldType.set(packet, type);

            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}
