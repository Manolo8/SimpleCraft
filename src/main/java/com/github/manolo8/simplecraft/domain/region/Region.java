package com.github.manolo8.simplecraft.domain.region;

import com.github.manolo8.simplecraft.core.protection.Protection;
import com.github.manolo8.simplecraft.data.model.NamedEntity;
import com.github.manolo8.simplecraft.domain.user.User;
import com.github.manolo8.simplecraft.utils.location.SimpleArea;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

public class Region extends NamedEntity implements Protection {

    private int worldId;
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
    public int getWorldId() {
        return worldId;
    }

    public void setWorldId(int worldId) {
        this.worldId = worldId;
    }

    public SimpleArea getArea() {
        return area;
    }

    public void setArea(SimpleArea area) {
        if(this.area != null) setNeedSave(true);
        this.area = area;
    }

    public void setPvpOn(boolean pvpOn) {
        if (this.pvpOn != pvpOn) setNeedSave(true);
        this.pvpOn = pvpOn;
    }

    public boolean isPvpAnimalOn() {
        return pvpAnimalOn;
    }

    public void setPvpAnimalOn(boolean pvpAnimalOn) {
        if (this.pvpAnimalOn != pvpAnimalOn) setNeedSave(true);
        this.pvpAnimalOn = pvpAnimalOn;
    }

    public boolean isCanSpread() {
        return canSpread;
    }

    public void setCanSpread(boolean canSpread) {
        if (this.canSpread != canSpread) setNeedSave(true);
        this.canSpread = canSpread;
    }

    public boolean isCanPistonWork() {
        return canPistonWork;
    }

    public void setCanPistonWork(boolean canPistonWork) {
        if (this.canPistonWork != canPistonWork) setNeedSave(true);
        this.canPistonWork = canPistonWork;
    }

    public boolean isCanExplode() {
        return canExplode;
    }

    public void setCanExplode(boolean canExplode) {
        if (this.canExplode != canExplode) setNeedSave(true);
        this.canExplode = canExplode;
    }

    public boolean isCanBreak() {
        return canBreak;
    }

    public void setCanBreak(boolean canBreak) {
        if (this.canBreak != canBreak) setNeedSave(true);
        this.canBreak = canBreak;
    }

    public boolean isCanPlace() {
        return canPlace;
    }

    public void setCanPlace(boolean canPlace) {
        if (this.canPlace != canPlace) setNeedSave(true);
        this.canPlace = canPlace;
    }

    public boolean isCanInteract() {
        return canInteract;
    }

    public void setCanInteract(boolean canInteract) {
        if (this.canInteract != canInteract) setNeedSave(true);
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
    public boolean canPistonWork() {
        return canPistonWork;
    }

    @Override
    public boolean canExplode() {
        return canExplode;
    }

    @Override
    public boolean isInArea(Location location) {
        return area != null && area.isInArea(location);
    }

    @Override
    public boolean canBreak(User user, Material material) {
        return canBreak || user.hasPermission("admin.block.break");
    }

    @Override
    public boolean canPlace(User user, Material material) {
        return canPlace || user.hasPermission("admin.block.place");
    }

    @Override
    public boolean canInteract(User user, Material material) {
        return canInteract || user.hasPermission("admin.interact");
    }

    @Override
    public boolean isPvpOn() {
        return pvpOn;
    }

    @Override
    public boolean isAnimalPvpOn(User ignored) {
        return pvpAnimalOn;
    }
    //---- METHODS ----
}
