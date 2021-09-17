package ru.complitex.pspoffice.address.sync.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.complitex.catalog.entity.Item;
import ru.complitex.catalog.entity.Locale;
import ru.complitex.pspoffice.address.sync.entity.Sync;
import ru.complitex.pspoffice.address.sync.entity.SyncCatalog;

import java.time.LocalDate;
import java.util.Iterator;

/**
 * @author Ivanov Anatoliy
 */
public interface ISyncService {
    Logger log = LoggerFactory.getLogger(ISyncService.class);

    Long CATALOG_ORGANIZATION = 1L;
    Long CORRECTION_ORGANIZATION = 2L;

    default int getAltLocale(int locale) {
        return switch (locale) {
            case Locale.RU -> Locale.UA;
            case Locale.UA -> Locale.RU;
            default -> throw new IllegalStateException();
        };
    }

    Iterator<SyncCatalog> getSyncCatalogs(LocalDate date, int locale);

    Item getCorrection(Sync sync, LocalDate date);

    Item getItem(Sync sync, LocalDate date, int locale);

    Item addItem(Sync sync, LocalDate date, int locale);

    Item addCorrection(Item item, Sync sync, LocalDate date, int locale);

    boolean updateCorrection(Item correction, Sync sync, LocalDate date, int locale);

    boolean updateItem(Item correction, LocalDate date, int locale);
}
