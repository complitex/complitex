package org.complitex.keconnection.organization.strategy;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.complitex.common.converter.BooleanConverter;
import org.complitex.common.entity.Attribute;
import org.complitex.common.entity.AttributeType;
import org.complitex.common.entity.DomainObject;
import org.complitex.common.entity.DomainObjectFilter;
import org.complitex.common.strategy.StringCultureBean;
import org.complitex.common.strategy.StringLocaleBean;
import org.complitex.common.strategy.organization.IOrganizationStrategy;
import org.complitex.common.util.StringCultures;
import org.complitex.common.web.component.domain.AbstractComplexAttributesPanel;
import org.complitex.keconnection.organization.strategy.entity.KeOrganization;
import org.complitex.keconnection.organization.strategy.web.edit.KeOrganizationEditComponent;
import org.complitex.keconnection.organization.strategy.web.list.OrganizationList;
import org.complitex.keconnection.organization_type.strategy.KeConnectionOrganizationTypeStrategy;
import org.complitex.organization.strategy.OrganizationStrategy;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static org.complitex.common.util.DateUtil.addMonth;
import static org.complitex.common.util.DateUtil.getCurrentDate;

@Stateless(name = IOrganizationStrategy.BEAN_NAME)
public class KeOrganizationStrategy extends OrganizationStrategy {
    public final static String KECONNECTION_ORGANIZATION_STRATEGY_NAME =  IOrganizationStrategy.BEAN_NAME;

    /**
     * Flag of organization being performer.
     */
    public final static long PERFORMER = 921;
    /**
     * Flag of readiness to close operating month.
     */
    public final static long READY_CLOSE_OPER_MONTH = 922;
    /**
     * Itself organization instance id.
     */
    public final static long ITSELF_ORGANIZATION_OBJECT_ID = 0;
    /**
     * КИЕВЭНЕРГО
     */
    public final static long KE_ORGANIZATION_OBJECT_ID = 1;

    private static final String NS = KeOrganizationStrategy.class.getName();

    private static final List<Long> CUSTOM_ATTRIBUTE_TYPES = ImmutableList.of(READY_CLOSE_OPER_MONTH, DATA_SOURCE);
    public static final String PARENT_SHORT_NAME_FILTER = "parentShortName";

    @EJB
    private StringLocaleBean stringLocaleBean;

    @EJB
    private StringCultureBean stringBean;

    @Override
    public PageParameters getEditPageParams(Long objectId, Long parentId, String parentEntity) {
        PageParameters pageParameters = super.getEditPageParams(objectId, parentId, parentEntity);
        pageParameters.set(STRATEGY, KECONNECTION_ORGANIZATION_STRATEGY_NAME);
        return pageParameters;
    }

    @Override
    public PageParameters getHistoryPageParams(Long objectId) {
        PageParameters pageParameters = super.getHistoryPageParams(objectId);
        pageParameters.set(STRATEGY, KECONNECTION_ORGANIZATION_STRATEGY_NAME);
        return pageParameters;
    }

    @Override
    public PageParameters getListPageParams() {
        return new PageParameters();
    }

    @Override
    public Class<? extends WebPage> getListPage() {
        return OrganizationList.class;
    }

    public List<KeOrganization> getAllServicingOrganizations(Locale locale) {
        DomainObjectFilter example = new DomainObjectFilter();
        example.addAdditionalParam(ORGANIZATION_TYPE_PARAMETER,
                ImmutableList.of(KeConnectionOrganizationTypeStrategy.SERVICING_ORGANIZATION_TYPE));
        if (locale != null) {
            example.setOrderByAttributeTypeId(NAME);
            example.setLocaleId(stringLocaleBean.convert(locale).getId());
            example.setAsc(true);
        }
        configureFilter(example, ImmutableMap.<String, Long>of(), null);
        return getList(example);
    }

    public String displayShortName(Long organizationId, Locale locale) {
        DomainObject domainObject = getDomainObject(organizationId, true);

        if (domainObject != null) {
            return domainObject.getStringValue(SHORT_NAME, locale);
        }

        return "";
    }

    protected void extendOrderBy(DomainObjectFilter example) {
        super.extendOrderBy(example);
        if (example.getOrderByAttributeTypeId() != null
                && example.getOrderByAttributeTypeId().equals(CODE)) {
            example.setOrderByNumber(true);
        }
    }

    public DomainObject getItselfOrganization() {
        return getDomainObject(ITSELF_ORGANIZATION_OBJECT_ID, true);
    }

    @Override
    public List<KeOrganization> getAllOuterOrganizations(Locale locale) {
        DomainObjectFilter example = new DomainObjectFilter();

        if (locale != null) {
            example.setOrderByAttributeTypeId(NAME);
            example.setLocaleId(stringLocaleBean.convert(locale).getId());
            example.setAsc(true);
        }

        example.addAdditionalParam(ORGANIZATION_TYPE_PARAMETER,
                ImmutableList.of(KeConnectionOrganizationTypeStrategy.SERVICE_PROVIDER,
                    KeConnectionOrganizationTypeStrategy.SERVICING_ORGANIZATION_TYPE,
                    KeConnectionOrganizationTypeStrategy.CALCULATION_MODULE));
        configureFilter(example, ImmutableMap.<String, Long>of(), null);

        return getList(example);
    }
    @Override
    public Class<? extends AbstractComplexAttributesPanel> getComplexAttributesPanelAfterClass() {
        return KeOrganizationEditComponent.class;
    }

