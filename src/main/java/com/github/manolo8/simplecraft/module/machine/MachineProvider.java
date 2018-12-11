package com.github.manolo8.simplecraft.module.machine;

import com.github.manolo8.simplecraft.core.world.WorldInfo;
import com.github.manolo8.simplecraft.core.world.container.IContainer;
import com.github.manolo8.simplecraft.core.world.provider.Provider;
import com.github.manolo8.simplecraft.module.user.MessageType;
import com.github.manolo8.simplecraft.module.user.User;
import com.github.manolo8.simplecraft.utils.location.SimpleArea;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockPlaceEvent;

import java.sql.SQLException;

public class MachineProvider extends Provider<Machine, MachineService> {

    public MachineProvider(WorldInfo worldInfo, MachineService service) {
        super(worldInfo, service);
    }

    public void onPlace(User user, BlockPlaceEvent event) {
        Block block = event.getBlock();

        IContainer iContainer = worldInfo.getContainer().update(block);

        if (iContainer.countTypes(Machine.class) >= 80) {
            user.sendMessage(MessageType.ERROR, "O terreno atingiu o limite de 80 spawners. Evolua eles!");
            event.setCancelled(true);
        } else {

            Machine machine;

            try {
                machine = service.create(worldInfo, new SimpleArea(block.getX(), block.getY(), block.getZ()).build(), event.getItemInHand());
            } catch (SQLException e) {
                e.printStackTrace();
                user.sendMessage(MessageType.ERROR, "Houve um erro interno. Contate um ADMIN!");
                event.setCancelled(true);
                return;
            }

            machine.setWorldInfo(worldInfo);

            worldInfo.getContainer().addContainer(machine);

            machine.updateBlock();

            user.sendAction("§aSpawner criado! Clique nele para mais informações.");
        }
    }
}
