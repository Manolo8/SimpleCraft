package com.github.manolo8.simplecraft;

import com.github.manolo8.simplecraft.cache.CacheManager;
import com.github.manolo8.simplecraft.cache.impl.GroupCache;
import com.github.manolo8.simplecraft.cache.impl.UserCache;
import com.github.manolo8.simplecraft.commands.CommandController;
import com.github.manolo8.simplecraft.commands.Commands;
import com.github.manolo8.simplecraft.core.world.WorldService;
import com.github.manolo8.simplecraft.data.ConnectionBuilder;
import com.github.manolo8.simplecraft.data.dao.GroupDao;
import com.github.manolo8.simplecraft.data.dao.UserDao;
import com.github.manolo8.simplecraft.data.dao.impl.GroupDaoImpl;
import com.github.manolo8.simplecraft.data.dao.impl.UserDaoImpl;
import com.github.manolo8.simplecraft.data.repository.GroupRepository;
import com.github.manolo8.simplecraft.data.repository.UserRepository;
import com.github.manolo8.simplecraft.domain.group.GroupService;
import com.github.manolo8.simplecraft.core.protection.ProtectionController;
import com.github.manolo8.simplecraft.domain.user.UserService;
import com.github.manolo8.simplecraft.listener.MainListener;
import com.github.manolo8.simplecraft.domain.chat.Chat;
import org.bukkit.plugin.java.JavaPlugin;

public class SimpleCraft extends JavaPlugin {

    private ConnectionBuilder builder;
    private CacheManager cacheManager;
    private UserService userService;
    private GroupService groupService;

    @Override
    public void onEnable() {
        builder = new ConnectionBuilder();
        builder.build(this);
        cacheManager = new CacheManager();

        GroupDao groupDao = new GroupDaoImpl(builder);
        GroupCache groupCache = new GroupCache(groupDao);
        GroupRepository groupRepository = new GroupRepository(groupCache, groupDao);
        GroupService groupService = new GroupService(groupRepository);
        cacheManager.addCache(groupCache);

        UserDao userDao = new UserDaoImpl(builder);
        UserCache userCache = new UserCache(userDao);
        UserRepository userRepository = new UserRepository(userCache, userDao, groupRepository);
        userService = new UserService(userRepository);
        cacheManager.addCache(userCache);

        WorldService worldService = new WorldService();

        ProtectionController protectionController = new ProtectionController(worldService);

        Commands commands = new Commands(userService, groupService);
        CommandController commandController = new CommandController(userService, commands);

        getCommand("money").setExecutor(commandController);
        getCommand("group").setExecutor(commandController);
        getCommand("pay").setExecutor(commandController);

        Chat chat = new Chat(userService);

        MainListener mainListener = new MainListener(userService, protectionController, chat);
        getServer().getPluginManager().registerEvents(mainListener, this);
        getServer().getScheduler().runTaskTimer(this, cacheManager, 100, 100);
    }

    @Override
    public void onDisable() {
        userService.saveAll();
        cacheManager.saveAll();
        builder.closeConnection();
    }

}
