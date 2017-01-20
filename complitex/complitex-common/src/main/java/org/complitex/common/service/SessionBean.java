package org.complitex.common.service;

import com.google.common.collect.Sets;
import org.apache.wicket.util.string.Strings;
import org.complitex.common.entity.DomainObject;
import org.complitex.common.entity.FilterWrapper;
import org.complitex.common.entity.UserGroup.GROUP_NAME;
import org.complitex.common.exception.WrongCurrentPasswordException;
import org.complitex.common.strategy.IStrategy;
import org.complitex.common.strategy.PermissionBean;
import org.complitex.common.strategy.StrategyFactory;
import org.complitex.common.strategy.organization.IOrganizationStrategy;
import org.complitex.common.web.DictionaryFwSession;

import javax.annotation.Resource;
import javax.annotation.security.DeclareRoles;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import java.util.*;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 29.11.10 19:00
 */
@Stateless
@DeclareRoles(SessionBean.CHILD_ORGANIZATION_VIEW_ROLE)
public class SessionBean extends AbstractBean {

    private static final String MAPPING_NAMESPACE = SessionBean.class.getName();
    private static final String ORGANIZATION_ENTITY = "organization";
    public static final String CHILD_ORGANIZATION_VIEW_ROLE = "CHILD_ORGANIZATION_VIEW";

    @Resource
    private SessionContext sessionContext;

    @EJB
    private IUserProfileBean userProfileBean;

    @EJB
    private StrategyFactory strategyFactory;

    @EJB(lookup = IOrganizationStrategy.BEAN_LOOKUP)
    private IOrganizationStrategy organizationStrategy;

    public boolean isAdmin() {
        List<String> userGroups = getCurrentUserGroups();

        return userGroups != null && userGroups.contains(GROUP_NAME.ADMINISTRATORS.name());
    }

    private List<String> getCurrentUserGroups() {
        return sqlSession().selectList(MAPPING_NAMESPACE + ".getUserGroups", sessionContext.getCallerPrincipal().getName());
    }

    public boolean hasAnyUserGroup(String... userGroups) {
        if (userGroups == null){
            return false;
        }

        List<String> list = getCurrentUserGroups();

        for (String userGroup : userGroups){
            if (list.contains(userGroup)){
                return true;
            }
        }

        return false;
    }

    public Long getCurrentUserId() {
        return sqlSession().selectOne(MAPPING_NAMESPACE + ".selectUserId", getCurrentUserLogin());
    }

    public String getCurrentUserLogin() {
        return sessionContext.getCallerPrincipal().getName();
    }

    public List<Long> getUserOrganizationObjectIds() {
        return sqlSession().selectList(MAPPING_NAMESPACE + ".selectOrganizationObjectIds", getCurrentUserLogin());
    }

    private List<Long> getOrganizationChildrenObjectId(Long parentObjectId) {
        return sqlSession().selectList(MAPPING_NAMESPACE + ".selectOrganizationChildrenObjectIds", parentObjectId);
    }

    public List<Long> getUserOrganizationTreeObjectIds() {
        List<Long> objectIds = new ArrayList<>();

        for (Long objectId : getUserOrganizationObjectIds()) {
            addChildOrganizations(objectIds, objectId);
        }

        return objectIds;
    }

    private void addChildOrganizations(List<Long> objectIds, Long objectId) {
        objectIds.add(objectId);

        for (Long id : getOrganizationChildrenObjectId(objectId)) {
            if (!objectIds.contains(id)) {
                addChildOrganizations(objectIds, id);
            }
        }
    }

    private List<Long> getUserOrganizationTreePermissionIds(String table) {
        Map<String, String> parameter = new HashMap<>();
        parameter.put("table", table);
        parameter.put("organizations", getUserOrganizationTreeString());

        return sqlSession().selectList(MAPPING_NAMESPACE + ".selectUserOrganizationTreePermissionIds", parameter);
    }

    public String getUserOrganizationTreeString(){
        String s = "";
        String d = "";

        for (Long p : getUserOrganizationTreeObjectIds()) {
            s += d + p;
            d = ", ";
        }

        return !Strings.isEmpty(s) ? "(" + s + ")" : "(0)";
    }

    public String getOrganizationString(Long organizationId){
        String s = "";
        String d = "";

        List<Long> ids = new ArrayList<>();
        addChildOrganizations(ids, organizationId);

        for (Long p : ids) {
            s += d + p;
            d = ", ";
        }

        return !Strings.isEmpty(s) ? "(" + s + ")" : "(0)";
    }

    /**
     * Loads main user's organization id from database.
     *
     * @return main organization id
     */
    private Long getMainUserOrganizationObjectId() {
            return sqlSession().selectOne(MAPPING_NAMESPACE + ".selectMainOrganizationObjectId", getCurrentUserLogin());
    }

    private List<Long> getUserOrganizationPermissionIds(final String table) {
        return sqlSession().selectList(MAPPING_NAMESPACE + ".selectUserOrganizationPermissionIds",
                new HashMap<String, String>() {

                    {
                        put("table", table);
                        put("login", getCurrentUserLogin());
                    }
                });
    }

    public String getPermissionString(String table) {
        List<Long> permissions = sessionContext.isCallerInRole(CHILD_ORGANIZATION_VIEW_ROLE)
                ? getUserOrganizationTreePermissionIds(table)
                : getUserOrganizationPermissionIds(table);
        permissions.add(PermissionBean.VISIBLE_BY_ALL_PERMISSION_ID);

        String s = "";
        String d = "";

        for (Long p : permissions) {
            s += d + p;
            d = ", ";
        }

        return "(" + s + ")";
    }

