package com.github.manolo8.simplecraft.core.world.container;

import com.github.manolo8.simplecraft.interfaces.Protection;
import com.github.manolo8.simplecraft.module.user.User;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class ChunkContainer extends AbstractContainer<WorldContainer, ProxyContainer<?>> {

    public final int x;
    public final int z;
    final int maxX;
    final int minX;
    final int maxZ;
    final int minZ;
    final Set<User> listening;

    public ChunkContainer(WorldContainer main, int x, int z) {
        super(main);

        this.listening = new HashSet<>();

        this.x = x;
        this.z = z;
        this.maxX = x + 1;
        this.minX = x - 1;
        this.maxZ = z + 1;
        this.minZ = z - 1;
    }

    /**
     * Add an user to receive packets near
     *
     * @param user
     */
    void changeMainListenContainer(User user, IContainer iContainer) {

        ChunkContainer old = null;

        if (iContainer instanceof ChunkContainer) {
            old = (ChunkContainer) iContainer;
        } else if (iContainer instanceof ProxyContainer) {
            old = ((ProxyContainer) iContainer).chunk;
        }

        if (old == null) {
            //First login
            addAll(user);
        } else if (old.main != main) {
            //World change
            old.removeAll(user);
            addAll(user);
        } else {
            //Chunk change
            old.removeIfNotListing(user, this);
            addIfNotListing(user, old);
        }
    }

    private void addAll(User user) {
        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                ChunkContainer chunk = main.getChunk(x, z);

                if (chunk != null) chunk.addListenUser(user);
            }
        }
    }

    private void removeAll(User user) {
        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                ChunkContainer chunk = main.getChunk(x, z);

                if (chunk != null) chunk.removeListenUser(user);
            }
        }
    }

    private void removeIfNotListing(User user, ChunkContainer current) {
        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {

                if (!current.isListing(x, z)) {
                    ChunkContainer chunk = main.getChunk(x, z);

                    if (chunk != null) chunk.removeListenUser(user);
                }

            }
        }
    }

    private void addIfNotListing(User user, ChunkContainer old) {
        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {

                if (!old.isListing(x, z)) {
                    ChunkContainer chunk = main.getChunk(x, z);

                    if (chunk != null) chunk.addListenUser(user);
                }

            }
        }
    }

    private boolean isListing(int ax, int az) {
        return minX <= ax &&
                maxX >= ax &&
                minZ <= az &&
                maxZ >= az;
    }

    /**
     * Add an user to receive near packets
     *
     * @param user
     */
    void addListenUser(User user) {
        listening.add(user);

        if (subContainers.size() != 0) {

            for (ProxyContainer proxy : subContainers) {
                proxy.addNearbyUser(user);
            }

        }
    }

    /**
     * Remove an user to not receive near packets
     *
     * @param user
     */
    void removeListenUser(User user) {
        listening.remove(user);

        if (subContainers.size() != 0) {

            for (ProxyContainer proxy : subContainers) {
                proxy.removeNearbyUser(user);
            }

        }

    }

    /**
     * @param x pos posX
     * @param y pos y
     * @param z pos posZ
     * @return true case this points are inside this container
     */
    @Override
    public boolean inside(int x, int y, int z) {
        return x >> 4 == this.x && z >> 4 == this.z;
    }

    /**
     * @param area
     * @return true if the area is available
     */
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

    public boolean addContainer(Container container) {
        return addContainer(container.createProxy(this), true);
    }

    /**
     * Called when container is added
     *
     * @param container
     */
    @Override
    public void addedContainer(Container container) {

        main.addedContainer(container);

        if (listening.size() != 0) {

            Iterator<User> i = listening.iterator();

            while (i.hasNext()) {
                container.addNearbyUser(i.next());
            }

        }

    }

    @Override
    public void removedContainer(IContainer iContainer) {
        main.removedContainer(iContainer);

        if (listening.size() != 0) {
            ProxyContainer removed = (ProxyContainer) iContainer;

            Iterator<User> i = listening.iterator();

            while (i.hasNext()) {
                removed.removeNearbyUser(i.next());
            }
        }

    }

    @Override
    public void refreshDefaults() {
    }

    /**
     * Called when an user exit from the server or change world
     *
     * @param user
     */
    @Override
    public void exit(User user) {
        removeAll(user);
        main.exit(user);
    }

    /**
     * @param container que será adicionado
     * @return true caso o container foi adicionado
     */
    public boolean addContainer(ProxyContainer container, boolean isNew) {
        ProxyContainer found = null;
        boolean needRefresh = false;

        for (ProxyContainer sub : subContainers) {
            if (sub.intersect(container)) {
                if (sub.inside(container)) {
                    found = sub;
                } else if (container.inside(sub)) {
                    needRefresh = true;
                } else {
                    return false;
                }
            }
        }

        if (found == null) {

            container.main = this;

            if (needRefresh) {
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

    /**
     * @param clazz
     * @return o número de subcontainer que percente a classe clazz
     */
    @Override
    public int countTypes(Class clazz) {
        int total = 0;

        for (IContainer iContainer : subContainers) {
            total += iContainer.countTypes(clazz);
        }

        return total;
    }

    @Override
    public IContainer update(int x, int y, int z) {
        if (!inside(x, y, z)) {
            return main.update(x, y, z);
        } else {

            if (subContainers.size() != 0) {
                for (IContainer sub : subContainers) {
                    if (sub.inside(x, y, z)) {
                        return sub.update(x, y, z);
                    }
                }
            }

            return this;
        }
    }

    /**
     * @param user O jogador
     * @param x    posição x
     * @param y    posição y
     * @param z    posição z
     * @return false caso possa entrar no container atualizado,
     * true caso contrário
     */
    @Override
    public boolean refreshUser(User user, int x, int y, int z, boolean add) {
        if (inside(x, y, z)) {

            if (add) {
                changeMainListenContainer(user, user.iContainer());
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
            return main.refreshUser(user, x, y, z, false);
        }
    }

    @Override
    public <C> C getClosest(Class<C> type) {
        return main.getClosest(type);
    }

    /**
     * @param other
     */
    @Override
    public void removeContainer(IContainer other) {
        subContainers.remove(other);
        removedContainer(other);
    }

    /**
     * @return retorna a proteção mais próxima
     */
    @Override
    public Protection protection() {
        return main.protection();
    }

    /**
     * Descarrega o container
     */
    @Override
    public void unAttachAndRemove() {
        throw new UnsupportedOperationException();
    }

    /**
     * Descarrega o container recursivamente
     */
    @Override
    public void unload() {

        for (ProxyContainer container : subContainers) {
            container.unload();
        }

        subContainers.clear();

        main.removeContainer(this);
    }
}
