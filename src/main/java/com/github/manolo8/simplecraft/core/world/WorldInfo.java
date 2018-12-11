package com.github.manolo8.simplecraft.core.world;

import com.github.manolo8.simplecraft.core.service.ContainerService;
import com.github.manolo8.simplecraft.core.world.container.Container;
import com.github.manolo8.simplecraft.core.world.container.ChunkContainer;
import com.github.manolo8.simplecraft.core.world.container.WorldContainer;
import com.github.manolo8.simplecraft.core.world.provider.Provider;
import com.github.manolo8.simplecraft.core.data.model.named.NamedEntity;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.WorldCreator;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class WorldInfo extends NamedEntity {

    List<Provider> providers;
    List<ContainerService> services;
    private World world;
    private WorldContainer container;
    private WorldFlag flag;
    private WorldCreator creator;
    private boolean disabled;

    public WorldInfo() {
        providers = new ArrayList();
        services = new ArrayList();
    }

    //======================================================
    //====================ENCAPSULATION=====================
    //======================================================
    public World getWorld() {
        return world;
    }

    public void setWorld(World world) {
        this.world = world;
    }

    public WorldContainer getContainer() {
        return container;
    }

    public void setContainer(WorldContainer container) {
        this.container = container;
    }

    public WorldFlag flags() {
        return flag;
    }

    public void setFlag(WorldFlag flag) {
        this.flag = flag;
    }

    public WorldCreator creator() {
        return creator;
    }

    public void setCreator(WorldCreator creator) {
        this.creator = creator;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
        modified();
    }
    //======================================================
    //===================_ENCAPSULATION=====================
    //======================================================

    public boolean isLoaded() {
        return world != null;
    }

    public void containerLoad(ChunkContainer chunkContainer) {
        for (ContainerService<Container, ?> loader : this.services) {
            for (Container loaded : loader.containerLoad(this, chunkContainer)) {
                chunkContainer.addContainer(loaded);
            }
        }
    }

    public void addService(ContainerService<? extends Container, ?> containerService) {
        this.services.add(containerService);
        this.providers.add(containerService.initProvider(this));

        if (isLoaded() && container.getSubContainers().size() != 0) {
            for (ChunkContainer chunkContainer : container.getSubContainers()) {
                for (Container loaded : containerService.containerLoad(this, chunkContainer)) {
                    chunkContainer.addContainer(loaded);
                }
            }
        }
    }

    public void removeService(ContainerService service) {

        if (services.remove(service)) {

            for (Provider provider : providers) {
                if (provider.match(service)) {
                    providers.remove(provider);
                    break;
                }
            }

            if (isLoaded() && container.getSubContainers().size() != 0) {
                for (ChunkContainer chunkContainer : container.getSubContainers()) {
                    chunkContainer.unAttachAlByMatcher(service.matcher());
                }
            }

        }


    }

    public <A> A getProvider(Class<A> type) {
        for (Provider provider : providers)
            if (type.isAssignableFrom(provider.getClass()))
                return (A) provider;

        return null;
    }

    public Set<Chunk> getChunks() {
        return container.getChunks();
    }
}