package com.github.manolo8.simplecraft.module.warp;

import com.github.manolo8.simplecraft.core.commands.inventory.ItemAction;
import com.github.manolo8.simplecraft.core.commands.inventory.base.BaseItemAction;
import com.github.manolo8.simplecraft.core.commands.inventory.base.BaseView;
import com.github.manolo8.simplecraft.core.world.WorldService;
import com.github.manolo8.simplecraft.core.world.container.IContainer;
import com.github.manolo8.simplecraft.utils.location.SimpleLocation;
import com.github.manolo8.simplecraft.utils.mc.ItemStackUtils;
import org.bukkit.Material;

import java.util.List;

public class WarpView extends BaseView {

    private final WarpService warpService;

    public WarpView(WarpService warpService) {
        this.warpService = warpService;
    }

    @Override
    public int size() {
        return 27;
    }

    @Override
    public String getTitle() {
        return "Warps";
    }

    @Override
    public void createActions(List<ItemAction> actions) {

        int rank = user().rank().get();

        for (Warp warp : warpService.getEntities()) {
            BaseItemAction warpAction = new BaseItemAction();

            warpAction.setIndex(warp.getSlot());

            //Player size handler
            SimpleLocation loc = warp.getLocation();
            IContainer iContainer = warp.getWorldInfo().getContainer().update(loc.getX(), loc.getY(), loc.getZ());

            int players = 1;
            String online = "§eJogadores: " + (iContainer == null ? 0 : 0);

            //Player size handler

            if (rank >= warp.getMinRank()) {
                warpAction.setItem(ItemStackUtils.create(warp.getIcon(), players, "§e" + warp.getName(), online));
                warpAction.setAction(() -> {
                    user().teleport(warp);
                    getMain().close(true);
                });
            } else {
                warpAction.setItem(ItemStackUtils.create(Material.BLACK_WOOL, players, "§e" + warp.getName() + " (bloqueado)", online));
            }

            actions.add(warpAction);
        }

    }

    @Override
    public void tick() {
        if (WorldService.tick % 40 == 1) {
            getMain().update();
        }
    }
}
