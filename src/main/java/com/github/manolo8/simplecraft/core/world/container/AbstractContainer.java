package com.github.manolo8.simplecraft.core.world.container;

import com.github.manolo8.simplecraft.module.user.User;
import com.github.manolo8.simplecraft.utils.def.Matcher;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public abstract class AbstractContainer
        <M extends AbstractContainer<?, ?>, S extends AbstractContainer<?, ?>>
        implements IContainer {

    protected final M main;
    protected final Set<S> subContainers;

    public AbstractContainer(M main) {
        this.main = main;
        this.subContainers = new HashSet();
    }

    /**
     * @param loc
     * @return updated container by that loc
     */
    @Override
    public IContainer update(Location loc) {
        return update(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
    }

    /**
     * @param clazz
     * @return number of subContainers by given class
     */
    @Override
    public int countTypes(Class clazz) {
        int total = 0;

        if (getCurrent(clazz) != null) total++;

        for (IContainer iContainer : subContainers) {
            total += iContainer.countTypes(clazz);
        }

        return total;
    }

    /**
     * @param block
     * @return updated container by that block
     */
    @Override
    public IContainer update(Block block) {
        return update(block.getX(), block.getY(), block.getZ());
    }

    /**
     * Called when an user exit from the server or change world
     *
     * @param user
     */
    @Override
    public void exit(User user) {
        if (main != null) main.exit(user);
    }

    /**
     * Called when container is added
     *
     * @param container
     */
    protected void addedContainer(Container container) {
        if (main != null) main.addedContainer(container);
    }

    /**
     * @param iContainer wish already removed
     */
    protected void removedContainer(IContainer iContainer) {
        if (main != null) main.removedContainer(iContainer);
    }

    /**
     * @param type
     * @return true case this container, or subContainers has the given container
     */
    @Override
    public boolean hasClosestType(Class type) {
        return getClosest(type) != null;
    }

    /**
     * Execute an action in the first found container assignable by type
     *
     * @param type
     * @param consumer
     */
    @Override
    public <C> void doInClosest(Class<C> type, Consumer<C> consumer) {
        C c = getClosest(type);
        if (c != null) consumer.accept(c);
    }

    /**
     * @param type
     * @return type if value is assignable or null
     */
    @Override
    public <C> C getCurrent(Class<C> type) {
        return type.isAssignableFrom(getClass()) ? (C) this : null;
    }

    /**
     * @param type
     * @param <C>
     * @return closest founded C
     */
    @Override
    public <C> C getClosest(Class<C> type) {
        C c = getCurrent(type);

        if (c != null) return c;

        return main == null ? null : main.getClosest(type);
    }

    /**
     * @param chunk loaded chunk
     */
    protected void chunkLoad(Chunk chunk) {
        for (S sub : subContainers) {
            sub.chunkLoad(chunk);
        }
    }

    /**
     * @param chunk unloaded chunk
     */
    protected void chunkUnload(Chunk chunk) {
        for (S sub : subContainers) {
            sub.chunkUnload(chunk);
        }
    }

    /**
     * @param iContainer wish will be removed
     */
    protected abstract void removeContainer(IContainer iContainer);

    /**
     * Refresh default informations when the container is
     * attached
     */
    protected abstract void refreshDefaults();

    /**
     * unAttach this container
     */
    protected abstract void unAttachAndRemove();

    public void unAttachAlByMatcher(Matcher matcher) {
        if (subContainers.size() != 0) {
            for (S s : new ArrayList<>(subContainers)) {
                s.unAttachAlByMatcher(matcher);
            }
        }
    }

    /**
     * unload containers recursive
     */
    protected abstract void unload();
}
