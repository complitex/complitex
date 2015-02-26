/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.military.strategy;

import com.google.common.collect.ImmutableMap;
import org.complitex.common.entity.AttributeFilter;
import org.complitex.common.entity.DomainObject;
import org.complitex.common.entity.DomainObjectFilter;
import org.complitex.common.strategy.StringLocaleBean;
import org.complitex.template.strategy.TemplateStrategy;
import org.complitex.template.web.security.SecurityRole;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static org.apache.wicket.util.string.Strings.isEmpty;
import static org.complitex.common.util.ResourceUtil.getString;

/**
 *
 * @author Artem
 */
@Stateless
public class MilitaryServiceRelationStrategy extends TemplateStrategy {

    private static final String RESOURCE_BUNDLE = MilitaryServiceRelationStrategy.class.getName();
    /**
     * Attribute type ids
     */
    public static final long NAME = 2900;
    public static final long CODE = 2901;

    @EJB
    private StringLocaleBean stringLocaleBean;

    @Override
    public String getEntityTable() {
        return "military_service_relation";
    }

    @Override
    public List<Long> getListAttributeTypes() {
        return newArrayList(NAME, CODE);
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
    public String getPluralEntityLabel(Locale locale) {
        return getString(RESOURCE_BUNDLE, getEntityTable(), locale);
    }

    @Override
    public String[] getEditRoles() {
        return new String[]{SecurityRole.REFERENCE_DATA_MODULE_EDIT};
    }

    @Override
    public String[] getListRoles() {
        return new String[]{SecurityRole.REFERENCE_DATA_MODULE_VIEW};
    }
}
