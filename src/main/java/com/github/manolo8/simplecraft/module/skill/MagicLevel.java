package com.github.manolo8.simplecraft.module.skill;

import com.github.manolo8.simplecraft.module.skill.tools.Interactable;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

public abstract class MagicLevel extends Level<SkillMagic> implements Interactable {

    private int max;
    private int cooldown;

    public MagicLevel(int upgradeAmount, int cost, int max, int cooldown) {
        super(upgradeAmount, cost);
        this.max = max;
        this.cooldown = cooldown;
    }

    @Override
    public boolean match(ItemStack itemStack, Action action) {
        return skill.isWand(itemStack) && (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK);
    }

    @Override
    public void onInteract() {
        if (!skill.canTakeMana()) {
            user().sendAction("§cSem mana!");
        } else if (!skill.canUse()) {
            user().sendAction("§cHabilidade em cooldown!");
        } else if (!user().iContainer().protection().canUseSkill(skill.getType())) {
            user().sendAction("§cEssa skill está desabilitada aqui!");
        } else {

            skill.use();
            skill.takeMana();

            skill.updateWand();

            handle();
        }
    }

    public abstract void handle();

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public int getCooldown() {
        return cooldown;
    }
}
