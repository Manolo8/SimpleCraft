package com.github.manolo8.simplecraft.utils.def;

import com.github.manolo8.simplecraft.module.skin.Skin;
import com.github.manolo8.simplecraft.module.user.User;
import com.github.manolo8.simplecraft.module.user.UserService;
import com.mojang.authlib.properties.Property;
import net.minecraft.server.v1_13_R2.*;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftPlayer;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static net.minecraft.server.v1_13_R2.PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER;
import static net.minecraft.server.v1_13_R2.PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER;

public class MojangSkinUtils {

    private final List<ProxyDetails> proxies;
    private int current = 0;
    private long lastUpdate;

    public MojangSkinUtils() throws IOException {
        proxies = new ArrayList<>();

        findProxies();
    }

    public String[] findSkinData(String name) throws IOException {

        String uuid = findUUID(name);

        if (uuid == null) {
            return null;
        } else {
            String[] data = new String[3];

            data[0] = name;

            InputStream stream = getInputStream("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false", true);

            if (stream == null) return null;

            Reader reader = new InputStreamReader(stream);

            int read;
            int current = 0;

            StringBuilder builder = new StringBuilder();

            while ((read = reader.read()) != -1) {
                char c = (char) read;

                if (c == '"') {
                    current++;
                } else if (current == 17) {
                    builder.append(c);
                } else if (current == 18) {
                    data[1] = builder.toString();
                    builder.setLength(0);
                } else if (current == 21) {
                    builder.append(c);
                } else if (current == 22) {
                    data[2] = builder.toString();
                    break;
                }
            }

            return data;
        }
    }

    private String findUUID(String name) throws IOException {
        StringBuilder builder = new StringBuilder();

        Reader reader = new InputStreamReader(getInputStream("https://api.mojang.com/users/profiles/minecraft/" + name, false));

        int read;
        int current = 0;

        while ((read = reader.read()) != -1) {
            char c = (char) read;

            if (c == '"') {
                current++;
            } else if (current == 3) {
                builder.append(c);
            } else if (current == 4) {
                break;
            }
        }

        return current == 0 ? null : builder.toString();
    }

    private ProxyDetails nextProxy() {

        synchronized (proxies) {

            ProxyDetails best = null;

            for (ProxyDetails current : proxies) {
                if (current.isAvailable() && current.time != 0) {
                    if (best == null) {
                        best = current;
                    } else {
                        if (best.time > current.time) {
                            best = current;
                        }
                    }
                }
            }

            return best;
        }

    }

    private void findProxies() throws IOException {
        proxies.clear();

        Reader reader = new FileReader("C:\\Servidor\\1.8.9\\plugins\\SimpleCraft\\list_9325220406.txt");

        StringBuilder builder = new StringBuilder();
        int read;

        while ((read = reader.read()) != -1) {
            char c = (char) read;

            if (c == '\n') {
                proxies.add(new ProxyDetails(builder.toString()));
                builder.setLength(0);
            } else {
                builder.append(c);
            }
//
//            if (c == '>') {
//                proxies.add(new ProxyDetails(builder.toString()));
//                builder.setLength(0);
//            } else if (!(c == '<' || c == 'b' || c == 'r')) {
//                builder.append(c);
//            }
        }

        reader.close();

        lastUpdate = System.currentTimeMillis();
    }

