package org.complitex.osznconnection.file.service;

import au.com.bytecode.opencsv.CSVReader;
import org.complitex.common.entity.Attribute;
import org.complitex.common.entity.DomainObject;
import org.complitex.common.service.AbstractImportService;
import org.complitex.common.service.IImportListener;
import org.complitex.common.service.exception.ImportFileNotFoundException;
import org.complitex.common.service.exception.ImportFileReadException;
import org.complitex.common.strategy.StringCultureBean;
import org.complitex.common.util.DateUtil;
import org.complitex.common.util.StringCultures;
import org.complitex.osznconnection.file.strategy.PrivilegeStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.io.IOException;

import static org.complitex.osznconnection.file.entity.PrivilegeImportFile.PRIVILEGE;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 02.03.11 15:15
 */
@Stateless
public class PrivilegeImportService extends AbstractImportService{
    private final Logger log = LoggerFactory.getLogger(PrivilegeImportService.class);

    @EJB
    private PrivilegeStrategy privilegeStrategy;

    @EJB
    private StringCultureBean stringCultureBean;

    /**
     * PRIVILEGE_ID	Код	Короткое наименование	Название привилегии
     * @param listener
     * @throws ImportFileNotFoundException
     * @throws ImportFileReadException
     */
    public void process(IImportListener listener)
            throws ImportFileNotFoundException, ImportFileReadException {
        listener.beginImport(PRIVILEGE, getRecordCount(PRIVILEGE));

        CSVReader reader = getCsvReader(PRIVILEGE);

        int recordIndex = 0;

        try {
            String[] line;

            while ((line = reader.readNext()) != null){
                recordIndex++;

                DomainObject domainObject = privilegeStrategy.newInstance();

                //PRIVILEGE_ID
                domainObject.setExternalId(line[0].trim());

                //Код
                Attribute code = domainObject.getAttribute(PrivilegeStrategy.CODE);
                StringCultures.getSystemStringCulture(code.getLocalizedValues()).setValue(line[1].trim());

                //Короткое наименование
                //todo implement in future release

                //Название привилегии
                Attribute name = domainObject.getAttribute(PrivilegeStrategy.NAME);
                StringCultures.getSystemStringCulture(name.getLocalizedValues()).setValue(line[3].trim());

                privilegeStrategy.insert(domainObject, DateUtil.getCurrentDate());

                listener.recordProcessed(PRIVILEGE, recordIndex);
            }

            listener.completeImport(PRIVILEGE, recordIndex);
        } catch (IOException | NumberFormatException e) {
            throw new ImportFileReadException(e, PRIVILEGE.getFileName(), recordIndex);
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                log.error("Ошибка закрытия потока", e);
            }
        }
    }
}


