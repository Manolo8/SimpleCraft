package com.github.manolo8.simplecraft.module.clan.user;

import com.github.manolo8.simplecraft.module.clan.Clan;
import com.github.manolo8.simplecraft.module.clan.ClanFlag;
import com.github.manolo8.simplecraft.module.user.UserService;
import com.github.manolo8.simplecraft.module.user.model.identity.BaseIdentity;
import com.github.manolo8.simplecraft.utils.def.LimitedArrayList;

public class ClanUser extends BaseIdentity {

    Clan clan;
    ClanFlag flag;

    LimitedArrayList<Integer> lastKills;

    int kills;
    int deaths;

    int killingSpree;
    int bestKillingSpree;

    public ClanUser() {
        this.lastKills = new LimitedArrayList(100);
    }

    //======================================================
    //====================ENCAPSULATION=====================
    //======================================================
    public ClanFlag flags() {
        return flag;
    }

    public Clan get() {
        return clan;
    }

    public int getKills() {
        return kills;
    }

    public int getDeaths() {
        return deaths;
    }

    public int getKdr() {
        return deaths == 0 ? 100 : (int) ((double) kills / (kills + deaths) * 100D);
    }

    public int getKillingSpree() {
        return killingSpree;
    }

    public int getBestKillingSpree() {
        return bestKillingSpree;
    }
    //======================================================
    //===================_ENCAPSULATION=====================
    //======================================================


    //======================================================
    //=======================METHODS========================
    //======================================================
    public void changeClan(Clan newClan, boolean add) {
        if (clan != null && add) clan.removeMember(this);
        if (newClan != null && add) newClan.addMember(this);
        this.clan = newClan;
        this.flag.reset();
        this.modified();
    }

    public boolean isIn() {
        return clan != null;
    }

    public void resetKDR() {

        if (clan != null) {
            clan.changeKill(-kills);
            clan.changeDeath(-deaths);
        }

        kills = 0;
        deaths = 0;

        modified();
    }

    public boolean same(ClanUser clanUser) {
        return isIn() && clanUser.get() == get();
    }

    public void onKill(ClanUser victim) {
        int counter = lastKills.count(victim.id);

        if (counter == 8) {
            identity.sendAction("§cVocê está fazendo freekill? KDA inalterado. (Se não estiver, fique tranquilo :))");
            return;
        }

        lastKills.add(victim.id);

        kills++;
        killingSpree++;

        if (clan != null) clan.changeKill(1);

        if (killingSpree > bestKillingSpree) bestKillingSpree = killingSpree;

        modified();

        victim.onDeath(this);
    }

    private void onDeath(ClanUser killer) {
        deaths++;

        if (killingSpree > 25) {
            UserService.broadcastTitle("§a" + identity.getName(), "§cAcabou de perder o killing spree de " + killingSpree + " para " + killer.getIdentity().getName() + "!");
        } else if (killingSpree > 5) {
            UserService.broadcastAction(identity.getName() + " acabou de perder o killing spree de " + killingSpree + "!");
        }

        if (killingSpree > bestKillingSpree) bestKillingSpree = killingSpree;
        killingSpree = 0;

        if (clan != null) clan.changeDeath(1);

        modified();
    }
    //======================================================
    //======================_METHODS========================
    //======================================================
}
