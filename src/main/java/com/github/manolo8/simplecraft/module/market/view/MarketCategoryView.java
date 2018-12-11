package com.github.manolo8.simplecraft.module.market.view;

import com.github.manolo8.simplecraft.core.commands.inventory.ItemAction;
import com.github.manolo8.simplecraft.core.commands.inventory.base.BaseItemAction;
import com.github.manolo8.simplecraft.core.commands.inventory.base.BaseView;
import com.github.manolo8.simplecraft.module.market.MarketCategory;
import com.github.manolo8.simplecraft.module.market.MarketItem;
import com.github.manolo8.simplecraft.module.market.MarketService;
import com.github.manolo8.simplecraft.module.user.UserService;
import com.github.manolo8.simplecraft.utils.def.StringUtils;
import com.github.manolo8.simplecraft.utils.mc.ItemStackUtils;
import org.bukkit.Material;

import java.sql.SQLException;
import java.util.List;

public class MarketCategoryView extends BaseView {

    private final MarketService service;
    private MarketCategory category;

    public MarketCategoryView(MarketService service, MarketCategory category) {
        this.service = service;
        this.category = category;
    }

    @Override
    public int size() {
        return 36;
    }

    @Override
    public String getTitle() {
        return "Mercado - " + category.name;
    }

    @Override
    public void createActions(List<ItemAction> actions) throws SQLException {

        List<MarketItem> items = service.findByCategory(category, getPage());

        int index = 0;

        for (MarketItem marketItem : items) {

            actions.add(new BaseItemAction()
                    .setIndex(index++)
                    .setItem(ItemStackUtils.changeLore(marketItem.getItem().clone(),
                            "§ePreço: " + StringUtils.doubleToString(marketItem.getCost()),
                            "§eDono: " + marketItem.getOwner().getIdentity().getName(),
                            "§eCriado há " + StringUtils.longTimeToString(System.currentTimeMillis() - marketItem.getCreation())))
                    .setAction(() -> getMain().add(new MarketItemView(marketItem))));
        }
    }

    @Override
    public void createPagination(List<ItemAction> pagination) {
        pagination.add(new BaseItemAction()
                .setItem(ItemStackUtils.create(Material.IRON_AXE, "§cAnterior"))
                .setAction(this::previousPage)
                .setIndex(3));

        pagination.add(new BaseItemAction()
                .setItem(ItemStackUtils.create(Material.CLOCK, "§eAtualizar"))
                .setAction(() -> getMain().update())
                .setIndex(4));

        pagination.add(new BaseItemAction()
                .setItem(ItemStackUtils.create(Material.IRON_SWORD, "§aPróximo"))
                .setAction(this::nextPage)
                .setIndex(5));

        MarketCategory next = category.next();
        MarketCategory back = category.back();

        pagination.add(new BaseItemAction()
                .setItem(ItemStackUtils.create(back.material, "§a" + back.name))
                .setAction(() -> {
                    this.category = back;
                    getMain().backAndAdd(this);
                })
                .setIndex(7));

        pagination.add(new BaseItemAction()
                .setItem(ItemStackUtils.create(next.material, "§a" + next.name))
                .setAction(() -> {
                    this.category = next;
                    getMain().backAndAdd(this);
                })
                .setIndex(8));
    }

    @Override
    public void tick() {
        super.tick();

        if (UserService.tick % 5 == 1) {
            getMain().update();
        }
    }
}
