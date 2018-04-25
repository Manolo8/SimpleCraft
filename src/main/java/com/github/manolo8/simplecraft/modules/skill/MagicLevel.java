package com.github.manolo8.simplecraft.modules.skill;

import com.github.manolo8.simplecraft.modules.skill.tools.Interactable;
import com.github.manolo8.simplecraft.modules.user.User;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

public abstract class MagicLevel extends Level<SkillMagic> implements Interactable {

    private int max;
    private int cooldown;

    public MagicLevel(int upgradeAmount, int max, int cooldown) {
        super(upgradeAmount);
        this.max = max;
        this.cooldown = cooldown;
    }

    @Override
    public boolean match(ItemStack itemStack, Action action) {
        return skill.isWand(itemStack) && (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK);
    }

    @Override
    public void onInteract(User user) {

        if (!skill.useIfCan()) {
            user.sendAction("§cHabilidade em cooldown!");
            return;
        }

        skill.updateWand(user);

        handle(user);
    }

    public abstract void handle(User user);

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public int getCooldown() {
        return cooldown;
    }

    public void setCooldown(int cooldown) {
        this.cooldown = cooldown;
    }
}
