/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.strategy.web.history.person;

import static com.google.common.collect.Lists.*;
import java.util.List;

import org.complitex.dictionary.web.component.DomainObjectComponentUtil;
import org.complitex.dictionary.web.component.css.CssAttributeBehavior;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import javax.ejb.EJB;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.complitex.dictionary.entity.Attribute;
import org.complitex.dictionary.entity.description.EntityAttributeType;
import org.complitex.pspoffice.document.strategy.DocumentStrategy;
import org.complitex.pspoffice.document.strategy.entity.Document;
import org.complitex.pspoffice.person.strategy.entity.DocumentModification;

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
                final EntityAttributeType attributeType = documentStrategy.getEntity().getAttributeType(attr.getAttributeTypeId());
                item.add(new Label("label", DomainObjectComponentUtil.labelModel(attributeType.getAttributeNames(), getLocale())));
                WebMarkupContainer required = new WebMarkupContainer("required");
                item.add(required);
                required.setVisible(attributeType.isMandatory());

                Component input = DomainObjectComponentUtil.newInputComponent(documentStrategy.getEntityTable(), null, document, attr, getLocale(), true);
                input.add(new CssAttributeBehavior(modification.getAttributeModificationType(attr.getAttributeTypeId()).getCssClass()));
                item.add(input);
            }
        });

    }
}
