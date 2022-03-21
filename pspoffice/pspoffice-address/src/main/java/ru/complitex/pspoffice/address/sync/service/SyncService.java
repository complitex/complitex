package ru.complitex.pspoffice.address.sync.service;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSessionManager;
import org.mybatis.cdi.SqlSessionManagerRegistry;
import ru.complitex.catalog.entity.Item;
import ru.complitex.pspoffice.address.sync.util.Threads;
import ru.complitex.pspoffice.address.sync.entity.SyncCatalog;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Ivanov Anatoliy
 */
public abstract class SyncService implements ISyncService {
    @Inject
    private SqlSessionManagerRegistry registry;

    private final Semaphore semaphore = new Semaphore(8);

    private final AtomicBoolean error = new AtomicBoolean();

    private void sync(int catalog, LocalDate date, int locale, ISyncListener<SyncCatalog> listener, AtomicBoolean status, SyncCatalog syncCatalog) {
        if (syncCatalog.getCode() == 1) {
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
                    if (updateCorrection(correction, sync, date, locale)) {
                        listener.onUpdateCorrection(catalog, correction, sync);
                    }

                    if (updateItem(correction, date, locale)) {
                        listener.onUpdate(catalog, correction, sync);
                    }
                }
            });
        } else {
            syncCatalog.setSyncs(null);

            log.error("error code {}", syncCatalog);

            listener.onError(catalog, syncCatalog, null, null, null, new RuntimeException("error code"));
        }
    }

    public Future<?> asyncSync(int catalog, LocalDate date, int locale, ISyncListener<SyncCatalog> listener, AtomicBoolean status, SyncCatalog syncCatalog) {
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        Future<?> future = Threads.submit(() -> {
            try {
                registry.getManagers().forEach(m -> m.startManagedSession(ExecutorType.REUSE));

                sync(catalog, date, locale, listener, status, syncCatalog);

                registry.getManagers().forEach(SqlSessionManager::commit);

                semaphore.release();
            } catch (Exception e) {
                log.error("sync error ", e);

                registry.getManagers().forEach(SqlSessionManager::rollback);

                error.set(true);

                throw e;
            } finally {
                registry.getManagers().forEach(SqlSessionManager::close);
            }
        });

        if (error.get()) {
            throw new RuntimeException();
        }

        return future;
    }

    public void sync(int catalog, LocalDate date, int locale, ISyncListener<SyncCatalog> listener, AtomicBoolean status) {
        List<Future<?>> futures = new ArrayList<>();

        getSyncCatalogs(date, locale).forEachRemaining(syncCatalog -> {
            if (status.get()) {
                futures.add(asyncSync(catalog, date, locale, listener, status, syncCatalog));
            } else {
                throw new RuntimeException("canceled");
            }
        });

        futures.forEach(future -> {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        });

        listener.onSynced(catalog);
    }
}
