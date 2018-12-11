package com.github.manolo8.simplecraft.core.data.model.named;

import com.github.manolo8.simplecraft.core.data.cache.SaveCache;
import com.github.manolo8.simplecraft.core.data.model.base.Repository;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.util.*;

public class NamedCache<E extends NamedEntity, R extends Repository<E>> extends SaveCache<E, R> {

    protected final TreeMap<String, Reference<E>> nameIndex;

    public NamedCache(R repository) {
        super(repository);

        this.nameIndex = new TreeMap<>();
    }

    @Override
    protected void addIndexers() {
        super.addIndexers();

        //null safe
        indexers.add(new FastIndexer<NamedEntityReference<E>>() {
            @Override
            public void added(NamedEntityReference<E> reference) {
                if (reference.name != null) {
                    nameIndex.put(reference.name, reference);
                }
            }

            @Override
            public void collected(NamedEntityReference<E> reference) {
                if (reference.name != null) {
                    nameIndex.remove(reference.name);
                }
            }

            @Override
            public void changed(NamedEntityReference<E> reference) {
                E e = reference.get();

                if (e != null) {

                    if (!e.isRemoved()) {
                        String old = reference.name;
                        String name = e.getFastName();

                        if (!Objects.equals(name, old)) {
                            if (old != null) nameIndex.remove(old);
                            if (name != null) nameIndex.put(name, reference);
                            reference.name = name;
                        }
                    }
                }
            }
        });
    }

    //======================================================
    //========================SEARCH========================
    //======================================================
    public E getIfMatchName(String name) {
        Reference<E> s = nameIndex.get(name);

        return s == null ? null : s.get();
    }
    //======================================================
    //=======================_SEARCH========================
    //======================================================

    //======================================================
    //=========================FILL=========================
    //======================================================
    public List<String> getIfStartWith(String start, int limit) {
        long time = System.nanoTime();
        Iterator<Map.Entry<String, Reference<E>>> iterator = nameIndex.subMap(start, start + Character.MAX_VALUE).entrySet().iterator();

        List<String> names = new ArrayList<>();

        while (iterator.hasNext() && --limit > 0) {
            Map.Entry entry = iterator.next();

            Reference<E> r = (Reference) entry.getValue();

            if (r.get() != null && !r.get().isRemoved()) {
                names.add((String) entry.getKey());
            }
        }

        System.out.println(System.nanoTime() - time);

        return names;
    }
    //======================================================
    //========================_FILL=========================
    //======================================================


    //======================================================
    //=========================INDEX========================
    //======================================================
    protected Reference<E> newInstance(E e) {
        return new NamedEntityReference<>(e, referenceQueue);
    }

    protected class NamedEntityReference<E extends NamedEntity> extends EntityReference<E> {

        private String name;

        public NamedEntityReference(E referent, ReferenceQueue q) {
            super(referent, q);

            this.name = referent.getFastName();
        }
    }
    //======================================================
    //========================_INDEX========================
    //======================================================
}
