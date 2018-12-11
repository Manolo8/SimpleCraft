package com.github.manolo8.simplecraft.interfaces;

import com.github.manolo8.simplecraft.module.user.User;

public interface Proximity {

    /**
     * If an container was multiple chunks, that method can be
     * called multiple times
     *
     * @param user when an user is nearby of this container
     */
    void onNearby(User user);

    /**
     * If an container was multiple chunks, that method can be
     * called multiple times
     *
     * @param user when an user is away from this container
     */
    void onAway(User user);

}
