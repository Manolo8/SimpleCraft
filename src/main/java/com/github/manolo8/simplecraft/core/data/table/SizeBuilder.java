package com.github.manolo8.simplecraft.core.data.table;

public class SizeBuilder {

    private int size;
    private int decimals;

    public SizeBuilder(int size, int decimals) {
        this.size = size;
        this.decimals = decimals;
    }

    public SizeBuilder(Size size) {
        this(size.value(), size.decimals());
    }

    public SizeBuilder(int size) {
        this.size = size;
    }

    @Override
    public String toString() {
        if (size == 0) return "";
        return "(" + size + (decimals != 0 ? "," + decimals : "") + ")";
    }
}
