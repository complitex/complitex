package ru.flexpay.eirc.registry.service;

import ru.complitex.common.exception.ExecuteException;

/**
 * @author Pavel Sknar
 */
//TODO Migrate to java.util.concurrent.Callable
public interface AbstractJob<T> {

    T execute() throws ExecuteException;
}
