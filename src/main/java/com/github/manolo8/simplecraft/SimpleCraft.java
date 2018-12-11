package com.github.manolo8.simplecraft;

import com.github.manolo8.simplecraft.core.commands.line.CommandService;
import com.github.manolo8.simplecraft.core.placeholder.PlaceHolderService;
import com.github.manolo8.simplecraft.module.tag.TagList;
import com.github.manolo8.simplecraft.core.world.WorldInfoRepository;
import com.github.manolo8.simplecraft.core.world.WorldService;
import com.github.manolo8.simplecraft.core.data.cache.CacheService;
import com.github.manolo8.simplecraft.core.data.connection.Database;
import com.github.manolo8.simplecraft.core.data.connection.DatabaseBuilder;
import com.github.manolo8.simplecraft.listener.MainListener;
import com.github.manolo8.simplecraft.module.board.BoardItemRepository;
import com.github.manolo8.simplecraft.module.board.BoardService;
import com.github.manolo8.simplecraft.module.clan.ClanRepository;
import com.github.manolo8.simplecraft.module.clan.ClanService;
import com.github.manolo8.simplecraft.module.clan.clanarea.ClanAreaService;
import com.github.manolo8.simplecraft.module.group.GroupRepository;
import com.github.manolo8.simplecraft.module.group.GroupService;
import com.github.manolo8.simplecraft.module.hologram.HologramRepository;
import com.github.manolo8.simplecraft.module.hologram.HologramService;
import com.github.manolo8.simplecraft.module.kit.KitRepository;
import com.github.manolo8.simplecraft.module.kit.KitService;
import com.github.manolo8.simplecraft.module.market.MarketItemRepository;
import com.github.manolo8.simplecraft.module.market.MarketService;
import com.github.manolo8.simplecraft.module.mine.MineRepository;
import com.github.manolo8.simplecraft.module.mine.MineService;
import com.github.manolo8.simplecraft.module.mobarea.MobAreaRepository;
import com.github.manolo8.simplecraft.module.mobarea.MobAreaService;
import com.github.manolo8.simplecraft.module.money.MoneyRepository;
import com.github.manolo8.simplecraft.module.money.MoneyService;
import com.github.manolo8.simplecraft.module.plot.PlotRepository;
import com.github.manolo8.simplecraft.module.plot.PlotService;
import com.github.manolo8.simplecraft.module.portal.PortalRepository;
import com.github.manolo8.simplecraft.module.portal.PortalService;
import com.github.manolo8.simplecraft.module.rank.RankRepository;
import com.github.manolo8.simplecraft.module.rank.RankService;
import com.github.manolo8.simplecraft.module.region.RegionRepository;
import com.github.manolo8.simplecraft.module.region.RegionService;
import com.github.manolo8.simplecraft.module.shop.ShopRepository;
import com.github.manolo8.simplecraft.module.shop.ShopService;
import com.github.manolo8.simplecraft.module.skill.SkillRepository;
import com.github.manolo8.simplecraft.module.skill.SkillService;
import com.github.manolo8.simplecraft.module.skin.SkinRepository;
import com.github.manolo8.simplecraft.module.skin.SkinService;
import com.github.manolo8.simplecraft.module.user.UserRepository;
import com.github.manolo8.simplecraft.module.user.UserService;
import com.github.manolo8.simplecraft.module.user.identity.IdentityRepository;
import com.github.manolo8.simplecraft.module.warp.WarpRepository;
import com.github.manolo8.simplecraft.module.warp.WarpService;
import com.github.manolo8.simplecraft.tools.item.ItemRepository;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;

@SuppressWarnings("unused")
public class SimpleCraft extends JavaPlugin {

    public static SimpleCraft instance;
    private CacheService cacheService;
    private DatabaseBuilder databaseBuilder;
    private MainListener mainListener;

