package com.github.manolo8.simplecraft.modules.skill.tools;

import com.github.manolo8.simplecraft.modules.user.User;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

public interface Interactable {

    boolean match(ItemStack itemStack, Action action);

    void onInteract(User user);
}
