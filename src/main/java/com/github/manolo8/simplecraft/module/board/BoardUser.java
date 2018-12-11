package com.github.manolo8.simplecraft.module.board;

import com.github.manolo8.simplecraft.module.board.helper.BasicHandler;
import com.github.manolo8.simplecraft.module.board.helper.BoardHandler;
import com.github.manolo8.simplecraft.module.board.helper.HeaderHandler;
import com.github.manolo8.simplecraft.module.board.helper.TabHandler;
import com.github.manolo8.simplecraft.module.board.packet.PacketObjective;
import com.github.manolo8.simplecraft.module.board.packet.PacketOutObjective;
import com.github.manolo8.simplecraft.module.user.User;
import net.minecraft.server.v1_13_R2.IScoreboardCriteria;

import java.util.ArrayList;
import java.util.List;

public class BoardUser {

    public static final Object LOCKER = new Object();
    private static PacketObjective packetDisplayObjective = new PacketObjective();
    private static PacketOutObjective packetOutObjective = new PacketOutObjective();

    private final User user;

    private final List<BoardHandler> items;

    private final HeaderHandler headerHandler;
    private final TabHandler tabHandler;

    public BoardUser(User user) {
        this.user = user;
        this.items = new ArrayList<>();

        this.headerHandler = new HeaderHandler(user);
        this.tabHandler = new TabHandler(user);

        this.items.add(tabHandler);
        this.items.add(headerHandler);

        packetOutObjective.create("sidebar", user.identity().getName(), IScoreboardCriteria.EnumScoreboardHealthDisplay.INTEGER, 0).send(user);
        packetDisplayObjective.create(1, "sidebar").send(user);
    }

    //======================================================
    //========================METHODS=======================
    //======================================================

    public void add(BoardItem item) {
        toggle(item.getPriority(), item);
    }

    public void remove(BoardItem item) {
        toggle(item.getPriority(), null);
    }

    private void toggle(int priority, BoardItem item) {
        switch (priority) {
            case -1:
                headerHandler.setHeader(item);
                break;
            case -2:
                tabHandler.setHeader(item);
                break;
            case -3:
                tabHandler.setFooter(item);
                break;
            default:
                if (item == null) {
                    for (int i = 0, itemsSize = items.size(); i < itemsSize; i++) {
                        BoardHandler handler = items.get(i);
                        if (handler instanceof BasicHandler && ((BasicHandler) handler).matchPriority(priority)) {
                            ((BasicHandler) handler).sendRemovePackets();
                            items.remove(i);
                            break;
                        }
                    }
                } else {
                    this.items.add(new BasicHandler(user, item));
                }
        }
    }

    public void clear() {
        headerHandler.sendRemovePacket();
    }

    //======================================================
    //=======================_METHODS=======================
    //======================================================


    public void tick() {
        synchronized (LOCKER) {
            for (BoardHandler handler : items) {
                handler.tick();
            }
        }
    }
}
