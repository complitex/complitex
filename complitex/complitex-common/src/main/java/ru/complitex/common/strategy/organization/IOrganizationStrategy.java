package ru.complitex.common.strategy.organization;

import ru.complitex.common.entity.DomainObject;
import ru.complitex.common.entity.RemoteDataSource;
import ru.complitex.common.mybatis.SqlSessionFactoryBean;
import ru.complitex.common.strategy.IStrategy;

import java.util.List;
import java.util.Locale;
import java.util.Set;


public interface IOrganizationStrategy extends IStrategy{
    String BEAN_NAME = "OrganizationStrategy";
    String BEAN_LOOKUP = "java:module/OrganizationStrategy";

    /**
     * Organization name.
     */
    long NAME = 900;

    /**
     * Organization's short name.
     */
    long SHORT_NAME = 906;

    /**
     * Organization's code.
     */
    long CODE = 901;
    /**
     * District reference.
     */
    long DISTRICT = 902;
    /**
     * User organization's parent.
     */
    long USER_ORGANIZATION_PARENT = 903;
    /**
     * Organization type.
     */
    long ORGANIZATION_TYPE = 904;

    /**
     * Reference to jdbc data source. It is calculation center only attribute.
     */
    long DATA_SOURCE = 913;

    /**
     * EDRPOU(ЕДРПОУ).
     */
    long EDRPOU = 926;

    /**
     * Service reference.
     */
    long SERVICE = 4914;

    /**
     * Billing reference.
     */
    long BILLING = 4915;

    long ENTITY_ID = 900;

    
    /**
     * Filter parameter to filter out organizations by organization types.
     */
    String ORGANIZATION_TYPE_PARAMETER = "organizationTypeIds";

    String BALANCE_HOLDER_PARAMETER = "balanceHolder";

    /**
     * Returns user organizations that current user can see (at least to read).
     * That is, it returns all user organizations that accessed by user's organizations.
     * 
     * @param locale Locale. It is used in sorting of user organizations by name.
     * @param excludeOrganizationsId Ids of user organizations that should be excluded from returned list.
     * @return User organizations that current user can see.
     */

    List<? extends DomainObject> getUserOrganizations(Locale locale, Long... excludeOrganizationsId);

    /**
     * Returns set of user organization object's ids that descendant of user organization with id of {@code parentOrganizationId}
     * regardless of visibility of organization objects to current user, i.e. it works for any user the same way as for admin user.
     * 
     * @param parentOrganizationId Id of given user organization object.
     * @return Ids of all descendant user organizations.
     */

    Set<Long> getTreeChildrenOrganizationIds(Long parentOrganizationId);

    /**
     * Calculates whether given {@code organization} play role of user organization, i.e. has user organization type
     * in set of organizations type attributes (recall that organization may play role of more one organization type at the same time).
     * 
     * @param organization Organization object.
     * @return true if {@code organization} is user organization.
     */
    boolean isUserOrganization(DomainObject organization);

    /**
     * Validates code of organization for existing. 
     * <p>
     * Note: 
     * If tested organization is new then {@code id} is <code>null</code>.
     * </p>
     * 
     * @param id Id of tested organization.
     * @param code Code of tested organization.
     * @return Id of any existing organization with the same code but another id if such exists and <code>null</code> otherwise.
     */

    Long validateCode(Long id, String code);

    /**
     * Validates name of organization for existing.
     * 
     * @param id Id of tested organization.
     * @param name Name of tested organization.
     * @param locale Locale. It is used to validate only for names in given {@code locale}.
     * @return Id of any existing organization with the same name but another id if such exists and <code>null</code> otherwise.
     */

    Long validateName(Long id, String name, Locale locale);

    /**
     * Returns code of {@code organization}.
     * 
     * @param organization Organization.
     * @return Organization's code.
     */
    String getCode(DomainObject organization);

    /**
     * Returns code of organization with {@code organizationId}.
     * 
     * @param organizationId Organization id.
     * @return Organization's code.
     */
    String getCode(Long organizationId);


    Long getObjectIdByCode(String code);

    /**
     * Figures out all outer (OSZNs and calculation centers) organizations visible to current user
     * and returns them sorted by organization's name in given {@code locale}.
     *
     * @param locale Locale. It is used in sorting of organizations by name.
     * @return All outer organizations visible to user.
     */

    List<? extends DomainObject> getAllOuterOrganizations(Locale locale);

    List<? extends DomainObject> getOrganizations(Long... types);

    String displayShortNameAndCode(DomainObject organization, Locale locale);

    String displayShortNameAndCode(Long organizationId, Locale locale);

    void setSqlSessionFactoryBean(SqlSessionFactoryBean sqlSessionFactoryBean);

    List<RemoteDataSource> findRemoteDataSources(String currentDataSource);
}
