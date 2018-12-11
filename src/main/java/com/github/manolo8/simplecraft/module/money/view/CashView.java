package com.github.manolo8.simplecraft.module.money.view;

import com.github.manolo8.simplecraft.core.commands.inventory.Action;
import com.github.manolo8.simplecraft.core.commands.inventory.ItemAction;
import com.github.manolo8.simplecraft.core.commands.inventory.base.BaseItemAction;
import com.github.manolo8.simplecraft.core.commands.inventory.base.BaseView;
import com.github.manolo8.simplecraft.utils.mc.ItemStackUtils;
import org.bukkit.Material;
import org.bukkit.Sound;

import java.util.List;

public class CashView extends BaseView {

    @Override
    public int size() {
        return 27;
    }

    @Override
    public String getTitle() {
        return "CASH (" + user().money().getCashFormatted() + ")";
    }

    @Override
    public void createActions(List<ItemAction> actions) {

        actions.add(new BaseItemAction(ItemStackUtils.create(Material.DIAMOND, "§aVIP"))
                .setAction(() -> getMain().add(new CashVipView()))
                .setIndex(2, 5));

    }

    @Override
    public void createPagination(List<ItemAction> pagination) {

        BaseItemAction add = new BaseItemAction();
        add.setItem(ItemStackUtils.create(Material.HOPPER, "§aTrocar estrelas por CASH!", "§e1 estrela = 3 cash"));
        add.setAction((Action.Info) (info) -> {
            if (info.getCursor().getType() != Material.NETHER_STAR) {
                user().playSound(Sound.BLOCK_ANVIL_LAND, 20, 20, false);
            } else {
                user().money().depositCash(info.getCursor().getAmount() * 3);
                user().playSound(Sound.ENTITY_PLAYER_LEVELUP, 20, 20, false);
                info.getCursor().setAmount(0);

                //UPDATE TITLE
                getMain().open();
            }
        });

        add.setIndex(4);

        pagination.add(add);
    }
}
