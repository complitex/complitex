package ru.complitex.eirc.account.component.input;

import org.apache.wicket.model.IModel;
import ru.complitex.catalog.component.ItemInput;
import ru.complitex.catalog.entity.Item;
import ru.complitex.catalog.entity.Locale;
import ru.complitex.catalog.service.CatalogService;
import ru.complitex.catalog.util.Dates;

import javax.inject.Inject;
import java.time.LocalDate;

/**
 * @author Ivanov Anatoliy
 */
public class NameInput extends ItemInput {
    @Inject
    private CatalogService catalogService;

    public NameInput(String id, int catalog, IModel<Long> model, int value, LocalDate date) {
        super(id, catalog, model, value, date);
    }

    @Override
    public void onSubmit() {
        if (getObjectId() == null) {
            String name = getInputText();

            if (name != null && !name.isEmpty()) {
                name = name.toUpperCase();

                int catalog = getCatalog().getKeyId();

                int value = getValue().getKeyId();

                LocalDate date = Dates.now();

                Item item = catalogService.getItem(catalog, date)
                        .withText(value, name, Locale.SYSTEM)
                        .get();

                if (item == null) {
                    item = catalogService.newItem(catalog)
                            .withText(value, name, Locale.SYSTEM)
                            .get();

                    catalogService.insert(item, date);
                }

                getModel().setObject(item.getId());
            }
        }
    }
}
