package ru.complitex.eirc.registry.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.*;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * @author Pavel Sknar
 */
@Stateless
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class JobProcessor {

    private final Logger log = LoggerFactory.getLogger(JobProcessor.class);

    @Asynchronous
    public <T> Future<T> processJob(Callable<T> job) {

        T result;
        try {
            result = job.call();
        } catch (Exception e) {
            Thread.interrupted();
            throw new IllegalStateException(e);
        }

        return new AsyncResult<>(result);

    }
}

