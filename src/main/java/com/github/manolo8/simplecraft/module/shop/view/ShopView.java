package com.github.manolo8.simplecraft.module.shop.view;

import com.github.manolo8.simplecraft.core.commands.inventory.ItemAction;
import com.github.manolo8.simplecraft.core.commands.inventory.base.BaseItemAction;
import com.github.manolo8.simplecraft.core.commands.inventory.base.BaseView;
import com.github.manolo8.simplecraft.module.shop.Shop;
import com.github.manolo8.simplecraft.utils.mc.ItemStackUtils;
import org.bukkit.Material;

import java.util.List;

import static com.github.manolo8.simplecraft.utils.def.StringUtils.doubleToString;

public class ShopView extends BaseView {

    private Shop shop;

    public ShopView(Shop shop) {
        this.shop = shop;
    }


    @Override
    public int size() {
        return 27;
    }

    @Override
    public String getTitle() {
        return "Shop";
    }

    @Override
    public void createActions(List<ItemAction> actions) {
        BaseItemAction item = new BaseItemAction();
        item.setIndex(2, 5);
        item.setItem(shop.getItem());
        actions.add(item);

        BaseItemAction itemOwner = new BaseItemAction();
        itemOwner.setIndex(1, 5);
        itemOwner.setItem(ItemStackUtils.createSkullByIdentity(shop.getMoney().getIdentity(), "§e" + shop.getMoney().getIdentity().getName(), "§eCoins: " + shop.getMoney().getCoinsFormatted()));
        actions.add(itemOwner);
    }

    @Override
    public void createPagination(List<ItemAction> pagination) {
        int size = shop.getItem().getMaxStackSize();

        BaseItemAction status = new BaseItemAction();
        status.setIndex(4);
        status.setItem(ItemStackUtils.create(Material.PAPER, "§aStatus", shop.getStatus()));
        pagination.add(status);

        if (shop.getBuy() >= 0) {
            pagination.add(build(3, 1, true));
            if (size >= 8) pagination.add(build(2, 8, true));
            if (size >= 32) pagination.add(build(1, 32, true));
            if (shop.getAvailableToBuy() >= 64 && size > 32)
                pagination.add(build(0, Math.min(shop.getAvailableToBuy(), 2304), true));
        }

        if (shop.getSell() >= 0) {
            pagination.add(build(5, 1, false));
            if (size >= 8) pagination.add(build(6, 8, false));
            if (size >= 32) pagination.add(build(7, 32, false));
            if (shop.getAvailableToSell() >= 64 && size > 32)
                pagination.add(build(8, Math.min(shop.getAvailableToSell(), 2304), false));
        }
    }

    private ItemAction build(int index, int quantity, boolean buy) {
        BaseItemAction item = new BaseItemAction();

        item.setIndex(index);

        if (quantity <= 32) {
            item.setItem(ItemStackUtils.create(buy ? Material.RED_WOOL : Material.GREEN_WOOL,
                    quantity,
                    buy ? "§eComprar por " + doubleToString(shop.buyPriceTo(quantity))
                            : "§cVender por " + doubleToString(shop.sellPriceTo(quantity))));
        } else {
            item.setItem(ItemStackUtils.create(Material.CHEST,
                    "§a" + quantity + " " + (buy ? "§eComprar por " + doubleToString(shop.buyPriceTo(quantity))
                            : "§cVender por " + doubleToString(shop.sellPriceTo(quantity)))));
        }

        if (buy) item.setAction(() -> shop.buy(user(), quantity));
        else item.setAction(() -> shop.sell(user(), quantity));

        return item;
    }
}
