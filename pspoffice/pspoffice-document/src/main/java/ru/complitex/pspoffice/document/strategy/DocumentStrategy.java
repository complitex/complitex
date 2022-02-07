/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.complitex.pspoffice.document.strategy;

import ru.complitex.common.entity.Attribute;
import ru.complitex.common.entity.DomainObject;
import ru.complitex.common.entity.DomainObjectFilter;
import ru.complitex.common.entity.EntityAttribute;
import ru.complitex.common.strategy.PermissionBean;
import ru.complitex.common.strategy.StringValueBean;
import ru.complitex.common.util.StringValueUtil;
import ru.complitex.pspoffice.document.strategy.entity.Document;
import ru.complitex.template.strategy.TemplateStrategy;
import ru.complitex.template.web.security.SecurityRole;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.*;

import static com.google.common.collect.Lists.newArrayList;

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
    public static final long DOCUMENT_SERIES = 2801;
    public static final long DOCUMENT_NUMBER = 2802;
    public static final long ORGANIZATION_ISSUED = 2803;
    public static final long DATE_ISSUED = 2804;
    @EJB
    private StringValueBean stringBean;

    @Override
    public DomainObject newInstance() {
        throw new UnsupportedOperationException("Not supported yet.");
    }


    public void disable(Document document, Date endDate) {
        document.setEndDate(endDate);
        changeActivity(document, false);
    }


    public Document findById(long id) {
        DomainObject object = super.getDomainObject(id, true);
        if (object != null) {
            return new Document(object);
        } else {
            return null;
        }
    }

    public Document newInstance(long documentTypeId) {
        Document document = new Document();

        Attribute documentTypeAttribute = new Attribute();
        documentTypeAttribute.setAttributeId(1L);
        documentTypeAttribute.setEntityAttributeId(DOCUMENT_TYPE);
        documentTypeAttribute.setValueId(documentTypeId);
        document.addAttribute(documentTypeAttribute);

        fillAttributes(null, document);

        document.setSubjectIds(new HashSet<>(Collections.singletonList(PermissionBean.VISIBLE_BY_ALL_PERMISSION_ID)));

        return document;
    }

    @Override
    protected void fillAttributes(String dataSource, DomainObject document) {
        List<Attribute> toAdd = newArrayList();

        for (EntityAttribute entityAttribute : getEntity().getAttributes()) {
            if (document.getAttributes(entityAttribute.getId()).isEmpty()
                    && !entityAttribute.isObsolete()
                    && !entityAttribute.getId().equals(DOCUMENT_TYPE)) {
                Attribute attribute = new Attribute();
                attribute.setEntityAttributeId(entityAttribute.getId());
                attribute.setObjectId(document.getObjectId());
                attribute.setAttributeId(1L);

                if (entityAttribute.getValueType().isSimple()) {
                    attribute.setStringValues(StringValueUtil.newStringValues());
                }
                toAdd.add(attribute);
            }
        }
        if (!toAdd.isEmpty()) {
            document.getAttributes().addAll(toAdd);
        }
    }

    @Override
    public void configureFilter(DomainObjectFilter filter, Map<String, Long> ids, String searchTextInput) {

    }

    @Override
    public String displayDomainObject(DomainObject object, Locale locale) {
        Document document = (Document) object;
        return document.getSeries() + " " + document.getNumber();
    }

    @Override
    public String getEntityName() {
        return "document";
    }

    @Override
    public String[] getEditRoles() {
        return new String[]{SecurityRole.PERSON_MODULE_EDIT};
    }

    public Document getHistoryDocument(long documentId, Date date) {
        DomainObject historyObject = super.getHistoryObject(documentId, date);
        if (historyObject == null) {
            return null;
        }
        return new Document(historyObject);
    }
}
