package com.github.manolo8.simplecraft.domain.shop;

import com.github.manolo8.simplecraft.domain.user.User;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.inventory.ItemStack;

public class ShopController {

    private final ShopService shopService;

    public ShopController(ShopService shopService) {
        this.shopService = shopService;
    }

    public void userClickSign(User user, Block block) {
        Sign sign = (Sign) block.getState();
        if (isNotValid(sign)) return;

        Shop shop = shopService.findOne(block.getX(), block.getY(), block.getZ(), user.getWorldId());

        if (shop == null) {
            user.sendMessage("§cO shopping não foi encontrado");
            return;
        }

        if (shop.getItemStack() == null) {
            if (user.equals(shop.getOwner())
                    || (user.hasPermission("shopping.admin") && shop.getOwner().getId() == -1)) {
                ItemStack hand = user.getBase().getItemInHand();
                if (hand == null || hand.getType() == Material.AIR) {
                    user.sendMessage("§cClique na placa com um item para coloca-lo a venda!");
                    return;
                }
                shop.setItemStack(hand);
                sign.setLine(3, hand.getType().name());
                sign.update();
                return;
            }
            user.sendMessage("§cO shop ainda não tem um item definido.");
            return;
        }

        user.sendMessage("§aAbrindo a GUI...");
        user.createView(new ShopView(shop));
    }

    public void userCreateSign(User user, SignChangeEvent event) {
        if (isNotValid(event)) return;

        ShopConverter converter = new ShopConverter().convert(user, event);

        if (!converter.isValid()) {
            user.sendMessage("§c" + converter.getStatus());
            return;
        }

        Location loc = event.getBlock().getLocation();

        Shop shop = shopService.create(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), user.getWorldId());

        converter.update(shop);

        user.sendMessage("§aShop criado com sucesso!");
    }

    private boolean isNotValid(Sign sign) {
        return !(sign.getLine(0).length() > 3
                && NumberUtils.toInt(sign.getLine(1)) > 0);
    }

    private boolean isNotValid(SignChangeEvent sign) {
        return !(sign.getLine(0).length() > 3
                && NumberUtils.toInt(sign.getLine(1)) > 0);
    }
}
