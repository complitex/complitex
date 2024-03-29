package ru.complitex.eirc.registry.service.parse;

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
public class ParserQueueProcessor extends QueueProcessor {

    private final Logger log = LoggerFactory.getLogger(ParserQueueProcessor.class);

    @EJB(name = "JobProcessor")
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
