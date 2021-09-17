package ru.complitex.pspoffice.address.sync.service;

import ru.complitex.catalog.entity.Item;
import ru.complitex.catalog.service.CatalogService;
import ru.complitex.pspoffice.address.catalog.entity.CityType;
import ru.complitex.pspoffice.address.correction.entity.CityTypeCorrection;
import ru.complitex.pspoffice.address.producer.SyncProducer;
import ru.complitex.pspoffice.address.sync.entity.Sync;
import ru.complitex.pspoffice.address.sync.entity.SyncCatalog;
import ru.complitex.pspoffice.address.sync.mapper.SyncMapper;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Iterator;

/**
 * @author Ivanov Anatoliy
 */
@ApplicationScoped
public class CityTypeSyncService extends SyncService {
    @Inject
    @SyncProducer
    private SyncMapper syncMapper;

    @Inject
    private CatalogService catalogService;

    @Override
    public Iterator<SyncCatalog> getSyncCatalogs(LocalDate date, int locale) {
        SyncCatalog syncCatalog = new SyncCatalog(date, locale);

        syncMapper.callCityTypeSyncs(syncCatalog);

        return Collections.singletonList(syncCatalog).iterator();
    }

    @Override
    public Item getCorrection(Sync sync, LocalDate date) {
        return catalogService.getItem(CityTypeCorrection.CATALOG, date)
                .withReferenceId(CityTypeCorrection.CATALOG_ORGANIZATION, CATALOG_ORGANIZATION)
                .withReferenceId(CityTypeCorrection.CORRECTION_ORGANIZATION, CORRECTION_ORGANIZATION)
                .withLong(CityTypeCorrection.CITY_TYPE_ID, sync.getExternalId())
                .get();
    }

    @Override
    public Item getItem(Sync sync, LocalDate date, int locale) {
        return catalogService.getItem(CityType.CATALOG, date)
                .withText(CityType.CITY_TYPE_NAME, locale, sync.getName())
                .get();
    }

    @Override
    public Item addItem(Sync sync, LocalDate date, int locale) {
        Item item = catalogService.newItem(CityType.CATALOG)
                .withText(CityType.CITY_TYPE_NAME, locale, sync.getName())
                .withText(CityType.CITY_TYPE_NAME, getAltLocale(locale), sync.getAltName())
                .withText(CityType.CITY_TYPE_SHORT_NAME, locale, sync.getAdditionalName())
                .withText(CityType.CITY_TYPE_SHORT_NAME, getAltLocale(locale), sync.getAltAdditionalName())
                .get();

        return catalogService.inserts(item, date);
    }

    @Override
    public Item addCorrection(Item item, Sync sync, LocalDate date, int locale) {
        Item correction = catalogService.newItem(CityTypeCorrection.CATALOG)
                .withReferenceId(CityTypeCorrection.CITY_TYPE, item.getId())
                .withReferenceId(CityTypeCorrection.CATALOG_ORGANIZATION, CATALOG_ORGANIZATION)
                .withReferenceId(CityTypeCorrection.CORRECTION_ORGANIZATION, CORRECTION_ORGANIZATION)
                .withLong(CityTypeCorrection.CITY_TYPE_ID, sync.getExternalId())
                .withText(CityTypeCorrection.CITY_TYPE_CODE, sync.getAdditionalExternalId())
                .withText(CityTypeCorrection.CITY_TYPE_NAME, locale, sync.getName())
                .withText(CityTypeCorrection.CITY_TYPE_NAME, getAltLocale(locale), sync.getAltName())
                .withText(CityTypeCorrection.CITY_TYPE_SHORT_NAME, locale, sync.getAdditionalName())
                .withText(CityTypeCorrection.CITY_TYPE_SHORT_NAME, getAltLocale(locale), sync.getAltAdditionalName())
                .withTimestamp(CityTypeCorrection.SYNCHRONIZATION_DATE, date)
                .get();

        return catalogService.inserts(correction, date);
    }

    @Override
    public boolean updateCorrection(Item correction, Sync sync, LocalDate date, int locale) {
        correction.setTimestamp(CityTypeCorrection.SYNCHRONIZATION_DATE, date);

        catalogService.update(correction, date);

        correction.setText(CityTypeCorrection.CITY_TYPE_CODE, sync.getAdditionalExternalId());
        correction.setText(CityTypeCorrection.CITY_TYPE_NAME, locale, sync.getName());
        correction.setText(CityTypeCorrection.CITY_TYPE_NAME, getAltLocale(locale), sync.getAltName());
        correction.setText(CityTypeCorrection.CITY_TYPE_SHORT_NAME, locale, sync.getAdditionalName());
        correction.setText(CityTypeCorrection.CITY_TYPE_SHORT_NAME, getAltLocale(locale), sync.getAltAdditionalName());

        return catalogService.update(correction, date);
    }

    @Override
    public boolean updateItem(Item correction, LocalDate date, int locale) {
        Item item = catalogService.getReferenceItem(correction, CityTypeCorrection.CITY_TYPE, date);

        item.setText(CityType.CITY_TYPE_NAME, locale, correction.getText(CityTypeCorrection.CITY_TYPE_NAME, locale));
        item.setText(CityType.CITY_TYPE_NAME, getAltLocale(locale), correction.getText(CityTypeCorrection.CITY_TYPE_NAME, getAltLocale(locale)));
        item.setText(CityType.CITY_TYPE_SHORT_NAME, locale, correction.getText(CityTypeCorrection.CITY_TYPE_SHORT_NAME, locale));
        item.setText(CityType.CITY_TYPE_SHORT_NAME, getAltLocale(locale), correction.getText(CityTypeCorrection.CITY_TYPE_SHORT_NAME, getAltLocale(locale)));

        return catalogService.update(item, date);
    }
}
