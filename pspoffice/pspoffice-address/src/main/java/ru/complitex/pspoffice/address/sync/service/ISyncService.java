package ru.complitex.pspoffice.address.sync.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.complitex.catalog.entity.Item;
import ru.complitex.catalog.entity.Locale;
import ru.complitex.catalog.util.Threads;
import ru.complitex.pspoffice.address.sync.entity.Sync;
import ru.complitex.pspoffice.address.sync.entity.SyncCatalog;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

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

    default void sync(int catalog, LocalDate date, int locale, ISyncListener<SyncCatalog> listener, AtomicBoolean status) {
        getSyncCatalogs(date, locale).forEachRemaining(syncCatalog -> {
            if (syncCatalog.getCode() == 1) {
                List<Future<?>> futures = new ArrayList<>();

                syncCatalog.getSyncs().forEach(sync -> {
                    listener.onSync(catalog, sync);

                    Item correction = getCorrection(sync, date);

                    if (correction == null) {
                        Item item = getItem(sync, date, locale);

                        if (item == null) {
                            item = addItem(sync, date, locale);

                            listener.onAdd(catalog, item, sync);
                        }

                        correction = addCorrection(item, sync, date, locale);

                        listener.onAddCorrection(catalog, correction, sync);
                    } else {
                        Item updateCorrection = correction;

                        futures.add(Threads.submit(() -> {
                            if (updateCorrection(updateCorrection, sync, date, locale)) {
                                listener.onUpdateCorrection(catalog, updateCorrection, sync);
                            }
                        }));

                        futures.add(Threads.submit(() -> {
                            if (updateItem(updateCorrection, date, locale)) {
                                listener.onUpdate(catalog, updateCorrection, sync);
                            }
                        }));
                    }
                });

                futures.removeIf(future -> {
                    try {
                        return future.get() != null;
                    } catch (InterruptedException | ExecutionException e) {
                        listener.onError(catalog, syncCatalog, null, null, null, new RuntimeException(e));

                        e.printStackTrace();

                        throw new RuntimeException(e);
                    }
                });
            } else {
                syncCatalog.setSyncs(null);

                log.error("error code {}", syncCatalog);

                listener.onError(catalog, syncCatalog, null, null, null, new RuntimeException("error code"));
            }

            if (!status.get()) {
                throw new RuntimeException("canceled");
            }
        });

        listener.onSynced(catalog);
    }
}
