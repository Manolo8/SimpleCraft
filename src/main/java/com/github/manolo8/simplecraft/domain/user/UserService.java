package com.github.manolo8.simplecraft.domain.user;

import com.github.manolo8.simplecraft.core.world.WorldService;
import com.github.manolo8.simplecraft.domain.user.data.UserRepository;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserService {

    public static UserService instance;
    private final List<User> logged;
    private final UserRepository userRepository;
    private final WorldService worldService;

    public UserService(WorldService worldService, UserRepository userRepository) {
        this.logged = new ArrayList<>();
        this.userRepository = userRepository;
        this.worldService = worldService;

        instance = this;
        init();
    }

    private void init() {
        for (World world : Bukkit.getWorlds())
            for (Player player : world.getPlayers())
                playerJoin(player);
    }

    private User findOfflineUser(UUID uuid) {
        //Não adiciona referencias
        //Assim o sistema de cache ira remover
        //Dentro do tempo programado automaticamente
        return userRepository.findOne(uuid);
    }

    /**
     * @param lastName nome
     * @return User ou null
     */
    public User getOfflineUser(String lastName) {
        for (User user : logged) if (user.match(lastName)) return user;

        return userRepository.findOne(lastName);
    }

    public User getOfflineUser(int id) {
        for (User user : logged) if (user.match(id)) return user;

        return userRepository.findOne(id);
    }

    public User getOnlineUser(Player player) {
        for (User user : logged) if (user.match(player)) return user;

        return null;
    }

    public void saveAll() {
        for (User user : logged) userRepository.save(user);
    }

    public User playerJoin(Player player) {
        User user = userRepository.findOne(player.getUniqueId());

        user.setName(player.getName());
        user.setWorldId(worldService.getWorldId(player.getWorld()));
        user.addReference();
        user.setBase(player);

        logged.add(user);

        return user;
    }

    public void playerQuit(Player player) {
        for (int i = 0; i < logged.size(); i++) {
            User user = logged.get(i);
            if (user.match(player)) {
                user.removeReference();
                if (user.getInventoryView() != null) {
                    user.getInventoryView().close(true);
                    user.setInventoryView(null);
                }
                user.setBase(null);
                logged.remove(i);
                userRepository.save(user);
                break;
            }
        }
    }


    public List<User> getLogged() {
        return logged;
    }
}
