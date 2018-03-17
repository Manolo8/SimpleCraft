package com.github.manolo8.simplecraft.core.commands.inventory;

import com.github.manolo8.simplecraft.utils.ItemStackUtils;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public abstract class PaginationView extends BaseView {

    @Override
    public List<? extends ItemAction> getPagination() {
        List<ItemAction> pagination = new ArrayList<>();
        ItemActionImpl next = new ItemActionImpl();
        next.setItemStack(ItemStackUtils.create(Material.DIAMOND_SWORD, "§aPróximo"));
        next.setAction(user -> user.getInventoryView().nextPage());
        next.setIndex(5);
        pagination.add(next);

        ItemActionImpl previous = new ItemActionImpl();
        previous.setItemStack(ItemStackUtils.create(Material.BOW, "§cAnterior"));
        previous.setAction(user -> user.getInventoryView().previousPage());
        previous.setIndex(3);
        pagination.add(previous);

        return pagination;
    }
}
