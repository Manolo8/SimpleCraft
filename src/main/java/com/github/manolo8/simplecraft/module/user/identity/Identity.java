package com.github.manolo8.simplecraft.module.user.identity;

import com.github.manolo8.simplecraft.core.data.model.named.NamedEntity;
import com.github.manolo8.simplecraft.module.skin.Skin;
import com.github.manolo8.simplecraft.module.user.MessageType;
import com.github.manolo8.simplecraft.module.user.User;
import com.github.manolo8.simplecraft.utils.def.StringUtils;

import java.util.UUID;

public class Identity extends NamedEntity {

    private UUID uuid;
    private User user;

    private Skin skin;

    private long ban;
    private long mute;
    private long lastLogin;
    private long firstLogin;
    private long onlineAllTime;

    //======================================================
    //=====================ENCAPSULATION====================
    //======================================================
    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public boolean match(UUID uuid) {
        return this.uuid.equals(uuid);
    }

    public boolean isOnline() {
        return user != null;
    }

    public Skin getSkin() {
        return skin;
    }

    public void setSkin(Skin skin) {
        this.skin = skin;
    }

    public long getBan() {
        return ban;
    }

    public void setBan(long ban) {
        this.ban = ban;
    }

    public long getMute() {
        return mute;
    }

    public void setMute(long mute) {
        this.mute = mute;
    }

    public long getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(long lastLogin) {
        this.lastLogin = lastLogin;
    }

    public long getFirstLogin() {
        return firstLogin;
    }

    public void setFirstLogin(long firstLogin) {
        this.firstLogin = firstLogin;
    }

    public long getOnlineAllTime() {
        return onlineAllTime;
    }

    public void setOnlineAllTime(long onlineAllTime) {
        this.onlineAllTime = onlineAllTime;
    }

    //======================================================
    //====================_ENCAPSULATION====================
    //======================================================


    //======================================================
    //========================METHODS=======================
    //======================================================
    public boolean isBanned() {
        return ban > System.currentTimeMillis();
    }

    public void ban(long time) {
        ban = System.currentTimeMillis() + time;
        modified();
    }

    public void unban() {
        ban = 0;
        modified();
    }

    public String getBanTime() {
        return StringUtils.longTimeToString(ban - System.currentTimeMillis());
    }

    public boolean isMuted() {
        return mute > System.currentTimeMillis();
    }

    public void mute(long time) {
        mute = System.currentTimeMillis() + time;
        modified();
    }

    public void unmute() {
        mute = 0;
        modified();
    }

    public String getMuteTime() {
        return StringUtils.longTimeToString(mute - System.currentTimeMillis());
    }

    public void changeSkin(Skin skin) {
        this.skin = skin;
        modified();
    }

    public User user() {
        return user;
    }

    public void setUser(User user) {

        if (user == null) {
            onlineAllTime += this.user.getCurrentOnlineTime();
        } else {
            user.setLoginSince(System.currentTimeMillis());
        }

        lastLogin = System.currentTimeMillis();
        modified();
        this.user = user;
    }
    //======================================================
    //=======================_METHODS=======================
    //======================================================

    //======================================================
    //===============UTILS (WHEN ONLINE)====================
    //======================================================
    public void sendAction(Object object) {
        if (isOnline()) user.sendAction(object);
    }

    public void sendTitle(Object title, Object message) {
        if (isOnline()) user.sendTitle(title, message);
    }

    public void sendMessage(Object message) {
        if (isOnline()) user.sendMessage(message);
    }

    public void sendMessage(MessageType type, Object message) {
        if (isOnline()) user.sendMessage(type, message);
    }
    //======================================================
    //========================UTILS=========================
    //======================================================
}
