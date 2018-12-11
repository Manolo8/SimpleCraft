package com.github.manolo8.simplecraft.utils.reflection;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ReflectionUtils {

    public static <O> O createConstructor(Class<O> clazz, Object object) {

        Constructor[] constructors = clazz.getDeclaredConstructors();

        try {
            Constructor constructor = constructors[0];

            constructor.setAccessible(true);
            if (constructor.getParameterTypes().length == 0) {
                return (O) constructor.newInstance();
            } else if (constructor.getParameterTypes()[0] == object.getClass()) {
                return (O) constructor.newInstance(object);
            } else {
                return (O) constructor.newInstance(search(constructor.getParameterTypes(), object));
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new Error("Error constructing " + clazz.getSimpleName() + "!");
        }
    }

    private static Object[] search(Class[] targets, Object provider) throws IllegalAccessException {
        Object[] found = new Object[targets.length];

        for (int i = 0; i < targets.length; i++) {
            found[i] = search(targets[i], provider);
        }

        return found;
    }

    private static Object search(Class target, Object provider) throws IllegalAccessException {
        Field[] fields = provider.getClass().getDeclaredFields();

        for (Field field : fields) {

            field.setAccessible(true);

            Object object = field.get(provider);

            if (object != null && object.getClass() == target) {
                return object;
            }

        }

        for (Field field : fields) {
            Object object = field.get(provider);

            if (object != null) {

                Object found = search(target, object);

                if (found != null) {
                    return found;
                }
            }

        }

        return null;
    }
}
