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
import org.complitex.osznconnection.file.strategy.OwnershipStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.io.IOException;

import static org.complitex.osznconnection.file.entity.OwnershipImportFile.OWNERSHIP;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 01.03.11 18:50
 */
@Stateless
public class OwnershipImportService extends AbstractImportService{
    private final Logger log = LoggerFactory.getLogger(OwnershipImportService.class);

    @EJB
    private OwnershipStrategy ownershipStrategy;

    @EJB
    private StringCultureBean stringCultureBean;

    /**
     * OWNERSHIP_ID	Название формы собственности
     * @param listener
     * @throws ImportFileNotFoundException
     * @throws ImportFileReadException
     */
    public void process(IImportListener listener)
            throws ImportFileNotFoundException, ImportFileReadException {
        listener.beginImport(OWNERSHIP, getRecordCount(OWNERSHIP));

        CSVReader reader = getCsvReader(OWNERSHIP);

        int recordIndex = 0;

        try {
            String[] line;

            while ((line = reader.readNext()) != null){
                recordIndex++;

                DomainObject domainObject = ownershipStrategy.newInstance();
                Attribute name = domainObject.getAttribute(OwnershipStrategy.NAME);

                //OWNERSHIP_ID
                domainObject.setExternalId(line[0].trim());

                //Название формы собственности
                StringCultures.getSystemStringCulture(name.getStringCultures()).setValue(line[1].trim());

                ownershipStrategy.insert(domainObject, DateUtil.getCurrentDate());

                listener.recordProcessed(OWNERSHIP, recordIndex);
            }

            listener.completeImport(OWNERSHIP, recordIndex);
        } catch (IOException e) {
            throw new ImportFileReadException(e, OWNERSHIP.getFileName(), recordIndex);
        } catch (NumberFormatException e){
            throw new ImportFileReadException(e, OWNERSHIP.getFileName(), recordIndex);
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                log.error("Ошибка закрытия потока", e);
            }
        }
    }
}
