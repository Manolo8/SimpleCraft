package com.github.manolo8.simplecraft.module.region;

import com.github.manolo8.simplecraft.core.world.container.Container;
import com.github.manolo8.simplecraft.interfaces.BoardContainer;
import com.github.manolo8.simplecraft.interfaces.Protection;
import com.github.manolo8.simplecraft.module.rank.Rank;
import com.github.manolo8.simplecraft.module.rank.RankService;
import com.github.manolo8.simplecraft.module.user.User;
import org.bukkit.Material;

public class Region extends Container implements Protection, BoardContainer {

    private RegionFlag flag;
    private int minRank;

    //======================================================
    //====================ENCAPSULATION=====================
    //======================================================
    public RegionFlag flags() {
        return flag;
    }

    public void setFlag(RegionFlag flag) {
        this.flag = flag;
    }

    public int getMinRank() {
        return minRank;
    }

    public void setMinRank(int minRank) {
        this.minRank = minRank;
        modified();
    }
    //======================================================
    //===================_ENCAPSULATION=====================
    //======================================================


    //======================================================
    //======================PROTECTION======================
    //======================================================


    //======================================================
    //======================PROTECTION======================
    //======================================================
    @Override
    public boolean canSpread(Material type) {
        return (type == Material.WATER) ? flag.canWaterSpread() : flag.canFireSpread();
    }

    @Override
    public boolean canPistonWork() {
        return flag.canPistonWork();
    }

    @Override
    public boolean canExplode() {
        return flag.canExplode();
    }

    @Override
    public boolean canEnter(User user) {
        if (user.rank().get() < minRank) {

            Rank rank = RankService.instance.findByPoints(minRank);

            user.sendAction("§cRank nescessário: " + (rank == null ? "null" : rank.getTag()));

            return false;
        } else {
            return true;
        }
    }

    @Override
    public boolean canExit(User user) {
        return true;
    }

    @Override
    public boolean canBreak(User user, Material type) {
        return flag.canBreak() || user.hasPermission("simplecraft.world.break");
    }

    @Override
    public boolean canPlace(User user, Material type) {
        return flag.canPlace() || user.hasPermission("simplecraft.world.place");
    }

    @Override
    public boolean canInteract(User user, Material type) {
        return flag.canInteract() || user.hasPermission("simplecraft.world.interact");
    }

    @Override
    public boolean canRemoveSpecials(User user) {
        return user.hasPermission("simplecraft.world.modules");
    }

    @Override
    public boolean canUseSkill(int type) {
        return flag.canUseSkills();
    }

    @Override
    public boolean canFly() {
        return flag.clanFly();
    }

    @Override
    public boolean isPvpOn() {
        return flag.canPvp();
    }

    @Override
    public boolean isPveOn(User user) {
        return flag.canPvpAnimal();
    }

//    @Override
//    public List<OLDBoardItem> providerTo(User user) {
//        List<OLDBoardItem> data = new ArrayList<>();
//
//        data.add(new BaseOLDBoardItem(15, "§6" + getName() + ":", true));
//        data.add(new ProvidedOLDBoardItem(16, pvpStatusProvider, true));
//        data.add(new ProvidedOLDBoardItem(17, pveStatusProvider, true));
//        data.add(new ProvidedOLDBoardItem(18, playerSizeProvider, true));
//        data.add(new BaseOLDBoardItem(19, "   ", true));
//
//        return data;
//    }
    //======================================================
    //=====================_PROTECTION======================
    //======================================================
}