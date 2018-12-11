package com.github.manolo8.simplecraft.module.shop.view;

import com.github.manolo8.simplecraft.core.commands.inventory.ItemAction;
import com.github.manolo8.simplecraft.core.commands.inventory.base.BaseItemAction;
import com.github.manolo8.simplecraft.core.commands.inventory.base.BaseView;
import com.github.manolo8.simplecraft.module.shop.Shop;
import com.github.manolo8.simplecraft.utils.mc.InventoryUtils;
import com.github.manolo8.simplecraft.utils.mc.ItemStackUtils;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;

import java.util.List;

public class ShopOwnerView extends BaseView {

    private Shop shop;

    public ShopOwnerView(Shop shop) {
        this.shop = shop;
    }

    @Override
    public int size() {
        return 27;
    }

    @Override
    public String getTitle() {
        return "Shop (DONO)";
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
        int stock = shop.getStock();

        Inventory inventory = user().base().getInventory();

        int available = InventoryUtils.getItemQuantity(inventory, shop.getItem());
        int space = InventoryUtils.getFreeSpace(inventory, shop.getItem());


        BaseItemAction status = new BaseItemAction();
        status.setIndex(4);
        status.setItem(ItemStackUtils.create(Material.PAPER, "§aStatus", shop.getStatus()));
        pagination.add(status);

        if (available > 0) pagination.add(build(3, 1, true));
        if (available >= 8) pagination.add(build(2, 8, true));
        if (available >= 32) pagination.add(build(1, 32, true));
        if (available > 0) pagination.add(build(0, Math.min(available, 2304), true));

        if (stock > 0 && space > 0) pagination.add(build(5, 1, false));
        if (stock >= 8 && space >= 8) pagination.add(build(6, 8, false));
        if (stock >= 32 && space >= 32) pagination.add(build(7, 32, false));
        if (stock > 0 && space > 0) pagination.add(build(8, Math.min(stock, 2304), false));

    }

    private ItemAction build(int index, int quantity, boolean add) {
        BaseItemAction item = new BaseItemAction();

        item.setIndex(index);

        item.setItem(ItemStackUtils.create(add ? Material.HOPPER_MINECART : Material.CHEST_MINECART, (add ? "§eEstocar " : "§cColetar ") + quantity));

        if (add) item.setAction(() -> {
            shop.stock(user(), quantity);
            getMain().updateAll();
        });
        else item.setAction(() -> {
            shop.collect(user(), quantity);
            getMain().updateAll();
        });

        return item;
    }
}
