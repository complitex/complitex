package ru.complitex.pspoffice.address.sync.service;

import ru.complitex.catalog.entity.Item;
import ru.complitex.catalog.service.CatalogService;
import ru.complitex.address.entity.House;
import ru.complitex.correction.entity.*;
import ru.complitex.pspoffice.address.producer.SyncProducer;
import ru.complitex.pspoffice.address.sync.entity.Sync;
import ru.complitex.pspoffice.address.sync.entity.SyncCatalog;
import ru.complitex.pspoffice.address.sync.mapper.SyncMapper;

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
    @SyncProducer
    private SyncMapper syncMapper;

    @Inject
    private CatalogService catalogService;

    @Inject
    private StreetSyncService streetSyncService;

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
                    String street = streetCorrection.getText(StreetCorrection.STREET_NAME, locale);

                    String streetType = getStreetTypeShortName(streetCorrection, date, locale);

                    Item cityCorrection = getCityCorrection(streetCorrection, date);

                    String city = cityCorrection.getText(CityCorrection.CITY_NAME, locale);

                    String cityType = streetSyncService.getCityTypeShortName(cityCorrection, date, locale);

                    return catalogService.getItems(DistrictCorrection.CATALOG, date)
                            .withReferenceId(DistrictCorrection.CATALOG_ORGANIZATION, CATALOG_ORGANIZATION)
                            .withReferenceId(DistrictCorrection.CORRECTION_ORGANIZATION, CORRECTION_ORGANIZATION)
                            .get().stream()
                            .map(districtCorrection -> {
                                SyncCatalog syncCatalog = null;

                                try {
                                     syncCatalog = new SyncCatalog(date, locale);

                                    syncCatalog.setCity(city);
                                    syncCatalog.setCityType(cityType);
                                    syncCatalog.setDistrict(districtCorrection.getText(DistrictCorrection.DISTRICT_NAME, locale));
                                    syncCatalog.setStreet(street);
                                    syncCatalog.setStreetType(streetType != null ? streetType : "");

                                    syncMapper.callHouseSyncs(syncCatalog);

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
                .withText(House.HOUSE_NUMBER, locale, sync.getName())
                .withText(House.HOUSE_PART, locale, sync.getAdditionalName())
                .get();
    }

    @Override
    public Item addItem(Sync sync, LocalDate date, int locale) {
        Item item = catalogService.newItem(House.CATALOG)
                .withReferenceId(House.DISTRICT, getDistrictId(Long.valueOf(sync.getAdditionalParentId()), date))
                .withReferenceId(House.STREET, getStreetId(sync.getParentId(), date))
                .withText(House.HOUSE_NUMBER, locale, sync.getName())
                .withText(House.HOUSE_NUMBER, getAltLocale(locale), sync.getAltName())
                .withText(House.HOUSE_PART, locale, sync.getAdditionalName())
                .withText(House.HOUSE_PART, getAltLocale(locale), sync.getAltAdditionalName())
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
                .withText(HouseCorrection.HOUSE_NUMBER, locale, sync.getName())
                .withText(HouseCorrection.HOUSE_NUMBER, getAltLocale(locale), sync.getAltName())
                .withText(HouseCorrection.HOUSE_PART, locale, sync.getAdditionalName())
                .withText(HouseCorrection.HOUSE_PART, getAltLocale(locale), sync.getAltAdditionalName())
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
        correction.setText(HouseCorrection.HOUSE_NUMBER, locale, sync.getName());
        correction.setText(HouseCorrection.HOUSE_NUMBER, getAltLocale(locale), sync.getAltName());
        correction.setText(HouseCorrection.HOUSE_PART, locale, sync.getAdditionalName());
        correction.setText(HouseCorrection.HOUSE_PART, getAltLocale(locale), sync.getAltAdditionalName());
        correction.setLong(HouseCorrection.SERVICING_ORGANIZATION_ID, sync.getServicingOrganization());
        correction.setLong(HouseCorrection.BALANCE_HOLDER_ID, sync.getBalanceHolder());

        return catalogService.update(correction, date);
    }

    @Override
    public boolean updateItem(Item correction, LocalDate date, int locale) {
        Item item = catalogService.getReferenceItem(correction, HouseCorrection.HOUSE, date);

        item.setReferenceId(House.STREET, getStreetId(correction.getLong(HouseCorrection.STREET_ID), date));
        item.setReferenceId(House.DISTRICT, getDistrictId(correction.getLong(HouseCorrection.DISTRICT_ID), date));
        item.setText(House.HOUSE_NUMBER, locale, correction.getText(HouseCorrection.HOUSE_NUMBER, locale));
        item.setText(House.HOUSE_NUMBER, getAltLocale(locale), correction.getText(HouseCorrection.HOUSE_NUMBER, getAltLocale(locale)));
        item.setText(House.HOUSE_PART, locale, correction.getText(HouseCorrection.HOUSE_PART, locale));
        item.setText(House.HOUSE_PART, getAltLocale(locale), correction.getText(HouseCorrection.HOUSE_PART, getAltLocale(locale)));

        return catalogService.update(item, date);
    }
}

