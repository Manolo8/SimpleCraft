package com.github.manolo8.simplecraft.modules.region.data;

import com.github.manolo8.simplecraft.utils.location.SimpleArea;
import com.github.manolo8.simplecraft.utils.location.SimpleLocation;

import java.util.List;

/**
 * Talvez não seja nescessário isso...
 * No entanto, vai que alguma hora eu queira
 * Adicionar alguma coisa?
 */
public class RegionDTO {

    private Integer id;
    private List<Integer> subRegions;
    private String name;
    private int worldId;
    private int maxX;
    private int maxY;
    private int maxZ;
    private int minX;
    private int minY;
    private int minZ;
    private boolean pvpOn;
    private boolean pvpAnimalOn;
    private boolean canSpread;
    private boolean canPistonWork;
    private boolean canExplode;
    private boolean canBreak;
    private boolean canPlace;
    private boolean canInteract;

    public Integer getId() {
        return id;
    }

    public String getName() {

        return name;
    }

    public List<Integer> getSubRegions() {
        return subRegions;
    }

    public void setSubRegions(List<Integer> subRegions) {
        this.subRegions = subRegions;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getWorldId() {
        return worldId;
    }

    public void setWorldId(int worldId) {
        this.worldId = worldId;
    }

    public void setMaxX(int maxX) {
        this.maxX = maxX;
    }

    public void setMaxY(int maxY) {
        this.maxY = maxY;
    }

    public void setMaxZ(int maxZ) {
        this.maxZ = maxZ;
    }

    public void setMinX(int minX) {
        this.minX = minX;
    }

    public void setMinY(int minY) {
        this.minY = minY;
    }

    public void setMinZ(int minZ) {
        this.minZ = minZ;
    }

    public boolean isPvpOn() {
        return pvpOn;
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

    public SimpleArea getArea() {
        return new SimpleArea(new SimpleLocation(maxX, maxY, maxZ), new SimpleLocation(minX, minY, minZ));
    }
}
