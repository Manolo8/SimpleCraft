package com.github.manolo8.simplecraft.core.commands.inventory;

import com.github.manolo8.simplecraft.utils.ItemStackUtils;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Uma view usada para confirmar a ação de um ItemAction
 * Caso o jogador confirme a ação, a ação do ItemAction será
 * executada, caso cancele, será executada um Back no InventoryView
 */
public class ConfirmView extends BaseView {

    private ItemStack itemStack;
    private Action action;

    public ConfirmView(ItemStack itemStack, Action action) {
        this.itemStack = itemStack;
        this.action = action;
    }

    @Override
    public String getTitle() {
        return "Confirmar";
    }

    @Override
    public List<? extends ItemAction> getActions() {
        List<ItemAction> actions = new ArrayList<>();

        ItemActionImpl confirm = new ItemActionImpl();
        confirm.setIndex(24);
        confirm.setItemStack(ItemStackUtils.create(Material.WOOL, DyeColor.LIME, "Confirmar"));
        confirm.setAction(action);
        actions.add(confirm);

        ItemActionImpl cancel = new ItemActionImpl();
        cancel.setIndex(20);
        cancel.setItemStack(ItemStackUtils.create(Material.WOOL, DyeColor.RED, "Cancelar"));
        cancel.setAction(user -> user.getInventoryView().back());
        actions.add(cancel);

        //Cria um item fake para ficar no lugar do item a ser confirmado
        ItemActionImpl fakeItem = new ItemActionImpl();
        fakeItem.setItemStack(itemStack);
        fakeItem.setIndex(13);
        fakeItem.setAction(user -> user.playSound(Sound.BLOCK_ANVIL_LAND, 20, 20));
        actions.add(fakeItem);

        return actions;
    }
}
