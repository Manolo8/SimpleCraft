package com.github.manolo8.simplecraft.module.crate;

import com.github.manolo8.simplecraft.SimpleCraft;
import com.github.manolo8.simplecraft.core.world.WorldService;
import com.github.manolo8.simplecraft.core.world.container.Area;
import com.github.manolo8.simplecraft.core.world.container.Container;
import com.github.manolo8.simplecraft.interfaces.Proximity;
import com.github.manolo8.simplecraft.interfaces.Tickable;
import com.github.manolo8.simplecraft.module.user.User;
import com.github.manolo8.simplecraft.utils.fake.FakeArmorStand;
import com.github.manolo8.simplecraft.utils.fake.FakeEntity;
import com.github.manolo8.simplecraft.utils.fake.PacketSpawnEntity;
import net.minecraft.server.v1_13_R2.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.Random;

public class Crate extends Container implements Proximity, Tickable {

    private static Random random = new Random();
    private FakeArmorStand[] items;
    private ItemStack[] equipments;
    private double x;
    private double y;
    private double z;
    private double radius;
    private int size;
    private double move;
    private double base;
    private double speed;
    private int expiration;

    public Crate(User user) {

        this.worldInfo = user.worldInfo();

        Location location = user.base().getLocation();

        this.size = 25;
        this.speed = 0.2;
        this.expiration = 200;

        this.x = location.getX();
        this.y = location.getY();
        this.z = location.getZ();
        this.base = (2 * Math.PI) / size;

        this.radius = size / Math.PI;

        this.area = new Area(location.getBlockX(),
                location.getBlockY(),
                location.getBlockZ(),
                location.getBlockX(),
                location.getBlockY(),
                location.getBlockZ()
        );
    }

    @Override
    public void refreshDefaults() {
        items = new FakeArmorStand[size + 1];
        equipments = new ItemStack[size + 1];

        for (int i = 0; i < items.length; i++) {
            FakeArmorStand item = new FakeArmorStand(0, 0, 0);

            item.setInvisible(true);
            item.setNoGravity(true);

            item.setRightArmPose(new Vector3f(270, 0, 0));

            ItemStack eq;

            if (i == size) {
                calc(item, 0, 0);
                eq = new ItemStack(Items.ENDER_PEARL);
                item.y = y + 0.5;
            } else {
                calc(item, i, 0);
                eq = new ItemStack(Items.APPLE);
                item.y = y;
            }

            Packet create = PacketSpawnEntity.instance.create(item).packet();
            Packet equipment = new PacketPlayOutEntityEquipment(item.id, EnumItemSlot.MAINHAND, eq);

            eachNearby(user -> {
                user.sendPacket(create);
                user.sendPacket(equipment);
            });

            items[i] = item;
            equipments[i] = eq;
        }
    }

    /**
     * Create an first position
     *
     * @param entity
     * @param i      return true if is near
     */
    private void calc(FakeEntity entity, int i, double add) {
        double angle = (((i + 1) * base) + add) % (2 * Math.PI);

        entity.x = (radius * Math.cos(angle)) + x;
        entity.z = (radius * Math.sin(angle)) + z;

        double dx = x - entity.x;
        double dz = z - entity.z;

        float yaw = (float) Math.toDegrees(Math.atan2(dz, dx)) - 90;

        entity.yaw = yaw < 0 ? yaw + 360 : yaw;
    }

    /**
     * If an container was multiple chunks, that method can be
     * called multiple times
     *
     * @param user when an user is nearby of this container
     */
    @Override
    public void onNearby(User user) {
        for (int i = 0; i < items.length; i++) {
            user.sendPacket(PacketSpawnEntity.instance.create(items[i]));
            user.sendPacket(new PacketPlayOutEntityEquipment(items[i].id, EnumItemSlot.MAINHAND, equipments[i]));
        }
    }

    /**
     * If an container was multiple chunks, that method can be
     * called multiple times
     *
     * @param user when an user is away from this container
     */
    @Override
    public void onAway(User user) {
        for (FakeArmorStand item : items) {
            user.sendPacket(new PacketPlayOutEntityDestroy(item.id));
        }
    }

    @Override
    public void unAttachAndRemove() {
        eachNearby(this::onAway);
        super.unAttachAndRemove();
    }

    @Override
    public void tick() {

        move += speed;

        if (speed - 0.035 > 0) {
            speed -= 0.002;
        }

        for (int i = 0; i < size; i++) {

            FakeArmorStand item = items[i];

            if (size != 1 && i == size - 1 && random.nextInt(100) == 0) {
                --size;

                Packet remove = new PacketPlayOutEntityDestroy(item.id);

                eachNearby(user -> user.sendPacket(remove));

                this.base = (2 * Math.PI) / size;
                this.radius = size / Math.PI;
            }

            long oldX = MathHelper.d((item.x) * 4096.0D);
            long oldY = MathHelper.d((item.y) * 4096.0D);
            long oldZ = MathHelper.d((item.z) * 4096.0D);

            calc(item, i, move);

            long newX = MathHelper.d((item.x) * 4096.0D);
            long newY = MathHelper.d((item.y) * 4096.0D);
            long newZ = MathHelper.d((item.z) * 4096.0D);

            byte yaw = (byte) (item.yaw == 0 ? 0 : MathHelper.d(item.yaw * 256.0F / 360.0F));
            byte pitch = (byte) (item.pitch == 0 ? 0 : MathHelper.d(item.pitch * 256.0F / 360.0F));

            Packet move = new PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook(item.id, newX - oldX, newY - oldY, newZ - oldZ, yaw, pitch, false);

            eachNearby(user -> user.sendPacket(move));
        }


        if (WorldService.tick % 20 == 0 && --expiration == 0) {
            Bukkit.getScheduler().runTaskLater(SimpleCraft.instance, this::remove, 0);
        }

    }
}
