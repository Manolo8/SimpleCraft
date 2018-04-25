package com.github.manolo8.simplecraft.modules.shop;

import com.github.manolo8.simplecraft.core.commands.inventory.BaseView;
import com.github.manolo8.simplecraft.core.commands.inventory.ItemAction;
import com.github.manolo8.simplecraft.core.commands.inventory.ItemActionImpl;
import com.github.manolo8.simplecraft.utils.ItemStackUtils;
import org.bukkit.DyeColor;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public class ShopView extends BaseView {

    private Shop shop;

    public ShopView(Shop shop) {
        this.shop = shop;
    }

    @Override
    public String getTitle() {
        return "Shopping";
    }

    @Override
    public List<? extends ItemAction> getActions() {
        List<ItemAction> actions = new ArrayList<>();

        ItemActionImpl item = new ItemActionImpl();
        item.setIndex(22);
        item.setItemStack(shop.getItemStack());
        actions.add(item);

        ItemActionImpl itemOwner = new ItemActionImpl();
        itemOwner.setIndex(13);
        itemOwner.setItemStack(ItemStackUtils.create(Material.DIAMOND, "§a" + shop.getOwner().getName(), "§cDinheiro " + shop.getOwner().getMoney()));
        actions.add(itemOwner);

        if (shop.getBuyPrice() > 0) {
            ItemActionImpl buy = new ItemActionImpl();
            buy.setIndex(39);
            buy.setItemStack(ItemStackUtils.create(Material.WOOL, DyeColor.YELLOW, "Comprar"));
            buy.setAction(user -> shop.userBuy(user, 1));
            actions.add(buy);
        }

        if (shop.getSellPrice() > 0) {
            ItemActionImpl sell = new ItemActionImpl();
            sell.setIndex(41);
            sell.setItemStack(ItemStackUtils.create(Material.WOOL, DyeColor.RED, "Vender"));
            sell.setAction(user -> shop.userSell(user, 1));
            actions.add(sell);
        }

        return actions;
    }

    @Override
    public void addReference() {
        super.addReference();
        shop.addReference();
    }

    @Override
    public void removeReference() {
        super.removeReference();
        shop.removeReference();
    }
}
