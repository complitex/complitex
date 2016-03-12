package org.complitex.address.strategy.street;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.util.string.Strings;
import org.complitex.address.resource.CommonResources;
import org.complitex.address.strategy.street.web.edit.StreetTypeComponent;
import org.complitex.address.strategy.street_type.StreetTypeStrategy;
import org.complitex.common.entity.AttributeFilter;
import org.complitex.common.entity.DomainObject;
import org.complitex.common.entity.DomainObjectFilter;
import org.complitex.common.strategy.StrategyFactory;
import org.complitex.common.strategy.StringCultureBean;
import org.complitex.common.strategy.StringLocaleBean;
import org.complitex.common.util.BuildingNumberConverter;
import org.complitex.common.util.ResourceUtil;
import org.complitex.common.util.StringCultures;
import org.complitex.common.web.component.DomainObjectInputPanel;
import org.complitex.common.web.component.domain.AbstractComplexAttributesPanel;
import org.complitex.common.web.component.domain.DomainObjectListPanel;
import org.complitex.common.web.component.search.ISearchCallback;
import org.complitex.template.strategy.TemplateStrategy;
import org.complitex.template.web.security.SecurityRole;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.io.Serializable;
import java.util.*;

import static org.complitex.common.util.StringUtil.removeWhiteSpaces;

/**
 * Street strategy.

 * <p><b>Important note : </b>
 * In some projects custom logic is needed for some operations. In these cases EJB interceptor technique is used 
 * to intercept calls to relevant operations and to apply custom logic instead of all-project general code. 
 * For example, in pspoffice project <code>org.complitex.pspoffice.address.street.StreetStrategyInterceptor</code>
 * is used to replace logic for "found" and "count" operations.</p>
 *
 * see StreetStrategyInterceptor
 *
 * @author Artem
 */
@Stateless
public class StreetStrategy extends TemplateStrategy {
    private static final String STREET_NS = StreetStrategy.class.getName();

    @EJB
    private StringCultureBean stringBean;

    @EJB
    private StrategyFactory strategyFactory;

    @EJB
    private StringLocaleBean stringLocaleBean;

    @EJB
    private StreetTypeStrategy streetTypeStrategy;

    /*
     * Attribute type ids
     */
    public static final long NAME = 300L;
    public static final long STREET_TYPE = 301L;
    public static final long STREET_CODE = 303L;

    public static final long PARENT_ENTITY_ID = 400L;

    @Override
    public String getEntityName() {
        return "street";
    }

    @SuppressWarnings({"unchecked"})
    @Override
    public List<DomainObject> getList(DomainObjectFilter example) {
        if (example.getObjectId() != null && example.getObjectId() <= 0) {
            return Collections.emptyList();
        }

        example.setEntityName(getEntityName());
        prepareExampleForPermissionCheck(example);

        List<DomainObject> objects = sqlSession().selectList(STREET_NS + ".selectDomainObjects", example);

        objects.forEach(o -> {
            o.setEntityName(getEntityName());
            loadAttributes(o);
            o.setSubjectIds(loadSubjects(o.getPermissionId()));
        });

        return objects;
    }


    @Override
    public Long getCount(DomainObjectFilter example) {
        if (example.getObjectId() != null && example.getObjectId() <= 0) {
            return 0L;
        }

        example.setEntityName(getEntityName());
        prepareExampleForPermissionCheck(example);

        return sqlSession().selectOne(STREET_NS + ".selectDomainObjectCount", example);
    }

    @Override
    public List<Long> getColumnAttributeTypeIds() {
        return Lists.newArrayList(STREET_TYPE, NAME);
    }

    @Override
    public String displayDomainObject(DomainObject object, Locale locale) {
        String streetName = getName(object, locale);
        String streetTypeName = getStreetTypeShortName(object, locale);

        return streetTypeName != null ? streetTypeName + " " + streetName : streetName;
    }

    public String getStreetTypeShortName(DomainObject domainObject, Locale locale){
        Long streetTypeId = getStreetType(domainObject);

        if (streetTypeId != null) {
            DomainObject streetType = streetTypeStrategy.getDomainObject(streetTypeId, true);

            if (streetType != null) {
                return streetTypeStrategy.getShortName(streetType, locale);
            }
        }

        return null;
    }

    public String getStreetTypeShortName(DomainObject domainObject){
        return getStreetTypeShortName(domainObject, stringLocaleBean.getSystemLocale());
    }

    public String getStreetTypeExternalId(DomainObject domainObject){
        Long streetTypeId = getStreetType(domainObject);

        if (streetTypeId != null) {
            DomainObject streetType = streetTypeStrategy.getDomainObject(streetTypeId, true);

            if (streetType != null) {
                return streetType.getExternalId();
            }
        }

        return null;
    }

    @Override
    public List<String> getSearchFilters() {
        return ImmutableList.of("country", "region", "city");
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

        Long districtId = ids.get("district");

        if (districtId != null) {
            filter.addAdditionalParam("district", districtId);
        }

        Long cityId = ids.get("city");

        if (cityId != null && cityId > 0) {
            filter.setParentId(cityId);
            filter.setParentEntity("city");
        }
    }

