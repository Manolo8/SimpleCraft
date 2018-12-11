package com.github.manolo8.simplecraft.module.plot.view;

import com.github.manolo8.simplecraft.core.commands.inventory.ItemAction;
import com.github.manolo8.simplecraft.core.commands.inventory.base.BaseItemAction;
import com.github.manolo8.simplecraft.core.commands.inventory.base.BaseView;
import com.github.manolo8.simplecraft.module.plot.Plot;
import com.github.manolo8.simplecraft.module.plot.member.PlotMember;
import com.github.manolo8.simplecraft.utils.def.Flag;
import com.github.manolo8.simplecraft.utils.mc.ItemStackUtils;
import org.bukkit.Material;

import java.util.List;

public class PlotMembersView extends BaseView {

    private final Plot plot;

    public PlotMembersView(Plot plot) {
        this.plot = plot;
    }


    @Override
    public String getTitle() {
        return "Membros";
    }

    @Override
    public void createActions(List<ItemAction> actions) {
        BaseItemAction flagBuild = new BaseItemAction();
        flagBuild.setItem(ItemStackUtils.create(Material.STONE, "§aPode construir"));
        flagBuild.setIndex(9);
        actions.add(flagBuild);

        BaseItemAction flagBreak = new BaseItemAction();
        flagBreak.setItem(ItemStackUtils.create(Material.DIAMOND_PICKAXE, "§aPode quebrar blocos"));
        flagBreak.setIndex(18);
        actions.add(flagBreak);

        BaseItemAction flagInteract = new BaseItemAction();
        flagInteract.setItem(ItemStackUtils.create(Material.CHEST, "§aInteragir", "§eCom baús", "§eCom máquinas", "§eCom spawners"));
        flagInteract.setIndex(27);
        actions.add(flagInteract);

        BaseItemAction flagModules = new BaseItemAction();
        flagModules.setItem(ItemStackUtils.create(Material.GOLDEN_PICKAXE, "§aEspeciais:", "§eQuebrar spawners", "§eQuebrar máquinas", "§eTirar módulos de máquinas", "§eTrocar ovo dos spawners"));
        flagModules.setIndex(36);
        actions.add(flagModules);

        List<PlotMember> members = plot.getMembers();

        for (int i = 0; i < members.size(); i++) {
            PlotMember member = members.get(i);

            BaseItemAction remove = new BaseItemAction();
            remove.setItem(ItemStackUtils.createSkullByIdentity(member.getIdentity(), "§e" + member.getIdentity().getName(), "§cClique para remover!"));
            remove.setAction(() -> {
                plot.removeMember(member);
                getMain().update();
            });
            remove.setIndex(i + 1);
            actions.add(remove);

            List<Flag.Toggle> togglers = member.flags().getTogglers();
            for (int i1 = 0; i1 < togglers.size(); i1++) {

                Flag.Toggle toggle = togglers.get(i1);
                BaseItemAction flag = new BaseItemAction();

                boolean value = toggle.isTrue(member.flags());

                flag.setItem(ItemStackUtils.create(value ? Material.GREEN_WOOL : Material.RED_WOOL, "§aClique para alterar!"));
                flag.setAction(() -> {
                    toggle.set(member, PlotMember::flags, !value);
                    getMain().update();
                });

                flag.setIndex(((i1 + 1) * 9) + i + 1);
                actions.add(flag);
            }
        }

    }
}
