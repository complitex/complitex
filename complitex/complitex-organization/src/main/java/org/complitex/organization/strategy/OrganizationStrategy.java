package org.complitex.organization.strategy;

import com.google.common.collect.*;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.util.string.Strings;
import org.complitex.common.entity.*;
import org.complitex.common.exception.DeleteException;
import org.complitex.common.mybatis.SqlSessionFactoryBean;
import org.complitex.common.strategy.*;
import org.complitex.common.strategy.organization.IOrganizationStrategy;
import org.complitex.common.util.AttributeUtil;
import org.complitex.common.util.Numbers;
import org.complitex.common.util.ResourceUtil;
import org.complitex.common.web.component.domain.AbstractComplexAttributesPanel;
import org.complitex.common.web.component.domain.validate.IValidator;
import org.complitex.organization.strategy.web.edit.OrganizationEdit;
import org.complitex.organization.strategy.web.edit.OrganizationEditComponent;
import org.complitex.organization.strategy.web.edit.OrganizationValidator;
import org.complitex.organization_type.strategy.OrganizationTypeStrategy;
import org.complitex.template.strategy.TemplateStrategy;
import org.complitex.template.web.security.SecurityRole;

import javax.ejb.EJB;
import java.util.*;

/**
 *
 * @author Artem
 */
public abstract class OrganizationStrategy<T extends DomainObject> extends TemplateStrategy implements IOrganizationStrategy<T> {
    public static final String ORGANIZATION_NS = OrganizationStrategy.class.getName();

    private static final String RESOURCE_BUNDLE = OrganizationStrategy.class.getName();

    @EJB
    private StringLocaleBean stringLocaleBean;

    @EJB
    private PermissionBean permissionBean;

    @EJB
    private SequenceBean sequenceBean;

    @EJB
    private StrategyFactory strategyFactory;

    @Override
    public String getEntityName() {
        return "organization";
    }

    @Override
    public List<Long> getListAttributeTypes() {
        return Lists.newArrayList(NAME, CODE, USER_ORGANIZATION_PARENT);
    }

    @Override
    public String displayDomainObject(DomainObject object, Locale locale) {
        return object.getStringValue(NAME, locale);
    }

    @Override
    public void configureExample(DomainObjectFilter example, Map<String, Long> ids, String searchTextInput) {
        if (!Strings.isEmpty(searchTextInput)) {
            AttributeFilter attrExample = example.getAttributeExample(NAME);
            if (attrExample == null) {
                attrExample = new AttributeFilter(NAME);
                example.addAttributeFilter(attrExample);
            }
            attrExample.setValue(searchTextInput);
        }
    }

    @Override
    public String getPluralEntityLabel(Locale locale) {
        return ResourceUtil.getString(RESOURCE_BUNDLE, getEntityName(), locale);
    }

    @Override
    public boolean canPropagatePermissions(DomainObject organization) {
        return isUserOrganization(organization) && organization.getObjectId() != null
                && !getTreeChildrenOrganizationIds(organization.getObjectId()).isEmpty();
    }

    @Override
    public boolean isUserOrganization(DomainObject organization) {
        List<Long> organizationTypeIds = getOrganizationTypeIds(organization);
        return organizationTypeIds != null && organizationTypeIds.contains(OrganizationTypeStrategy.USER_ORGANIZATION_TYPE);
    }

    protected List<Long> getOrganizationTypeIds(DomainObject organization) {
        List<Long> organizationTypeIds = Lists.newArrayList();
        List<Attribute> organizationTypeAttributes = organization.getAttributes(ORGANIZATION_TYPE);
        if (organizationTypeAttributes != null && !organizationTypeAttributes.isEmpty()) {
            for (Attribute attribute : organizationTypeAttributes) {
                if (attribute.getValueId() != null) {
                    organizationTypeIds.add(attribute.getValueId());
                }
            }
        }
        return organizationTypeIds;
    }

