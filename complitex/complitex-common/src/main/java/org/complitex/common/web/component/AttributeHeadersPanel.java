package org.complitex.common.web.component;

import org.apache.wicket.Component;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.ISortStateLocator;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.util.string.Strings;
import org.complitex.common.entity.EntityAttribute;
import org.complitex.common.web.component.datatable.ArrowOrderByBorder;

import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 14.08.2010 21:59:20
 */
public class AttributeHeadersPanel extends Panel {

    public AttributeHeadersPanel(String id, List<EntityAttribute> entityAttributes,
            final ISortStateLocator stateLocator, final DataView dataView,
            final Component refreshComponent) {
        super(id);

        ListView<EntityAttribute> headers = new ListView<EntityAttribute>("headers", entityAttributes) {

            @Override
            protected void populateItem(ListItem<EntityAttribute> item) {
                EntityAttribute entityAttribute = item.getModelObject();

                ArrowOrderByBorder header = new ArrowOrderByBorder("header", String.valueOf(entityAttribute.getId()),
                        stateLocator, dataView, refreshComponent);
                item.add(header);

                header.add(new Label("header_name", Strings.capitalize(entityAttribute.getAttributeName(getLocale()))));
            }
        };

        add(headers);
    }
}
