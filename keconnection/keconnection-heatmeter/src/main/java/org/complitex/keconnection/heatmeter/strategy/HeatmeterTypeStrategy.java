/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.keconnection.heatmeter.strategy;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
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
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Artem
 */
@Stateless
public class HeatmeterTypeStrategy extends TemplateStrategy {

    private static final String RESOURCE_BUNDLE = HeatmeterTypeStrategy.class.getName();
    /**
     * Attribute type ids
     */
    public static final long NAME = 3400;
    /**
     * Predefined heatmeter type ids
     */
    public static final long HEATING = 1;
    public static final long HEATING_AND_WATER = 2;
    /* Reserved heatmeter types */
    private static final Set<Long> RESERVED_HEATMETER_TYPE_IDS = ImmutableSet.of(HEATING, HEATING_AND_WATER);

    @Override
    public String getEntityTable() {
        return "heatmeter_type";
    }

    @Override
    protected List<Long> getListAttributeTypes() {
        return Lists.newArrayList(NAME);
    }

    @Override
    public String displayDomainObject(DomainObject object, Locale locale) {
        return object.getStringValue(NAME, locale);
    }

    @Override
    public void configureExample(DomainObjectFilter example, Map<String, Long> ids, String searchTextInput) {
        if (!Strings.isEmpty(searchTextInput)) {
            AttributeFilter attrExample = example.getAttributeExample(NAME);
            if (attrExample == null) {
                attrExample = new AttributeFilter(NAME);
                example.addAttributeExample(attrExample);
            }
            attrExample.setValue(searchTextInput);
        }
    }

    @Override
    public String getPluralEntityLabel(Locale locale) {
        return ResourceUtil.getString(RESOURCE_BUNDLE, getEntityTable(), locale);
    }

    public List<DomainObject> getAll() {
        DomainObjectFilter example = new DomainObjectFilter();
        configureExample(example, ImmutableMap.<String, Long>of(), null);
        return (List<DomainObject>) getList(example);
    }


    @Override
    protected void deleteChecks(long objectId, Locale locale) throws DeleteException {
        if (RESERVED_HEATMETER_TYPE_IDS.contains(objectId)) {
            throw new DeleteException(ResourceUtil.getString(RESOURCE_BUNDLE, "delete_reserved_instance_error", locale));
        }
        super.deleteChecks(objectId, locale);
    }

    @Override
    public String[] getEditRoles() {
        return new String[]{SecurityRole.ADMIN_MODULE_EDIT};
    }

    @Override
    public String[] getListRoles() {
        return new String[]{SecurityRole.ADMIN_MODULE_EDIT};
    }
}
