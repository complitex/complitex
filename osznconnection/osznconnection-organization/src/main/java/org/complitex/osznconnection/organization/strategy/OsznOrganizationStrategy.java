package org.complitex.osznconnection.organization.strategy;

import com.google.common.collect.ImmutableList;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.complitex.common.entity.Attribute;
import org.complitex.common.entity.AttributeType;
import org.complitex.common.entity.DomainObject;
import org.complitex.common.entity.StringCulture;
import org.complitex.common.strategy.StringCultureBean;
import org.complitex.common.strategy.StringLocaleBean;
import org.complitex.common.strategy.organization.IOrganizationStrategy;
import org.complitex.common.util.AttributeUtil;
import org.complitex.common.util.StringCultures;
import org.complitex.common.web.component.domain.AbstractComplexAttributesPanel;
import org.complitex.common.web.component.domain.validate.IValidator;
import org.complitex.organization.entity.ServiceBilling;
import org.complitex.organization.strategy.OrganizationStrategy;
import org.complitex.osznconnection.organization.strategy.web.edit.OsznOrganizationEditComponent;
import org.complitex.osznconnection.organization.strategy.web.edit.OsznOrganizationValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.List;
import java.util.Locale;

import static org.complitex.osznconnection.organization_type.strategy.OsznOrganizationTypeStrategy.*;

/**
 *
 * @author Artem
 */
@Stateless(name = IOrganizationStrategy.BEAN_NAME)
public class OsznOrganizationStrategy extends OrganizationStrategy<DomainObject> {
    /**
     * References to associations between service provider types and calculation centres. It is user organization only attribute.
     */
    public final static long SERVICE_ASSOCIATIONS = 914;

    /**
     * Load payments/benefits directory. It is OSZN only attribute.
     */
    public final static long LOAD_PAYMENT_BENEFIT_FILES_DIR = 915;

    /**
     * Save payments/benefits directory. It is OSZN only attribute.
     */
    public final static long SAVE_PAYMENT_BENEFIT_FILES_DIR = 916;

    /**
     * Load actual payments directory. It is OSZN only attribute.
     */
    public final static long LOAD_ACTUAL_PAYMENT_DIR = 917;

    /**
     * Save actual payments directory. It is OSZN only attribute.
     */
    public final static long SAVE_ACTUAL_PAYMENT_DIR = 918;

    /**
     * Load subsidies directory. It is OSZN only attribute.
     */
    public final static long LOAD_SUBSIDY_DIR = 919;

    /**
     * Save subsidies directory. It is OSZN only attribute.
     */
    public final static long SAVE_SUBSIDY_DIR = 920;

    /**
     * Load dwelling characteristics directory. It is OSZN only attribute.
     */
    public final static long LOAD_DWELLING_CHARACTERISTICS_DIR = 921;

    /**
     * Save dwelling characteristics directory. It is OSZN only attribute.
     */
    public final static long SAVE_DWELLING_CHARACTERISTICS_DIR = 922;

    /**
     * Load facility service type directory. It is OSZN only attribute.
     */
    public final static long LOAD_FACILITY_SERVICE_TYPE_DIR = 923;

    /**
     * Save facility service type directory. It is OSZN only attribute.
     */
    public final static long SAVE_FACILITY_SERVICE_TYPE_DIR = 924;

    /**
     * References directory. It is OSZN only attribute.
     */
    public final static long REFERENCES_DIR = 925;

    /**
     * EDRPOU(ЕДРПОУ). It is user organization only attribute.
     */
    public final static long EDRPOU = 926;

    /**
     * Root directory for loading and saving request files. It is user organization only attribute.
     */
    public final static long ROOT_REQUEST_FILE_DIRECTORY = 927;

    /**
     * Save facility form2 directory. It is OSZN only attribute.
     */
    public final static long SAVE_FACILITY_FORM2_DIR = 928;

    public final static long ROOT_EXPORT_DIRECTORY = 930;


    /**
     * Itself organization instance id.
     */


