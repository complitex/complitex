package ru.complitex.sync.service;

import ru.complitex.address.entity.CityType;
import ru.complitex.catalog.entity.Item;
import ru.complitex.catalog.service.CatalogService;
import ru.complitex.correction.entity.CityTypeCorrection;
import ru.complitex.sync.entity.Sync;
import ru.complitex.sync.entity.SyncCatalog;

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
    private CatalogService catalogService;

    @Inject
    private IAddressService addressService;

    @Override
    public Iterator<SyncCatalog> getSyncCatalogs(LocalDate date, int locale) {
        SyncCatalog syncCatalog = new SyncCatalog(date, locale);

        addressService.getCityTypeSyncs(syncCatalog);

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
                .withText(CityType.CITY_TYPE_NAME, sync.getName(), locale)
                .get();
    }

    @Override
    public Item addItem(Sync sync, LocalDate date, int locale) {
        Item item = catalogService.newItem(CityType.CATALOG)
                .withText(CityType.CITY_TYPE_NAME, sync.getName(), locale)
                .withText(CityType.CITY_TYPE_NAME, sync.getAltName(), getAltLocale(locale))
                .withText(CityType.CITY_TYPE_SHORT_NAME, sync.getAdditionalName(), locale)
                .withText(CityType.CITY_TYPE_SHORT_NAME, sync.getAltAdditionalName(), getAltLocale(locale))
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
                .withText(CityTypeCorrection.CITY_TYPE_NAME, sync.getName(), locale)
                .withText(CityTypeCorrection.CITY_TYPE_NAME, sync.getAltName(), getAltLocale(locale))
                .withText(CityTypeCorrection.CITY_TYPE_SHORT_NAME, sync.getAdditionalName(), locale)
                .withText(CityTypeCorrection.CITY_TYPE_SHORT_NAME, sync.getAltAdditionalName(), getAltLocale(locale))
                .withTimestamp(CityTypeCorrection.SYNCHRONIZATION_DATE, date)
                .get();

        return catalogService.inserts(correction, date);
    }

    @Override
    public boolean updateCorrection(Item correction, Sync sync, LocalDate date, int locale) {
        correction.setTimestamp(CityTypeCorrection.SYNCHRONIZATION_DATE, date);

        catalogService.update(correction, date);

        correction.setText(CityTypeCorrection.CITY_TYPE_CODE, sync.getAdditionalExternalId());
        correction.setText(CityTypeCorrection.CITY_TYPE_NAME, sync.getName(), locale);
        correction.setText(CityTypeCorrection.CITY_TYPE_NAME, sync.getAltName(), getAltLocale(locale));
        correction.setText(CityTypeCorrection.CITY_TYPE_SHORT_NAME, sync.getAdditionalName(), locale);
        correction.setText(CityTypeCorrection.CITY_TYPE_SHORT_NAME, sync.getAltAdditionalName(), getAltLocale(locale));

        return catalogService.update(correction, date);
    }

    @Override
    public boolean updateItem(Item correction, LocalDate date, int locale) {
        Item item = catalogService.getReferenceItem(correction, CityTypeCorrection.CITY_TYPE, date);

        item.setText(CityType.CITY_TYPE_NAME, correction.getText(CityTypeCorrection.CITY_TYPE_NAME, locale), locale);
        item.setText(CityType.CITY_TYPE_NAME, correction.getText(CityTypeCorrection.CITY_TYPE_NAME, getAltLocale(locale)), getAltLocale(locale));
        item.setText(CityType.CITY_TYPE_SHORT_NAME, correction.getText(CityTypeCorrection.CITY_TYPE_SHORT_NAME, locale), locale);
        item.setText(CityType.CITY_TYPE_SHORT_NAME, correction.getText(CityTypeCorrection.CITY_TYPE_SHORT_NAME, getAltLocale(locale)), getAltLocale(locale));

        return catalogService.update(item, date);
    }
}
