package com.github.manolo8.simplecraft.utils.def;

import com.github.manolo8.simplecraft.module.user.User;
import com.github.manolo8.simplecraft.module.user.UserService;
import net.minecraft.server.v1_13_R2.Packet;

import java.lang.reflect.Field;

public class PacketAccessor<E extends Packet> {

    private final Class<E> clazz;

    public PacketAccessor(Class<E> clazz) {
        this.clazz = clazz;
    }

    protected Field getField(String name) {
        try {
            Field field = null;
            field = clazz.getDeclaredField(name);
            field.setAccessible(true);
            return field;
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            return null;
        }
    }

    public class PacketBuilder {

        protected final E packet;

        public PacketBuilder() {
            try {
                packet = clazz.newInstance();
            } catch (Exception e) {
                throw new Error("Can't create packet " + clazz.getName());
            }
        }

        public void send(User user) {
            user.sendPacket(packet);
        }

        public void sendAll() {
            UserService.eachExecuteStatic(this::send);
        }

        public Packet packet() {
            return packet;
        }
    }
}
