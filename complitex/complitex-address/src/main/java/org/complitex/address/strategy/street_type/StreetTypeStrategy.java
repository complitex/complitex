/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.address.strategy.street_type;

import org.complitex.address.resource.CommonResources;
import org.complitex.common.entity.AttributeFilter;
import org.complitex.common.entity.DomainObject;
import org.complitex.common.entity.DomainObjectFilter;
import org.complitex.common.strategy.StringCultureBean;
import org.complitex.common.util.ResourceUtil;
import org.complitex.template.strategy.TemplateStrategy;
import org.complitex.template.web.security.SecurityRole;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static org.apache.wicket.util.string.Strings.isEmpty;

/**
 *
 * @author Artem
 */
@Stateless
public class StreetTypeStrategy extends TemplateStrategy {

    @EJB
    private StringCultureBean stringBean;
    /*
     * Attribute type ids
     */
    public static final long SHORT_NAME = 1400;
    public static final long NAME = 1401;

    @Override
    public String getEntityTable() {
        return "street_type";
    }

    @Override
    public List<Long> getListAttributeTypes() {
        return newArrayList(NAME);
    }

    @Override
    public String displayDomainObject(DomainObject object, Locale locale) {
        return object.getStringValue(SHORT_NAME, locale).toLowerCase(locale) + ".";
    }

    public String getShortName(DomainObject object, Locale locale){
        return object.getStringValue(SHORT_NAME, locale);
    }

    public String getShortName(DomainObject object){
        return getShortName(object, getSystemLocale());
    }

    public String getName(DomainObject object, Locale locale){
        return object.getStringValue(NAME, locale);
    }

    public String getName(DomainObject object){
        return object.getStringValue(NAME);
    }

    @Deprecated
    public String displayFullName(DomainObject streetTypeObject, Locale locale) {
        return getName(streetTypeObject, locale);
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
        return ResourceUtil.getString(CommonResources.class.getName(), getEntityTable(), locale);
    }

    @Override
    public String[] getEditRoles() {
        return new String[]{SecurityRole.ADDRESS_MODULE_EDIT};
    }

    @Override
    public long getDefaultOrderByAttributeId() {
        return NAME;
    }

    @Override
    public String[] getListRoles() {
        return new String[]{SecurityRole.ADDRESS_MODULE_VIEW};
    }
}
