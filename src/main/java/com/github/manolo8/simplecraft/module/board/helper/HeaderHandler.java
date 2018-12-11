package com.github.manolo8.simplecraft.module.board.helper;

import com.github.manolo8.simplecraft.core.placeholder.tools.HolderString;
import com.github.manolo8.simplecraft.module.board.BoardItem;
import com.github.manolo8.simplecraft.module.board.packet.PacketOutObjective;
import com.github.manolo8.simplecraft.module.user.User;
import net.minecraft.server.v1_13_R2.IScoreboardCriteria;

public class HeaderHandler extends BoardHandler {

    private static PacketOutObjective packetOutObjective = new PacketOutObjective();

    private HolderString<?> header;
    private String oldValue;

    public HeaderHandler(User user) {
        super(user);

        this.header = HolderString.EMPTY;
    }

    public void setHeader(BoardItem header) {
        this.header = header == null ? HolderString.EMPTY : new HolderString<>(user, header);
    }

    public void sendRemovePacket() {
        if (oldValue != null) {
            packetOutObjective.create("sidebar", oldValue, IScoreboardCriteria.EnumScoreboardHealthDisplay.INTEGER, 1).send(user);
        }
    }

    @Override
    public void tick() {

        header.checkModified();

        if (oldValue != header.value()) {
            packetOutObjective.create("sidebar", oldValue = header.value(), IScoreboardCriteria.EnumScoreboardHealthDisplay.INTEGER, 2).send(user);
        }
    }
}
