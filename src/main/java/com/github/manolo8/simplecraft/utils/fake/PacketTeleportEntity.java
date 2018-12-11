package com.github.manolo8.simplecraft.utils.fake;

import com.github.manolo8.simplecraft.utils.def.PacketAccessor;
import net.minecraft.server.v1_13_R2.PacketPlayOutEntityTeleport;

import java.lang.reflect.Field;

public class PacketTeleportEntity extends PacketAccessor<PacketPlayOutEntityTeleport> {

    public static PacketTeleportEntity instance = new PacketTeleportEntity();

    private Field id;
    private Field x;
    private Field y;
    private Field z;
    private Field yaw;
    private Field pitch;
    private Field ground;

    public PacketTeleportEntity() {
        super(PacketPlayOutEntityTeleport.class);

        id = getField("a");
        x = getField("b");
        y = getField("c");
        z = getField("d");
        yaw = getField("e");
        pitch = getField("f");
        ground = getField("g");
    }

    public PacketTeleportEntityBuilder create(FakeLivingEntity entity) {
        return new PacketTeleportEntityBuilder(entity);
    }

    public class PacketTeleportEntityBuilder extends PacketBuilder {

        public PacketTeleportEntityBuilder(FakeLivingEntity entity) {
            try {
                id.set(packet, entity.id);
                x.set(packet, entity.x);
                y.set(packet, entity.y);
                z.set(packet, entity.z);
                yaw.set(packet, (byte) (entity.yaw * 256.0F / 360.0F));
                pitch.set(packet, (byte) (entity.pitch * 256.0F / 360.0F));
                ground.set(packet, false);
            } catch (Exception ignored) {
            }
        }
    }
}
