package com.github.manolo8.simplecraft.core.placeholder.tools;

class BasicStoredString implements StoredString {

    private final String value;

    public BasicStoredString(String value) {
        this.value = value;
    }

    @Override
    public long lastModified() {
        return 0;
    }

    @Override
    public String value() {
        return value;
    }

}
