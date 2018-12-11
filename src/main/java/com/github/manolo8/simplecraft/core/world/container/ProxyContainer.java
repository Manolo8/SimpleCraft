package com.github.manolo8.simplecraft.core.world.container;

import com.github.manolo8.simplecraft.core.world.container.Container.CustomProtection;
import com.github.manolo8.simplecraft.interfaces.Protection;
import com.github.manolo8.simplecraft.module.user.User;
import com.github.manolo8.simplecraft.utils.def.Matcher;
import org.bukkit.Chunk;

import java.util.Iterator;

public class ProxyContainer<C extends Container> extends AbstractContainer<AbstractContainer<?, ?>, ProxyContainer<?>> {

    protected final Area area;
    public final C controller;
    protected final ChunkContainer chunk;
    protected AbstractContainer main;
    protected Protection protection;
    protected boolean attached;

    public ProxyContainer(C controller, ChunkContainer main) {
        super(main);
        this.controller = controller;
        this.chunk = main;
        this.attached = true;

        Area o = controller.area;

        this.area = new Area(
                Math.min(main.x * 16 + 15, o.maxX),
                o.maxY,
                Math.min(main.z * 16 + 15, o.maxZ),
                Math.max(main.x * 16, o.minX),
                o.minY,
                Math.max(main.z * 16, o.minZ)
        );
    }

    /**
     * @param other to test
     * @return true case this container intersects the other
     */
    public boolean intersect(ProxyContainer other) {
        return controller.area.intersect(other.controller.area);
    }

    /**
     * @param other to test
     * @return true case other container is inside this container
     */
    public boolean inside(ProxyContainer other) {
        return controller.area.inside(other.controller.area);
    }

    /**
     * @param x pos posX
     * @param y pos y
     * @param z pos posZ
     * @return true case this points are inside this container
     */
    @Override
    public boolean inside(int x, int y, int z) {
        return area.inside(x, y, z);
    }

    /**
     * @param area
     * @return true if the area is available
     */
    @Override
    public boolean isAvailable(Area area, Container ignore) {
        ProxyContainer found = null;

        //Needs to be controller area
        for (ProxyContainer sub : subContainers) {
            if (sub.controller != ignore && sub.controller.area.intersect(area)) {
                if (sub.controller.area.inside(area)) {
                    found = sub;
                } else if (!area.inside(sub.controller.area)) {
                    return false;
                }
            }
        }

        if (found == null) {
            return true;
        } else {
            return found.isAvailable(area, ignore);
        }
    }

    /**
     * @param container attempt to add
     * @return true case successful added
     */
    public boolean addContainer(ProxyContainer container, boolean isNew) {
        ProxyContainer found = null;
        boolean refresh = false;

        for (ProxyContainer sub : subContainers) {
            if (sub.intersect(container)) {
                if (sub.inside(container)) {
                    found = sub;
                } else if (container.inside(sub)) {
                    refresh = true;
                } else {
                    return false;
                }
            }
        }

        if (found == null) {

            container.main = this;

            if (refresh) {
                Iterator<ProxyContainer<?>> i = subContainers.iterator();
                while (i.hasNext()) {
                    ProxyContainer move = i.next();
                    if (container.inside(move)) {
                        i.remove();
                        container.addContainer(move, false);
                    }
                }
            }

            subContainers.add(container);

            if (isNew) {
                container.refreshDefaults();
                addedContainer(container.controller);
            }

            return true;
        } else {
            return found.addContainer(container, isNew);
        }
    }

    @Override
    public IContainer update(int x, int y, int z) {
        if (inside(x, y, z) && attached) {

            if (subContainers.size() != 0) {
                for (IContainer sub : subContainers) {
                    if (sub.inside(x, y, z)) {
                        return sub.update(x, y, z);
                    }
                }
            }

            return this;

        } else {
            return main.update(x, y, z);
        }
    }

    /**
     * @param clazz
     * @return number of subContainers by given class
     */
    @Override
    public int countTypes(Class clazz) {
        int total = 0;

        for (ProxyContainer proxy : controller.proxies) {
            total += proxy.countContainers0(clazz);
        }

        return total;
    }

    private int countContainers0(Class clazz) {
        int total = 0;

        if (getCurrent(clazz) != null) total++;

        for (ProxyContainer container : subContainers) {
            total += container.countContainers0(clazz);
        }

        return total;
    }

