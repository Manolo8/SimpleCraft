package com.github.manolo8.simplecraft.core.data.model.base;

import com.github.manolo8.simplecraft.core.data.cache.Cache;
import com.github.manolo8.simplecraft.core.data.cache.CacheService;
import com.github.manolo8.simplecraft.core.data.connection.Database;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class BaseRepository<E extends BaseEntity,
        O extends DTO,
        D extends DAO<O>,
        C extends Cache<E>,
        L extends Loader<E, O>>
        implements Repository<E> {

    protected final Database database;
    protected D dao;
    protected C cache;
    protected L loader;

    public BaseRepository(Database database) {
        this.database = database;
    }

    public void init() throws SQLException {
        this.dao = initDao();
        this.cache = initCache();
        this.loader = initLoader();

        CacheService.register(cache);
    }

    protected abstract D initDao() throws SQLException;

    protected abstract L initLoader();

    protected abstract C initCache();

    public E findOne(int id) throws SQLException {
        synchronized (Cache.LOCKER) {
            E e = cache.getIfMatchId(id);

            if (e == null) {
                O o = dao.findOne(id);

                if (o != null) {
                    e = fromDTO(o);
                }
            }

            return e;
        }
    }

    public List<E> findByIdIn(List<Integer> ids) throws SQLException {
        synchronized (Cache.LOCKER) {
            List<E> list = new ArrayList<>();

            if (!ids.isEmpty()) {

                Iterator<Integer> i = ids.iterator();

                while (i.hasNext()) {

                    E e = cache.getIfMatchId(i.next());

                    if (e != null) {
                        list.add(e);
                        i.remove();
                    }
                }

                if (!ids.isEmpty()) {
                    list.addAll(fromDTO(dao.findByIdIn(ids)));
                }
            }

            return list;
        }
    }

    public List<E> findAll() throws SQLException {
        return findByIdIn(dao.findAll());
    }

    public E create(O o) throws SQLException {
        return fromDTO(dao.create(o));
    }

    public void save(E entity) throws SQLException {
        dao.save(loader.toDTO(entity));
    }

    protected E fromDTO(O dto) throws SQLException {
        E entity = null;

        if (dto != null) {
            entity = loader.fromDTO(dto);

            //Reseta
            entity.saved();

            cache.add(entity);
        }

        return entity;
    }

    protected List<E> fromDTO(List<O> dtos) throws SQLException {
        List<E> entities = new ArrayList<>();

        for (O dto : dtos) {
            if (dto != null) {
                entities.add(fromDTO(dto));
            }
        }

        return entities;
    }

    public void delete(E entity) throws SQLException {
        dao.delete(entity.getId());
    }

    public C getCache() {
        return cache;
    }
}
