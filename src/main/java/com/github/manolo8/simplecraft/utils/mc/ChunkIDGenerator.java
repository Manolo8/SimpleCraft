package com.github.manolo8.simplecraft.utils.mc;

import com.github.manolo8.simplecraft.utils.def.ObjectList;
import com.github.manolo8.simplecraft.utils.def.PosXY;

import java.util.Random;

public class ChunkIDGenerator {

    /**
     * Se retornar -1, não é um plot válido...
     *
     * @param xx valor posX
     * @param zz valor posZ
     * @return id baseado no posX e posZ
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
     * Retorna um valor posX e y baseado no id
     *
     * @return id, posX e y
     */
    public static PosXY generate(int id) {

        int x = 0;
        int z = 0;
        boolean isZLeg = false;
        boolean isNeg = false;
        int length = -1;
        int current = 0;

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

        return new PosXY(x, z);
    }

    public static void main(String[] args) {

        Move[] cache = new Move[500];
        ObjectList<PosXY> cache2 = new ObjectList<>();

        Move move = new Move();
        for (int i = 0; i < 500000; i++) {
            move.next();

            if (i % 1000 == 0) {

                int o = i / 1000;

                cache[o] = move.copy();
                cache2.add(new PosXY(move.x, move.z));
            }

        }

        Random random = new Random();

        while (true) {

            long time = System.nanoTime();

            PosXY toFound = new PosXY(4, 5);

            Move closest = cache[cache2.indexOfClosest(toFound)].copy();

            int comp = toFound.compareTo(new PosXY(closest.x, closest.z));

            if (comp == 0) {

            } else if (comp < 0) {

                while (closest.x != toFound.x || closest.z != toFound.z) {
                    closest.back();
                }

            } else {

                while (closest.x != toFound.x || closest.z != toFound.z) {
                    closest.back();
                }
            }

            System.out.println(System.nanoTime() - time);
        }

    }

    static class Move {

        int id;
        int x;
        int z;
        int length;
        int current;
        boolean isZLeg;
        boolean isNeg;

        public Move() {
            length = -1;
        }

        Move copy() {
            Move copy = new Move();

            copy.id = id;
            copy.x = x;
            copy.z = z;
            copy.current = current;
            copy.length = length;
            copy.isZLeg = isZLeg;
            copy.isNeg = isNeg;

            return copy;
        }

        void next() {
            id++;

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

        void back() {
            id--;

            if (isZLeg) z += (isNeg) ? 1 : -1;
            else x += (isNeg) ? 1 : -1;

            if (current > 0)
                current--;
            else {
                if (isZLeg) {
                    isNeg ^= true;
                    length--;
                }
                isZLeg ^= true;
                current = length;
            }
        }

    }

}
