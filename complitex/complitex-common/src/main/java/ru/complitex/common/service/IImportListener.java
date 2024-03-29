package ru.complitex.common.service;

import ru.complitex.common.entity.IImportFile;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 28.02.11 16:10
 */
public interface IImportListener {

    public void beginImport(IImportFile importFile, int recordCount);

    public void recordProcessed(IImportFile importFile, int recordIndex);

    public void completeImport(IImportFile importFile, int recordCount);
    
    public void warn(IImportFile importFile, String message);
}
