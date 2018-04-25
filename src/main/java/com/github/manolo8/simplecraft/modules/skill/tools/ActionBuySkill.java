package com.github.manolo8.simplecraft.modules.skill.tools;

import com.github.manolo8.simplecraft.core.commands.inventory.ItemActionImpl;
import com.github.manolo8.simplecraft.modules.skill.Skill;
import com.github.manolo8.simplecraft.modules.skill.data.SkillRepository;
import com.github.manolo8.simplecraft.utils.ItemStackUtils;
import org.bukkit.Material;
import org.bukkit.Sound;

public class ActionBuySkill extends ItemActionImpl {

    private Skill skill;

    public ActionBuySkill(Skill skill) {
        this.skill = skill;

        this.action = (user) -> {
            if (user.hasFreePoints()) {
                SkillRepository.instance.create(user, skill.getType());
                user.getInventoryView().update();
                user.cleanupSkills();
            } else user.playSound(Sound.BLOCK_ANVIL_LAND, 20, 20);
        };

        this.itemStack = ItemStackUtils.create(Material.DIRT, "Fazer o primeiro upgrade dessa skill");
    }
}
