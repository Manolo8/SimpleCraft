package com.github.manolo8.simplecraft.module.user;

import com.github.manolo8.simplecraft.core.data.cache.Cache;
import com.github.manolo8.simplecraft.core.data.cache.SaveCache;
import com.github.manolo8.simplecraft.core.data.connection.Database;
import com.github.manolo8.simplecraft.core.data.model.base.BaseDAO;
import com.github.manolo8.simplecraft.core.data.model.base.BaseLoader;
import com.github.manolo8.simplecraft.core.data.model.base.BaseRepository;
import com.github.manolo8.simplecraft.module.clan.user.ClanUserRepository;
import com.github.manolo8.simplecraft.module.group.user.GroupUser;
import com.github.manolo8.simplecraft.module.group.user.GroupUserRepository;
import com.github.manolo8.simplecraft.module.kit.user.KitUserRepository;
import com.github.manolo8.simplecraft.module.money.MoneyRepository;
import com.github.manolo8.simplecraft.module.plot.user.PlotUserRepository;
import com.github.manolo8.simplecraft.module.rank.RankRepository;
import com.github.manolo8.simplecraft.module.skill.user.SkillUserRepository;
import com.github.manolo8.simplecraft.module.user.identity.Identity;
import com.github.manolo8.simplecraft.module.user.identity.IdentityDTO;
import com.github.manolo8.simplecraft.module.user.identity.IdentityRepository;
import com.github.manolo8.simplecraft.utils.def.IntegerList;
import org.bukkit.entity.Player;

