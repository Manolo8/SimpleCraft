package com.github.manolo8.simplecraft.module.clan.view;

import com.github.manolo8.simplecraft.core.commands.inventory.Action;
import com.github.manolo8.simplecraft.core.commands.inventory.ItemAction;
import com.github.manolo8.simplecraft.core.commands.inventory.base.BaseView;
import com.github.manolo8.simplecraft.module.clan.Clan;
import com.github.manolo8.simplecraft.module.clan.user.ClanUser;
import com.github.manolo8.simplecraft.utils.mc.ItemStackUtils;
import org.bukkit.Material;

import java.sql.SQLException;
import java.util.List;

import static com.github.manolo8.simplecraft.core.commands.inventory.base.BaseItemAction.of;
import static com.github.manolo8.simplecraft.utils.mc.ItemStackUtils.create;

public class ClanMemberView extends BaseView {

    private final Clan clan;
    private final ClanUser holder;

    public ClanMemberView(ClanUser holder) {
        this.holder = holder;
        this.clan = holder.get();
    }

    @Override
    public int size() {
        return 27;
    }

    @Override
    public String getTitle() {
        return "Membro (" + holder.getIdentity().getName() + ")";
    }

    @Override
    public void createActions(List<ItemAction> actions) {

        actions.add(of(ItemStackUtils.createSkullByIdentity(holder.getIdentity(), "§e" + holder.getIdentity().getName())).setIndex(2, 5));

        if (user().clan() == holder || clan == null) return;

        ClanUser user = user().clan();

        if (clan.canKick(user, holder))
            actions.add(of(create(Material.SPONGE, "§aKicar do CLAN"))
                    .setAction(() -> filter(() -> clan.kick(holder)))
                    .setIndex(3, 4));

        if (clan.canAddToStaff(user, holder))
            actions.add(of(create(Material.BLAZE_ROD, "§aTornar STAFF"))
                    .setAction(() -> filter(() -> clan.addToStaff(holder)))
                    .setIndex(3, 5));
        else if (clan.canRemoveFromStaff(user, holder))
            actions.add(of(create(Material.STICK, "§aTirar da STAFF"))
                    .setAction(() -> filter(() -> clan.removeFromStaff(holder)))
                    .setIndex(3, 5));

        if (clan.canChangeLeader(user, holder))
            actions.add(of(create(Material.DIAMOND, "§aPassar liderança"))
                    .setAction(() -> filter(() -> clan.changeLeader(holder)))
                    .setIndex(3, 6));
    }

    private void filter(Action action) throws SQLException {
        if ((user().clan().get() != clan || holder.get() != clan) && !user().isAdmin()) return;
        action.click();
        getMain().update();
    }
}
