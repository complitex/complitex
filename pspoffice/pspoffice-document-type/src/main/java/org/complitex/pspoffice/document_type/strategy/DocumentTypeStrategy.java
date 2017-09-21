/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.document_type.strategy;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.complitex.common.entity.AttributeFilter;
import org.complitex.common.entity.DomainObject;
import org.complitex.common.entity.DomainObjectFilter;
import org.complitex.common.exception.DeleteException;
import org.complitex.common.strategy.StringLocaleBean;
import org.complitex.entity.StringValue;
import org.complitex.template.strategy.TemplateStrategy;
import org.complitex.template.web.security.SecurityRole;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.*;
import java.util.stream.Collectors;

import static com.google.common.collect.ImmutableSet.of;
import static com.google.common.collect.Lists.newArrayList;
import static org.apache.wicket.util.string.Strings.isEmpty;
import static org.complitex.common.util.ResourceUtil.getString;

/**
 *
 * @author Artem
 */
@Stateless
public class DocumentTypeStrategy extends TemplateStrategy {

    private static final String RESOURCE_BUNDLE = DocumentTypeStrategy.class.getName();
    /**
     * Attribute type ids
     */
    public static final long NAME = 2700;
    /**
     * Owner relationship instance ids
     */
    public static final long PASSPORT = 1;
    public static final long BIRTH_CERTIFICATE = 2;
    public static final Set<Long> RESERVED_INSTANCE_IDS = of(PASSPORT, BIRTH_CERTIFICATE);

    @EJB
    private StringLocaleBean stringLocaleBean;

    @Override
    public String getEntityName() {
        return "document_type";
    }

    @Override
    public List<Long> getColumnAttributeTypeIds() {
        return newArrayList(NAME);
    }

    @Override
    public String displayDomainObject(DomainObject object, Locale locale) {
        return object.getStringValue(NAME, locale);
    }

    @Override
    public void configureFilter(DomainObjectFilter filter, Map<String, Long> ids, String searchTextInput) {
        if (isEmpty(searchTextInput)) {
            AttributeFilter attrExample = filter.getAttributeExample(NAME);
            if (attrExample == null) {
                attrExample = new AttributeFilter(NAME);
                filter.addAttributeFilter(attrExample);
            }
            attrExample.setValue(searchTextInput);
        }
    }

    @Override
    public String getPluralEntityLabel(Locale locale) {
        return getString(RESOURCE_BUNDLE, getEntityName(), locale);
    }


    public List<DomainObject> getAll(Locale sortLocale) {
        DomainObjectFilter example = new DomainObjectFilter();
        if (sortLocale != null) {
            example.setLocaleId(stringLocaleBean.convert(sortLocale).getId());
            example.setOrderByAttributeTypeId(NAME);
            example.setAsc(true);
        }
        configureFilter(example, ImmutableMap.<String, Long>of(), null);
        return (List<DomainObject>) getList(example);
    }


    public List<DomainObject> getKidDocumentTypes() {
        return newArrayList(getAll(null).stream().filter(documentType -> isKidDocumentType(documentType.getObjectId())).collect(Collectors.toList()));
    }


    public List<DomainObject> getAdultDocumentTypes() {
        return newArrayList(getAll(null).stream().filter(documentType -> isAdultDocumentType(documentType.getObjectId())).collect(Collectors.toList()));
    }

    @Override
    public String[] getEditRoles() {
        return new String[]{SecurityRole.REFERENCE_DATA_MODULE_EDIT};
    }

    @Override
    public String[] getListRoles() {
        return new String[]{SecurityRole.REFERENCE_DATA_MODULE_VIEW};
    }


    @Override
    protected void deleteChecks(Long objectId, Locale locale) throws DeleteException {
        if (RESERVED_INSTANCE_IDS.contains(objectId)) {
            throw new DeleteException(getString(RESOURCE_BUNDLE, "delete_reserved_instance_error", locale));
        }
        super.deleteChecks(objectId, locale);
    }

    public boolean isKidDocumentType(long documentTypeId) {
        return !RESERVED_INSTANCE_IDS.contains(documentTypeId) || documentTypeId == BIRTH_CERTIFICATE;
    }

    public boolean isAdultDocumentType(long documentTypeId) {
        return true;
    }

    public Collection<StringValue> reservedNames() {
        final Collection<StringValue> reservedNames = newArrayList();

        for (long id : RESERVED_INSTANCE_IDS) {
            final DomainObject o = getDomainObject(id, true);
            if (o != null) {
                reservedNames.addAll(ImmutableList.copyOf(o.getAttribute(NAME).getStringValues()));
            }
        }

        return ImmutableList.copyOf(reservedNames);
    }
}