    @Override
    public void insert(DomainObject object, Date insertDate) {
        object.setObjectId(sequenceBean.nextId(getEntityName()));

        if (!object.getSubjectIds().contains(PermissionBean.VISIBLE_BY_ALL_PERMISSION_ID)) {
            object.getSubjectIds().add(object.getObjectId());
        }

        object.setPermissionId(getNewPermissionId(object.getSubjectIds()));
        insertDomainObject(object, insertDate);
        for (Attribute attribute : object.getAttributes()) {
            attribute.setObjectId(object.getObjectId());
            attribute.setStartDate(insertDate);
            insertAttribute(attribute);
        }

        changeDistrictPermissions(object);
    }

    protected void changeDistrictPermissions(DomainObject newOrganization) {
        if (isUserOrganization(newOrganization)) {
            Attribute districtAttribute = newOrganization.getAttribute(DISTRICT);
            Long districtId = districtAttribute.getValueId();
            if (districtId != null) {
                IStrategy districtStrategy = strategyFactory.getStrategy("district");

                DomainObject districtObject = districtStrategy.getDomainObject(districtId, false);
                if (districtObject != null) {
                    Set<Long> addSubjectIds = Sets.newHashSet(newOrganization.getObjectId());
                    districtStrategy.changePermissionsInDistinctThread(districtId, districtObject.getPermissionId(), addSubjectIds, null);
                }
            }
        }
    }

    @Override
    public void update(DomainObject oldObject, DomainObject newObject, Date updateDate) {
        super.update(oldObject, newObject, updateDate);
        changeDistrictPermissions(oldObject, newObject);
    }

    protected void changeDistrictPermissions(DomainObject oldOrganization, DomainObject newOrganization) {
        if (isUserOrganization(newOrganization)) {
            long organizationId = newOrganization.getObjectId();
            Set<Long> subjectIds = Sets.newHashSet(organizationId);
            Attribute oldDistrictAttribute = oldOrganization.getAttribute(DISTRICT);
            Attribute newDistrictAttribute = newOrganization.getAttribute(DISTRICT);
            Long oldDistrictId = oldDistrictAttribute != null ? oldDistrictAttribute.getValueId() : null;
            Long newDistrictId = newDistrictAttribute != null ? newDistrictAttribute.getValueId() : null;
            if (!Numbers.isEqual(oldDistrictId, newDistrictId)) {
                IStrategy districtStrategy = strategyFactory.getStrategy("district");

                //district reference has changed
                if (oldDistrictId != null) {
                    long oldDistrictPermissionId = districtStrategy.getDomainObject(oldDistrictId, true).getPermissionId();
                    districtStrategy.changePermissionsInDistinctThread(oldDistrictId, oldDistrictPermissionId, null, subjectIds);
                }

                if (newDistrictId != null) {
                    long newDistrictPermissionId = districtStrategy.getDomainObject(newDistrictId, true).getPermissionId();
                    districtStrategy.changePermissionsInDistinctThread(newDistrictId, newDistrictPermissionId, subjectIds, null);
                }
            }
        }
    }

    @Override
    public void updateAndPropagate(DomainObject oldObject, DomainObject newObject, Date updateDate) {
        super.updateAndPropagate(oldObject, newObject, updateDate);
        changeDistrictPermissions(oldObject, newObject);
    }

    @Override
    public void replaceChildrenPermissions(long parentId, Set<Long> subjectIds) {
        for (PermissionInfo childPermissionInfo : getTreeChildrenPermissionInfo(parentId)) {

            Set<Long> childSubjectIds = Sets.newHashSet(subjectIds);
            if (!childSubjectIds.contains(PermissionBean.VISIBLE_BY_ALL_PERMISSION_ID)) {
                childSubjectIds.add(childPermissionInfo.getId());
            }

            replaceObjectPermissions(childPermissionInfo, childSubjectIds);
        }
    }

