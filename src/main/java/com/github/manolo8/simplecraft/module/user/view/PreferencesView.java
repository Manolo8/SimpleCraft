package com.github.manolo8.simplecraft.module.user.view;

import com.github.manolo8.simplecraft.core.commands.inventory.ItemAction;
import com.github.manolo8.simplecraft.core.commands.inventory.base.BaseItemAction;
import com.github.manolo8.simplecraft.core.commands.inventory.base.BaseView;
import com.github.manolo8.simplecraft.module.money.view.CashView;
import com.github.manolo8.simplecraft.module.plot.view.PlotView;
import com.github.manolo8.simplecraft.module.user.User;
import com.github.manolo8.simplecraft.utils.def.Flag;
import com.github.manolo8.simplecraft.utils.mc.ItemStackUtils;
import org.bukkit.Material;

import java.util.List;

public class PreferencesView extends BaseView {

    @Override
    public int size() {
        return 27;
    }

    @Override
    public String getTitle() {
        return "Preferências";
    }

    @Override
    public void createActions(List<ItemAction> actions) {

        int current = 11;

        for (Flag.Toggle toggle : user().flags().getTogglers()) {

            boolean active = toggle.isTrue(user().flags());

            actions.add(new BaseItemAction()
                    .setItem(ItemStackUtils.create(active ? Material.GREEN_WOOL : Material.RED_WOOL, "§a" + toggle.getDescription()))
                    .setIndex(current++)
                    .setAction(() -> {
                        toggle.set(user(), User::flags, !active);
                        getMain().update();
                    }));
        }
    }

    @Override
    public void createPagination(List<ItemAction> pagination) {

        pagination.add(new BaseItemAction()
                .setItem(ItemStackUtils.create(Material.GRASS, "§aPlots"))
                .setAction(() -> getMain().add(new PlotView(user().plot())))
                .setIndex(3));

        pagination.add(new BaseItemAction()
                .setItem(ItemStackUtils.create(Material.NETHER_STAR, "§aCash"))
                .setAction(() -> getMain().add(new CashView()))
                .setIndex(4));

        pagination.add(new BaseItemAction()
                .setItem(ItemStackUtils.create(Material.CHEST, "§aAutoSellList", "§eAlterar itens vendidos automaticamente"))
                .setAction(() -> getMain().add(new SellListView()))
                .setIndex(5));
    }
}
