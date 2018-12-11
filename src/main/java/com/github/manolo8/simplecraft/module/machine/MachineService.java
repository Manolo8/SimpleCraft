package com.github.manolo8.simplecraft.module.machine;

import com.github.manolo8.simplecraft.core.commands.line.Command;
import com.github.manolo8.simplecraft.core.commands.line.annotation.CmdDescription;
import com.github.manolo8.simplecraft.core.commands.line.annotation.CmdInfo;
import com.github.manolo8.simplecraft.core.commands.line.annotation.CmdMapping;
import com.github.manolo8.simplecraft.core.commands.line.annotation.CmdPermission;
import com.github.manolo8.simplecraft.core.service.ContainerService;
import com.github.manolo8.simplecraft.core.world.WorldInfo;
import com.github.manolo8.simplecraft.core.world.container.Area;
import com.github.manolo8.simplecraft.core.world.container.Container;
import com.github.manolo8.simplecraft.core.world.provider.Provider;
import com.github.manolo8.simplecraft.module.user.User;
import com.github.manolo8.simplecraft.utils.def.Matcher;
import com.github.manolo8.simplecraft.utils.mc.ItemStackUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;

@SuppressWarnings("unused")
public class MachineService extends ContainerService<Machine, MachineRepository> {

    public MachineService(MachineRepository repository) {
        super(repository, -1);
    }

    @Override
    public Provider<Machine, ?> initProvider(WorldInfo worldInfo) {
        return new MachineProvider(worldInfo, this);
    }

    @Override
    public Matcher<Container> matcher() {
        return entity -> entity.getClass() == Machine.class;
    }

    public Machine create(WorldInfo info, Area area, ItemStack item) throws SQLException {
        return repository.create(info, area, item);
    }

    @CmdInfo("machine")
    public void addInfo(Command command) {
        command.setDescription("Comandos úteis dos spawners");
    }

    @CmdMapping("machine create")
    @CmdDescription("Pega um mobspawner")
    @CmdPermission("simplecraft.admin")
    public void getSpawner(User user) {
        user.base().getInventory().addItem(new ItemStack(Material.SPAWNER));
    }

    @CmdMapping("machine create essence <quantity>")
    @CmdDescription("Cria essencia")
    @CmdPermission("simplecraft.admin")
    public void createEssence(User user, double value) {
        user.base().getInventory().addItem(ItemStackUtils.create(Material.BROWN_MUSHROOM, "§e" + value + " essências"));
    }
}
