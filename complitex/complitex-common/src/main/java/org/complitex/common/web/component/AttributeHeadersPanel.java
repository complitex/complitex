package org.complitex.common.web.component;

import org.apache.wicket.Component;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.ISortStateLocator;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.util.string.Strings;
import org.complitex.common.entity.AttributeType;
import org.complitex.common.web.component.datatable.ArrowOrderByBorder;

import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 14.08.2010 21:59:20
 */
public class AttributeHeadersPanel extends Panel {

    public AttributeHeadersPanel(String id, List<AttributeType> attributeTypes,
            final ISortStateLocator stateLocator, final DataView dataView,
            final Component refreshComponent) {
        super(id);

        ListView<AttributeType> headers = new ListView<AttributeType>("headers", attributeTypes) {

            @Override
            protected void populateItem(ListItem<AttributeType> item) {
                AttributeType attributeType = item.getModelObject();

                ArrowOrderByBorder header = new ArrowOrderByBorder("header", String.valueOf(attributeType.getId()),
                        stateLocator, dataView, refreshComponent);
                item.add(header);

                header.add(new Label("header_name", Strings.capitalize(attributeType.getAttributeName(getLocale()))));
            }
        };

        add(headers);
    }
}
