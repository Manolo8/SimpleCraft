package com.github.manolo8.simplecraft.modules.user;

import com.github.manolo8.simplecraft.core.commands.inventory.ItemAction;
import com.github.manolo8.simplecraft.core.commands.inventory.ItemActionImpl;
import com.github.manolo8.simplecraft.core.commands.inventory.BaseView;
import com.github.manolo8.simplecraft.modules.plot.PlotView;
import com.github.manolo8.simplecraft.utils.ItemStackUtils;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public class UserView extends BaseView {

    private final User target;

    public UserView(User target) {
        this.target = target;
    }

    @Override
    public String getTitle() {
        return "§b" + target.getName();
    }

    @Override
    public List<ItemAction> getActions() {
        List<ItemAction> actions = new ArrayList<>();

        ItemActionImpl plots = new ItemActionImpl();
        plots.setAction(user -> getInventoryView().addView(new PlotView(target)));
        plots.setItemStack(ItemStackUtils.create(Material.GRASS, "§cPlots §b(" + target.getPlots().size() + ")"));
        plots.setIndex(38);
        actions.add(plots);

        ItemActionImpl money = new ItemActionImpl();
        money.setIndex(42);
        money.setItemStack(ItemStackUtils.create(Material.DIAMOND, "§cMoney §b(" + target.getMoney() + ")"));
        actions.add(money);

        return actions;
    }

    @Override
    public void addReference() {
        super.addReference();
        target.addReference();
    }

    @Override
    public void removeReference() {
        super.removeReference();
        target.removeReference();
    }
}
