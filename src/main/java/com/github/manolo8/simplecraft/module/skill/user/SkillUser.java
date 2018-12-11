package com.github.manolo8.simplecraft.module.skill.user;

import com.github.manolo8.simplecraft.module.user.model.identity.BaseIdentity;
import com.github.manolo8.simplecraft.interfaces.Tickable;
import com.github.manolo8.simplecraft.module.skill.Level;
import com.github.manolo8.simplecraft.module.skill.Skill;
import com.github.manolo8.simplecraft.module.skill.tools.DisableSkill;
import com.github.manolo8.simplecraft.module.user.UserService;
import com.github.manolo8.simplecraft.utils.def.StringUtils;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public class SkillUser extends BaseIdentity {

    private long exp;
    private int level;
    private double mana;
    private double maxMana;
    private double regen;

    private Map<Class, List<Level>> skillLevel;
    private List<Skill> skills;

    //======================================================
    //===================ENCAPSULATION======================
    //======================================================

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
        modified();

        this.level = level;
        this.maxMana = 100 + 25 * Math.pow(level, 0.88);
        this.regen = 1 + 0.4 * Math.pow(level, 0.5);

    }

    public double getMana() {
        return mana;
    }

    public void setMana(double mana) {
        this.mana = mana;
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

    //======================================================
    //===================_ENCAPSULATION=====================
    //======================================================


    //======================================================
    //======================METHODS=========================
    //======================================================

    /**
     * @param type entity
     * @return retorna uma skill pelo entity
     */
    public Skill getSkill(int type) {

        for (Skill skill : skills)
            if (skill.getType() == type) return skill;

        return null;
    }

    public boolean takeMana(double quantity) {

        if (mana >= quantity) {
            mana -= quantity;
            return true;
        }

        return false;
    }

    public void giveMana(double quantity) {
        if (mana == maxMana) return;

        if (mana + quantity > maxMana) mana = maxMana;
        else mana += quantity;

        modified();
    }

    /**
     * @return os pontos disponíveis
     */
    public int getFreePoints() {
        int total = 0;

        for (Skill skill : skills)
            total += skill.getLevel();

        return level - total;
    }

    /**
     * @return checa de há pontos disponíveis
     */
    public boolean hasFreePoints() {
        return getFreePoints() > 0;
    }

    /**
     * @return a quantia de exp nescessária para upar
     */
    public long expToNextLevel() {
        return (int) (100 + Math.pow(level + 5, 1.75D));
    }

    /**
     * Upa 1 level e manda uma mensagem para o jogador
     */
    public void levelUp() {
        setLevel(level + 1);
        modified();

        identity.sendAction("§aVocê upou para o level " + level + "!");
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
     * Dá uma quantia de EXP para o jogador e depois verifica se ele
     * pode upar, envia uma mensagem de aviso caso esteja online
     *
     * @param quantity quantidade
     */
    public void giveExp(long quantity) {
        if (quantity <= 0) return;
        this.exp += quantity;

        modified();

        identity.sendAction("§a+ " + quantity + "! de exp! " + (StringUtils.doubleToString(((double) exp / expToNextLevel()) * 100)) + "%");

        canLevelUp();
    }

    /**
     * Deve ser chamado sempre que um jogador reseta ou
     * Faz upgrade em uma de suas skills
     */
    public void cleanupSkills() {
        if (skillLevel == null) skillLevel = new IdentityHashMap<>();
        else skillLevel.clear();
    }

    public boolean hasFreeSlot() {
        return getFreeSlots() > 0;
    }

    public int getFreeSlots() {
        return identity.isOnline() ? identity.user().getPermissionQuantity("simplecraft.user.skills") - getActiveSkillsCount() : 0;
    }

    public List<Skill> getActiveSkills() {
        List<Skill> actives = new ArrayList<>();

        for (Skill skill : skills)
            if (skill.isActive()) actives.add(skill);

        return actives;
    }

    public int getActiveSkillsCount() {
        int total = 0;

        for (Skill skill : skills)
            if (skill.isActive()) total++;

        return total;
    }

    public void activeSkill(Skill skill) {
        skill.setActive(true);
        cleanupSkills();
    }

    public void disableSkill(Skill skill) {

        if (skill instanceof DisableSkill)
            ((DisableSkill) skill).onDisable();

        if (skill.getLevelHandler() instanceof DisableSkill)
            ((DisableSkill) skill.getLevelHandler()).onDisable();

        skill.setActive(false);
        cleanupSkills();
    }

    public void clearSkills() {
        if (identity.isOnline()) {
            for (Skill skill : skills) disableSkill(skill);
            cleanupSkills();
        }

        for (Skill skill : skills) skill.remove();

        skills.clear();
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
            if (!skill.isActive()) continue;
            Level level = skill.getLevelHandler();
            if (type.isAssignableFrom(skill.getClass())) newList.add(skill);
            if (type.isAssignableFrom(level.getClass())) newList.add(level);
        }

        skillLevel.put(type, newList);

        return (List<A>) newList;
    }

    public void add(Skill skill) {
        this.skills.add(skill);
        cleanupSkills();
    }

    public void tick() {
        final List<Tickable> tickables = getByType(Tickable.class);

        for (Tickable tickable : tickables) tickable.tick();

        if (UserService.tick % 20 == 1) {
            giveMana(regen);
        }
    }

    //======================================================
    //======================_METHODS========================
    //======================================================
}