    private BoardItemRepository boardItemRepository;
    private WorldInfoRepository worldInfoRepository;
    private IdentityRepository identityRepository;
    private GroupRepository groupRepository;
    private MoneyRepository moneyRepository;
    private UserRepository userRepository;
    private RegionRepository regionRepository;
    private ItemRepository itemRepository;
    private ShopRepository shopRepository;
    private PlotRepository plotRepository;
    private MineRepository mineRepository;
    private PortalRepository portalRepository;
    private RankRepository rankRepository;
    private MobAreaRepository mobAreaRepository;
    private ClanRepository clanRepository;
    private SkillRepository skillRepository;
    private KitRepository kitRepository;
    //    private MachineRepository machineRepository;
    private WarpRepository warpRepository;
    private SkinRepository skinRepository;
    private MarketItemRepository marketItemRepository;
    private HologramRepository hologramRepository;

    private PlaceHolderService placeHolderService;
    private BoardService boardService;
    private GroupService groupService;
    private MoneyService moneyService;
    private UserService userService;
    private WorldService worldService;
    private RegionService regionService;
    private ShopService shopService;
    private PlotService plotService;
    private MineService mineService;
    private PortalService portalService;
    private RankService rankService;
    private MobAreaService mobAreaService;
    private ClanService clanService;
    private ClanAreaService clanAreaService;
    private SkillService skillService;
    private KitService kitService;
    private CommandService commandService;
    //    private MachineService machineService;
    private WarpService warpService;
    private SkinService skinService;
    private MarketService marketService;
    private HologramService hologramService;

    private TagList tagList;

