package ru.complitex.sync.service;

import ru.complitex.address.entity.House;
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
public class HouseSyncService extends SyncService {
    @Inject
    private CatalogService catalogService;

    @Inject
    private CitySyncService citySyncService;

    @Inject
    private DistrictSyncService districtSyncService;

    @Inject
    private StreetSyncService streetSyncService;

    @Inject
    private IAddressService addressService;

    public Item getCityCorrection(Item streetCorrection, LocalDate date) {
        return catalogService.getItem(CityCorrection.CATALOG, date)
                .withReferenceId(CityCorrection.CATALOG_ORGANIZATION, CATALOG_ORGANIZATION)
                .withReferenceId(CityCorrection.CORRECTION_ORGANIZATION, CORRECTION_ORGANIZATION)
                .withLong(CityCorrection.CITY_ID, streetCorrection.getLong(StreetCorrection.CITY_ID))
                .get();
    }

    public String getStreetTypeShortName(Item streetCorrection, LocalDate date, int locale) {
        return catalogService.getItem(StreetTypeCorrection.CATALOG, date)
                .withReferenceId(StreetTypeCorrection.CATALOG_ORGANIZATION, CATALOG_ORGANIZATION)
                .withReferenceId(StreetTypeCorrection.CORRECTION_ORGANIZATION, CORRECTION_ORGANIZATION)
                .withLong(StreetTypeCorrection.STREET_TYPE_ID, streetCorrection.getLong(StreetCorrection.STREET_TYPE_ID))
                .get()
                .getText(StreetTypeCorrection.STREET_TYPE_SHORT_NAME, locale);
    }

    @Override
    public Iterator<SyncCatalog> getSyncCatalogs(LocalDate date, int locale) {
        return catalogService.getItems(StreetCorrection.CATALOG, date)
                .withReferenceId(StreetCorrection.CATALOG_ORGANIZATION, CATALOG_ORGANIZATION)
                .withReferenceId(StreetCorrection.CORRECTION_ORGANIZATION, CORRECTION_ORGANIZATION)
                .get().stream()
                .flatMap(streetCorrection -> {
                    String streetType = getStreetTypeShortName(streetCorrection, date, locale);

                    Item cityCorrection = getCityCorrection(streetCorrection, date);

                    String cityType = streetSyncService.getCityTypeShortName(cityCorrection, date, locale);

                    Item regionCorrection = districtSyncService.getRegionCorrection(cityCorrection, date, locale);

                    String country = citySyncService.getCountryName(regionCorrection, date, locale);

                    return catalogService.getItems(DistrictCorrection.CATALOG, date)
                            .withReferenceId(DistrictCorrection.CATALOG_ORGANIZATION, CATALOG_ORGANIZATION)
                            .withReferenceId(DistrictCorrection.CORRECTION_ORGANIZATION, CORRECTION_ORGANIZATION)
                            .get().stream()
                            .map(districtCorrection -> {
                                SyncCatalog syncCatalog = null;

                                try {
                                     syncCatalog = new SyncCatalog(date, locale);

                                    syncCatalog.setCity(cityCorrection.getText(CityCorrection.CITY_NAME, locale));
                                    syncCatalog.setCityType(cityType);
                                    syncCatalog.setDistrict(districtCorrection.getText(DistrictCorrection.DISTRICT_NAME, locale));
                                    syncCatalog.setStreet(streetCorrection.getText(StreetCorrection.STREET_NAME, locale));
                                    syncCatalog.setStreetType(streetType != null ? streetType : "");

                                    syncCatalog.setRegion(regionCorrection.getText(RegionCorrection.REGION_NAME, locale));
                                    syncCatalog.setCountry(country);

                                    addressService.getHouseSyncs(syncCatalog);

                                    return syncCatalog;
                                } catch (Exception e) {
                                    log.error("{}", syncCatalog);

                                    throw new RuntimeException(e);
                                }
                            });
                })
                .iterator();
    }

    @Override
    public Item getCorrection(Sync sync, LocalDate date) {
        return catalogService.getItem(HouseCorrection.CATALOG, date)
                .withReferenceId(HouseCorrection.CATALOG_ORGANIZATION, CATALOG_ORGANIZATION)
                .withReferenceId(HouseCorrection.CORRECTION_ORGANIZATION, CORRECTION_ORGANIZATION)
                .withLong(HouseCorrection.HOUSE_ID, sync.getExternalId())
                .get();
    }

    public Long getStreetId(Long correctionStreetId, LocalDate date) {
        return catalogService.getReferenceId(StreetCorrection.CATALOG, StreetCorrection.STREET, date)
                .withReferenceId(StreetCorrection.CATALOG_ORGANIZATION, CATALOG_ORGANIZATION)
                .withReferenceId(StreetCorrection.CORRECTION_ORGANIZATION, CORRECTION_ORGANIZATION)
                .withLong(StreetCorrection.STREET_ID, correctionStreetId)
                .get();
    }

    public Long getDistrictId(Long correctionDistrictId, LocalDate date) {
        return catalogService.getReferenceId(DistrictCorrection.CATALOG, DistrictCorrection.DISTRICT, date)
                .withReferenceId(DistrictCorrection.CATALOG_ORGANIZATION, CATALOG_ORGANIZATION)
                .withReferenceId(DistrictCorrection.CORRECTION_ORGANIZATION, CORRECTION_ORGANIZATION)
                .withLong(DistrictCorrection.DISTRICT_ID, correctionDistrictId)
                .get();
    }

