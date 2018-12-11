package com.github.manolo8.simplecraft.core.world;

import com.github.manolo8.simplecraft.core.commands.line.ParameterBuilder;
import com.github.manolo8.simplecraft.core.commands.line.Result;
import com.github.manolo8.simplecraft.core.commands.line.Sender;
import com.github.manolo8.simplecraft.core.commands.line.TabArguments;
import com.github.manolo8.simplecraft.core.commands.line.annotation.CmdDescription;
import com.github.manolo8.simplecraft.core.commands.line.annotation.CmdMapping;
import com.github.manolo8.simplecraft.core.commands.line.annotation.CmdPermission;
import com.github.manolo8.simplecraft.core.commands.line.annotation.SupplierOptions;
import com.github.manolo8.simplecraft.core.commands.line.inf.Supplier;
import com.github.manolo8.simplecraft.core.service.ContainerService;
import com.github.manolo8.simplecraft.core.service.NamedHolderService;
import com.github.manolo8.simplecraft.core.service.Service;
import com.github.manolo8.simplecraft.core.world.container.WorldContainer;
import com.github.manolo8.simplecraft.module.user.MessageType;
import com.github.manolo8.simplecraft.utils.def.Flag;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class WorldService
        extends NamedHolderService<WorldInfo, WorldInfoRepository>
        implements Runnable {

    public static int tick;
    private final List<ContainerService> services;

    public WorldService(WorldInfoRepository repository) {
        super(repository);
        this.services = new ArrayList<>();
    }

    public void init() {
        for (World world : Bukkit.getWorlds()) {
            worldLoad(world);
        }
    }

    public void postInit() throws SQLException {
        for (WorldInfo info : repository.findAll()) {
            if (!info.isLoaded() && !info.isDisabled()) {
                info.creator().createWorld();
            }
        }
    }

    public void stop() {
        for (WorldInfo info : entities) info.getContainer().unload();
    }

    public void worldLoad(World world) {
        load(findByWorld(world));
    }

    public void worldUnload(World world) {
        unload(findByWorld(world));
    }

    @Override
    protected void load(WorldInfo entity) {

        for (ContainerService service : services)
            if (service.useInWorld(entity))
                entity.addService(service);

        entity.getContainer().refreshDefaults();

        super.load(entity);
    }

    @Override
    protected void unload(WorldInfo entity) {
        entity.getContainer().unload();

        entity.setContainer(null);
        entity.services.clear();
        entity.providers.clear();

        super.unload(entity);
    }

    public void chunkLoad(Chunk chunk) {
        WorldInfo info = findByWorld(chunk.getWorld());

        info.getContainer().chunkLoad(chunk);
    }

    public void chunkUnload(Chunk chunk) {
        WorldInfo info = findByWorld(chunk.getWorld());

        info.getContainer().chunkUnload(chunk);
    }

    public WorldInfo findByWorld(World world) {
        for (WorldInfo worldInfo : entities)
            if (worldInfo.getWorld() == world)
                return worldInfo;

        return findOrCreate(world);
    }

    private WorldInfo findOrCreate(World world) {

        WorldInfo info = null;

        try {
            info = repository.findOrCreate(world.getName());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (info != null) {
            info.setContainer(new WorldContainer(info));
            info.setWorld(world);
            info.setName(world.getName());

            load(info);
        }

        return info;
    }

    private void refresh(WorldInfo info) {
        for (ContainerService service : services) {
            if (service.useInWorld(info)) {

                if (!info.services.contains(service)) {
                    info.addService(service);
                }

            } else if (info.services.contains(service)) {
                info.removeService(service);
            }
        }
    }

    public void register(ContainerService provider) {
        this.services.add(provider);
    }

    public <A> A findFirstProvider(Class<A> type) {

        for (WorldInfo info : entities) {
            A a = info.getProvider(type);

            if (a != null) return a;
        }

        return null;
    }

    @Override
    public void run() {
        tick++;
        for (WorldInfo info : entities) info.getContainer().tick();
    }


    //======================================================
    //=======================COMMANDS=======================
    //======================================================

    @CmdMapping("world create <name> <builder>")
    @CmdDescription("Cria um novo mundo")
    @CmdPermission("simplecraft.admin")
    public void create(Sender sender, String name, WorldBuilder builder) throws SQLException {

        if (exists(name)) {
            sender.sendMessage(MessageType.ERROR, "Já existe um mundo com esse nome!");
        } else {
            WorldInfo info = repository.create(name, builder);

            info.creator().createWorld();

            sender.sendMessage(MessageType.SUCCESS, "O mundo foi criado!");
        }

    }

    @CmdMapping("world -e <world> providers <provider> <boolean>")
    @CmdDescription("Seta uma flag em um mundo")
    @CmdPermission("simplecraft.admin")
    public void setFlag(Sender sender, WorldInfo info, Flag.Toggle toggler, boolean value) {

        if (toggler.set(info, WorldInfo::flags, value)) {
            sender.sendMessage(MessageType.SUCCESS, "A flag foi alterada!");
            refresh(info);
        } else {
            sender.sendMessage(MessageType.INFO, "A flag não precisou ser alterada!");
        }

    }

    @CmdMapping("world -e <world> enable")
    @CmdDescription("Habilita um mundo")
    @CmdPermission("simplecraft.admin")
    public void enable(Sender sender, WorldInfo info) {

        if (!info.isDisabled()) {
            sender.sendMessage(MessageType.ERROR, "O mundo já está habilitado!");
        } else {
            info.setDisabled(false);
            info.creator().createWorld();
            sender.sendMessage(MessageType.SUCCESS, "Mundo ativado!");
        }

    }

    @CmdMapping("world -e <world> disable")
    @CmdDescription("Desabilita um mundo")
    @CmdPermission("simplecraft.admin")
    public void disable(Sender sender, WorldInfo info) {

        if (info.isDisabled()) {
            sender.sendMessage(MessageType.ERROR, "O mundo já esta desabilitado!");
        } else {
            World def = Bukkit.getWorlds().get(0);

            if (def == info.getWorld()) {
                sender.sendMessage(MessageType.ERROR, "Não é possível desabilitar o overworld!");
            } else {

                for (Player player : info.getWorld().getPlayers()) {
                    if (!player.teleport(def.getSpawnLocation())) {
                        sender.sendMessage(MessageType.ERROR, "Não foi possível teletransportar o jogador " + player.getName() + " para o spawn!");
                        return;
                    }
                }

                if (Bukkit.unloadWorld(info.getWorld(), true)) {
                    info.setDisabled(true);
                    sender.sendMessage(MessageType.SUCCESS, "O mundo foi descarregado!");
                } else {
                    sender.sendMessage(MessageType.ERROR, "Não foi possível descarregar o mundo...");
                }

            }
        }

    }

    @SupplierOptions("world")
    class WorldInfoConverter implements Supplier.Convert<WorldInfo> {

        @Override
        public void tabComplete(TabArguments arguments) throws SQLException {
            arguments.offerSafe(repository.findNames(arguments.getComplete()));
        }

        @Override
        public Result<WorldInfo> convert(ParameterBuilder builder, Sender sender, String value) throws SQLException {

            WorldInfo info = repository.findByName(value);

            if (info == null) {
                return new Result.Error("O mundo '" + value + "' não foi encontrado!");
            } else {
                return new Result(info);
            }

        }
    }

    @SupplierOptions("builder")
    class WorldBuilderConverter implements Supplier.Convert<WorldBuilder> {

        @Override
        public void tabComplete(TabArguments arguments) throws SQLException {
            for (WorldBuilder creator : repository.builders) {
                arguments.offer(creator.getName());
            }
        }

        @Override
        public Result<WorldBuilder> convert(ParameterBuilder builder, Sender sender, String value) {

            value = value.toLowerCase();

            for (WorldBuilder creator : repository.builders) {
                if (creator.getName().equals(value)) {
                    return new Result<>(creator);
                }
            }

            return new Result.Error("O criador '" + value + "' não foi encontrado!");
        }
    }

    @SupplierOptions("provider")
    class WorldFlagConverter implements Supplier.Convert<Flag.Toggle> {

        @Override
        public void tabComplete(TabArguments arguments) {
            for (Flag.Toggle toggle : WorldFlag.togglers) {
                arguments.offer(toggle.getName());
            }
        }

        @Override
        public Result<Flag.Toggle> convert(ParameterBuilder builder, Sender sender, String value) {

            for (Flag.Toggle toggle : WorldFlag.togglers) {
                if (toggle.getName().equals(value)) return new Result<>(toggle);
            }

            return new Result.Error("A flag '" + value + "' não foi encontrada!");
        }
    }

    //======================================================
    //======================_COMMANDS=======================
    //======================================================
}