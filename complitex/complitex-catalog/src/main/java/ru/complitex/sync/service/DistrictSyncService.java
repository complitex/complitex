package ru.complitex.sync.service;

import ru.complitex.address.entity.District;
import ru.complitex.catalog.entity.Item;
import ru.complitex.catalog.service.CatalogService;
import ru.complitex.correction.entity.*;
import ru.complitex.sync.entity.Sync;
import ru.complitex.sync.entity.SyncCatalog;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.LocalDate;
import java.util.Iterator;

/**
 * @author Ivanov Anatoliy
 */
@ApplicationScoped
public class DistrictSyncService extends SyncService {
    @Inject
    private CatalogService catalogService;

    @Inject
    private CitySyncService citySyncService;

    @Inject
    private IAddressService addressService;

    public Item getRegionCorrection(Item cityCorrection, LocalDate date, int locale) {
        return catalogService.getItem(RegionCorrection.CATALOG, date)
                .withReferenceId(RegionCorrection.CATALOG_ORGANIZATION, CATALOG_ORGANIZATION)
                .withReferenceId(RegionCorrection.CORRECTION_ORGANIZATION, CORRECTION_ORGANIZATION)
                .withLong(RegionCorrection.REGION_ID, cityCorrection.getLong(CityCorrection.REGION_ID))
                .get();
    }

    @Override
    public Iterator<SyncCatalog> getSyncCatalogs(LocalDate date, int locale) {
        return catalogService.getItems(CityCorrection.CATALOG, date)
                .withReferenceId(CityCorrection.CATALOG_ORGANIZATION, CATALOG_ORGANIZATION)
                .withReferenceId(CityCorrection.CORRECTION_ORGANIZATION, CORRECTION_ORGANIZATION)
                .get().stream()
                .map(cityCorrection -> {
                    String cityTypeCorrection = catalogService.getItem(CityTypeCorrection.CATALOG, date)
                            .withReferenceId(RegionCorrection.CATALOG_ORGANIZATION, CATALOG_ORGANIZATION)
                            .withReferenceId(RegionCorrection.CORRECTION_ORGANIZATION, CORRECTION_ORGANIZATION)
                            .withLong(CityTypeCorrection.CITY_TYPE_ID, cityCorrection.getLong(CityCorrection.CITY_TYPE_ID))
                            .get()
                            .getText(CityTypeCorrection.CITY_TYPE_SHORT_NAME, locale);

                    SyncCatalog syncCatalog = new SyncCatalog(date, locale);

                    syncCatalog.setCityType(cityTypeCorrection);
                    syncCatalog.setCity(cityCorrection.getText(CityCorrection.CITY_NAME, locale));

                    Item regionCorrection = getRegionCorrection(cityCorrection, date, locale);

                    syncCatalog.setRegion(regionCorrection.getText(RegionCorrection.REGION_NAME, locale));
                    syncCatalog.setCountry(citySyncService.getCountryName(regionCorrection, date, locale));

                    addressService.getDistrictSyncs(syncCatalog);

                    return syncCatalog;
                })
                .iterator();
    }

    @Override
    public Item getCorrection(Sync sync, LocalDate date) {
        return catalogService.getItem(DistrictCorrection.CATALOG, date)
                .withReferenceId(DistrictCorrection.CATALOG_ORGANIZATION, CATALOG_ORGANIZATION)
                .withReferenceId(DistrictCorrection.CORRECTION_ORGANIZATION, CORRECTION_ORGANIZATION)
                .withLong(DistrictCorrection.CITY_ID, sync.getParentId())
                .withLong(DistrictCorrection.DISTRICT_ID, sync.getExternalId())
                .get();
    }

    public Long getCityId(Long correctionCityId, LocalDate date) {
        return catalogService.getReferenceId(CityCorrection.CATALOG, CityCorrection.CITY, date)
                .withReferenceId(DistrictCorrection.CATALOG_ORGANIZATION, CATALOG_ORGANIZATION)
                .withReferenceId(CityCorrection.CORRECTION_ORGANIZATION, CORRECTION_ORGANIZATION)
                .withLong(CityCorrection.CITY_ID, correctionCityId)
                .get();
    }

    @Override
    public Item getItem(Sync sync, LocalDate date, int locale) {
        return catalogService.getItem(District.CATALOG, date)
                .withReferenceId(District.CITY, getCityId(sync.getParentId(), date))
                .withText(District.DISTRICT_NAME, locale, sync.getName())
                .get();
    }

    @Override
    public Item addItem(Sync sync, LocalDate date, int locale) {
        Item item = catalogService.newItem(District.CATALOG)
                .withReferenceId(District.CITY, getCityId(sync.getParentId(), date))
                .withText(District.DISTRICT_NAME, locale, sync.getName())
                .withText(District.DISTRICT_NAME, getAltLocale(locale), sync.getAltName())
                .get();

        return catalogService.inserts(item, date);
    }

    @Override
    public Item addCorrection(Item item, Sync sync, LocalDate date, int locale) {
        Item correction = catalogService.newItem(DistrictCorrection.CATALOG)
                .withReferenceId(DistrictCorrection.DISTRICT, item.getId())
                .withReferenceId(DistrictCorrection.CATALOG_ORGANIZATION, CATALOG_ORGANIZATION)
                .withReferenceId(DistrictCorrection.CORRECTION_ORGANIZATION, CORRECTION_ORGANIZATION)
                .withLong(DistrictCorrection.CITY_ID, sync.getParentId())
                .withLong(DistrictCorrection.DISTRICT_ID, sync.getExternalId())
                .withText(DistrictCorrection.DISTRICT_CODE, sync.getAdditionalExternalId())
                .withText(DistrictCorrection.DISTRICT_NAME, locale, sync.getName())
                .withText(DistrictCorrection.DISTRICT_NAME, getAltLocale(locale), sync.getAltName())
                .withTimestamp(DistrictCorrection.SYNCHRONIZATION_DATE, date)
                .get();

        return catalogService.inserts(correction, date);
    }

    @Override
    public boolean updateCorrection(Item correction, Sync sync, LocalDate date, int locale) {
        correction.setTimestamp(DistrictCorrection.SYNCHRONIZATION_DATE, date);

        catalogService.update(correction, date);

        correction.setLong(DistrictCorrection.CITY_ID, sync.getParentId());
        correction.setText(DistrictCorrection.DISTRICT_CODE, sync.getAdditionalExternalId());
        correction.setText(DistrictCorrection.DISTRICT_NAME, locale, sync.getName());
        correction.setText(DistrictCorrection.DISTRICT_NAME, getAltLocale(locale), sync.getAltName());

        return catalogService.update(correction, date);
    }

    @Override
    public boolean updateItem(Item correction, LocalDate date, int locale) {
        Item item = catalogService.getReferenceItem(correction, DistrictCorrection.DISTRICT, date);

        item.setReferenceId(District.CITY, getCityId(correction.getLong(DistrictCorrection.CITY_ID), date));
        item.setText(District.DISTRICT_NAME, locale, correction.getText(DistrictCorrection.DISTRICT_NAME, locale));
        item.setText(District.DISTRICT_NAME, getAltLocale(locale), correction.getText(DistrictCorrection.DISTRICT_NAME, getAltLocale(locale)));

        return catalogService.update(item, date);
    }
}
