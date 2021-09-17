package ru.complitex.pspoffice.address.sync.service;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSessionManager;
import org.mybatis.cdi.SqlSessionManagerRegistry;
import ru.complitex.catalog.util.Threads;
import ru.complitex.pspoffice.address.sync.entity.SyncCatalog;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Ivanov Anatoliy
 */
public abstract class SyncService implements ISyncService {
    @Inject
    private SqlSessionManagerRegistry registry;

    private final Semaphore semaphore = new Semaphore(5);

    private final AtomicBoolean error = new AtomicBoolean();

    @Override
    public void sync(int catalog, LocalDate date, int locale, ISyncListener<SyncCatalog> listener, AtomicBoolean status, SyncCatalog syncCatalog) {
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        Threads.submit(() -> {
            try {
                registry.getManagers().forEach(m -> m.startManagedSession(ExecutorType.REUSE));

                ISyncService.super.sync(catalog, date, locale, listener, status, syncCatalog);

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
    }
}
