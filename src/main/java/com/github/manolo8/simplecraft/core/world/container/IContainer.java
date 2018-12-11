package com.github.manolo8.simplecraft.core.world.container;

import com.github.manolo8.simplecraft.interfaces.Protection;
import com.github.manolo8.simplecraft.module.user.User;
import org.bukkit.Location;
import org.bukkit.block.Block;

import java.util.function.Consumer;

public interface IContainer {

    /**
     * @param x pos posX
     * @param y pos y
     * @param z pos posZ
     * @return true case this points are inside this container
     */
    boolean inside(int x, int y, int z);

    /**
     * @param area
     * @return true if the area is available
     */
    boolean isAvailable(Area area, Container ignore);

    /**
     * @param loc
     * @return updated container by that loc
     */
    IContainer update(Location loc);

    /**
     * @param block
     * @return updated container by that block
     */
    IContainer update(Block block);

    /**
     * @param x
     * @param y
     * @param z
     * @return updated container by the coordinates
     */
    IContainer update(int x, int y, int z);

    /**
     * @param user target
     * @param x    pos x
     * @param y    pos y
     * @param z    pos z
     * @return true case the given user can't access this container
     */
    boolean refreshUser(User user, int x, int y, int z, boolean add);

    /**
     * Called when an user exit from the server or change world
     *
     * @param user
     */
    void exit(User user);

    /**
     * @param clazz
     * @return number of subContainers by given class
     */
    int countTypes(Class clazz);

    /**
     * @param type
     * @return true case this container, or subContainers has the given container
     */
    boolean hasClosestType(Class type);

    /**
     * @param type
     * @param <C>
     * @return closest founded C
     */
    <C> C getClosest(Class<C> type);

    /**
     * @param type
     * @param <C>
     * @return type if value is assignable or null
     */
    <C> C getCurrent(Class<C> type);

    /**
     * Execute an action in the first found container assignable by type
     *
     * @param type
     * @param consumer
     */
    <C> void doInClosest(Class<C> type, Consumer<C> consumer);

    /**
     * @return return closest Protection
     */
    Protection protection();
}