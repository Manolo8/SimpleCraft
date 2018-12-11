package com.github.manolo8.simplecraft.module.skin;

import com.github.manolo8.simplecraft.core.data.model.named.NamedEntity;
import com.mojang.authlib.properties.Property;

public class Skin extends NamedEntity {

    private String value;
    private String signature;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public Property toProperty() {
        return new Property("textures", value, signature);
    }
}
