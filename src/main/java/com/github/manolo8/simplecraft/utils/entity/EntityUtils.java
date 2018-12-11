package com.github.manolo8.simplecraft.utils.entity;

import com.github.manolo8.simplecraft.core.world.WorldInfo;
import com.github.manolo8.simplecraft.utils.def.Matcher;
import org.bukkit.Chunk;
import org.bukkit.entity.Entity;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Set;

public class EntityUtils {

    private static Field idField = null;

    public static int removeEntities(WorldInfo info, Matcher<Entity> matcher) {

        Set<Chunk> chunks = info.getChunks();

        int counter = 0;

        Iterator<Chunk> iterator = chunks.iterator();

        while (iterator.hasNext()) {
            Chunk chunk = iterator.next();

            for (Entity entity : chunk.getEntities()) {
                if (matcher.match(entity)) {
                    entity.remove();
                    counter++;
                }
            }
        }

        return counter;
    }

    public static int nextEntityId() {
        try {

            if (idField == null) {
                idField = net.minecraft.server.v1_13_R2.Entity.class.getDeclaredField("entityCount");
                idField.setAccessible(true);
            }

            int id = (int) idField.get(null);

            idField.set(null, id + 1);

            return id;

        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        }
    }
}
