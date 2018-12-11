package com.github.manolo8.simplecraft.module.kit.view;

import com.github.manolo8.simplecraft.core.commands.inventory.ItemAction;
import com.github.manolo8.simplecraft.core.commands.inventory.base.BaseItemAction;
import com.github.manolo8.simplecraft.core.commands.inventory.base.BaseView;
import com.github.manolo8.simplecraft.module.kit.Kit;
import com.github.manolo8.simplecraft.module.kit.KitService;
import com.github.manolo8.simplecraft.module.kit.user.KitUser;
import com.github.manolo8.simplecraft.module.kit.user.delay.KitDelay;
import com.github.manolo8.simplecraft.module.user.User;
import com.github.manolo8.simplecraft.utils.def.StringUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.github.manolo8.simplecraft.utils.mc.ItemStackUtils.create;
import static java.lang.Math.max;
import static org.bukkit.Material.*;

public class KitView extends BaseView {

    private final KitService service;
    protected int selected;
    protected int available;
    protected int cooldown;
    protected int blocked;

    public KitView(KitService service) {
        this.service = service;
    }

    @Override
    public int size() {
        return 9;
    }

    @Override
    public String getTitle() {
        return "Kits";
    }

    @Override
    public void createActions(List<ItemAction> actions) throws SQLException {
        User user = user();
        KitUser kitUser = user.kit();
        List<Kit> kits = service.getEntities();

        available = 0;
        cooldown = 0;
        blocked = 0;

        for (Kit kit : kits) {
            if (kit.canUse(user)) {
                KitDelay delay = kitUser.getDelay(kit);
                if (delay.canUse()) {
                    //Liberado
                    available++;
                    if (selected == 0) {
                        actions.add(buildKitAction(kit, null).setIndex(-1));
                    }

                } else {
                    //Em cd
                    cooldown++;
                    if (selected == 1) {
                        actions.add(buildKitAction(kit, delay).setIndex(-1));
                    }

                }
            } else {

                //Bloqueado
                blocked++;
                if (selected == 2) {
                    actions.add(buildKitAction(kit, null).setIndex(-1));
                }

            }
        }
    }

    @Override
    public void createPagination(List<ItemAction> pagination) {
        pagination.add(buildPaginationAction(GREEN_WOOL, "Disponíveis", 0, available).setIndex(3));
        pagination.add(buildPaginationAction(YELLOW_WOOL, "Em cooldown", 1, cooldown).setIndex(4));
        pagination.add(buildPaginationAction(RED_WOOL, "Bloqueados", 2, blocked).setIndex(5));
    }

    private BaseItemAction buildPaginationAction(Material material, String title, int selection, int quantity) {
        BaseItemAction itemAction = new BaseItemAction();

        itemAction.setItem(create(selection == selected ? WHITE_WOOL : material, max(1, quantity), title));
        itemAction.setAction(() -> {
            selected = selection;
            getMain().updateAll();
        });

        return itemAction;
    }

    private BaseItemAction buildKitAction(Kit kit, KitDelay delay) {
        BaseItemAction itemAction = new BaseItemAction();

        ItemStack item;
        ItemMeta meta;

        if (kit.getItems().size() > 0) {
            item = kit.getItems().get(0).getItem().clone();
            item.setAmount(1);
            meta = item.getItemMeta();
        } else {
            item = new ItemStack(PAPER);
            meta = item.getItemMeta();
        }

        meta.setDisplayName("§e" + kit.getName().toUpperCase());

        List<String> lore = new ArrayList<>();
        lore.add("§aRank " + kit.getRank());
        lore.add("§aResfriamento: " + StringUtils.longTimeToString(kit.getDelay()));
        if (delay != null) {
            lore.add("§cEspere " + StringUtils.longTimeToString(delay.getWaitTime()) + "!");
        }

        meta.setLore(lore);

        item.setItemMeta(meta);

        itemAction.setAction(() -> getMain().add(new KitInfoView(kit)));
        itemAction.setItem(item);

        return itemAction;
    }
}
