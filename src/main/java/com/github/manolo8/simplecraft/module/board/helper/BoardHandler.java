package com.github.manolo8.simplecraft.module.board.helper;

import com.github.manolo8.simplecraft.module.user.User;

public abstract class BoardHandler {

    protected final User user;

    public BoardHandler(User user) {
        this.user = user;
    }

    public abstract void tick();
}
