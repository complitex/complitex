package ru.flexpay.eirc.registry.service;

import org.complitex.common.service.executor.ExecuteException;

/**
 * @author Pavel Sknar
 */
//TODO Migrate to java.util.concurrent.Callable
public interface AbstractJob<T> {

    T execute() throws ExecuteException;
}
