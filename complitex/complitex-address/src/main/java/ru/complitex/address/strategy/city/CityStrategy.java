package ru.complitex.address.strategy.city;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.util.string.Strings;
import ru.complitex.address.resource.CommonResources;
import ru.complitex.address.strategy.city.web.edit.CityTypeComponent;
import ru.complitex.common.entity.AttributeFilter;
import ru.complitex.common.entity.DomainObject;
import ru.complitex.common.entity.DomainObjectFilter;
import ru.complitex.common.strategy.IStrategy;
import ru.complitex.common.strategy.StrategyFactory;
import ru.complitex.common.util.ResourceUtil;
import ru.complitex.common.web.component.DomainObjectInputPanel;
import ru.complitex.common.web.component.domain.AbstractComplexAttributesPanel;
import ru.complitex.common.web.component.domain.DomainObjectListPanel;
import ru.complitex.common.web.component.search.ISearchCallback;
import ru.complitex.template.strategy.TemplateStrategy;
import ru.complitex.template.web.security.SecurityRole;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.io.Serializable;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 *
 * @author Artem
 */
@Stateless
public class CityStrategy extends TemplateStrategy {
    private static final String CITY_NS = CityStrategy.class.getName();

    @EJB
    private StrategyFactory strategyFactory;

    public static final long NAME = 400;
    public static final long CITY_TYPE = 401;
    public static final long PARENT_ENTITY_ID = 700L;

    @Override
    public List<Long> getColumnAttributeTypeIds() {
        return Lists.newArrayList(NAME);
    }

    @Override
    public String getEntityName() {
        return "city";
    }

    @Override
    public String displayDomainObject(DomainObject object, Locale locale) {
        if (object == null){
            return "";
        }

        String cityName = object.getStringValue(NAME, locale);

        Long cityTypeId = object.getAttribute(CITY_TYPE).getValueId();
        if (cityTypeId != null) {
            IStrategy cityTypeStrategy = strategyFactory.getStrategy("city_type");
            DomainObject cityType = cityTypeStrategy.getDomainObject(cityTypeId, true);

            if (cityType != null) {
                return cityTypeStrategy.displayDomainObject(cityType, locale) + " " + cityName;
            }
        }
        return cityName;
    }

    public String getName(DomainObject object){
        return getName(object, getSystemLocale());
    }

    public String getName(DomainObject object, Locale locale){
        return object.getStringValue(NAME, locale);
    }

    @Override
    public ISearchCallback getSearchCallback() {
        return new ISearchCallback() {
            @Override
            public void found(Component component, Map<String, Long> ids, AjaxRequestTarget target) {
                DomainObjectListPanel list = component.findParent(DomainObjectListPanel.class);
                configureExampleImpl(list.getFilter(), ids, null);
                list.refreshContent(target);
            }
        };
    }

    @Override
    public void configureFilter(DomainObjectFilter filter, Map<String, Long> ids, String searchTextInput) {
        configureExampleImpl(filter, ids, searchTextInput);
    }

    private void configureExampleImpl(DomainObjectFilter example, Map<String, Long> ids, String searchTextInput) {
        if (!Strings.isEmpty(searchTextInput)) {
            AttributeFilter attrExample = example.getAttributeExample(NAME);
            if (attrExample == null) {
                attrExample = new AttributeFilter(NAME);
                example.addAttributeFilter(attrExample);
            }
            attrExample.setValue(searchTextInput);
        }

        if (ids.containsKey("region")) {
            Long regionId = ids.get("region");
            if (regionId != null && regionId > 0) {
                example.setParentId(regionId);
                example.setParentEntity("region");
            } else {
                example.setParentId(null);
                example.setParentEntity(null);
            }
        }
    }

    @Override
    public List<String> getSearchFilters() {
        return ImmutableList.of("country", "region");
    }

    @Override
    public ISearchCallback getParentSearchCallback() {
        return new ParentSearchCallback();
    }

    private static class ParentSearchCallback implements ISearchCallback, Serializable {

        @Override
        public void found(Component component, Map<String, Long> ids, AjaxRequestTarget target) {
            DomainObjectInputPanel inputPanel = component.findParent(DomainObjectInputPanel.class);
            Long regionId = ids.get("region");
            if (regionId != null && regionId > 0) {
                inputPanel.getObject().setParentId(regionId);
                inputPanel.getObject().setParentEntityId(PARENT_ENTITY_ID);
            } else {
                inputPanel.getObject().setParentId(null);
                inputPanel.getObject().setParentEntityId(null);
            }
        }
    }

    @Override
    public String getPluralEntityLabel(Locale locale) {
        return ResourceUtil.getString(CommonResources.class.getName(), getEntityName(), locale);
    }

    @Override
    public String[] getRealChildren() {
        return new String[]{"district", "street"};
    }

    @Override
    public String[] getLogicalChildren() {
        return new String[]{"district"};
    }

    @Override
    public String[] getParents() {
        return new String[]{"region"};
    }

    @Override
    public Class<? extends AbstractComplexAttributesPanel> getComplexAttributesPanelAfterClass() {
        return CityTypeComponent.class;
    }

    @SuppressWarnings({"EjbClassBasicInspection"})
    public static Long getCityType(DomainObject cityObject) {
        return cityObject.getAttribute(CITY_TYPE).getValueId();
    }

    @Override
    public Long performDefaultValidation(DomainObject cityObject, Locale locale) {
        Map<String, Object> params = super.createValidationParams(cityObject, locale);
        Long cityTypeId = getCityType(cityObject);
        params.put("cityTypeId", cityTypeId);
        List<Long> results = sqlSession().selectList(CITY_NS + ".defaultValidation", params);
        for (Long result : results) {
            if (!result.equals(cityObject.getObjectId())) {
                return result;
            }
        }
        return null;
    }

    @Override
    public String[] getEditRoles() {
        return new String[]{SecurityRole.ADDRESS_MODULE_EDIT};
    }

    @Override
    public String[] getListRoles() {
        return new String[]{SecurityRole.ADDRESS_MODULE_VIEW};
    }

    @Override
    public boolean isUpperCase() {
        return true;
    }
}
