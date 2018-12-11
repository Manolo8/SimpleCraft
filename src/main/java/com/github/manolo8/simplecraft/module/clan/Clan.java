package com.github.manolo8.simplecraft.module.clan;

import com.github.manolo8.simplecraft.core.data.model.named.NamedEntity;
import com.github.manolo8.simplecraft.module.clan.clanarea.ClanArea;
import com.github.manolo8.simplecraft.module.clan.invite.ClanInvite;
import com.github.manolo8.simplecraft.module.clan.invite.ClanInviteRepository;
import com.github.manolo8.simplecraft.module.clan.user.ClanUser;
import com.github.manolo8.simplecraft.module.user.User;
import com.github.manolo8.simplecraft.module.user.identity.Identity;
import com.github.manolo8.simplecraft.utils.entity.LazyLoaderList;

import java.sql.SQLException;
import java.util.List;

public class Clan extends NamedEntity {

    private final ClanInviteRepository inviteRepository;

    //LAZY
    LazyLoaderList<ClanUser> members;
    LazyLoaderList<ClanInvite> invites;
    LazyLoaderList<ClanArea> areas;

    String tag;
    String coloredTag;

    boolean friendlyFire;
    long founded;

    int kills;
    int deaths;

    public Clan(ClanInviteRepository inviteRepository) {
        this.inviteRepository = inviteRepository;
    }

    //======================================================
    //====================ENCAPSULATION=====================
    //======================================================
    public String getTag() {
        return tag;
    }

    public String getColoredTag() {
        return coloredTag;
    }

    public List<ClanUser> getMembers() {
        return members.get();
    }

    public List<ClanInvite> getInvites() {
        return invites.get();
    }

    public List<ClanArea> getAreas() {
        return areas.get();
    }

    public boolean isFriendlyFire() {
        return friendlyFire;
    }

    public long getFounded() {
        return founded;
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
    //======================================================
    //===================_ENCAPSULATION=====================
    //======================================================


    //======================================================
    //=======================METHODS========================
    //======================================================
    public void addArea(ClanArea area) {
        areas.add(area);
    }

    public void removeArea(ClanArea area) {
        areas.remove(area);
    }

    public void addMember(ClanUser member) {
        members.add(member);

        changeKill(member.getKills());
        changeDeath(member.getDeaths());
    }

    public void removeMember(ClanUser member) {
        members.remove(member);

        changeKill(-member.getKills());
        changeDeath(-member.getDeaths());
    }

    public int freeSlots() {
        return 15 - (members.get().size() + invites.get().size());
    }

    public ClanInvite createInvite(Identity target) throws SQLException {
        ClanInvite invite = inviteRepository.create(target, this);

        invites.get().add(invite);

        return invite;
    }

    public void removeInvite(ClanInvite invite) {
        invites.remove(invite);

        invite.remove();
    }

    public ClanInvite getInvite(Identity identity) {
        for (ClanInvite invite : invites.get()) {
            if (invite.getInvited() == identity)
                return invite;
        }

        return null;
    }

    public void changeColoredTag(String tag) {
        this.coloredTag = tag;
        modified();
    }

    public void changeFriendFire(boolean ff) {
        this.friendlyFire = ff;
    }

    public void changeTag(String tag) {
        this.tag = tag;
        modified();
    }

    public void changeKill(int value) {
        this.kills += value;
        modified();
    }

    public void changeDeath(int value) {
        this.deaths += value;
        modified();
    }

    //======================================================
    //=====================PERMISSIONS======================
    //======================================================
    public boolean canAddToStaff(ClanUser user, ClanUser target) {
        return user.getIdentity().user().isAdmin() || user.same(target) && user.flags().isStaff() && !target.flags().isStaff();
    }

    public void addToStaff(ClanUser target) {
        ClanFlag.staff.set(target, ClanUser::flags, true);
    }

    public boolean canRemoveFromStaff(ClanUser user, ClanUser target) {
        return user.getIdentity().user().isAdmin() || user.same(target) && user.flags().isStaff() && !target.flags().isLeader();
    }

    public void removeFromStaff(ClanUser target) {
        ClanFlag.staff.set(target, ClanUser::flags, false);
    }

    public void changeLeader(ClanUser target) {

        for (ClanUser clanUser : members.get()) {
            if (clanUser.flags().isLeader()) {
                ClanFlag.leader.set(clanUser, ClanUser::flags, false);
                break;
            }
        }

        ClanFlag.leader.set(target, ClanUser::flags, true);
    }

    public boolean canChangeLeader(ClanUser user, ClanUser target) {
        return user.getIdentity().user().isAdmin() || user.same(target) && user.flags().isLeader();
    }

    public void kick(ClanUser target) {
        target.changeClan(null, true);
    }

    public boolean canKick(ClanUser user, ClanUser target) {
        return user.getIdentity().user().isAdmin() || user.same(target) && user.flags().isStaff() && !target.flags().isLeader();
    }

    public boolean isStaff(User user) {
        return user.clan().get() == this && user.clan().flags().isStaff() || user.isAdmin();
    }

    //======================================================
    //====================_PERMISSIONS======================
    //======================================================


    //======================================================
    //=======================OVERRIDE=======================
    //======================================================
    @Override
    public void remove() {
        super.remove();

        for (ClanUser member : members.get())
            member.changeClan(null, false);

        for (ClanInvite invite : invites.get())
            invite.remove();

        for (ClanArea area : areas.get())
            area.changeClan(null, false);
    }
    //======================================================
    //======================_OVERRIDE=======================
    //======================================================


    //======================================================
    //======================_METHODS========================
    //======================================================
}
