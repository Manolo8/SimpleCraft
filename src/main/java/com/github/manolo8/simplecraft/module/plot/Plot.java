package com.github.manolo8.simplecraft.module.plot;

import com.github.manolo8.simplecraft.core.world.container.Area;
import com.github.manolo8.simplecraft.core.world.container.Container;
import com.github.manolo8.simplecraft.interfaces.BoardContainer;
import com.github.manolo8.simplecraft.interfaces.Enter;
import com.github.manolo8.simplecraft.interfaces.Exit;
import com.github.manolo8.simplecraft.interfaces.Protection;
import com.github.manolo8.simplecraft.module.hologram.Hologram;
import com.github.manolo8.simplecraft.module.plot.member.PlotMember;
import com.github.manolo8.simplecraft.module.plot.member.PlotMemberRepository;
import com.github.manolo8.simplecraft.module.user.User;
import com.github.manolo8.simplecraft.module.user.identity.Identity;
import com.github.manolo8.simplecraft.utils.entity.LazyLoaderList;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;

import java.sql.SQLException;
import java.util.List;

public class Plot extends Container implements Protection, Enter, Exit, BoardContainer {

    private final PlotMemberRepository repository;
    private LazyLoaderList<PlotMember> members;

    private Identity identity;
    private PlotFlag plotFlag;

    public Plot(PlotMemberRepository repository) {
        this.repository = repository;
    }

    //======================================================
    //======================ENCAPSULATION===================
    //======================================================
    public Identity getIdentity() {
        return identity;
    }

    public void setIdentity(Identity identity) {
        this.identity = identity;
    }

    public PlotFlag getPlotFlag() {
        return plotFlag;
    }

    public void setPlotFlag(PlotFlag plotFlag) {
        this.plotFlag = plotFlag;
    }

    public List<PlotMember> getMembers() {
        return members.get();
    }

    public void setMembers(LazyLoaderList<PlotMember> members) {
        this.members = members;
    }
    //======================================================
    //=====================_ENCAPSULATION===================
    //======================================================


    //======================================================
    //========================METHODS=======================
    //======================================================
    public PlotMember getMember(Identity identity) {
        for (PlotMember member : members.get())
            if (member.getIdentity() == identity) return member;

        return null;
    }

    public PlotMember getMember(String name) {
        for (PlotMember member : members.get())
            if (member.getIdentity().match(name)) return member;

        return null;
    }

    public void addMember(Identity identity) throws SQLException {
        PlotMember member;

        member = repository.create(this, identity);

        members.add(member);
    }

    public boolean isMember(Identity member) {
        return getMember(member) != null;
    }

    public void removeMember(PlotMember member) {
        members.remove(member);
        member.remove();
    }

    //======================================================
    //=======================OVERRIDE=======================
    //======================================================
    @Override
    public boolean teleport(User user) {
        return user.teleport(new Location(worldInfo.getWorld(), area.minX + 24.5, 65, area.minZ - 0.5));
    }

    //======================================================
    //======================_OVERRIDE=======================
    //======================================================


    //======================================================
    //======================CONTAINER=======================
    //======================================================


    public void onEnter(User user) {
        user.sendTitle("§a" + getName(), "Bem vindo a o terreno de " + identity.getName());
    }

    public void onExit(User user) {
        user.sendAction("§aTé mais!");
    }
    //======================================================
    //=====================_CONTAINER=======================
    //======================================================


    //======================================================
    //========================_METHODS======================
    //======================================================


    //======================================================
    //=======================PROTECTION=====================
    //======================================================
    @Override
    public boolean canSpread(Material type) {
        return type == Material.WATER;
    }

    @Override
    public boolean canPistonWork() {
        return false;
    }

    @Override
    public boolean canExplode() {
        return false;
    }

    @Override
    public boolean canEnter(User user) {
        if (plotFlag.canEntry() || user.identity() == identity || user.hasPermission("simplecraft.plot.entry")) {
            return true;
        } else {
            return getMember(user.identity()) != null;
        }
    }

    @Override
    public boolean canExit(User user) {
        return true;
    }

    @Override
    public boolean canBreak(User user, Material type) {
        if (user.identity() == identity || user.hasPermission("simplecraft.world.break")) {
            return true;
        } else {
            PlotMember member = getMember(user.identity());
            return member != null && member.flags().canBreak();
        }
    }

    @Override
    public boolean canPlace(User user, Material type) {
        if (user.identity() == identity || user.hasPermission("simplecraft.world.place")) {
            return true;
        } else {
            PlotMember member = getMember(user.identity());
            return member != null && member.flags().canPlace();
        }
    }

    @Override
    public boolean canInteract(User user, Material type) {
        if (user.identity() == identity || user.hasPermission("simplecraft.world.interact")) {
            return true;
        } else {
            PlotMember member = getMember(user.identity());
            return member != null && member.flags().canInteract();
        }
    }

    @Override
    public boolean canRemoveSpecials(User user) {
        if (user.identity() == identity || user.hasPermission("simplecraft.world.specials")) {
            return true;
        } else {
            PlotMember member = getMember(user.identity());
            return member != null && member.flags().canSpecials();
        }
    }

    @Override
    public boolean canUseSkill(int type) {
        return type == 4;
    }

    @Override
    public boolean canFly() {
        return true;
    }

    @Override
    public boolean isPvpOn() {
        return plotFlag.isPvpOn();
    }

    @Override
    public boolean isPveOn(User user) {
        if (user.identity() == identity || user.hasPermission("simplecraft.world.pve")) {
            return true;
        } else {
            return getMember(user.identity()) != null;
        }
    }

//    @Override
//    public List<OLDBoardItem> providerTo(User user) {
//        List<OLDBoardItem> data = new ArrayList<>();
//
//        data.add(new ProvidedOLDBoardItem(15, plotNameProvider, true));
//        data.add(new ProvidedOLDBoardItem(16, ownerNameProvider, true));
//        data.add(new ProvidedOLDBoardItem(17, pvpStatusProvider, true));
//        data.add(new BaseOLDBoardItem(19, "  ", true));
//
//        return data;
//    }
    //======================================================
    //======================_PROTECTION=====================
    //======================================================
}
