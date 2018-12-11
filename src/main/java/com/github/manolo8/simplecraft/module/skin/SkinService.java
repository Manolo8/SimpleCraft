package com.github.manolo8.simplecraft.module.skin;

import com.github.manolo8.simplecraft.SimpleCraft;
import com.github.manolo8.simplecraft.core.commands.line.Sender;
import com.github.manolo8.simplecraft.core.commands.line.annotation.CmdDescription;
import com.github.manolo8.simplecraft.core.commands.line.annotation.CmdMapping;
import com.github.manolo8.simplecraft.core.commands.line.annotation.CmdPermission;
import com.github.manolo8.simplecraft.core.service.RepositoryService;
import com.github.manolo8.simplecraft.module.user.MessageType;
import com.github.manolo8.simplecraft.module.user.User;
import com.github.manolo8.simplecraft.module.user.identity.Identity;
import com.github.manolo8.simplecraft.utils.def.MojangSkinUtils;
import org.bukkit.Bukkit;

import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Queue;

@SuppressWarnings("unused")
public class SkinService extends RepositoryService<SkinRepository> {

    public static SkinService instance;
    private final Queue<SetupSkin> setups;
    private MojangSkinUtils skinUtils;
    private volatile boolean running = true;
    private int threads;

    public SkinService(SkinRepository skinRepository) {
        super(skinRepository);

        instance = this;
        this.setups = new LinkedList<>();
    }

    @Override
    public void init() throws Exception {
        this.skinUtils = new MojangSkinUtils();
        this.running = true;

        for (int i = 0; i < 2; i++) new SetupSkinThread().start();
    }

    public void stop() {
        this.running = false;
    }

    private void setUserSkin(User user, Skin skin) {
        user.identity().changeSkin(skin);

        refreshSkin(user);
    }

    public void refreshSkin(User user) {

        Skin skin = user.identity().getSkin();

        if (skin == null) {
            installUserSkin(user, user.identity().getName());
        } else {
            skinUtils.refreshUserSkin(user, user.identity().getSkin());
        }
    }

    public Skin findByName(String name) throws SQLException {
        return repository.findByName(name);
    }

    private Skin findOrCreateSkin(String name) throws SQLException {

        Skin skin = repository.findByName(name);

        if (skin != null) return skin;

        try {
            String[] data = skinUtils.findSkinData(name);

            if (data == null) {
                return null;
            } else {
                return repository.fromData(data);
            }

        } catch (IOException e) {
            return null;
        }
    }

    public void installUserSkin(User user, String name) {
        synchronized (setups) {
            setups.add(new SetupSkin(user.identity(), name));
        }
    }

    //======================================================
    //=======================COMMANDS=======================
    //======================================================

    @CmdMapping("skin <name>")
    @CmdDescription("Trocar sua skin")
    @CmdPermission("simplecraft.user")
    public void skin(User user, String name) {

        if (user.timeChecker(6, 60000)) {
            user.sendMessage(MessageType.ERROR, "Espere " + user.timeNeed(6, 60000) + " milissegundos!");
        } else {
            user.sendMessage(MessageType.SUCCESS, "Estamos procurando a skin! Aguarde.");
            installUserSkin(user, name);
        }

    }

    @CmdMapping("skin -e <user> set <name>")
    @CmdDescription("Trocar a skin de outro jogador")
    @CmdPermission("simplecraft.admin")
    public void skinOther(Sender sender, User other, String name) {

        installUserSkin(other, name);
        sender.sendMessage(MessageType.SUCCESS, "Skin alterada!");

    }

    @CmdMapping("skin -e <user> reset")
    @CmdDescription("Reseta a skin de um jogador")
    @CmdPermission("simplecraft.admin")
    public void skinOtherReset(Sender sender, User other) {

        installUserSkin(other, other.identity().getName());
        sender.sendMessage(MessageType.SUCCESS, "Skin resetada!");

    }

    //======================================================
    //======================_COMMANDS=======================
    //======================================================

    private class SetupSkinThread extends Thread {

        public SetupSkinThread() {
            super("SkinThread # " + (threads++));
        }

        @Override
        public void run() {
            while (running) {

                SetupSkin setupSkin;

                synchronized (setups) {
                    setupSkin = setups.poll();
                }

                if (setupSkin != null) {

                    setupSkin.setup();

                } else {

                    skinUtils.organizeProxies();

                }

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class SetupSkin {

        private Identity target;
        private String name;

        public SetupSkin(Identity target, String name) {
            this.target = target;
            this.name = name;
        }

        public void setup() {
            try {
                Skin skin = findOrCreateSkin(name);

                if (skin == null) {
                    target.sendMessage(MessageType.ERROR, "Skin '" + name + "' não encontrada! Use /skin <nome>");
                    skin = findOrCreateSkin("SimpleCraft");
                } else {
                    target.sendMessage(MessageType.SUCCESS, "A skin '" + name + "' agora é sua!");
                }

                final Skin found = skin;

                if (target.isOnline()) {
                    Bukkit.getScheduler().runTaskLater(SimpleCraft.instance, () -> setUserSkin(target.user(), found), 0);
                }

            } catch (Exception exception) {
                target.sendMessage(MessageType.ERROR, "Houve um erro ao baixar sua SKIN :(");
                exception.printStackTrace();
            }
        }

    }
}
