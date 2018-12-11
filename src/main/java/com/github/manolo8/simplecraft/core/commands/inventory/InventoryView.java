package com.github.manolo8.simplecraft.core.commands.inventory;

import com.github.manolo8.simplecraft.module.user.User;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class InventoryView {

    private final User user;
    private final List<View> views;
    public boolean opening;

    private View current;
    private Handler handler;

    public InventoryView(User user) {
        this.views = new ArrayList<>();
        this.user = user;
    }

    //======================================================
    //=====================ENCAPSULATION====================
    //======================================================
    public User user() {
        return user;
    }

    public Handler getHandler() {
        return handler;
    }

    public List<View> getViews() {
        return views;
    }
    //======================================================
    //=====================ENCAPSULATION====================
    //======================================================

    public void open() {
        open(null);
    }

    public void open(Handler restore) {

        handler = restore == null ? current.createHandler() : restore;
        handler = current.createHandler();

        handler.prepare(this, current);


        opening = true;
        handler.open();
        opening = false;

        updateAll();

    }

    public void close(boolean force) {
        for (View view : views) {
            view.close();
        }

        if (force) {
            user.base().getOpenInventory().close();
        }
    }

    public void add(View view) {

        //Checa quantas views já estão aberta, se for > 100, então fecha
        //O jogador deve esta tentando testar o sistema
        if (views.size() > 100) {
            close(true);
            user.sendAction("§cO seu histórico está muito grande :0");
            return;
        }

        view.setMain(this);

        this.views.add(view);

        current = views.get(this.views.size() - 1);

        open();
    }

    public void back() {
        back(null);
    }

    /**
     * Volta para o último view
     * Caso seja o último view, fecha o inventário
     */
    public void back(Handler restore) {
        int size = views.size();

        if (size == 1) {
            user().base().closeInventory();
            //A referência será removida pelo evento InventoryCloseEvent
            return;
        }

        current.close();

        this.views.remove(size - 1);

        current = views.get(size - 2);

        open(restore);
    }

    public void backAndAdd(View view) {
        int size = views.size();

        if (size != 1) {
            current.close();
            this.views.remove(size - 1);
            current = views.get(size - 2);
        }

        add(view);
    }

    public void update() {
        handler.update(true, false);
    }

    public void updatePagination() {
        handler.update(false, true);
    }

    public void updateAll() {
        handler.update(true, true);
    }


    public void handleClick(int index, ItemStack cursor, boolean left) {
        handler.click(index, cursor, left);
    }

    public void tick() {
        current.tick();
    }
}
