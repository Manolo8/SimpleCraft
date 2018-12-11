package com.github.manolo8.simplecraft.utils.def;

import java.util.Arrays;

public class IntegerList {

    private int[] data;
    private int size;

    public IntegerList() {
        this.data = new int[0];
    }

    public IntegerList(byte[] b) {
        if (b == null) {
            this.data = new int[0];
        } else {

            this.data = new int[b.length / 4];
            this.size = b.length / 4;

            int off = 0;

            for (int i = 0; i < data.length; i++) {
                this.data[i] = ((b[off++]) << 24) +
                        ((b[off++] & 0xFF) << 16) +
                        ((b[off++] & 0xFF) << 8) +
                        ((b[off++] & 0xFF));
            }
        }
    }

    public byte[] toBytes() {

        trimToSize();

        byte[] b = new byte[data.length * 4];

        int off = 0;

        for (int val : data) {
            b[off++] = (byte) (val >>> 24);
            b[off++] = (byte) (val >>> 16);
            b[off++] = (byte) (val >>> 8);
            b[off++] = (byte) (val);
        }

        return b;
    }

    public boolean contains(int value) {
        return Arrays.binarySearch(data, 0, size, value) >= 0;
    }

    public void add(final int id) {
        int index = Arrays.binarySearch(data, 0, size, id);

        if (index < 0) {

            index = (index * -1) - 1;

            if (data.length <= size) {
                data = Arrays.copyOf(data, data.length + 8);
            }

            System.arraycopy(data, index, data, index + 1,
                    data.length - 1 - index);

            data[index] = id;
            size++;
        }
    }

    public void remove(final int id) {
        final int index = Arrays.binarySearch(data, 0, size, id);

        if (index >= 0) {
            int numMoved = data.length - index - 1;
            if (numMoved > 0) {
                System.arraycopy(data, index + 1, data, index,
                        numMoved);
            }
            data[--size] = 0;
        }
    }

    public void trimToSize() {
        if (data.length > size) {
            data = Arrays.copyOf(data, size);
        }
    }

    public int[] values() {
        return data;
    }
}
