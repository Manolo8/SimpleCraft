package com.github.manolo8.simplecraft.module.user;

import com.github.manolo8.simplecraft.core.commands.inventory.InventoryView;
import com.github.manolo8.simplecraft.core.commands.inventory.View;
import com.github.manolo8.simplecraft.core.data.model.base.BaseEntity;
import com.github.manolo8.simplecraft.core.world.WorldInfo;
import com.github.manolo8.simplecraft.core.world.container.Container;
import com.github.manolo8.simplecraft.core.world.container.IContainer;
import com.github.manolo8.simplecraft.core.world.container.ProxyContainer;
import com.github.manolo8.simplecraft.interfaces.Teleportable;
import com.github.manolo8.simplecraft.module.board.BoardUser;
import com.github.manolo8.simplecraft.module.clan.user.ClanUser;
import com.github.manolo8.simplecraft.module.group.user.GroupUser;
import com.github.manolo8.simplecraft.module.kit.user.KitUser;
import com.github.manolo8.simplecraft.module.money.Money;
import com.github.manolo8.simplecraft.module.plot.user.PlotUser;
import com.github.manolo8.simplecraft.module.rank.Rank;
import com.github.manolo8.simplecraft.module.skill.user.SkillUser;
import com.github.manolo8.simplecraft.module.tag.TagUser;
import com.github.manolo8.simplecraft.module.user.identity.Identity;
import com.github.manolo8.simplecraft.utils.def.IntegerList;
import com.github.manolo8.simplecraft.utils.def.PacketAccessor;
import net.minecraft.server.v1_13_R2.ChatMessageType;
import net.minecraft.server.v1_13_R2.IChatBaseComponent;
import net.minecraft.server.v1_13_R2.Packet;
import net.minecraft.server.v1_13_R2.PacketPlayOutChat;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class User extends BaseEntity {

    private Identity identity;
    private WorldInfo worldInfo;

    private Container container;
    private IContainer iContainer;
    private int modifier;

    private Player base;

    private Money money;
    private Rank rank;
    private UserFlag flag;
    private IntegerList sellItems;

    private BoardUser boardUser;
    private TagUser tagUser;

    private GroupUser groupUser;
    private ClanUser clanUser;
    private PlotUser plotUser;
    private SkillUser skillUser;
    private KitUser kitUser;

    private Points points;
    private InventoryView inventoryView;
    private boolean hidden;
    private String password;
    private boolean authenticated;

    private long address;
    private long[] lastInfo = new long[7];

    private long loginSince;

    private long lastDamage;
    private User lastDamageUser;

    //======================================================
    //=====================ENCAPSULATION====================
    //======================================================
    public Identity identity() {
        return identity;
    }

    public void setIdentity(Identity identity) {
        this.identity = identity;
    }

    public Player base() {
        return base;
    }

    public void setBase(Player base) {
        this.base = base;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
        modified();
    }

    public long getCurrentOnlineTime() {
        return System.currentTimeMillis() - loginSince;
    }

    public Rank rank() {
        return rank;
    }

    public void setRank(Rank rank) {
        this.rank = rank;
    }

    public BoardUser board() {
        return boardUser;
    }

    public void setBoard(BoardUser boardUser) {
        this.boardUser = boardUser;
    }

    public TagUser tag() {
        return tagUser;
    }

    public void setTag(TagUser tagUser) {
        this.tagUser = tagUser;
    }

    public Money money() {
        return money;
    }

    public void setMoney(Money money) {
        this.money = money;
    }

    public WorldInfo worldInfo() {
        return worldInfo;
    }

    public Points points() {
        return points;
    }

    public void setPoints(Points points) {
        this.points = points;
    }

    public SkillUser skill() {
        return skillUser;
    }

    public void setSkill(SkillUser skillUser) {
        this.skillUser = skillUser;
    }

    public ClanUser clan() {
        return clanUser;
    }

    public void setClan(ClanUser clanUser) {
        this.clanUser = clanUser;
    }

    public PlotUser plot() {
        return plotUser;
    }

    public void setPlot(PlotUser plotUser) {
        this.plotUser = plotUser;
    }

    public KitUser kit() {
        return kitUser;
    }

    public void setKit(KitUser kitUser) {
        this.kitUser = kitUser;
    }

    public GroupUser group() {
        return groupUser;
    }

    public void setGroup(GroupUser groupUser) {
        this.groupUser = groupUser;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public User getLastDamage() {
        return lastDamageUser;
    }

    public void setLastDamage(User lastDamageUser) {
        this.lastDamageUser = lastDamageUser;
    }

    public UserFlag flags() {
        return flag;
    }

    public void setLoginSince(long loginSince) {
        this.loginSince = loginSince;
    }

    public void setFlag(UserFlag flag) {
        this.flag = flag;
    }

    public IntegerList getSellItems() {
        return sellItems;
    }

    public void setSellItems(IntegerList sellItems) {
        this.sellItems = sellItems;
    }

    public long getAddress() {
        return address;
    }

    public void setAddress(long address) {
        this.address = address;
    }
    //======================================================
    //====================_ENCAPSULATION====================
    //======================================================


    //======================================================
    //=======================METHODS========================
    //======================================================


    //======================================================
    //========================UTILS=========================
    //======================================================

    /**
     * Envia uma mensagem para um jogador
     *
     * @param message mensagem
     */
    public boolean sendMessage(Object message) {
        if (base != null && base.isOnline() && isAuthenticated()) {
            base.sendMessage(String.valueOf(message));
            return true;
        } else {
            return false;
        }
    }

    /**
     * Envia uma mensagem tipada para um jogador
     *
     * @param message mensagem
     */
    public void sendMessage(MessageType type, Object message) {
        sendMessage(type.format(message));
    }

    /**
     * Envia uma mensagem para um determinado jogador
     * (Acima do HOTBAR)
     *
     * @param object mensagem
     */
    public void sendAction(Object object) {
        if (base != null && base.isOnline() && isAuthenticated()) {
            PacketPlayOutChat packet = new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + String.valueOf(object) + "\"}"), ChatMessageType.GAME_INFO);
            sendPacket(packet);
        }
    }

    public void sendPacket(Packet packet) {
        ((CraftPlayer) base).getHandle().playerConnection.sendPacket(packet);
    }

    public void sendPacket(PacketAccessor.PacketBuilder builder) {
        sendPacket(builder.packet());
    }

    /**
     * Envia uma mensagem com título para determinado jogador
     *
     * @param title   título
     * @param message mensagem
     */
    public void sendTitle(Object title, Object message) {
        if (base != null && base.isOnline() && isAuthenticated()) {
            base.sendTitle(String.valueOf(title), String.valueOf(message), 10, 40, 10);
        }
    }

    /**
     * Toca um som na localização do jogador
     *
     * @param sound  O Som
     * @param v      v
     * @param v1     v1
     * @param global global ou apenas para o jogador
     */
    public void playSound(Sound sound, float v, float v1, boolean global) {
        if (base != null && base.isOnline()) {
            playSound(sound, base.getLocation(), v, v1, global);
        }
    }

    /**
     * Toca um som na localização do jogador
     *
     * @param sound    O Som
     * @param location Local
     * @param v        v
     * @param v1       v1
     * @param global   global ou apenas para o jogador
     */
    public void playSound(Sound sound, Location location, float v, float v1, boolean global) {
        if (global) {
            base.getWorld().playSound(location, sound, v, v1);
        } else {
            base.playSound(location, sound, v, v1);
        }
    }

    /**
     * @param target alvo
     * @return a distancia entre 2 jogadores, -1 caso seja outro mundo
     */
    public double distanceSquared(User target) {
        if (base == null || target.base == null) return -1;
        if (worldInfo != target.worldInfo) return -1;
        Location loc1 = base.getLocation();
        Location loc2 = target.base.getLocation();
        return square(loc1.getX() - loc2.getX()) /*+ square(loc1.getY() - loc2.getY()) - não precisa do y */ + square(loc1.getZ() - loc2.getZ());
    }

    private double square(double value) {
        return value * value;
    }

    public boolean teleport(Teleportable teleportable) {
        return teleportable.teleport(this);
    }

    public boolean teleport(Location location) {

        if (location.getWorld() == null) {
            sendMessage(MessageType.ERROR, "O mundo não está carregado!");

            return false;

        } else {
            return base().teleport(location);
        }

    }
    //======================================================
    //=======================_UTILS=========================
    //======================================================


    //======================================================
    //=========================PVP==========================
    //======================================================
    public boolean isInPvp() {
        return System.currentTimeMillis() - lastDamage < 15000;
    }

    public int getLastPvpTime() {
        return (int) ((System.currentTimeMillis() - lastDamage) / 1000);
    }

    public void updateLastDamage() {
        lastDamage = System.currentTimeMillis();
    }
    //======================================================
    //========================_PVP==========================
    //======================================================


    //======================================================
    //====================PERMISSIONS=======================
    //======================================================

    /**
     * @param permission permissão
     * @return true caso o jogador tenha a permissão
     */
    public boolean hasPermission(String permission) {
        return isAdmin() || groupUser.get().hasPermission(permission);
    }

    public boolean isAdmin() {
        return base.isOp() || groupUser.get().hasPermission("simplecraft.admin");
    }

    /**
     * @param permission permissão
     * @return um valor numérico para essa permissão (0, se não houver)
     */
    public int getPermissionQuantity(String permission) {
        return groupUser.get().getPermissionQuantity(permission);
    }

    //======================================================
    //===================_PERMISSIONS=======================
    //======================================================


    //======================================================
    //======================SECURITY========================
    //======================================================
    public boolean isAuthenticated() {
        return authenticated;
    }

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }

    public boolean lastInfo(int id, long ms) {
        if (System.currentTimeMillis() - lastInfo[id] > ms) {
            lastInfo[id] = System.currentTimeMillis();
            return true;
        }

        return false;
    }

    public boolean timeChecker(int id, long ms) {
        if (System.currentTimeMillis() - lastInfo[id] < ms) {
            return !isAdmin();
        } else {
            lastInfo[id] = System.currentTimeMillis();
            return false;
        }
    }

    public long timeNeed(int id, long ms) {
        return ms - (System.currentTimeMillis() - lastInfo[id]);
    }
    //======================================================
    //====================_SECURITY=========================
    //======================================================


    //======================================================
    //=====================CONTAINER========================
    //======================================================

    /**
     * Atualiza as informações do mundo do jogador
     *
     * @param worldInfo worldInfo
     */
    public void updateWorldInfo(WorldInfo worldInfo) {
        if (iContainer != null) iContainer.exit(this);
        if (worldInfo == null) return;

        this.worldInfo = worldInfo;
        this.iContainer = worldInfo.getContainer();
    }

    /**
     * Atualiza o IContainer atual do jogador
     * (Também verifica se o jogador pode voar
     * no novo IContainer)
     *
     * @param iContainer
     */
    public void updateContainer(IContainer iContainer) {
        if (base.getAllowFlight()
                && base.getGameMode() == GameMode.SURVIVAL
                && !iContainer.protection().canFly()) {
            base.setAllowFlight(false);
        }

        if (iContainer instanceof ProxyContainer) {
            if (container != ((ProxyContainer) iContainer).controller) {
                container = ((ProxyContainer) iContainer).controller;
                modifier++;
            }
        } else if (container != null) {
            container = null;
            modifier++;
        }

        this.iContainer = iContainer;
    }

    /**
     * @return o IContainer atualizado do jogador
     */
    public IContainer iContainer() {
        return iContainer;
    }

    public Container container() {
        return container;
    }

    public int getModifier() {
        return modifier;
    }

    //======================================================
    //====================_CONTAINER========================
    //======================================================


    //======================================================
    //========================RANK==========================
    //======================================================

    /**
     * Atualiza o rank atual do jogador
     *
     * @param rank novo rank
     */
    public void changeRank(Rank rank) {
        this.rank = rank;

        modified();
    }
    //======================================================
    //========================RANK==========================
    //======================================================


    //=====================================================
    //====================Inventory view===================
    //=====================================================

    /**
     * @param view Cria uma view para o jogador
     */
    public void createView(View view) {
        InventoryView iv = new InventoryView(this);

        if (inventoryView != null) inventoryView.close(true);

        setInventoryView(iv);

        iv.add(view);
    }

    /**
     * @return a view atual do jogador ou null
     */
    public InventoryView getInventoryView() {
        return inventoryView;
    }

    /**
     * @param inventoryView Seta a view em que o jogador esta visualizando
     */
    public void setInventoryView(InventoryView inventoryView) {
        this.inventoryView = inventoryView;
    }

    public void closeView() {
        if (inventoryView != null) inventoryView.close(true);
    }
    //=====================================================
    //====================_InventoryView===================
    //=====================================================

    public void tickSync() {
        if (isAuthenticated()) {
            if (inventoryView != null) inventoryView.tick();
            skillUser.tick();
        }
    }

    public void tickAsync() {

        if (isAuthenticated()) {
            boardUser.tick();
            tagUser.tick();
            groupUser.tick();
        }
    }

    @Override
    public boolean equals(Object o) {
        return this == o;
    }

    @Override
    public int hashCode() {
        return id;
    }

    //======================================================
    //======================_METHODS========================
    //======================================================
}
