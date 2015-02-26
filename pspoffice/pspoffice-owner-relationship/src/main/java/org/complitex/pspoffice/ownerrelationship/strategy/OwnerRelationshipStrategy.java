/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.ownerrelationship.strategy;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.complitex.common.entity.AttributeFilter;
import org.complitex.common.entity.DomainObject;
import org.complitex.common.entity.DomainObjectFilter;
import org.complitex.common.entity.StringCulture;
import org.complitex.common.exception.DeleteException;
import org.complitex.common.strategy.StringLocaleBean;
import org.complitex.template.strategy.TemplateStrategy;
import org.complitex.template.web.security.SecurityRole;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.*;

import static com.google.common.collect.ImmutableSet.of;
import static com.google.common.collect.Lists.newArrayList;
import static org.apache.wicket.util.string.Strings.isEmpty;
import static org.complitex.common.util.ResourceUtil.getString;

/**
 *
 * @author Artem
 */
@Stateless
public class OwnerRelationshipStrategy extends TemplateStrategy {

    private static final String RESOURCE_BUNDLE = OwnerRelationshipStrategy.class.getName();
    /**
     * Attribute type ids
     */
    public static final long NAME = 2200;
    /**
     * Owner relationship instance ids
     */
    public static final long SON = 1;
    public static final long DAUGHTER = 2;
    private static final Set<Long> RESERVED_INSTANCE_IDS = of(SON, DAUGHTER);

    @EJB
    private StringLocaleBean stringLocaleBean;

    @Override
    public String getEntityTable() {
        return "owner_relationship";
    }

    @Override
    public List<Long> getListAttributeTypes() {
        return newArrayList(NAME);
    }

    @Override
    public String displayDomainObject(DomainObject object, Locale locale) {
        return object.getStringValue(NAME, locale);
    }

    @Override
    public void configureExample(DomainObjectFilter example, Map<String, Long> ids, String searchTextInput) {
        if (!isEmpty(searchTextInput)) {
            AttributeFilter attrExample = example.getAttributeExample(NAME);
            if (attrExample == null) {
                attrExample = new AttributeFilter(NAME);
                example.addAttributeFilter(attrExample);
            }
            attrExample.setValue(searchTextInput);
        }
    }

    @Override
    public String getPluralEntityLabel(Locale locale) {
        return getString(RESOURCE_BUNDLE, getEntityTable(), locale);
    }


    public List<DomainObject> getAll(Locale sortLocale) {
        DomainObjectFilter example = new DomainObjectFilter();
        if (sortLocale != null) {
            example.setLocaleId(stringLocaleBean.convert(sortLocale).getId());
            example.setAsc(true);
            example.setOrderByAttributeTypeId(NAME);
        }
        configureExample(example, ImmutableMap.<String, Long>of(), null);
        return (List<DomainObject>) getList(example);
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
    protected void deleteChecks(long objectId, Locale locale) throws DeleteException {
        if (RESERVED_INSTANCE_IDS.contains(objectId)) {
            throw new DeleteException(getString(RESOURCE_BUNDLE, "delete_reserved_instance_error", locale));
        }
        super.deleteChecks(objectId, locale);
    }

    public Collection<StringCulture> reservedNames() {
        final Collection<StringCulture> reservedNames = newArrayList();

        for (long id : RESERVED_INSTANCE_IDS) {
            final DomainObject o = getDomainObject(id, true);
            if (o != null) {
                reservedNames.addAll(ImmutableList.copyOf(o.getAttribute(NAME).getStringCultures()));
            }
        }

        return ImmutableList.copyOf(reservedNames);
    }
}
