package ru.complitex.pspoffice.address.util;

import org.jboss.weld.context.api.ContextualInstance;
import org.jboss.weld.context.bound.BoundLiteral;
import org.jboss.weld.context.bound.BoundRequestContext;
import org.jboss.weld.manager.api.WeldManager;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.spi.CDI;
import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * @author Ivanov Anatoliy
 */
public class Threads {
    private final static ExecutorService THREAD_POOL = Executors.newCachedThreadPool();

    public static Future<?> submit(ExecutorService executorService, Runnable task) {
        Collection<ContextualInstance<?>> requestContexts = CDI.current().select(WeldManager.class).get()
                .getActiveWeldAlterableContexts().stream()
                .filter(c -> c.getScope().equals(RequestScoped.class))
                .flatMap(c -> c.getAllContextualInstances().stream())
                .collect(Collectors.toList());

        return executorService.submit(() -> {
            BoundRequestContext requestContext = CDI.current().select(WeldManager.class).get().instance()
                    .select(BoundRequestContext.class, BoundLiteral.INSTANCE)
                    .get();

            requestContext.associate(new HashMap<>());
            requestContext.activate();
            requestContext.clearAndSet(requestContexts);

            task.run();

            requestContext.deactivate();
        });
    }

    public static Future<?> submit(Runnable task) {
        return submit(THREAD_POOL, task);
    }
}
