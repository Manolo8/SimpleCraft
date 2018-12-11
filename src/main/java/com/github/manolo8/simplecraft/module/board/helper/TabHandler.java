package com.github.manolo8.simplecraft.module.board.helper;

import com.github.manolo8.simplecraft.core.placeholder.tools.HolderString;
import com.github.manolo8.simplecraft.module.board.BoardItem;
import com.github.manolo8.simplecraft.module.board.packet.PacketPlayerListText;
import com.github.manolo8.simplecraft.module.user.User;

public class TabHandler extends BoardHandler {

    private static PacketPlayerListText packetPlayerListText = new PacketPlayerListText();

    private HolderString<?> header;
    private HolderString<?> footer;

    private String oldHeader;
    private String oldFooter;

    public TabHandler(User user) {
        super(user);

        this.header = HolderString.EMPTY;
        this.footer = HolderString.EMPTY;
    }


    public void setHeader(BoardItem header) {
        this.header = header == null ? HolderString.EMPTY : new HolderString<>(user, header);
    }

    public void setFooter(BoardItem footer) {
        this.footer = footer == null ? HolderString.EMPTY : new HolderString<>(user, footer);
    }

    @Override
    public void tick() {

        header.checkModified();
        footer.checkModified();

        if (oldHeader != header.value() || oldFooter != footer.value()) {
            packetPlayerListText
                    .create((oldHeader = header.value()).replace('^', '\n'),
                            (oldFooter = footer.value()).replace('^', '\n'))
                    .send(user);
        }

    }
}
