package ru.complitex.common.web.component.datatable;

import org.apache.wicket.Component;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.ISortStateLocator;
import org.apache.wicket.markup.html.border.Border;
import org.apache.wicket.markup.repeater.data.DataView;

public class EnhancedOrderByBorder extends Border {

    private EnhancedOrderByLink link;

    public EnhancedOrderByBorder(String id, String property, ISortStateLocator stateLocator,
            EnhancedOrderByLink.ICssProvider cssProvider, DataView<?> dataView, Component refreshComponent) {
        super(id);
        link = new EnhancedOrderByLink("orderByLink", property, stateLocator,
                EnhancedOrderByLink.VoidCssProvider.getInstance(), dataView, refreshComponent) {

            private static final long serialVersionUID = 1L;

            @Override
            protected void onSortChanged() {
                super.onSortChanged();
                EnhancedOrderByBorder.this.onSortChanged();
            }
        };
        addToBorder(link);
        add(new EnhancedOrderByLink.CssModifier(link, cssProvider));
    }

    public EnhancedOrderByLink getLink() {
        return link;
    }

    /**
     * This method is a hook for subclasses to perform an action after sort has changed
     */
    protected void onSortChanged() {
        // noop
    }

    public EnhancedOrderByBorder(String id, String property, ISortStateLocator stateLocator, DataView<?> dataView, Component refreshComponent) {
        this(id, property, stateLocator, EnhancedOrderByLink.DefaultCssProvider.getInstance(), dataView, refreshComponent);
    }
}
