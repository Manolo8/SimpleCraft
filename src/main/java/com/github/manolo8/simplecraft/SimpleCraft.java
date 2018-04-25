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
import com.github.manolo8.simplecraft.listener.MainListener;
import com.github.manolo8.simplecraft.modules.action.*;
import com.github.manolo8.simplecraft.modules.group.GroupService;
import com.github.manolo8.simplecraft.modules.group.data.GroupDaoImpl;
import com.github.manolo8.simplecraft.modules.group.data.GroupRepository;
import com.github.manolo8.simplecraft.modules.mob.MobService;
import com.github.manolo8.simplecraft.modules.plot.PlotService;
import com.github.manolo8.simplecraft.modules.plot.data.PlotDaoImpl;
import com.github.manolo8.simplecraft.modules.plot.data.PlotRepository;
import com.github.manolo8.simplecraft.modules.portal.PortalService;
import com.github.manolo8.simplecraft.modules.portal.data.PortalDao;
import com.github.manolo8.simplecraft.modules.portal.data.PortalDaoImpl;
import com.github.manolo8.simplecraft.modules.region.RegionService;
import com.github.manolo8.simplecraft.modules.region.data.RegionDaoImpl;
import com.github.manolo8.simplecraft.modules.region.data.RegionRepository;
import com.github.manolo8.simplecraft.modules.shop.ShopController;
import com.github.manolo8.simplecraft.modules.shop.ShopService;
import com.github.manolo8.simplecraft.modules.shop.data.ShopDao;
import com.github.manolo8.simplecraft.modules.shop.data.ShopDaoImpl;
import com.github.manolo8.simplecraft.modules.shop.data.ShopRepository;
import com.github.manolo8.simplecraft.modules.skill.data.SkillDao;
import com.github.manolo8.simplecraft.modules.skill.data.SkillDaoImpl;
import com.github.manolo8.simplecraft.modules.skill.data.SkillRepository;
import com.github.manolo8.simplecraft.modules.user.UserService;
import com.github.manolo8.simplecraft.modules.user.data.UserDaoImpl;
import com.github.manolo8.simplecraft.modules.user.data.UserRepository;
import com.github.manolo8.simplecraft.modules.warp.WarpService;
import com.github.manolo8.simplecraft.modules.warp.data.WarpDao;
import com.github.manolo8.simplecraft.modules.warp.data.WarpDaoImpl;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@SuppressWarnings("unused")
public class SimpleCraft extends JavaPlugin {

    public static SimpleCraft instance;
    public static SendAction sendAction;
    private ConnectionBuilder builder;
    private CacheManager cacheManager;
    private UserService userService;
    private GroupService groupService;
    private PlotService plotService;
    private RegionService regionService;
    private WarpService warpService;
    private MobService mobService;
    private PortalService portalService;

    @Override
    public void onEnable() {
        instance = this;

        setupSendAction();

        builder = new ConnectionBuilder();
        builder.build(this);
        cacheManager = new CacheManager();

        WorldInfoDao worldInfoDao = new WorldInfoDaoImpl(builder);
        WorldService worldService = new WorldService(worldInfoDao);

        PlotDao plotDao = new PlotDaoImpl(builder);
        PlotCache plotCache = new PlotCache(plotDao);
        PlotRepository plotRepository = new PlotRepository(plotDao, plotCache);
        plotService = new PlotService(worldService, plotRepository);
        cacheManager.addCache(plotCache);

        GroupDao groupDao = new GroupDaoImpl(builder);
        GroupCache groupCache = new GroupCache(groupDao);
        GroupRepository groupRepository = new GroupRepository(groupCache, groupDao);
        groupService = new GroupService(groupRepository);
        cacheManager.addCache(groupCache);

        SkillDao skillDao = new SkillDaoImpl(builder);
        SkillCache skillCache = new SkillCache(skillDao);
        SkillRepository skillRepository = new SkillRepository(skillCache, skillDao);
        cacheManager.addCache(skillCache);

        UserDao userDao = new UserDaoImpl(builder);
        UserCache userCache = new UserCache(userDao);
        UserRepository userRepository = new UserRepository(userCache, userDao, groupRepository, plotRepository, skillRepository);
        userService = new UserService(worldService, userRepository);
        cacheManager.addCache(userCache);

        mobService = new MobService(new Random(), userService);

        RegionDao regionDao = new RegionDaoImpl(builder);
        RegionCache regionCache = new RegionCache(regionDao);
        RegionRepository regionRepository = new RegionRepository(regionCache, regionDao);
        regionService = new RegionService(regionRepository, worldService, mobService);
        cacheManager.addCache(regionCache);

        worldService.init();

        ShopDao shopDao = new ShopDaoImpl(builder);
        ShopCache shopCache = new ShopCache(shopDao);
        ShopRepository shopRepository = new ShopRepository(userRepository, shopDao, shopCache);
        ShopService shopService = new ShopService(shopRepository);
        ShopController shopController = new ShopController(shopService);
        cacheManager.addCache(shopCache);

        WarpDao warpDao = new WarpDaoImpl(builder);
        warpService = new WarpService(warpDao);

        PortalDao portalDao = new PortalDaoImpl(builder);
        PortalService portalService = new PortalService(portalDao);

        ProtectionController protectionController = new ProtectionController(userService, worldService);

        Chat chat = new Chat(userService);

        Commands commands = new Commands(userService,
                groupService,
                regionService,
                plotService,
                worldService,
                warpService,
                portalService);

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
        getCommand("portal").setExecutor(commandController);
        getCommand("skill").setExecutor(commandController);

        MainListener mainListener = new MainListener(userService, protectionController, shopController, mobService, chat, portalService);

        getServer().getPluginManager().registerEvents(mainListener, this);
        getServer().getScheduler().runTaskTimer(this, cacheManager, 100, 100);
        getServer().getScheduler().runTaskTimer(this, mobService, 200, 200);
        getServer().getScheduler().runTaskTimer(this, userService, 2, 2);
        getServer().getScheduler().runTaskTimerAsynchronously(this, plotService, 20, 20);
    }

    @Override
    public void onDisable() {
        userService.saveAll();
        cacheManager.saveAll();
        warpService.saveAll();
        builder.closeConnection();
    }

    private void setupSendAction() {
        List<SendAction> sendActions = new ArrayList<>();
        sendActions.add(new NMS1_12());
        sendActions.add(new NMS1_9());
        sendActions.add(new NMS1_8());
        sendActions.add(new NMS1_X());

        String version = Bukkit.getServer().getClass().getPackage().getName().substring(23).toLowerCase();

        for (SendAction loop : sendActions)
            if (loop.support(version)) {
                getLogger().info("Handling action-bar with nsm version " + loop.getClass().getSimpleName());
                sendAction = loop;
                return;
            }
    }
}
