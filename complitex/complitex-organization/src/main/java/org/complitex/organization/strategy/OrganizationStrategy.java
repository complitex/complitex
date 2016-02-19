package org.complitex.organization.strategy;

import com.google.common.collect.*;
import org.apache.commons.collections4.map.HashedMap;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.util.string.Strings;
import org.complitex.common.entity.*;
import org.complitex.common.exception.DeleteException;
import org.complitex.common.mybatis.SqlSessionFactoryBean;
import org.complitex.common.service.exception.ServiceRuntimeException;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.naming.*;
import javax.sql.DataSource;
import java.util.*;
import java.util.stream.Collectors;

public abstract class OrganizationStrategy extends TemplateStrategy implements IOrganizationStrategy {
    private Logger log = LoggerFactory.getLogger(OrganizationStrategy.class);

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
    public List<Long> getColumnAttributeTypeIds() {
        return Lists.newArrayList(NAME, CODE, USER_ORGANIZATION_PARENT);
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

    @Override
    public boolean canPropagatePermissions(DomainObject organization) {
        return isUserOrganization(organization) && organization.getObjectId() != null
                && !getTreeChildrenOrganizationIds(organization.getObjectId()).isEmpty();
    }

    @Override
    public boolean isUserOrganization(DomainObject organization) {
        return getOrganizationTypeIds(organization).contains(OrganizationTypeStrategy.USER_ORGANIZATION_TYPE);
    }

    protected List<Long> getOrganizationTypeIds(DomainObject organization) {
        return organization.getAttributes(ORGANIZATION_TYPE).stream()
                .map(Attribute::getValueId)
                .collect(Collectors.toList());
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

//    @Override
//    public void replaceChildrenPermissions(Long parentId, Set<Long> subjectIds) {
//        for (PermissionInfo childPermissionInfo : getTreeChildrenPermissionInfo(parentId)) {
//
//            Set<Long> childSubjectIds = Sets.newHashSet(subjectIds);
//            if (!childSubjectIds.contains(PermissionBean.VISIBLE_BY_ALL_PERMISSION_ID)) {
//                childSubjectIds.add(childPermissionInfo.getId());
//            }
//
//            replaceObjectPermissions(childPermissionInfo, childSubjectIds);
//        }
//    }

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
    public IValidator getValidator() {
        return new OrganizationValidator(stringLocaleBean.getSystemLocale());
    }

    @Override
    public Class<? extends AbstractComplexAttributesPanel> getComplexAttributesPanelAfterClass() {
        return OrganizationEditComponent.class;
    }

    @Override
    public void insert(DomainObject organization, Date insertDate) {
        organization.setObjectId(sequenceBean.nextId(getEntityName()));

        if (!organization.getSubjectIds().contains(PermissionBean.VISIBLE_BY_ALL_PERMISSION_ID)) {
            organization.getSubjectIds().add(organization.getObjectId());
        }

        organization.setPermissionId(getNewPermissionId(organization.getSubjectIds()));
        insertDomainObject(organization, insertDate);

        for (Attribute attribute : organization.getAttributes()) {
            attribute.setObjectId(organization.getObjectId());
            attribute.setStartDate(insertDate);
            insertAttribute(attribute);
        }

        changeDistrictPermissions(organization);
    }

    @Override
    public void update(DomainObject oldObject, DomainObject newObject, Date updateDate) {
        changeDistrictPermissions(oldObject, newObject);

        super.update(oldObject, newObject, updateDate);
    }

    @Override
    public List<? extends DomainObject> getList(DomainObjectFilter example) {
        if (example.getObjectId() != null && example.getObjectId() <= 0) {
            return Collections.emptyList();
        }

        example.setEntityName(getEntityName());
        if (!example.isAdmin()) {
            prepareExampleForPermissionCheck(example);
        }
        extendOrderBy(example);

        List<DomainObject> organizations = sqlSession().selectList(ORGANIZATION_NS + ".selectOrganizations", example);

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
    public List<? extends DomainObject> getUserOrganizations(Locale locale, Long... excludeOrganizationsId) {
        DomainObjectFilter example = new DomainObjectFilter();
        example.addAdditionalParam(ORGANIZATION_TYPE_PARAMETER, ImmutableList.of(OrganizationTypeStrategy.USER_ORGANIZATION_TYPE));
        if (locale != null) {
            example.setOrderByAttributeTypeId(NAME);
            example.setLocaleId(stringLocaleBean.convert(locale).getId());
            example.setAsc(true);
        }
        configureFilter(example, ImmutableMap.<String, Long>of(), null);
        List<? extends DomainObject> userOrganizations = getList(example);

        if (excludeOrganizationsId == null) {
            return userOrganizations;
        }

        List<DomainObject> finalUserOrganizations = Lists.newArrayList();

        Set<Long> excludeSet = Sets.newHashSet(excludeOrganizationsId);

        for (DomainObject userOrganization : userOrganizations) {
            if (!excludeSet.contains(userOrganization.getObjectId())) {
                finalUserOrganizations.add(userOrganization);
            }
        }

        return finalUserOrganizations;
    }

    @Override
    public Set<Long> getTreeChildrenOrganizationIds(Long parentOrganizationId) {
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
    public void changeChildrenActivity(Long parentId, boolean enable) {
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
    protected void deleteChecks(Long objectId, Locale locale) throws DeleteException {
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
    public String getCode(Long organizationId) {
        DomainObject organization = getDomainObject(organizationId, true);
        return organization != null ? getCode(organization) : null;
    }

    @Override
    public Long getObjectId(String externalId) {
        return sqlSession().selectOne(ORGANIZATION_NS + ".selectOrganizationExternalId", externalId);
    }

    public Long getObjectIdByCode(String code) {
        return sqlSession().selectOne(ORGANIZATION_NS + ".selectOrganizationObjectIdByCode", code);
    }

    public Long getObjectIdByEdrpou(String edrpou) {
        return sqlSession().selectOne(ORGANIZATION_NS + ".selectOrganizationObjectIdByEdrpou", edrpou);
    }

    @Override
    public List<? extends DomainObject> getOrganizations(Long... types) {
        return getList(new DomainObjectFilter().addAdditionalParam(ORGANIZATION_TYPE_PARAMETER, types));
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

    /**
     * Finds remote jdbc data sources.
     * @param currentDataSource Current data source.
     * @return Remote jdbc data sources.
     */
    public List<RemoteDataSource> findRemoteDataSources(String currentDataSource) {
        final String JDBC_PREFIX = "jdbc";
        final String GLASSFISH_INTERNAL_SUFFIX = "__pm";
        final Set<String> PREDEFINED_DATA_SOURCES = ImmutableSet.of("sample", "__TimerPool", "__default");

        Set<RemoteDataSource> remoteDataSources = Sets.newTreeSet(new Comparator<RemoteDataSource>() {

            @Override
            public int compare(RemoteDataSource o1, RemoteDataSource o2) {
                return o1.getDataSource().compareTo(o2.getDataSource());
            }
        });

        boolean currentDataSourceEnabled = false;

        try {
            Context context = new InitialContext();
            final NamingEnumeration<NameClassPair> resources = context.list(JDBC_PREFIX);
            if (resources != null) {
                while (resources.hasMore()) {
                    final NameClassPair nc = resources.next();
                    if (nc != null) {
                        final String name = nc.getName();
                        if (!Strings.isEmpty(name) && !name.endsWith(GLASSFISH_INTERNAL_SUFFIX)
                                && !PREDEFINED_DATA_SOURCES.contains(name)) {
                            final String fullDataSource = JDBC_PREFIX + "/" + name;
                            Object jndiObject = null;
                            try {
                                jndiObject = context.lookup(fullDataSource);
                            } catch (NamingException e) {
                            }

                            if (jndiObject instanceof DataSource) {
                                boolean current = false;
                                if (fullDataSource.equals(currentDataSource)) {
                                    currentDataSourceEnabled = true;
                                    current = true;
                                }
                                remoteDataSources.add(new RemoteDataSource(fullDataSource, current));
                            }
                        }

                    }
                }
            }
        } catch (NamingException e) {
            log.error("", e);
        }

        if (!currentDataSourceEnabled && !Strings.isEmpty(currentDataSource)) {
            remoteDataSources.add(new RemoteDataSource(currentDataSource, true, false));
        }

        return Lists.newArrayList(remoteDataSources);
    }

    /**
     * Figures out data source of calculation center.
     *
     * @param billingId Calculation center's id
     * @return Calculation center's data source
     */
    public String getDataSource(Long billingId) {
        return getDomainObject(billingId, true).getStringValue(DATA_SOURCE);
    }

    public Long getBillingId(Long userOrganizationId, Long serviceId){
        DomainObject userOrganization = getDomainObject(userOrganizationId);

        if (userOrganization != null){
            Long serviceAttributeId = serviceId != null ? userOrganization.getAttributes(SERVICE).stream()
                    .filter(a -> serviceId.equals(a.getValueId()))
                    .findAny()
                    .orElseThrow(() -> new ServiceRuntimeException("dataSource not found"))
                    .getAttributeId() : 1L;

            Long billingId = userOrganization.getAttribute(BILLING, serviceAttributeId).getValueId();

            if(billingId != null){
                return billingId;
            }else {
                throw new ServiceRuntimeException("МН не найдет для организации {0}",
                        userOrganization.getStringValue(OrganizationStrategy.NAME));
            }
        }else {
            throw new ServiceRuntimeException("Организация не найдена по идентификатору {0}", userOrganizationId);
        }
    }

    public Map<Long, Set<Long>> getBillingServices(Long userOrganizationId){
        DomainObject userOrganization = getDomainObject(userOrganizationId);

        List<Attribute> services = userOrganization.getAttributes(SERVICE);
        List<Attribute> billing = userOrganization.getAttributes(BILLING);

        Map<Long, Set<Long>> map = new HashedMap<>();

        for (int i = 0; i < services.size(); ++i){
            Long billingId = billing.get(i).getValueId();

            Set<Long> serviceIds = map.get(billingId);

            if (serviceIds == null){
                serviceIds = new HashSet<>();
                map.put(billingId, serviceIds);
            }

            serviceIds.add(services.get(i).getValueId());
        }

        return map;
    }

    public Set<Long> getServices(Long userOrganizationId){
        return getDomainObject(userOrganizationId).getValueIds(SERVICE);
    }

    public Long getBillingId(Long userOrganizationId){
        return getBillingId(userOrganizationId, null);
    }

    public String getDataSource(Long userOrganizationId, Long serviceId){
        return getDataSource(getBillingId(userOrganizationId, serviceId));
    }

    public String getDataSourceByUserOrganizationId(Long userOrganizationId){
        return getDataSource(getBillingId(userOrganizationId, null));
    }

    @Override
    public List<? extends DomainObject> getAllOuterOrganizations(Locale locale) {
        return null;
    }
}