    private final Logger log = LoggerFactory.getLogger(OsznOrganizationStrategy.class);
    public static final String OSZN_ORGANIZATION_STRATEGY_NAME = IOrganizationStrategy.BEAN_NAME;
    private static final String RESOURCE_BUNDLE = OsznOrganizationStrategy.class.getName();

    public static final List<Long> LOAD_SAVE_FILE_DIR_ATTRIBUTES =
            ImmutableList.of(LOAD_PAYMENT_BENEFIT_FILES_DIR, SAVE_PAYMENT_BENEFIT_FILES_DIR,
                    LOAD_ACTUAL_PAYMENT_DIR, SAVE_ACTUAL_PAYMENT_DIR, LOAD_SUBSIDY_DIR, SAVE_SUBSIDY_DIR,
                    LOAD_DWELLING_CHARACTERISTICS_DIR, SAVE_DWELLING_CHARACTERISTICS_DIR, REFERENCES_DIR,
                    LOAD_FACILITY_SERVICE_TYPE_DIR, SAVE_FACILITY_SERVICE_TYPE_DIR, SAVE_FACILITY_FORM2_DIR);

    private static final List<Long> CUSTOM_ATTRIBUTE_TYPES = ImmutableList.<Long>builder().
            add(DATA_SOURCE).
            addAll(LOAD_SAVE_FILE_DIR_ATTRIBUTES).
            add(EDRPOU).
            add(ROOT_REQUEST_FILE_DIRECTORY).
            add(ROOT_EXPORT_DIRECTORY).
            build();

    private static final List<Long> ATTRIBUTE_TYPES_WITH_CUSTOM_STRING_PROCESSING =
            ImmutableList.<Long>builder().
                    add(DATA_SOURCE).
                    addAll(LOAD_SAVE_FILE_DIR_ATTRIBUTES).
                    add(ROOT_REQUEST_FILE_DIRECTORY).
                    add(ROOT_EXPORT_DIRECTORY).
                    build();

    @EJB
    private StringLocaleBean stringLocaleBean;

    @EJB
    private StringCultureBean stringBean;

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
    public PageParameters getHistoryPageParams(long objectId) {
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
    public List<DomainObject> getAllOuterOrganizations(Locale locale) {
        return getOrganizations(OSZN_TYPE, CALCULATION_CENTER_TYPE, SERVICING_ORGANIZATION_TYPE);
    }

    public List<DomainObject> getAllOSZNs(Locale locale) {
        return getOrganizations(OSZN_TYPE);
    }

    public List<DomainObject> getAllCalculationCentres(Locale locale) {
        return getOrganizations(CALCULATION_CENTER_TYPE);
    }

    @Override
    public boolean isSimpleAttributeType(AttributeType attributeType) {
        return !CUSTOM_ATTRIBUTE_TYPES.contains(attributeType.getId())
                && super.isSimpleAttributeType(attributeType);
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


    private void saveServiceAssociation(ServiceBilling serviceBilling) {
        sqlSession().insert(MAPPING_NAMESPACE + ".insertServiceAssociation", serviceBilling);
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
    protected Long insertStrings(long attributeTypeId, List<StringCulture> strings) {
        /* if it's data source or one of load/save request file directory attributes 
         * or root directory for loading and saving request files
         * then string value should be inserted as is and not upper cased. */
        return ATTRIBUTE_TYPES_WITH_CUSTOM_STRING_PROCESSING.contains(attributeTypeId)
                ? stringBean.save(strings, getEntityName(), false)
                : super.insertStrings(attributeTypeId, strings);
    }

    public DomainObject getBalanceHolder(Long organizationId){
        DomainObject organization = getDomainObject(organizationId, true);

        Attribute parent = organization.getAttribute(USER_ORGANIZATION_PARENT);

        return parent == null || parent.getValueId() == null ? organization : getBalanceHolder(parent.getValueId());
    }

    @Override
    public String displayAttribute(Attribute attribute, Locale locale) {
        if (attribute != null && attribute.getAttributeTypeId().equals(USER_ORGANIZATION_PARENT)){
            return displayShortNameAndCode(attribute.getValueId(), locale);
        }

        return super.displayAttribute(attribute, locale);
    }
}
