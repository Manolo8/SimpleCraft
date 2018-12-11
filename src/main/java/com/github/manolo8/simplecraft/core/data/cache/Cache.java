package com.github.manolo8.simplecraft.core.data.cache;

import com.github.manolo8.simplecraft.core.data.model.base.BaseEntity;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.*;

public class Cache<E extends BaseEntity> {

    public final static Object LOCKER = new Object();

    protected final Set<Reference<E>> references;
    protected final List<FastIndexer> indexers;
    protected final HashMap<Integer, Reference<E>> idIndex;

    protected final ReferenceQueue<BaseEntity> referenceQueue;

    public Cache() {

        this.references = new HashSet<>();
        this.indexers = new ArrayList<>();
        this.idIndex = new HashMap<>();
        this.referenceQueue = new ReferenceQueue<>();

        addIndexers();
    }

    protected void addIndexers() {
        indexers.add(new FastIndexer<EntityReference<E>>() {
            @Override
            public void added(EntityReference<E> reference) {
                idIndex.put(reference.id, reference);
            }

            @Override
            public void collected(EntityReference<E> reference) {
                idIndex.remove(reference.id);
            }

            @Override
            public void changed(EntityReference<E> reference) {
            }
        });
    }

    protected E extract(Reference<E> reference) {
        E entity = reference.get();

        return entity == null || entity.isRemoved() ? null : entity;
    }

    //======================================================
    //========================SEARCH========================
    //======================================================
    public E getIfMatchId(int id) {
        Reference<E> s = idIndex.get(id);

        return s == null ? null : s.get();
    }

    public Reference<E> getReference(int id) {
        return idIndex.get(id);
    }
    //======================================================
    //=======================_SEARCH========================
    //======================================================


    //======================================================
    //=========================CACHE========================
    //======================================================
    protected void save(E entity) {
    }

    public void add(E e) {
        Reference<E> s = newInstance(e);

        e.cache(this);

        added(s);

        synchronized (LOCKER) {
            references.add(s);
        }
    }

    protected Reference<E> newInstance(E e) {
        return new EntityReference<>(e, referenceQueue);
    }

    protected void added(Reference<E> s) {
        for (FastIndexer indexer : indexers) {
            indexer.added((EntityReference) s);
        }
    }

    protected void collected(Reference<E> s) {
        references.remove(s);

        for (FastIndexer indexer : indexers) {
            indexer.collected((EntityReference) s);
        }
    }

    protected void checkChanges(Reference<E> s) {
        for (FastIndexer indexer : indexers) {
            indexer.changed((EntityReference) s);
        }
    }

    public void modified(BaseEntity entity) {
        checkChanges(getReference(entity.getId()));
        CacheService.modified(entity);
    }

    public void checkQueue() {
        Reference r;
        while ((r = referenceQueue.poll()) != null) {
            collected(r);
        }
    }
    //======================================================
    //========================_CACHE========================
    //======================================================

    protected abstract class FastIndexer<ER extends EntityReference> {
        public abstract void added(ER reference);

        public abstract void collected(ER reference);

        public abstract void changed(ER reference);
    }

    protected class EntityReference<E extends BaseEntity> extends WeakReference<E> {

        protected final int id;

        public EntityReference(E referent, ReferenceQueue q) {
            super(referent, q);

            this.id = referent.getId();
        }

        @Override
        public int hashCode() {
            return id;
        }

        @Override
        public boolean equals(Object obj) {
            return this == obj;
        }
    }
}
