package ru.flexpay.eirc.registry.entity;

import org.complitex.common.service.exception.AbstractException;

import java.io.IOException;

/**
 * @author Pavel Sknar
 */
public interface DataSource {

    Registry getRegistry();

    RegistryRecordData getNextRecord() throws AbstractException, IOException;

}