    @Override
    public Item getItem(Sync sync, LocalDate date, int locale) {
        return catalogService.getItem(House.CATALOG, date)
                .withReferenceId(House.DISTRICT, getDistrictId(Long.valueOf(sync.getAdditionalParentId()), date))
                .withReferenceId(House.STREET, getStreetId(sync.getParentId(), date))
                .withText(House.HOUSE_NUMBER, sync.getName(), locale)
                .withText(House.HOUSE_PART, sync.getAdditionalName(), locale)
                .get();
    }

    @Override
    public Item addItem(Sync sync, LocalDate date, int locale) {
        Item item = catalogService.newItem(House.CATALOG)
                .withReferenceId(House.DISTRICT, getDistrictId(Long.valueOf(sync.getAdditionalParentId()), date))
                .withReferenceId(House.STREET, getStreetId(sync.getParentId(), date))
                .withText(House.HOUSE_NUMBER, sync.getName(), locale)
                .withText(House.HOUSE_NUMBER, sync.getAltName(), getAltLocale(locale))
                .withText(House.HOUSE_PART, sync.getAdditionalName(), locale)
                .withText(House.HOUSE_PART, sync.getAltAdditionalName(), getAltLocale(locale))
                .get();

        return catalogService.inserts(item, date);
    }

    @Override
    public Item addCorrection(Item item, Sync sync, LocalDate date, int locale) {
        Item correction = catalogService.newItem(HouseCorrection.CATALOG)
                .withReferenceId(HouseCorrection.HOUSE, item.getId())
                .withReferenceId(HouseCorrection.CATALOG_ORGANIZATION, CATALOG_ORGANIZATION)
                .withReferenceId(HouseCorrection.CORRECTION_ORGANIZATION, CORRECTION_ORGANIZATION)
                .withLong(HouseCorrection.DISTRICT_ID, Long.valueOf(sync.getAdditionalParentId()))
                .withLong(HouseCorrection.STREET_ID, sync.getParentId())
                .withLong(HouseCorrection.HOUSE_ID, sync.getExternalId())
                .withText(HouseCorrection.HOUSE_CODE, sync.getAdditionalExternalId())
                .withText(HouseCorrection.HOUSE_NUMBER, sync.getName(), locale)
                .withText(HouseCorrection.HOUSE_NUMBER, sync.getAltName(), getAltLocale(locale))
                .withText(HouseCorrection.HOUSE_PART, sync.getAdditionalName(), locale)
                .withText(HouseCorrection.HOUSE_PART, sync.getAltAdditionalName(), getAltLocale(locale))
                .withLong(HouseCorrection.SERVICING_ORGANIZATION_ID, sync.getServicingOrganization())
                .withLong(HouseCorrection.BALANCE_HOLDER_ID, sync.getBalanceHolder())
                .withTimestamp(HouseCorrection.SYNCHRONIZATION_DATE, date)
                .get();

        return catalogService.inserts(correction, date);
    }

    @Override
    public boolean updateCorrection(Item correction, Sync sync, LocalDate date, int locale) {
        correction.setTimestamp(HouseCorrection.SYNCHRONIZATION_DATE, date);

        catalogService.update(correction, date);

        correction.setLong(HouseCorrection.DISTRICT_ID, Long.valueOf(sync.getAdditionalParentId()));
        correction.setLong(HouseCorrection.STREET_ID, sync.getParentId());
        correction.setText(HouseCorrection.HOUSE_CODE, sync.getAdditionalExternalId());
        correction.setText(HouseCorrection.HOUSE_NUMBER, sync.getName(), locale);
        correction.setText(HouseCorrection.HOUSE_NUMBER, sync.getAltName(), getAltLocale(locale));
        correction.setText(HouseCorrection.HOUSE_PART, sync.getAdditionalName(), locale);
        correction.setText(HouseCorrection.HOUSE_PART, sync.getAltAdditionalName(), getAltLocale(locale));
        correction.setLong(HouseCorrection.SERVICING_ORGANIZATION_ID, sync.getServicingOrganization());
        correction.setLong(HouseCorrection.BALANCE_HOLDER_ID, sync.getBalanceHolder());

        return catalogService.update(correction, date);
    }

    @Override
    public boolean updateItem(Item correction, LocalDate date, int locale) {
        Item item = catalogService.getReferenceItem(correction, HouseCorrection.HOUSE, date);

        item.setReferenceId(House.STREET, getStreetId(correction.getLong(HouseCorrection.STREET_ID), date));
        item.setReferenceId(House.DISTRICT, getDistrictId(correction.getLong(HouseCorrection.DISTRICT_ID), date));
        item.setText(House.HOUSE_NUMBER, correction.getText(HouseCorrection.HOUSE_NUMBER, locale), locale);
        item.setText(House.HOUSE_NUMBER, correction.getText(HouseCorrection.HOUSE_NUMBER, getAltLocale(locale)), getAltLocale(locale));
        item.setText(House.HOUSE_PART, correction.getText(HouseCorrection.HOUSE_PART, locale), locale);
        item.setText(House.HOUSE_PART, correction.getText(HouseCorrection.HOUSE_PART, getAltLocale(locale)), getAltLocale(locale));

        return catalogService.update(item, date);
    }
}

