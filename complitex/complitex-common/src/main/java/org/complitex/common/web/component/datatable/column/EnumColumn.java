package org.complitex.common.web.component.datatable.column;

import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.ChoiceFilteredPropertyColumn;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.util.WildcardListModel;
import org.complitex.common.web.component.form.EnumChoiceRenderer;

import java.util.Arrays;
import java.util.Locale;

/**
 * @author Anatoly Ivanov
 *         Date: 21.07.2014 19:52
 */
public class EnumColumn<T, Y extends Enum<Y>> extends ChoiceFilteredPropertyColumn<T, Y, String> {
    private EnumChoiceRenderer<Y> enumChoiceRenderer;

    private String titleProperty;

    public EnumColumn(IModel<String> displayModel, String propertyExpression, Class<? extends Y> enumClass, Locale locale) {
        super(displayModel, propertyExpression, new WildcardListModel<Y>(Arrays.asList(enumClass.getEnumConstants())));

        enumChoiceRenderer = new EnumChoiceRenderer<>(locale);
    }

    public EnumColumn(String id, Class<? extends Y> enumClass, Locale locale) {
        this(new ResourceModel(id), id, enumClass, locale);
    }

    public EnumColumn(String id, Class<? extends Y> enumClass, String titleProperty, Locale locale) {
        this(new ResourceModel(id), id, enumClass, locale);

        this.titleProperty = titleProperty;
    }

    @Override
    public void populateItem(Item<ICellPopulator<T>> item, String componentId, IModel<T> rowModel) {
        item.add(new Label(componentId, getDataModel(rowModel))
                .add(new AttributeAppender("title", new PropertyModel<>(rowModel, titleProperty))));
    }

    @Override
    protected IChoiceRenderer<Y> getChoiceRenderer() {
        return enumChoiceRenderer;
    }

    @Override
    public IModel<Object> getDataModel(final IModel<T> rowModel) {
        return new IModel<Object>() {
            @Override
            public Object getObject() {
                return enumChoiceRenderer.getDisplayValue(new PropertyModel<Y>(rowModel, getPropertyExpression()).getObject());
            }

            @Override
            public void setObject(Object object) {
            }

            @Override
            public void detach() {
            }
        };
    }

    @Override
    protected boolean enableAutoSubmit() {
        return false;
    }
}
