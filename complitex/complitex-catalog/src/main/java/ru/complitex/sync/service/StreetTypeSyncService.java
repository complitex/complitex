package ru.complitex.sync.service;

import ru.complitex.address.entity.StreetType;
import ru.complitex.catalog.entity.Item;
import ru.complitex.catalog.service.CatalogService;
import ru.complitex.correction.entity.StreetTypeCorrection;
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
public class StreetTypeSyncService extends SyncService {
    @Inject
    private CatalogService catalogService;

    @Inject
    private IAddressService addressService;

    @Override
    public Iterator<SyncCatalog> getSyncCatalogs(LocalDate date, int locale) {
        SyncCatalog syncCatalog = new SyncCatalog(date, locale);

        addressService.getStreetTypeSyncs(syncCatalog);

        return Collections.singletonList(syncCatalog).iterator();
    }

    @Override
    public Item getCorrection(Sync sync, LocalDate date) {
        return catalogService.getItem(StreetTypeCorrection.CATALOG, date)
                .withReferenceId(StreetTypeCorrection.CATALOG_ORGANIZATION, CATALOG_ORGANIZATION)
                .withReferenceId(StreetTypeCorrection.CORRECTION_ORGANIZATION, CORRECTION_ORGANIZATION)
                .withLong(StreetTypeCorrection.STREET_TYPE_ID, sync.getExternalId())
                .get();
    }

    @Override
    public Item getItem(Sync sync, LocalDate date, int locale) {
        return catalogService.getItem(StreetType.CATALOG, date)
                .withText(StreetType.STREET_TYPE_NAME, locale, sync.getName())
                .get();
    }

    @Override
    public Item addItem(Sync sync, LocalDate date, int locale) {
        Item item = catalogService.newItem(StreetType.CATALOG)
                .withText(StreetType.STREET_TYPE_NAME, locale, sync.getName())
                .withText(StreetType.STREET_TYPE_NAME, getAltLocale(locale), sync.getAltName())
                .withText(StreetType.STREET_TYPE_SHORT_NAME, getAltLocale(locale), sync.getAltAdditionalName())
                .get();

        return catalogService.inserts(item, date);
    }

    @Override
    public Item addCorrection(Item item, Sync sync, LocalDate date, int locale) {
        Item correction = catalogService.newItem(StreetTypeCorrection.CATALOG)
                .withReferenceId(StreetTypeCorrection.STREET_TYPE, item.getId())
                .withReferenceId(StreetTypeCorrection.CATALOG_ORGANIZATION, CATALOG_ORGANIZATION)
                .withReferenceId(StreetTypeCorrection.CORRECTION_ORGANIZATION, CORRECTION_ORGANIZATION)
                .withLong(StreetTypeCorrection.STREET_TYPE_ID, sync.getExternalId())
                .withText(StreetTypeCorrection.STREET_TYPE_CODE, sync.getAdditionalExternalId())
                .withText(StreetTypeCorrection.STREET_TYPE_NAME, locale, sync.getName())
                .withText(StreetTypeCorrection.STREET_TYPE_NAME, getAltLocale(locale), sync.getAltName())
                .withText(StreetTypeCorrection.STREET_TYPE_SHORT_NAME, locale, sync.getAdditionalName())
                .withText(StreetTypeCorrection.STREET_TYPE_SHORT_NAME, getAltLocale(locale), sync.getAltAdditionalName())
                .withTimestamp(StreetTypeCorrection.SYNCHRONIZATION_DATE, date)
                .get();

        return catalogService.inserts(correction, date);
    }

    @Override
    public boolean updateCorrection(Item correction, Sync sync, LocalDate date, int locale) {
        correction.setTimestamp(StreetTypeCorrection.SYNCHRONIZATION_DATE, date);

        catalogService.update(correction, date);

        correction.setText(StreetTypeCorrection.STREET_TYPE_CODE, sync.getAdditionalExternalId());
        correction.setText(StreetTypeCorrection.STREET_TYPE_NAME, locale, sync.getName());
        correction.setText(StreetTypeCorrection.STREET_TYPE_NAME, getAltLocale(locale), sync.getAltName());
        correction.setText(StreetTypeCorrection.STREET_TYPE_SHORT_NAME, locale, sync.getAdditionalName());
        correction.setText(StreetTypeCorrection.STREET_TYPE_SHORT_NAME, getAltLocale(locale), sync.getAltAdditionalName());

        return catalogService.update(correction, date);
    }

    @Override
    public boolean updateItem(Item correction, LocalDate date, int locale) {
        Item item = catalogService.getReferenceItem(correction, StreetTypeCorrection.STREET_TYPE, date);

        item.setText(StreetType.STREET_TYPE_NAME, locale, correction.getText(StreetTypeCorrection.STREET_TYPE_NAME, locale));
        item.setText(StreetType.STREET_TYPE_NAME, getAltLocale(locale), correction.getText(StreetTypeCorrection.STREET_TYPE_NAME, getAltLocale(locale)));
        item.setText(StreetType.STREET_TYPE_SHORT_NAME, locale, correction.getText(StreetTypeCorrection.STREET_TYPE_SHORT_NAME, locale));
        item.setText(StreetType.STREET_TYPE_SHORT_NAME, getAltLocale(locale), correction.getText(StreetTypeCorrection.STREET_TYPE_SHORT_NAME, getAltLocale(locale)));

        return catalogService.update(item, date);
    }
}
