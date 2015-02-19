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
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.complitex.common.strategy.StrategyFactory;
import org.complitex.common.util.EjbBeanLocator;

import java.util.Locale;

/**
 * @author inheaven on 019 19.02.15 19:17
 */
public class DomainObjectFilteredColumn<T> extends AbstractColumn<T, String> implements IFilteredColumn<T, String> {
    private String entityTable;
    private String propertyExpression;
    private Locale locale;

    public DomainObjectFilteredColumn(String entityTable, String propertyExpression, Locale locale) {
        super(new ResourceModel(propertyExpression));

        this.entityTable = entityTable;
        this.propertyExpression = propertyExpression;
        this.locale = locale;
    }

    @Override
    public Component getFilter(String componentId, FilterForm<?> form) {
        return new TextFilter<>(componentId, Model.of(""), form);
    }

    @Override
    public void populateItem(Item<ICellPopulator<T>> cellItem, String componentId, IModel<T> rowModel) {
        String name = "";

        Long objectId = new PropertyModel<Long>(rowModel, propertyExpression).getObject();

        if (objectId != null){
            name = EjbBeanLocator.getBean(StrategyFactory.class).getStrategy(entityTable).displayDomainObject(objectId, locale);
        }

        cellItem.add(new Label(componentId, Model.of(name)));
    }
}