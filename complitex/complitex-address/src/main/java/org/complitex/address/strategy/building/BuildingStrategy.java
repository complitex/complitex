package org.complitex.address.strategy.building;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.util.string.Strings;
import org.complitex.address.resource.CommonResources;
import org.complitex.address.strategy.building.entity.BuildingCode;
import org.complitex.address.strategy.building.web.edit.BuildingEdit;
import org.complitex.common.entity.*;
import org.complitex.common.service.LogBean;
import org.complitex.common.service.SessionBean;
import org.complitex.common.strategy.StringLocaleBean;
import org.complitex.common.util.BuildingNumberConverter;
import org.complitex.common.util.Locales;
import org.complitex.common.util.ResourceUtil;
import org.complitex.common.web.component.DomainObjectInputPanel;
import org.complitex.common.web.component.domain.DomainObjectListPanel;
import org.complitex.common.web.component.search.ISearchCallback;
import org.complitex.template.strategy.TemplateStrategy;
import org.complitex.template.web.security.SecurityRole;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.io.Serializable;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

import static org.complitex.common.util.StringUtil.removeWhiteSpaces;

@Stateless
public class BuildingStrategy extends TemplateStrategy {
    public static final String NS = BuildingStrategy.class.getName();
    private static final String RESOURCE_BUNDLE = BuildingStrategy.class.getName();

    public static final long PARENT_ENTITY_ID = 300L;

    public static final long NUMBER = 500;
    public static final long CORP = 501;
    public static final long STRUCTURE = 502;
    public static final long DISTRICT = 503;
    private static final long BUILDING_CODE = 504;

    public enum OrderBy {

        NUMBER(BuildingStrategy.NUMBER), CORP(BuildingStrategy.CORP), STRUCTURE(BuildingStrategy.STRUCTURE);
        private Long orderByAttributeId;

        OrderBy(Long orderByAttributeId) {
            this.orderByAttributeId = orderByAttributeId;
        }

        public Long getOrderByAttributeId() {
            return orderByAttributeId;
        }
    }

    public static final String P_STREET = "street";
    public static final String P_CITY = "city";
    public static final String P_SERVICING_ORGANIZATION_ID = "servicingOrganizationId";

    @EJB
    private StringLocaleBean stringLocaleBean;

    @EJB
    private SessionBean sessionBean;

    @EJB
    private LogBean logBean;

    @Override
    public String getEntityName() {
        return "building";
    }

    @Override
    public List<Long> getColumnAttributeTypeIds() {
        return Arrays.asList(NUMBER, CORP, STRUCTURE);
    }

    @Override
    public String displayDomainObject(DomainObject object, Locale locale) {
        return displayBuildingAddress(object.getStringValue(NUMBER), object.getStringValue(CORP),
                object.getStringValue(STRUCTURE), locale);
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

    public String getName(DomainObject object){
        return displayDomainObject(object, Locales.getSystemLocale());
    }

    private String displayBuilding(String number, String corp, String structure, Locale locale) {
        if (Strings.isEmpty(corp)) {
            if (Strings.isEmpty(structure)) {
                return number;
            } else {
                return MessageFormat.format(ResourceUtil.getString(RESOURCE_BUNDLE, "number_structure", locale), number, structure);
            }
        } else {
            if (Strings.isEmpty(structure)) {
                return MessageFormat.format(ResourceUtil.getString(RESOURCE_BUNDLE, "number_corp", locale), number, corp);
            } else {
                return MessageFormat.format(ResourceUtil.getString(RESOURCE_BUNDLE, "number_corp_structure", locale), number, corp, structure);
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
//                filter.setParentId(cityId);
//                filter.setParentEntity("city");
            } else {
                filter.setParentId(null);
                filter.setParentEntity(null);
            }
        }
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
                domainObject.setParentEntityId(PARENT_ENTITY_ID);
            } else {
                Long cityId = ids.get("city");
                if (cityId != null && cityId > 0) {
//                    domainObject.setParentId(cityId);
//                    domainObject.setParentEntityId(400L);
                } else {
                    domainObject.setParentId(null);
                    domainObject.setParentEntityId(null);
                }
            }
        }
    }

    @Override
    public ISearchCallback getParentSearchCallback() {
        return new ParentSearchCallback();
    }

    private static class SearchCallback implements ISearchCallback, Serializable {

        @Override
        public void found(Component component, Map<String, Long> ids, AjaxRequestTarget target) {
            DomainObjectListPanel list = component.findParent(DomainObjectListPanel.class);

            assert list != null;
            DomainObjectFilter filter = list.getFilter();

            Long streetId = ids.get("street");
            if (streetId != null && streetId > 0) {
                filter.setParentEntity("street");
                filter.setParentId(streetId);
            } else {
//                example.addAdditionalParam(P_STREET, null);
//                Long cityId = ids.get("city");
//                if (cityId != null && cityId > 0) {
//                    example.addAdditionalParam(P_CITY, cityId);
//                } else {
//                    example.addAdditionalParam(P_CITY, null);
//                }

                filter.setParentEntity(null);
                filter.setParentId(null);
            }

            list.refreshContent(target);
        }
    }

    @Override
    public ISearchCallback getSearchCallback() {
        return new SearchCallback();
    }

    @Override
    public List<String> getSearchFilters() {
        return ImmutableList.of("country", "region", "city", "street");
    }

    @Override
    public String getPluralEntityLabel(Locale locale) {
        return ResourceUtil.getString(CommonResources.class.getName(), getEntityName(), locale);
    }

