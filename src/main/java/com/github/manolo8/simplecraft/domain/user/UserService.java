package com.github.manolo8.simplecraft.domain.user;

import com.github.manolo8.simplecraft.data.repository.UserRepository;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserService {

    private final List<User> logged;
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.logged = new ArrayList<>();
        this.userRepository = userRepository;

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
     * @param lastName ultimo nome
     * @return User ou null
     */
    public User getOfflineUser(String lastName) {
        for (User user : logged) if (user.match(lastName)) return user;

        return userRepository.findOne(lastName);
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
