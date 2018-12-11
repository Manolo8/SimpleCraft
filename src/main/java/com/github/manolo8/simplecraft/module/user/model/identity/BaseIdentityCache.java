package com.github.manolo8.simplecraft.module.user.model.identity;

import com.github.manolo8.simplecraft.core.data.cache.SaveCache;
import com.github.manolo8.simplecraft.core.data.model.base.Repository;
import com.github.manolo8.simplecraft.module.user.identity.Identity;

import java.lang.ref.Reference;

public class BaseIdentityCache<E extends BaseIdentity, R extends Repository<E>> extends SaveCache<E, R> {

    public BaseIdentityCache(R repository) {
        super(repository);
    }

    public E getIfMatchIdentity(Identity identity) {
        for (Reference<E> s : references) {
            E e = extract(s);
            if (e != null && e.getIdentity() == identity) {
                return e;
            }
        }
        return null;
    }
}
