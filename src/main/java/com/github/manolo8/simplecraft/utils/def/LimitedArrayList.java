package com.github.manolo8.simplecraft.utils.def;

import java.util.ArrayList;

public class LimitedArrayList<E> extends ArrayList<E> {

    private int limit;

    public LimitedArrayList(int limit) {
        this.limit = limit;
    }

    @Override
    public boolean add(E e) {
        super.add(0, e);

        if (size() > limit) remove(limit);

        return true;
    }

    public int count(E e) {
        int counter = 0;

        for (E loop : this)
            if (loop.equals(e))
                counter++;

        return counter;
    }
}
