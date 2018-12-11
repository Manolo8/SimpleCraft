package com.github.manolo8.simplecraft.module.machine.type;

import com.github.manolo8.simplecraft.core.data.model.named.NamedEntity;
import com.github.manolo8.simplecraft.module.machine.type.drop.MachineDrop;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public class MachineType extends NamedEntity {

    protected Material material;
    protected double amplifier;
    protected double limit;
    protected int minFuelLevel;

    //======================================================
    //=====================ENCAPSULATION====================
    //======================================================
    public Material getMaterial() {
        return material;
    }

    /**
     * @param material block which represents that machine
     */
    public void setMaterial(Material material) {
        this.material = material;
    }

    public double getAmplifier() {
        return amplifier;
    }

    /**
     * An aplifier, which multiply the product of the machine
     *
     * @param amplifier
     */
    public void setAmplifier(double amplifier) {
        this.amplifier = amplifier;
    }

    public int getMinFuelLevel() {
        return minFuelLevel;
    }

    /**
     * Min fuel type level
     *
     * @param minFuelLevel
     */
    public void setMinFuelLevel(int minFuelLevel) {
        this.minFuelLevel = minFuelLevel;
    }

    public double getLimit() {
        return limit;
    }

    /**
     * Max production that can be stored
     *
     * @param limit
     */
    public void setLimit(double limit) {
        this.limit = limit;
    }

    public List<MachineDrop> getDrops() {
        return new ArrayList<>();
    }
    //======================================================
    //====================_ENCAPSULATION====================
    //======================================================

}
