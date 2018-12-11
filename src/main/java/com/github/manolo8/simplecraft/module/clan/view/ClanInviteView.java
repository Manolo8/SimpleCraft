package com.github.manolo8.simplecraft.module.clan.view;

import com.github.manolo8.simplecraft.core.commands.inventory.ItemAction;
import com.github.manolo8.simplecraft.core.commands.inventory.base.BaseItemAction;
import com.github.manolo8.simplecraft.core.commands.inventory.base.BaseView;
import com.github.manolo8.simplecraft.module.clan.Clan;
import com.github.manolo8.simplecraft.module.clan.invite.ClanInvite;
import com.github.manolo8.simplecraft.utils.mc.ItemStackUtils;

import java.util.List;

public class ClanInviteView extends BaseView {

    private final Clan clan;

    public ClanInviteView(Clan clan) {
        this.clan = clan;
    }

    @Override
    public String getTitle() {
        return "Convites do clan (" + clan.getColoredTag() + "§0)";
    }

    @Override
    public void createActions(List<ItemAction> actions) {

        int index = 0;
        for (ClanInvite invite : clan.getInvites()) {
            BaseItemAction inviteAction = new BaseItemAction();

            inviteAction.setItem(ItemStackUtils.createSkullByIdentity(invite.getInvited(), "§a" + invite.getInvited().getName(), "§aClique para remover!"));
            inviteAction.setAction(() -> {
                clan.removeInvite(invite);
                getMain().update();
            });
            actions.add(inviteAction.setIndex(11 + (index / 5) * 9 + index % 5));
            index++;
        }

    }
}
