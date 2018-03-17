package com.github.manolo8.simplecraft.domain.plot;

import com.github.manolo8.simplecraft.core.commands.inventory.*;
import com.github.manolo8.simplecraft.domain.plot.data.PlotInfo;
import com.github.manolo8.simplecraft.domain.user.User;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class PlotView extends PaginationView {

    private final User target;

    public PlotView(User target) {
        this.target = target;
    }

    @Override
    public String getTitle() {
        return "§b" + target.getName();
    }

    @Override
    public List<ItemAction> getActions() {
        List<ItemAction> actions = new ArrayList<>();

        for (final PlotInfo info : target.getPlots()) {
            ItemActionImpl itemActionImpl = new ItemActionImpl();
            itemActionImpl.setIndex(-1);

            ItemStack itemStack = new ItemStack(Material.DIRT);
            ItemMeta meta = itemStack.getItemMeta();
            meta.setDisplayName("§aPLOT - " + info.getId());
            itemStack.setItemMeta(meta);

            itemActionImpl.setItemStack(itemStack);

            itemActionImpl.setAction(user -> user.getInventoryView().addView(new ConfirmView(itemStack, user1 -> user1.teleport(info))));

            actions.add(itemActionImpl);
        }

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
