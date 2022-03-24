package ru.complitex.eirc.registry.entity;

import ru.complitex.common.exception.AbstractException;

import java.io.IOException;

/**
 * @author Pavel Sknar
 */
public interface DataSource {

    Registry getRegistry();

    RegistryRecordData getNextRecord() throws AbstractException, IOException;

}
