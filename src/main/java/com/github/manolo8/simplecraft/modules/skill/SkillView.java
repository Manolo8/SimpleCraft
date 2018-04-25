package com.github.manolo8.simplecraft.modules.skill;

import com.github.manolo8.simplecraft.core.commands.inventory.BaseView;
import com.github.manolo8.simplecraft.core.commands.inventory.ItemAction;
import com.github.manolo8.simplecraft.core.commands.inventory.ItemActionImpl;
import com.github.manolo8.simplecraft.modules.skill.data.SkillRepository;
import com.github.manolo8.simplecraft.modules.skill.tools.ActionBuySkill;
import com.github.manolo8.simplecraft.modules.skill.tools.ActionUpgradeSkill;
import com.github.manolo8.simplecraft.modules.skill.types.SkillMagicAreaDamage;
import com.github.manolo8.simplecraft.modules.user.User;
import com.github.manolo8.simplecraft.utils.ItemStackUtils;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public class SkillView extends BaseView {

    private User user;


    public SkillView(User user) {
        this.user = user;
    }

    @Override
    public String getTitle() {
        return "Skills";
    }

    @Override
    public List<? extends ItemAction> getActions() {

        List<ItemAction> actions = new ArrayList<>();
        boolean column = true;
        int current = 0;

        ItemActionImpl freePoints = new ItemActionImpl();
        int free = user.getFreePoints();

        freePoints.setItemStack(ItemStackUtils.create(free > 0 ? Material.LAVA_BUCKET : Material.BUCKET, "§aVocê tem " + free + " pontos!"));
        freePoints.setIndex(22);
        actions.add(freePoints);

        for (Skill loop : SkillRepository.types) {
            Skill skill = user.getSkill(loop.getType());

            ItemActionImpl itemAction = new ItemActionImpl();
            ItemAction itemAction2;

            if (skill == null) {
                itemAction.setItemStack(ItemStackUtils.create(loop.getMaterial(), loop.getName(), loop.getLevelHandler().getInfo()));
                itemAction2 = new ActionBuySkill(loop);
            } else {
                itemAction.setItemStack(ItemStackUtils.create(loop.getMaterial(), skill.getDisplay(), skill.getLevelHandler().getInfo()));
                itemAction2 = new ActionUpgradeSkill(skill);

                if (skill instanceof SkillMagic) {
                    ItemActionImpl getWand = new ItemActionImpl();
                    getWand.setIndex(current + (column ? 0 : 3));
                    getWand.setItemStack(((SkillMagic) skill).getWand());
                    getWand.setAction((user1 -> {
                        ((SkillMagic) skill).giveWand(user);
                    }));
                    actions.add(getWand);
                }
            }

            itemAction.setIndex(current + 1);
            itemAction2.setIndex(current + 2);

            actions.add(itemAction);
            actions.add(itemAction2);

            if (column) current += 5;
            else current += 4;
            column = !column;
        }

        return actions;
    }
}
