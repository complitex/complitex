package ru.complitex.pspoffice.address.page;

import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;
import ru.complitex.catalog.component.CatalogPanel;
import ru.complitex.catalog.entity.Catalog;
import ru.complitex.catalog.entity.Item;
import ru.complitex.catalog.entity.Value;
import ru.complitex.catalog.util.Dates;

import java.time.LocalDate;

/**
 * @author Ivanov Anatoliy
 */
public class AddressPage extends BasePage {
    private final CatalogPanel catalogPanel;

    private final LocalDate date = Dates.now();

    public AddressPage(int catalog) {
        super();

        catalogPanel = new CatalogPanel("table", catalog) {
            @Override
            protected IModel<String> newModel(IModel<Item> model, Value value) {
                return AddressPage.this.newModel(model, value);
            }

            @Override
            protected Component newReferenceGroup(String id, IModel<Item> model, Value value) {
                return AddressPage.this.newReferenceGroup(id, model, value);
            }

            @Override
            protected boolean isRequired(Value value) {
                return AddressPage.this.isRequired(value);
            }

            @Override
            protected boolean validate(Item item) {
                return AddressPage.this.validate(item);
            }
        };

        add(catalogPanel);
    }

    public LocalDate getDate() {
        return date;
    }

    protected IModel<String> newModel(IModel<Item> model, Value value) {
        return null;
    }

    protected Component newReferenceGroup(String id, IModel<Item> model, Value value) {
        return null;
    }

    protected boolean isRequired(Value value) {
        return false;
    }

    protected boolean validate(Item item) {
        return true;
    }

    protected Catalog getCatalog() {
        return catalogPanel.getCatalog();
    }
}
