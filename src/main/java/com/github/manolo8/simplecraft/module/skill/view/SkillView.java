package com.github.manolo8.simplecraft.module.skill.view;

import com.github.manolo8.simplecraft.core.commands.inventory.ItemAction;
import com.github.manolo8.simplecraft.core.commands.inventory.base.BaseItemAction;
import com.github.manolo8.simplecraft.core.commands.inventory.base.BaseView;
import com.github.manolo8.simplecraft.module.skill.Skill;
import com.github.manolo8.simplecraft.module.skill.SkillMagic;
import com.github.manolo8.simplecraft.module.skill.SkillService;
import com.github.manolo8.simplecraft.module.skill.user.SkillUser;
import com.github.manolo8.simplecraft.utils.mc.ItemStackUtils;
import org.bukkit.Material;
import org.bukkit.Sound;

import java.util.List;

import static com.github.manolo8.simplecraft.utils.mc.ItemStackUtils.create;

public class SkillView extends BaseView {

    private final SkillService service;

    public SkillView(SkillService service) {
        this.service = service;
    }


    @Override
    public int size() {
        return 18;
    }

    @Override
    public String getTitle() {
        return "Skills (" + user().skill().getLevel() + ")";
    }

    @Override
    public void createActions(List<ItemAction> actions) {

        SkillUser skillUser = user().skill();

        int i = 0;
        for (Skill loop : service.getTypes()) {
            Skill skill = skillUser.getSkill(loop.getType());

            if (skill == null) {
                actions.add(new BaseItemAction(create(loop.getMaterial(), loop.getDisplay(), "§eClique para mais", "§einformações!"))
                        .setAction(() -> getMain().add(new SkillBuyView(service, loop)))
                        .setIndex(i));
                actions.add(new BaseItemAction(create(Material.STONE, "§eSkill não evoluída!")).setIndex(i + 9));
            } else {
                actions.add(new BaseItemAction(create(skill.getMaterial(), skill.getHandlerId() + 1, skill.getDisplay(), skill.getLevelHandler().getInfo()))
                        .setAction(() -> getMain().add(new SkillUpgradeView(skill)))
                        .setIndex(i));
                if (skill.isActive()) {
                    if (skill instanceof SkillMagic && !((SkillMagic) skill).hasWand()) {
                        actions.add(new BaseItemAction((ItemStackUtils.create(Material.STICK, "§eClique para pegar a varinha")))
                                .setAction(() -> {
                                    ((SkillMagic) skill).giveWand();
                                    getMain().update();
                                })
                                .setIndex(i + 9));
                    } else {
                        actions.add(new BaseItemAction(create(Material.GREEN_WOOL, "§eClique para desativar!"))
                                .setAction(() -> {
                                    user().skill().disableSkill(skill);
                                    getMain().updateAll();
                                })
                                .setIndex(i + 9));
                    }
                } else {
                    actions.add(new BaseItemAction(create(Material.RED_WOOL, "§eClique para ativar!"))
                            .setAction(() -> {
                                if (skillUser.hasFreeSlot()) {
                                    skillUser.activeSkill(skill);
                                    getMain().updateAll();
                                } else getMain().add(new SkillInactiveOneView(skill));
                            })
                            .setIndex(i + 9));
                }
            }

            i++;
        }
    }

    @Override
    public void createPagination(List<ItemAction> pagination) {
        SkillUser skillUser = user().skill();

        int slots = skillUser.getFreeSlots();
        int points = skillUser.getFreePoints();

        pagination.add(new BaseItemAction(create(slots > 0 ? Material.LAVA_BUCKET : Material.BUCKET, "§aVocê tem " + slots + " slot(s)!")).setIndex(3));

        pagination.add(new BaseItemAction()
                .setItem(ItemStackUtils.create(Material.PAPER, "§eResetar skills por 10 cash"))
                .setAction(() -> {
                    if (user().money().withdrawCash(10)) {
                        skillUser.clearSkills();
                        getMain().updateAll();
                    } else {
                        user().playSound(Sound.BLOCK_ANVIL_LAND, 20, 20, false);
                    }
                })
                .setIndex(4));

        pagination.add(new BaseItemAction(create(points > 0 ? Material.MILK_BUCKET : Material.BUCKET, "§aVocê tem " + points + " ponto(s)!")).setIndex(5));
    }
}