package com.github.manolo8.simplecraft.core.world.container;

import com.github.manolo8.simplecraft.core.world.WorldInfo;
import com.github.manolo8.simplecraft.core.world.container.ProxyContainer.CustomProtectionProxy;
import com.github.manolo8.simplecraft.core.data.model.named.NamedEntity;
import com.github.manolo8.simplecraft.interfaces.*;
import com.github.manolo8.simplecraft.module.user.User;
import com.github.manolo8.simplecraft.utils.def.IncrementInteger;
import org.bukkit.Chunk;
import org.bukkit.Material;

import java.util.*;
import java.util.function.Consumer;

public class Container extends NamedEntity implements Teleportable {

    public final List<ProxyContainer> proxies;

    protected Area area;
    protected WorldInfo worldInfo;

    protected Set<User> users;
    protected HashMap<User, IncrementInteger> listening;

    protected List<Chunk> chunks;
    protected int visiblePlayers;

    public Container() {
        this.proxies = new ArrayList<>();
        this.chunks = new ArrayList<>();
        this.users = new HashSet<>();
        this.listening = new HashMap<>();
    }

    //======================================================
    //====================ENCAPSULATION=====================
    //======================================================
    public Area getArea() {
        return area;
    }

    public void setArea(Area area) {
        this.area = area;
    }

    public WorldInfo getWorldInfo() {
        return worldInfo;
    }

    public void setWorldInfo(WorldInfo worldInfo) {
        this.worldInfo = worldInfo;
    }

    public Set<User> getUsers() {
        return users;
    }

    public List<Chunk> getChunks() {
        return chunks;
    }
    //======================================================
    //===================_ENCAPSULATION=====================
    //======================================================


    //======================================================
    //========================PROXY=========================
    //======================================================
    void addInsideUser(User user) {

        if (!user.isHidden()) visiblePlayers++;

        users.add(user);

        if (this instanceof Enter) {
            ((Enter) this).onEnter(user);
        }

    }

    void removeInsideUser(User user) {
        if (!user.isHidden()) visiblePlayers--;

        users.remove(user);

        if (this instanceof Exit) {
            ((Exit) this).onExit(user);
        }
    }

    void addNearbyUser(User user) {

        IncrementInteger value = listening.get(user);

        if (value == null) {

            listening.put(user, new IncrementInteger(1));

            if (this instanceof Proximity) {
                ((Proximity) this).onNearby(user);
            }

        } else {
            value.increase();
        }

    }

    void removeNearbyUser(User user) {
        IncrementInteger value = listening.get(user);

        if (value != null) {
            value.decrease();


            if (value.get() == 0) {
                listening.remove(user);

                if (this instanceof Proximity) {
                    ((Proximity) this).onAway(user);
                }
            }

        }
    }

    ProxyContainer createProxy(ChunkContainer chunk) {
        ProxyContainer proxy = new ProxyContainer(this, chunk);

        proxies.add(proxy);

        return proxy;
    }

    void removeProxy(ProxyContainer container) {
        proxies.remove(container);
    }

    boolean isFirstProxy() {
        return proxies.size() == 1;
    }
    //======================================================
    //=======================_PROXY=========================
    //======================================================


    //======================================================
    //======================CONTAINER=======================
    //======================================================
    public void unAttachAndRemove() {
        for (ProxyContainer proxy : new ArrayList<>(this.proxies)) {
            proxy.unAttachAndRemove();
        }
    }

    public boolean isAttached() {
        return !proxies.isEmpty();
    }

    protected void unloaded() {

    }

    public void refreshDefaults() {

    }

    public int getTotalChunks() {
        return area.getTotalChunks();
    }

    public <C> C getByType(Class<C> type) {
        if (type.isAssignableFrom(getClass())) {
            return (C) this;
        } else {
            return null;
        }
    }

    /**
     * Run an operation for each user nearby from this container
     *
     * @param consumer
     */
    public void eachNearby(Consumer<User> consumer) {
        Iterator<User> i = listening.keySet().iterator();

        while (i.hasNext()) {
            consumer.accept(i.next());
        }

    }

    public void chunkUnload(Chunk chunk) {
        chunks.add(chunk);
    }

    public void chunkLoad(Chunk chunk) {
        chunks.remove(chunk);
    }

    //======================================================
    //======================_OVERRIDE=======================
    //======================================================
    @Override
    public boolean teleport(User user) {
        return false;
    }

    @Override
    public void remove() {

        if (isAttached()) {
            unAttachAndRemove();
        }

        super.remove();
    }
    //======================================================
    //======================_OVERRIDE=======================
    //======================================================


    //======================================================
    //=====================_CONTAINER=======================
    //======================================================

    public static class CustomProtection extends Container implements Protection {

        private Protection protection;

        @Override
        ProxyContainer createProxy(ChunkContainer chunk) {
            ProxyContainer proxy = new CustomProtectionProxy(this, chunk);

            proxies.add(proxy);

            return proxy;
        }

        void setupMainProtection(Protection protection) {
            this.protection = protection;
        }

        @Override
        public boolean canSpread(Material type) {
            return protection.canSpread(type);
        }

        @Override
        public boolean canPistonWork() {
            return protection.canPistonWork();
        }

        @Override
        public boolean canExplode() {
            return protection.canExplode();
        }

        @Override
        public boolean canEnter(User user) {
            return protection.canEnter(user);
        }

        @Override
        public boolean canExit(User user) {
            return protection.canExit(user);
        }

        @Override
        public boolean canBreak(User user, Material type) {
            return protection.canBreak(user, type);
        }

        @Override
        public boolean canPlace(User user, Material type) {
            return protection.canPlace(user, type);
        }

        @Override
        public boolean canInteract(User user, Material type) {
            return protection.canInteract(user, type);
        }

        @Override
        public boolean canRemoveSpecials(User user) {
            return protection.canRemoveSpecials(user);
        }

        @Override
        public boolean canUseSkill(int type) {
            return protection.canUseSkill(type);
        }

        @Override
        public boolean canFly() {
            return protection.canFly();
        }

        @Override
        public boolean isPvpOn() {
            return protection.isPvpOn();
        }

        @Override
        public boolean isPveOn(User user) {
            return protection.isPveOn(user);
        }
    }
}