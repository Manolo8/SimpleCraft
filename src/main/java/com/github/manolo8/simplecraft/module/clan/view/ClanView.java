package com.github.manolo8.simplecraft.module.clan.view;

import com.github.manolo8.simplecraft.core.commands.inventory.ItemAction;
import com.github.manolo8.simplecraft.core.commands.inventory.base.BaseItemAction;
import com.github.manolo8.simplecraft.core.commands.inventory.base.BaseView;
import com.github.manolo8.simplecraft.module.clan.Clan;
import com.github.manolo8.simplecraft.module.clan.user.ClanUser;
import com.github.manolo8.simplecraft.module.user.UserService;
import com.github.manolo8.simplecraft.utils.def.StringUtils;
import com.github.manolo8.simplecraft.utils.mc.ItemStackUtils;
import org.bukkit.Material;

import java.util.List;

import static com.github.manolo8.simplecraft.utils.def.StringUtils.longToDate;

public class ClanView extends BaseView {

    private final Clan clan;

    public ClanView(Clan clan) {
        this.clan = clan;
    }

    @Override
    public String getTitle() {
        return "Clan (" + clan.getColoredTag() + "§0)";
    }

    @Override
    public void createActions(List<ItemAction> actions) {

        BaseItemAction creationInfo = new BaseItemAction();
        creationInfo.setItem(ItemStackUtils.create(Material.SPONGE, clan.getColoredTag(),
                "§eNome: " + clan.getName(),
                "§eFundação: " + longToDate(clan.getFounded()),
                "§eMembros: " + clan.getMembers().size() + "/15"));
        actions.add(creationInfo.setIndex(2, 2));

        if (clan.isStaff(user())) {
            BaseItemAction inviteInfo = new BaseItemAction();
            inviteInfo.setItem(ItemStackUtils.create(Material.WRITABLE_BOOK, Math.max(1, clan.getInvites().size()), "§eConvites: " + clan.getInvites().size()));
            inviteInfo.setAction(() -> getMain().add(new ClanInviteView(clan)));
            actions.add(inviteInfo.setIndex(3, 2));
        }

        BaseItemAction domAreas = new BaseItemAction();
        domAreas.setItem(ItemStackUtils.create(Material.BARRIER, "§eÁreas dominadas: " + clan.getAreas().size()));
        actions.add(domAreas.setIndex(4, 2));

        BaseItemAction kdrStatus = new BaseItemAction();
        kdrStatus.setItem(ItemStackUtils.create(Material.DIAMOND_SWORD, "§eStatus",
                "§eEliminações: " + clan.getKills(),
                "§eMortes: " + clan.getDeaths(),
                "§eKDR: " + clan.getKdr() + "%"));
        actions.add(kdrStatus.setIndex(5, 2));

        BaseItemAction members = new BaseItemAction();
        members.setItem(ItemStackUtils.create(Material.PAPER, "§eLista de membros:"));
        actions.add(members.setIndex(2, 4));

        int index = 0;
        for (ClanUser loop : clan.getMembers()) {
            BaseItemAction member = new BaseItemAction();
            member.setItem(ItemStackUtils.createSkullByIdentity(loop.getIdentity(), "§e" + loop.getIdentity().getName(),
                    (loop.getIdentity().isOnline() && !loop.getIdentity().user().isHidden()) ? "§aONLINE" : "§cOFFLINE",
                    "§eEliminações: " + loop.getKills(),
                    "§eMortes: " + loop.getDeaths(),
                    "§eKDR: " + loop.getKdr() + "%",
                    "§eÚltimo login: " + StringUtils.longTimeToString(System.currentTimeMillis() - loop.getIdentity().getLastLogin())));
            member.setAction(() -> getMain().add(new ClanMemberView(loop)));

            actions.add(member.setIndex(21 + (index / 5) * 9 + index % 5));
            index++;
        }

    }

    @Override
    public void tick() {
        if (UserService.tick % 20 == 10) {
            if (clan.isRemoved()) {
                getMain().close(true);
            } else {
                getMain().update();
            }
        }
    }
}