    /**
     * @param user target
     * @param x    pos x
     * @param y    pos y
     * @param z    pos z
     * @param add
     * @return true case the given user can't access this container
     */
    @Override
    public boolean refreshUser(User user, int x, int y, int z, boolean add) {
        if (attached) {
            if (inside(x, y, z)) {

                if (add) {
                    if (protection.canEnter(user)) {
                        controller.addInsideUser(user);
                    } else {
                        return true;
                    }
                }

                if (!subContainers.isEmpty()) {
                    for (ProxyContainer sub : subContainers) {
                        if (sub.inside(x, y, z)) {
                            return sub.refreshUser(user, x, y, z, true);
                        }
                    }
                }

                if (user.iContainer() != this) {
                    user.updateContainer(this);
                }

                return false;

            } else {

                if (!protection.canExit(user)) {
                    return true;
                } else if (controller.proxies.size() != 1) {
                    //if proxies size != 1, maybe has an proxy in the current chunk

                    IContainer updated = chunk.main.getChunk(x >> 4, z >> 4).update(x, y, z);

                    if (updated instanceof ProxyContainer) {

                        ProxyContainer proxy = (ProxyContainer) updated;

                        if (proxy.controller == controller) {

                            boolean status = proxy.refreshUser(user, x, y, z, false);

                            if (status) {
                                proxy.chunk.changeMainListenContainer(user, this);
                            }

                            return status;
                        }
                    }
                }

            }
        }

        controller.removeInsideUser(user);

        return main.refreshUser(user, x, y, z, false);
    }

    /**
     * Called when an user exit from the server or change world
     *
     * @param user
     */
    @Override
    public void exit(User user) {
        controller.removeInsideUser(user);
        super.exit(user);
    }

    /**
     * @param other
     */
    @Override
    public void removeContainer(IContainer other) {
        this.subContainers.remove(other);
        removedContainer(other);
    }

    @Override
    public void refreshDefaults() {
        protection = getClosest(Protection.class);

        for (ProxyContainer sub : subContainers) {
            sub.refreshDefaults();
        }
    }

    public void addNearbyUser(User user) {
        controller.addNearbyUser(user);

        if (subContainers.size() != 0) {
            for (ProxyContainer proxy : subContainers) {
                proxy.addNearbyUser(user);
            }
        }
    }

    public void removeNearbyUser(User user) {
        controller.removeNearbyUser(user);

        if (subContainers.size() != 0) {
            for (ProxyContainer proxy : subContainers) {
                proxy.removeNearbyUser(user);
            }
        }
    }

    /**
     * @return return closest Protection
     */
    @Override
    public Protection protection() {
        return protection;
    }

    /**
     * unAttach this container
     */
    @Override
    public void unAttachAndRemove() {

        this.attached = false;

        controller.removeProxy(this);
        main.removeContainer(this);

        if (subContainers.size() != 0) {
            for (ProxyContainer container : subContainers) {
                if (main instanceof ChunkContainer) {
                    ((ChunkContainer) main).addContainer(container, false);
                } else if (main instanceof ProxyContainer) {
                    ((ProxyContainer) main).addContainer(container, false);
                }
            }
        }
    }

    @Override
    public void unAttachAlByMatcher(Matcher matcher) {
        super.unAttachAlByMatcher(matcher);

        if (matcher.match(controller)) {
            unAttachAndRemove();
        }
    }


    /**
     * @param type
     * @return type if value is assignable or null
     */
    @Override
    public <C> C getCurrent(Class<C> type) {
        return type.isAssignableFrom(controller.getClass()) ? (C) controller : null;
    }

    /**
     * unload containers recursive
     */
    @Override
    public void unload() {

        controller.removeProxy(this);

        this.attached = false;

        removedContainer(this);

        if (subContainers.size() != 0) {

            for (ProxyContainer container : subContainers) {
                container.unload();
            }

            subContainers.clear();
        }
    }

    /**
     * @param chunk loaded chunk
     */
    @Override
    public void chunkLoad(Chunk chunk) {
        controller.chunkLoad(chunk);

        for (ProxyContainer container : subContainers) {
            container.chunkLoad(chunk);
        }
    }

    /**
     * @param chunk unloaded chunk
     */
    @Override
    protected void chunkUnload(Chunk chunk) {
        controller.chunkUnload(chunk);

        for (ProxyContainer container : subContainers) {
            container.chunkUnload(chunk);
        }
    }

    public static class CustomProtectionProxy extends ProxyContainer<CustomProtection> {

        public CustomProtectionProxy(CustomProtection controller, ChunkContainer chunk) {
            super(controller, chunk);
        }

        @Override
        public void refreshDefaults() {
            super.refreshDefaults();

            controller.setupMainProtection(protection);
        }

        @Override
        public <C> C getCurrent(Class<C> type) {
            if (type == Protection.class) return null;
            else return super.getCurrent(type);
        }
    }
}
