package com.github.manolo8.simplecraft.module.kit.view;

import com.github.manolo8.simplecraft.core.commands.inventory.ItemAction;
import com.github.manolo8.simplecraft.core.commands.inventory.base.BaseItemAction;
import com.github.manolo8.simplecraft.core.commands.inventory.base.BaseView;
import com.github.manolo8.simplecraft.module.kit.Kit;
import com.github.manolo8.simplecraft.module.kit.item.KitItem;
import com.github.manolo8.simplecraft.module.kit.user.delay.KitDelay;
import com.github.manolo8.simplecraft.module.user.MessageType;
import org.bukkit.Material;

import java.sql.SQLException;
import java.util.List;

import static com.github.manolo8.simplecraft.utils.def.StringUtils.longTimeToString;
import static com.github.manolo8.simplecraft.utils.mc.ItemStackUtils.create;

public class KitInfoView extends BaseView {

    private final Kit kit;

    public KitInfoView(Kit kit) {
        this.kit = kit;
    }

    @Override
    public int size() {
        return ((kit.getItems().size() / 9) + 1) * 9;
    }

    @Override
    public String getTitle() {
        return "Kit (" + kit.getName().toUpperCase() + ")";
    }

    @Override
    public void createActions(List<ItemAction> actions) {

        for (KitItem item : kit.getItems()) {
            actions.add(new BaseItemAction(item.getItem()).setIndex(-1));
        }

    }

    @Override
    public void createPagination(List<ItemAction> pagination) throws SQLException {
        KitDelay delay = user().kit().getDelay(kit);

        if (kit.canUse(user())) {

            if (delay.canUse()) {
                pagination.add(new BaseItemAction(create(Material.REDSTONE, "§eClique para usar!"))
                        .setAction(() -> {
                            if (kit.canUse(user()) && delay.use()) {
                                kit.giveTo(user());
                                user().sendMessage(MessageType.SUCCESS, "Kit recebido!");
                                getMain().close(true);
                            }
                        })
                        .setIndex(4));
            } else {
                pagination.add(new BaseItemAction(create(Material.PAPER, "§eEM CD", "§cEspere " + longTimeToString(delay.getWaitTime()) + "!")).setIndex(4));
            }

        } else {
            pagination.add(new BaseItemAction(create(Material.BLACK_WOOL, "§eBLOQUEADO")).setIndex(4));
        }
    }
}
