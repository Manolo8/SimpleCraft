package com.github.manolo8.simplecraft.core.world.container;

import com.github.manolo8.simplecraft.core.world.WorldInfo;
import com.github.manolo8.simplecraft.interfaces.Protection;
import com.github.manolo8.simplecraft.interfaces.Tickable;
import com.github.manolo8.simplecraft.module.user.User;
import org.bukkit.Chunk;
import org.bukkit.Material;

import java.util.*;

import static com.github.manolo8.simplecraft.utils.location.CoordinatePair.pair;

public class WorldContainer extends AbstractContainer<WorldContainer, ChunkContainer> implements Protection {

    final WorldInfo worldInfo;
    final List<Tickable> tickables;
    private final HashMap<Long, ChunkContainer> fastContainers;
    private final Set<Chunk> chunks;

    public WorldContainer(WorldInfo worldInfo) {
        super(null);
        this.worldInfo = worldInfo;
        this.fastContainers = new HashMap<>();
        this.tickables = new LinkedList<>();
        this.chunks = new HashSet<>();
    }

    @Override
    public void refreshDefaults() {
        for (Chunk chunk : worldInfo.getWorld().getLoadedChunks())
            chunkLoad(chunk);
    }

    public Set<Chunk> getChunks() {
        return chunks;
    }

    public Set<ChunkContainer> getSubContainers() {
        return subContainers;
    }

    /**
     * @param x pos x
     * @param z pos z
     * @return an loaded ChunkContainer
     */
    public ChunkContainer getChunk(int x, int z) {
        return fastContainers.get(pair(x, z));
    }

    /**
     * @param x pos x
     * @param z pos z
     * @return ChunkContainer representing the chunk
     */
    public ChunkContainer getOrLoadChunk(int x, int z) {
        ChunkContainer chunk = getChunk(x, z);

        if (chunk == null) {
            chunk = new ChunkContainer(this, x, z);

            subContainers.add(chunk);
            fastContainers.put(pair(x, z), chunk);
            chunk.refreshDefaults();

            worldInfo.containerLoad(chunk);
        }

        return chunk;
    }

    /**
     * @param x pos posX
     * @param y pos y
     * @param z pos posZ
     * @return true case this points are inside this container
     */
    @Override
    public boolean inside(int x, int y, int z) {
        return true;
    }

    /**
     * @param area
     * @return true if the area is available
     */
    public boolean isAvailable(Area area, Container ignore) {

        //limit of 100 chunks
        if (area.getTotalChunks() > 128) {
            return false;
        }

        int maxCX = area.maxX >> 4;
        int maxCZ = area.maxZ >> 4;
        int minCX = area.minX >> 4;
        int minCZ = area.minZ >> 4;

        List<ChunkContainer> unload = null;
        boolean result = true;

        for (int x = minCX; x <= maxCX; x++) {
            for (int z = minCZ; z <= maxCZ; z++) {

                //Need to load if the chunk is not loaded, to test if is available
                ChunkContainer container = getChunk(x, z);

                if (container == null) {
                    container = getOrLoadChunk(x, z);

                    if (unload == null) unload = new ArrayList<>();

                    unload.add(container);
                }

                if (!container.isAvailable(area, ignore)) {
                    result = false;
                    break;
                }
            }
        }

        if (unload != null) {
            for (ChunkContainer container : unload) {
                container.unload();
            }
        }

        return result;
    }

    /**
     * @param container que será adicionado
     * @return true caso o container foi adicionado
     */
    public void addContainer(Container container) {

        Area area = container.getArea();

        int maxCX = area.maxX >> 4;
        int maxCZ = area.maxZ >> 4;
        int minCX = area.minX >> 4;
        int minCZ = area.minZ >> 4;

        for (int x = minCX; x <= maxCX; x++) {
            for (int z = minCZ; z <= maxCZ; z++) {
                ChunkContainer chunk = getChunk(x, z);

                //Add only if the chunk is loaded
                if (chunk != null) {
                    chunk.addContainer(container);
                }

            }
        }
    }

    /**
     * Remove um subContainer
     *
     * @param other que será removido
     */
    @Override
    public void removeContainer(IContainer other) {
        ChunkContainer chunk = (ChunkContainer) other;
        fastContainers.remove(pair(chunk.x, chunk.z));
        subContainers.remove(other);
    }

