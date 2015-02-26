package org.complitex.common.web.component.domain;

import org.apache.wicket.Component;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.IFilteredColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.TextFilter;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.complitex.common.entity.AttributeType;
import org.complitex.common.entity.DomainObject;

import java.util.Locale;

/**
 * @author inheaven on 14.12.2014 21:39.
 */
public class DomainFilteredColumn extends AbstractColumn<DomainObject, Long> implements IFilteredColumn<DomainObject, Long> {
    private String entityTable;
    private AttributeType attributeType;

    public DomainFilteredColumn(String entityTable, AttributeType attributeType, Locale locale) {
        super(Model.of("Hello!"));

        this.entityTable = entityTable;
        this.attributeType = attributeType;
    }

    @Override
    public Component getFilter(String componentId, FilterForm<?> form) {
        return new TextFilter<>(componentId, new Model<>(), form);
    }

    @Override
    public void populateItem(Item<ICellPopulator<DomainObject>> cellItem, String componentId, IModel<DomainObject> rowModel) {

        cellItem.add(new Label(componentId, Model.of(rowModel.getObject().getObjectId())));
    }
}
