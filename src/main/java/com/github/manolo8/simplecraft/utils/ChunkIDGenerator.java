package com.github.manolo8.simplecraft.utils;

import com.github.manolo8.simplecraft.modules.plot.data.PlotInfo;

public class ChunkIDGenerator {

    /**
     * Se retornar -1, não é um plot válido...
     *
     * @param xx valor x
     * @param zz valor z
     * @return id baseado no x e z
     */
    public static int generate(int xx, int zz) {
        if (xx == 0 && zz == 0) return 0;
        if (Math.abs(xx) + Math.abs(zz) > 2000) return -1;


        int id = 1;
        int x = 0;
        int z = 0;
        boolean isZLeg = false;
        boolean isNeg = false;
        int length = -1;
        int current = 0;

        while (true) {
            if (current < length)
                current++;
            else {
                current = 0;
                isZLeg ^= true;
                if (isZLeg) {
                    isNeg ^= true;
                    length++;
                }
            }

            if (isZLeg) z += (isNeg) ? -1 : 1;
            else x += (isNeg) ? -1 : 1;

            if (x == xx && z == zz) return id;

            id++;
        }
    }

    /**
     * Retorna um valor x e y baseado no id
     *
     * @return id, x e y
     */
    public static PlotInfo generate(int id) {
        int x = 0;
        int z = 0;
        boolean isZLeg = false;
        boolean isNeg = false;
        int length = -1;
        int current = 0;
        int oldId = id;

        while (id > 0) {
            id--;
            if (current < length)
                current++;
            else {
                current = 0;
                isZLeg ^= true;
                if (isZLeg) {
                    isNeg ^= true;
                    length++;
                }
            }

            if (isZLeg) z += (isNeg) ? -1 : 1;
            else x += (isNeg) ? -1 : 1;
        }

        return new PlotInfo(oldId, x, z);
    }
}
