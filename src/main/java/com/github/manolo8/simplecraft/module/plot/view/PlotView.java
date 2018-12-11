package com.github.manolo8.simplecraft.module.plot.view;

import com.github.manolo8.simplecraft.core.commands.inventory.ItemAction;
import com.github.manolo8.simplecraft.core.commands.inventory.base.BaseItemAction;
import com.github.manolo8.simplecraft.core.commands.inventory.base.BaseView;
import com.github.manolo8.simplecraft.module.plot.Plot;
import com.github.manolo8.simplecraft.module.plot.user.PlotUser;
import com.github.manolo8.simplecraft.module.user.UserService;
import com.github.manolo8.simplecraft.utils.mc.ItemStackUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class PlotView extends BaseView {

    private final PlotUser plotUser;

    public PlotView(PlotUser plotUser) {
        this.plotUser = plotUser;
    }

    @Override
    public String getTitle() {
        return "Plots (" + plotUser.getIdentity().getName() + ")";
    }

    @Override
    public int size() {
        return 18;
    }

    @Override
    public void createActions(List<ItemAction> actions) {
        if (plotUser.getPlots().size() == 0) {

            actions.add(new BaseItemAction(ItemStackUtils.create(Material.BUCKET, "§cO jogador não tem PLOT!")).setIndex(2, 5));

        } else {
            for (Plot info : plotUser.getPlots()) {
                BaseItemAction baseItemAction = new BaseItemAction();
                baseItemAction.setIndex(-1);

                boolean canEntry = info.canEnter(user());

                Material material = canEntry ? Material.GRASS : Material.NETHERRACK;

                ItemStack itemStack = ItemStackUtils.create(material, info.getName(),
                        "§eChunks carregadas: " + info.proxies.size() + "/9",
                        "§eJogadores: " + 0,
                        "§eEntrada liberada: " + (canEntry ? "sim" : "§cnão"));

                baseItemAction.setItem(itemStack);

                baseItemAction.setAction(() -> user().teleport(info));

                actions.add(baseItemAction);
            }
        }
    }

    @Override
    public void tick() {
        if (UserService.tick % 20 == 15) {
            getMain().update();
        }
    }
}