    @Override
    public DomainObject getDomainObject(Long id, boolean runAsAdmin) {
        DomainObject object = super.getDomainObject(id, runAsAdmin);

        if (object == null) {
            return null;
        }

        KeOrganization organization = new KeOrganization(object);

        loadOperatingMonthDate(organization);

        return organization;
    }


    @Override
    public List<KeOrganization> getList(DomainObjectFilter example) {
        if (example.getLocaleId() == null){
            example.setLocaleId(-1L);
        }

        if (example.getObjectId() != null && example.getObjectId() <= 0) {
            return Collections.emptyList();
        }

        example.setEntityName(getEntityName());
        if (!example.isAdmin()) {
            prepareExampleForPermissionCheck(example);
        }
        extendOrderBy(example);

        setupFindOperationParameters(example);
        List<KeOrganization> organizations = sqlSession().selectList(NS + ".selectOrganizations", example);

        for (KeOrganization organization : organizations) {
            loadAttributes(organization);
            //load subject ids
            organization.setSubjectIds(loadSubjects(organization.getPermissionId()));
            //load operating month date
            loadOperatingMonthDate(organization);
        }
        return organizations;
    }

    private void setupFindOperationParameters(DomainObjectFilter example) {
        //set up attribute type id parameters:
        example.addAdditionalParam("parentAT", USER_ORGANIZATION_PARENT);
        example.addAdditionalParam("organizationShortNameAT", SHORT_NAME);
    }


    @Override
    public Long getCount(DomainObjectFilter example) {
        if (example.getObjectId() != null && example.getObjectId() <= 0) {
            return 0L;
        }
        example.setEntityName(getEntityName());
        prepareExampleForPermissionCheck(example);
        setupFindOperationParameters(example);

        return sqlSession().selectOne(NS + ".selectOrganizationCount", example);
    }

    private void loadOperatingMonthDate(KeOrganization organization) {
        organization.setOperatingMonthDate(getOperatingMonthDate(organization.getObjectId()));
    }

    public Date getOperatingMonthDate(long organizationId) {
        return sqlSession().selectOne(NS + ".findOperatingMonthDate", organizationId);
    }

    public Date getMinOperatingMonthDate(long organizationId) {
        return sqlSession().selectOne(NS + ".findMinOperatingMonthDate", organizationId);
    }

    @Override
    public boolean isSimpleAttributeType(AttributeType attributeType) {
        if (CUSTOM_ATTRIBUTE_TYPES.contains(attributeType.getId())) {
            return false;
        }

        return super.isSimpleAttributeType(attributeType);
    }

    @Override
    protected void fillAttributes(DomainObject object) {
        super.fillAttributes(object);

        for (long attributeTypeId : CUSTOM_ATTRIBUTE_TYPES) {
            if (object.getAttribute(attributeTypeId).getStringCultures() == null) {
                object.getAttribute(attributeTypeId).setStringCultures(StringCultures.newStringCultures());
            }
        }
    }

    @Override
    protected void loadStringCultures(List<Attribute> attributes) {
        super.loadStringCultures(attributes);

        for (Attribute attribute : attributes) {
            if (CUSTOM_ATTRIBUTE_TYPES.contains(attribute.getAttributeTypeId())) {
                if (attribute.getValueId() != null) {
                    loadStringCultures(attribute);
                } else {
                    attribute.setStringCultures(StringCultures.newStringCultures());
                }
            }
        }
    }


    @Override
    public DomainObject getHistoryObject(Long objectId, Date date) {
        DomainObject object = super.getHistoryObject(objectId, date);

        if (object == null) {
            return null;
        }

        KeOrganization organization = new KeOrganization(object);
        loadOperatingMonthDate(organization);

        return organization;
    }


    public void setReadyCloseOperatingMonthFlag(KeOrganization organization) {
        organization.setStringValue(READY_CLOSE_OPER_MONTH, new BooleanConverter().toString(Boolean.TRUE));
        update(getDomainObject(organization.getObjectId(), true), organization, getCurrentDate());
    }


    public void closeOperatingMonth(KeOrganization organization) {
        organization.setStringValue(READY_CLOSE_OPER_MONTH, new BooleanConverter().toString(Boolean.FALSE));
        update(getDomainObject(organization.getObjectId(), true), organization, getCurrentDate());

        sqlSession().insert(NS + ".insertOperatingMonth",
                ImmutableMap.of("organizationId", organization.getObjectId(),
                        "beginOm", addMonth(organization.getOperatingMonthDate(), 1),
                        "updated", getCurrentDate()));
    }
}
