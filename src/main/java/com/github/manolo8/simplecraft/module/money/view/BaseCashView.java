package com.github.manolo8.simplecraft.module.money.view;

import com.github.manolo8.simplecraft.core.commands.inventory.Action;
import com.github.manolo8.simplecraft.core.commands.inventory.ItemAction;
import com.github.manolo8.simplecraft.core.commands.inventory.base.BaseItemAction;
import com.github.manolo8.simplecraft.core.commands.inventory.base.BaseView;
import com.github.manolo8.simplecraft.utils.mc.InventoryUtils;
import com.github.manolo8.simplecraft.utils.mc.ItemStackUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.Inventory;

import java.util.List;

public abstract class BaseCashView extends BaseView {

    @Override
    public void createPagination(List<ItemAction> pagination) {
        pagination.add(new BaseItemAction(ItemStackUtils.create(Material.NETHER_STAR, "§eCASH " + user().money().getCashFormatted())).setIndex(4));
    }

    protected class CashCommandAction extends BaseItemAction {

        private String command;
        private String message;
        private double cost;

        public CashCommandAction setCommand(String command) {
            this.command = command;
            return this;
        }

        public CashCommandAction setMessage(String message) {
            this.message = message;
            return this;
        }

        public CashCommandAction setCost(double cost) {
            this.cost = cost;
            return this;
        }

        @Override
        public Action getAction() {
            return () -> getMain().add(new CashBuyConfirmView(getItemStack(), (() -> {
                if (user().money().withdrawCash(cost)) {
                    Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), command.replace("$", user().identity().getName()));
                    user().sendAction(message);
                } else {
                    user().playSound(Sound.BLOCK_ANVIL_LAND, 20, 20, true);
                }
                getMain().close(true);
            })));
        }
    }

    protected class CashGiveItemAction extends BaseItemAction {

        private String message;
        private double cost;

        public CashGiveItemAction setMessage(String message) {
            this.message = message;
            return this;
        }

        public CashGiveItemAction setCost(double cost) {
            this.cost = cost;
            return this;
        }

        @Override
        public Action getAction() {
            return () -> getMain().add(new CashBuyConfirmView(getItemStack(), () -> {
                if (user().money().hasCash(cost)) {

                    Inventory inventory = user().base().getInventory();

                    if (InventoryUtils.isFull(inventory, getItemStack())) {
                        user().sendAction("§cNão foi possível comprar: Inventário cheio.");
                    } else {
                        //Recheck '-'
                        if (user().money().withdrawCash(cost))
                            InventoryUtils.addItemTo(inventory, getItemStack());

                        user().sendAction(message);
                    }

                } else {
                    user().playSound(Sound.BLOCK_ANVIL_LAND, 20, 20, true);
                }
                getMain().close(true);
            }));
        }
    }
}
