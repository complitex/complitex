package org.complitex.common.web.component;

import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;
import org.complitex.common.entity.AttributeFilter;

import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 14.08.2010 23:05:11
 */
public class AttributeFiltersPanel extends Panel {
    public AttributeFiltersPanel(String id, List<AttributeFilter> attributeFilters) {
        super(id);

        ListView filters = new ListView<AttributeFilter>("filters", attributeFilters){
            @Override
            protected void populateItem(ListItem<AttributeFilter> item) {
                item.add(new TextField<String>("filter", new PropertyModel<String>(item.getModel(), "value")));
            }
        };

        add(filters);
    }
}