    @Override
    public void onEnable() {
        long time = System.nanoTime();

        databaseBuilder = new DatabaseBuilder();
        Database database = databaseBuilder.build(this).getDatabase();
        cacheService = new CacheService();

        instance = this;

        tagList = new TagList();

        try {
            //REPOSITORIES

            skinRepository = new SkinRepository(database);

            boardItemRepository = new BoardItemRepository(database);

            identityRepository = new IdentityRepository(database, skinRepository);

            groupRepository = new GroupRepository(database, identityRepository);

            worldInfoRepository = new WorldInfoRepository(database);

            moneyRepository = new MoneyRepository(database,
                    identityRepository);

            regionRepository = new RegionRepository(database,
                    worldInfoRepository);

            itemRepository = new ItemRepository(database);

            shopRepository = new ShopRepository(database,
                    worldInfoRepository,
                    moneyRepository,
                    itemRepository);

            plotRepository = new PlotRepository(database,
                    worldInfoRepository,
                    identityRepository);

            rankRepository = new RankRepository(database);

            clanRepository = new ClanRepository(database,
                    identityRepository,
                    worldInfoRepository);

            mineRepository = new MineRepository(database,
                    worldInfoRepository,
                    itemRepository);

            portalRepository = new PortalRepository(database,
                    worldInfoRepository);

            mobAreaRepository = new MobAreaRepository(database,
                    itemRepository,
                    worldInfoRepository);

            skillRepository = new SkillRepository(database,
                    identityRepository);

            kitRepository = new KitRepository(database,
                    identityRepository,
                    itemRepository);

            userRepository = new UserRepository(database,
                    identityRepository,
                    moneyRepository,
                    plotRepository.getPlotUserRepository(),
                    kitRepository.getKitUserRepository(),
                    rankRepository,
                    clanRepository.getClanUserRepository(),
                    groupRepository.getGroupUserRepository(),
                    skillRepository.getSkillUserRepository());

//            machineRepository = new MachineRepository(database,
//                    worldInfoRepository, null, null);

            warpRepository = new WarpRepository(database,
                    worldInfoRepository);

            marketItemRepository = new MarketItemRepository(database,
                    itemRepository,
                    moneyRepository);

            hologramRepository = new HologramRepository(database, worldInfoRepository);
            //REPOSITORIES

            boardItemRepository.init();
            skinRepository.init();
            groupRepository.init();
            rankRepository.init();
            userRepository.init();
            identityRepository.init();
            moneyRepository.init();
            worldInfoRepository.init();
            regionRepository.init();
            itemRepository.init();
            shopRepository.init();
            plotRepository.init();
            mineRepository.init();
            portalRepository.init();
            mobAreaRepository.init();
            clanRepository.init();
            skillRepository.init();
            kitRepository.init();
//            machineRepository.init();
            warpRepository.init();
            marketItemRepository.init();
            hologramRepository.init();

            //SERVICES
            placeHolderService = new PlaceHolderService();
            boardService = new BoardService(boardItemRepository);
            skinService = new SkinService(skinRepository);
            groupService = new GroupService(groupRepository);
            moneyService = new MoneyService(moneyRepository);
            worldService = new WorldService(worldInfoRepository);
            userService = new UserService(worldService, userRepository, skinService, boardService);
            regionService = new RegionService(regionRepository);
            shopService = new ShopService(shopRepository);
            plotService = new PlotService(worldService, plotRepository);
            mineService = new MineService(mineRepository);
            portalService = new PortalService(portalRepository);
            rankService = new RankService(rankRepository);
            mobAreaService = new MobAreaService(mobAreaRepository);
            clanService = new ClanService(clanRepository, userService.getChat());
            clanAreaService = new ClanAreaService(clanRepository.getClanAreaRepository());
            skillService = new SkillService(skillRepository);
            kitService = new KitService(kitRepository);
//            machineService = new MachineService(machineRepository);
            commandService = new CommandService(userService, worldService);
            warpService = new WarpService(warpRepository);
            marketService = new MarketService(marketItemRepository);
            hologramService = new HologramService(hologramRepository);
            //SERVICES

            worldService.register(regionService);
            worldService.register(shopService);
            worldService.register(plotService);
            worldService.register(mineService);
            worldService.register(portalService);
            worldService.register(mobAreaService);
            worldService.register(clanAreaService);
//            worldService.register(machineService);
            worldService.register(hologramService);

            commandService.register(boardService);
            commandService.register(mineService);
            commandService.register(moneyService);
            commandService.register(mobAreaService);
            commandService.register(kitService);
            commandService.register(groupService);
            commandService.register(plotService);
            commandService.register(userService);
            commandService.register(clanService);
            commandService.register(rankService);
            commandService.register(portalService);
            commandService.register(clanAreaService);
            commandService.register(regionService);
            commandService.register(skillService);
//            commandService.register(machineService);
            commandService.register(warpService);
            commandService.register(cacheService);
            commandService.register(skinService);
            commandService.register(marketService);
            commandService.register(worldService);
            commandService.register(hologramService);

            placeHolderService.register(userService);
            placeHolderService.register(moneyService);
            placeHolderService.register(clanService);
            placeHolderService.register(groupService);

            boardService.init();

            skinService.init();
            worldService.init();

            rankService.init();
            userService.init();
            kitService.init();
            warpService.init();

            commandService.init();

            getServer().getScheduler().runTaskTimerAsynchronously(this, cacheService, 1, 1);
            getServer().getScheduler().runTaskTimerAsynchronously(this, () -> moneyService.updateMoneyTop(), 0, 20 * 60 * 5);

            getServer().getScheduler().runTaskTimer(this, worldService, 1, 1);
            getServer().getScheduler().runTaskTimer(this, () -> userService.runSync(), 1, 1);
            getServer().getScheduler().runTaskTimerAsynchronously(this, () -> userService.runAsync(), 0, 1);

            getServer().getScheduler().runTaskLater(this, this::onFinishLoad, 0);

            mainListener = new MainListener(worldService, userService);

            getServer().getPluginManager().registerEvents(mainListener, this);

        } catch (Exception e) {
            e.printStackTrace();
            Bukkit.shutdown();
        }

        System.out.println("TOOK " + (System.nanoTime() - time) + " nanoseconds");
    }

    public void onFinishLoad() {
        try {
            worldService.postInit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        getServer().getScheduler().cancelTasks(this);
        tagList.removeAll();
        userService.stop();
        skinService.stop();
        worldService.stop();
        cacheService.stop();
        databaseBuilder.closeConnection();
    }
}