    protected List<PermissionInfo> getTreeChildrenPermissionInfo(long parentId) {
        List<PermissionInfo> childrenPermissionInfo = sqlSession().selectList(ORGANIZATION_NS
                + ".selectOrganizationChildrenPermissionInfo", parentId);
        List<PermissionInfo> treeChildrenPermissionInfo = Lists.newArrayList(childrenPermissionInfo);
        for (PermissionInfo childPermissionInfo : childrenPermissionInfo) {
            treeChildrenPermissionInfo.addAll(getTreeChildrenPermissionInfo(childPermissionInfo.getId()));
        }
        return treeChildrenPermissionInfo;
    }

    @Override
    public void changePermissionsInDistinctThread(long objectId, long permissionId, Set<Long> addSubjectIds, Set<Long> removeSubjectIds) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void changePermissions(PermissionInfo objectPermissionInfo, Set<Long> addSubjectIds, Set<Long> removeSubjectIds) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IValidator getValidator() {
        return new OrganizationValidator(stringLocaleBean.getSystemLocale());
    }

    @Override
    public Class<? extends AbstractComplexAttributesPanel> getComplexAttributesPanelAfterClass() {
        return OrganizationEditComponent.class;
    }

    @Override
    public List<T> getList(DomainObjectFilter example) {
        if (example.getObjectId() != null && example.getObjectId() <= 0) {
            return Collections.emptyList();
        }

        example.setEntityName(getEntityName());
        if (!example.isAdmin()) {
            prepareExampleForPermissionCheck(example);
        }
        extendOrderBy(example);

        List<T> organizations = sqlSession().selectList(ORGANIZATION_NS + ".selectOrganizations", example);

        for (DomainObject object : organizations) {
            loadAttributes(object);
            //load subject ids
            object.setSubjectIds(loadSubjects(object.getPermissionId()));
        }

        return organizations;
    }

    @Override
    public Long getCount(DomainObjectFilter example) {
        if (example.getObjectId() != null && example.getObjectId() <= 0) {
            return 0L;
        }
        example.setEntityName(getEntityName());
        prepareExampleForPermissionCheck(example);

        return sqlSession().selectOne(ORGANIZATION_NS + ".selectOrganizationCount", example);
    }

    @Override
    public Long validateCode(Long id, String code) {
        List<Long> results = sqlSession().selectList(ORGANIZATION_NS + ".validateCode", code);
        for (Long result : results) {
            if (!result.equals(id)) {
                return result;
            }
        }
        return null;
    }

