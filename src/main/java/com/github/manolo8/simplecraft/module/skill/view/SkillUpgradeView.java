package com.github.manolo8.simplecraft.module.skill.view;

import com.github.manolo8.simplecraft.core.commands.inventory.ItemAction;
import com.github.manolo8.simplecraft.core.commands.inventory.base.BaseItemAction;
import com.github.manolo8.simplecraft.core.commands.inventory.base.BaseView;
import com.github.manolo8.simplecraft.module.skill.Skill;
import com.github.manolo8.simplecraft.module.skill.user.SkillUser;
import org.bukkit.Material;
import org.bukkit.Sound;

import java.util.List;

import static com.github.manolo8.simplecraft.utils.mc.ItemStackUtils.create;

public class SkillUpgradeView extends BaseView {

    private final Skill skill;

    public SkillUpgradeView(Skill skill) {
        this.skill = skill;
    }

    @Override
    public int size() {
        return 9;
    }

    @Override
    public String getTitle() {
        return "Evoluir skill (" + skill.getName() + ")";
    }

    @Override
    public void createActions(List<ItemAction> actions) {

        SkillUser skillUser = user().skill();

        if (skill.hasNextLevel()) {
            actions.add(new BaseItemAction(create(skill.getMaterial(), skill.getHandlerId() + 1, skill.getName(), skill.getLevelHandler().getInfo())).setIndex(3));
            actions.add(new BaseItemAction(create(Material.FEATHER, "§ePróximo nível")).setIndex(4));
            actions.add(new BaseItemAction(create(skill.getMaterial(), skill.getHandlerId() + 2, skill.getName(), skill.getNextHandler().getInfo())).setIndex(5));

            actions.add(new BaseItemAction(create(Material.DIAMOND, Math.max(1, skill.getMissing()), skill.getUpgradeDisplay(), "§eClique para evoluir!"))
                    .setAction(() -> {
                        if (skill.upgrade()) {
                            skillUser.cleanupSkills();
                            user().playSound(Sound.ENTITY_PLAYER_LEVELUP, 20, 20, false);
                            getMain().updateAll();
                        } else {
                            user().playSound(Sound.BLOCK_ANVIL_LAND, 20, 20, false);
                        }
                    })
                    .setIndex(7));
        } else {
            actions.add(new BaseItemAction(create(Material.PAPER, "§aSkill completa")).setIndex(4));
        }
    }

    @Override
    public void createPagination(List<ItemAction> pagination) {
        SkillUser skillUser = user().skill();

        int slots = skillUser.getFreeSlots();
        int points = skillUser.getFreePoints();

        pagination.add(new BaseItemAction(create(points > 0 ? Material.MILK_BUCKET : Material.BUCKET, "§aVocê tem " + points + " ponto(s)!")).setIndex(4));
    }
}
