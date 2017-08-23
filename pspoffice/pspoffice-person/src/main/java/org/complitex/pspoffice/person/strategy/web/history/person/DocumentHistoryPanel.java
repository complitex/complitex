/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.strategy.web.history.person;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.complitex.common.entity.Attribute;
import org.complitex.common.entity.EntityAttribute;
import org.complitex.common.web.component.DomainObjectComponentUtil;
import org.complitex.common.web.component.css.CssAttributeBehavior;
import org.complitex.pspoffice.document.strategy.DocumentStrategy;
import org.complitex.pspoffice.document.strategy.entity.Document;
import org.complitex.pspoffice.person.strategy.entity.DocumentModification;

import javax.ejb.EJB;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

/**
 *
 * @author Artem
 */
final class DocumentHistoryPanel extends Panel {

    @EJB
    private DocumentStrategy documentStrategy;

    DocumentHistoryPanel(String id, final Document document, final DocumentModification modification) {
        super(id);

        //simple attributes
        List<Attribute> simpleAttributes = newArrayList();
        for (Attribute attribute : document.getAttributes()) {
            if (documentStrategy.isSimpleAttribute(attribute)) {
                simpleAttributes.add(attribute);
            }
        }

        add(new ListView<Attribute>("attributes", simpleAttributes) {

            @Override
            protected void populateItem(ListItem<Attribute> item) {
                Attribute attr = item.getModelObject();
                final EntityAttribute entityAttribute = documentStrategy.getEntity().getAttribute(attr.getAttributeTypeId());
                item.add(new Label("label", DomainObjectComponentUtil.labelModel(entityAttribute.getNames(), getLocale())));
                WebMarkupContainer required = new WebMarkupContainer("required");
                item.add(required);
                required.setVisible(entityAttribute.isMandatory());

                Component input = DomainObjectComponentUtil.newInputComponent(documentStrategy.getEntityName(), null, document, attr, getLocale(), true);
                input.add(new CssAttributeBehavior(modification.getAttributeModificationType(attr.getAttributeTypeId()).getCssClass()));
                item.add(input);
            }
        });

    }
}
