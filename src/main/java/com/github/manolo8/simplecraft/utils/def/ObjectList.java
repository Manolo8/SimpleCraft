package com.github.manolo8.simplecraft.utils.def;

import java.util.Arrays;

public class ObjectList<E> {

    private Object[] data;
    private int size;

    public ObjectList() {
        this.data = new Object[0];
    }

    public boolean contains(E value) {
        return Arrays.binarySearch(data, 0, size, value) >= 0;
    }

    public void add(final E element) {
        int index = Arrays.binarySearch(data, 0, size, element);

        if (index < 0) {

            index = (index * -1) - 1;

            if (data.length <= size) {
                data = Arrays.copyOf(data, data.length + 8);
            }

            System.arraycopy(data, index, data, index + 1,
                    data.length - 1 - index);

            data[index] = element;
            size++;
        }
    }

    public E closest(E element) {
        int index = indexOfClosest(element);

        if (index >= 0) {
            return (E) data[index];
        } else {
            return null;
        }
    }

    public int indexOfClosest(E element) {
        int index = Arrays.binarySearch(data, 0, size, element);

        return index >= 0 ? index : (index * -1) - 1;
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

    public E[] values() {
        return (E[]) data;
    }
}
