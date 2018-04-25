package com.github.manolo8.simplecraft.modules.portal;

import com.github.manolo8.simplecraft.data.model.NamedEntity;
import com.github.manolo8.simplecraft.utils.location.SimpleLocation;

public class Portal extends NamedEntity {

    private int worldId;
    private String pos1Message;
    private SimpleLocation pos1;
    private String pos2Message;
    private SimpleLocation pos2;

    public int getWorldId() {
        return worldId;
    }

    public void setWorldId(int worldId) {
        this.worldId = worldId;
    }

    public String getPos1Message() {
        return pos1Message;
    }

    public void setPos1Message(String pos1Message) {
        this.pos1Message = pos1Message;
        this.setNeedSave(true);
    }

    public SimpleLocation getPos1() {
        return pos1;
    }

    public void setPos1(SimpleLocation pos1) {
        this.pos1 = pos1;
        this.setNeedSave(true);
    }

    public String getPos2Message() {
        return pos2Message;
    }

    public void setPos2Message(String pos2Message) {
        this.pos2Message = pos2Message;
        this.setNeedSave(true);
    }

    public SimpleLocation getPos2() {
        return pos2;
    }

    public void setPos2(SimpleLocation pos2) {
        this.pos2 = pos2;
        this.setNeedSave(true);
    }
}