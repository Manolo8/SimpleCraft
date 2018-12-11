package com.github.manolo8.simplecraft.utils.fake;

import com.github.manolo8.simplecraft.utils.def.StringUtils;
import net.minecraft.server.v1_13_R2.DataWatcher;
import net.minecraft.server.v1_13_R2.DataWatcherObject;
import net.minecraft.server.v1_13_R2.DataWatcherRegistry;
import net.minecraft.server.v1_13_R2.IChatBaseComponent;

import java.util.Optional;
import java.util.UUID;

import static com.github.manolo8.simplecraft.utils.fake.FakeDataWatcher.registerKey;

public class FakeEntity {

    protected static final DataWatcherObject<Byte> flag = registerKey(FakeEntity.class, DataWatcherRegistry.a); //ac
    protected static final DataWatcherObject<Integer> airTicks = registerKey(FakeEntity.class, DataWatcherRegistry.b); //aD
    protected static final DataWatcherObject<Optional<IChatBaseComponent>> customName = registerKey(FakeEntity.class, DataWatcherRegistry.f); //aE
    protected static final DataWatcherObject<Boolean> customNameVisible = registerKey(FakeEntity.class, DataWatcherRegistry.i); //aF
    protected static final DataWatcherObject<Boolean> silent = registerKey(FakeEntity.class, DataWatcherRegistry.i); //aG
    protected static final DataWatcherObject<Boolean> noGravity = registerKey(FakeEntity.class, DataWatcherRegistry.i); //aH

    public static int fakeID = Integer.MAX_VALUE - 500;
    public final int id;
    public final UUID uuid;
    public double x;
    public double y;
    public double z;
    public DataWatcher datawatcher;
    public int type;
    public float yaw;
    public float pitch;

    private String lastName;

    public FakeEntity(double x, double y, double z) {

        this.id = --fakeID;
        this.uuid = UUID.randomUUID();
        this.x = x;
        this.y = y;
        this.z = z;
        this.lastName = "";

        this.datawatcher = new FakeDataWatcher(this);

        this.datawatcher.register(flag, (byte) 0);
        this.datawatcher.register(airTicks, 300);
        this.datawatcher.register(customNameVisible, Boolean.FALSE);
        this.datawatcher.register(customName, Optional.empty());
        this.datawatcher.register(silent, false);
        this.datawatcher.register(noGravity, false);
    }

    public void setCustomName(String name) {
        // CraftBukkit start - Add a sane limit for name length
        if (name.length() > 256) {
            name = name.substring(0, 256);
        }
        lastName = name;
        // CraftBukkit end
        this.datawatcher.set(customName, Optional.of(StringUtils.serialize(name)));
    }


    public String getName() {
        return lastName;
    }

    public void setCustomNameVisible(boolean visible) {
        this.datawatcher.set(customNameVisible, visible);
    }

    public void setInvisible(boolean value) {
        this.setFlag(5, value);
    }

    public void setNoGravity(boolean value) {
        this.datawatcher.set(noGravity, value);
    }

    private void setFlag(int i, boolean value) {
        byte flag = this.datawatcher.get(FakeArmorStand.flag);

        if (value) {
            this.datawatcher.set(FakeArmorStand.flag, (byte) (flag | 1 << i));
        } else {
            this.datawatcher.set(FakeArmorStand.flag, (byte) (flag & ~(1 << i)));
        }
    }
}
