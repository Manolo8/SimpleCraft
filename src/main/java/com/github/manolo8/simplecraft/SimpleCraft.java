package com.github.manolo8.simplecraft;

import com.github.manolo8.simplecraft.cache.CacheManager;
import com.github.manolo8.simplecraft.cache.impl.*;
import com.github.manolo8.simplecraft.core.chat.Chat;
import com.github.manolo8.simplecraft.core.commands.def.CommandController;
import com.github.manolo8.simplecraft.core.commands.def.Commands;
import com.github.manolo8.simplecraft.core.protection.ProtectionController;
import com.github.manolo8.simplecraft.core.world.WorldService;
import com.github.manolo8.simplecraft.core.world.data.WorldInfoDaoImpl;
import com.github.manolo8.simplecraft.data.builder.ConnectionBuilder;
import com.github.manolo8.simplecraft.data.dao.*;
import com.github.manolo8.simplecraft.domain.group.GroupService;
import com.github.manolo8.simplecraft.domain.group.data.GroupDaoImpl;
import com.github.manolo8.simplecraft.domain.group.data.GroupRepository;
import com.github.manolo8.simplecraft.domain.plot.PlotService;
import com.github.manolo8.simplecraft.domain.plot.data.PlotDaoImpl;
import com.github.manolo8.simplecraft.domain.plot.data.PlotRepository;
import com.github.manolo8.simplecraft.domain.region.RegionService;
import com.github.manolo8.simplecraft.domain.region.data.RegionDaoImpl;
import com.github.manolo8.simplecraft.domain.region.data.RegionRepository;
import com.github.manolo8.simplecraft.domain.shop.ShopController;
import com.github.manolo8.simplecraft.domain.shop.ShopService;
import com.github.manolo8.simplecraft.domain.shop.data.ShopDao;
import com.github.manolo8.simplecraft.domain.shop.data.ShopDaoImpl;
import com.github.manolo8.simplecraft.domain.shop.data.ShopRepository;
import com.github.manolo8.simplecraft.domain.user.UserService;
import com.github.manolo8.simplecraft.domain.user.data.UserDaoImpl;
import com.github.manolo8.simplecraft.domain.user.data.UserRepository;
import com.github.manolo8.simplecraft.domain.warp.WarpService;
import com.github.manolo8.simplecraft.domain.warp.data.WarpDao;
import com.github.manolo8.simplecraft.domain.warp.data.WarpDaoImpl;
import com.github.manolo8.simplecraft.listener.MainListener;
import org.bukkit.plugin.java.JavaPlugin;

@SuppressWarnings("unused")
public class SimpleCraft extends JavaPlugin {

    private ConnectionBuilder builder;
    private CacheManager cacheManager;
    private UserService userService;
    private GroupService groupService;
    private PlotService plotService;
    private RegionService regionService;
    private WarpService warpService;

    @Override
    public void onEnable() {
        builder = new ConnectionBuilder();
        builder.build(this);
        cacheManager = new CacheManager();

        WorldInfoDao worldInfoDao = new WorldInfoDaoImpl(builder);
        WorldService worldService = new WorldService(worldInfoDao);

        RegionDao regionDao = new RegionDaoImpl(builder);
        RegionCache regionCache = new RegionCache(regionDao);
        RegionRepository regionRepository = new RegionRepository(regionCache, regionDao);
        regionService = new RegionService(regionRepository, worldService);
        cacheManager.addCache(regionCache);

        PlotDao plotDao = new PlotDaoImpl(builder);
        PlotCache plotCache = new PlotCache(plotDao);
        PlotRepository plotRepository = new PlotRepository(plotDao, plotCache);
        plotService = new PlotService(worldService, plotRepository);
        cacheManager.addCache(plotCache);

        worldService.init();

        GroupDao groupDao = new GroupDaoImpl(builder);
        GroupCache groupCache = new GroupCache(groupDao);
        GroupRepository groupRepository = new GroupRepository(groupCache, groupDao);
        groupService = new GroupService(groupRepository);
        cacheManager.addCache(groupCache);

        UserDao userDao = new UserDaoImpl(builder);
        UserCache userCache = new UserCache(userDao);
        UserRepository userRepository = new UserRepository(userCache, userDao, groupRepository, plotRepository);
        userService = new UserService(worldService, userRepository);
        cacheManager.addCache(userCache);

        ShopDao shopDao = new ShopDaoImpl(builder);
        ShopCache shopCache = new ShopCache(shopDao);
        ShopRepository shopRepository = new ShopRepository(userRepository, shopDao, shopCache);
        ShopService shopService = new ShopService(shopRepository);
        ShopController shopController = new ShopController(shopService);
        cacheManager.addCache(shopCache);

        WarpDao warpDao = new WarpDaoImpl(builder);
        warpService = new WarpService(warpDao);

        ProtectionController protectionController = new ProtectionController(userService, worldService);

        Commands commands = new Commands(userService,
                groupService,
                regionService,
                plotService,
                worldService,
                warpService);

        CommandController commandController = new CommandController(userService, commands);

        getCommand("money").setExecutor(commandController);
        getCommand("group").setExecutor(commandController);
        getCommand("pay").setExecutor(commandController);
        getCommand("region").setExecutor(commandController);
        getCommand("plot").setExecutor(commandController);
        getCommand("world").setExecutor(commandController);
        getCommand("status").setExecutor(commandController);
        getCommand("warp").setExecutor(commandController);
        getCommand("warpadmin").setExecutor(commandController);

        Chat chat = new Chat(userService);

        MainListener mainListener = new MainListener(userService, protectionController, shopController, chat);
        getServer().getPluginManager().registerEvents(mainListener, this);
        getServer().getScheduler().runTaskTimer(this, cacheManager, 100, 100);
        getServer().getScheduler().runTaskTimerAsynchronously(this, plotService, 20, 20);
    }

    @Override
    public void onDisable() {
        userService.saveAll();
        cacheManager.saveAll();
        warpService.saveAll();
        builder.closeConnection();
    }
}
