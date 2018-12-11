package com.github.manolo8.simplecraft.core.data.cache;

import com.github.manolo8.simplecraft.core.commands.line.ParameterBuilder;
import com.github.manolo8.simplecraft.core.commands.line.Result;
import com.github.manolo8.simplecraft.core.commands.line.Sender;
import com.github.manolo8.simplecraft.core.commands.line.TabArguments;
import com.github.manolo8.simplecraft.core.commands.line.annotation.CmdDescription;
import com.github.manolo8.simplecraft.core.commands.line.annotation.CmdMapping;
import com.github.manolo8.simplecraft.core.commands.line.annotation.CmdPermission;
import com.github.manolo8.simplecraft.core.commands.line.annotation.SupplierOptions;
import com.github.manolo8.simplecraft.core.commands.line.inf.Supplier;
import com.github.manolo8.simplecraft.core.data.model.base.BaseEntity;
import com.github.manolo8.simplecraft.core.service.Service;
import com.github.manolo8.simplecraft.module.user.MessageType;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

@SuppressWarnings("unused")
public class CacheService extends Service implements Runnable {

    private static CacheService instance;
    private final List<Cache> cacheList;
    private final Queue<BaseEntity> queue;

    public CacheService() {
        cacheList = new LinkedList<>();
        queue = new LinkedList<>();

        instance = this;
    }

    public static void modified(BaseEntity entity) {
        instance.modified0(entity);
    }

    public static void register(Cache cache) {
        instance.cacheList.add(cache);
    }

    public void add(Cache cache) {
        this.cacheList.add(cache);
    }

    public void stop() {
    }

    private void modified0(BaseEntity entity) {
        synchronized (queue) {
            if (!entity.isModified()) {
                queue.add(entity);
                entity.saving();
            }
        }
    }

    private void save(BaseEntity entity) {
        entity.cache().save(entity);
        entity.saved();
    }

    @Override
    public void run() {

        BaseEntity e;

        synchronized (queue) {
            e = queue.poll();
        }

        //Don't need to be synchronized, i think '-'
        if (e != null) {
            save(e);
        }

        synchronized (Cache.LOCKER) {
            for (Cache cache : cacheList) {
                cache.checkQueue();
            }
        }

    }

    //======================================================
    //=======================COMMANDS=======================
    //======================================================

//    @CmdMapping("cache <cache>")
//    @CmdDescription("Cache pelo nome")
//    @CmdPermission("simplecraft.admin")
//    public void cache(Sender sender, Cache<BaseEntity> cache) {
//        for (Reference<BaseEntity> reference : cache.references) {
//            BaseEntity entity = reference.get();
//            sender.sendMessage(" - " + (entity instanceof NamedEntity ? ((NamedEntity) entity).getName() : entity.getClass().getSimpleName()));
//        }
//    }

    @CmdMapping("cache gc")
    @CmdDescription("Invoca o gargabage collector")
    @CmdPermission("simplecraft.admin")
    public void cacheGc(Sender sender) {
        System.gc();
        sender.sendMessage(MessageType.SUCCESS, "Coletor de lixo invocado");
    }

    @SupplierOptions("cache")
    public class CacheConverter implements Supplier.Convert<Cache> {

        @Override
        public void tabComplete(TabArguments arguments) {
            for (Cache cache : cacheList) {
                arguments.offer(cache.getClass().getSimpleName());
            }
        }

        @Override
        public Result<Cache> convert(ParameterBuilder builder, Sender sender, String value) {

            for (Cache cache : cacheList) {
                if (cache.getClass().getSimpleName().equalsIgnoreCase(value)) {
                    return new Result<>(cache);
                }
            }

            return new Result.Error("O cache '" + value + " não foi encontrado!");
        }
    }

    //======================================================
    //=======================COMMANDS=======================
    //======================================================

}
