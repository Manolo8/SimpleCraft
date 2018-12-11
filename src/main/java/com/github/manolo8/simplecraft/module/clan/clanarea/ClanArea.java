package com.github.manolo8.simplecraft.module.clan.clanarea;

import com.github.manolo8.simplecraft.core.world.WorldService;
import com.github.manolo8.simplecraft.core.world.container.Container;
import com.github.manolo8.simplecraft.interfaces.BlockBreak;
import com.github.manolo8.simplecraft.interfaces.BoardContainer;
import com.github.manolo8.simplecraft.interfaces.Tickable;
import com.github.manolo8.simplecraft.module.clan.Clan;
import com.github.manolo8.simplecraft.module.user.User;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ClanArea extends Container.CustomProtection implements BlockBreak, Tickable, BoardContainer {

    Clan clan;
    long lastConquest;

    //======================================================
    //====================ENCAPSULATION=====================
    //======================================================
    public Clan getClan() {
        return clan;
    }

    public long getLastConquest() {
        return lastConquest;
    }

    public long getLastConquestTime() {
        return ((System.currentTimeMillis() - lastConquest));
    }
    //======================================================
    //===================_ENCAPSULATION=====================
    //======================================================

    //======================================================
    //=======================METHODS========================
    //======================================================
    public void changeClan(Clan newClan, boolean add) {
        if (clan != null && add) clan.removeArea(this);
        if (newClan != null && add) newClan.addArea(this);
        this.clan = newClan;
        this.lastConquest = System.currentTimeMillis();
        this.modified();
    }

    public boolean havePermission(User user) {
        return user.clan().isIn() && user.clan().get() == clan;
    }
    //======================================================
    //======================_METHODS========================
    //======================================================


    //======================================================
    //======================CONTAINER=======================
    //======================================================
    @Override
    public void onBreak(User user, BlockBreakEvent event) {
        Material material = event.getBlock().getType();

        if (material != Material.OBSIDIAN || user.base().getGameMode() == GameMode.CREATIVE) return;

        event.setCancelled(true);

        if (!user.clan().isIn()) {
            user.sendAction("§cVocê não está em um CLAN!");
            return;
        }

        Clan clan = user.clan().get();

        if (this.clan == clan) {
            user.sendAction("§cSeu clan já dominou está área!");
            return;
        }

        user.sendAction("§aÁrea dominada com sucesso!");
        changeClan(clan, true);
    }

    @Override
    public void remove() {
        super.remove();
        if (clan != null) clan.removeArea(this);
    }

//    @Override
//    public List<OLDBoardItem> providerTo(User user) {
//        List<OLDBoardItem> data = new ArrayList<>();
//
//        data.add(new BaseOLDBoardItem(15, "§cDominado por:", true));
//        data.add(new ProvidedOLDBoardItem(16, clanConquestProvider, true));
//        data.add(new ProvidedOLDBoardItem(17, messageProvider, true));
//        data.add(new BaseOLDBoardItem(18, "  ", true));
//
//        return data;
//    }

    @Override
    public void tick() {
        if (WorldService.tick % 75 == 1) {
            for (User user : users) {
                if (havePermission(user))
                    user.base().addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 100, 1), true);
                else user.base().addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 100, 1), true);
            }
        }
    }

    //======================================================
    //=====================_CONTAINER=======================
    //======================================================


    //======================================================
    //===================PROTECTION PROXY===================
    //======================================================
    @Override
    public boolean canBreak(User user, Material type) {
        return type == Material.OBSIDIAN || (super.canBreak(user, type) || (type == Material.STONE || type == Material.COBBLESTONE));
    }

    @Override
    public boolean canPlace(User user, Material type) {
        return super.canPlace(user, type) || (type == Material.STONE || type == Material.COBBLESTONE) && havePermission(user);
    }

    @Override
    public boolean canInteract(User user, Material type) {
        return super.canInteract(user, type) || havePermission(user);
    }
    //======================================================
    //==================_PROTECTION PROXY===================
    //======================================================
}
