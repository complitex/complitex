package ru.complitex.catalog.service;

import org.mybatis.cdi.Transactional;
import ru.complitex.catalog.entity.Catalog;
import ru.complitex.catalog.entity.Data;
import ru.complitex.catalog.entity.Item;
import ru.complitex.catalog.mapper.CatalogMapper;
import ru.complitex.catalog.mapper.DataMapper;
import ru.complitex.catalog.mapper.ItemMapper;
import ru.complitex.ui.entity.Filter;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Ivanov Anatoliy
 */
@RequestScoped
public class CatalogService {
    @Inject
    private CatalogMapper catalogMapper;

    @Inject
    private ItemMapper itemMapper;

    @Inject
    private DataMapper dataMapper;

    public Catalog getCatalog(Long id) {
        return catalogMapper.selectCatalog(id);
    }

    public Catalog getCatalog(int catalog) {
        return catalogMapper.selectCatalogByKeyId(catalog);
    }

    public abstract class ItemBuilder<T> {
        private final Item item;

        public ItemBuilder(int catalog) {
            item = new Item(getCatalog(catalog));
        }

        public ItemBuilder<T> withLong(int value, Long numeric) {
            item.setLong(value, numeric);

            return this;
        }

        public ItemBuilder<T> withText(int value, String text, int locale) {
            item.setText(value, text, locale);

            return this;
        }

        public ItemBuilder<T> withText(int value, String text) {
            item.setText(value, text);

            return this;
        }

        public ItemBuilder<T> withTimestamp(int value, LocalDate date) {
            item.setTimestamp(value, date);

            return this;
        }

        public ItemBuilder<T> withReferenceId(int value, Long referenceId) {
            item.setReferenceId(value, referenceId);

            return this;
        }

        public Item item() {
            return item;
        }

        public abstract T get();

        public Optional<T> getOptional(){
            return Optional.ofNullable(get());
        }
    }

    public abstract class FilterItemBuilder<T> extends ItemBuilder<T> {
        private final Filter<Item> filter;

        public FilterItemBuilder(int catalog) {
            super(catalog);

            filter = new Filter<>(item());
        }

        public Filter<Item> filter() {
            return filter;
        }

        @SuppressWarnings("unchecked")
        public FilterItemBuilder<T> withNotNull(int value) {
            filter.getMap().computeIfAbsent("notNull", k -> new HashSet<>());

            ((Set<Integer>)filter.getMap().get("notNull")).add(value);

            return this;
        }

        @SuppressWarnings("unchecked")
        public FilterItemBuilder<T> withoutId(Long id) {
            filter.getMap().computeIfAbsent("noId", k -> new HashSet<>());

            ((Set<Long>)filter.getMap().get("noId")).add(id);

            return this;
        }
    }

    public ItemBuilder<Item> newItem(int catalog) {
        return new ItemBuilder<>(catalog) {
            @Override
            public Item get() {
                return item();
            }
        };
    }

    public Item getItem(int catalog, Long id, LocalDate date) {
        return itemMapper.selectItem(getCatalog(catalog).getTable(), id, date);
    }

    public String getText(int catalog, Long id, int value, LocalDate date) {
        return getItem(catalog, id, date).getText(value);
    }

    public String getText(int catalog, Long id, int value, int locale, LocalDate date) {
        return getItem(catalog, id, date).getText(value, locale);
    }

    public Item getReferenceItem(Item item, int value, LocalDate date) {
        return itemMapper.selectItem(item.getCatalog().getValue(value).getReferenceCatalog().getTable(), item.getReferenceId(value), date);
    }

    public Long getReferenceId(int catalog, Long id, int value, LocalDate date) {
        return getItem(catalog, id, date).getReferenceId(value);
    }

    public Long getItemsCount(Filter<Item> filter) {
        return itemMapper.selectItemsCount(filter);
    }

    public FilterItemBuilder<Long> getItemsCount(int catalog, LocalDate date) {
        return new FilterItemBuilder<>(catalog) {
            @Override
            public Long get() {
                return getItemsCount(filter().date(date));
            }
        };
    }

    public List<Item> getItems(Filter<Item> filter) {
        return itemMapper.selectItems(filter);
    }

    public FilterItemBuilder<List<Item>> getItems(int catalog, LocalDate date) {
        return new FilterItemBuilder<>(catalog) {
            @Override
            public List<Item> get() {
                return getItems(filter().date(date));
            }
        };
    }

    public ItemBuilder<Item> getItem(int catalog, LocalDate date) {
        return new ItemBuilder<>(catalog) {
            @Override
            public Item get() {
                List<Item> items = itemMapper.selectItems(new Filter<>(item()).date(date));

                return !items.isEmpty() ? items.get(0) : null;
            }
        };
    }

    public ItemBuilder<Long> getReferenceId(int catalog, int value, LocalDate date) {
        return new ItemBuilder<>(catalog) {
            @Override
            public Long get() {
                List<Item> items = itemMapper.selectItems(new Filter<>(item()).date(date));

                return items.get(0).getReferenceId(value);
            }
        };
    }

    @Transactional
    public Item insert(Item item, LocalDate date) {
        itemMapper.insertItem(item);

        item.getData().stream()
                .filter(data -> !data.isEmpty())
                .peek(data -> data.setStartDate(date))
                .forEach(data -> dataMapper.insertData(item, data));

        return item;
    }

    @Transactional
    public Item inserts(Item item, LocalDate date) {
        itemMapper.insertItem(item);

        dataMapper.insertsData(item, item.getData().stream()
                .filter(data -> !data.isEmpty())
                .peek(data -> data.setStartDate(date))
                .collect(Collectors.toList()));

        return item;
    }

    @Transactional
    public boolean update(Item item, LocalDate date) {
        boolean updated = false;

        for (Data db : getItem(item.getCatalog().getKeyId(), item.getId(), date).getData()) {
            Data data = item.getData(db.getValue());

            if (data != null) {
                if (!data.isEqual(db)) {
                    data.setId(null);

                    data.setStartDate(date);

                    db.setEndDate(date);

                    dataMapper.updateData(item, db);

                    updated = true;
                }
            } else {
                dataMapper.deleteData(item, db);

                updated = true;
            }
        }

        for (Data data : item.getData()) {
            if (data.getId() == null && !data.isEmpty()) {
                if (data.getStartDate() == null) {
                    Data dataBefore = dataMapper.selectDataBefore(item, data, date);

                    if (dataBefore != null) {
                        data.setStartDate(dataBefore.getEndDate());
                    }
                }

                if (data.getEndDate() == null) {
                    Data dataAfter = dataMapper.selectDataAfter(item, data, date);

                    if (dataAfter != null) {
                        data.setEndDate(dataAfter.getStartDate());
                    }
                }

                dataMapper.insertData(item, data);

                updated = true;
            }
        }

        return updated;
    }
}