    public String getCurrentUserFullName(Locale locale) {
        return userProfileBean.getFullName(getCurrentUserId(), locale);
    }

    public String getMainUserOrganizationName(DictionaryFwSession session) {
        try {
            IStrategy strategy = strategyFactory.getStrategy(ORGANIZATION_ENTITY);
            DomainObject mainUserOrganization = getMainUserOrganization(session);
            return mainUserOrganization != null && mainUserOrganization.getObjectId() != null
                    ? strategy.displayDomainObject(mainUserOrganization, session.getLocale()) : "";
        } catch (Exception e) {
            return "[NA]";
        }
    }

    /**
     * Loads main user's organization from database.
     *
     * @return
     */
    public DomainObject loadMainUserOrganization() {
        IStrategy strategy = strategyFactory.getStrategy(ORGANIZATION_ENTITY);
        Long oId = getMainUserOrganizationObjectId();

        return oId != null ? strategy.getDomainObject(oId, true) : null;
    }


    public void updatePassword(String currentPassword, final String password) throws WrongCurrentPasswordException {
        userProfileBean.updatePassword(currentPassword, password);
    }

    public boolean isBlockedUser(String login) {
        int userGroupCount = (Integer) sqlSession().selectOne(MAPPING_NAMESPACE + ".getUserGroupCount", login);
        return userGroupCount == 0;
    }

    /**
     * Loads main user's organization from session at first and if it doesn't find then fallbacks to loading from database.
     *
     * @param session
     * @return
     */
    public DomainObject getMainUserOrganization(DictionaryFwSession session) {
        DomainObject sessionOrganization = session.getMainUserOrganization();

        if (sessionOrganization != null && sessionOrganization.getObjectId() != null) {
            return sessionOrganization;
        } else {
            DomainObject mainUserOrganization = loadMainUserOrganization();
            session.setMainUserOrganization(mainUserOrganization);

            return mainUserOrganization;
        }
    }

    /**
     * Updates main user's organization in database and session.
     */

    public void updateMainUserOrganization(DictionaryFwSession session, DomainObject mainUserOrganization) {
        userProfileBean.updateMainUserOrganization(mainUserOrganization.getObjectId());
        session.setMainUserOrganization(mainUserOrganization);
    }

    public String getAllOuterOrganizationsString() {
        String s = "";
        String d = "";

        for (long id : getAllOuterOrganizationObjectIds()) {
            s += d + id;
            d = ",";
        }

        return "(" + s + ")";
    }

    private List<Long> getAllOuterOrganizationObjectIds() {
        List<Long> objectIds = new ArrayList<>();

        for (DomainObject o : organizationStrategy.getAllOuterOrganizations(null)) {
            objectIds.add(o.getObjectId());
        }

        return objectIds;
    }

    public boolean isAuthorized(Long userOrganizationId) {
        return isAdmin() || isUserOrganizationVisibleToCurrentUser(userOrganizationId);
    }

    /**
     * Returns main user's organization by means
     *  of {@link SessionBean#getMainUserOrganization(DictionaryFwSession)}
     *
     * @param session dictionary session.
     * @return
     */
    public Long getCurrentUserOrganizationId(DictionaryFwSession session) {
        DomainObject mainUserOrganization = getMainUserOrganization(session);

        return mainUserOrganization != null && mainUserOrganization.getObjectId() != null
                && mainUserOrganization.getObjectId() > 0 ? mainUserOrganization.getObjectId() : null;
    }

    public void authorize(AbstractFilter filter) { //todo dev secure rule
        filter.setAdmin(isAdmin());

        if (!isAdmin()) {
            //filter.setOuterOrganizationsString(getAllOuterOrganizationsString());
            filter.setUserOrganizationsString(getCurrentUserOrganizationsString());
        }
    }

    public void authorize(FilterWrapper filter) {
        filter.setAdmin(isAdmin());

        if (!isAdmin()) {
//            filter.setOuterOrganizationsString(getAllOuterOrganizationsString());
            filter.setUserOrganizationsString(getCurrentUserOrganizationsString());
        }
    }

    public String getCurrentUserOrganizationsString() {
        return getUserOrganizationsString(getUserOrganizationIdsVisibleToCurrentUser());
    }

    private String getUserOrganizationsString(Set<Long> userOrganizationIds) {
        String s = "";
        String d = "";

        for (long p : userOrganizationIds) {
            s += d + p;
            d = ", ";
        }

        return "(" + s + ")";
    }

    private Set<Long> getUserOrganizationIdsVisibleToCurrentUser() {
        return sessionContext.isCallerInRole(SessionBean.CHILD_ORGANIZATION_VIEW_ROLE)
                ? Sets.newHashSet(getUserOrganizationTreeObjectIds())
                : Sets.newHashSet(getUserOrganizationObjectIds());
    }

    public String getMainUserOrganizationString(Long userOrganizationId) {
        return getUserOrganizationsString(Sets.newHashSet(userOrganizationId));
    }

    public String getMainUserOrganizationString(Long userOrganizationId, boolean nullOnAdmin) {
        return nullOnAdmin && isAdmin() ? null : getUserOrganizationsString(Sets.newHashSet(userOrganizationId));
    }

    private boolean isUserOrganizationVisibleToCurrentUser(Long userOrganizationId) {
        return userOrganizationId == null || getUserOrganizationIdsVisibleToCurrentUser().contains(userOrganizationId);
    }

}
