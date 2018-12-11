package com.github.manolo8.simplecraft.utils.fake;

import net.minecraft.server.v1_13_R2.DataWatcherObject;
import net.minecraft.server.v1_13_R2.DataWatcherRegistry;
import net.minecraft.server.v1_13_R2.EntityTypes;
import net.minecraft.server.v1_13_R2.Vector3f;

import static com.github.manolo8.simplecraft.utils.fake.FakeDataWatcher.registerKey;

public class FakeArmorStand extends FakeLivingEntity {

    protected static final DataWatcherObject<Byte> flag3 = registerKey(FakeArmorStand.class, DataWatcherRegistry.a); //a
    protected static final DataWatcherObject<Vector3f> headPose = registerKey(FakeArmorStand.class, DataWatcherRegistry.k); //b
    protected static final DataWatcherObject<Vector3f> bodyPose = registerKey(FakeArmorStand.class, DataWatcherRegistry.k); //c
    protected static final DataWatcherObject<Vector3f> leftArmorPose = registerKey(FakeArmorStand.class, DataWatcherRegistry.k); //d
    protected static final DataWatcherObject<Vector3f> rightArmorPose = registerKey(FakeArmorStand.class, DataWatcherRegistry.k); //e
    protected static final DataWatcherObject<Vector3f> leftLegPose = registerKey(FakeArmorStand.class, DataWatcherRegistry.k); //f
    protected static final DataWatcherObject<Vector3f> rightLegPose = registerKey(FakeArmorStand.class, DataWatcherRegistry.k); //g

    public FakeArmorStand(double x, double y, double z) {
        super(x, y, z);

        this.type = 1;

        this.datawatcher.register(flag3, (byte) 0);
        this.datawatcher.register(headPose, new Vector3f(0.0F, 0.0F, 0.0F));
        this.datawatcher.register(bodyPose, new Vector3f(0.0F, 0.0F, 0.0F));
        this.datawatcher.register(leftArmorPose, new Vector3f(-10.0F, 0.0F, -10.0F));
        this.datawatcher.register(rightArmorPose, new Vector3f(-15.0F, 0.0F, 10.0F));
        this.datawatcher.register(leftLegPose, new Vector3f(-1.0F, 0.0F, -1.0F));
        this.datawatcher.register(rightLegPose, new Vector3f(1.0F, 0.0F, 1.0F));
    }

    public void setRightArmPose(Vector3f vector3f) {
        this.datawatcher.set(rightArmorPose, vector3f);
    }
}
