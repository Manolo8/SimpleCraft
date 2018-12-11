package com.github.manolo8.simplecraft.module.skill.view;

import com.github.manolo8.simplecraft.core.commands.inventory.ItemAction;
import com.github.manolo8.simplecraft.core.commands.inventory.base.BaseItemAction;
import com.github.manolo8.simplecraft.core.commands.inventory.base.BaseView;
import com.github.manolo8.simplecraft.module.skill.Skill;
import com.github.manolo8.simplecraft.module.skill.SkillService;
import com.github.manolo8.simplecraft.module.skill.user.SkillUser;
import org.bukkit.Material;
import org.bukkit.Sound;

import java.util.List;

import static com.github.manolo8.simplecraft.utils.mc.ItemStackUtils.create;

public class SkillBuyView extends BaseView {

    private final SkillService service;
    private final Skill skill;

    public SkillBuyView(SkillService service, Skill skill) {
        this.service = service;
        this.skill = skill;
    }

    @Override
    public int size() {
        return 9;
    }

    @Override
    public String getTitle() {
        return "Upar nível 1 (" + skill.getName() + ")";
    }

    @Override
    public void createActions(List<ItemAction> actions) {

        SkillUser skillUser = user().skill();

        actions.add(new BaseItemAction(create(skill.getMaterial(), skill.getDisplay(), skill.getLevelHandler().getInfo())).setIndex(3));
        actions.add(new BaseItemAction(create(Material.ANVIL, "§aFazer o primeiro upgrade?"))
                .setAction(() -> {
                    if (skillUser.hasFreePoints()) {
                        service.create(user(), skill.getType());
                        getMain().back();
                    } else user().playSound(Sound.BLOCK_ANVIL_LAND, 20, 20, false);
                })
                .setIndex(5));
    }

    @Override
    public void createPagination(List<ItemAction> pagination) {
        SkillUser skillUser = user().skill();

        int slots = skillUser.getFreeSlots();
        int points = skillUser.getFreePoints();

        pagination.add(new BaseItemAction(create(points > 0 ? Material.MILK_BUCKET : Material.BUCKET, "§aVocê tem " + points + " ponto(s)!")).setIndex(4));
    }
}
