package com.github.manolo8.simplecraft.modules.user;

import com.github.manolo8.simplecraft.SimpleCraft;
import com.github.manolo8.simplecraft.core.commands.inventory.InventoryView;
import com.github.manolo8.simplecraft.core.commands.inventory.View;
import com.github.manolo8.simplecraft.core.protection.Protection;
import com.github.manolo8.simplecraft.core.protection.ProtectionChecker;
import com.github.manolo8.simplecraft.core.world.WorldService;
import com.github.manolo8.simplecraft.data.model.NamedEntity;
import com.github.manolo8.simplecraft.data.model.PositionEntity;
import com.github.manolo8.simplecraft.modules.action.SendAction;
import com.github.manolo8.simplecraft.modules.group.Group;
import com.github.manolo8.simplecraft.modules.plot.data.PlotInfo;
import com.github.manolo8.simplecraft.modules.skill.Level;
import com.github.manolo8.simplecraft.modules.skill.Skill;
import com.github.manolo8.simplecraft.modules.skill.tools.JoinSkill;
import com.github.manolo8.simplecraft.modules.warp.Warp;
import com.github.manolo8.simplecraft.utils.location.SimpleArea;
import com.github.manolo8.simplecraft.utils.location.SimpleLocation;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.*;

public class User extends NamedEntity {

    private static SendAction sendAction = SimpleCraft.sendAction;
    private UUID uuid;
    private int worldId;
    private Player base;

    private Group group;

    private double money;

    private ProtectionChecker currentChecker;
    private Protection protection;

    private InventoryView inventoryView;

    private SimpleLocation pos1;
    private SimpleLocation pos2;

    private List<PlotInfo> plots;

    private long lastPvp;

    private long exp;
    private int level;

    private Map<Class, List<Level>> skillLevel;
    private List<Skill> skills;

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public int getWorldId() {
        return worldId;
    }

    public void setWorldId(int worldId) {
        this.worldId = worldId;
    }

    public boolean match(Player player) {
        return uuid.equals(player.getUniqueId());
    }

    public Player getBase() {
        return base;
    }

    public void setBase(Player base) {
        this.base = base;

        if (base == null) return;

        cleanupSkills();
    }

    @Override
    public void addReference() {
        super.addReference();
        getGroup().addReference();
        for (Skill skill : skills)
            skill.addReference();
    }

    @Override
    public void removeReference() {
        super.removeReference();
        getGroup().removeReference();
        for (Skill skill : skills)
            skill.removeReference();
        if (this.protection != null) this.protection.removeUser();
    }

    //==================================================
    //======================POS=========================
    //==================================================
    public SimpleLocation getPos1() {
        return pos1;
    }

    public void setPos1(SimpleLocation pos1) {
        this.pos1 = pos1;
    }

    public SimpleLocation getPos2() {
        return pos2;
    }

    public void setPos2(SimpleLocation pos2) {
        this.pos2 = pos2;
    }

    public boolean isMarked() {
        return pos1 != null && pos2 != null;
    }

    public SimpleArea asSimpleArea() {
        return new SimpleArea(pos1, pos2);
    }
    //==================================================
    //=====================_POS=========================
    //==================================================


    //==================================================
    //=====================PLOTS========================
    //==================================================
    public List<PlotInfo> getPlots() {
        return plots;
    }

    public void setPlots(List<PlotInfo> plots) {
        this.plots = plots;
    }

    public int getPlotQuantity() {
        return plots.size();
    }
    //==================================================
    //====================_PLOTS========================
    //==================================================


    //==================================================
    //=====================UTILS========================
    //==================================================
    public void sendMessage(Object object) {
        if (base == null || !base.isOnline()) return;

        base.sendMessage(object.toString());
    }

