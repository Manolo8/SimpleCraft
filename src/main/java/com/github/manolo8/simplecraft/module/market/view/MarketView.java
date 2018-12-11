package com.github.manolo8.simplecraft.module.market.view;

import com.github.manolo8.simplecraft.core.commands.inventory.ItemAction;
import com.github.manolo8.simplecraft.core.commands.inventory.base.BaseItemAction;
import com.github.manolo8.simplecraft.core.commands.inventory.base.BaseView;
import com.github.manolo8.simplecraft.module.market.MarketCategory;
import com.github.manolo8.simplecraft.module.market.MarketService;
import com.github.manolo8.simplecraft.utils.mc.ItemStackUtils;

import java.sql.SQLException;
import java.util.List;

public class MarketView extends BaseView {

    private final MarketService service;

    public MarketView(MarketService service) {
        this.service = service;
    }

    @Override
    public int size() {
        return 36;
    }

    @Override
    public String getTitle() {
        return "Mercado";
    }

    @Override
    public void createActions(List<ItemAction> actions) throws SQLException {

        int index = 0;

        for (MarketCategory category : MarketCategory.values()) {

            actions.add(new BaseItemAction()
                    .setAction(() -> getMain().add(new MarketCategoryView(service, category)))
                    .setItem(ItemStackUtils.create(category.material, Math.max(1, service.countByCategory(category)), "§e" + category.name))
                    .setIndex(10 + (index / 7) * 9 + index++ % 7));
        }
    }

    @Override
    public void createPagination(List<ItemAction> pagination) {
        pagination.add(new BaseItemAction()
                .setIndex(4)
                .setItem(ItemStackUtils.createSkullByIdentity(user().identity(), "§aVer seus ítens"))
                .setAction(() -> getMain().add(new MarketOwnerView(service))));
    }
}
