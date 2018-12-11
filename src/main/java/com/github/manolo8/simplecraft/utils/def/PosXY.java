package com.github.manolo8.simplecraft.utils.def;

import org.jetbrains.annotations.NotNull;

public class PosXY implements Comparable<PosXY> {

    public final int x;
    public final int z;

    public PosXY(int x, int z) {
        this.x = x;
        this.z = z;
    }

    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
    }

    @Override
    public int compareTo(@NotNull PosXY o) {
        int result = Integer.compare(x, o.x);
        if (result == 0) {
            result = Integer.compare(z, o.z);
        }
        return result;
    }
}
