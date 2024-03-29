package ru.complitex.eirc.registry.service.handle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.complitex.eirc.registry.service.JobProcessor;
import ru.complitex.eirc.registry.service.QueueProcessor;

import javax.annotation.PostConstruct;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.EJB;
import javax.ejb.Singleton;

/**
 * @author Pavel Sknar
 */
@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class HandleQueueProcessor extends QueueProcessor {

    private static final Logger log = LoggerFactory.getLogger(HandleQueueProcessor.class);

    @EJB
    private JobProcessor processor;

    @PostConstruct
    @Override
    public void run() {
        setProcessor(processor);
        log.info("run");
        try {
            super.run();
        } catch (InterruptedException e) {
            //
        }
        log.info("runned");
    }
}