import java.lang.ref.Reference;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class UserRepository extends BaseRepository<User,
        UserRepository.UserDTO,
        UserRepository.UserDAO,
        UserRepository.UserCache,
        UserRepository.UserLoader> {

    private final IdentityRepository identityRepository;
    private final RankRepository rankRepository;

    private final MoneyRepository moneyRepository;
    private final PlotUserRepository plotUserRepository;
    private final KitUserRepository kitUserRepository;
    private final ClanUserRepository clanUserRepository;
    private final GroupUserRepository groupUserRepository;
    private final SkillUserRepository skillUserRepository;

    //======================================================
    //======================REPOSITORY======================
    //======================================================
    public UserRepository(Database database,
                          IdentityRepository identityRepository,
                          MoneyRepository moneyRepository,
                          PlotUserRepository plotUserRepository,
                          KitUserRepository kitUserRepository,
                          RankRepository rankRepository,
                          ClanUserRepository clanUserRepository,
                          GroupUserRepository groupUserRepository,
                          SkillUserRepository skillUserRepository) {
        super(database);

        this.identityRepository = identityRepository;
        this.rankRepository = rankRepository;

        this.moneyRepository = moneyRepository;
        this.kitUserRepository = kitUserRepository;
        this.plotUserRepository = plotUserRepository;
        this.clanUserRepository = clanUserRepository;
        this.groupUserRepository = groupUserRepository;
        this.skillUserRepository = skillUserRepository;
    }

    @Override
    protected UserDAO initDao() throws SQLException {
        return new UserDAO(database);
    }

    @Override
    protected UserLoader initLoader() {
        return new UserLoader();
    }

    @Override
    protected UserCache initCache() {
        return new UserCache(this);
    }

    public User findOrCreate(Player player) throws SQLException {
        synchronized (Cache.LOCKER) {

            UUID uuid = player.getUniqueId();

            User user = findOne(uuid);

            if (user == null) {
                UserDTO dto = new UserDTO();

                dto.name = player.getName();
                dto.fastName = player.getName().toLowerCase();
                dto.leastSigBits = uuid.getLeastSignificantBits();
                dto.mostSigBits = uuid.getMostSignificantBits();
                dto.firstLogin = System.currentTimeMillis();

                user = create(dto);
            }

            return user;
        }
    }

    private User findOne(UUID uuid) throws SQLException {
        User user = cache.getIfMatchUUID(uuid);

        if (user != null) return user;

        UserDTO dto = dao.findOne(uuid);

        return fromDTO(dto);
    }

    public GroupUser findIdentityGroup(Identity identity) throws SQLException {
        return groupUserRepository.findOneByIdentity(identity);
    }

    public IdentityRepository getIdentityRepository() {
        return identityRepository;
    }
    //======================================================
    //=====================_REPOSITORY======================
    //======================================================


    //======================================================
    //==========================DTO=========================
    //======================================================
    public class UserDTO extends IdentityDTO {

        private String password;
        private int rankId;
        private byte[] flag;
        private byte[] autoSellItems;

    }
    //======================================================
    //==========================DTO=========================
    //======================================================


    // =====================================================
    //==========================DAO=========================
    //======================================================
    class UserDAO extends BaseDAO<UserDTO> {

        private final String findOneByUUID = "SELECT * FROM Users WHERE mostSigBits=? AND leastSigBits=?";

        protected UserDAO(Database database) throws SQLException {
            super(database, "Users", UserDTO.class);
        }

        public UserDTO findOne(UUID uuid) throws SQLException {
            PreparedStatement statement = prepareStatement(findOneByUUID);

            statement.setLong(1, uuid.getMostSignificantBits());
            statement.setLong(2, uuid.getLeastSignificantBits());

            ResultSet result = statement.executeQuery();

            UserDTO dto = result.next() ? fromResult(result) : null;

            result.close();

            return dto;
        }

    }
    //======================================================
    //=========================_DAO=========================
    //======================================================


    //======================================================
    //==========================CACHE=======================
    //======================================================
    class UserCache extends SaveCache<User, UserRepository> {

        public UserCache(UserRepository repository) {
            super(repository);
        }

        public User getIfMatchUUID(UUID uuid) {
            for (Reference<User> reference : references) {
                User user = extract(reference);

                if (user != null && user.identity().getUuid().equals(uuid)) {
                    return user;
                }
            }

            return null;
        }
    }
    //======================================================
    //=========================_CACHE=======================
    //======================================================


    //======================================================
    //=========================LOADER=======================
    //======================================================
    class UserLoader extends BaseLoader<User, UserDTO> {

        @Override
        public User newEntity() {
            return new User();
        }

        @Override
        public User fromDTO(UserDTO dto) throws SQLException {
            User user = super.fromDTO(dto);

            Identity identity = identityRepository.findByUser(dto);

            user.setIdentity(identity);
            user.setPassword(dto.password);

            user.setFlag(new UserFlag(dto.flag));
            user.setSellItems(new IntegerList(dto.autoSellItems));
            user.setRank(rankRepository.findOneOrDefault(dto.rankId));

            user.setMoney(moneyRepository.findOneByIdentity(identity));
            user.setGroup(groupUserRepository.findOneByIdentity(identity));
            user.setPlot(plotUserRepository.findOneByIdentity(identity));
            user.setClan(clanUserRepository.findOneByIdentity(identity));
            user.setSkill(skillUserRepository.findOneByIdentity(identity));
            user.setKit(kitUserRepository.findOneByIdentity(identity));

            return user;
        }

        @Override
        public UserDTO toDTO(User entity) throws SQLException {
            UserDTO dto = super.toDTO(entity);

            Identity identity = entity.identity();

            dto.name = identity.getName();
            dto.fastName = identity.getName().toLowerCase();
            dto.password = entity.getPassword();
            dto.flag = entity.flags().get();
            dto.autoSellItems = entity.getSellItems().toBytes();
            dto.rankId = entity.rank().getId();

            dto.skinId = (identity.getSkin() == null ? 0 : identity.getSkin().getId());
            dto.ban = identity.getBan();
            dto.mute = identity.getMute();
            dto.lastLogin = identity.getLastLogin();
            dto.onlineAllTime = identity.getOnlineAllTime();

            return dto;
        }
    }
    //======================================================
    //========================_LOADER=======================
    //======================================================
}
