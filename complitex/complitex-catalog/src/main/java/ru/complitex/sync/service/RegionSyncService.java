package ru.complitex.sync.service;

import ru.complitex.address.entity.Region;
import ru.complitex.catalog.entity.Item;
import ru.complitex.catalog.service.CatalogService;
import ru.complitex.correction.entity.CountryCorrection;
import ru.complitex.correction.entity.RegionCorrection;
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
public class RegionSyncService extends SyncService {
    @Inject
    private IAddressService syncCatalogService;

    @Inject
    private CatalogService catalogService;

    @Override
    public Iterator<SyncCatalog> getSyncCatalogs(LocalDate date, int locale) {
        return catalogService.getItems(CountryCorrection.CATALOG, date)
                .withReferenceId(CountryCorrection.CATALOG_ORGANIZATION, CATALOG_ORGANIZATION)
                .withReferenceId(CountryCorrection.CORRECTION_ORGANIZATION, CORRECTION_ORGANIZATION)
                .get().stream()
                .map(countryCorrection -> {
                    SyncCatalog syncCatalog = new SyncCatalog(date, locale);

                    syncCatalog.setCountry(countryCorrection.getText(CountryCorrection.COUNTRY_NAME, locale));

                    syncCatalogService.getRegionSyncs(syncCatalog);

                    return syncCatalog;
                })
                .iterator();
    }

    @Override
    public Item getCorrection(Sync sync, LocalDate date) {
        return catalogService.getItem(RegionCorrection.CATALOG, date)
                .withReferenceId(RegionCorrection.CATALOG_ORGANIZATION, CATALOG_ORGANIZATION)
                .withReferenceId(RegionCorrection.CORRECTION_ORGANIZATION, CORRECTION_ORGANIZATION)
                .withLong(RegionCorrection.REGION_ID, sync.getExternalId())
                .get();
    }

    public Long getCountryId(Long correctionCountryId, LocalDate date) {
        return catalogService.getReferenceId(CountryCorrection.CATALOG, CountryCorrection.COUNTRY, date)
                .withReferenceId(CountryCorrection.CATALOG_ORGANIZATION, CATALOG_ORGANIZATION)
                .withReferenceId(CountryCorrection.CORRECTION_ORGANIZATION, CORRECTION_ORGANIZATION)
                .withLong(CountryCorrection.COUNTRY_ID, correctionCountryId)
                .get();
    }

    @Override
    public Item getItem(Sync sync, LocalDate date, int locale) {
        return catalogService.getItem(Region.CATALOG, date)
                .withReferenceId(Region.COUNTRY, getCountryId(sync.getParentId(), date))
                .withText(Region.REGION_NAME, locale, sync.getName())
                .get();
    }

    @Override
    public Item addItem(Sync sync, LocalDate date, int locale) {
        Item item = catalogService.newItem(Region.CATALOG)
                .withReferenceId(Region.COUNTRY, getCountryId(sync.getParentId(), date))
                .withText(Region.REGION_NAME, locale, sync.getName())
                .withText(Region.REGION_NAME, getAltLocale(locale), sync.getAltName())
                .get();

        return catalogService.inserts(item, date);
    }

    @Override
    public Item addCorrection(Item item, Sync sync, LocalDate date, int locale) {
        Item correction = catalogService.newItem(RegionCorrection.CATALOG)
                .withReferenceId(RegionCorrection.REGION, item.getId())
                .withReferenceId(RegionCorrection.CATALOG_ORGANIZATION, CATALOG_ORGANIZATION)
                .withReferenceId(RegionCorrection.CORRECTION_ORGANIZATION, CORRECTION_ORGANIZATION)
                .withLong(RegionCorrection.COUNTRY_ID, sync.getParentId())
                .withLong(RegionCorrection.REGION_ID, sync.getExternalId())
                .withText(RegionCorrection.REGION_CODE, sync.getAdditionalExternalId())
                .withText(RegionCorrection.REGION_NAME, locale, sync.getName())
                .withText(RegionCorrection.REGION_NAME, getAltLocale(locale), sync.getAltName())
                .withTimestamp(RegionCorrection.SYNCHRONIZATION_DATE, date)
                .get();

        return catalogService.inserts(correction, date);
    }

    @Override
    public boolean updateCorrection(Item correction, Sync sync, LocalDate date, int locale) {
        correction.setTimestamp(RegionCorrection.SYNCHRONIZATION_DATE, date);

        catalogService.update(correction, date);

        correction.setLong(RegionCorrection.COUNTRY_ID, sync.getParentId());
        correction.setText(RegionCorrection.REGION_CODE, sync.getAdditionalExternalId());
        correction.setText(RegionCorrection.REGION_NAME, locale, sync.getName());
        correction.setText(RegionCorrection.REGION_NAME, getAltLocale(locale), sync.getAltName());

        return catalogService.update(correction, date);
    }

    @Override
    public boolean updateItem(Item correction, LocalDate date, int locale) {
        Item item = catalogService.getReferenceItem(correction, RegionCorrection.REGION, date);

        item.setReferenceId(Region.COUNTRY, getCountryId(correction.getLong(RegionCorrection.COUNTRY_ID), date));
        item.setText(Region.REGION_NAME, locale, correction.getText(RegionCorrection.REGION_NAME, locale));
        item.setText(Region.REGION_NAME, getAltLocale(locale), correction.getText(RegionCorrection.REGION_NAME, getAltLocale(locale)));

        return catalogService.update(item, date);
    }
}
