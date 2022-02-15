package ru.complitex.pspoffice.address.sync.service;

import ru.complitex.catalog.entity.Item;
import ru.complitex.catalog.service.CatalogService;
import ru.complitex.pspoffice.address.entity.Flat;
import ru.complitex.pspoffice.address.correction.entity.*;
import ru.complitex.pspoffice.address.producer.SyncProducer;
import ru.complitex.pspoffice.address.sync.entity.Sync;
import ru.complitex.pspoffice.address.sync.entity.SyncCatalog;
import ru.complitex.pspoffice.address.sync.mapper.SyncMapper;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.LocalDate;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Ivanov Anatoliy
 */
@ApplicationScoped
public class FlatSyncService extends SyncService {
    @Inject
    @SyncProducer
    private SyncMapper syncMapper;

    @Inject
    private CatalogService catalogService;

    @Inject
    private StreetSyncService streetSyncService;

    @Inject
    private HouseSyncService houseSyncService;

    @Override
    public Iterator<SyncCatalog> getSyncCatalogs(LocalDate date, int locale) {
        return catalogService.getItems(StreetCorrection.CATALOG, date)
                .withReferenceId(FlatCorrection.CATALOG_ORGANIZATION, CATALOG_ORGANIZATION)
                .withReferenceId(FlatCorrection.CORRECTION_ORGANIZATION, CORRECTION_ORGANIZATION)
                .get().stream()
                .flatMap(streetCorrection -> catalogService.getItems(HouseCorrection.CATALOG, date)
                        .withReferenceId(HouseCorrection.CATALOG_ORGANIZATION, CATALOG_ORGANIZATION)
                        .withReferenceId(HouseCorrection.CORRECTION_ORGANIZATION, CORRECTION_ORGANIZATION)
                        .withLong(HouseCorrection.STREET_ID, streetCorrection.getLong(StreetCorrection.STREET_ID))
                        .get().stream()
                        .map(houseCorrection -> {
                            String street = streetCorrection.getText(StreetCorrection.STREET_NAME, locale);

                            String streetType = houseSyncService.getStreetTypeShortName(streetCorrection, date, locale);

                            String district = catalogService.getItem(DistrictCorrection.CATALOG, date)
                                    .withReferenceId(DistrictCorrection.CATALOG_ORGANIZATION, CATALOG_ORGANIZATION)
                                    .withReferenceId(DistrictCorrection.CORRECTION_ORGANIZATION, CORRECTION_ORGANIZATION)
                                    .withLong(DistrictCorrection.DISTRICT_ID, houseCorrection.getLong(HouseCorrection.DISTRICT_ID))
                                    .get()
                                    .getText(DistrictCorrection.DISTRICT_NAME, locale);

                            Item cityCorrection = houseSyncService.getCityCorrection(streetCorrection, date);

                            String city = cityCorrection.getText(CityCorrection.CITY_NAME, locale);

                            String cityType = streetSyncService.getCityTypeShortName(cityCorrection, date, locale);

                            SyncCatalog syncCatalog = new SyncCatalog(date, locale);

                            syncCatalog.setCity(city);
                            syncCatalog.setCityType(cityType);
                            syncCatalog.setDistrict(district);
                            syncCatalog.setStreet(street);
                            syncCatalog.setStreetType(streetType);
                            syncCatalog.setHouse(houseCorrection.getText(HouseCorrection.HOUSE_NUMBER, locale));

                            String part = houseCorrection.getText(HouseCorrection.HOUSE_PART, locale);

                            syncCatalog.setPart(part != null ? part : "");

                            syncMapper.callFlatSyncs(syncCatalog);

                            return syncCatalog;
                        }))
                .iterator();
    }

    @Override
    public Item getCorrection(Sync sync, LocalDate date) {
        return catalogService.getItem(FlatCorrection.CATALOG, date)
                .withReferenceId(FlatCorrection.CATALOG_ORGANIZATION, CATALOG_ORGANIZATION)
                .withReferenceId(FlatCorrection.CORRECTION_ORGANIZATION, CORRECTION_ORGANIZATION)
                .withLong(FlatCorrection.FLAT_ID, sync.getExternalId())
                .get();
    }

    private final Map<Long, Map<LocalDate, Long>> houseCache = new ConcurrentHashMap<>();

    public Long getHouseId(Long correctionHouseId, LocalDate date) {
        return houseCache
                .computeIfAbsent(correctionHouseId, c -> new ConcurrentHashMap<>())
                .computeIfAbsent(date, d -> catalogService.getReferenceId(HouseCorrection.CATALOG, HouseCorrection.HOUSE, d)
                        .withReferenceId(HouseCorrection.CATALOG_ORGANIZATION, CATALOG_ORGANIZATION)
                        .withReferenceId(HouseCorrection.CORRECTION_ORGANIZATION, CORRECTION_ORGANIZATION)
                        .withLong(HouseCorrection.HOUSE_ID, correctionHouseId)
                        .get());
    }

    private final Map<Long, Map<LocalDate, Long>> streetCache = new ConcurrentHashMap<>();

