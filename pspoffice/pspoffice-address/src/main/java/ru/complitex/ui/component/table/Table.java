package ru.complitex.ui.component.table;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.IModel;
import ru.complitex.ui.entity.Filter;

import java.io.Serializable;
import java.util.List;

/**
 * @author Ivanov Anatoliy
 */
public class Table<T extends Serializable> extends Panel {
    private final Provider<T> provider;

    private final WebMarkupContainer body;
    private final WebMarkupContainer footer;

    public Table(String id, List<Column<T>> columns, Provider<T> provider) {
        super(id);

        this.provider = provider;

        setOutputMarkupId(true);

        RepeatingView headers = new RepeatingView("headers");
        add(headers);

        columns.forEach(c -> headers.add(c.newHeader(headers.newChildId(), this)));

        RepeatingView filters = new RepeatingView("filters");
        add(filters);

        columns.forEach(c -> filters.add(c.newFilter(filters.newChildId(), this)));

        body = new WebMarkupContainer("body");
        body.setOutputMarkupId(true);
        add(body);

        DataView<T> items = new DataView<>("items", provider) {
            @Override
            protected void populateItem(Item<T> item) {
                RepeatingView repeatingView = new RepeatingView("data");
                item.add(repeatingView);

                columns.forEach(c -> c.populate(repeatingView, repeatingView.newChildId(), item.getModel()));

                item.add(new AjaxEventBehavior("click") {
                    @Override
                    protected void onEvent(AjaxRequestTarget target) {
                        Table.this.onClick(item.getModel(), target);
                    }
                });
            }
        };
        items.setOutputMarkupId(true);
        items.setItemsPerPage(10);
        body.add(items);

        footer = new WebMarkupContainer("footer");
        footer.setOutputMarkupId(true);
        footer.add(AttributeModifier.replace("colspan", () -> String.valueOf(columns.size())));
        add(footer);

        footer.add(new Paging("paging", items, id){
            @Override
            protected void onChange(AjaxRequestTarget target) {
                target.add(Table.this);
            }
        });
    }

    protected void onClick(IModel<T> itemModel, AjaxRequestTarget target) {}

    public Filter<T> getFilter() {
        return provider.getFilter();
    }

    public void update(AjaxRequestTarget target) {
        target.add(body, footer);
    }
}
