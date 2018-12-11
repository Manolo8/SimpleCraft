package com.github.manolo8.simplecraft.tools.item;

import com.github.manolo8.simplecraft.core.data.cache.Cache;
import com.github.manolo8.simplecraft.core.data.connection.Database;
import com.github.manolo8.simplecraft.core.data.model.base.BaseDAO;
import com.github.manolo8.simplecraft.core.data.model.base.BaseLoader;
import com.github.manolo8.simplecraft.core.data.model.base.BaseRepository;
import com.github.manolo8.simplecraft.core.data.model.base.DTO;
import org.bukkit.inventory.ItemStack;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Objects;

public class ItemRepository
        extends BaseRepository<Item,
        ItemRepository.ItemDTO,
        ItemRepository.ItemDAO,
        ItemRepository.ItemCache,
        ItemRepository.ItemLoader> {


    public ItemRepository(Database database) {
        super(database);
    }

    @Override
    protected ItemDAO initDao() throws SQLException {
        return new ItemDAO(database);
    }

    @Override
    protected ItemLoader initLoader() {
        return new ItemLoader();
    }

    @Override
    protected ItemCache initCache() {
        return new ItemCache();
    }

    public int findOrCreateId(ItemStack item) throws SQLException {
        return item == null ? 0 : findOrCreate(item).getId();
    }

    private Item findOrCreate(ItemStack itemStack) throws SQLException {

        if (itemStack.getAmount() != 1) {
            itemStack = itemStack.clone();
            itemStack.setAmount(1);
        }

        int hash = itemStack.hashCode();
        int material = itemStack.getType().getId();

        Item e = cache.getIfMatchHash(material, hash);

        if (e == null) {
            ItemDTO o = dao.findByHash(material, hash);

            if (o == null) {
                Item item = new Item();
                item.set(itemStack);
                item.setHash(hash);

                e = create(loader.toDTO(item));
            } else {
                e = fromDTO(o);
            }

        }

        return e;
    }

    //======================================================
    //=====================_REPOSITORY======================
    //======================================================


    //======================================================
    //==========================DTO=========================
    //======================================================
    class ItemDTO extends DTO {

        private int hash;
        private int material;
        private byte[] bytes;

    }
    //======================================================
    //==========================DTO=========================
    //======================================================


    //======================================================
    //==========================DAO=========================
    //======================================================
    class ItemDAO extends BaseDAO<ItemDTO> {

        private final String findByHashAndMaterialQuery = "SELECT * FROM Items WHERE material=? AND hash=?";

        protected ItemDAO(Database database) throws SQLException {
            super(database, "Items", ItemDTO.class);
        }

        public ItemDTO findByHash(int material, int hash) throws SQLException {
            PreparedStatement statement = prepareStatement(findByHashAndMaterialQuery);

            statement.setInt(1, material);
            statement.setInt(2, hash);

            ResultSet result = statement.executeQuery();

            ItemDTO dto = result.next() ? fromResult(result) : null;

            statement.close();

            return dto;
        }
    }
    //======================================================
    //=========================_DAO=========================
    //======================================================


    //======================================================
    //=========================CACHE========================
    //======================================================
    class ItemCache extends Cache<Item> {

        private HashMap<ItemSearch, ItemReference> itemSearchIndex;

        public ItemCache() {
            super();
        }

        @Override
        protected void addIndexers() {
            super.addIndexers();

            indexers.add(new FastIndexer<ItemReference>() {
                @Override
                public void added(ItemReference reference) {
                    itemSearchIndex.put(reference.itemSearch, reference);
                }

                @Override
                public void collected(ItemReference reference) {
                    itemSearchIndex.remove(reference.itemSearch);
                }

                @Override
                public void changed(ItemReference reference) {
                    Item e = reference.get();

                    if (e != null) {

                        if (e.getHash() != reference.itemSearch.hash || e.getType() != reference.itemSearch.type) {
                            itemSearchIndex.remove(reference.itemSearch);
                            reference.itemSearch = new ItemSearch(e.getHash(), e.getType());
                            itemSearchIndex.put(reference.itemSearch, reference);

                            System.out.println("ItemHash changed...  How?");

                        }

                    }
                }
            });
        }

        public Item getIfMatchHash(int material, int hash) {
            synchronized (Cache.LOCKER) {
                return itemSearchIndex.get(new ItemSearch(hash, material)).get();
            }
        }

        @Override
        protected Reference<Item> newInstance(Item item) {
            return new ItemReference(item, referenceQueue);
        }

        protected class ItemReference extends EntityReference<Item> {

            private ItemSearch itemSearch;

            public ItemReference(Item referent, ReferenceQueue q) {
                super(referent, q);

                this.itemSearch = new ItemSearch(referent.getHash(), referent.getType());
            }
        }
    }

    class ItemSearch {

        private int hash;
        private int type;

        public ItemSearch(int hash, int type) {
            this.hash = hash;
            this.type = type;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ItemSearch that = (ItemSearch) o;
            return hash == that.hash &&
                    type == that.type;
        }

        @Override
        public int hashCode() {
            return Objects.hash(hash, type);
        }
    }
    //======================================================
    //========================_CACHE========================
    //======================================================


    //======================================================
    //=========================LOADER=======================
    //======================================================
    class ItemLoader extends BaseLoader<Item, ItemDTO> {

        private final ItemStackData stackData;

        public ItemLoader() {
            this.stackData = new ItemStackData();
        }

        @Override
        public Item newEntity() {
            return new Item();
        }

        @Override
        public Item fromDTO(ItemDTO dto) throws SQLException {
            Item item = super.fromDTO(dto);

            item.set(stackData.fromBytes(dto.bytes));
            item.setHash(dto.hash);

            return item;
        }

        @Override
        public ItemDTO toDTO(Item entity) throws SQLException {
            ItemDTO dto = super.toDTO(entity);

            dto.hash = entity.getHash();
            dto.material = entity.get().getType().getId();
            dto.bytes = stackData.toBytes(entity.get());

            return dto;
        }
    }
    //======================================================
    //=========================_LOADER======================
    //======================================================
}
