package ru.complitex.sync.handler;

import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.sync.entity.DomainSync;
import ru.complitex.sync.entity.DomainSyncParameter;
import ru.complitex.sync.entity.SyncEntity;
import ru.complitex.sync.service.DomainSyncBean;

import javax.ejb.EJB;
import java.util.Date;
import java.util.List;

/**
 * @author Ivanov Anatoliy
 */
public abstract class DomainSyncHandler implements IDomainSyncHandler {
    @EJB
    private DomainSyncBean domainSyncBean;

    public DomainSync getDomainSync(SyncEntity entity, Long externalId) {
        List<DomainSync> domainSyncs = domainSyncBean.getList(FilterWrapper.of(new DomainSync(entity, externalId)));

        if (domainSyncs.isEmpty()) {
            throw new RuntimeException(entity + " not found " + externalId);
        }

        return domainSyncs.get(0);
    }

    public DomainSyncParameter getCityDomainSyncParameter(DomainSync city, Date date) {
        DomainSync cityType = getDomainSync(SyncEntity.CITY_TYPE, Long.valueOf(city.getAdditionalParentId()));
        DomainSync region = getDomainSync(SyncEntity.REGION, city.getParentId());
        DomainSync county = getDomainSync(SyncEntity.COUNTRY, region.getParentId());

        DomainSyncParameter parameter = new DomainSyncParameter(date);

        parameter.setCountryName(county.getName());
        parameter.setRegionName(region.getName());
        parameter.setCityTypeName(cityType.getName());
        parameter.setCityName(city.getName());

        return parameter;
    }
}
