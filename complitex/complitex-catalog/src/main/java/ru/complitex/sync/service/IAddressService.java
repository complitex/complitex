package ru.complitex.sync.service;

import ru.complitex.sync.entity.SyncCatalog;

/**
 * @author Ivanov Anatoliy
 */
public interface IAddressService {
    void getOrganizationSyncs(SyncCatalog syncCatalog);

    void getCountrySyncs(SyncCatalog syncCatalog);

    void getRegionSyncs(SyncCatalog syncCatalog);

    void getCityTypeSyncs(SyncCatalog syncCatalog);

    void getCitySyncs(SyncCatalog syncCatalog);

    void getDistrictSyncs(SyncCatalog syncCatalog);

    void getStreetTypeSyncs(SyncCatalog syncCatalog);

    void getStreetSyncs(SyncCatalog syncCatalog);

    void getHouseSyncs(SyncCatalog syncCatalog);

    void getFlatSyncs(SyncCatalog syncCatalog);
}
