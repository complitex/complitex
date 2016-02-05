package org.complitex.keconnection.organization_type.strategy;

import com.google.common.collect.ImmutableList;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.complitex.organization_type.strategy.OrganizationTypeStrategy;

import javax.ejb.Stateless;
import java.util.Collection;

/**
 *
 * @author Artem
 */
@Stateless
public class KeConnectionOrganizationTypeStrategy extends OrganizationTypeStrategy {

    private static final String STRATEGY_NAME = KeConnectionOrganizationTypeStrategy.class.getSimpleName();

    @Override
    protected Collection<Long> getReservedInstanceIds() {
        return ImmutableList.of(USER_ORGANIZATION_TYPE, BILLING_TYPE, BALANCE_HOLDER_TYPE,
                SERVICING_ORGANIZATION_TYPE, SERVICE_PROVIDER_TYPE, CONTRACTOR_TYPE);
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
