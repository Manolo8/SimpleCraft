package com.github.manolo8.simplecraft.module.market.view;

import com.github.manolo8.simplecraft.core.commands.inventory.ItemAction;
import com.github.manolo8.simplecraft.core.commands.inventory.base.BaseItemAction;
import com.github.manolo8.simplecraft.core.commands.inventory.base.BaseView;
import com.github.manolo8.simplecraft.module.market.MarketItem;
import com.github.manolo8.simplecraft.module.money.Money;
import com.github.manolo8.simplecraft.utils.mc.ItemStackUtils;
import org.bukkit.Material;

import java.util.List;

import static com.github.manolo8.simplecraft.utils.def.StringUtils.doubleToString;

public class MarketItemView extends BaseView {

    private final MarketItem marketItem;

    public MarketItemView(MarketItem marketItem) {
        this.marketItem = marketItem;
    }

    @Override
    public int size() {
        return 27;
    }

    @Override
    public String getTitle() {
        return "Mercado - Item de " + marketItem.getOwner().getIdentity().getName();
    }

    @Override
    public void createActions(List<ItemAction> actions) {

        final Money money = marketItem.getOwner();

        BaseItemAction itemStats = new BaseItemAction();
        itemStats.setItem(marketItem.getItem());
        itemStats.setIndex(2, 5);
        actions.add(itemStats);

        BaseItemAction itemOwner = new BaseItemAction();
        itemOwner.setIndex(1, 5);
        itemOwner.setItem(ItemStackUtils.createSkullByIdentity(money.getIdentity(), "§e" + money.getIdentity().getName(), "§eCoins: " + money.getCoinsFormatted()));
        actions.add(itemOwner);
    }

    @Override
    public void createPagination(List<ItemAction> pagination) {
        if (marketItem.getOwner() == user().money()) {
            pagination.add(new BaseItemAction()
                    .setItem(ItemStackUtils.create(Material.GREEN_WOOL, "§eRetirar"))
                    .setAction(() -> {
                        if (marketItem.collect(user())) getMain().back();
                    })
                    .setIndex(4));

        } else {
            pagination.add(new BaseItemAction()
                    .setItem(ItemStackUtils.create(Material.YELLOW_WOOL, "§eComprar por " + doubleToString(marketItem.getCost())))
                    .setAction(() -> {
                        if (marketItem.buy(user())) getMain().back();
                    })
                    .setIndex(4));
        }
    }

    @Override
    public void tick() {
        if (marketItem.isRemoved()) {
            getMain().back();
        }
    }
}