//    @Override
//    public IValidator getValidator() {
//        return new BuildingValidator(stringLocaleBean.getSystemLocale());
//    }
//
//    @Override
//    public Class<? extends AbstractComplexAttributesPanel> getComplexAttributesPanelAfterClass() {
//        return BuildingEditComponent.class;
//    }

    @Override
    public String[] getParents() {
        return new String[]{"street"};
    }

    @Override
    public int getSearchTextFieldSize() {
        return 8;
    }

    @Override
    public boolean allowProceedNextSearchFilter() {
        return true;
    }



    @Override
    protected void insertUpdatedDomainObject(DomainObject object, Date updateDate) {
        super.insertDomainObject(object, updateDate);
    }

    public List<Long> getObjectIds(Long parentId, String number, String corp, String structure, long parentEntityId,
                                    Locale locale) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("buildingAddressNumberAT", NUMBER);
        params.put("buildingAddressCorpAT", CORP);
        params.put("buildingAddressStructureAT", STRUCTURE);
        params.put("number", number);
        params.put("corp", corp != null && corp.isEmpty() ? null : corp);
        params.put("structure", structure);
        params.put("parentEntityId", parentEntityId);
        params.put("parentId", parentId);
        params.put("localeId", stringLocaleBean.convert(locale).getId());

        return sqlSession().selectList(NS + ".checkBuildingAddress", params);
    }

    public Long checkForExistingAddress(Long id, String number, String corp, String structure, long parentEntityId,
                                        long parentId, Locale locale) {
        List<Long> buildingIds = getObjectIds(parentEntityId, number, corp, structure, parentId, locale);

        for (Long buildingId : buildingIds) {
            if (!buildingId.equals(id)) {
                return buildingId;
            }
        }

        return null;
    }

    @Override
    public String[] getEditRoles() {
        return new String[]{SecurityRole.ADDRESS_MODULE_EDIT};
    }

    @Override
    public String[] getRealChildren() {
        return new String[]{"apartment", "room"};
    }


    public Long getDistrictId(DomainObject building) {
        return building.getValueId(DISTRICT);
    }

    @Override
    public String[] getListRoles() {
        return new String[]{SecurityRole.ADDRESS_MODULE_VIEW};
    }

    @Override
    public Class<? extends WebPage> getEditPage() {
        return BuildingEdit.class;
    }

    @Override
    public Long getDefaultSortAttributeTypeId() {
        return OrderBy.NUMBER.getOrderByAttributeId();
    }

    /**
     * Найти дом в локальной адресной базе.
     * При поиске к значению номера(buildingNumber) и корпуса(buildingCorp) дома применяются SQL функции TRIM() и TO_CYRILLIC()
     */
    //todo ref
    public List<Long> getBuildingObjectIds(Long cityId, Long streetId, String buildingNumber, String buildingCorp) {
        Map<String, Object> params = Maps.newHashMap();

        String preparedNumber = BuildingNumberConverter.convert(buildingNumber);
        params.put("number", preparedNumber == null ? "" : preparedNumber);
        String preparedCorp = removeWhiteSpaces(buildingCorp);
        params.put("corp", Strings.isEmpty(preparedCorp) ? null : preparedCorp);

        params.put("parentId", streetId != null ? streetId : cityId);
        params.put("parentEntityId", streetId != null ? 300 : 400);

        return sqlSession().selectList(NS + ".selectBuildingObjectIds", params);
    }

    public List<BuildingCode> loadBuildingCodes(DomainObject building) {
        List<Attribute> buildingCodeAttributes = building.getAttributes(BUILDING_CODE);
        Set<Long> buildingCodeIds = Sets.newHashSet();
        buildingCodeIds.addAll(buildingCodeAttributes.stream().map(Attribute::getValueId).collect(Collectors.toList()));

        List<BuildingCode> buildingCodes = new ArrayList<>();
        if (!buildingCodeIds.isEmpty()) {
            buildingCodes = getBuildingCodes(buildingCodeIds);
            Collections.sort(buildingCodes, new Comparator<BuildingCode>() {

                @Override
                public int compare(BuildingCode o1, BuildingCode o2) {
                    return o1.getId().compareTo(o2.getId());
                }
            });
        }

        return buildingCodes;
    }

    public List<BuildingCode> getBuildingCodes(Set<Long> buildingCodeIds) {
        return sqlSession().selectList(NS + ".getBuildingCodes", ImmutableMap.of("ids", buildingCodeIds));
    }

    public Long getBuildingCodeId(Long organizationId, String buildingCode) {
        return sqlSession().selectOne(NS + ".selectBuildingCodeIdByCode",
                ImmutableMap.of("organizationId", organizationId, "buildingCode", buildingCode));
    }

    public Long getBuildingCodeId(Long organizationId, Long buildingId) {
        return sqlSession().selectOne(NS + ".selectBuildingCodeIdByBuilding",
                ImmutableMap.of("organizationId", organizationId, "buildingId", buildingId));
    }

    public BuildingCode getBuildingCode(Long buildingId, String organizationCode){
        return sqlSession().selectOne(NS + ".selectBuildingCodeByOrganizationCode",
                ImmutableMap.of("buildingId", buildingId, "organizationCode", organizationCode));
    }

    public BuildingCode getBuildingCodeById(long buildingCodeId) {
        List<BuildingCode> codes = getBuildingCodes(Collections.singleton(buildingCodeId));
        if (codes == null || codes.size() > 1) {
            throw new IllegalStateException("There are more one building code for id: " + buildingCodeId);
        }
        return codes.get(0);
    }


    private void addBuildingCode(DomainObject building) {
        building.removeAttribute(BUILDING_CODE);

        long i = 1;
//        for (BuildingCode buildingCode : building.getBuildingCodes()) {
//            buildingCode.setBuildingId(building.getObjectId());
//            saveBuildingCode(buildingCode);
//
//            building.addAttribute(newBuildingCodeAttribute(i++, buildingCode.getId()));
//        }
    }

    private Attribute newBuildingCodeAttribute(long attributeId, long buildingCodeId) {
        Attribute a = new Attribute();
        a.setEntityAttributeId(BUILDING_CODE);
        a.setValueId(buildingCodeId);
        a.setAttributeId(attributeId);

        return a;
    }

    private void saveBuildingCode(BuildingCode buildingCode) {
        sqlSession().insert(NS + ".insertBuildingCode", buildingCode);
    }

    @Override
    public boolean isUpperCase() {
        return true;
    }

    private List<PermissionInfo> getBuildingPermissionInfoByParent(long buildingAddressId) {
        return sqlSession().selectList(NS + ".selectBuildingPermissionInfoByParent", buildingAddressId);
    }


    private List<PermissionInfo> getBuildingPermissionInfoByReference(long buildingAddressId) {
        return sqlSession().selectList(NS + ".selectBuildingPermissionInfoByReference", buildingAddressId);
    }


    private List<PermissionInfo> getReferenceAddressPermissionInfo(long buildingId) {
        return sqlSession().selectList(NS + ".selectReferenceAddressPermissionInfo", buildingId);
    }


    private List<PermissionInfo> getParentAddressPermissionInfo(long buildingId) {
        return sqlSession().selectList(NS + ".selectParentAddressPermissionInfo", buildingId);
    }


    private Set<Long> getBuildingActivityInfoByParent(long buildingAddressId) {
        List<Long> results = sqlSession().selectList(NS + ".selectBuildingActivityInfoByParent", buildingAddressId);
        return Sets.newHashSet(results);
    }


    private Set<Long> getBuildingActivityInfoByReference(long buildingAddressId) {
        List<Long> results = sqlSession().selectList(NS + ".selectBuildingActivityInfoByReference", buildingAddressId);
        return Sets.newHashSet(results);
    }


    private Set<Long> getReferenceAddressActivityInfo(long buildingId) {
        List<Long> results = sqlSession().selectList(NS + ".selectReferenceAddressActivityInfo", buildingId);
        return Sets.newHashSet(results);
    }


    private Set<Long> getParentAddressActivityInfo(long buildingId) {
        List<Long> results = sqlSession().selectList(NS + ".selectParentAddressActivityInfo", buildingId);
        return Sets.newHashSet(results);
    }


}
