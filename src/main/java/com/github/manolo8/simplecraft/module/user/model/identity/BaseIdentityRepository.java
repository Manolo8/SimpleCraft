package com.github.manolo8.simplecraft.module.user.model.identity;

import com.github.manolo8.simplecraft.core.data.cache.Cache;
import com.github.manolo8.simplecraft.core.data.connection.Database;
import com.github.manolo8.simplecraft.core.data.model.base.BaseRepository;
import com.github.manolo8.simplecraft.module.user.identity.Identity;
import com.github.manolo8.simplecraft.module.user.identity.IdentityRepository;

import java.sql.SQLException;

public abstract class BaseIdentityRepository<E extends BaseIdentity,
        O extends BaseIdentityDTO,
        D extends BaseIdentityDAO<O>,
        C extends BaseIdentityCache<E, ?>,
        L extends BaseIdentityLoader<E, O>>
        extends BaseRepository<E, O, D, C, L> {

    protected final IdentityRepository identityRepository;

    //======================================================
    //======================REPOSITORY======================
    //======================================================
    public BaseIdentityRepository(Database database, IdentityRepository identityRepository) {
        super(database);

        this.identityRepository = identityRepository;
    }

    public IdentityRepository getIdentityRepository() {
        return identityRepository;
    }

    /**
     * @param id id da identidade
     * @return procura uma identidade pelo id
     * @throws SQLException
     */
    public E findOneByIdentity(int id) throws SQLException {
        synchronized (Cache.LOCKER) {
            Identity identity = identityRepository.findOne(id);

            E e = null;

            if (identity != null) {
                e = asyncFindOneByIdentity(identity);
            }

            return e;
        }
    }

    /**
     * @param name nome da identidade
     * @return procura uma identidade pelo nome
     * @throws SQLException
     */
    public E findOneByIdentity(String name) throws SQLException {
        synchronized (Cache.LOCKER) {
            Identity identity = identityRepository.findByName(name);

            E e = null;

            if (identity != null) {
                e = asyncFindOneByIdentity(identity);
            }

            return e;
        }
    }

    /**
     * @param identity a identidade do jogador
     * @return procura por uma entidade representada por essa
     * identidade, ou cria uma nova, caso não encontre
     * @throws SQLException
     */
    public E findOneByIdentity(Identity identity) throws SQLException {
        synchronized (Cache.LOCKER) {
            return asyncFindOneByIdentity(identity);
        }
    }

    /**
     * @param identity a identidade do jogador
     * @return procura por uma entidade representada por essa
     * identidade, ou cria uma nova, caso não encontre
     * @throws SQLException
     */
    private E asyncFindOneByIdentity(Identity identity) throws SQLException {
        E e = cache.getIfMatchIdentity(identity);

        if (e == null) {
            O dto = dao.findByIdentity(identity.getId());

            if (dto == null) {
                e = create(identity);
            } else {
                e = fromDTO(dto);
            }

        }

        return e;
    }

    /**
     * @param identity A identidade do jogador
     * @return uma nova entidade representado pela identity
     * @throws SQLException
     */
    private E create(Identity identity) throws SQLException {
        O o = loader.newDTO();

        o.setIdentityId(identity.getId());

        return super.create(o);
    }
    //======================================================
    //=====================_REPOSITORY======================
    //======================================================
}
