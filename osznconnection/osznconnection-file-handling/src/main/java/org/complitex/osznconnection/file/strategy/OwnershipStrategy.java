package org.complitex.osznconnection.file.strategy;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import org.apache.wicket.util.string.Strings;
import org.complitex.common.entity.AttributeFilter;
import org.complitex.common.entity.DomainObject;
import org.complitex.common.entity.DomainObjectFilter;
import org.complitex.common.entity.IEntityName;
import org.complitex.common.strategy.StringValueBean;
import org.complitex.common.util.ResourceUtil;
import org.complitex.template.strategy.TemplateStrategy;
import org.complitex.template.web.security.SecurityRole;

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
public class OwnershipStrategy extends TemplateStrategy {

    private static final String RESOURCE_BUNDLE = OwnershipStrategy.class.getName();
    /**
     * Attribute type ids
     */
    public static final long NAME = 1100;

    public static IEntityName OWNERSHIP_ENTITY = new IEntityName() {
        @Override
        public String getEntityName() {
            return "ownership";
        }
    };

    @EJB
    private StringValueBean stringBean;

    @Override
    public String getEntityName() {
        return "ownership";
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
        example.setOrderByAttributeTypeId(NAME);
        configureFilter(example, ImmutableMap.of(), null);

        return  getList(example);
    }

    @Override
    public String[] getEditRoles() {
        return new String[]{SecurityRole.OWNERSHIP_MODULE_EDIT};
    }
}
