package com.github.manolo8.simplecraft.utils.fake;

import com.google.common.collect.Maps;
import net.minecraft.server.v1_13_R2.DataWatcher;
import net.minecraft.server.v1_13_R2.DataWatcherObject;
import net.minecraft.server.v1_13_R2.DataWatcherSerializer;
import org.apache.commons.lang3.ObjectUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

public class FakeDataWatcher extends DataWatcher {

    private static final Map<Class, Integer> keyCounter = Maps.newHashMap();
    private static Field changed;
    private static Method value;
    private FakeEntity entity;

    public FakeDataWatcher(FakeEntity entity) {
        super(null);

        this.entity = entity;
    }

    public static <T> DataWatcherObject<T> registerKey(Class<? extends FakeEntity> oclass, DataWatcherSerializer<T> datawatcherserializer) {
        int i;

        if (keyCounter.containsKey(oclass)) {
            i = keyCounter.get(oclass) + 1;
        } else {
            i = 0;

            //Ignore for now, unless make object oriented

            Class oclass2 = oclass;
            while (oclass2 != FakeEntity.class) {
                oclass2 = oclass2.getSuperclass();
                if (keyCounter.containsKey(oclass2)) {
                    i = keyCounter.get(oclass2) + 1;
                    break;
                }
            }

        }

        if (i > 254) {
            throw new IllegalArgumentException("Data value id is too big with " + i + "! (Max is " + 254 + ")");
        } else {
            keyCounter.put(oclass, Integer.valueOf(i));
            return datawatcherserializer.a(i);
        }
    }

    @Override
    public <T> void set(DataWatcherObject<T> datawatcherobject, T t0) {
        DataWatcher.Item datawatcher_item = getValue(datawatcherobject);
        if (ObjectUtils.notEqual(t0, datawatcher_item.b())) {
            datawatcher_item.a(t0);
            datawatcher_item.a(true);
            setChanged(true);
        }
    }

    private <T> DataWatcher.Item<T> getValue(DataWatcherObject<T> key) {
        try {
            if (value == null) {

                value = DataWatcher.class.getDeclaredMethod("b", DataWatcherObject.class);
                value.setAccessible(true);
            }

            return (Item<T>) value.invoke(this, key);

        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();

            return null;
        }
    }

    private void setChanged(boolean value) {
        try {
            if (changed == null) {

                changed = DataWatcher.class.getDeclaredField("g");
                changed.setAccessible(true);
            }

            changed.set(this, value);

        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
