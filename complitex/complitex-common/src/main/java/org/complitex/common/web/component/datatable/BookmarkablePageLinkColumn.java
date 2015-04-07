package org.complitex.common.web.component.datatable;

import org.apache.wicket.Page;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.TextFilteredPropertyColumn;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.complitex.common.web.component.BookmarkablePageLinkPanel;

/**
 * @author inheaven on 007 07.04.15 17:22
 */
public abstract class BookmarkablePageLinkColumn<T> extends TextFilteredPropertyColumn<T, String, String> {
    private Class<? extends Page> page;

    public BookmarkablePageLinkColumn(String propertyExpression, Class<? extends Page> page) {
        super(new ResourceModel(propertyExpression), propertyExpression);

        this.page = page;
    }

    @Override
    public void populateItem(Item<ICellPopulator<T>> item, String componentId, IModel<T> rowModel) {
        item.add(new BookmarkablePageLinkPanel(componentId, new PropertyModel<>(rowModel, getPropertyExpression()),
                page, getPageParameters(rowModel)));
    }

    protected abstract PageParameters getPageParameters(IModel<T> rowModel);
}
