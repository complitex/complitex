package ru.complitex.sync.service;

import ru.complitex.address.entity.Street;
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
public class StreetSyncService extends SyncService {
    @Inject
    private CatalogService catalogService;

    @Inject
    private CitySyncService citySyncService;

    @Inject
    private DistrictSyncService districtSyncService;

    @Inject
    private IAddressService addressService;

    public String getCityTypeShortName(Item cityCorrection, LocalDate date, int locale) {
        return catalogService.getItem(CityTypeCorrection.CATALOG, date)
                .withReferenceId(CityTypeCorrection.CATALOG_ORGANIZATION, CATALOG_ORGANIZATION)
                .withReferenceId(CityTypeCorrection.CORRECTION_ORGANIZATION, CORRECTION_ORGANIZATION)
                .withLong(CityTypeCorrection.CITY_TYPE_ID, cityCorrection.getLong(CityCorrection.CITY_TYPE_ID))
                .get()
                .getText(CityTypeCorrection.CITY_TYPE_SHORT_NAME, locale);
    }

    @Override
    public Iterator<SyncCatalog> getSyncCatalogs(LocalDate date, int locale) {
        return catalogService.getItems(CityCorrection.CATALOG, date)
                .withReferenceId(CityCorrection.CATALOG_ORGANIZATION, CATALOG_ORGANIZATION)
                .withReferenceId(CityCorrection.CORRECTION_ORGANIZATION, CORRECTION_ORGANIZATION)
                .get().stream()
                .map(cityCorrection -> {
                    String cityType = getCityTypeShortName(cityCorrection, date, locale);

                    SyncCatalog syncCatalog = new SyncCatalog(date, locale);

                    syncCatalog.setCityType(cityType);
                    syncCatalog.setCity(cityCorrection.getText(CityCorrection.CITY_NAME, locale));

                    Item regionCorrection = districtSyncService.getRegionCorrection(cityCorrection, date, locale);

                    syncCatalog.setRegion(regionCorrection.getText(RegionCorrection.REGION_NAME, locale));
                    syncCatalog.setCountry(citySyncService.getCountryName(regionCorrection, date, locale));

                    addressService.getStreetSyncs(syncCatalog);

                    return syncCatalog;
                })
                .iterator();
    }

    @Override
    public Item getCorrection(Sync sync, LocalDate date) {
        return catalogService.getItem(StreetCorrection.CATALOG, date)
                .withReferenceId(StreetCorrection.CATALOG_ORGANIZATION, CATALOG_ORGANIZATION)
                .withReferenceId(StreetCorrection.CORRECTION_ORGANIZATION, CORRECTION_ORGANIZATION)
                .withLong(StreetCorrection.STREET_ID, sync.getExternalId())
                .get();
    }

    public Long getCityId(Long correctionCityId, LocalDate date) {
        return catalogService.getReferenceId(CityCorrection.CATALOG, CityCorrection.CITY, date)
                .withReferenceId(StreetCorrection.CATALOG_ORGANIZATION, CATALOG_ORGANIZATION)
                .withReferenceId(CityCorrection.CORRECTION_ORGANIZATION, CORRECTION_ORGANIZATION)
                .withLong(CityCorrection.CITY_ID, correctionCityId)
                .get();
    }

    public Long getStreetTypeId(Long correctionStreetTypeId, LocalDate date) {
        return catalogService.getReferenceId(StreetTypeCorrection.CATALOG, StreetTypeCorrection.STREET_TYPE, date)
                .withReferenceId(StreetCorrection.CATALOG_ORGANIZATION, CATALOG_ORGANIZATION)
                .withReferenceId(StreetTypeCorrection.CORRECTION_ORGANIZATION, CORRECTION_ORGANIZATION)
                .withLong(StreetTypeCorrection.STREET_TYPE_ID, correctionStreetTypeId)
                .get();
    }

    @Override
    public Item getItem(Sync sync, LocalDate date, int locale) {
        return catalogService.getItem(Street.CATALOG, date)
                .withReferenceId(Street.CITY, getCityId(sync.getParentId(), date))
                .withReferenceId(Street.STREET_TYPE, getStreetTypeId(Long.valueOf(sync.getAdditionalParentId()), date))
                .withText(Street.STREET_NAME, sync.getName(), locale)
                .get();
    }

    @Override
    public Item addItem(Sync sync, LocalDate date, int locale) {
        Item item = catalogService.newItem(Street.CATALOG)
                .withReferenceId(Street.CITY, getCityId(sync.getParentId(), date))
                .withReferenceId(Street.STREET_TYPE, getStreetTypeId(Long.valueOf(sync.getAdditionalParentId()), date))
                .withText(Street.STREET_NAME, sync.getName(), locale)
                .withText(Street.STREET_NAME, sync.getAltName(), getAltLocale(locale))
                .get();

        return catalogService.inserts(item, date);
    }

    @Override
    public Item addCorrection(Item item, Sync sync, LocalDate date, int locale) {
        Item correction = catalogService.newItem(StreetCorrection.CATALOG)
                .withReferenceId(CityCorrection.CITY, item.getId())
                .withReferenceId(StreetCorrection.CATALOG_ORGANIZATION, CATALOG_ORGANIZATION)
                .withReferenceId(StreetCorrection.CORRECTION_ORGANIZATION, CORRECTION_ORGANIZATION)
                .withLong(StreetCorrection.CITY_ID, sync.getParentId())
                .withLong(StreetCorrection.STREET_TYPE_ID, Long.valueOf(sync.getAdditionalParentId()))
                .withLong(StreetCorrection.STREET_ID, sync.getExternalId())
                .withText(StreetCorrection.STREET_CODE, sync.getAdditionalExternalId())
                .withText(StreetCorrection.STREET_NAME, sync.getName(), locale)
                .withText(StreetCorrection.STREET_NAME, sync.getAltName(), getAltLocale(locale))
                .withTimestamp(StreetCorrection.SYNCHRONIZATION_DATE, date)
                .get();

        return catalogService.inserts(correction, date);
    }

    @Override
    public boolean updateCorrection(Item correction, Sync sync, LocalDate date, int locale) {
        correction.setTimestamp(StreetCorrection.SYNCHRONIZATION_DATE, date);

        catalogService.update(correction, date);

        correction.setLong(StreetCorrection.CITY_ID, sync.getParentId());
        correction.setLong(StreetCorrection.STREET_TYPE_ID, Long.valueOf(sync.getAdditionalParentId()));
        correction.setText(StreetCorrection.STREET_CODE, sync.getAdditionalExternalId());
        correction.setText(StreetCorrection.STREET_NAME, sync.getName(), locale);
        correction.setText(StreetCorrection.STREET_NAME, sync.getAltName(), getAltLocale(locale));

        return catalogService.update(correction, date);
    }

    @Override
    public boolean updateItem(Item correction, LocalDate date, int locale) {
        Item item = catalogService.getReferenceItem(correction, StreetCorrection.STREET, date);

        item.setReferenceId(Street.CITY, getCityId(correction.getLong(StreetCorrection.CITY_ID), date));
        item.setReferenceId(Street.STREET_TYPE, getStreetTypeId(correction.getLong(StreetCorrection.STREET_TYPE_ID), date));
        item.setText(Street.STREET_NAME, correction.getText(StreetCorrection.STREET_NAME, locale), locale);
        item.setText(Street.STREET_NAME, correction.getText(StreetCorrection.STREET_NAME, getAltLocale(locale)), getAltLocale(locale));

        return catalogService.update(item, date);
    }
}
