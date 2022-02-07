package ru.complitex.common.service;

import au.com.bytecode.opencsv.CSVReader;
import nl.knaw.dans.common.dbflib.Table;
import ru.complitex.common.entity.DictionaryConfig;
import ru.complitex.common.entity.IImportFile;
import ru.complitex.common.exception.ImportFileNotFoundException;
import ru.complitex.common.exception.ImportFileReadException;
import ru.complitex.common.util.ImportStorageUtil;

import javax.ejb.EJB;
import java.io.InputStream;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 02.03.11 16:40
 */
public abstract class AbstractImportService {

    @EJB
    private ConfigBean configBean;

    protected String getDir(){
        return configBean.getString(DictionaryConfig.IMPORT_FILE_STORAGE_DIR, false);
    }

    protected int getRecordCount(String fileName) throws ImportFileNotFoundException, ImportFileReadException {
        return ImportStorageUtil.getRecordCount(getDir(), fileName);
    }

    protected CSVReader getCsvReader(String fileName) throws ImportFileNotFoundException {
        return getCsvReader(fileName, "cp1251", ',');
    }

    protected CSVReader getCsvReader(String fileName, String charsetName, char separator) throws ImportFileNotFoundException {
        return ImportStorageUtil.getCsvReader(getDir(), fileName, charsetName, separator);
    }

    protected Table getDbfTable(String fileName) throws ImportFileNotFoundException, ImportFileReadException {
        return ImportStorageUtil.getDbfTable(getDir(), fileName, "cp866");
    }

    protected String[] getFileList(String dir, String extension){
        return ImportStorageUtil.getFileList(dir, extension);
    }

    protected InputStream getInputStream(String fileName) throws ImportFileNotFoundException {
        return ImportStorageUtil.getInputStream(getDir(), fileName);
    }

    protected int getRecordCount(IImportFile file) throws ImportFileNotFoundException, ImportFileReadException {
        return getRecordCount(file.getFileName());
    }

    protected CSVReader getCsvReader(IImportFile file) throws ImportFileNotFoundException {
        return getCsvReader(file.getFileName());
    }
}
