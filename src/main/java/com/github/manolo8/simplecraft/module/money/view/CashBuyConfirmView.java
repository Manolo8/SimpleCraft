package com.github.manolo8.simplecraft.module.money.view;

import com.github.manolo8.simplecraft.core.commands.inventory.Action;
import com.github.manolo8.simplecraft.core.commands.inventory.ItemAction;
import com.github.manolo8.simplecraft.core.commands.inventory.base.BaseItemAction;
import com.github.manolo8.simplecraft.utils.mc.ItemStackUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;
import java.util.List;

public class CashBuyConfirmView extends BaseCashView {

    private final ItemStack example;
    private final Action confirm;

    public CashBuyConfirmView(ItemStack example, Action confirm) {
        this.example = example;
        this.confirm = confirm;
    }

    @Override
    public int size() {
        return 9;
    }

    @Override
    public String getTitle() {
        return "CASH - Confirmar compra";
    }

    @Override
    public void createActions(List<ItemAction> actions) {
        actions.add(new BaseItemAction(example).setIndex(1, 5));

        ItemStack green = ItemStackUtils.create(Material.GREEN_WOOL, "§aCONFIRMAR");
        ItemStack red = ItemStackUtils.create(Material.RED_WOOL, "§cCANCELAR");

        for (int i = 0; i < 5; i++) {
            actions.add(new BaseItemAction(green).setAction(confirm).setIndex(1, i));
        }

        for (int i = 6; i < 10; i++) {
            actions.add(new BaseItemAction(red).setAction(() -> getMain().back()).setIndex(1, i));
        }
    }
}