    public void sendAction(Object object) {
        if (base == null || !base.isOnline()) return;

        try {
            sendAction.sendAction(base, String.valueOf(object));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void spawnParticle(Particle particle, Location location, int quantity) {
        base.getWorld().spawnParticle(particle, base.getLocation(), quantity);
    }

    public void spawnParticle(Particle particle, int quantity) {
        if (base == null || !base.isOnline()) return;
        spawnParticle(particle, base.getLocation(), quantity);
    }

    public void teleport(Location location) {
        getBase().teleport(location);
    }

    public void teleport(PositionEntity p) {
        World world = WorldService.instance.getWorldByWorldId(p.getWorldId());

        int x = (p.getX() * 64) + 40;
        int z = (p.getZ() * 64) + 15;

        teleport(new Location(world, x, 65, z));
    }

    public void teleport(Warp warp) {
        World world = WorldService.instance.getWorldByWorldId(warp.getWorldId());

        teleport(warp.getLocation().getLocation(world));
    }

    public double distance(User target) {
        if (base == null || target.base == null) return -1;
        Location loc1 = base.getLocation();
        Location loc2 = target.base.getLocation();
        if (!loc1.getWorld().equals(loc2.getWorld())) return -1;
        return loc1.distance(loc2);
    }

    public void playSound(Sound sound, float v, float v1) {
        if (base == null || !base.isOnline()) return;
        playSound(sound, base.getLocation(), v, v1);
    }

    public void playSound(Sound sound, Location location, float v, float v1) {
        base.getWorld().playSound(location, sound, v, v1);
    }
    //==================================================
    //====================_UTILS========================
    //==================================================


    //==================================================
    //======================PVP=========================
    //==================================================
    public boolean isInPvp() {
        return System.currentTimeMillis() - lastPvp < 15000;
    }

    public void updatePvp() {
        if (!isInPvp()) sendMessage("§cAgora você esta em modo pvp por §a15s§c. Não poderá usar portais!");
        this.lastPvp = System.currentTimeMillis();
    }
    //==================================================
    //=====================_PVP=========================
    //==================================================


    //==================================================
    //=====================MONEY========================
    //==================================================
    public void deposit(double quantity) {
        setNeedSave(true);
        this.money += quantity;
    }

    public boolean hasMoney(double quantity) {
        return this.money >= quantity;
    }

    public boolean withdraw(double quantity) {
        if (this.money >= quantity) {
            this.money -= quantity;
            setNeedSave(true);
            return true;
        }
        return false;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        setNeedSave(true);
        this.money = money;
    }
    //==================================================
    //====================_MONEY========================
    //==================================================


    //==================================================
    //===================PERMISSION=====================
    //==================================================
    public boolean hasPermission(String permission) {
        return (base != null && base.isOp() || group != null && group.hasPermission(permission));
    }

    public int getPermissionQuantity(String key) {
        return group == null ? 0 : group.getPermissionQuantity(key);
    }

    public Group getGroup() {
        return this.group;
    }

    public void setGroup(Group group) {
        setNeedSave(true);
        this.group = group;
    }
    //==================================================
    //==================_PERMISSION=====================
    //==================================================


    //==================================================
    //===================PROTECTION=====================
    //==================================================
    public Protection getLocationProtection(Location location) {
        return currentChecker.getUserProtection(this, location);
    }

    public Protection getProtection() {
        return protection;
    }

    public void setProtection(Protection protection) {
        if (this.protection != null) this.protection.removeUser();
        this.protection = protection;
        this.protection.addUser();
    }

    public ProtectionChecker getCurrentChecker() {
        return currentChecker;
    }

    public void setCurrentChecker(ProtectionChecker currentChecker) {
        this.currentChecker = currentChecker;
    }
    //==================================================
    //==================_PROTECTION=====================
    //==================================================


    //==================================================
    //==================Inventory view==================
    //==================================================

    /**
     * @param view Cria uma view para o jogador
     */
    public void createView(View view) {
        InventoryView creation = new InventoryView();
        creation.setUser(this);
        setInventoryView(creation);
        creation.open();
        creation.addView(view);
        getBase().updateInventory();
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

    //==================================================
    //=================_Inventory view==================
    //==================================================


    //==================================================
    //=====================SKILLS=======================
    //==================================================

    public Skill getSkill(int type) {
        for (Skill skill : skills) {
            if (skill.getType() == type) return skill;
        }

        return null;
    }

    public int getFreePoints() {
        int total = 0;

        for (Skill skill : skills) {
            total += skill.getLevel();
        }

        return level - total;
    }

    public boolean hasFreePoints() {
        return getFreePoints() > 0;
    }

    /**
     * @return a exp do jogador
     */
    public long getExp() {
        return exp;
    }

    /**
     * @param exp Seta a exp atual do jogador
     */
    public void setExp(long exp) {
        this.exp = exp;
    }

    /**
     * @return o level atual do jogador
     */
    public int getLevel() {
        return level;
    }

    /**
     * @param level seta o level atual do jogador
     */
    public void setLevel(int level) {
        setNeedSave(true);
        this.level = level;
    }

    /**
     * @param quantity Dá uma quantia de EXP para o jogador e depois verifica se ele pode upar
     *                 Também envia uma mensagem
     */
    public void giveExp(long quantity) {
        this.exp += quantity;

        setNeedSave(true);

        sendAction("§aVocê recebeu " + quantity + "! de exp! Falta " + (expToNextLevel() - exp) + " para upar!");

        canLevelUp();
    }

    /**
     * @return a quantia de exp nescessária para upar
     */
    public long expToNextLevel() {
        return (int) (250 + Math.pow(level + 15, 1.75D));
    }

    /**
     * Upa 1 level e manda uma mensagem para o jogador
     */
    public void levelUp() {
        level++;
        setNeedSave(true);
        sendAction("§aVocê upou para o level " + level + "! Use /skills para evoluir!");
    }

    /**
     * Checa se com a XP atual é possível upar
     */
    public void canLevelUp() {
        long nextLevel = expToNextLevel();
        if (exp >= nextLevel) {
            exp -= nextLevel;
            levelUp();
            canLevelUp();
        }
    }

    /**
     * @return todas as skills do jogador
     */
    public List<Skill> getSkills() {
        return skills;
    }

    /**
     * @param skills skills do jogador
     */
    public void setSkills(List<Skill> skills) {
        this.skills = skills;
    }

    /**
     * Deve ser chamado sempre que um jogador reseta ou
     * Faz upgrade em uma de suas skills
     */
    public void cleanupSkills() {
        if (skillLevel == null) skillLevel = new HashMap<>();
        else skillLevel.clear();

        List<JoinSkill> joinSkills = getByType(JoinSkill.class);

        //Handle JoinSkill
        for (JoinSkill join : joinSkills)
            join.onJoin(this);
    }

    /**
     * @param type classe do executor
     * @return uma lista para ser executado nessa classe
     */
    public <A> List<A> getByType(Class<A> type) {
        List list = skillLevel.get(type);

        if (list != null) return (List<A>) list;

        List newList = new ArrayList<>();

        for (Skill skill : skills) {
            Level level = skill.getLevelHandler();
            if (type.isAssignableFrom(skill.getClass())) newList.add(skill);
            if (type.isAssignableFrom(level.getClass())) newList.add(level);
        }

        skillLevel.put(type, newList);

        return (List<A>) newList;
    }
    //==================================================
    //====================_SKILLS=======================
    //==================================================
}
