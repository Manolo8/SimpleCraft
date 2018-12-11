package com.github.manolo8.simplecraft.module.board.packet;

import com.github.manolo8.simplecraft.utils.def.PacketAccessor;
import net.minecraft.server.v1_13_R2.IChatBaseComponent;
import net.minecraft.server.v1_13_R2.PacketPlayOutPlayerListHeaderFooter;

import java.lang.reflect.Field;

public class PacketPlayerListText extends PacketAccessor<PacketPlayOutPlayerListHeaderFooter> {

    private Field fieldHeader;
    private Field fieldFooter;

    public PacketPlayerListText() {
        super(PacketPlayOutPlayerListHeaderFooter.class);
        fieldHeader = getField("header");
        fieldFooter = getField("footer");
    }

    public PacketPlayerListTextBuilder create(String header, String footer) {
        return new PacketPlayerListTextBuilder(header, footer);
    }

    public class PacketPlayerListTextBuilder extends PacketAccessor.PacketBuilder {

        public PacketPlayerListTextBuilder(String header, String footer) {
            try {

                fieldHeader.set(packet, IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + header + "\"}"));
                fieldFooter.set(packet, IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + footer + "\"}"));

            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}
