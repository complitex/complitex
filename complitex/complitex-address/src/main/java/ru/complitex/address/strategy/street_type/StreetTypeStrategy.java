package ru.complitex.address.strategy.street_type;

import ru.complitex.address.resource.CommonResources;
import ru.complitex.common.entity.AttributeFilter;
import ru.complitex.common.entity.DomainObject;
import ru.complitex.common.entity.DomainObjectFilter;
import ru.complitex.common.util.ResourceUtil;
import ru.complitex.template.strategy.TemplateStrategy;
import ru.complitex.template.web.security.SecurityRole;

import javax.ejb.Stateless;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static org.apache.wicket.util.string.Strings.isEmpty;

@Stateless
public class StreetTypeStrategy extends TemplateStrategy {
    public static final long SHORT_NAME = 1400;

    public static final long NAME = 1401;

    @Override
    public String getEntityName() {
        return "street_type";
    }

    @Override
    public List<Long> getColumnAttributeTypeIds() {
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
    public void configureFilter(DomainObjectFilter filter, Map<String, Long> ids, String searchTextInput) {
        if (!isEmpty(searchTextInput)) {
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
        return ResourceUtil.getString(CommonResources.class.getName(), getEntityName(), locale);
    }

    @Override
    public String[] getEditRoles() {
        return new String[]{SecurityRole.ADDRESS_MODULE_EDIT};
    }

    @Override
    public Long getDefaultOrderByAttributeId() {
        return NAME;
    }

    @Override
    public String[] getListRoles() {
        return new String[]{SecurityRole.ADDRESS_MODULE_VIEW};
    }
}
