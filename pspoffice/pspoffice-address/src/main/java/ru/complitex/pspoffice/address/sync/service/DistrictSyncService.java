package ru.complitex.pspoffice.address.sync.service;

import ru.complitex.catalog.entity.Item;
import ru.complitex.catalog.service.CatalogService;
import ru.complitex.pspoffice.address.catalog.entity.District;
import ru.complitex.pspoffice.address.correction.entity.CityCorrection;
import ru.complitex.pspoffice.address.correction.entity.CityTypeCorrection;
import ru.complitex.pspoffice.address.correction.entity.DistrictCorrection;
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
public class DistrictSyncService implements ISyncService {
    @Inject
    @SyncProducer
    private SyncMapper syncMapper;

    @Inject
    private CatalogService catalogService;

    @Override
    public Iterator<SyncCatalog> getSyncCatalogs(LocalDate date, int locale) {
        return catalogService.getItems(CityCorrection.CATALOG, date)
                .withReferenceId(CityCorrection.CATALOG_ORGANIZATION, CATALOG_ORGANIZATION)
                .withReferenceId(CityCorrection.CORRECTION_ORGANIZATION, CORRECTION_ORGANIZATION)
                .get().stream()
                .map(cityCorrection -> {
                    String cityTypeCorrection = catalogService.getItem(CityTypeCorrection.CATALOG, date)
                            .withLong(CityTypeCorrection.CITY_TYPE_ID, cityCorrection.getLong(CityCorrection.CITY_TYPE_ID))
                            .get()
                            .getText(CityTypeCorrection.CITY_TYPE_SHORT_NAME, locale);

                    SyncCatalog syncCatalog = new SyncCatalog(date, locale);

                    syncCatalog.setCityType(cityTypeCorrection);
                    syncCatalog.setCity(cityCorrection.getText(CityCorrection.CITY_NAME, locale));

                    syncMapper.callDistrictSyncs(syncCatalog);

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
