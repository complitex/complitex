package ru.complitex.keconnection.heatmeter.strategy;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import org.apache.wicket.util.string.Strings;
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
import java.util.Set;

/**
 *
 * @author Artem
 */
@Stateless
public class HeatmeterPeriodTypeStrategy extends TemplateStrategy {

    private static final String RESOURCE_BUNDLE = HeatmeterPeriodTypeStrategy.class.getName();
    /**
     * Attribute type ids
     */
    public static final long NAME = 3500;
    /**
     * Predefined heatmeter period type ids
     */
    public static final Integer OPERATING = 1;
    public static final Integer ADJUSTMENT = 2;
    /* Reserved heatmeter period types */
    private static final Set<Integer> RESERVED_HEATMETER_PERIOD_TYPE_IDS = ImmutableSet.of(OPERATING, ADJUSTMENT);

    @Override
    public String getEntityName() {
        return "heatmeter_period_type";
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

    public List<DomainObject> getAll() {
        DomainObjectFilter example = new DomainObjectFilter();
        configureFilter(example, ImmutableMap.<String, Long>of(), null);
        return (List<DomainObject>) getList(example);
    }


//    @Override
//    protected void deleteChecks(long objectId, Locale locale) throws DeleteException {
//        if (RESERVED_HEATMETER_PERIOD_TYPE_IDS.contains(objectId)) {
//            throw new DeleteException(ResourceUtil.getString(RESOURCE_BUNDLE, "delete_reserved_instance_error", locale));
//        }
//        super.deleteChecks(objectId, locale);
//    }

    @Override
    public String[] getEditRoles() {
        return new String[]{SecurityRole.ADMIN_MODULE_EDIT};
    }

    @Override
    public String[] getListRoles() {
        return new String[]{SecurityRole.ADMIN_MODULE_EDIT};
    }
}
