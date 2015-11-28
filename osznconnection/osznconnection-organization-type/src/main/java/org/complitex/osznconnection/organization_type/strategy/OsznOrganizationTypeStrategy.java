package org.complitex.osznconnection.organization_type.strategy;

import com.google.common.collect.ImmutableList;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.complitex.organization_type.strategy.OrganizationTypeStrategy;

import javax.ejb.Stateless;
import java.util.Collection;

@Stateless
public class OsznOrganizationTypeStrategy extends OrganizationTypeStrategy {
    private static final String STRATEGY_NAME = OsznOrganizationTypeStrategy.class.getSimpleName();

    public static final long SUBSIDY_DEPARTMENT_TYPE = 7;
    public static final long PRIVILEGE_DEPARTMENT_TYPE = 8;

    @Override
    protected Collection<Long> getReservedInstanceIds() {
        return ImmutableList.of(USER_ORGANIZATION_TYPE, SERVICING_ORGANIZATION_TYPE, BILLING_TYPE);
    }

    @Override
    public PageParameters getEditPageParams(Long objectId, Long parentId, String parentEntity) {
        PageParameters pageParameters = super.getEditPageParams(objectId, parentId, parentEntity);
        pageParameters.set(STRATEGY, STRATEGY_NAME);
        return pageParameters;
    }

    @Override
    public PageParameters getHistoryPageParams(Long objectId) {
        PageParameters pageParameters = super.getHistoryPageParams(objectId);
        pageParameters.set(STRATEGY, STRATEGY_NAME);
        return pageParameters;
    }

    @Override
    public PageParameters getListPageParams() {
        PageParameters pageParameters = super.getListPageParams();
        pageParameters.set(STRATEGY, STRATEGY_NAME);
        return pageParameters;
    }
}
