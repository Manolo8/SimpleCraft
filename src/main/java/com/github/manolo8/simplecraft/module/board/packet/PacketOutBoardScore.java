package com.github.manolo8.simplecraft.module.board.packet;

import com.github.manolo8.simplecraft.utils.def.PacketAccessor;
import net.minecraft.server.v1_13_R2.PacketPlayOutScoreboardScore;
import net.minecraft.server.v1_13_R2.ScoreboardServer;

import java.lang.reflect.Field;

public class PacketOutBoardScore extends PacketAccessor<PacketPlayOutScoreboardScore> {

    private Field fieldName;
    private Field fieldObjective;
    private Field fieldScore;
    private Field fieldAction;

    public PacketOutBoardScore() {
        super(PacketPlayOutScoreboardScore.class);

        fieldName = getField("a");
        fieldObjective = getField("b");
        fieldScore = getField("c");
        fieldAction = getField("d");
    }

    public PacketOutBoardBuilder create(String name, String team, int score, ScoreboardServer.Action action) {
        return new PacketOutBoardBuilder(name, team, score, action);
    }

    public class PacketOutBoardBuilder extends PacketAccessor.PacketBuilder {

        public PacketOutBoardBuilder(String name, String team, int score, ScoreboardServer.Action action) {
            try {

                fieldName.set(packet, name);
                fieldObjective.set(packet, team);
                fieldScore.set(packet, score);
                fieldAction.set(packet, action);

            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}
