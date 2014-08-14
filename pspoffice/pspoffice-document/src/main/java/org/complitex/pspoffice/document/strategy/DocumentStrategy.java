/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.document.strategy;

import static com.google.common.collect.Lists.*;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import org.complitex.dictionary.entity.Attribute;
import org.complitex.dictionary.entity.DomainObject;
import org.complitex.dictionary.entity.description.EntityAttributeType;
import org.complitex.dictionary.entity.description.EntityAttributeValueType;
import org.complitex.dictionary.mybatis.Transactional;
import org.complitex.dictionary.service.StringCultureBean;
import org.complitex.pspoffice.document.strategy.entity.Document;
import org.complitex.template.strategy.TemplateStrategy;
import org.complitex.template.web.security.SecurityRole;

/**
 *
 * @author Artem
 */
@Stateless
public class DocumentStrategy extends TemplateStrategy {

    /**
     * Common attribute type ids
     */
    public static final long DOCUMENT_TYPE = 2800;
    public static final long DOCUMENT_SERIA = 2801;
    public static final long DOCUMENT_NUMBER = 2802;
    public static final long ORGANIZATION_ISSUED = 2803;
    public static final long DATE_ISSUED = 2804;
    @EJB
    private StringCultureBean stringBean;

    @Override
    public DomainObject newInstance() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Transactional
    public void disable(Document document, Date endDate) {
        document.setEndDate(endDate);
        changeActivity(document, false);
    }

    @Transactional
    public Document findById(long id) {
        DomainObject object = super.findById(id, true);
        if (object != null) {
            Document doc = new Document(object);
            return doc;
        } else {
            return null;
        }
    }

    public Document newInstance(long documentTypeId) {
        Document document = new Document();
        Attribute documentTypeAttribute = new Attribute();
        documentTypeAttribute.setAttributeId(1L);
        documentTypeAttribute.setAttributeTypeId(DOCUMENT_TYPE);
        documentTypeAttribute.setValueId(documentTypeId);
        documentTypeAttribute.setValueTypeId(DOCUMENT_TYPE);
        document.addAttribute(documentTypeAttribute);
        fillAttributes(document);

        return document;
    }

    @Override
    protected void fillAttributes(DomainObject document) {
        List<Attribute> toAdd = newArrayList();

        for (EntityAttributeType attributeType : getEntity().getEntityAttributeTypes()) {
            if (document.getAttributes(attributeType.getId()).isEmpty()
                    && (attributeType.getEntityAttributeValueTypes().size() == 1)
                    && !attributeType.isObsolete()
                    && !attributeType.getId().equals(DOCUMENT_TYPE)) {
                Attribute attribute = new Attribute();
                EntityAttributeValueType attributeValueType = attributeType.getEntityAttributeValueTypes().get(0);
                attribute.setAttributeTypeId(attributeType.getId());
                attribute.setValueTypeId(attributeValueType.getId());
                attribute.setObjectId(document.getId());
                attribute.setAttributeId(1L);

                if (isSimpleAttributeType(attributeType)) {
                    attribute.setLocalizedValues(stringBean.newStringCultures());
                }
                toAdd.add(attribute);
            }
        }
        if (!toAdd.isEmpty()) {
            document.getAttributes().addAll(toAdd);
        }
    }

    @Override
    public String displayDomainObject(DomainObject object, Locale locale) {
        Document document = (Document) object;
        return document.getSeries() + " " + document.getNumber();
    }

    @Override
    public String getEntityTable() {
        return "document";
    }

    @Override
    public String[] getEditRoles() {
        return new String[]{SecurityRole.PERSON_MODULE_EDIT};
    }

    public Document getHistoryDocument(long documentId, Date date) {
        DomainObject historyObject = super.findHistoryObject(documentId, date);
        if (historyObject == null) {
            return null;
        }
        Document document = new Document(historyObject);
        return document;
    }
}
