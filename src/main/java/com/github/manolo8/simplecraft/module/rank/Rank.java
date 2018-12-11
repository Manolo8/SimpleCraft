package com.github.manolo8.simplecraft.module.rank;

import com.github.manolo8.simplecraft.core.data.model.named.NamedEntity;
import com.github.manolo8.simplecraft.utils.def.StringUtils;
import org.bukkit.Material;

public class Rank extends NamedEntity {

    private int rank;
    private double cost;
    private String tag;
    private Material representation;

    //======================================================
    //=====================ENCAPSULATION====================
    //======================================================
    public int get() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
        modified();
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
        modified();
    }

    public String getCostFormatted() {
        return StringUtils.doubleToString(cost);
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
        modified();
    }

    public Material getRepresentation() {
        return representation;
    }

    public void setRepresentation(Material representation) {
        this.representation = representation;
        modified();
    }
    //======================================================
    //====================_ENCAPSULATION====================
    //======================================================
}