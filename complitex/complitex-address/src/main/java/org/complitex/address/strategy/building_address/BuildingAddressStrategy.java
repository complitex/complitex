package org.complitex.address.strategy.building_address;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.Strings;
import org.complitex.address.resource.CommonResources;
import org.complitex.address.strategy.building.BuildingStrategy;
import org.complitex.common.entity.*;
import org.complitex.common.util.Locales;
import org.complitex.common.util.ResourceUtil;
import org.complitex.common.web.component.DomainObjectInputPanel;
import org.complitex.common.web.component.search.ISearchCallback;
import org.complitex.template.strategy.TemplateStrategy;
import org.complitex.template.web.security.SecurityRole;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.io.Serializable;
import java.text.MessageFormat;
import java.util.*;

@Stateless
public class BuildingAddressStrategy extends TemplateStrategy {
    private static final String BUILDING_ADDRESS_NS = BuildingAddressStrategy.class.getName();

    public static final long NUMBER = 1500;
    public static final long CORP = 1501;
    public static final long STRUCTURE = 1502;
    public static final long PARENT_STREET_ENTITY_ID = 300L;
    /**
     * It indicates default sorting by combination of number, corp and structure
     */

    public static final long DEFAULT_ORDER_BY_ID = -1;

    public static final String DISTRICT_ID = "districtId";

    @EJB
    private BuildingStrategy buildingStrategy;

    @Override
    public String getEntityName() {
        return "building_address";
    }

    @Override
    public String displayDomainObject(DomainObject object, Locale locale) {
        return displayBuildingAddress(object.getStringValue(NUMBER), object.getStringValue(CORP),
                object.getStringValue(STRUCTURE), locale);
    }

    public String getName(DomainObject object){
        return displayDomainObject(object, Locales.getSystemLocale());
    }

    private String displayBuildingAddress(String number, String corp, String structure, Locale locale) {
        if (Strings.isEmpty(corp)) {
            if (Strings.isEmpty(structure)) {
                return number;
            } else {
                return MessageFormat.format(ResourceUtil.getString(BuildingStrategy.class.getName(),
                        "number_structure", locale), number, structure);
            }
        } else {
            if (Strings.isEmpty(structure)) {
                return MessageFormat.format(ResourceUtil.getString(BuildingStrategy.class.getName(),
                        "number_corp", locale), number, corp);
            } else {
                return MessageFormat.format(ResourceUtil.getString(BuildingStrategy.class.getName(),
                        "number_corp_structure", locale), number, corp, structure);
            }
        }
    }

    @Override
    public void configureFilter(DomainObjectFilter filter, Map<String, Long> ids, String searchTextInput) {
        if (!Strings.isEmpty(searchTextInput)) {
            AttributeFilter number = filter.getAttributeExample(NUMBER);
            if (number == null) {
                number = new AttributeFilter(NUMBER);
                filter.addAttributeFilter(number);
            }
            number.setValue(searchTextInput);
        }

        Long streetId = ids.get("street");
        if (streetId != null && streetId > 0) {
            filter.setParentId(streetId);
            filter.setParentEntity("street");
        } else {
            Long cityId = ids.get("city");
            if (cityId != null && cityId > 0) {
                filter.setParentId(cityId);
                filter.setParentEntity("city");
            } else {
                filter.setParentId(null);
                filter.setParentEntity(null);
            }
        }
    }

    @Override
    public List<? extends DomainObject> getList(DomainObjectFilter filter) {
        if (filter.getObjectId() != null && filter.getObjectId() <= 0) {
            return Collections.emptyList();
        }

        filter.setEntityName(getEntityName());
        prepareExampleForPermissionCheck(filter);

        List<DomainObject> objects = sqlSession().selectList(BUILDING_ADDRESS_NS + ".selectBuildingAddresses", filter);

        for (DomainObject object : objects) {
            loadAttributes(object);
            //load subject ids
            object.setSubjectIds(getSubjects(object.getPermissionId()));
        }
        return objects;
    }

    @Override
    public ISearchCallback getParentSearchCallback() {
        return new ParentSearchCallback();
    }

    @Override
    public List<String> getParentSearchFilters() {
        return ImmutableList.of("country", "region", "city", "street");
    }

    private static class ParentSearchCallback implements ISearchCallback, Serializable {

