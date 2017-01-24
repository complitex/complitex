package org.complitex.osznconnection.organization.strategy;

import com.google.common.collect.ImmutableList;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.Strings;
import org.complitex.address.strategy.district.DistrictStrategy;
import org.complitex.common.entity.*;
import org.complitex.common.exception.ServiceRuntimeException;
import org.complitex.common.strategy.StringCultureBean;
import org.complitex.common.strategy.StringLocaleBean;
import org.complitex.common.strategy.organization.IOrganizationStrategy;
import org.complitex.common.util.AttributeUtil;
import org.complitex.common.util.StringCultures;
import org.complitex.common.web.component.domain.AbstractComplexAttributesPanel;
import org.complitex.common.web.component.domain.validate.IValidator;
import org.complitex.correction.entity.OrganizationCorrection;
import org.complitex.correction.service.OrganizationCorrectionBean;
import org.complitex.organization.strategy.OrganizationStrategy;
import org.complitex.osznconnection.organization.strategy.web.edit.OsznOrganizationEditComponent;
import org.complitex.osznconnection.organization.strategy.web.edit.OsznOrganizationValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import java.util.List;
import java.util.Locale;

import static org.complitex.osznconnection.organization_type.strategy.OsznOrganizationTypeStrategy.BILLING_TYPE;
import static org.complitex.osznconnection.organization_type.strategy.OsznOrganizationTypeStrategy.SERVICING_ORGANIZATION_TYPE;

@Stateless(name = IOrganizationStrategy.BEAN_NAME)
public class OsznOrganizationStrategy extends OrganizationStrategy {
    /**
     * Load payments/benefits directory. 
     */
    public final static long LOAD_PAYMENT_BENEFIT_FILES_DIR = 915;

    /**
     * Save payments/benefits directory. 
     */
    public final static long SAVE_PAYMENT_BENEFIT_FILES_DIR = 916;

    /**
     * Load actual payments directory. 
     */
    public final static long LOAD_ACTUAL_PAYMENT_DIR = 917;

    /**
     * Save actual payments directory. 
     */
    public final static long SAVE_ACTUAL_PAYMENT_DIR = 918;

    /**
     * Load subsidies directory. 
     */
    public final static long LOAD_SUBSIDY_DIR = 919;

    /**
     * Save subsidies directory. 
     */
    public final static long SAVE_SUBSIDY_DIR = 920;

    /**
     * Load dwelling characteristics directory. 
     */
    public final static long LOAD_DWELLING_CHARACTERISTICS_DIR = 921;

    /**
     * Save dwelling characteristics directory. 
     */
    public final static long SAVE_DWELLING_CHARACTERISTICS_DIR = 922;

    /**
     * Load facility service type directory. 
     */
    public final static long LOAD_FACILITY_SERVICE_TYPE_DIR = 923;

    /**
     * Save facility service type directory. 
     */
    public final static long SAVE_FACILITY_SERVICE_TYPE_DIR = 924;

    /**
     * References directory. 
     */
    public final static long REFERENCES_DIR = 925;

    /**
     * EDRPOU(ЕДРПОУ). 
     */
    public final static long EDRPOU = 926;

    /**
     * Root directory for loading and saving request files. 
     */
    public final static long ROOT_REQUEST_FILE_DIRECTORY = 927;

    /**
     * Save facility form2 directory. 
     */
    public final static long SAVE_FACILITY_FORM2_DIR = 928;

    public final static long SAVE_FACILITY_LOCAL_DIR = 929;

    public final static long ROOT_EXPORT_DIRECTORY = 930;

    /**
     * Load dwelling characteristics directory.
     */
    public final static long LOAD_PRIVILEGE_PROLONGATION_DIR = 931;

    private final Logger log = LoggerFactory.getLogger(OsznOrganizationStrategy.class);
    public static final String OSZN_ORGANIZATION_STRATEGY_NAME = IOrganizationStrategy.BEAN_NAME;
    private static final String RESOURCE_BUNDLE = OsznOrganizationStrategy.class.getName();

    public static final List<Long> LOAD_SAVE_FILE_DIR_SUBSIDY_ATTRIBUTES =
            ImmutableList.of(LOAD_PAYMENT_BENEFIT_FILES_DIR, SAVE_PAYMENT_BENEFIT_FILES_DIR,
                    LOAD_ACTUAL_PAYMENT_DIR, SAVE_ACTUAL_PAYMENT_DIR, LOAD_SUBSIDY_DIR, SAVE_SUBSIDY_DIR);