    /**
     * Called when container is added
     *
     * @param container
     */
    @Override
    protected void addedContainer(Container container) {
        if (container.isFirstProxy()) {

            Tickable tickable = container.getByType(Tickable.class);

            if (tickable != null) {
                tickables.add(tickable);
            }

            container.refreshDefaults();
        }
    }

    /**
     * @param iContainer wish already removed
     */
    @Override
    public void removedContainer(IContainer iContainer) {

        if (iContainer instanceof ProxyContainer) {

            ProxyContainer proxy = (ProxyContainer) iContainer;

            if (!proxy.controller.isAttached()) {

                proxy.controller.unloaded();

                Tickable tickable = iContainer.getCurrent(Tickable.class);

                if (tickable != null) {
                    tickables.remove(tickable);
                }
            }

        }
    }

    /**
     * @param clazz
     * @return o número de subcontainer que percente a classe clazz
     */
    @Override
    public int countTypes(Class clazz) {
        return -1;
    }

    @Override
    public IContainer update(int x, int y, int z) {
        ChunkContainer chunkContainer = getChunk(x >> 4, z >> 4);

        return chunkContainer == null ? this : chunkContainer.update(x, y, z);
    }

    /**
     * @param user target
     * @param x    pos x
     * @param y    pos y
     * @param z    pos z
     * @return true case the given user can't access this container
     */
    @Override
    public boolean refreshUser(User user, int x, int y, int z, boolean add) {
        ChunkContainer chunkContainer = getChunk(x >> 4, z >> 4);

        return chunkContainer != null && chunkContainer.refreshUser(user, x, y, z, true);
    }

    /**
     * @param type
     * @param <C>
     * @return closest founded C KAPPA
     */
    @Override
    public <C> C getClosest(Class<C> type) {
        if (type.isAssignableFrom(this.getClass())) {
            return (C) this;
        } else {
            return null;
        }
    }

    /**
     * @return return closest Protection
     */
    @Override
    public Protection protection() {
        return this;
    }

    /**
     * unAttach this container
     */
    @Override
    public void unAttachAndRemove() {
        throw new UnsupportedOperationException();
    }

    /**
     * unload containers recursive
     */
    @Override
    public void unload() {
        for (ChunkContainer container : new ArrayList<>(subContainers)) {
            container.unload();
        }
    }

    /**
     * @param chunk loaded chunk
     */
    @Override
    public void chunkLoad(Chunk chunk) {
        chunks.add(chunk);

        getOrLoadChunk(chunk.getX(), chunk.getZ()).chunkLoad(chunk);
    }

    /**
     * @param chunk unloaded chunk
     */
    @Override
    public void chunkUnload(Chunk chunk) {
        chunks.remove(chunk);

        ChunkContainer container = getChunk(chunk.getX(), chunk.getZ());

        container.chunkUnload(chunk);

        container.unload();
    }

    public void tick() {
        for (Tickable tickable : tickables) {
            tickable.tick();
        }
    }

    //======================================================
    //==================DEFAULT PROTECTION==================
    //======================================================
    @Override
    public boolean canSpread(Material type) {
        return false;
    }

    @Override
    public boolean canPistonWork() {
        return false;
    }

    @Override
    public boolean canExplode() {
        return false;
    }

    @Override
    public boolean canEnter(User user) {
        return true;
    }

    @Override
    public boolean canExit(User user) {
        return true;
    }

    @Override
    public boolean canBreak(User user, Material type) {
        return user.hasPermission("simplecraft.world.break");
    }

    @Override
    public boolean canPlace(User user, Material type) {
        return user.hasPermission("simplecraft.world.place");
    }

    @Override
    public boolean canInteract(User user, Material type) {
        return user.hasPermission("simplecraft.world.interact");
    }

    @Override
    public boolean canRemoveSpecials(User user) {
        return user.hasPermission("simplecraft.world.modules");
    }

    @Override
    public boolean canUseSkill(int type) {
        return false;
    }

    @Override
    public boolean canFly() {
        return true;
    }

    @Override
    public boolean isPvpOn() {
        return false;
    }

    @Override
    public boolean isPveOn(User user) {
        return false;
    }
    //======================================================
    //================_DEFAULT PROTECTION===================
    //======================================================
}
