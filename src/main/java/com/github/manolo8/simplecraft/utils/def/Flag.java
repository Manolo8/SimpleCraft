package com.github.manolo8.simplecraft.utils.def;

import com.github.manolo8.simplecraft.core.data.model.base.BaseEntity;

import java.util.Arrays;
import java.util.List;

public abstract class Flag {

    private byte[] data;

    public Flag() {
        this.data = new byte[0];
    }

    public Flag(byte[] data) {
        this.data = data == null ? new byte[0] : data;
    }

    public byte[] get() {
        return data;
    }

    public boolean has(int id) {
        int index = id / 8;
        return index < data.length && (data[index] >> (id % 8) & 1) == 1;
    }

    public void set(int id, boolean value) {
        int index = id / 8;
        int w = id % 8;

        if (index >= data.length) {
            this.data = Arrays.copyOf(data, index + 1);
        }

        if (value) data[index] |= 1 << w;
        else data[index] &= ~(1 << w);
    }

    public void reset() {
        data = new byte[0];
    }

    public abstract List<Toggle> getTogglers();

    public interface FlagExtractor<E> {
        Flag extract(E element);
    }

    public static class Toggle {

        private int id;
        private String name;
        private String description;

        public Toggle(String name, String description, int id) {
            this.name = name;
            this.description = description;
            this.id = id;
        }

        public <E extends BaseEntity> boolean set(E handler, FlagExtractor<E> extractor, boolean value) {

            Flag flag = extractor.extract(handler);

            if (flag.has(id) != value) {
                flag.set(id, value);
                handler.modified();
                return true;
            }

            return false;
        }

        public boolean isTrue(Flag flag) {
            return flag.has(id);
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }
    }

    public static class Basic extends Flag {

        public Basic() {
        }

        public Basic(byte[] data) {
            super(data);
        }

        @Override
        public List<Toggle> getTogglers() {
            return null;
        }
    }
}