    public static final List<Long> LOAD_SAVE_FILE_DIR_PRIVILEGES_ATTRIBUTES =
            ImmutableList.of(LOAD_DWELLING_CHARACTERISTICS_DIR, SAVE_DWELLING_CHARACTERISTICS_DIR,
                    LOAD_FACILITY_SERVICE_TYPE_DIR, SAVE_FACILITY_SERVICE_TYPE_DIR, SAVE_FACILITY_FORM2_DIR,
                    SAVE_FACILITY_LOCAL_DIR, LOAD_PRIVILEGE_PROLONGATION_DIR);

    private static final List<Long> CUSTOM_ATTRIBUTE_TYPES = ImmutableList.<Long>builder().
            add(DATA_SOURCE).
            addAll(LOAD_SAVE_FILE_DIR_SUBSIDY_ATTRIBUTES).
            addAll(LOAD_SAVE_FILE_DIR_PRIVILEGES_ATTRIBUTES).
            add(REFERENCES_DIR).
            add(EDRPOU).
            add(ROOT_REQUEST_FILE_DIRECTORY).
            add(ROOT_EXPORT_DIRECTORY).
            build();

    private static final List<Long> ATTRIBUTE_TYPES_WITH_CUSTOM_STRING_PROCESSING =
            ImmutableList.<Long>builder().
                    add(DATA_SOURCE).
                    addAll(LOAD_SAVE_FILE_DIR_SUBSIDY_ATTRIBUTES).
                    addAll(LOAD_SAVE_FILE_DIR_PRIVILEGES_ATTRIBUTES).
                    add(REFERENCES_DIR).
                    add(ROOT_REQUEST_FILE_DIRECTORY).
                    add(ROOT_EXPORT_DIRECTORY).
                    build();

    @EJB
    private StringLocaleBean stringLocaleBean;

    @EJB
    private StringCultureBean stringBean;

    @EJB
    private OrganizationCorrectionBean organizationCorrectionBean;

    @EJB
    private DistrictStrategy districtStrategy;

    @Override
    public IValidator getValidator() {
        return new OsznOrganizationValidator(stringLocaleBean.getSystemLocale());
    }

    @Override
    public Class<? extends AbstractComplexAttributesPanel> getComplexAttributesPanelAfterClass() {
        return OsznOrganizationEditComponent.class;
    }

    @Override
    public PageParameters getEditPageParams(Long objectId, Long parentId, String parentEntity) {
        PageParameters pageParameters = super.getEditPageParams(objectId, parentId, parentEntity);
        pageParameters.set(STRATEGY, OSZN_ORGANIZATION_STRATEGY_NAME);
        return pageParameters;
    }

    @Override
    public PageParameters getHistoryPageParams(Long objectId) {
        PageParameters pageParameters = super.getHistoryPageParams(objectId);
        pageParameters.set(STRATEGY, OSZN_ORGANIZATION_STRATEGY_NAME);
        return pageParameters;
    }

    @Override
    public PageParameters getListPageParams() {
        PageParameters pageParameters = super.getListPageParams();
        pageParameters.set(STRATEGY, OSZN_ORGANIZATION_STRATEGY_NAME);
        return pageParameters;
    }

    @Override
    public List<? extends DomainObject> getAllOuterOrganizations(Locale locale) {
        return getOrganizations(SERVICING_ORGANIZATION_TYPE, BILLING_TYPE, SERVICING_ORGANIZATION_TYPE);
    }

    @Override
    public boolean isSimpleAttributeType(AttributeType attributeType) {
        return !CUSTOM_ATTRIBUTE_TYPES.contains(attributeType.getId()) &&
                super.isSimpleAttributeType(attributeType);
    }

