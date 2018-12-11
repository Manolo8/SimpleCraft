package com.github.manolo8.simplecraft.module.skill.view;

import com.github.manolo8.simplecraft.core.commands.inventory.ItemAction;
import com.github.manolo8.simplecraft.core.commands.inventory.base.BaseItemAction;
import com.github.manolo8.simplecraft.core.commands.inventory.base.BaseView;
import com.github.manolo8.simplecraft.module.skill.Skill;
import com.github.manolo8.simplecraft.module.skill.user.SkillUser;
import com.github.manolo8.simplecraft.utils.mc.ItemStackUtils;

import java.util.List;

public class SkillInactiveOneView extends BaseView {

    private Skill toActive;

    public SkillInactiveOneView(Skill toActive) {
        this.toActive = toActive;
    }

    @Override
    public int size() {
        return 9;
    }

    @Override
    public String getTitle() {
        return "Escolha uma skill para desativar!";
    }

    @Override
    public void createActions(List<ItemAction> actions) {
        SkillUser skillUser = user().skill();

        List<Skill> activeSkills = skillUser.getActiveSkills();

        for (int i = 0; i < activeSkills.size(); i++) {

            Skill skill = activeSkills.get(i);
            BaseItemAction loop = new BaseItemAction();
            loop.setItem(ItemStackUtils.create(skill.getMaterial(), skill.getDisplay(), skill.getLevelHandler().getInfo()));
            loop.setIndex(i);

            loop.setAction(() -> {
                skillUser.disableSkill(skill);
                skillUser.activeSkill(toActive);
                getMain().back();
            });

            actions.add(loop);
        }
    }

    @Override
    public void createPagination(List<ItemAction> pagination) {
        BaseItemAction skillInfo = new BaseItemAction();
        skillInfo.setItem(ItemStackUtils.create(toActive.getMaterial(), toActive.getDisplay(), toActive.getLevelHandler().getInfo()));
        skillInfo.setIndex(4);
        pagination.add(skillInfo);
    }
}