    public Long getStreetId(Long correctionStreetId, LocalDate date) {
        return streetCache
                .computeIfAbsent(correctionStreetId, c -> new ConcurrentHashMap<>())
                .computeIfAbsent(date, d -> catalogService.getReferenceId(StreetCorrection.CATALOG, StreetCorrection.STREET, d)
                        .withReferenceId(StreetCorrection.CATALOG_ORGANIZATION, CATALOG_ORGANIZATION)
                        .withReferenceId(StreetCorrection.CORRECTION_ORGANIZATION, CORRECTION_ORGANIZATION)
                        .withLong(StreetCorrection.STREET_ID, correctionStreetId)
                        .get());
    }

    @Override
    public Item getItem(Sync sync, LocalDate date, int locale) {
        return catalogService.getItem(Flat.CATALOG, date)
                .withReferenceId(Flat.HOUSE, getHouseId(sync.getParentId(), date))
                .withReferenceId(Flat.STREET, getStreetId(Long.valueOf(sync.getAdditionalParentId()), date))
                .withText(Flat.FLAT_NUMBER, locale, sync.getName())
                .get();
    }

    @Override
    public Item addItem(Sync sync, LocalDate date, int locale) {
        Item item = catalogService.newItem(Flat.CATALOG)
                .withReferenceId(Flat.HOUSE, getHouseId(sync.getParentId(), date))
                .withReferenceId(Flat.STREET, getStreetId(Long.valueOf(sync.getAdditionalParentId()), date))
                .withText(Flat.FLAT_NUMBER, locale, sync.getName())
                .withText(Flat.FLAT_NUMBER, getAltLocale(locale), sync.getAltName())
                .get();

        return catalogService.inserts(item, date);
    }

    @Override
    public Item addCorrection(Item item, Sync sync, LocalDate date, int locale) {
        Item correction = catalogService.newItem(FlatCorrection.CATALOG)
                .withReferenceId(FlatCorrection.FLAT, item.getId())
                .withReferenceId(FlatCorrection.CATALOG_ORGANIZATION, CATALOG_ORGANIZATION)
                .withReferenceId(FlatCorrection.CORRECTION_ORGANIZATION, CORRECTION_ORGANIZATION)
                .withLong(FlatCorrection.STREET_ID, Long.valueOf(sync.getAdditionalParentId()))
                .withLong(FlatCorrection.HOUSE_ID, sync.getParentId())
                .withLong(FlatCorrection.FLAT_ID, sync.getExternalId())
                .withText(FlatCorrection.FLAT_NUMBER, locale, sync.getName())
                .withText(FlatCorrection.FLAT_NUMBER, getAltLocale(locale), sync.getAltName())
                .withLong(FlatCorrection.SERVICING_ORGANIZATION_ID, sync.getServicingOrganization())
                .withLong(FlatCorrection.BALANCE_HOLDER_ID, sync.getBalanceHolder())
                .withTimestamp(FlatCorrection.SYNCHRONIZATION_DATE, date)
                .get();

        return catalogService.inserts(correction, date);
    }

    @Override
    public boolean updateCorrection(Item correction, Sync sync, LocalDate date, int locale) {
        correction.setTimestamp(FlatCorrection.SYNCHRONIZATION_DATE, date);

        catalogService.update(correction, date);

        correction.setLong(FlatCorrection.STREET_ID, Long.valueOf(sync.getAdditionalParentId()));
        correction.setLong(FlatCorrection.HOUSE_ID, sync.getParentId());
        correction.setText(FlatCorrection.FLAT_NUMBER, locale, sync.getName());
        correction.setText(FlatCorrection.FLAT_NUMBER, getAltLocale(locale), sync.getAltName());
        correction.setLong(FlatCorrection.SERVICING_ORGANIZATION_ID, sync.getServicingOrganization());
        correction.setLong(FlatCorrection.BALANCE_HOLDER_ID, sync.getBalanceHolder());

        return catalogService.update(correction, date);
    }

    @Override
    public boolean updateItem(Item correction, LocalDate date, int locale) {
        Item item = catalogService.getReferenceItem(correction, FlatCorrection.FLAT, date);

        item.setReferenceId(Flat.HOUSE, getHouseId(correction.getLong(FlatCorrection.HOUSE_ID), date));
        item.setReferenceId(Flat.STREET, getStreetId(correction.getLong(FlatCorrection.STREET_ID), date));
        item.setText(Flat.FLAT_NUMBER, locale, correction.getText(FlatCorrection.FLAT_NUMBER, locale));
        item.setText(Flat.FLAT_NUMBER, getAltLocale(locale), correction.getText(FlatCorrection.FLAT_NUMBER, getAltLocale(locale)));

        return catalogService.update(item, date);
    }

    @Override
    public void sync(int catalog, LocalDate date, int locale, ISyncListener<SyncCatalog> listener, AtomicBoolean status) {
        super.sync(catalog, date, locale, listener, status);

        streetCache.clear();
        houseCache.clear();
    }
}