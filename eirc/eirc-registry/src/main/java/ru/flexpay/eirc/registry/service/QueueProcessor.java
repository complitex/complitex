package ru.flexpay.eirc.registry.service;

import com.google.common.collect.Queues;
import org.complitex.common.service.executor.ExecuteException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.Semaphore;

/**
 * @author Pavel Sknar
 */
public abstract class QueueProcessor {

    private final Logger log = LoggerFactory.getLogger(QueueProcessor.class);

    private Queue<Callable<Void>> jobs = Queues.newConcurrentLinkedQueue();

    private int MAX_AVAILABLE = 10;
    private final Semaphore available = new Semaphore(0, false);
    private final Semaphore fullQueue = new Semaphore(MAX_AVAILABLE, false);

    private JobProcessor processor;

    public QueueProcessor() {
    }

    protected void setProcessor(JobProcessor processor) {
        this.processor = processor;
    }

    private Callable<Void> getItem() throws InterruptedException {
        available.acquire();
        fullQueue.acquire();
        return jobs.poll();
    }

    public void execute(Callable<Void> job) {
        jobs.add(job);
        available.release();
    }

    public void run() throws InterruptedException {
        processor.processJob(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                while (true) {

                    final Callable<Void> job;
                    try {
                        job = getItem();
                    } catch (InterruptedException e) {
                        throw new ExecuteException(e, "Interrupted");
                    }
                    processor.processJob(new Callable<Object>() {
                        @Override
                        public Object call() throws Exception {
                            try {
                                return job.call();
                            } finally {
                                fullQueue.release();
                            }
                        }
                    });

                }
            }
        });
    }

}