    public void organizeProxies() {

        if (current < proxies.size()) {

            ProxyDetails details = proxies.get(current++);

            try {
                URL url = new URL("https://sessionserver.mojang.com/session/minecraft/profile/2eed7ca885fd4af19dc45df0c022a73e?unsigned=false");

                HttpURLConnection connection;

                long time = System.currentTimeMillis();

                Proxy proxy = details.asProxy();

                connection = (HttpURLConnection) url.openConnection(proxy);

                setupDefaults(connection);

                connection.getContent();

                details.time = System.currentTimeMillis() - time;
                details.lastUse = System.currentTimeMillis();

                connection.disconnect();

            } catch (Exception ignored) {
                details.time = 1000 * 10 * 20;
            }

        } else if (System.currentTimeMillis() - lastUpdate > 1000 * 60 * 60 * 12) {
            try {
                findProxies();
                current = 0;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private InputStream getInputStream(String url0, boolean useProxy) throws IOException {
        return useProxy ? getInputStreamProxy(url0, 0) : getInputStream(url0);
    }

    private InputStream getInputStream(String url0) throws IOException {
        URL url = new URL(url0);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        setupDefaults(connection);

        return connection.getInputStream();
    }

    private InputStream getInputStreamProxy(String url0, int tried) throws IOException {

        URL url = new URL(url0);

        HttpURLConnection connection;

        ProxyDetails next = nextProxy();

        long time = System.currentTimeMillis();

        Proxy proxy = next.asProxy();

        connection = (HttpURLConnection) url.openConnection(proxy);

        setupDefaults(connection);

        try {

            InputStream stream = connection.getInputStream();

            next.time = System.currentTimeMillis() - time;
            next.lastUse = System.currentTimeMillis();

            return stream;

        } catch (IOException ignored) {
            next.time = 1000 * 10 * 20;
            if (tried < 30) {
                return getInputStreamProxy(url0, ++tried);
            } else {
                return null;
            }
        }
    }

    private void setupDefaults(HttpURLConnection connection) throws ProtocolException {
        connection.setRequestMethod("GET");
        connection.setRequestProperty("User-Agent", "SimpleCraft");
        connection.setConnectTimeout(2000);
        connection.setReadTimeout(2000);
        connection.setDoOutput(true);
    }

    public void refreshUserSkin(User user, Skin skin) {
        Property property = skin.toProperty();

        EntityPlayer player = ((CraftPlayer) user.base().getPlayer()).getHandle();

        player.getProfile().getProperties().clear();
        player.getProfile().getProperties().put("textures", property);

        Packet removePlayer = new PacketPlayOutPlayerInfo(REMOVE_PLAYER, player);
        Packet removeEntity = new PacketPlayOutEntityDestroy(player.getId());
        Packet addNamed = new PacketPlayOutNamedEntitySpawn(player);
        Packet addPlayer = new PacketPlayOutPlayerInfo(ADD_PLAYER, player);

        EnumGamemode gameMode = player.playerInteractManager.getGameMode();

        Packet playerRespawn = new PacketPlayOutRespawn(DimensionManager.OVERWORLD, player.world.worldData.getDifficulty(), player.world.worldData.getType(), gameMode);
        Packet playerPosition = new PacketPlayOutPosition(player.locX, player.locY, player.locZ, player.yaw, player.pitch, new HashSet<>(), 0);

        Packet mainHand = new PacketPlayOutEntityEquipment(player.getId(), EnumItemSlot.MAINHAND, player.inventory.getItemInHand());
        Packet offHand = new PacketPlayOutEntityEquipment(player.getId(), EnumItemSlot.OFFHAND, player.inventory.extraSlots.get(0));
        Packet head = new PacketPlayOutEntityEquipment(player.getId(), EnumItemSlot.HEAD, player.inventory.getItem(player.inventory.getSize() - 2));
        Packet chest = new PacketPlayOutEntityEquipment(player.getId(), EnumItemSlot.CHEST, player.inventory.getItem(player.inventory.getSize() - 3));
        Packet legs = new PacketPlayOutEntityEquipment(player.getId(), EnumItemSlot.LEGS, player.inventory.getItem(player.inventory.getSize() - 4));
        Packet feet = new PacketPlayOutEntityEquipment(player.getId(), EnumItemSlot.FEET, player.inventory.getItem(player.inventory.getSize() - 5));
        Packet held = new PacketPlayOutHeldItemSlot(player.inventory.itemInHandIndex);

        UserService.eachExecuteStatic(loop -> {
            PlayerConnection connection = (((CraftPlayer) loop.base()).getHandle().playerConnection);
            if (loop == user) {
                connection.sendPacket(removeEntity);
                connection.sendPacket(addPlayer);
                connection.sendPacket(playerRespawn);
                player.updateAbilities();
                connection.sendPacket(playerPosition);
                connection.sendPacket(held);
                user.base().updateInventory();
                player.triggerHealthUpdate();
                if (user.base().isOp()) {
                    user.base().setOp(false);
                    user.base().setOp(true);
                }
            } else if (loop.worldInfo() == user.worldInfo() && user.base().canSee(loop.base())) {
                connection.sendPacket(removeEntity);
                connection.sendPacket(removePlayer);
                connection.sendPacket(addPlayer);
                connection.sendPacket(addNamed);
                connection.sendPacket(mainHand);
                connection.sendPacket(offHand);
                connection.sendPacket(head);
                connection.sendPacket(chest);
                connection.sendPacket(legs);
                connection.sendPacket(feet);
            } else {
                connection.sendPacket(removePlayer);
                connection.sendPacket(addPlayer);
            }
        });
    }

    private class ProxyDetails {

        private String ip;
        private int port;
        private long time;
        private long lastUse;

        public ProxyDetails(String data) {
            int sep = data.indexOf(':');
            this.ip = data.substring(0, sep);
            this.port = Integer.valueOf(data.substring(sep + 1, data.length() - 1));
        }

        public boolean isAvailable() {
            return System.currentTimeMillis() - lastUse > 10000;
        }

        public Proxy asProxy() {
            return new Proxy(Proxy.Type.HTTP, new InetSocketAddress(ip, port));
        }
    }

}
