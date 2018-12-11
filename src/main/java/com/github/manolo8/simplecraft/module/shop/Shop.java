package com.github.manolo8.simplecraft.module.shop;

import com.github.manolo8.simplecraft.core.world.container.Container;
import com.github.manolo8.simplecraft.interfaces.BlockBreak;
import com.github.manolo8.simplecraft.interfaces.BlockInteract;
import com.github.manolo8.simplecraft.interfaces.Tickable;
import com.github.manolo8.simplecraft.module.money.Money;
import com.github.manolo8.simplecraft.module.shop.view.ShopOwnerView;
import com.github.manolo8.simplecraft.module.shop.view.ShopView;
import com.github.manolo8.simplecraft.module.user.User;
import com.github.manolo8.simplecraft.utils.mc.InventoryUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class Shop extends Container implements BlockInteract, BlockBreak, Tickable {

    private Money money;
    private double buy;
    private double sell;
    private ItemStack item;
    private int totalBuy;
    private int totalSell;
    private int stock;
    private boolean changed;

    //======================================================
    //===================ENCAPSULATION======================
    //======================================================
    public Money getMoney() {
        return money;
    }

    public void setMoney(Money money) {
        this.money = money;
    }

    public double getBuy() {
        return buy;
    }

    public void setBuy(double buy) {
        this.buy = buy;
    }

    public double getSell() {
        return sell;
    }

    public void setSell(double sell) {
        this.sell = sell;
    }

    public ItemStack getItem() {
        return item;
    }

    public void setItem(ItemStack item) {
        this.item = item;
    }

    public int getTotalBuy() {
        return totalBuy;
    }

    public void setTotalBuy(int totalBuy) {
        this.totalBuy = totalBuy;
    }

    public int getTotalSell() {
        return totalSell;
    }

    public void setTotalSell(int totalSell) {
        this.totalSell = totalSell;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }
    //======================================================
    //==================_ENCAPSULATION======================
    //======================================================


    //======================================================
    //======================METHODS=========================
    //======================================================
    public Block getBlock() {
        return worldInfo.getWorld().getBlockAt(area.maxX, area.maxY, area.maxZ);
    }

    public int getAvailableToBuy() {
        return isRemoved() ? 0 : money.getId() == 1 ? 8192 : stock;
    }

    public int getAvailableToSell() {
        return isRemoved() ? 0 : money.getId() == 1 ? 8192 : (int) (money.getCoins() / sell);
    }

    public void add(int quantity, boolean status) {
        stock += quantity;
        if (status) totalSell += quantity;
        modified();
        changed = true;
    }

    public void take(int quantity, boolean status) {
        stock -= quantity;
        if (status) totalBuy += quantity;
        modified();
        changed = true;
    }

    public void buy(User user, int amount) {
        if (amount > getAvailableToBuy()) amount = getAvailableToBuy();

        if (buy != 0) {
            int maxBuys = (int) (user.money().getCoins() / buy);
            if (amount > maxBuys) amount = maxBuys;
        }

        Inventory userInventory = user.base().getInventory();

        amount = Math.min(InventoryUtils.getFreeSpace(userInventory, item), amount);

        if (amount != 0) {
            take(amount, true);
            money.depositCoins(amount * buy);
            user.money().withdrawCoins(amount * buy);

            InventoryUtils.addItemTo(userInventory, item, amount);
        }

    }

    public void sell(User user, int amount) {
        Inventory userInventory = user.base().getInventory();

        amount = Math.min(InventoryUtils.getItemQuantity(userInventory, item), amount);

        if (sell != 0) {
            int maxSells = getAvailableToSell();
            if (amount > maxSells) amount = maxSells;
        }

        if (amount != 0) {

            add(amount, true);

            money.withdrawCoins(amount * sell);
            user.money().depositCoins(amount * sell);

            InventoryUtils.removeItems(userInventory, item, amount);
        }
    }

    public void stock(User user, int amount) {
        Inventory userInventory = user.base().getInventory();
        int userQuantity = InventoryUtils.getItemQuantity(userInventory, item);
        if (amount > userQuantity) amount = userQuantity;

        if (amount != 0) {

            add(amount, false);

            InventoryUtils.removeItems(userInventory, item, amount);
        }

    }

    public void collect(User user, int amount) {
        if (amount > stock) amount = stock;

        Inventory userInventory = user.base().getInventory();
        int userFreeSpace = InventoryUtils.getFreeSpace(userInventory, item);
        if (amount > userFreeSpace) amount = userFreeSpace;

        if (amount != 0) {
            take(amount, false);

            money.depositCoins(amount * buy);
            user.money().withdrawCoins(amount * buy);

            InventoryUtils.addItemTo(userInventory, item, amount);
        }

    }

    public double buyPriceTo(int quantity) {
        return quantity * buy;
    }

    public double sellPriceTo(int quantity) {
        return quantity * sell;
    }

    public List<String> getStatus() {
        return Arrays.asList("§aVendidos: " + totalSell,
                "§aComprados: " + totalBuy,
                "§bReceita: " + ((totalSell * buy) - (totalBuy * sell)),
                "§bEstoque: " + (money.getId() == 1 ? "INFINITO" : getAvailableToBuy()));
    }

    //======================================================
    //======================CONTAINER=======================
    //======================================================
    @Override
    public void onBlockInteract(User user, PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            //Se o jogador é novato, avisa...
            if (user.identity().getOnlineAllTime() < 60 * 30 * 1000) {
                user.sendAction("§aUse o botão direito para negociar no shop!");
            }
            return;
        }

        if ((item == null || item.getType() == Material.AIR) && !updateItem(user)) {
            return;
        }

        if (user.identity() == money.getIdentity()) {
            user.createView(new ShopOwnerView(this));
        } else {
            user.createView(new ShopView(this));
        }
    }

    @Override
    public void onBreak(User user, BlockBreakEvent event) {

        if (!user.iContainer()
                .update(event.getBlock())
                .protection()
                .canRemoveSpecials(user)) {
            user.sendAction("§cVocê não pode quebrar shop!");
            event.setCancelled(true);
        } else {

            ItemStack hand = user.base().getInventory().getItemInMainHand();
            if (hand.getType() != Material.GOLDEN_AXE) {
                user.sendAction("§cUse um machado de ouro para quebrar!");
                event.setCancelled(true);
            } else if (stock > 0) {
                user.sendAction("§cRemova os itens antes de quebrar!");
                event.setCancelled(true);
            } else {
                user.sendAction("§aO shopping foi removido!");
                remove();
            }
        }
    }

    private boolean updateItem(User user) {
        if (!(user.identity() == money.getIdentity() || user.isAdmin()))
            return false;

        ItemStack hand = user.base().getItemInHand();

        if (hand == null || hand.getType() == Material.AIR) {
            return false;
        } else {

            item = hand.clone();
            item.setAmount(1);
            modified();
            return true;

        }
    }

    @Override
    public void tick() {
        if (changed) {
            changed = false;

            BlockState state = getBlock().getState();

            if (!(state instanceof Sign)) {
                getBlock().setType(Material.SIGN);
                return;
            }

            Sign sign = (Sign) state;

            sign.setLine(3, "etq. " + (money.getId() == 1 ? "~" : getAvailableToBuy()));

            sign.update();
        }
    }

    //======================================================
    //=====================_CONTAINER=======================
    //======================================================


    //======================================================
    //======================METHODS=========================
    //======================================================
}