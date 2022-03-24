package ru.complitex.pspoffice.address.sync.service;

import ru.complitex.pspoffice.address.producer.SyncProducer;
import ru.complitex.sync.entity.SyncCatalog;
import ru.complitex.pspoffice.address.sync.mapper.SyncMapper;
import ru.complitex.sync.service.IAddressService;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

/**
 * @author Ivanov Anatoliy
 */
@ApplicationScoped
public class AddressService implements IAddressService {
    @Inject
    @SyncProducer
    private SyncMapper syncMapper;

    @Override
    public void getOrganizationSyncs(SyncCatalog syncCatalog) {
        syncMapper.callOrganizationSyncs(syncCatalog);
    }

    @Override
    public void getCountrySyncs(SyncCatalog syncCatalog) {
        syncMapper.callCountrySyncs(syncCatalog);
    }

    @Override
    public void getRegionSyncs(SyncCatalog syncCatalog) {
        syncMapper.callRegionSyncs(syncCatalog);
    }

    @Override
    public void getCityTypeSyncs(SyncCatalog syncCatalog) {
        syncMapper.callCityTypeSyncs(syncCatalog);
    }

    @Override
    public void getCitySyncs(SyncCatalog syncCatalog) {
        syncMapper.callCitySyncs(syncCatalog);
    }

    @Override
    public void getDistrictSyncs(SyncCatalog syncCatalog) {
        syncMapper.callDistrictSyncs(syncCatalog);
    }

    @Override
    public void getStreetTypeSyncs(SyncCatalog syncCatalog) {
        syncMapper.callStreetTypeSyncs(syncCatalog);
    }

    @Override
    public void getStreetSyncs(SyncCatalog syncCatalog) {
        syncMapper.callStreetSyncs(syncCatalog);
    }

    @Override
    public void getHouseSyncs(SyncCatalog syncCatalog) {
        syncMapper.callHouseSyncs(syncCatalog);
    }

    @Override
    public void getFlatSyncs(SyncCatalog syncCatalog) {
        syncMapper.callFlatSyncs(syncCatalog);
    }
}
