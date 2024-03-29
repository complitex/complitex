package ru.complitex.keconnection.heatmeter.service;

import com.google.common.collect.Sets;
import ru.complitex.common.entity.DomainObject;
import ru.complitex.common.service.SessionBean;
import ru.complitex.common.strategy.StrategyFactory;
import ru.complitex.common.web.DictionaryFwSession;
import ru.complitex.keconnection.organization.strategy.KeOrganizationStrategy;

import javax.annotation.Resource;
import javax.annotation.security.DeclareRoles;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 09.02.11 14:43
 */
@Stateless
@DeclareRoles(SessionBean.CHILD_ORGANIZATION_VIEW_ROLE)
public class KeConnectionSessionBean {

    @Resource
    private SessionContext sessionContext;
    @EJB
    private SessionBean sessionBean;
    @EJB
    protected StrategyFactory strategyFactory;

    @EJB
    protected KeOrganizationStrategy organizationStrategy;

    public boolean isAdmin() {
        return sessionBean.isAdmin();
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
        List<Long> objectIds = new ArrayList<Long>();

        for (DomainObject o : organizationStrategy.getAllOuterOrganizations(null)) {
            objectIds.add(o.getObjectId());
        }

        return objectIds;
    }

    private boolean hasOuterOrganization(Long objectId) {
        return getAllOuterOrganizationObjectIds().contains(objectId);
    }

    public boolean isAuthorized(Long outerOrganizationObjectId, Long userOrganizationId) {
        return isAdmin()
                || (hasOuterOrganization(outerOrganizationObjectId) && isUserOrganizationVisibleToCurrentUser(userOrganizationId));
    }

    /**
     * Returns main user's organization by means 
     *  of {@link SessionBean#getMainUserOrganization(DictionaryFwSession)}
     * 
     * @param session dictionary session.
     * @return 
     */
    public Long getCurrentUserOrganizationId(DictionaryFwSession session) {
        DomainObject mainUserOrganization = sessionBean.getMainUserOrganization(session);
        return mainUserOrganization != null && mainUserOrganization.getObjectId() != null
                && mainUserOrganization.getObjectId() > 0 ? mainUserOrganization.getObjectId() : null;
    }


    public String getMainUserOrganizationForSearchCorrections(Long userOrganizationId) {
        if (sessionBean.isAdmin()) {
            return null;
        }
        return getMainUserOrganizationString(userOrganizationId);
    }

    private String getCurrentUserOrganizationsString() {
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
                ? Sets.newHashSet(sessionBean.getUserOrganizationTreeObjectIds())
                : Sets.newHashSet(sessionBean.getUserOrganizationObjectIds());
    }

    public String getMainUserOrganizationString(Long userOrganizationId) {
        return getUserOrganizationsString(Sets.newHashSet(userOrganizationId));
    }

    private boolean isUserOrganizationVisibleToCurrentUser(Long userOrganizationId) {
        return userOrganizationId == null || getUserOrganizationIdsVisibleToCurrentUser().contains(userOrganizationId);
    }
}
