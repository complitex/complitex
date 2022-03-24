package ru.complitex.sync.service;

import java.util.concurrent.Future;

/**
 * @author Ivanov Anatoliy
 */
public interface IThreadService {
    Future<?> submit(Runnable task);
}
