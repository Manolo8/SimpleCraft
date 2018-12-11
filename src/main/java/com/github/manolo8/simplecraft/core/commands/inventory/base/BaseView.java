package com.github.manolo8.simplecraft.core.commands.inventory.base;

import com.github.manolo8.simplecraft.core.commands.inventory.*;
import com.github.manolo8.simplecraft.utils.mc.ItemStackUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseView extends AbstractView {

    private int page;

    protected BaseView() {
        page = 0;
    }

    @Override
    public int size() {
        return 45;
    }

    public void nextPage() {
        page++;

        main.update();
    }

    public void previousPage() {
        if (page != 0) {
            page--;

            main.update();
        }
    }

    public int getPage() {
        return page;
    }

    @Override
    public Handler createHandler() {
        return new DefaultHandler();
    }

    public void createActions(List<ItemAction> actions) throws SQLException {
    }

    public void createPagination(List<ItemAction> pagination) throws SQLException {
    }

    public void close() {

    }

    @Override
    public void tick() {

    }

    //Separated to garbage collector
    class DefaultHandler implements Handler<BaseView> {

        private InventoryView main;
        private BaseView view;
        private boolean s1, s2;
        private ItemAction[] actions;
        private Inventory inventory;
        private int size;

        @Override
        public void prepare(InventoryView main, BaseView view) {

            this.main = main;
            this.view = view;
            actions = new ItemAction[(this.size = view.size()) + 9];

            inventory = Bukkit.createInventory(null, view.size() + 9, view.getTitle());
        }

        @Override
        public void open() {
            main.user().base().openInventory(inventory);
        }

        @Override
        public void click(int index, ItemStack cursor, boolean left) {
            try {
                ItemAction itemAction;

                if (index < actions.length && index >= 0) {
                    itemAction = actions[index];
                } else {
                    itemAction = null;
                    main.back();
                }

                if (itemAction != null && itemAction.getAction() != null) {

                    Action action = itemAction.getAction();

                    if (action instanceof Action.Info) {
                        ((Action.Info) action).click(new ClickInfo(cursor, left));
                    } else {
                        action.click();
                    }

                }

            } catch (SQLException e) {
                main.user().sendAction("§cHouve um erro interno. Contate um ADMIN!");
                main.close(true);
                e.printStackTrace();
            }
        }

        @Override
        public void update(boolean items, boolean pagination) {
            List<ItemAction> temp = new ArrayList<>();

            int mid;
            int total;

            try {

                if (items) {
                    view.createActions(temp);
                }

                mid = temp.size();

                if (pagination) {
                    view.createPagination(temp);

                    if (main.getViews().size() != 1) {
                        BaseItemAction back = new BaseItemAction();
                        back.setItem(ItemStackUtils.create(Material.FEATHER, "§cVOLTAR"));
                        back.setIndex(0);
                        back.setAction(() -> main.back());
                        temp.add(back);
                    }
                }

                total = temp.size();

            } catch (Exception e) {
                main.close(true);
                main.user().sendAction("§cHouve um erro interno. Contate um ADMIN!");
                e.printStackTrace();
                return;
            }


            if (items && mid != 0) {

                s1 = !s1;

                for (int i = 0; i < mid; i++) {
                    ItemAction action = temp.get(i);

                    if (action.index() == -1) {
                        action.setIndex(i);
                    }

                    if (size > action.index()) {
                        ItemAction now = actions[action.index()];

                        if (now == null || !now.getItemStack().equals(action.getItemStack())) {
                            inventory.setItem(action.index(), action.getItemStack());
                        }

                        actions[action.index()] = action;
                        action.state = s1;
                    }

                }

                for (int i = 0; i < size; i++) {
                    ItemAction action = actions[i];

                    if (action != null && action.state != s1) {
                        actions[i] = null;
                        inventory.setItem(i, null);
                    }
                }

            }

            if (pagination && total != mid) {

                s2 = !s2;

                for (int i = mid; i < total; i++) {
                    ItemAction action = temp.get(i);

                    int index = action.index();

                    if (index < 0 || index > 8) {
                        continue;
                    }

                    index += size;

                    ItemAction now = actions[index];

                    if (now == null || !now.getItemStack().equals(action.getItemStack())) {
                        inventory.setItem(index, action.getItemStack());
                    }

                    actions[index] = action;
                    action.state = s2;
                }

                for (int i = size; i < size + 9; i++) {
                    ItemAction action = actions[i];

                    if (action != null && action.state != s2) {
                        actions[i] = null;
                        inventory.setItem(i, null);
                    }
                }

            }
        }

    }
}