    @Override
    public ISearchCallback getSearchCallback() {
        return new ISearchCallback(){ //Serializable

            @Override
            public void found(Component component, Map<String, Long> ids, AjaxRequestTarget target) {
                DomainObjectListPanel list = component.findParent(DomainObjectListPanel.class);

                if (list != null) {
                    configureFilter(list.getFilter(), ids, null);
                    list.refreshContent(target);
                }
            }
        };
    }

    @Override
    public ISearchCallback getParentSearchCallback() {
        return new ParentSearchCallback();
    }

    private static class ParentSearchCallback implements ISearchCallback, Serializable {

        @Override
        public void found(Component component, Map<String, Long> ids, AjaxRequestTarget target) {
            DomainObjectInputPanel inputPanel = component.findParent(DomainObjectInputPanel.class);
            if (inputPanel != null) {
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
    }

    @Override
    public String getPluralEntityLabel(Locale locale) {
        return ResourceUtil.getString(CommonResources.class.getName(), getEntityName(), locale);
    }

    @Override
    public String[] getRealChildren() {
        return new String[]{"building_address"};
    }

    @Override
    public String[] getLogicalChildren() {
        return new String[]{"building"};
    }

    @Override
    public Class<? extends AbstractComplexAttributesPanel> getComplexAttributesPanelAfterClass() {
        return StreetTypeComponent.class;
    }

    @Override
    public String[] getParents() {
        return new String[]{"city"};
    }

    public Long getStreetType(DomainObject streetObject) {
        return streetObject.getAttribute(STREET_TYPE).getValueId();
    }

    public Long getStreetType(Long streetId) {
        return getDomainObject(streetId).getAttribute(STREET_TYPE).getValueId();
    }

    @SuppressWarnings({"unchecked"})
    @Override
    public Long performDefaultValidation(DomainObject streetObject, Locale locale) {
        Map<String, Object> params = super.createValidationParams(streetObject, locale);
        params.put("streetNameAT", NAME);
        params.put("streetTypeAT", STREET_TYPE);
        Long streetTypeId = getStreetType(streetObject);
        params.put("streetTypeId", streetTypeId);
        List<Long> results = sqlSession().selectList(STREET_NS + ".defaultValidation", params);

        for (Long result : results) {
            if (!result.equals(streetObject.getObjectId())) {
                return result;
            }
        }

        return null;
    }


    @Override
    public void replaceChildrenPermissions(Long parentId, Set<Long> subjectIds) {
        replaceChildrenPermissions("building_address", parentId, subjectIds);
    }


    @Override
    protected void changeChildrenPermissions(Long parentId, Set<Long> addSubjectIds, Set<Long> removeSubjectIds) {
        changeChildrenPermissions("building_address", parentId, addSubjectIds, removeSubjectIds);
    }


    @Override
    public void changeChildrenActivity(Long parentId, boolean enable) {
        changeChildrenActivity(parentId, "building_address", enable);
    }

    @Override
    public String[] getEditRoles() {
        return new String[]{SecurityRole.ADDRESS_MODULE_EDIT};
    }

    public String getName(Long streetId) {
        DomainObject streetObject = getDomainObject(streetId, true);

        return streetObject != null ? getName(streetObject) : null;
    }

    public String getFullName(Long streetId) {
        DomainObject streetObject = getDomainObject(streetId, true);

        return streetObject != null ? getStreetTypeShortName(streetObject) + " " + getName(streetObject) : null;
    }

    public String getName(DomainObject streetObject){
        return getName(streetObject, stringLocaleBean.getSystemLocale());
    }

    public String getName(DomainObject street, Locale locale) {
        return StringCultures.getValue(street.getAttributes().stream()
                .filter(attr -> attr.getAttributeTypeId().equals(NAME)).findFirst().get().getStringCultures(), locale);
    }

    @Override
    public String[] getListRoles() {
        return new String[]{SecurityRole.ADDRESS_MODULE_VIEW};
    }

    public List<Long> getStreetIds(Long cityObjectId, Long streetTypeObjectId, String streetName){
        Map<String, Object> parameter = new HashMap<>();

        parameter.put("cityId", cityObjectId);
        parameter.put("streetTypeId", streetTypeObjectId);
        parameter.put("streetName", streetName);

        return sqlSession().selectList(STREET_NS + ".selectStreetIds", parameter);
    }

    public List<Long> getStreetIdsByDistrict(Long cityObjectId, String street, Long organizationId) {
        return sqlSession().selectList(STREET_NS + ".selectStreetIdsByDistrict",
                new HashMap<String, Object>(){{
                    put("street", street);
                    put("cityId", cityObjectId);
                    put("organizationId", organizationId);
                }});
    }

    public List<Long> getStreetObjectIdsByBuilding(Long cityId, String street, String buildingNumber, String buildingCorp) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("street", street);
        params.put("cityId", cityId);
        String preparedNumber = BuildingNumberConverter.convert(buildingNumber);
        params.put("number", preparedNumber == null ? "" : preparedNumber);
        String preparedCorp = removeWhiteSpaces(buildingCorp);
        params.put("corp", Strings.isEmpty(preparedCorp) ? null : preparedCorp);

        return sqlSession().selectList(STREET_NS + ".selectStreetIdsByBuilding", params);
    }

    public Long getStreetIdByCode(String code){
        return sqlSession().selectOne(STREET_NS + ".selectStreetIdByCode", code);
    }

}
