package com.github.manolo8.simplecraft.module.money.view;

import com.github.manolo8.simplecraft.core.commands.inventory.ItemAction;
import com.github.manolo8.simplecraft.utils.mc.ItemStackUtils;
import org.bukkit.Material;

import java.util.List;

public class CashVipView extends BaseCashView {

    @Override
    public int size() {
        return 27;
    }

    @Override
    public String getTitle() {
        return "CASH - VIP";
    }

    @Override
    public void createActions(List<ItemAction> actions) {

        actions.add(new CashCommandAction()
                .setCommand("group user $ set vip 1d true")
                .setCost(10)
                .setMessage("§aO VIP foi adquirido com sucesso!")
                .setItem(ItemStackUtils.create(Material.GOLD_BLOCK, "§aVIP 1 DIA","§e10 CASH"))
                .setIndex(2, 3));

        actions.add(new CashCommandAction()
                .setCommand("group user $ set vip 7d true")
                .setCost(60)
                .setMessage("§aO VIP foi adquirido com sucesso!")
                .setItem(ItemStackUtils.create(Material.GOLD_BLOCK, "§aVIP 7 DIAS","§e60 CASH"))
                .setIndex(2, 5));

                actions.add(new CashCommandAction()
                .setCommand("group user $ set vip 30d true")
                .setCost(200)
                .setMessage("§aO VIP foi adquirido com sucesso!")
                .setItem(ItemStackUtils.create(Material.GOLD_BLOCK, "§aVIP 30 DIAS","§e200 CASH"))
                .setIndex(2, 7));

    }
}
