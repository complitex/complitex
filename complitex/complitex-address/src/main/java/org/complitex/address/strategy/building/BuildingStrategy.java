package org.complitex.address.strategy.building;

import com.google.common.collect.*;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.util.string.Strings;
import org.complitex.address.Module;
import org.complitex.address.resource.CommonResources;
import org.complitex.address.strategy.building.entity.Building;
import org.complitex.address.strategy.building.entity.BuildingCode;
import org.complitex.address.strategy.building.web.edit.BuildingEdit;
import org.complitex.address.strategy.building.web.edit.BuildingEditComponent;
import org.complitex.address.strategy.building.web.edit.BuildingValidator;
import org.complitex.address.strategy.building_address.BuildingAddressStrategy;
import org.complitex.common.entity.*;
import org.complitex.common.exception.DeleteException;
import org.complitex.common.service.LogBean;
import org.complitex.common.service.SessionBean;
import org.complitex.common.strategy.StringLocaleBean;
import org.complitex.common.strategy.StringValueBean;
import org.complitex.common.util.BuildingNumberConverter;
import org.complitex.common.util.ResourceUtil;
import org.complitex.common.util.StringValueUtil;
import org.complitex.common.web.component.domain.AbstractComplexAttributesPanel;
import org.complitex.common.web.component.domain.DomainObjectEditPanel;
import org.complitex.common.web.component.domain.DomainObjectListPanel;
import org.complitex.common.web.component.domain.validate.IValidator;
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
    public static final String BUILDING_NS = BuildingStrategy.class.getName();
    private static final String RESOURCE_BUNDLE = BuildingStrategy.class.getName();

    /**
     * Attribute ids
     */
    public static final long DISTRICT = 500;
    public static final long BUILDING_ADDRESS = 501;
    private static final long BUILDING_CODE = 502;


    /**
     * Order by related constants
     */
    public static enum OrderBy {

        NUMBER(BuildingAddressStrategy.NUMBER), CORP(BuildingAddressStrategy.CORP), STRUCTURE(BuildingAddressStrategy.STRUCTURE);
        private Long orderByAttributeId;

        private OrderBy(Long orderByAttributeId) {
            this.orderByAttributeId = orderByAttributeId;
        }

        public Long getOrderByAttributeId() {
            return orderByAttributeId;
        }
    }
    /**
     * Filter constants
     */
    public static final String P_NUMBER = "number";
    public static final String P_CORP = "corp";
    public static final String P_STRUCTURE = "structure";
    public static final String P_STREET = "street";
    public static final String P_CITY = "city";
    public static final String P_SERVICING_ORGANIZATION_ID = "servicingOrganizationId";

    public static final long PARENT_ENTITY_ID = 1500L;
    @EJB
    private StringValueBean stringBean;

    @EJB
    private StringLocaleBean stringLocaleBean;

    @EJB
    private BuildingAddressStrategy buildingAddressStrategy;

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
        return Arrays.asList(BuildingAddressStrategy.NUMBER, BuildingAddressStrategy.CORP, BuildingAddressStrategy.STRUCTURE);
    }

    @Override
    public List<Building> getList(DomainObjectFilter example) {
        if (example.getObjectId() != null && example.getObjectId() <= 0) {
            return Collections.emptyList();
        }

        example.setEntityName(getEntityName());
        prepareExampleForPermissionCheck(example);

        List<Building> buildings = Lists.newArrayList();

        if (example.getObjectId() != null) {
            boolean buildingFound = false;
            Building building = getDomainObject(example.getObjectId(), false);

            if (building != null) {
                buildingFound = true;
                Long streetId = example.getAdditionalParam(P_STREET);
                if (streetId != null && streetId > 0) {
                    DomainObject address = building.getAddress(streetId);
                    if (address == null) {
                        buildingFound = false;
                    } else {
                        building.setAccompaniedAddress(address);
                    }
                } else {
                    DomainObject primaryAddress = building.getPrimaryAddress();
                    Long cityId = example.getAdditionalParam(P_CITY);
                    if (cityId == null || cityId <= 0 || !(cityId.equals(primaryAddress.getParentId())
                            && Long.valueOf(400L).equals(primaryAddress.getParentEntityId()))) {
                        buildingFound = false;
                    } else {
                        building.setAccompaniedAddress(primaryAddress);
                    }
                }
            }
            if (buildingFound) {
                buildings.add(building);
            }
        } else {
            List<? extends DomainObject> addresses = buildingAddressStrategy.getList(createAddressExample(example));

            for (DomainObject address : addresses) {
                example.addAdditionalParam("buildingAddressId", address.getObjectId());
                List<Building> result = sqlSession().selectList(BUILDING_NS + ".selectBuildings", example);

                if (result.size() == 1) {
                    Building building = result.get(0);

                    loadAttributes(building);

                    //link address
                    building.setAccompaniedAddress(address);
                    building.addAttribute(address.getAttribute(BuildingAddressStrategy.NUMBER));
                    building.addAttribute(address.getAttribute(BuildingAddressStrategy.CORP));
                    building.addAttribute(address.getAttribute(BuildingAddressStrategy.STRUCTURE));

                    building.setSubjectIds(getSubjects(building.getPermissionId()));

                    buildings.add(building);
                } else {
                    if (result.isEmpty()) {
                        String message = "There are no building object linked to active building address object. Building address object id = " + address.getObjectId()
                                + ". Address base is in inconsistent state!";
                        throw new IllegalStateException(message);
                    } else {
                        List<Long> buildingIds = Lists.newArrayList(result.stream().map(DomainObject::getObjectId).collect(Collectors.toList()));

                        String message = "There are more than one building objects linked to one building address object. Building address object id = "
                                + address.getObjectId() + ", building object's ids linked to specified building address object: " + buildingIds;
                        throw new IllegalStateException(message);
                    }
                }
            }
        }

        return buildings;
    }

    private DomainObjectFilter createAddressExample(DomainObjectFilter buildingExample) {
        String number = buildingExample.getAdditionalParam(P_NUMBER);
        String corp = buildingExample.getAdditionalParam(P_CORP);
        String structure = buildingExample.getAdditionalParam(P_STRUCTURE);

        DomainObjectFilter addressExample = new DomainObjectFilter();

        addressExample.setAsc(buildingExample.isAsc());
        addressExample.setComparisonType(buildingExample.getComparisonType());
        addressExample.setLocaleId(buildingExample.getLocaleId());
        addressExample.setOrderByAttributeTypeId(buildingExample.getOrderByAttributeTypeId());
        addressExample.setFirst(buildingExample.getFirst());
        addressExample.setCount(buildingExample.getCount());
        addressExample.setStatus(buildingExample.getStatus());
        addressExample.setAdmin(buildingExample.isAdmin());
//        addressExample.setUserPermissionString(sessionBean.getPermissionString("building_address"));

        addressExample.addAttributeFilter(new AttributeFilter(BuildingAddressStrategy.NUMBER, number));
        addressExample.addAttributeFilter(new AttributeFilter(BuildingAddressStrategy.CORP, corp));
        addressExample.addAttributeFilter(new AttributeFilter(BuildingAddressStrategy.STRUCTURE, structure));

        Map<String, Long> ids = Maps.newHashMap();
        Long streetId = buildingExample.getAdditionalParam(P_STREET);
        ids.put("street", streetId);
        Long cityId = buildingExample.getAdditionalParam(P_CITY);
        ids.put("city", cityId);

        buildingAddressStrategy.configureFilter(addressExample, ids, null);

        return addressExample;
    }

    @Override

    public Long getCount(DomainObjectFilter example) {
        if (example.getObjectId() != null && example.getObjectId() <= 0) {
            return 0L;
        }

        prepareExampleForPermissionCheck(example);
        if (example.getObjectId() != null) {
            Building building = getDomainObject(example.getObjectId(), false);
            return building == null ? 0L : 1L;
        } else {
            DomainObjectFilter addressExample = createAddressExample(example);
            return buildingAddressStrategy.getCount(addressExample);
        }
    }

    private DomainObject getBuildingAddress(long id, Date date) {
        if (date == null) {
            return buildingAddressStrategy.getDomainObject(id, true);
        } else {
            return buildingAddressStrategy.getHistoryObject(id, date);
        }
    }

    private void setAlternativeAddresses(Building building, Date date) {
        for (Attribute attr : building.getAttributes()) {
            if (attr.getAttributeTypeId().equals(BUILDING_ADDRESS)) {
                DomainObject alternativeAddress = getBuildingAddress(attr.getValueId(), date);
                if (alternativeAddress != null) {
                    building.addAlternativeAddress(alternativeAddress);
                }
            }
        }
    }

    @Override
    public Building getDomainObject(Long id, boolean runAsAdmin) {
        DomainObjectFilter example = new DomainObjectFilter(id, getEntityName());

        if (!runAsAdmin) {
            prepareExampleForPermissionCheck(example);
        } else {
            example.setAdmin(true);
        }

        Building building = sqlSession().selectOne(BUILDING_NS + ".selectBuildingById", example);

        if (building != null) {
            loadAttributes(building);
            DomainObject primaryAddress = getBuildingAddress(building.getParentId(), null);
            building.setPrimaryAddress(primaryAddress);
            building.setAccompaniedAddress(primaryAddress);
            setAlternativeAddresses(building, null);
            fillAttributes(null, building);
            updateStringsForNewLocales(building);

            //load subject ids
            building.setSubjectIds(getSubjects(building.getPermissionId()));

            //load building codes
            building.setBuildingCodes(loadBuildingCodes(building));
        }

        return building;
    }

    @Override
    public Building getDomainObject(Long id){
        return getDomainObject(id, true);
    }


    @Override
    public Building newInstance() {
        Building building = new Building(super.newInstance());
        building.setPrimaryAddress(buildingAddressStrategy.newInstance());

        return building;
    }

    @Override
    public String displayDomainObject(DomainObject object, Locale locale) {
        Building building = (Building) object;

        return displayBuilding(building.getAccompaniedNumber(locale), building.getAccompaniedCorp(locale),
                building.getAccompaniedStructure(locale), locale);
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
            filter.addAdditionalParam("number", searchTextInput);
        }
        Long streetId = ids.get("street");
        if (streetId != null && streetId > 0) {
            filter.addAdditionalParam(P_STREET, streetId);
        } else {
            filter.addAdditionalParam(P_STREET, null);
            Long cityId = ids.get("city");
            if (cityId != null && cityId > 0) {
                filter.addAdditionalParam(P_CITY, cityId);
            } else {
                filter.addAdditionalParam(P_CITY, null);
            }
        }
    }

    private static class SearchCallback implements ISearchCallback, Serializable {

        @Override
        public void found(Component component, Map<String, Long> ids, AjaxRequestTarget target) {
            DomainObjectListPanel list = component.findParent(DomainObjectListPanel.class);

            assert list != null;
            DomainObjectFilter example = list.getFilter();

            Long streetId = ids.get("street");
            if (streetId != null && streetId > 0) {
                example.addAdditionalParam(P_STREET, streetId);
            } else {
                example.addAdditionalParam(P_STREET, null);
                Long cityId = ids.get("city");
                if (cityId != null && cityId > 0) {
                    example.addAdditionalParam(P_CITY, cityId);
                } else {
                    example.addAdditionalParam(P_CITY, null);
                }
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

    @Override
    public IValidator getValidator() {
        return new BuildingValidator(stringLocaleBean.getSystemLocale());
    }

    @Override
    public Class<? extends AbstractComplexAttributesPanel> getComplexAttributesPanelAfterClass() {
        return BuildingEditComponent.class;
    }

    @Override
    public String[] getParents() {
        return new String[]{"city"};
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
    protected void insertDomainObject(DomainObject object, Date insertDate) {
        Building building = (Building) object;

        for (DomainObject buildingAddress : building.getAllAddresses()) {
            buildingAddress.setSubjectIds(building.getSubjectIds());
            buildingAddressStrategy.insert(buildingAddress, insertDate);
        }

        building.enhanceAlternativeAddressAttributes();
        building.setParentId(building.getPrimaryAddress().getObjectId());
        building.setParentEntityId(PARENT_ENTITY_ID);
        super.insertDomainObject(object, insertDate);

        for (DomainObject buildingAddress : building.getAllAddresses()) {
            logBean.log(Log.STATUS.OK, Module.NAME, DomainObjectEditPanel.class,
                    Log.EVENT.CREATE, buildingAddressStrategy, null, buildingAddress, null);
        }

        //building codes
        addBuildingCode(building);
    }


    @Override
    protected void insertUpdatedDomainObject(DomainObject object, Date updateDate) {
        super.insertDomainObject(object, updateDate);
    }

    public List<Long> getObjectIds(Long parentId, String number, String corp, String structure, long parentEntityId,
                                    Locale locale) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("additionalAddressAT", BUILDING_ADDRESS);
        params.put("buildingAddressNumberAT", BuildingAddressStrategy.NUMBER);
        params.put("buildingAddressCorpAT", BuildingAddressStrategy.CORP);
        params.put("buildingAddressStructureAT", BuildingAddressStrategy.STRUCTURE);
        params.put("number", number);
        params.put("corp", corp != null && corp.isEmpty() ? null : corp);
        params.put("structure", structure);
        params.put("parentEntityId", parentEntityId);
        params.put("parentId", parentId);
        params.put("localeId", stringLocaleBean.convert(locale).getId());

        return sqlSession().selectList(BUILDING_NS + ".checkBuildingAddress", params);
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
    protected void fillAttributes(String dataSource, DomainObject object) {
        List<Attribute> toAdd = Lists.newArrayList();

        for (AttributeType attributeType : getEntity().getAttributeTypes()) {
            if (!attributeType.isObsolete()) {
                if (object.getAttributes(attributeType.getId()).isEmpty()) {
                    if ((attributeType.getAttributeValueTypes().size() == 1) && !attributeType.getId().equals(BUILDING_ADDRESS)) {
                        Attribute attribute = new Attribute();
                        AttributeValueType attributeValueType = attributeType.getAttributeValueTypes().get(0);
                        attribute.setAttributeTypeId(attributeType.getId());
                        attribute.setValueTypeId(attributeValueType.getId());
                        attribute.setObjectId(object.getObjectId());
                        attribute.setAttributeId(1L);

                        if (isSimpleAttributeType(attributeType)) {
                            attribute.setStringValues(StringValueUtil.newStringValues());
                        }
                        toAdd.add(attribute);
                    }
                }
            }
        }
        if (!toAdd.isEmpty()) {
            object.getAttributes().addAll(toAdd);
        }
    }


    @Override
    public void update(DomainObject oldObject, DomainObject newObject, Date updateDate) {
        Building oldBuilding = (Building) oldObject;
        Building newBuilding = (Building) newObject;

        //building codes
        long index = 1;
        for (Attribute a : oldBuilding.getAttributes(BUILDING_CODE)){
            if (a.getAttributeId() > index){
                index = a.getAttributeId();
            }
        }

        for (BuildingCode bc : newBuilding.getBuildingCodes()){
            if (bc.getId() == null){
                bc.setBuildingId(newBuilding.getObjectId());
                saveBuildingCode(bc);

                newBuilding.addAttribute(newBuildingCodeAttribute(++index, bc.getId()));
            }
        }

        //addresses
        List<DomainObject> removedAddresses = determineRemovedAddresses(oldBuilding, newBuilding);
        List<DomainObject> addedAddresses = determineAddedAddresses(newBuilding);
        Map<DomainObject, DomainObject> updatedAddressesMap = determineUpdatedAddresses(oldBuilding, newBuilding);

        if (removedAddresses != null) {
            for (DomainObject removedAddress : removedAddresses) {
                buildingAddressStrategy.archive(removedAddress, updateDate);
            }
        }
        if (addedAddresses != null) {
            for (DomainObject newAddress : addedAddresses) {
                newAddress.setSubjectIds(newBuilding.getSubjectIds());
                buildingAddressStrategy.insert(newAddress, updateDate);
            }
        }

        if (updatedAddressesMap != null) {
            for (Map.Entry<DomainObject, DomainObject> updatedAddress : updatedAddressesMap.entrySet()) {
                DomainObject oldAddress = updatedAddress.getKey();
                DomainObject newAddress = updatedAddress.getValue();
                newAddress.setSubjectIds(newBuilding.getSubjectIds());
                buildingAddressStrategy.update(oldAddress, newAddress, updateDate);
            }
        }

        newBuilding.enhanceAlternativeAddressAttributes();

        super.update(oldBuilding, newBuilding, updateDate);

        //log
        if (addedAddresses != null) {
            for (DomainObject newAddress : addedAddresses) {
                logBean.log(Log.STATUS.OK, Module.NAME, DomainObjectEditPanel.class,
                        Log.EVENT.CREATE, buildingAddressStrategy, null, newAddress, null);
            }
        }

        if (updatedAddressesMap != null) {
            for (Map.Entry<DomainObject, DomainObject> updatedAddress : updatedAddressesMap.entrySet()) {
                DomainObject oldAddress = updatedAddress.getKey();
                DomainObject newAddress = updatedAddress.getValue();
                logBean.log(Log.STATUS.OK, Module.NAME, DomainObjectEditPanel.class,
                        Log.EVENT.EDIT, buildingAddressStrategy, oldAddress, newAddress, null);
            }
        }
    }

    private List<DomainObject> determineRemovedAddresses(Building oldBuilding, Building newBuilding) {
        List<DomainObject> removedAddresses = Lists.newArrayList();

        List<DomainObject> oldAddresses = oldBuilding.getAllAddresses();
        List<DomainObject> newAddresses = newBuilding.getAllAddresses();

        for (DomainObject oldAddress : oldAddresses) {
            boolean removed = true;
            for (DomainObject newAddress : newAddresses) {
                if (oldAddress.getObjectId().equals(newAddress.getObjectId())) {
                    removed = false;
                    break;
                }
            }
            if (removed) {
                removedAddresses.add(oldAddress);
            }
        }
        return removedAddresses;
    }

    private List<DomainObject> determineAddedAddresses(Building newBuilding) {
        List<DomainObject> addedAddresses = Lists.newArrayList();

        List<DomainObject> newAddresses = newBuilding.getAllAddresses();

        for (DomainObject newAddress : newAddresses) {
            if (newAddress.getObjectId() == null) {
                addedAddresses.add(newAddress);
            }
        }
        return addedAddresses;
    }

    private Map<DomainObject, DomainObject> determineUpdatedAddresses(Building oldBuilding, Building newBuilding) {
        Map<DomainObject, DomainObject> updatedAddressesMap = Maps.newHashMap();

        List<DomainObject> oldAddresses = oldBuilding.getAllAddresses();
        List<DomainObject> newAddresses = newBuilding.getAllAddresses();

        for (DomainObject oldAddress : oldAddresses) {
            for (DomainObject newAddress : newAddresses) {
                if (oldAddress.getObjectId().equals(newAddress.getObjectId())) {
                    updatedAddressesMap.put(oldAddress, newAddress);
                    break;
                }
            }
        }
        return updatedAddressesMap;
    }

    @Override
    public Long getDefaultOrderByAttributeId() {
        return BuildingAddressStrategy.DEFAULT_ORDER_BY_ID;
    }


    @Override
    public TreeSet<Date> getHistoryDates(Long objectId) {
        TreeSet<Date> historyDates = super.getHistoryDates(objectId);
        Set<Long> addressIds = getBuildingAddresses(objectId);
        for (Long addressId : addressIds) {
            TreeSet<Date> addressHistoryDates = buildingAddressStrategy.getHistoryDates(addressId);
            historyDates.addAll(addressHistoryDates);
        }
        return historyDates;
    }


    private Set<Long> getBuildingAddresses(long buildingId) {
        List<Long> results = sqlSession().selectList(BUILDING_NS + ".selectBuildingAddresses", buildingId);
        return Sets.newHashSet(results);
    }


    @Override
    public Building getHistoryObject(Long objectId, Date date) {
        DomainObjectFilter example = new DomainObjectFilter(objectId, getEntityName(), date);

        Building building = sqlSession().selectOne(BUILDING_NS + ".selectHistoryObject", example);
        if (building == null) {
            return null;
        }

        List<Attribute> historyAttributes = loadHistoryAttributes(objectId, date);
        loadStringValues(historyAttributes);
        building.setAttributes(historyAttributes);
        DomainObject primaryAddress = getBuildingAddress(building.getParentId(), date);
        building.setPrimaryAddress(primaryAddress);
        building.setAccompaniedAddress(primaryAddress);
        setAlternativeAddresses(building, date);

        updateStringsForNewLocales(building);

        //building codes
        building.setBuildingCodes(loadBuildingCodes(building));

        return building;
    }

    @Override
    protected void changeActivity(DomainObject object, boolean enable) {
        super.changeActivity(object, enable);

        Building building = (Building) object;
        for (DomainObject address : building.getAllAddresses()) {
            buildingAddressStrategy.updateBuildingAddressActivity(address.getObjectId(), !enable);
        }
    }

    @Override
    public String[] getEditRoles() {
        return new String[]{SecurityRole.ADDRESS_MODULE_EDIT};
    }


    public void updateBuildingActivity(long buildingId, boolean enabled) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("buildingId", buildingId);
        params.put("enabled", enabled);
        params.put("status", enabled ? StatusType.INACTIVE : StatusType.ACTIVE);
        sqlSession().update(BUILDING_NS + ".updateBuildingActivity", params);
    }

    @Override
    public String[] getRealChildren() {
        return new String[]{"apartment", "room"};
    }


    @Override
    public void delete(Long objectId, Locale locale) throws DeleteException {
        deleteChecks(objectId, locale);

        sqlSession().delete(BUILDING_NS + ".deleteBuildingCodes", ImmutableMap.of("objectId", objectId,
                "buildingCodesAT", BUILDING_CODE));

        Set<Long> addressIds = getBuildingAddresses(objectId);

        deleteStrings(objectId);
        deleteAttributes(objectId);
        deleteDomainObject(objectId);

        //delete building address:
        for (Long addressId : addressIds) {
            buildingAddressStrategy.delete(addressId, locale);
        }
    }

    public Long getDistrictId(Building building) {
        Attribute districtAttribute = building.getAttribute(DISTRICT);
        return districtAttribute != null ? districtAttribute.getValueId() : null;
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
    public List<Long> getBuildingObjectIds(Long cityId, Long streetId, String buildingNumber, String buildingCorp) {
        Map<String, Object> params = Maps.newHashMap();

        String preparedNumber = BuildingNumberConverter.convert(buildingNumber);
        params.put("number", preparedNumber == null ? "" : preparedNumber);
        String preparedCorp = removeWhiteSpaces(buildingCorp);
        params.put("corp", Strings.isEmpty(preparedCorp) ? null : preparedCorp);

        params.put("parentId", streetId != null ? streetId : cityId);
        params.put("parentEntityId", streetId != null ? 300 : 400);

        return sqlSession().selectList(BUILDING_NS + ".selectBuildingObjectIds", params);
    }

    public List<BuildingCode> loadBuildingCodes(Building building) {
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
        return sqlSession().selectList(BUILDING_NS + ".getBuildingCodes", ImmutableMap.of("ids", buildingCodeIds));
    }

    public Long getBuildingCodeId(Long organizationId, String buildingCode) {
        return sqlSession().selectOne(BUILDING_NS + ".selectBuildingCodeIdByCode",
                ImmutableMap.of("organizationId", organizationId, "buildingCode", buildingCode));
    }

    public Long getBuildingCodeId(Long organizationId, Long buildingId) {
        return sqlSession().selectOne(BUILDING_NS + ".selectBuildingCodeIdByBuilding",
                ImmutableMap.of("organizationId", organizationId, "buildingId", buildingId));
    }

    public BuildingCode getBuildingCode(Long buildingId, String organizationCode){
        return sqlSession().selectOne(BUILDING_NS + ".selectBuildingCodeByOrganizationCode",
                ImmutableMap.of("buildingId", buildingId, "organizationCode", organizationCode));
    }

    public BuildingCode getBuildingCodeById(long buildingCodeId) {
        List<BuildingCode> codes = getBuildingCodes(Collections.singleton(buildingCodeId));
        if (codes == null || codes.size() > 1) {
            throw new IllegalStateException("There are more one building code for id: " + buildingCodeId);
        }
        return codes.get(0);
    }


    private void addBuildingCode(Building building) {
        building.removeAttribute(BUILDING_CODE);

        long i = 1;
        for (BuildingCode buildingCode : building.getBuildingCodes()) {
            buildingCode.setBuildingId(building.getObjectId());
            saveBuildingCode(buildingCode);

            building.addAttribute(newBuildingCodeAttribute(i++, buildingCode.getId()));
        }
    }

    private Attribute newBuildingCodeAttribute(long attributeId, long buildingCodeId) {
        Attribute a = new Attribute();
        a.setAttributeTypeId(BUILDING_CODE);
        a.setValueId(buildingCodeId);
        a.setValueTypeId(BUILDING_CODE);
        a.setAttributeId(attributeId);

        return a;
    }

    private void saveBuildingCode(BuildingCode buildingCode) {
        sqlSession().insert(BUILDING_NS + ".insertBuildingCode", buildingCode);
    }
}