    @Override
    protected void fillAttributes(String dataSource, DomainObject object) {
        super.fillAttributes(null, object);

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
    protected void insertAttribute(Attribute attribute) {
        if (CUSTOM_ATTRIBUTE_TYPES.contains(attribute.getAttributeTypeId())){
            Long generatedStringId = insertStrings(attribute.getAttributeTypeId(), attribute.getStringCultures());
            attribute.setValueId(generatedStringId);
        }

        super.insertAttribute(attribute);
    }

    /**
     * Returns relative path to request files storage.
     * @param osznId Oszn's id.
     * @param fileTypeAttributeTypeId Attribute type id corresponding desired file type.
     * @return Relative path to request files storage.
     */
    public String getRelativeRequestFilesPath(long osznId, long fileTypeAttributeTypeId) {
        DomainObject oszn = getDomainObject(osznId, true);
        return AttributeUtil.getStringValue(oszn, fileTypeAttributeTypeId);
    }

    /**
     * Returns root directory to request files storage.
     * @param userOrganizationId User organization's id.
     * @return Root directory to request files storage.
     */
    public String getRootRequestFilesStoragePath(long userOrganizationId) {
        DomainObject userOrganization = getDomainObject(userOrganizationId, true);
        return AttributeUtil.getStringValue(userOrganization, ROOT_REQUEST_FILE_DIRECTORY);
    }

    public String getRootExportStoragePath(long userOrganizationId) {
        DomainObject userOrganization = getDomainObject(userOrganizationId, true);
        return AttributeUtil.getStringValue(userOrganization, ROOT_EXPORT_DIRECTORY);
    }

    @Override
    protected Long insertStrings(Long attributeTypeId, List<StringCulture> strings) {
        return ATTRIBUTE_TYPES_WITH_CUSTOM_STRING_PROCESSING.contains(attributeTypeId)
                ? stringBean.save(strings, getEntityName(), false)
                : stringBean.save(strings, getEntityName(), true);
    }

    public DomainObject getBalanceHolder(Long organizationId){
        DomainObject organization = getDomainObject(organizationId, true);

        Attribute parent = organization.getAttribute(USER_ORGANIZATION_PARENT);

        return parent == null || parent.getValueId() == null ? organization : getBalanceHolder(parent.getValueId());
    }

    @Override
    public String displayAttribute(Attribute attribute, Locale locale) {
        if (attribute != null && attribute.getAttributeTypeId().equals(USER_ORGANIZATION_PARENT)){
            return displayNameAndCode(attribute.getValueId(), locale);
        }

        return super.displayAttribute(attribute, locale);
    }

    public String getServiceProviderCode(String edrpou, Long organizationId, Long userOrganizationId){
        Long serviceProviderId = getServiceProviderId(edrpou, organizationId, userOrganizationId);

        if (serviceProviderId != null){
            return getDomainObject(serviceProviderId).getStringValue(OsznOrganizationStrategy.CODE);
        }else {
            throw new ServiceRuntimeException("ПУ не найден по ЕДРПОУ {0}", edrpou);
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Long getServiceProviderId(String edrpou, Long organizationId, Long userOrganizationId){
        Long serviceProviderId = null;

        List<OrganizationCorrection> list = organizationCorrectionBean.getOrganizationCorrections(
                FilterWrapper.of(new OrganizationCorrection(null, null, edrpou, organizationId, userOrganizationId, null)));

        if (!list.isEmpty()){
            serviceProviderId = list.get(0).getObjectId();
        }

        if (serviceProviderId == null){
            serviceProviderId = getObjectIdByEdrpou(edrpou);
        }

        return serviceProviderId;
    }

    public String getEdrpou(Long organizationId, Long userOrganizationId){
        List<OrganizationCorrection> list = organizationCorrectionBean.getOrganizationCorrections(
                FilterWrapper.of(new OrganizationCorrection(null, userOrganizationId, null,
                        organizationId, userOrganizationId, null)));

        if (!list.isEmpty()){
            return list.get(0).getCorrection();
        }

        return null;
    }

    public String getEdrpou(Long serviceProviderId, Long organizationId, Long userOrganizationId){
        DomainObject serviceProvider = getDomainObject(serviceProviderId);

        if (serviceProvider != null && !Strings.isEmpty(serviceProvider.getStringValue(EDRPOU))){
            return serviceProvider.getStringValue(EDRPOU);
        }

        List<OrganizationCorrection> list = organizationCorrectionBean.getOrganizationCorrections(
                FilterWrapper.of(new OrganizationCorrection(null, userOrganizationId, null,
                        organizationId, userOrganizationId, null)));

        if (!list.isEmpty()){
            return list.get(0).getCorrection();
        }

        return null;
    }

    public String getDistrict(Long organizationId){
        DomainObject organization = getDomainObject(organizationId);

        if (organization != null && organization.getAttribute(IOrganizationStrategy.DISTRICT) != null){
            DomainObject districtObject = districtStrategy.getDomainObject(organization.getAttribute(IOrganizationStrategy.DISTRICT).getValueId());

            if (districtObject != null){
                return districtObject.getStringValue(DistrictStrategy.NAME);
            }
        }

        return null;
    }
}
