/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.organization_type.strategy;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import org.apache.wicket.util.string.Strings;
import org.complitex.common.entity.DomainObject;
import org.complitex.common.entity.example.AttributeExample;
import org.complitex.common.entity.example.DomainObjectExample;
import org.complitex.common.mybatis.Transactional;
import org.complitex.common.strategy.DeleteException;
import org.complitex.common.util.AttributeUtil;
import org.complitex.common.util.ResourceUtil;
import org.complitex.template.strategy.TemplateStrategy;
import org.complitex.template.web.security.SecurityRole;

import javax.ejb.Stateless;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 *
 * @author Artem
 */
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
    public static final long SERVICING_ORGANIZATION_TYPE = 4;

    @Override
    public String getEntityTable() {
        return "organization_type";
    }

    @Override
    protected List<Long> getListAttributeTypes() {
        return Lists.newArrayList(NAME);
    }

    @Override
    public String displayDomainObject(DomainObject object, Locale locale) {
        return AttributeUtil.getStringCultureValue(object, NAME, locale);
    }

    @Override
    public void configureExample(DomainObjectExample example, Map<String, Long> ids, String searchTextInput) {
        if (!Strings.isEmpty(searchTextInput)) {
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
        return ResourceUtil.getString(RESOURCE_BUNDLE, getEntityTable(), locale);
    }

    @Transactional
    public List<? extends DomainObject> getAll() {
        DomainObjectExample example = new DomainObjectExample();
        configureExample(example, ImmutableMap.<String, Long>of(), null);

        return find(example);
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

    @Transactional
    @Override
    protected void deleteChecks(long objectId, Locale locale) throws DeleteException {
        if (getReservedInstanceIds().contains(objectId)) {
            throw new DeleteException(ResourceUtil.getString(RESOURCE_BUNDLE, "delete_reserved_instance_error", locale));
        }
        super.deleteChecks(objectId, locale);
    }
}
