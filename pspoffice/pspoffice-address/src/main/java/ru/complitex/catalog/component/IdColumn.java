package ru.complitex.catalog.component;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import ru.complitex.catalog.entity.Item;
import ru.complitex.ui.component.form.TextInput;
import ru.complitex.ui.component.table.Column;
import ru.complitex.ui.component.table.Table;

/**
 * @author Ivanov Anatoliy
 */
public class IdColumn extends Column<Item> {
    public IdColumn() {
        super(Model.of("№"));
    }

    @Override
    public void populate(WebMarkupContainer cell, String id, IModel<Item> model) {
        cell.add(new Label(id, model.getObject().getId()));
    }

    @Override
    public Component newFilter(String id, Table<Item> table) {
        return new TextInput<Long>(id, new PropertyModel<>(table.getFilter(), "map.id"))
                .onChange(table::update);
    }
}
