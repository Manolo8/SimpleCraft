package com.github.manolo8.simplecraft.core.placeholder.tools;

import com.github.manolo8.simplecraft.core.placeholder.PlaceHolder;

class Content {

    String before;
    PlaceHolder holder;

    public Content(PlaceHolder holder, String before) {
        this.holder = holder;
        this.before = before;
    }
}
