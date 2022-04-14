package ru.complitex.catalog.component;

import de.agilecoders.wicket.core.markup.html.bootstrap.common.NotificationPanel;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import ru.complitex.catalog.entity.Catalog;
import ru.complitex.catalog.entity.Item;
import ru.complitex.catalog.entity.Type;
import ru.complitex.catalog.entity.Value;
import ru.complitex.catalog.service.CatalogService;
import ru.complitex.catalog.util.Dates;
import ru.complitex.ui.component.table.Column;
import ru.complitex.ui.component.table.Provider;
import ru.complitex.ui.component.table.Table;
import ru.complitex.ui.entity.Filter;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Ivanov Anatoliy
 */
public class CatalogPanel extends Panel {
    @Inject
    private CatalogService catalogService;

    private final CatalogModal catalogModal;

    private final Catalog catalog;

    public CatalogPanel(String id, int catalog) {
        super(id);

        this.catalog = catalogService.getCatalog(catalog);

        List<Value> values = this.catalog.getValues().stream()
                .filter(this::isVisible)
                .collect(Collectors.toList());

        List<Column<Item>> columns = new ArrayList<>();

        columns.add(new IdColumn());

        values.forEach(value -> columns.add(newColumn(value)));

        Filter<Item> filter = new Filter<>(new Item(CatalogPanel.this.catalog)).date(Dates.now()).like();

        Provider<Item> provider = new Provider<>(filter) {
            @Override
            public long size() {
                return catalogService.getItemsCount(getFilter());
            }

            @Override
            protected List<Item> list() {
                return catalogService.getItems(getFilter());
            }
        };

        Form<Item> form = new Form<>("form");
        add(form);

        NotificationPanel notification = new NotificationPanel("notification");
        notification.setOutputMarkupId(true);
        form.add(notification);

        Table<Item> table = new Table<>("table", columns, provider) {
            @Override
            protected void onClick(IModel<Item> itemModel, AjaxRequestTarget target) {
                catalogModal.edit(itemModel.getObject().getId(), target);
            }
        };
        form.add(table);

        catalogModal = new CatalogModal("modal", CatalogPanel.this.catalog, values) {
            @Override
            protected boolean isRequired(Value value) {
                return CatalogPanel.this.isRequired(value);
            }

            @Override
            protected boolean isLongColumn(Value value) {
                return CatalogPanel.this.isLongColumn(value);
            }

            @Override
            protected Component newFormGroup(String id, Catalog catalog, Value value, IModel<Item> model) {
                Component group = CatalogPanel.this.newFormGroup(id, catalog, value, model);

                return group != null ? group : super.newFormGroup(id, catalog, value, model);
            }

            @Override
            protected int getReferenceValueKeyId(Value value) {
                int keyId =  CatalogPanel.this.getReferenceValueKeyId(value);

                return keyId != -1 ? keyId : super.getReferenceValueKeyId(value);
            }

            @Override
            protected Component newReferenceGroup(String id, IModel<Item> model, Value value) {
                Component group = CatalogPanel.this.newReferenceGroup(id, model, value);

                return group != null ? group : super.newReferenceGroup(id, model, value);
            }

            @Override
            protected boolean validate(Item item) {
                return CatalogPanel.this.validate(item);
            }

            @Override
            protected void onSave(AjaxRequestTarget target) {
                target.add(notification, table);
            }
        };
        form.add(catalogModal);

        AjaxLink<Item> create = new AjaxLink<>("create") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                catalogModal.create(target);
            }
        };
        form.add(create);
    }

    protected Column<Item> newColumn(Value value) {
        return new DataColumn(value) {
            @Override
            protected IModel<? extends Serializable> getModel(IModel<Item> model) {
                IModel<String> displayModel = CatalogPanel.this.getColumnModel(model, value);

                if (displayModel != null) {
                    return displayModel;
                }

                if (value.getType().getKeyId() == Type.REFERENCE) {
                    Long referenceId = model.getObject().getReferenceId(value.getKeyId());

                    if (referenceId != null) {
                        int keyId = getReferenceValueKeyId(value);

                        Value referenceValue = value.getReferenceCatalog().getValues().stream()
                                .filter(v -> v.getKeyId() == keyId ||
                                        v.getName().contains(value.getReferenceCatalog().getName() + "_NAME") ||
                                        v.getName().contains(value.getReferenceCatalog().getName() + "_NUMBER"))
                                .findFirst()
                                .orElse(null);

                        Item item = catalogService.getItem(value.getReferenceCatalog().getKeyId(), referenceId, Dates.now());

                        return new Model<>(item != null ? item.getText(referenceValue) : referenceValue);
                    }
                }

                return super.getModel(model);
            }
        };
    }

    protected int getReferenceValueKeyId(Value value) {
        return -1;
    }

    protected Component newReferenceGroup(String id, IModel<Item> model, Value value) {
        return null;
    }

    protected IModel<String> getColumnModel(IModel<Item> model, Value value) {
        return null;
    }

    protected boolean isRequired(Value value) {
        return false;
    }

    protected boolean isLongColumn(Value value) {
        return false;
    }

    protected boolean validate(Item item) {
        return true;
    }

    public Catalog getCatalog() {
        return catalog;
    }

    protected boolean isVisible(Value value) {
        return true;
    }

    protected Component newFormGroup(String id, Catalog catalog, Value value, IModel<Item> model) {
        return null;
    }
}
