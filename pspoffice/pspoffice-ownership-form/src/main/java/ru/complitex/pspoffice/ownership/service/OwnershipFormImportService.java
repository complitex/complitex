package ru.complitex.pspoffice.ownership.service;

import au.com.bytecode.opencsv.CSVReader;
import ru.complitex.common.entity.DomainObject;
import ru.complitex.common.exception.ImportFileNotFoundException;
import ru.complitex.common.exception.ImportFileReadException;
import ru.complitex.common.service.AbstractImportService;
import ru.complitex.common.service.IImportListener;
import ru.complitex.common.util.CloneUtil;
import ru.complitex.common.util.DateUtil;
import ru.complitex.pspoffice.ownership.strategy.OwnershipFormStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.io.IOException;
import java.util.Locale;

import static ru.complitex.pspoffice.ownership.entity.OwnershipFormImportFile.OWNERSHIP_FORM;

@Stateless
public class OwnershipFormImportService extends AbstractImportService {

    private final Logger log = LoggerFactory.getLogger(OwnershipFormImportService.class);
    @EJB
    private OwnershipFormStrategy strategy;

    /**
     * OWNERSHIP_FORM_ID	Название
     * @param listener
     * @throws ImportFileNotFoundException
     * @throws ImportFileReadException
     */
    public void process(IImportListener listener, Locale locale)
            throws ImportFileNotFoundException, ImportFileReadException {
        listener.beginImport(OWNERSHIP_FORM, getRecordCount(OWNERSHIP_FORM));

        CSVReader reader = getCsvReader(OWNERSHIP_FORM);

        int recordIndex = 0;

        try {
            String[] line;

            while ((line = reader.readNext()) != null) {
                recordIndex++;

                final String externalId = line[0].trim();
                final String name = line[1].trim();

                // Ищем по externalId в базе.
                final Long objectId = null;//strategy.getObjectId(externalId);
                if (objectId != null) {
                    DomainObject oldObject = strategy.getDomainObject(objectId, true);
                    if (oldObject != null) {
                        // нашли, обновляем (или дополняем) значения атрибутов и сохраняем.
                        DomainObject newObject = CloneUtil.cloneObject(oldObject);
                        newObject.setStringValue(OwnershipFormStrategy.NAME, name, locale);

                        strategy.update(oldObject, newObject, DateUtil.getCurrentDate());
                    }
                } else {
                    // не нашли, создаём объект заполняем его атрибуты и сохраняем.
                    DomainObject object = strategy.newInstance();

                    object.setStringValue(OwnershipFormStrategy.NAME, name, locale);

                    strategy.insert(object, DateUtil.getCurrentDate());
                }
                listener.recordProcessed(OWNERSHIP_FORM, recordIndex);
            }

            listener.completeImport(OWNERSHIP_FORM, recordIndex);
        } catch (IOException | NumberFormatException e) {
            throw new ImportFileReadException(e, OWNERSHIP_FORM.getFileName(), recordIndex);
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                log.error("Ошибка закрытия потока", e);
            }
        }
    }
}
