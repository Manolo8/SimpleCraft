package com.github.manolo8.simplecraft.utils.def;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.*;
import java.util.function.ToIntFunction;

public class SoftList<E> implements List<E>, RandomAccess {

    private final ToIntFunction<E> toIntFunction;
    private Reference<E>[] data;
    private int size;

    public SoftList(ToIntFunction<E> toIntFunction) {
        this.toIntFunction = toIntFunction;
        this.data = new Reference[0];
        this.size = 0;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean contains(Object o) {
        throw new UnsupportedOperationException();
    }

    @NotNull
    @Override
    public Iterator<E> iterator() {
        throw new UnsupportedOperationException();
    }

    @NotNull
    @Override
    public Object[] toArray() {
        throw new UnsupportedOperationException();
    }

    @NotNull
    @Override
    public <T> T[] toArray(@NotNull T[] a) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean add(E e) {

        int index = binarySearch(toIntFunction.applyAsInt(e));

        if (index < 0) {

            index = (index * -1) - 1;

            if (data.length <= size) {
                data = Arrays.copyOf(data, data.length + 8);
            }

            System.arraycopy(data, index, data, index + 1,
                    data.length - 1 - index);

            data[index] = new SoftReference(e);
            size++;

            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean remove(Object o) {
        int index = binarySearch(toIntFunction.applyAsInt((E) o));

        if (index >= 0) {
            int numMoved = data.length - index - 1;
            if (numMoved > 0) {
                System.arraycopy(data, index + 1, data, index,
                        numMoved);
            }
            size--;

            return true;
        }
        return false;
    }

    @Override
    public boolean containsAll(@NotNull Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(@NotNull Collection<? extends E> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(int index, @NotNull Collection<? extends E> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(@NotNull Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public E get(int index) {
        SoftReference<E> reference = (SoftReference<E>) data[index];

        return reference == null ? null : reference.get();
    }

    public E search(int id) {
        id = binarySearch(id);

        return id >= 0 ? data[id].get() : null;
    }

    @Override
    public E set(int index, E element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(int index, E element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public E remove(int index) {
        return null;
    }

    @Override
    public int indexOf(Object o) {
        return 0;
    }

    @Override
    public int lastIndexOf(Object o) {
        throw new UnsupportedOperationException();
    }

    @NotNull
    @Override
    public ListIterator<E> listIterator() {
        throw new UnsupportedOperationException();
    }

    @NotNull
    @Override
    public ListIterator<E> listIterator(int index) {
        throw new UnsupportedOperationException();
    }

    @NotNull
    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        throw new UnsupportedOperationException();
    }

    private <R extends Reference<E>> int binarySearch(int key) {
        int low = 0;
        int high = size - 1;
        boolean last = true;
        R[] a = (R[]) data;

        while (low <= high) {
            int mid = (low + high) >>> 1;
            R midVal = a[mid];

            E e = midVal.get();

            if (e == null) {
                if (last) {
                    low = mid + 1;
                } else {
                    high = mid - 1;
                }
                continue;
            }

            int cmp = Integer.compare(toIntFunction.applyAsInt(e), key);

            if (cmp < 0) {
                low = mid + 1;
                last = true;
            } else if (cmp > 0) {
                high = mid - 1;
                last = false;
            } else
                return mid; // key found
        }

        return -(low + 1);  // key not found.
    }
}