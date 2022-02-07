package ru.complitex.common.service;

import java.io.Serializable;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 10.09.12 17:58
 */
public interface IProcessListener<T> extends Serializable{
    void processed(T object);
    void skip(T object);
    void error(T object, Exception e);
    void done();
}