        @Override
        public void found(Component component, final Map<String, Long> ids, final AjaxRequestTarget target) {
            DomainObjectInputPanel inputPanel = component.findParent(DomainObjectInputPanel.class);

            assert inputPanel != null;
            DomainObject domainObject = inputPanel.getObject();

            Long streetId = ids.get("street");

            if (streetId != null && streetId > 0) {
                domainObject.setParentId(streetId);
                domainObject.setParentEntityId(PARENT_STREET_ENTITY_ID);
            } else {
                Long cityId = ids.get("city");
                if (cityId != null && cityId > 0) {
                    domainObject.setParentId(cityId);
                    domainObject.setParentEntityId(400L);
                } else {
                    domainObject.setParentId(null);
                    domainObject.setParentEntityId(null);
                }
            }
        }
    }

    @Override
    public String getPluralEntityLabel(Locale locale) {
        return ResourceUtil.getString(CommonResources.class.getName(), getEntityName(), locale);
    }

    @Override
    public String[] getRealChildren() {
        return new String[]{"building"};
    }

    @Override
    public Class<? extends WebPage> getEditPage() {
        return null;
    }

    @Override
    public PageParameters getEditPageParams(Long objectId, Long parentId, String parentEntity) {
        return null;
    }

    @Override
    public Class<? extends WebPage> getListPage() {
        return null;
    }

    @Override
    public PageParameters getListPageParams() {
        return null;
    }

    @Override
    public String[] getParents() {
        return new String[]{"street"};
    }

    @Override
    public Class<? extends WebPage> getHistoryPage() {
        return null;
    }

    @Override
    public PageParameters getHistoryPageParams(Long objectId) {
        return null;
    }

    @Override
    public String[] getEditRoles() {
        return new String[]{SecurityRole.ADDRESS_MODULE_EDIT};
    }


    private List<PermissionInfo> getBuildingPermissionInfoByParent(long buildingAddressId) {
        return sqlSession().selectList(BUILDING_ADDRESS_NS + ".selectBuildingPermissionInfoByParent", buildingAddressId);
    }


    private List<PermissionInfo> getBuildingPermissionInfoByReference(long buildingAddressId) {
        return sqlSession().selectList(BUILDING_ADDRESS_NS + ".selectBuildingPermissionInfoByReference", buildingAddressId);
    }


    private List<PermissionInfo> getReferenceAddressPermissionInfo(long buildingId) {
        return sqlSession().selectList(BUILDING_ADDRESS_NS + ".selectReferenceAddressPermissionInfo", buildingId);
    }


    private List<PermissionInfo> getParentAddressPermissionInfo(long buildingId) {
        return sqlSession().selectList(BUILDING_ADDRESS_NS + ".selectParentAddressPermissionInfo", buildingId);
    }


    private Set<Long> getBuildingActivityInfoByParent(long buildingAddressId) {
        List<Long> results = sqlSession().selectList(BUILDING_ADDRESS_NS + ".selectBuildingActivityInfoByParent", buildingAddressId);
        return Sets.newHashSet(results);
    }


    private Set<Long> getBuildingActivityInfoByReference(long buildingAddressId) {
        List<Long> results = sqlSession().selectList(BUILDING_ADDRESS_NS + ".selectBuildingActivityInfoByReference", buildingAddressId);
        return Sets.newHashSet(results);
    }


    private Set<Long> getReferenceAddressActivityInfo(long buildingId) {
        List<Long> results = sqlSession().selectList(BUILDING_ADDRESS_NS + ".selectReferenceAddressActivityInfo", buildingId);
        return Sets.newHashSet(results);
    }


    private Set<Long> getParentAddressActivityInfo(long buildingId) {
        List<Long> results = sqlSession().selectList(BUILDING_ADDRESS_NS + ".selectParentAddressActivityInfo", buildingId);
        return Sets.newHashSet(results);
    }


