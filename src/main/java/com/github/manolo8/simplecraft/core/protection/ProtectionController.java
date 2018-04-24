package com.github.manolo8.simplecraft.core.protection;

import com.github.manolo8.simplecraft.core.world.WorldService;
import com.github.manolo8.simplecraft.modules.user.User;
import com.github.manolo8.simplecraft.modules.user.UserService;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.List;

public class ProtectionController {

    private final UserService userService;
    private final WorldService worldService;

    public ProtectionController(UserService userService,
                                WorldService worldService) {
        this.userService = userService;
        this.worldService = worldService;

        init();
    }

    private void init() {
        for (User user : userService.getLogged())
            userJoin(user);
    }

    /**
     * Chamado sempre que um jogador entra no servidor
     * O sistema já pega a proteção em que o jogador se encontra
     *
     * @param user jogador que logou
     */
    public void userJoin(User user) {
        user.setCurrentChecker(worldService.getChecker(user.getBase().getWorld()));
        user.setProtection(user.getCurrentChecker().getLocationProtection(user.getBase().getLocation()));
    }

    /**
     * Chamado quando um jogador muda de mundo
     * O sistema atualiza o ProtectionChecker e encontra
     * A permissão em que o jogador se encontra
     *
     * @param user jogador que mudou de mundo
     */
    public void changeWorld(User user) {
        user.setWorldId(worldService.getWorldId(user.getBase().getWorld()));
        user.setCurrentChecker(worldService.getChecker(user.getWorldId()));
        user.setProtection(user.getCurrentChecker().getLocationProtection(user.getBase().getLocation()));
    }

    public void worldLoad(World world) {
        worldService.worldLoad(world);
    }

    /**
     * Usado para descarregar proteções desnecessárias
     *
     * @param x     posição x
     * @param z     posição z
     * @param world mundo em que ocorreu o evento
     */
    public void chunkLoad(int x, int z, World world) {
        worldService.getIWorld(world).chunkLoad(x, z);
    }


    public void chunkUnload(int x, int z, World world) {
        worldService.getIWorld(world).chunkUnload(x, z, world.getLoadedChunks());
    }

    /**
     * Checa se um jogador tem proteção para quebrar bloco em uma proteção
     *
     * @param user  o jogador
     * @param block o bloco quebrado
     * @return true se tem permissão e false caso não
     */
    public boolean breakBlock(User user, Block block) {
        return user.getLocationProtection(block.getLocation()).canBreak(user, block.getType());
    }


    /**
     * Checa se um jogador tem proteção para colocar bloco em uma proteção
     *
     * @param user  o jogador
     * @param block o bloco colocado
     * @return true se tem permissão e false caso não tenha
     */
    public boolean placeBlock(User user, Block block) {
        return user.getLocationProtection(block.getLocation()).canPlace(user, block.getType());
    }

    /**
     * @param user   o jogador
     * @param target o local do jogador alvo
     * @return true se tem pvp on, false caso não
     */
    public boolean isPvpOn(User user, Location target) {
        return user.getLocationProtection(user.getBase().getLocation()).isPvpOn()
                && user.getLocationProtection(target).isPvpOn();
    }

    /**
     * @param user     o jogador
     * @param location o local do animal alvo
     * @return true se tem pvp on, false caso não
     */
    public boolean isAnimalPvpOn(User user, Location location) {
        return user.getLocationProtection(location).isAnimalPvpOn(user);
    }

    /**
     * @param user  o jogador
     * @param block o bloco clicado
     * @return true se o jogador pode interagir, false caso não
     */
    public boolean canInteract(User user, Block block) {
        Material type = block.getType();

        if (type == Material.CHEST
                || type == Material.JUKEBOX
                || type == Material.DISPENSER
                || type == Material.FURNACE
                || type == Material.BURNING_FURNACE
                || type == Material.BREWING_STAND
                || type == Material.ENCHANTMENT_TABLE)
            return user.getLocationProtection(block.getLocation()).canInteract(user, type);

        return true;
    }

    public boolean blockExplode(List<Block> blocks) {
        if (blocks.size() == 0) return false;

        Protection protection = getLocationProtection(blocks.get(0).getLocation());

        for (Block block : blocks) {
            if (!protection.isInArea(block.getLocation()))
                protection = getLocationProtection(block.getLocation());

            if (!protection.canExplode()) return false;
        }

        return true;
    }

    public boolean canPistonWork(List<Block> blocks) {
        if (blocks.size() == 0) return true;

        Protection protection = getLocationProtection(blocks.get(0).getLocation());

        for (Block block : blocks) {
            if (!protection.isInArea(block.getLocation()))
                protection = getLocationProtection(block.getLocation());

            if (!protection.canPistonWork()) return false;
        }

        return true;
    }

    public boolean canSpread(Block block) {
        Protection protection = getLocationProtection(block.getLocation());

        return protection.canSpread(block.getType());
    }

    public void updateUserProtection(User user) {
        user.getLocationProtection(user.getBase().getLocation());
    }

    private Protection getLocationProtection(Location location) {
        return worldService.getChecker(location.getWorld()).getLocationProtection(location);
    }
}
