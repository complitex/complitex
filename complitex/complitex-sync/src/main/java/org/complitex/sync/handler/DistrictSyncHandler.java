package org.complitex.sync.handler;

import org.complitex.address.exception.RemoteCallException;
import org.complitex.address.strategy.city.CityStrategy;
import org.complitex.address.strategy.city_type.CityTypeStrategy;
import org.complitex.address.strategy.district.DistrictStrategy;
import org.complitex.common.entity.Cursor;
import org.complitex.common.entity.DomainObject;
import org.complitex.common.entity.DomainObjectFilter;
import org.complitex.common.util.DateUtil;
import org.complitex.common.util.Locales;
import org.complitex.common.web.component.ShowMode;
import org.complitex.correction.entity.DistrictCorrection;
import org.complitex.correction.service.AddressCorrectionBean;
import org.complitex.sync.entity.DomainSync;
import org.complitex.sync.service.DomainSyncAdapter;
import org.complitex.sync.service.DomainSyncBean;
import org.complitex.sync.service.IDomainSyncHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.complitex.common.entity.FilterWrapper.of;
import static org.complitex.sync.entity.DomainSyncStatus.LOADED;
import static org.complitex.sync.entity.SyncEntity.DISTRICT;

/**
 * @author Anatoly Ivanov
 * Date: 17.07.2014 23:34
 */
@Stateless
public class DistrictSyncHandler implements IDomainSyncHandler {
    private Logger log = LoggerFactory.getLogger(DistrictSyncHandler.class);

    @EJB
    private DomainSyncBean addressSyncBean;

    @EJB
    private DomainSyncAdapter addressSyncAdapter;

    @EJB
    private CityStrategy cityStrategy;

    @EJB
    private CityTypeStrategy cityTypeStrategy;

    @EJB
    private DistrictStrategy districtStrategy;

    @EJB
    private AddressCorrectionBean addressCorrectionBean;

    @Override
    public List<? extends DomainObject> getParentObjects(Map<String, DomainObject> map) {
        return cityStrategy.getList(new DomainObjectFilter().setStatus(ShowMode.ACTIVE.name()));
    }

    @Override
    public Cursor<DomainSync> getAddressSyncs(DomainObject parent, Date date) throws RemoteCallException {
        return addressSyncAdapter.getDistrictSyncs(cityStrategy.getName(parent),
                cityTypeStrategy.getShortName(parent.getAttribute(CityStrategy.CITY_TYPE).getValueId()),
                date);
    }

    @Override
    public List<? extends DomainObject> getObjects(DomainObject parent) {
        return districtStrategy.getList(new DomainObjectFilter().setParent("city", parent.getObjectId()));
    }

    @Override
    public boolean isEqualNames(DomainSync sync, DomainObject object) {
        return sync.getName().equals(districtStrategy.getName(object));
    }

    @Override
    public Long getParentId(DomainSync sync, DomainObject parent) {
        return parent.getObjectId();
    }

    public void insert(DomainSync sync){
        DomainObject domainObject = districtStrategy.newInstance();

        domainObject.setParentId(sync.getParentId());
        domainObject.setStringValue(DistrictStrategy.NAME, sync.getName());
        domainObject.setStringValue(DistrictStrategy.NAME, sync.getAltName(), Locales.getAlternativeLocale());
        domainObject.setStringValue(DistrictStrategy.CODE, sync.getAdditionalExternalId());

        districtStrategy.insert(domainObject, sync.getDate());
        addressSyncBean.delete(sync.getId());
    }

    public void update(DomainSync sync){
//        DomainObject oldObject = districtStrategy.getDomainObject(sync.getObjectId(), true);
//        DomainObject newObject = CloneUtil.cloneObject(oldObject);
//
//        newObject.setStringValue(DistrictStrategy.NAME, sync.getName());
//        newObject.setStringValue(DistrictStrategy.NAME, sync.getAltName(), Locales.getAlternativeLocale());
//        newObject.setStringValue(DistrictStrategy.CODE, sync.getAdditionalExternalId());
//
//        districtStrategy.update(oldObject, newObject, sync.getDate());
//        addressSyncBean.delete(sync.getId());
    }

    public void archive(DomainSync sync){
//        districtStrategy.archive(districtStrategy.getDomainObject(sync.getObjectId(), true), sync.getDate());
        addressSyncBean.delete(sync.getId());
    }

    @Override
    public String getName(DomainObject object) {
        return districtStrategy.getName(object);
    }

    @Override
    public List<DomainSync> getDomainSyncs(Long parentId) {
        return addressSyncBean.getList(of(new DomainSync(DISTRICT, LOADED)));
    }

    @Override
    public void sync(Long parentId) {
        Long organizationId = addressSyncAdapter.getOrganization().getObjectId();

        addressSyncBean.getList(of(new DomainSync(DISTRICT, LOADED))).forEach(s -> {
            List<DistrictCorrection> corrections = addressCorrectionBean.getDistrictCorrections(parentId,
                    s.getExternalId(), null, null, organizationId,null);

            if (!corrections.isEmpty()){

            }else {
                List<? extends DomainObject> domainObjects = districtStrategy.getList(
                        new DomainObjectFilter("district")
                                .setStatus(ShowMode.ACTIVE.name())
                                .setComparisonType(DomainObjectFilter.ComparisonType.EQUALITY.name())
                                .setParentEntity("city")
                                .setParentId(parentId)
                                .addAttribute(DistrictStrategy.NAME, s.getName(), Locales.getSystemLocaleId())
                                .addAttribute(DistrictStrategy.NAME, s.getAltName(), Locales.getAlternativeLocaleId()));

                if (!domainObjects.isEmpty()){
                    for (int i = 1; i < domainObjects.size(); ++i){
                        districtStrategy.archive(domainObjects.get(i), DateUtil.getCurrentDate());

                        log.info("sync: archive domain object {}", domainObjects.get(i));
                    }

                    DomainObject o = domainObjects.get(0);

                    DistrictCorrection correction = new DistrictCorrection(o.getParentId(), s.getExternalId(),
                            o.getObjectId(), s.getName(), organizationId, null, 0L);

                    addressCorrectionBean.insert(correction);

                    log.info("sync: add correction {}", correction);
                }else{
                    DomainObject domainObject = districtStrategy.newInstance();

                    domainObject.setParentEntityId(DistrictStrategy.PARENT_ENTITY_ID);
                    domainObject.setParentId(parentId);
                    domainObject.setStringValue(DistrictStrategy.CODE, s.getAdditionalExternalId());
                    domainObject.setStringValue(DistrictStrategy.NAME, s.getName(), Locales.getSystemLocale());
                    domainObject.setStringValue(DistrictStrategy.NAME, s.getAltName(), Locales.getAlternativeLocale());

                    districtStrategy.insert(domainObject, DateUtil.getCurrentDate());

                    log.info("sync: add domain object {}", domainObjects);
                }
            }
        });
    }
}
