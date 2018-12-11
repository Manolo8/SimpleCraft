package com.github.manolo8.simplecraft.module.skill.types;

import com.github.manolo8.simplecraft.interfaces.Tickable;
import com.github.manolo8.simplecraft.module.skill.Level;
import com.github.manolo8.simplecraft.module.skill.Skill;
import com.github.manolo8.simplecraft.module.skill.tools.BlockFarm;
import com.github.manolo8.simplecraft.module.user.UserService;
import com.github.manolo8.simplecraft.utils.def.StringUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SkillAutoPlant extends Skill {

    private static Level[] levels;

    static {
        levels = new Level[8];

        levels[0] = new Beginner(1, 2, 8);
        levels[1] = new Beginner(5, 2.2, 7);
        levels[2] = new Beginner(10, 2.5, 6);
        levels[3] = new Beginner(15, 2.9, 5);
        levels[4] = new Beginner(25, 3, 4);
        levels[5] = new Beginner(35, 3.2, 3);
        levels[6] = new Beginner(45, 3.5, 2);
        levels[7] = new Beginner(60, 4, 1);
    }

    public SkillAutoPlant() {
        super(levels, "Replantar", 8, Material.GOLDEN_HOE);
    }

    @Override
    public Skill newInstance() {
        return new SkillAutoPlant();
    }

    static class Beginner extends Level implements BlockFarm, Tickable {

        private List<Replant> replantList;
        private int chance;

        public Beginner(int upgradeAmount, double cost, int chance) {
            super(upgradeAmount, cost);
            this.info = Arrays.asList("§aReplantador automatico.",
                    "§aQuando colher, tem " + StringUtils.doubleToString0D((1D / chance) * 100) + "% de chance",
                    "§ade replantar, se estiver usando enxada",
                    "§c(Custa " + cost + " de mana por uso)");

            replantList = new ArrayList();
            this.chance = chance;
        }


        @Override
        public void onFarm(BlockBreakEvent event) {
            Block block = event.getBlock();
            Material type = block.getType();

            if (type == Material.POTATO || type == Material.CARROT || type == Material.WHEAT) {
                if (block.getState().getRawData() == 7) {

                    if (random.nextInt(chance) == 0) replantList.add(new Replant(block, type));

                } else {
                    event.setCancelled(true);
                }
            }
        }

        @Override
        public void tick() {
            if (replantList.size() != 0 && UserService.tick % 80 == 1) {

                for (Replant replant : replantList) {
                    replant.block.setType(replant.material);
                }

                replantList.clear();
            }
        }
    }

    static class Replant {
        Block block;
        Material material;

        public Replant(Block block, Material material) {
            this.block = block;
            this.material = material;
        }
    }
}