package ru.flexpay.eirc;

import org.apache.commons.lang.time.StopWatch;
import org.apache.ibatis.io.Resources;
import org.complitex.common.EjbTestBeanLocator;
import org.complitex.common.entity.FilterWrapper;
import org.complitex.common.service.executor.ExecuteException;
import ru.flexpay.eirc.registry.entity.Registry;
import ru.flexpay.eirc.registry.entity.RegistryStatus;
import ru.flexpay.eirc.registry.service.AbstractFinishCallback;
import ru.flexpay.eirc.registry.service.AbstractMessenger;
import ru.flexpay.eirc.registry.service.RegistryBean;
import ru.flexpay.eirc.registry.service.handle.RegistryHandler;
import ru.flexpay.eirc.registry.service.link.RegistryLinker;
import ru.flexpay.eirc.registry.service.parse.RegistryParser;

import javax.naming.Context;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Pavel Sknar
 */
public abstract class RegistryTestUtil {
    public static Registry processingRegistry(Context context, String fileName)
            throws IOException, ExecuteException, InterruptedException {
        RegistryParser registryParser = EjbTestBeanLocator.getBean(context, "RegistryParser");
        RegistryLinker registryLinker = EjbTestBeanLocator.getBean(context, "RegistryLinker");
        RegistryHandler registryHandler = EjbTestBeanLocator.getBean(context, "RegistryHandler");
        RegistryBean registryBean = EjbTestBeanLocator.getBean(context, "RegistryBean");

        AbstractMessenger messenger = getMessengerInstance();
        AbstractFinishCallback finishCallback = getFinishCallback();

        Registry registry = null;
        try {
            InputStream inputStream = Resources.getResourceAsStream("data/" + fileName);
            StopWatch watch = new StopWatch();

            watch.start();
            registry = registryParser.parse(messenger, finishCallback, inputStream, fileName, 1000, 1000);
            while (!finishCallback.isCompleted()) {
                Thread.sleep(1000L);
            }
            watch.stop();

            System.out.println("Parse watch time: " + watch.toString());
            watch.reset();

            assertNotNull("Registry did not create", registry);
            assertEquals("Registry parsed with errors", RegistryStatus.LOADED, registry.getStatus());

            watch.start();
            registryLinker.link(registry.getId(), messenger, finishCallback);
            while (!finishCallback.isCompleted()) {
                Thread.sleep(1000L);
            }
            watch.stop();

            System.out.println("Link watch time: " + watch.toString());
            watch.reset();
            List<Registry> registries = registryBean.getRegistries(FilterWrapper.of(new Registry(registry.getId())));
            assertEquals("Registry linked with errors", RegistryStatus.LINKED, registries.get(0).getStatus());

            watch.start();
            registryHandler.handle(registry.getId(), messenger, finishCallback);
            while (!finishCallback.isCompleted()) {
                Thread.sleep(1000L);
            }
            watch.stop();

            System.out.println("Handle watch time: " + watch.toString());
            watch.reset();

            registries = registryBean.getRegistries(FilterWrapper.of(new Registry(registry.getId())));
            assertEquals("Registry handle with errors", RegistryStatus.PROCESSED, registries.get(0).getStatus());
        } catch (Throwable th) {
            if (registry != null) {
                registryBean.delete(registry);
            }
            throw th;
        }

        return registry;
    }

    public static AbstractMessenger getMessengerInstance() {
        return new StdoutMessenger();
    }

    public static AbstractFinishCallback getFinishCallback() {
        return new AbstractFinishCallback() {
            private AtomicInteger counter = new AtomicInteger(0);

            @Override
            protected AtomicInteger getCounter() {
                return counter;
            }

            @Override
            public void setProcessId(Long processId) {

            }
        };
    }
}
