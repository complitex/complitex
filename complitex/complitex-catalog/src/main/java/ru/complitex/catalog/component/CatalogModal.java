package ru.complitex.catalog.component;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxButton;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.common.NotificationPanel;
import de.agilecoders.wicket.core.markup.html.bootstrap.dialog.Modal;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.complitex.catalog.entity.*;
import ru.complitex.catalog.model.DataModel;
import ru.complitex.catalog.service.CatalogService;
import ru.complitex.catalog.util.Dates;
import ru.complitex.ui.component.form.DateGroup;
import ru.complitex.ui.component.form.TextGroup;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * @author Ivanov Anatoliy
 */
public class CatalogModal extends Modal<Item> {
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Inject
    private CatalogService catalogService;

    private final WebMarkupContainer container;
    private final NotificationPanel notification;

    private final Catalog catalog;

    private final IModel<Item> model;

    private final LocalDate date = Dates.now();

    public CatalogModal(String markupId, Catalog catalog, List<Value> values) {
        super(markupId);

        this.catalog = catalog;

        model = Model.of(new Item());

        setBackdrop(Backdrop.FALSE);
        setCloseOnEscapeKey(false);
        size(Size.Large);
        setHeaderVisible(false);

        container = new WebMarkupContainer("container");
        container.setOutputMarkupId(true)
                .setOutputMarkupPlaceholderTag(true)
                .setVisible(true);
        add(container);

        notification = new NotificationPanel("notification");
        notification.showRenderedMessages(false).setOutputMarkupId(true);
        container.add(notification);

        ListView<Value> listView = new ListView<>("values", values) {

            @Override
            protected void populateItem(ListItem<Value> item) {
                Value value = item.getModelObject();

                Component group = newFormGroup("group",  value);

                if (group instanceof EmptyPanel) {
                    item.setVisible(false);
                }

                if (value.getType().getKeyId() == Type.REFERENCE || value.getType().getKeyId() == Type.TIMESTAMP) {
                    group.add(AttributeModifier.replace("class", "col-12"));
                }

                item.setRenderBodyOnly(true);

                item.add(group);
            }
        };
        listView.setReuseItems(true);
        container.add(listView);

        addButton(new BootstrapAjaxButton(Modal.BUTTON_MARKUP_ID, Buttons.Type.Outline_Primary) {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                CatalogModal.this.save(target);
            }

            @Override
            protected void onError(AjaxRequestTarget target) {
                target.add(container);
            }
        }.setLabel(new ResourceModel("save")).setSize(Buttons.Size.Small));

        addButton(new BootstrapAjaxLink<Void>(Modal.BUTTON_MARKUP_ID, Buttons.Type.Outline_Secondary) {
            @Override
            public void onClick(AjaxRequestTarget target) {
                CatalogModal.this.cancel(target);
            }
        }.setLabel(new ResourceModel("cancel")).setSize(Buttons.Size.Small));
    }

    protected boolean isRequired(Value value) {
        return false;
    }

    protected Component newFormGroup(String id, Value value) {
        IModel<String> labelModel = new ResourceModel(value.getResourceKey());

        boolean required  = isRequired(value);

        return switch (value.getType().getKeyId()) {
            case Type.NUMERIC -> new TextGroup<>(id, labelModel, new DataModel<>(model, value), BigDecimal.class).setRequired(required);
            case Type.TEXT -> new TextGroup<>(id, labelModel, new DataModel<>(model, value), String.class).setRequired(required);
            case Type.TIMESTAMP -> new DateGroup(id, labelModel, new DataModel<>(model, value)).setRequired(required);
            case Type.REFERENCE -> newReferenceGroup(id, model, value);
            default -> null;
        };
    }

    protected int getReferenceValueKeyId(Value value) {
        return value.getReferenceCatalog().getValues().stream()
                .filter(v -> v.getName().contains("NAME"))
                .findFirst()
                .map(KeyRelevance::getKeyId)
                .orElse(-1);
    }

    protected Component newReferenceGroup(String id, IModel<Item> model, Value value) {
        return new ItemGroup(id, new ResourceModel(value.getResourceKey()), value.getReferenceCatalog().getKeyId(),
                new DataModel<>(model, value), getReferenceValueKeyId(value), date)
                .setRequired(isRequired(value));
    }

    public void create(AjaxRequestTarget target) {
        edit(null, target);
    }

    public void edit(Long itemId, AjaxRequestTarget target) {
        Item item;

        if (itemId == null) {
            item = new Item(catalog);
        } else {
            item = catalogService.getItem(catalog.getKeyId(), itemId, date);
        }

        model.setObject(item);

        container.setVisible(true);
        target.add(container);

        show(target);
    }

    private void save(AjaxRequestTarget target){
        try {
            Item item = model.getObject();

            if (!CatalogModal.this.validate(item)){
                target.add(notification);

                return;
            }

            if (item.getId() == null) {
                catalogService.inserts(item, date);
            } else {
                catalogService.update(item, date);
            }

            onSave(target);

            close(target);

            getSession().success(getString("info_saved"));

            container.visitChildren(FormComponent.class, (c, v) -> ((FormComponent) c).clearInput());
        } catch (Exception e) {
            log.error("error save domain", e);

            getSession().error(getString("error_save") + e.getLocalizedMessage());

            target.add(notification);
        }
    }

    protected boolean validate(Item item){
        return true;
    }

    protected void onSave(AjaxRequestTarget target) {}

    public void cancel(AjaxRequestTarget target){
        close(target);

        container.visitChildren(FormComponent.class, (c, v) -> ((FormComponent) c).clearInput());
    }
}
