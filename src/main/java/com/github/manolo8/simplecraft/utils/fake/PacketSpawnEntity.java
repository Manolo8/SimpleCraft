package com.github.manolo8.simplecraft.utils.fake;

import com.github.manolo8.simplecraft.utils.def.PacketAccessor;
import net.minecraft.server.v1_13_R2.PacketPlayOutSpawnEntityLiving;

import java.lang.reflect.Field;

public class PacketSpawnEntity extends PacketAccessor<PacketPlayOutSpawnEntityLiving> {

    public static PacketSpawnEntity instance = new PacketSpawnEntity();

    private Field id;
    private Field uuid;
    private Field type;
    private Field x;
    private Field y;
    private Field z;
    private Field motX;
    private Field motY;
    private Field motZ;
    private Field yaw;
    private Field pitch;
    private Field ap;
    private Field datawatcher;
    private Field items;

    public PacketSpawnEntity() {
        super(PacketPlayOutSpawnEntityLiving.class);

        id = getField("a");
        uuid = getField("b");
        type = getField("c");
        x = getField("d");
        y = getField("e");
        z = getField("f");
        motX = getField("g");
        motY = getField("h");
        motZ = getField("i");
        yaw = getField("j");
        pitch = getField("k");
        ap = getField("l");
        datawatcher = getField("m");
        items = getField("n");
    }

    public PacketSpawnEntityBuilder create(FakeLivingEntity entity) {
        return new PacketSpawnEntityBuilder(entity);
    }

    public class PacketSpawnEntityBuilder extends PacketAccessor.PacketBuilder {

        public PacketSpawnEntityBuilder(FakeLivingEntity entity) {
            try {
                id.set(packet, entity.id);
                uuid.set(packet, entity.uuid);
                type.set(packet, entity.type);
                x.set(packet, entity.x);
                y.set(packet, entity.y);
                z.set(packet, entity.z);
                yaw.set(packet, (byte) (entity.yaw * 256.0F / 360.0F));
                pitch.set(packet, (byte) (entity.pitch * 256.0F / 360.0F));
                datawatcher.set(packet, entity.datawatcher);
            } catch (Exception ignored) {
            }
        }
    }
}
