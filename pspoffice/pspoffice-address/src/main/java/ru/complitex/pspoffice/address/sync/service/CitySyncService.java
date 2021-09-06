package ru.complitex.pspoffice.address.sync.service;

import ru.complitex.catalog.entity.Item;
import ru.complitex.catalog.service.CatalogService;
import ru.complitex.pspoffice.address.catalog.entity.City;
import ru.complitex.pspoffice.address.correction.entity.CityCorrection;
import ru.complitex.pspoffice.address.correction.entity.CityTypeCorrection;
import ru.complitex.pspoffice.address.correction.entity.RegionCorrection;
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
public class CitySyncService implements ISyncService {
    @Inject
    @SyncProducer
    private SyncMapper syncMapper;

    @Inject
    private CatalogService catalogService;

    @Override
    public Iterator<SyncCatalog> getSyncCatalogs(LocalDate date, int locale) {
        return catalogService.getItems(RegionCorrection.CATALOG, date)
                .withReferenceId(RegionCorrection.CATALOG_ORGANIZATION, CATALOG_ORGANIZATION)
                .withReferenceId(RegionCorrection.CORRECTION_ORGANIZATION, CORRECTION_ORGANIZATION)
                .get().stream()
                .map(regionCorrection -> {
                    SyncCatalog syncCatalog = new SyncCatalog(date, locale);

                    syncCatalog.setRegion(regionCorrection.getText(RegionCorrection.REGION_NAME, locale));

                    syncMapper.callCitySyncs(syncCatalog);

                    return syncCatalog;
                })
                .iterator();
    }

    @Override
    public Item getCorrection(Sync sync, LocalDate date) {
        return catalogService.getItem(CityCorrection.CATALOG, date)
                .withReferenceId(CityCorrection.CATALOG_ORGANIZATION, CATALOG_ORGANIZATION)
                .withReferenceId(CityCorrection.CORRECTION_ORGANIZATION, CORRECTION_ORGANIZATION)
                .withLong(CityCorrection.CITY_ID, sync.getExternalId())
                .get();
    }

    public Long getRegionId(Long correctionRegionId, LocalDate date) {
        return catalogService.getReferenceId(RegionCorrection.CATALOG, RegionCorrection.REGION, date)
                .withReferenceId(CityCorrection.CATALOG_ORGANIZATION, CATALOG_ORGANIZATION)
                .withReferenceId(RegionCorrection.CORRECTION_ORGANIZATION, CORRECTION_ORGANIZATION)
                .withLong(RegionCorrection.REGION_ID, correctionRegionId)
                .get();
    }

    public Long getCityTypeId(Long correctionCityTypeId, LocalDate date) {
        return catalogService.getReferenceId(CityTypeCorrection.CATALOG, CityTypeCorrection.CITY_TYPE, date)
                .withReferenceId(CityCorrection.CATALOG_ORGANIZATION, CATALOG_ORGANIZATION)
                .withReferenceId(CityTypeCorrection.CORRECTION_ORGANIZATION, CORRECTION_ORGANIZATION)
                .withLong(CityTypeCorrection.CITY_TYPE_ID, correctionCityTypeId)
                .get();
    }

    @Override
    public Item getItem(Sync sync, LocalDate date, int locale) {
        return catalogService.getItem(City.CATALOG, date)
                .withReferenceId(City.REGION, getRegionId(sync.getParentId(), date))
                .withReferenceId(City.CITY_TYPE, getCityTypeId(Long.valueOf(sync.getAdditionalParentId()), date))
                .withText(City.CITY_NAME, locale, sync.getName())
                .withText(City.CITY_NAME, getAltLocale(locale), sync.getAltName())
                .get();
    }

    @Override
    public Item addItem(Sync sync, LocalDate date, int locale) {
        Item item = catalogService.newItem(City.CATALOG)
                .withReferenceId(City.REGION, getRegionId(sync.getParentId(), date))
                .withReferenceId(City.CITY_TYPE, getCityTypeId(Long.valueOf(sync.getAdditionalParentId()), date))
                .withText(City.CITY_NAME, locale, sync.getName())
                .withText(City.CITY_NAME, getAltLocale(locale), sync.getAltName())
                .get();

        return catalogService.inserts(item, date);
    }

    @Override
    public Item addCorrection(Item item, Sync sync, LocalDate date, int locale) {
        Item correction = catalogService.newItem(CityCorrection.CATALOG)
                .withReferenceId(CityCorrection.CITY, item.getId())
                .withReferenceId(CityCorrection.CATALOG_ORGANIZATION, CATALOG_ORGANIZATION)
                .withReferenceId(CityCorrection.CORRECTION_ORGANIZATION, CORRECTION_ORGANIZATION)
                .withLong(CityCorrection.REGION_ID, sync.getParentId())
                .withLong(CityCorrection.CITY_TYPE_ID, Long.valueOf(sync.getAdditionalParentId()))
                .withLong(CityCorrection.CITY_ID, sync.getExternalId())
                .withText(CityCorrection.CITY_CODE, sync.getAdditionalExternalId())
                .withText(CityCorrection.CITY_NAME, locale, sync.getName())
                .withText(CityCorrection.CITY_NAME, getAltLocale(locale), sync.getAltName())
                .withTimestamp(CityCorrection.SYNCHRONIZATION_DATE, date)
                .get();

        return catalogService.inserts(correction, date);
    }

    @Override
    public boolean updateCorrection(Item correction, Sync sync, LocalDate date, int locale) {
        correction.setTimestamp(CityCorrection.SYNCHRONIZATION_DATE, date);

        catalogService.update(correction, date);

        correction.setLong(CityCorrection.REGION_ID, sync.getParentId());
        correction.setLong(CityCorrection.CITY_TYPE_ID, Long.valueOf(sync.getAdditionalParentId()));
        correction.setText(CityCorrection.CITY_CODE, sync.getAdditionalExternalId());
        correction.setText(CityCorrection.CITY_NAME, locale, sync.getName());
        correction.setText(CityCorrection.CITY_NAME, getAltLocale(locale), sync.getAltName());

        return catalogService.update(correction, date);
    }

    @Override
    public boolean updateItem(Item correction, LocalDate date, int locale) {
        Item item = catalogService.getReferenceItem(correction, CityCorrection.CITY, date);

        item.setReferenceId(City.REGION, getRegionId(correction.getLong(CityCorrection.REGION_ID), date));
        item.setReferenceId(City.CITY_TYPE, getCityTypeId(correction.getLong(CityCorrection.CITY_TYPE_ID), date));
        item.setText(City.CITY_NAME, locale, correction.getText(CityCorrection.CITY_NAME, locale));
        item.setText(City.CITY_NAME, getAltLocale(locale), correction.getText(CityCorrection.CITY_NAME, getAltLocale(locale)));

        return catalogService.update(item, date);
    }
}
