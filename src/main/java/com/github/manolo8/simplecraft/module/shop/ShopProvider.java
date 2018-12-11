package com.github.manolo8.simplecraft.module.shop;

import com.github.manolo8.simplecraft.core.world.provider.AreaProvider;
import com.github.manolo8.simplecraft.core.world.WorldInfo;
import com.github.manolo8.simplecraft.module.user.MessageType;
import com.github.manolo8.simplecraft.module.user.User;
import com.github.manolo8.simplecraft.utils.location.SimpleArea;
import org.bukkit.block.Block;
import org.bukkit.event.block.SignChangeEvent;

import java.sql.SQLException;

public class ShopProvider extends AreaProvider<Shop, ShopService> {

    public ShopProvider(WorldInfo info, ShopService service) {
        super(info, service);
    }

    public void signCreateEvent(User user, SignChangeEvent event) {
        String header = event.getLine(0);

        if (header.equalsIgnoreCase("SimpleCraft")) {
            if (!user.isAdmin()) {
                event.setCancelled(true);
                return;
            }
        } else if (!header.equalsIgnoreCase(user.identity().getName())) {
            return;
        }

        if (!isValid(event.getLine(2))) return;

        ShopConverter converter = new ShopConverter();
        converter.convert(user, event);

        if (!converter.isValid()) {
            user.sendMessage(MessageType.ERROR, converter.getStatus());
            return;
        }

        Block block = event.getBlock();
        SimpleArea area = new SimpleArea(block.getX(), block.getY(), block.getZ());

        if (canAdd(area)) {
            try {
                Shop shop = service.create(converter, area.build());
                worldInfo.getContainer().addContainer(shop);
            } catch (SQLException e) {
                e.printStackTrace();
                user.sendMessage(MessageType.ERROR, "Houve um erro interno!");
            }
        } else {
            user.sendMessage(MessageType.ERROR, "Não foi possível criar o SHOP!");
        }
    }

    private boolean isValid(String str) {

        boolean hasPrice = false;
        boolean hasMethod = false;

        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (Character.isDigit(c)) hasPrice = true;
            if (c == 'C' || c == 'c' || c == 'V' || c == 'v') hasMethod = true;
        }

        return hasPrice && hasMethod;
    }
}
