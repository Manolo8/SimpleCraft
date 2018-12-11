package com.github.manolo8.simplecraft.utils.def;

public class IncrementInteger {

    private int value;

    public IncrementInteger(int value) {
        this.value = value;
    }

    public void increase() {
        value++;
    }

    public void decrease() {
        value--;
    }

    public int get() {
        return value;
    }

    public void increasep() {
        this.value++;
    }
}