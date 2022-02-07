package ru.complitex.address.strategy.district;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.util.string.Strings;
import ru.complitex.address.resource.CommonResources;
import ru.complitex.common.entity.*;
import ru.complitex.common.entity.*;
import ru.complitex.common.strategy.IStrategy;
import ru.complitex.common.strategy.StrategyFactory;
import ru.complitex.common.util.ResourceUtil;
import ru.complitex.common.web.component.DomainObjectInputPanel;
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
import java.util.Set;

@Stateless
public class DistrictStrategy extends TemplateStrategy {
    private static final String DISTRICT_NS = DistrictStrategy.class.getName();

    @EJB
    private StrategyFactory strategyFactory;

    public static final long NAME = 600;
    public static final long CODE = 601;
    public static final long PARENT_ENTITY_ID = 400L;

    public String getName(DomainObject object){
        return object.getStringValue(NAME);
    }

    @Override
    public List<Long> getColumnAttributeTypeIds() {
        return Lists.newArrayList(NAME);
    }

    @Override
    public String getEntityName() {
        return "district";
    }

    @Override
    public String displayDomainObject(DomainObject object, Locale locale) {
        return object.getStringValue(NAME, locale);
    }

    public String displayDomainObject(Long objectId, Locale locale) {
        if (objectId == null){
            return null;
        }

        DomainObject object = getDomainObject(objectId, true);

        return object.getStringValue(NAME, locale);
    }

    @Override
    public ISearchCallback getSearchCallback() {
        return new ISearchCallback(){
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
        Long cityId = ids.get("city");
        if (cityId != null && cityId > 0) {
            example.setParentId(cityId);
            example.setParentEntity("city");
        } else {
            example.setParentId(null);
            example.setParentEntity(null);
        }
    }

    @Override
    public List<String> getSearchFilters() {
        return ImmutableList.of("country", "region", "city");
    }

    @Override
    public ISearchCallback getParentSearchCallback() {
        return new ParentSearchCallback();
    }

    private static class ParentSearchCallback implements ISearchCallback, Serializable {

        @Override
        public void found(Component component, Map<String, Long> ids, AjaxRequestTarget target) {
            DomainObjectInputPanel inputPanel = component.findParent(DomainObjectInputPanel.class);
            Long cityId = ids.get("city");
            if (cityId != null && cityId > 0) {
                inputPanel.getObject().setParentId(cityId);
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
        return null;
    }

    @Override
    public String[] getLogicalChildren() {
        return new String[]{"street"};
    }

    @Override
    public String[] getParents() {
        return new String[]{"city"};
    }

    public String getDistrictCode(long districtId) {
        return getDomainObject(districtId, true).getStringValue(CODE);
    }


    @Override
    protected List<PermissionInfo> findChildrenPermissionInfo(Long parentId, String childEntity, int start, int size) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("parentId", parentId);
        params.put("start", start);
        params.put("size", size);
        return sqlSession().selectList(DISTRICT_NS + ".selectChildrenPermissionInfo", params);
    }

    @Override
    public String[] getEditRoles() {
        return new String[]{SecurityRole.ADDRESS_MODULE_EDIT};
    }


    protected Set<Long> findChildrenActivityInfo(long districtId) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("districtId", districtId);
        List<Long> results = sqlSession().selectList(DISTRICT_NS + ".selectChildrenActivityInfo", params);
        return Sets.newHashSet(results);
    }


    protected void updateChildrenActivity(Set<Long> streetIds, boolean enabled) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("enabled", enabled);
        params.put("streetIds", streetIds);
        params.put("status", enabled ? Status.INACTIVE : Status.ACTIVE);
        sqlSession().update(DISTRICT_NS + ".updateChildrenActivity", params);
    }

    @Override
    public void changeChildrenActivity(Long parentId, boolean enable) {
        IStrategy streetStrategy = strategyFactory.getStrategy("street");

        Set<Long> streetIds = findChildrenActivityInfo(parentId);
        if (!streetIds.isEmpty()) {
            for (long childId : streetIds) {
                streetStrategy.changeChildrenActivity(childId, enable);
            }
            updateChildrenActivity(streetIds, !enable);
        }
    }

    @Override
    public String[] getListRoles() {
        return new String[]{SecurityRole.ADDRESS_MODULE_VIEW};
    }

    @Override
    public DomainObject newInstance() {
        DomainObject object =  super.newInstance();

        object.setParentEntityId(PARENT_ENTITY_ID);

        return object;
    }
}
