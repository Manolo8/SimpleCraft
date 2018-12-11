package com.github.manolo8.simplecraft.core.data.model.named;

import com.github.manolo8.simplecraft.core.data.cache.Cache;
import com.github.manolo8.simplecraft.core.data.connection.Database;
import com.github.manolo8.simplecraft.core.data.model.base.BaseRepository;

import java.sql.SQLException;
import java.util.List;

public abstract class NamedRepository<E extends NamedEntity,
        O extends NamedDTO,
        D extends NamedDAO<O>,
        C extends NamedCache<E, ?>,
        L extends NamedLoader<E, O>>
        extends BaseRepository<E, O, D, C, L> {

    public NamedRepository(Database database) {
        super(database);
    }

    public E create(String name) throws SQLException {
        O dto = loader.newDTO();

        dto.name = name;
        dto.fastName = name == null ? null : name.toLowerCase();

        return create(dto);
    }

    public E findByName(String name) throws SQLException {
        synchronized (Cache.LOCKER) {
            name = name.toLowerCase();

            E entity = cache.getIfMatchName(name);

            if (entity == null) {
                O dto = dao.findByName(name);

                entity = fromDTO(dto);
            }

            return entity;
        }
    }

    public List<String> findNames(String name) throws SQLException {
        List<String> names;

        synchronized (Cache.LOCKER) {
            names = cache.getIfStartWith(name, 20);
        }

        if (names.size() < 20) {
            List<String> query = dao.findNameList(name, 20 - names.size());

            for (String string : query)
                if (!names.contains(string))
                    names.add(string);

        }

        return names;
    }
}
