package ru.complitex.catalog.component;

import org.apache.wicket.model.IModel;
import ru.complitex.catalog.entity.Catalog;
import ru.complitex.catalog.entity.Item;
import ru.complitex.catalog.entity.Locale;
import ru.complitex.catalog.entity.Value;
import ru.complitex.catalog.service.CatalogService;
import ru.complitex.ui.component.form.AutoComplete;
import ru.complitex.ui.entity.Filter;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.Iterator;

/**
 * @author Ivanov Anatoliy
 */
public class ItemInput extends AutoComplete<Item> {
    @Inject
    private CatalogService catalogService;

    private final Catalog catalog;
    private final Value value;

    private final LocalDate date;

    private final Filter<Item> filter;

    public ItemInput(String id, int catalog, IModel<Long> model, int value, LocalDate date) {
        super(id, model);

        this.catalog = catalogService.getCatalog(catalog);
        this.value = this.catalog.getValue(value, Locale.SYSTEM);
        this.date = date;

        filter = newFilter();
    }

    public Catalog getCatalog() {
        return catalog;
    }

    public Value getValue() {
        return value;
    }

    protected Filter<Item> newFilter(){
        return new Filter<>(new Item(catalog), 5).date(date).like();
    }

    protected void onFilter(Filter<Item> filter){}

    @Override
    protected String getTextValue(Item item) {
        return item != null ? item.getText(value) : "";
    }

    @Override
    protected Iterator<Item> getChoices(String input) {
        filter.getObject().setText(value, input);

        onFilter(filter);

        return catalogService.getItems(filter).iterator();
    }

    @Override
    protected Long getId(Item item) {
        return item != null ? item.getId() : null;
    }

    @Override
    protected Item getObject(Long id) {
        return id != null ? catalogService.getItem(catalog.getKeyId(), id, date) : null;
    }
}