    @Override
    public Long validateName(Long id, String name, Locale locale) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("name", name);
        params.put("localeId", stringLocaleBean.convert(locale).getId());
        List<Long> results = sqlSession().selectList(ORGANIZATION_NS + ".validateName", params);
        for (Long result : results) {
            if (!result.equals(id)) {
                return result;
            }
        }
        return null;
    }

    @Override
    public List<T> getUserOrganizations(Locale locale, Long... excludeOrganizationsId) {
        DomainObjectFilter example = new DomainObjectFilter();
        example.addAdditionalParam(ORGANIZATION_TYPE_PARAMETER, ImmutableList.of(OrganizationTypeStrategy.USER_ORGANIZATION_TYPE));
        if (locale != null) {
            example.setOrderByAttributeTypeId(NAME);
            example.setLocaleId(stringLocaleBean.convert(locale).getId());
            example.setAsc(true);
        }
        configureExample(example, ImmutableMap.<String, Long>of(), null);
        List<T> userOrganizations = getList(example);

        if (excludeOrganizationsId == null) {
            return userOrganizations;
        }

        List<T> finalUserOrganizations = Lists.newArrayList();

        Set<Long> excludeSet = Sets.newHashSet(excludeOrganizationsId);

        for (T userOrganization : userOrganizations) {
            if (!excludeSet.contains(userOrganization.getObjectId())) {
                finalUserOrganizations.add(userOrganization);
            }
        }

        return finalUserOrganizations;
    }

    @Override
    public Set<Long> getTreeChildrenOrganizationIds(long parentOrganizationId) {
        List<Long> results = sqlSession().selectList(ORGANIZATION_NS + ".selectOrganizationChildrenObjectIds",
                parentOrganizationId);
        Set<Long> childrenIds = Sets.newHashSet(results);
        Set<Long> treeChildrenIds = Sets.newHashSet(childrenIds);

        for (Long childId : childrenIds) {
            treeChildrenIds.addAll(getTreeChildrenOrganizationIds(childId));
        }

        return Collections.unmodifiableSet(treeChildrenIds);
    }

    @Override
    public String[] getEditRoles() {
        return new String[]{SecurityRole.ORGANIZATION_MODULE_EDIT};
    }

    @Override
    public String[] getListRoles() {
        return new String[]{SecurityRole.ORGANIZATION_MODULE_VIEW};
    }

    @Override
    public void changeChildrenActivity(long parentId, boolean enable) {
        Set<Long> childrenIds = getTreeChildrenOrganizationIds(parentId);
        if (!childrenIds.isEmpty()) {
            updateChildrenActivity(childrenIds, !enable);
        }
    }

    private void updateChildrenActivity(Set<Long> childrenIds, boolean enabled) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("childrenIds", childrenIds);
        params.put("enabled", enabled);
        params.put("status", enabled ? StatusType.INACTIVE : StatusType.ACTIVE);

        sqlSession().update(ORGANIZATION_NS + ".updateChildrenActivity", params);
    }

    @Override
    protected void deleteChecks(long objectId, Locale locale) throws DeleteException {
        if (permissionBean.isOrganizationPermissionExists(getEntityName(), objectId)) {
            throw new DeleteException();
        }
        super.deleteChecks(objectId, locale);
    }

    @Override
    public Class<? extends WebPage> getEditPage() {
        return OrganizationEdit.class;
    }

    @Override
    public String getCode(DomainObject organization) {
        return AttributeUtil.getStringValue(organization, CODE);
    }

    @Override
    public String getCode(long organizationId) {
        DomainObject organization = getDomainObject(organizationId, true);
        return organization != null ? getCode(organization) : null;
    }

    @Override
    public Long getObjectId(String externalId) {
        return sqlSession().selectOne(ORGANIZATION_NS + ".selectOrganizationObjectId", externalId);
    }

    public Long getObjectIdByCode(String code) {
        return sqlSession().selectOne(ORGANIZATION_NS + ".selectOrganizationObjectIdByCode", code);
    }

    @Override
    public List<T> getAllOuterOrganizations(Locale locale) {
        return null;
    }

    @Override
    public List<T> getOrganizations(List<Long> types, Locale locale) {
        DomainObjectFilter example = new DomainObjectFilter();

        if (locale != null) {
            example.setOrderByAttributeTypeId(NAME);
            example.setLocaleId(stringLocaleBean.convert(locale).getId());
            example.setAsc(true);
        }

        example.addAdditionalParam(ORGANIZATION_TYPE_PARAMETER, types);

        configureExample(example, ImmutableMap.<String, Long>of(), null);

        return getList(example);
    }

    public String displayShortNameAndCode(DomainObject organization, Locale locale) {
        final String fullName = organization.getStringValue(NAME, locale);
        final String shortName = organization.getStringValue(SHORT_NAME, locale);
        final String code = getCode(organization);
        final String name = !com.google.common.base.Strings.isNullOrEmpty(shortName) ? shortName : fullName;
        return name + " (" + code + ")";
    }

    @Override
    public String displayShortNameAndCode(Long organizationId, Locale locale) {
        if (organizationId == null){
            return "";
        }

        return displayShortNameAndCode(getDomainObject(organizationId, true), locale);
    }

    @Override
    public void setSqlSessionFactoryBean(SqlSessionFactoryBean sqlSessionFactoryBean) {
        super.setSqlSessionFactoryBean(sqlSessionFactoryBean);
        stringLocaleBean.setSqlSessionFactoryBean(sqlSessionFactoryBean);
        permissionBean.setSqlSessionFactoryBean(sqlSessionFactoryBean);
        sequenceBean.setSqlSessionFactoryBean(sqlSessionFactoryBean);
    }
}
