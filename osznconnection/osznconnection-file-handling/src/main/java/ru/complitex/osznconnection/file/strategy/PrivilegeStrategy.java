package ru.complitex.osznconnection.file.strategy;

import com.google.common.collect.Lists;
import org.apache.wicket.util.string.Strings;
import ru.complitex.common.entity.AttributeFilter;
import ru.complitex.common.entity.DomainObject;
import ru.complitex.common.entity.DomainObjectFilter;
import ru.complitex.common.entity.IEntityName;
import ru.complitex.common.strategy.StringValueBean;
import ru.complitex.common.util.ResourceUtil;
import ru.complitex.template.strategy.TemplateStrategy;
import ru.complitex.template.web.security.SecurityRole;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 *
 * @author Artem
 */
@Stateless
public class PrivilegeStrategy extends TemplateStrategy {

    private static final String RESOURCE_BUNDLE = PrivilegeStrategy.class.getName();
    /**
     * Attribute type ids
     */
    public static final long NAME = 1200;
    public static final long CODE = 1201;

    public static IEntityName PRIVILEGE_ENTITY = new IEntityName() {
        @Override
        public String getEntityName() {
            return "privilege";
        }
    };

    @EJB
    private StringValueBean stringBean;

    @Override
    public String getEntityName() {
        return "privilege";
    }

    @Override
    public List<Long> getColumnAttributeTypeIds() {
        return Lists.newArrayList(NAME, CODE);
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
    public int getSearchTextFieldSize() {
        return 40;
    }

    @Override
    public String getPluralEntityLabel(Locale locale) {
        return ResourceUtil.getString(RESOURCE_BUNDLE, getEntityName(), locale);
    }

    @Override
    public String[] getEditRoles() {
        return new String[]{SecurityRole.PRIVILEGE_MODULE_EDIT};
    }

    @Override
    protected void extendOrderBy(DomainObjectFilter example) {
        if (example.getOrderByAttributeTypeId() != null
                && example.getOrderByAttributeTypeId().equals(CODE)) {
            example.setOrderByNumber(true);
        }
    }
}
