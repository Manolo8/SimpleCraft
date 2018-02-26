package com.github.manolo8.simplecraft.domain.region;

import com.github.manolo8.simplecraft.core.protection.Protection;
import com.github.manolo8.simplecraft.domain.user.User;
import com.github.manolo8.simplecraft.model.NamedEntity;
import com.github.manolo8.simplecraft.utils.location.SimpleArea;
import com.github.manolo8.simplecraft.utils.location.SimpleLocation;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

public class Region extends NamedEntity implements Protection {

    private World world;
    private SimpleArea area;
    private boolean pvpOn;
    private boolean pvpAnimalOn;
    private boolean canSpread;
    private boolean canPistonWork;
    private boolean canExplode;
    private boolean canBreak;
    private boolean canPlace;
    private boolean canInteract;

    //---- ENCAPSULATION ----
    public World getWorld() {
        return world;
    }

    public void setWorld(World world) {
        this.world = world;
    }

    public SimpleArea getArea() {
        return area;
    }

    public void setArea(SimpleArea area) {
        this.area = area;
    }

    public void setPvpOn(boolean pvpOn) {
        this.pvpOn = pvpOn;
    }

    public boolean isPvpAnimalOn() {
        return pvpAnimalOn;
    }

    public void setPvpAnimalOn(boolean pvpAnimalOn) {
        this.pvpAnimalOn = pvpAnimalOn;
    }

    public boolean isCanSpread() {
        return canSpread;
    }

    public void setCanSpread(boolean canSpread) {
        this.canSpread = canSpread;
    }

    public boolean isCanPistonWork() {
        return canPistonWork;
    }

    public void setCanPistonWork(boolean canPistonWork) {
        this.canPistonWork = canPistonWork;
    }

    public boolean isCanExplode() {
        return canExplode;
    }

    public void setCanExplode(boolean canExplode) {
        this.canExplode = canExplode;
    }

    public boolean isCanBreak() {
        return canBreak;
    }

    public void setCanBreak(boolean canBreak) {
        this.canBreak = canBreak;
    }

    public boolean isCanPlace() {
        return canPlace;
    }

    public void setCanPlace(boolean canPlace) {
        this.canPlace = canPlace;
    }

    public boolean isCanInteract() {
        return canInteract;
    }

    public void setCanInteract(boolean canInteract) {
        this.canInteract = canInteract;
    }
    //---- ENCAPSULATION ----

    //---- METHODS ----
    @Override
    public boolean isGlobal() {
        return false;
    }

    @Override
    public boolean canSpread(Material type) {
        return canSpread;
    }

    @Override
    public boolean canPistonWork(Location initiator) {
        return canPistonWork;
    }

    @Override
    public boolean canExplode() {
        return canExplode;
    }

    @Override
    public boolean isInArea(Location location) {
        return area.isInArea(location);
    }

    @Override
    public boolean canBreak(User user, Block block) {
        return canBreak || user.hasPermission("admin.block.break");
    }

    @Override
    public boolean canPlace(User user, Block block) {
        return canPlace || user.hasPermission("admin.block.place");
    }

    @Override
    public boolean canInteract(User user, Block block) {
        return canInteract;
    }

    @Override
    public boolean isPvpOn() {
        return pvpOn;
    }

    @Override
    public boolean isAnimalPvpOn() {
        return pvpAnimalOn;
    }
    //---- METHODS ----
}
