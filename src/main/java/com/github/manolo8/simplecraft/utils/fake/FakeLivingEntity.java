package com.github.manolo8.simplecraft.utils.fake;

import net.minecraft.server.v1_13_R2.DataWatcherObject;
import net.minecraft.server.v1_13_R2.DataWatcherRegistry;

import static com.github.manolo8.simplecraft.utils.fake.FakeDataWatcher.registerKey;

public class FakeLivingEntity extends FakeEntity {

    protected static final DataWatcherObject<Byte> flag2 = registerKey(FakeLivingEntity.class, DataWatcherRegistry.a); //aw
    protected static final DataWatcherObject<Float> health = registerKey(FakeLivingEntity.class, DataWatcherRegistry.c); //HEALTH
    protected static final DataWatcherObject<Integer> effects = registerKey(FakeLivingEntity.class, DataWatcherRegistry.b); //g
    protected static final DataWatcherObject<Boolean> hasEffects = registerKey(FakeLivingEntity.class, DataWatcherRegistry.i); //h
    protected static final DataWatcherObject<Integer> arrowCount = registerKey(FakeLivingEntity.class, DataWatcherRegistry.b); //bx

    public FakeLivingEntity(double x, double y, double z) {
        super(x, y, z);

        this.datawatcher.register(flag2, (byte) 0);
        this.datawatcher.register(effects, 0);
        this.datawatcher.register(hasEffects, false);
        this.datawatcher.register(arrowCount, 0);
        this.datawatcher.register(health, 1.0F);
    }


}
