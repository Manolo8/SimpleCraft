package com.github.manolo8.simplecraft.module.tag.packet;

import com.github.manolo8.simplecraft.utils.def.PacketAccessor;
import net.minecraft.server.v1_13_R2.ChatComponentText;
import net.minecraft.server.v1_13_R2.PacketPlayOutScoreboardTeam;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;

public class PacketTag extends PacketAccessor<PacketPlayOutScoreboardTeam> {

    private Field fieldMembers;
    private Field fieldPrefix;
    private Field fieldSuffix;
    private Field fieldTeamName;
    private Field fieldParamInt;
    private Field fieldPackOption;
    private Field fieldDisplayName;
    private Field fieldVisibility;

    public PacketTag() {
        super(PacketPlayOutScoreboardTeam.class);
        fieldMembers = getField("h");
        fieldPrefix = getField("c");
        fieldSuffix = getField("d");
        fieldTeamName = getField("a");
        fieldParamInt = getField("i");
        fieldPackOption = getField("j");
        fieldDisplayName = getField("b");
        fieldVisibility = getField("e");
    }

    public PacketTagBuilder create(String name, int param, List<String> members) {
        return new PacketTagBuilder(name, param, members);
    }

    public PacketTagBuilder create(String name, String prefix, String suffix, int param, List<String> members) {
        return new PacketTagBuilder(name, prefix, suffix, param, members);
    }

    public class PacketTagBuilder extends PacketAccessor.PacketBuilder {

        public PacketTagBuilder(String name, int param, List<String> members) {
            setDefaults(name, param);
            setMembers(members);
        }

        public PacketTagBuilder(String name, String prefix, String suffix, int param, List<String> members) {
            setDefaults(name, param);
            if (param == 0 || param == 2) {
                try {

                    fieldDisplayName.set(packet, new ChatComponentText(name));
                    fieldPrefix.set(packet, new ChatComponentText(prefix));
                    fieldSuffix.set(packet, new ChatComponentText(suffix));
                    fieldPackOption.set(packet, 1);
                    fieldVisibility.set(packet, "always");

                    if (param == 0) {
                        setMembers(members);
                    }

                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        private void setDefaults(String name, int param) {
            try {

                fieldTeamName.set(packet, name);
                fieldParamInt.set(packet, param);

            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        private void setMembers(List<String> list) {
            try {

                ((Collection) fieldMembers.get(packet)).addAll(list);

            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

}
