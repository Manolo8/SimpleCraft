package com.github.manolo8.simplecraft.core.placeholder;

import com.github.manolo8.simplecraft.core.placeholder.annotation.PlaceHolderMapping;
import com.github.manolo8.simplecraft.core.placeholder.tools.HolderString;
import com.github.manolo8.simplecraft.core.service.Service;
import com.github.manolo8.simplecraft.utils.reflection.ReflectionUtils;

import java.util.*;

public class PlaceHolderService extends Service {

    public static PlaceHolderService instance;

    private final HashMap<Class, TreeMap<String, Object>> placeHolders;

    public PlaceHolderService() {

        placeHolders = new HashMap<>();

        instance = this;

    }

    public void register(Object object) {
        for (Class<?> clazz : object.getClass().getDeclaredClasses()) {

            PlaceHolderMapping mapping = clazz.getAnnotation(PlaceHolderMapping.class);

            if (mapping != null) {
                register0(mapping.value(), ReflectionUtils.createConstructor(clazz, object));
            }

        }
    }

    private void register0(String key, Object placeHolder) {

        if (placeHolder instanceof PlaceHolder) {
            createOrAdd(PlaceHolder.class, key, placeHolder);
        } else if (placeHolder instanceof PlaceHolderBuilder) {
            createOrAdd(((PlaceHolderBuilder) placeHolder).getTargetClass(), key, placeHolder);
        } else {
            throw new Error(placeHolder.getClass().getSimpleName() + " not is an placeholder!");
        }

    }

    private void createOrAdd(Class clazz, String key, Object holder) {
        placeHolders.computeIfAbsent(clazz, k -> new TreeMap()).put(key, holder);
    }

    public PlaceHolder get(String key, Object target) {
        TreeMap map;

        if (target != null) {
            map = placeHolders.get(target.getClass());

            PlaceHolderBuilder builder = (PlaceHolderBuilder) map.get(key);

            if (builder != null) {
                return builder.build(target);
            }

        }

        map = placeHolders.get(PlaceHolder.class);

        if (map != null) {
            return (PlaceHolder) map.get(key);
        }

        return null;
    }

    public List<String> complete(String value, Class target) {
        List<String> complete = new ArrayList<>();

        TreeMap map;

        if (target != null) {
            map = placeHolders.get(target);

            if (map != null) {
                complete.addAll(map.subMap(value, value + Character.MAX_VALUE).keySet());
            }
        }


        map = placeHolders.get(PlaceHolder.class);

        if (map != null) {
            complete.addAll(map.subMap(value, value + Character.MAX_VALUE).keySet());
        }

        return complete;
    }
}
