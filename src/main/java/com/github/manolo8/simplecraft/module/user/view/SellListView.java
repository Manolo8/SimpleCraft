package com.github.manolo8.simplecraft.module.user.view;

import com.github.manolo8.simplecraft.core.commands.inventory.ItemAction;
import com.github.manolo8.simplecraft.core.commands.inventory.base.BaseItemAction;
import com.github.manolo8.simplecraft.core.commands.inventory.base.BaseView;
import com.github.manolo8.simplecraft.utils.calculator.MoneyCalculator;
import com.github.manolo8.simplecraft.utils.calculator.MoneyCalculator.MaterialValue;
import com.github.manolo8.simplecraft.utils.def.IntegerList;
import com.github.manolo8.simplecraft.utils.def.StringUtils;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public class SellListView extends BaseView {

    @Override
    public int size() {
        return 36;
    }

    @Override
    public String getTitle() {
        return "Lista de ítens";
    }

    @Override
    public void createActions(List<ItemAction> actions) {
        IntegerList flag = user().getSellItems();

        for (int i = 0; i < MoneyCalculator.materials.length; i++) {
            MaterialValue value = MoneyCalculator.materials[i];

            boolean active = !flag.contains(i);
            ItemStack item = new ItemStack(value.material, 1, value.data);
            ItemMeta meta = item.getItemMeta();
            meta.setLore(Arrays.asList("§eValor: " + StringUtils.doubleToString(value.cost), "§cStatus: " + (active ? "ativo" : "inativo")));
            item.setItemMeta(meta);
            BaseItemAction itemAction = new BaseItemAction().setIndex(i);
            int temp = i;

            if (active) {
                item.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
                itemAction.setAction(() -> {
                    flag.add(temp);
                    user().modified();
                    getMain().update();
                });
            } else {
                itemAction.setAction(() -> {
                    flag.remove(temp);
                    user().modified();
                    getMain().update();
                });
            }

            actions.add(itemAction.setItem(item));
        }
    }
}
