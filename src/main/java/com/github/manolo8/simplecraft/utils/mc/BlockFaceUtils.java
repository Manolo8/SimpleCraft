package com.github.manolo8.simplecraft.utils.mc;

import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

public class BlockFaceUtils {

    public static BlockFace fromInt(int face) {
        if (face == 0) return BlockFace.SOUTH;
        if (face == 1) return BlockFace.NORTH;
        if (face == 2) return BlockFace.EAST;
        if (face == 3) return BlockFace.WEST;
        return BlockFace.SOUTH;
    }

    public static int toInt(BlockFace face) {
        if (face == BlockFace.SOUTH) return 0;
        if (face == BlockFace.NORTH) return 1;
        if (face == BlockFace.EAST) return 2;
        if (face == BlockFace.WEST) return 3;

        return 0;
    }

    public static BlockFace getFace(Player player) {
        float yaw = player.getLocation().getYaw();
        //Como assim? O yaw não era negativo '-'
        if (yaw < 0) yaw += 360;
        if (yaw < 45) return BlockFace.SOUTH;
        else if (yaw < 135) return BlockFace.WEST;
        else if (yaw < 225) return BlockFace.NORTH;
        else if (yaw < 315) return BlockFace.EAST;
        else return BlockFace.SOUTH;
    }
}
