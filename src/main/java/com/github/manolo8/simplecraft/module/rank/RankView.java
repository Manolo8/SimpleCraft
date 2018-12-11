package com.github.manolo8.simplecraft.module.rank;

import com.github.manolo8.simplecraft.core.commands.inventory.ItemAction;
import com.github.manolo8.simplecraft.core.commands.inventory.base.BaseItemAction;
import com.github.manolo8.simplecraft.core.commands.inventory.base.BaseView;
import com.github.manolo8.simplecraft.utils.mc.ItemStackUtils;
import org.bukkit.Material;
import org.bukkit.Sound;

import java.util.List;

public class RankView extends BaseView {

    private final RankService rankService;

    public RankView() {
        rankService = RankService.instance;
    }

    @Override
    public int size() {
        return 27;
    }

    @Override
    public String getTitle() {
        return "Ranks disponíveis";
    }

    @Override
    public void createActions(List<ItemAction> actions) {
        Rank current = user().rank();

        Rank next = rankService.findNextRank(current);

        if (next != null) {
            actions.add(new NextRankAction(next).setIndex(3, 6));
        }

        actions.add(new CurrentRankAction(current).setIndex(3, next == null ? 5 : 4));

        actions.add(new InfoAction().setIndex(2, 5));
    }

    class NextRankAction extends BaseItemAction {

        public NextRankAction(Rank next) {

            setAction(() -> {
                if (user().money().withdrawCoins(next.getCost())) {
                    user().changeRank(next);
                    user().playSound(Sound.ENTITY_PLAYER_LEVELUP, 20, 20, false);
                    getMain().update();
                } else {
                    user().playSound(Sound.BLOCK_ANVIL_LAND, 20, 20, false);
                }
            });

            setItem(ItemStackUtils.create(next.getRepresentation(), next.getTag(), "§cNum " + next.get(), "§cCusto " + next.getCostFormatted(), "§aClique para upar!"));
        }
    }

    class CurrentRankAction extends BaseItemAction {

        public CurrentRankAction(Rank current) {
            setItem(ItemStackUtils.create(current.getRepresentation(),
                    current.getTag(),
                    "§cNum " + current.get(),
                    "§cCusto " + current.getCostFormatted()));
        }
    }

    class InfoAction extends BaseItemAction {

        public InfoAction() {
            setItem(ItemStackUtils.create(Material.PAPER,
                    "§aInformações",
                    "§eNovos ranks permitem a entrada",
                    "§eA diferentes lugares, dos quais",
                    "§eVocê recebe mais lucro em",
                    "§eem troca de mais dificuldade"));
        }
    }
}
