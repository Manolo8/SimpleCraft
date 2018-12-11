package com.github.manolo8.simplecraft.module.money;

import com.github.manolo8.simplecraft.module.user.model.identity.BaseIdentity;
import com.github.manolo8.simplecraft.utils.def.StringUtils;

public class Money extends BaseIdentity {

    private double coins;
    private double cash;

    //======================================================
    //=========================COINS========================
    //======================================================
    public boolean hasCoins(double quantity) {
        return coins >= quantity;
    }

    public boolean withdrawCoins(double quantity) {
        if (hasCoins(quantity)) {
            coins -= quantity;
            modified();
            return true;
        } else {
            return false;
        }
    }

    public void depositCoins(double quantity) {
        coins += quantity;
        modified();
    }

    public double getCoins() {
        return coins;
    }

    public void setCoins(double quantity) {
        this.coins = quantity;
        modified();
    }

    public String getCoinsFormatted() {
        return StringUtils.doubleToString0D(coins);
    }
    //======================================================
    //========================_COINS========================
    //======================================================


    //======================================================
    //=========================Cash========================
    //======================================================
    public boolean hasCash(double quantity) {
        return cash >= quantity;
    }

    public boolean withdrawCash(double quantity) {
        if (hasCash(quantity)) {
            cash -= quantity;
            modified();
            return true;
        } else {
            return false;
        }
    }

    public void depositCash(double quantity) {
        cash += quantity;
        modified();
    }

    public double getCash() {
        return cash;
    }

    public void setCash(double quantity) {
        this.cash = quantity;
        modified();
    }

    public String getCashFormatted() {
        return StringUtils.doubleToString0D(cash);
    }
    //======================================================
    //========================_COINS========================
    //======================================================
}
