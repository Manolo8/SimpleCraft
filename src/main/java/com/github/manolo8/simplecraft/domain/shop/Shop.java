package com.github.manolo8.simplecraft.domain.shop;

import com.github.manolo8.simplecraft.core.world.WorldService;
import com.github.manolo8.simplecraft.data.model.LocationEntity;
import com.github.manolo8.simplecraft.domain.user.User;
import com.github.manolo8.simplecraft.utils.InventoryUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Sign;

public class Shop extends LocationEntity {

    //ownerId -1 = ADMIN
    private User owner;
    private ItemStack itemStack;
    private double buyPrice;
    private double sellPrice;
    private int totalBuy;
    private int totalSell;
    //Chest block
    private Block block;

    //Encapsulation
    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public void setItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
        setNeedSave(true);
    }

    public double getBuyPrice() {
        return buyPrice;
    }

    public void setBuyPrice(double buyPrice) {
        this.buyPrice = buyPrice;
        setNeedSave(true);
    }

    public double getSellPrice() {
        return sellPrice;
    }

    public void setSellPrice(double sellPrice) {
        this.sellPrice = sellPrice;
        setNeedSave(true);
    }

    public int getTotalBuy() {
        return totalBuy;
    }

    public void setTotalBuy(int totalBuy) {
        this.totalBuy = totalBuy;
        setNeedSave(true);
    }

    public int getTotalSell() {
        return totalSell;
    }

    public void setTotalSell(int totalSell) {
        this.totalSell = totalSell;
        setNeedSave(true);
    }
    //Encapsulation

    //Methods
    @Override
    public void addReference() {
        super.addReference();
        owner.addReference();
    }

    @Override
    public void removeReference() {
        super.removeReference();
        owner.removeReference();
    }

    private void init() {
        Block temp = WorldService.instance.getWorldByWorldId(worldId).getBlockAt(x, y, z);

        //Verifica se é um attachment
        if (temp.getType() == Material.WALL_SIGN) {
            Sign sign = (Sign) temp.getState().getData();
            block = temp.getRelative(sign.getAttachedFace());

            //se for um attachment, e no que esta attachado
            //Não for um bau, continua para pegar o bloco a baixo
            if (block.getType() == Material.CHEST) return;
        }
        block = temp.getRelative(0, -1, 0);
    }

    public void userBuy(User user, int amount) {
        int quantity = getQuantity();
        if (amount > quantity) amount = quantity;
        int maxBuys = (int) (user.getMoney() / getBuyPrice());
        if (amount > maxBuys) amount = maxBuys;
        Inventory userInventory = user.getBase().getInventory();
        int userFreeSpace = InventoryUtils.getFreeSpace(userInventory, itemStack);
        if (amount > userFreeSpace) amount = userFreeSpace;

        if (amount == 0) return;

        removeItem(amount);
        owner.deposit(amount * buyPrice);
        user.withdraw(amount * buyPrice);
        InventoryUtils.addItems(userInventory, itemStack, amount);
    }

    public void userSell(User user, int amount) {
        int freeSpace = getFreeSpace();
        if (amount > freeSpace) amount = freeSpace;
        Inventory userInventory = user.getBase().getInventory();
        int userQuantity = InventoryUtils.getQuantity(userInventory, itemStack);
        if (amount > userQuantity) amount = userQuantity;

        int maxSells = (int) (owner.getMoney() / getSellPrice());
        if (amount > maxSells) amount = maxSells;

        if (amount == 0) return;

        addItem(amount);
        owner.withdraw(amount * buyPrice);
        user.deposit(amount * buyPrice);
        InventoryUtils.removeItems(userInventory, itemStack, amount);
    }

    private boolean isValid() {
        if (block == null) init();

        return block != null && block.getType() == Material.CHEST;
    }

    protected Inventory getInventory() {
        BlockState state = block.getState();
        return ((Chest) state).getBlockInventory();
    }

    /**
     * Se for admin retorna 4096, caso contrário o valor
     * padrão
     *
     * @return quantos itens há no depósito
     */
    private int getQuantity() {
        return getOwner().getId() == -1 ? 4096 : (isValid() ? InventoryUtils.getQuantity(getInventory(), itemStack) : 0);
    }

    /**
     * Se for admin retorna 4096
     *
     * @return quantos itens livres
     */
    private int getFreeSpace() {
        return getOwner().getId() == -1 ? 4096 : (isValid() ? InventoryUtils.getFreeSpace(getInventory(), itemStack) : 0);
    }

    private void removeItem(int amount) {
        if (getOwner().getId() == -1) return;
        InventoryUtils.removeItems(getInventory(), itemStack, amount);
    }

    private void addItem(int amount) {
        if (getOwner().getId() == -1) return;
        InventoryUtils.addItems(getInventory(), itemStack, amount);
    }
    //Methods
}
