package com.github.manolo8.simplecraft.utils.location;


public class CoordinatePair {
    public final int x;
    public final int z;

    public CoordinatePair(int var1, int var2) {
        this.x = var1;
        this.z = var2;
    }

    public static long pair(int x, int z) {
        return (long) x & 4294967295L | ((long) z & 4294967295L) << 32;
    }

    public int hashCode() {
        int var1 = 1664525 * this.x + 1013904223;
        int var2 = 1664525 * (this.z ^ -559038737) + 1013904223;
        return var1 ^ var2;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else if (!(object instanceof CoordinatePair)) {
            return false;
        } else {
            CoordinatePair var2 = (CoordinatePair) object;
            return this.x == var2.x && this.z == var2.z;
        }
    }
}
