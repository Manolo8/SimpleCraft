package com.github.manolo8.simplecraft.module.market.view;

import com.github.manolo8.simplecraft.core.commands.inventory.ItemAction;
import com.github.manolo8.simplecraft.core.commands.inventory.base.BaseItemAction;
import com.github.manolo8.simplecraft.core.commands.inventory.base.BaseView;
import com.github.manolo8.simplecraft.module.market.MarketItem;
import com.github.manolo8.simplecraft.module.market.MarketItemRepository;
import com.github.manolo8.simplecraft.module.market.MarketService;
import com.github.manolo8.simplecraft.utils.def.StringUtils;
import com.github.manolo8.simplecraft.utils.mc.ItemStackUtils;

import java.sql.SQLException;
import java.util.List;

public class MarketOwnerView extends BaseView {

    private final MarketService service;

    public MarketOwnerView(MarketService service) {
        this.service = service;
    }

    @Override
    public int size() {
        return 36;
    }

    @Override
    public String getTitle() {
        return "Mercado - Seus itens :)";
    }


    @Override
    public void createActions(List<ItemAction> actions) throws SQLException {
        List<MarketItem> items = service.findByIdentity(user().identity(), getPage());

        int index = 0;

        for (MarketItem marketItem : items) {

            actions.add(new BaseItemAction()
                    .setIndex(index++)
                    .setItem(ItemStackUtils.changeLore(marketItem.getItem().clone(),
                            "§ePreço: " + StringUtils.doubleToString(marketItem.getCost()),
                            "§aExpirado: " + (marketItem.getCreation() + MarketItemRepository.EXPIRATION_TIME < System.currentTimeMillis() ? "§cSIM" : "NÃO")))
                    .setAction(() -> getMain().add(new MarketItemView(marketItem))));
        }
    }
}
