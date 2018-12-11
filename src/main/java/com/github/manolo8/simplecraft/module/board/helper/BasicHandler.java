package com.github.manolo8.simplecraft.module.board.helper;

import com.github.manolo8.simplecraft.core.placeholder.tools.HolderString;
import com.github.manolo8.simplecraft.module.board.BoardItem;
import com.github.manolo8.simplecraft.module.board.packet.PacketOutBoardScore;
import com.github.manolo8.simplecraft.module.user.User;
import net.minecraft.server.v1_13_R2.ScoreboardServer;

public class BasicHandler extends BoardHandler {

    private static PacketOutBoardScore packetOutBoardScore = new PacketOutBoardScore();

    private final HolderString<BoardItem> holder;
    private String oldValue;

    public BasicHandler(User user, BoardItem item) {
        super(user);

        this.holder = new HolderString<>(user, item);
    }

    public boolean matchPriority(int priority) {
        return holder.provider().getPriority() == priority;
    }

    public void sendRemovePackets() {
        if (oldValue != null) {
            packetOutBoardScore.create(oldValue, "sidebar", holder.provider().getPriority(), ScoreboardServer.Action.REMOVE).send(user);
        }
    }

    @Override
    public void tick() {

        holder.checkModified();

        if (oldValue != holder.value()) {

            //CHECK
            if (oldValue != null) {
                packetOutBoardScore.create(oldValue, "sidebar", holder.provider().getPriority(), ScoreboardServer.Action.REMOVE).send(user);
            }

            packetOutBoardScore.create(oldValue = holder.value(), "sidebar", holder.provider().getPriority(), ScoreboardServer.Action.CHANGE).send(user);
        }
    }
}