    @Override
    protected void replaceChildrenPermissions(Long parentId, Set<Long> subjectIds) {
        long buildingAddressId = parentId;

        List<PermissionInfo> buildingPermissionInfoByParent = getBuildingPermissionInfoByParent(buildingAddressId);
        for (PermissionInfo buildingPermissionInfo : buildingPermissionInfoByParent) {
            long buildingId = buildingPermissionInfo.getId();
            List<PermissionInfo> referenceAddressPermissionInfos = getReferenceAddressPermissionInfo(buildingId);
            for (PermissionInfo referenceAddressPermissionInfo : referenceAddressPermissionInfos) {
                replaceObjectPermissions(referenceAddressPermissionInfo, subjectIds);
            }
            buildingStrategy.replacePermissions(buildingPermissionInfo, subjectIds);
        }

        List<PermissionInfo> buildingPermissionInfoByReference = getBuildingPermissionInfoByReference(buildingAddressId);
        for (PermissionInfo buildingPermissionInfo : buildingPermissionInfoByReference) {
            long buildingId = buildingPermissionInfo.getId();
            List<PermissionInfo> parentAddressPermissionInfos = getParentAddressPermissionInfo(buildingId);
            for (PermissionInfo parentAddressPermissionInfo : parentAddressPermissionInfos) {
                replaceObjectPermissions(parentAddressPermissionInfo, subjectIds);
            }
            buildingStrategy.replacePermissions(buildingPermissionInfo, subjectIds);
        }
    }


    @Override
    protected void changeChildrenPermissions(Long parentId, Set<Long> addSubjectIds, Set<Long> removeSubjectIds) {
        long buildingAddressId = parentId;

        List<PermissionInfo> buildingPermissionInfoByParent = getBuildingPermissionInfoByParent(buildingAddressId);
        for (PermissionInfo buildingPermissionInfo : buildingPermissionInfoByParent) {
            long buildingId = buildingPermissionInfo.getId();
            List<PermissionInfo> referenceAddressPermissionInfos = getReferenceAddressPermissionInfo(buildingId);
            for (PermissionInfo referenceAddressPermissionInfo : referenceAddressPermissionInfos) {
                changeObjectPermissions(referenceAddressPermissionInfo, addSubjectIds, removeSubjectIds);
            }
            buildingStrategy.changePermissions(buildingPermissionInfo, addSubjectIds, removeSubjectIds);
        }

        List<PermissionInfo> buildingPermissionInfoByReference = getBuildingPermissionInfoByReference(buildingAddressId);
        for (PermissionInfo buildingPermissionInfo : buildingPermissionInfoByReference) {
            long buildingId = buildingPermissionInfo.getId();
            List<PermissionInfo> parentAddressPermissionInfos = getParentAddressPermissionInfo(buildingId);
            for (PermissionInfo parentAddressPermissionInfo : parentAddressPermissionInfos) {
                changeObjectPermissions(parentAddressPermissionInfo, addSubjectIds, removeSubjectIds);
            }
            buildingStrategy.changePermissions(buildingPermissionInfo, addSubjectIds, removeSubjectIds);
        }
    }


    public void updateBuildingAddressActivity(long addressId, boolean enabled) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("addressId", addressId);
        params.put("enabled", enabled);
        params.put("status", enabled ? Status.INACTIVE : Status.ACTIVE);
        sqlSession().update(BUILDING_ADDRESS_NS + ".updateBuildingAddressActivity", params);
    }


    @Override
    public void changeChildrenActivity(Long parentId, boolean enable) {
        long buildingAddressId = parentId;

        Set<Long> buildingActivityInfoByParent = getBuildingActivityInfoByParent(buildingAddressId);
        for (long buildingId : buildingActivityInfoByParent) {
            Set<Long> referenceAddressActivityInfo = getReferenceAddressActivityInfo(buildingId);
            for (long referenceAddressId : referenceAddressActivityInfo) {
                updateBuildingAddressActivity(referenceAddressId, !enable);
            }
            buildingStrategy.changeChildrenActivity(buildingId, enable);
        }
        updateChildrenActivity(buildingAddressId, "building", !enable);

        Set<Long> buildingActivityInfoByReference = getBuildingActivityInfoByReference(buildingAddressId);
        for (long buildingId : buildingActivityInfoByReference) {
            Set<Long> parentAddressActivityInfo = getParentAddressActivityInfo(buildingId);
            for (long parentAddressId : parentAddressActivityInfo) {
                updateBuildingAddressActivity(parentAddressId, !enable);
            }
            buildingStrategy.changeChildrenActivity(buildingId, enable);
            buildingStrategy.updateBuildingActivity(buildingId, !enable);
        }
    }

    @Override
    public boolean isUpperCase() {
        return true;
    }
}
