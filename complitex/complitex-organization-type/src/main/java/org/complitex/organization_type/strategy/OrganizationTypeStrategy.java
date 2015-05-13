/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.organization_type.strategy;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import org.apache.wicket.util.string.Strings;
import org.complitex.common.entity.AttributeFilter;
import org.complitex.common.entity.DomainObject;
import org.complitex.common.entity.DomainObjectFilter;
import org.complitex.common.exception.DeleteException;
import org.complitex.common.util.ResourceUtil;
import org.complitex.template.strategy.TemplateStrategy;
import org.complitex.template.web.security.SecurityRole;

import javax.ejb.Stateless;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Stateless
public class OrganizationTypeStrategy extends TemplateStrategy {

    private static final String RESOURCE_BUNDLE = OrganizationTypeStrategy.class.getName();
    /**
     * Attribute type ids
     */
    public static final long NAME = 2300;
    /**
     * Organization type ids
     */
    public static final long USER_ORGANIZATION_TYPE = 1;
    public static final long CALCULATION_CENTER_TYPE = 3;
    public static final long SERVICING_ORGANIZATION_TYPE = 4;

    @Override
    public String getEntityName() {
        return "organization_type";
    }

    @Override
    public List<Long> getColumnAttributeTypeIds() {
        return Lists.newArrayList(NAME);
    }

    @Override
    public String displayDomainObject(DomainObject object, Locale locale) {
        return object.getStringValue(NAME, locale);
    }

    @Override
    public void configureFilter(DomainObjectFilter filter, Map<String, Long> ids, String searchTextInput) {
        if (!Strings.isEmpty(searchTextInput)) {
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
        return ResourceUtil.getString(RESOURCE_BUNDLE, getEntityName(), locale);
    }


    public List<? extends DomainObject> getAll() {
        DomainObjectFilter example = new DomainObjectFilter();
        configureFilter(example, ImmutableMap.<String, Long>of(), null);

        return getList(example);
    }

    @Override
    public String[] getEditRoles() {
        return new String[]{SecurityRole.ORGANIZATION_MODULE_EDIT};
    }

    @Override
    public String[] getListRoles() {
        return new String[]{SecurityRole.ORGANIZATION_MODULE_EDIT};
    }

    protected Collection<Long> getReservedInstanceIds() {
        return ImmutableList.of(USER_ORGANIZATION_TYPE, SERVICING_ORGANIZATION_TYPE);
    }


    @Override
    protected void deleteChecks(Long objectId, Locale locale) throws DeleteException {
        if (getReservedInstanceIds().contains(objectId)) {
            throw new DeleteException(ResourceUtil.getString(RESOURCE_BUNDLE, "delete_reserved_instance_error", locale));
        }
        super.deleteChecks(objectId, locale);
    }
}
