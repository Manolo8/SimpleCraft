package com.github.manolo8.simplecraft.modules.skill.tools;

import com.github.manolo8.simplecraft.core.commands.inventory.ItemActionImpl;
import com.github.manolo8.simplecraft.modules.skill.Skill;
import com.github.manolo8.simplecraft.utils.ItemStackUtils;
import org.bukkit.Material;
import org.bukkit.Sound;

public class ActionUpgradeSkill extends ItemActionImpl {

    public ActionUpgradeSkill(Skill skill) {
        this.action = (user) -> {
            if (!(user.hasFreePoints() && skill.hasNextLevel())) {
                user.playSound(Sound.BLOCK_ANVIL_LAND, 20, 20);
                return;
            }

            boolean upgrade = skill.addLevel();

            if (upgrade) {
                user.cleanupSkills();
                user.playSound(Sound.ENTITY_PLAYER_LEVELUP, 20, 20);
            }

            user.getInventoryView().update();
        };

        this.itemStack = ItemStackUtils.create(Material.GRASS, skill.getUpgradeDisplay());
    }
}
