package ru.complitex.catalog.page;

import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;
import ru.complitex.catalog.component.CatalogPanel;
import ru.complitex.catalog.entity.Catalog;
import ru.complitex.catalog.entity.Item;
import ru.complitex.catalog.entity.Value;
import ru.complitex.catalog.util.Dates;
import ru.complitex.ui.component.table.Column;
import ru.complitex.ui.page.BasePage;

import java.time.LocalDate;

/**
 * @author Ivanov Anatoliy
 */
public class CatalogPage extends BasePage {
    private final CatalogPanel catalogPanel;

    public CatalogPage(int catalog) {
        add(catalogPanel = new CatalogPanel("catalog", catalog) {
            @Override
            protected IModel<String> getColumnModel(IModel<Item> model, Value value) {
                return CatalogPage.this.getColumnModel(model, value);
            }

            @Override
            protected Column<Item> newColumn(Value value) {
                Column<Item> column = CatalogPage.this.newColumn(value);

                return column != null ? column : super.newColumn(value);
            }

            @Override
            protected int getReferenceValueKeyId(Value value) {
                return CatalogPage.this.getReferenceValueKeyId(value);
            }

            @Override
            protected Component newReferenceGroup(String id, IModel<Item> model, Value value) {
                return CatalogPage.this.newReferenceGroup(id, model, value);
            }

            @Override
            protected boolean isVisible(Value value) {
                return CatalogPage.this.isVisible(value);
            }

            @Override
            protected boolean isLongColumn(Value value) {
                return CatalogPage.this.isLongColumn(value);
            }

            @Override
            protected boolean isRequired(Value value) {
                return CatalogPage.this.isRequired(value);
            }

            @Override
            protected Component newFormGroup(String id, Catalog catalog, Value value, IModel<Item> model) {
                return CatalogPage.this.newFormGroup(id, catalog, value, model);
            }

            @Override
            protected boolean validate(Item item) {
                return CatalogPage.this.validate(item);
            }
        });
    }

    protected IModel<String> getColumnModel(IModel<Item> model, Value value) {
        return null;
    }

    protected Column<Item> newColumn(Value value) {
        return null;
    }

    protected Component newReferenceGroup(String id, IModel<Item> model, Value value) {
        return null;
    }
    protected int getReferenceValueKeyId(Value value) {
        return -1;
    }

    protected boolean isVisible(Value value) {
        return true;
    }

    protected boolean isLongColumn(Value value) {
        return false;
    }

    protected boolean isRequired(Value value) {
        return false;
    }

    protected Component newFormGroup(String id, Catalog catalog, Value value, IModel<Item> model) {
        return null;
    }

    protected boolean validate(Item item) {
        return true;
    }

    protected Catalog getCatalog() {
        return catalogPanel.getCatalog();
    }

    protected LocalDate getDate() {
        return Dates.now();
    }
}
