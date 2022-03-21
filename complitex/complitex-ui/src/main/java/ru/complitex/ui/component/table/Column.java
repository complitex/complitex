package ru.complitex.ui.component.table;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import ru.complitex.ui.component.form.TextInput;

import java.io.Serializable;

/**
 * @author Ivanov Anatoliy
 */
public class Column<T extends Serializable> implements Serializable {
    private IModel<String> headerModel;

    public Column() {
    }

    public Column(IModel<String> headerModel) {
        this.headerModel = headerModel;
    }

    public IModel<String> getHeaderModel() {
        return headerModel;
    }

    public void setHeaderModel(IModel<String> headerModel) {
        this.headerModel = headerModel;
    }

    public Component newHeader(String id, Table<T> table) {
        return new Label(id, headerModel);
    }

    public Component newFilter(String id, Table<T> table) {
        return new TextInput<>(id, Model.of(""));
    }

    protected IModel<? extends Serializable> getModel(IModel<T> model){
        return model;
    }

    public void populate(WebMarkupContainer cell, String id, IModel<T> model) {
        cell.add(new Label(id, getModel(model)));
    }
}
