package com.github.manolo8.simplecraft.core.commands.inventory;

import com.github.manolo8.simplecraft.modules.user.User;
import com.github.manolo8.simplecraft.utils.ItemStackUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;

public class InventoryView {

    private User user;
    private Inventory inventory;
    private List<ItemAction> temp;
    private List<ItemAction> pagination;

    private List<View> views;
    private int current;


    public InventoryView() {
        this.pagination = new ArrayList<>();
        this.views = new ArrayList<>();
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void nextPage() {
        if (getCurrent().nextPage()) {
            update();
            return;
        }
        user.playSound(Sound.BLOCK_ANVIL_LAND, 20, 20);
    }

    public void previousPage() {
        if (getCurrent().previousPage()) {
            update();
            return;
        }
        user.playSound(Sound.BLOCK_ANVIL_LAND, 20, 20);
    }

    public void open() {
        inventory = Bukkit.createInventory(null, 54, "§aMenu - §bSimpleCraft");

        user.getBase().openInventory(inventory);
    }

    public void close(boolean force) {
        for (View view : views) view.removeReference();

        if (force) user.getBase().getOpenInventory().close();
    }

    public void addView(View view) {
        view.setInventoryView(this);
        this.views.add(view);
        view.addReference();
        current = this.views.size() - 1;
        pagination();
        update();
    }

    private View getCurrent() {
        return views.get(current);
    }

    /**
     * Volta para o último view
     * Caso seja o último view, fecha o inventário
     */
    public void back() {
        int size = views.size();

        if (size == 1) {
            getUser().getBase().closeInventory();
            return;
        }

        //A view é removida, removemos a referencia
        getCurrent().removeReference();

        this.views = views.subList(0, size - 1);

        current = size - 2;
        pagination();
        update();
    }

    private void pagination() {
        clearPagination();

        pagination.addAll(getCurrent().getPagination());

        if (views.size() > 1) {
            ItemActionImpl back = new ItemActionImpl();
            back.setItemStack(ItemStackUtils.create(Material.COMPASS, "§cVOLTAR"));
            back.setIndex(0);
            back.setAction(user -> back());
            pagination.add(back);
        }

        for (ItemAction action : pagination)
            inventory.setItem(45 + action.getIndex(), action.getItemStack());
    }


    private void update() {
        clear();
        user.playSound(Sound.ENTITY_PLAYER_BURP, 20, 20);
        View view = getCurrent();
        temp = (List<ItemAction>) view.getActions();

        int iv = 0;
        for (int i = (45 * (view.getPage() - 1)); i < temp.size(); i++) {
            ItemAction action = temp.get(i);

            int index = (action.getIndex() == -1 ? i : action.getIndex());

            inventory.setItem(index, action.getItemStack());
            action.setIndex(index);
            iv++;
            if (iv == 45) break;
        }
    }

    public void handleClick(int index) {
        if (index > 44) {
            index = index % 45;
            for (ItemAction action : pagination)
                if (action.getIndex() == index) {
                    action.getAction().doAction(user);
                    break;
                }
            return;
        }


        index *= getCurrent().getPage();

        //Se não for, pega o valor e executa a ação
        for (ItemAction action : temp)
            if (action.getIndex() == index)
                if (action.getAction() != null) {
                    action.getAction().doAction(user);
                    break;
                }
    }

    private void clear() {
        for (int i = 0; i < 45; i++)
            inventory.setItem(i, null);
    }

    private void clearPagination() {
        this.pagination.clear();
        for (int i = 45; i < 54; i++)
            inventory.setItem(i, null);
    }
}
