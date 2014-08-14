/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.document_type.strategy;

import com.google.common.collect.ImmutableList;
import java.util.Collection;
import org.complitex.dictionary.entity.StringCulture;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import static com.google.common.collect.ImmutableSet.*;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import org.complitex.dictionary.entity.DomainObject;
import org.complitex.dictionary.entity.example.AttributeExample;
import org.complitex.dictionary.entity.example.DomainObjectExample;
import org.complitex.dictionary.mybatis.Transactional;
import org.complitex.dictionary.service.LocaleBean;
import org.complitex.dictionary.strategy.DeleteException;
import org.complitex.template.strategy.TemplateStrategy;
import org.complitex.template.web.security.SecurityRole;
import static com.google.common.collect.Lists.*;
import static org.complitex.dictionary.util.AttributeUtil.*;
import static org.complitex.dictionary.util.ResourceUtil.*;
import static org.apache.wicket.util.string.Strings.*;

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
    private static final Set<Long> RESERVED_INSTANCE_IDS = of(PASSPORT, BIRTH_CERTIFICATE);
    @EJB
    private LocaleBean localeBean;

    @Override
    public String getEntityTable() {
        return "document_type";
    }

    @Override
    protected List<Long> getListAttributeTypes() {
        return newArrayList(NAME);
    }

    @Override
    public String displayDomainObject(DomainObject object, Locale locale) {
        return getStringCultureValue(object, NAME, locale);
    }

    @Override
    public void configureExample(DomainObjectExample example, Map<String, Long> ids, String searchTextInput) {
        if (isEmpty(searchTextInput)) {
            AttributeExample attrExample = example.getAttributeExample(NAME);
            if (attrExample == null) {
                attrExample = new AttributeExample(NAME);
                example.addAttributeExample(attrExample);
            }
            attrExample.setValue(searchTextInput);
        }
    }

    @Override
    public String getPluralEntityLabel(Locale locale) {
        return getString(RESOURCE_BUNDLE, getEntityTable(), locale);
    }

    @Transactional
    public List<DomainObject> getAll(Locale sortLocale) {
        DomainObjectExample example = new DomainObjectExample();
        if (sortLocale != null) {
            example.setLocaleId(localeBean.convert(sortLocale).getId());
            example.setOrderByAttributeTypeId(NAME);
            example.setAsc(true);
        }
        configureExample(example, ImmutableMap.<String, Long>of(), null);
        return (List<DomainObject>) find(example);
    }

    @Transactional
    public List<DomainObject> getKidDocumentTypes() {
        return newArrayList(Iterables.filter(getAll(null), new Predicate<DomainObject>() {

            @Override
            public boolean apply(DomainObject documentType) {
                return isKidDocumentType(documentType.getId());
            }
        }));
    }

    @Transactional
    public List<DomainObject> getAdultDocumentTypes() {
        return newArrayList(Iterables.filter(getAll(null), new Predicate<DomainObject>() {

            @Override
            public boolean apply(DomainObject documentType) {
                return isAdultDocumentType(documentType.getId());
            }
        }));
    }

    @Override
    public String[] getEditRoles() {
        return new String[]{SecurityRole.REFERENCE_DATA_MODULE_EDIT};
    }

    @Override
    public String[] getListRoles() {
        return new String[]{SecurityRole.REFERENCE_DATA_MODULE_VIEW};
    }

    @Transactional
    @Override
    protected void deleteChecks(long objectId, Locale locale) throws DeleteException {
        if (RESERVED_INSTANCE_IDS.contains(objectId)) {
            throw new DeleteException(getString(RESOURCE_BUNDLE, "delete_reserved_instance_error", locale));
        }
        super.deleteChecks(objectId, locale);
    }

    public static boolean isKidDocumentType(long documentTypeId) {
        if (RESERVED_INSTANCE_IDS.contains(documentTypeId)) {
            return documentTypeId == BIRTH_CERTIFICATE;
        } else {
            return true;
        }
    }

    public static boolean isAdultDocumentType(long documentTypeId) {
        return true;
    }

    public Collection<StringCulture> reservedNames() {
        final Collection<StringCulture> reservedNames = newArrayList();

        for (long id : RESERVED_INSTANCE_IDS) {
            final DomainObject o = findById(id, true);
            if (o != null) {
                reservedNames.addAll(ImmutableList.copyOf(o.getAttribute(NAME).getLocalizedValues()));
            }
        }

        return ImmutableList.copyOf(reservedNames);
    }
}
