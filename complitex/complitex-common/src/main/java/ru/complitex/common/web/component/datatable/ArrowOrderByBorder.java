package ru.complitex.common.web.component.datatable;

import org.apache.wicket.Component;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.ISortStateLocator;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;

/**
 *
 * @author Artem
 */
public class ArrowOrderByBorder extends EnhancedOrderByBorder {

    private static final String UP = "&#8593";

    private static final String DOWN = "&#8595";

    public ArrowOrderByBorder(String id, final String property, final ISortStateLocator<String> stateLocator, DataView<?> dataView, Component refreshComponent) {
        super(id, property, stateLocator, dataView, refreshComponent);

        IModel<String> arrowModel = new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                if (stateLocator.getSortState().getPropertySortOrder(property) == SortOrder.DESCENDING) {
                    return DOWN;
                } else if (stateLocator.getSortState().getPropertySortOrder(property) == SortOrder.ASCENDING) {
                    return UP;
                }
                return null;
            }
        };
        EnhancedOrderByLink link = getLink();
        Label arrow = new Label("arrow", arrowModel);
        arrow.setEscapeModelStrings(false);
        link.add(arrow);
    }
}
