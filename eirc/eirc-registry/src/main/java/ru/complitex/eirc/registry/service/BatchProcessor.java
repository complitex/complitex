package ru.complitex.eirc.registry.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Pavel Sknar
 */
public class BatchProcessor<T> implements Serializable {

    private final Logger log = LoggerFactory.getLogger(BatchProcessor.class);

    private JobProcessor processor;
    private Semaphore semaphore;

    private CountDownLatch latch;
    private ReentrantLock lock = new ReentrantLock();

    private int batchSize;

    public BatchProcessor(int batchSize, JobProcessor processor) {
        this.processor = processor;
        this.batchSize = batchSize;
        semaphore = new Semaphore(batchSize);
    }

    public Future<T> processJob(final Callable<T> job) {

        Callable<T> innerJob = new Callable<T>() {
            @Override
            public T call() throws Exception {
                try {
                    return job.call();
                } finally {
                    lock.lock();
                    semaphore.release();
                    jobFinalize();
                    lock.unlock();
                }
            }
        };

        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            //
        }

        return processor.processJob(innerJob);

    }

    public void waitEndWorks() {
        lock.lock();
        int countWork = batchSize - semaphore.availablePermits();
        if (latch == null && countWork > 0) {
            latch = new CountDownLatch(countWork);
        }
        lock.unlock();
        try {
            if (latch != null) {
                latch.await();
            }
        } catch (InterruptedException e) {
            //
        }
        latch = null;
    }

    protected void jobFinalize() {
        log.debug("finalize job");
        if (latch != null) {
            latch.countDown();
            log.debug("finalize latch: {}", latch);
        }
    }
}