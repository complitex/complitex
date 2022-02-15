package ru.complitex.pspoffice.address.sync.service;

import ru.complitex.catalog.entity.Item;
import ru.complitex.catalog.service.CatalogService;
import ru.complitex.pspoffice.address.entity.Country;
import ru.complitex.pspoffice.address.correction.entity.CountryCorrection;
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
public class CountrySyncService extends SyncService {
    @Inject
    @SyncProducer
    private SyncMapper syncMapper;

    @Inject
    private CatalogService catalogService;

    public Iterator<SyncCatalog> getSyncCatalogs(LocalDate date, int locale) {
        SyncCatalog syncCatalog = new SyncCatalog(date, locale);

        syncMapper.callCountrySyncs(syncCatalog);

        return Collections.singletonList(syncCatalog).iterator();
    }

    @Override
    public Item getCorrection(Sync sync, LocalDate date) {
        return catalogService.getItem(CountryCorrection.CATALOG, date)
                .withReferenceId(CountryCorrection.CATALOG_ORGANIZATION, CATALOG_ORGANIZATION)
                .withReferenceId(CountryCorrection.CORRECTION_ORGANIZATION, CORRECTION_ORGANIZATION)
                .withLong(CountryCorrection.COUNTRY_ID, sync.getExternalId())
                .get();
    }

    @Override
    public Item getItem(Sync sync, LocalDate date, int locale) {
        return catalogService.getItem(Country.CATALOG, date)
                .withText(Country.COUNTRY_NAME, locale, sync.getName())
                .get();
    }

    @Override
    public Item addItem(Sync sync, LocalDate date, int locale) {
        Item item = catalogService.newItem(Country.CATALOG)
                .withText(Country.COUNTRY_NAME, locale, sync.getName())
                .withText(Country.COUNTRY_NAME, getAltLocale(locale), sync.getAltName())
                .get();

        return catalogService.inserts(item, date);
    }

    @Override
    public Item addCorrection(Item item, Sync sync, LocalDate date, int locale) {
        Item correction = catalogService.newItem(CountryCorrection.CATALOG)
                .withReferenceId(CountryCorrection.COUNTRY, item.getId())
                .withReferenceId(CountryCorrection.CATALOG_ORGANIZATION, CATALOG_ORGANIZATION)
                .withReferenceId(CountryCorrection.CORRECTION_ORGANIZATION, CORRECTION_ORGANIZATION)
                .withLong(CountryCorrection.COUNTRY_ID, sync.getExternalId())
                .withText(CountryCorrection.COUNTRY_CODE, sync.getAdditionalExternalId())
                .withText(CountryCorrection.COUNTRY_NAME, locale, sync.getName())
                .withText(CountryCorrection.COUNTRY_NAME, getAltLocale(locale), sync.getAltName())
                .withTimestamp(CountryCorrection.SYNCHRONIZATION_DATE, date)
                .get();

        return catalogService.inserts(correction, date);
    }

    @Override
    public boolean updateCorrection(Item correction, Sync sync, LocalDate date, int locale) {
        correction.setTimestamp(CountryCorrection.SYNCHRONIZATION_DATE, date);

        catalogService.update(correction, date);

        correction.setText(CountryCorrection.COUNTRY_CODE, sync.getAdditionalExternalId());
        correction.setText(CountryCorrection.COUNTRY_NAME, locale, sync.getName());
        correction.setText(CountryCorrection.COUNTRY_NAME, getAltLocale(locale), sync.getAltName());

        return catalogService.update(correction, date);
    }

    @Override
    public boolean updateItem(Item correction, LocalDate date, int locale) {
        Item item = catalogService.getReferenceItem(correction, CountryCorrection.COUNTRY, date);

        item.setText(Country.COUNTRY_NAME, locale, correction.getText(CountryCorrection.COUNTRY_NAME, locale));
        item.setText(Country.COUNTRY_NAME, getAltLocale(locale), correction.getText(CountryCorrection.COUNTRY_NAME, getAltLocale(locale)));

        return catalogService.update(item, date);
    }
}
