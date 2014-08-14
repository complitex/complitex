package org.complitex.pspoffice.ownership.service;

import au.com.bytecode.opencsv.CSVReader;
import org.complitex.dictionary.entity.Attribute;
import org.complitex.dictionary.entity.DomainObject;
import org.complitex.dictionary.entity.StringCulture;
import org.complitex.dictionary.service.AbstractImportService;
import org.complitex.dictionary.service.IImportListener;
import org.complitex.dictionary.service.exception.ImportFileNotFoundException;
import org.complitex.dictionary.service.exception.ImportFileReadException;
import org.complitex.dictionary.util.CloneUtil;
import org.complitex.dictionary.util.DateUtil;
import org.complitex.pspoffice.ownership.strategy.OwnershipFormStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.io.IOException;
import java.util.Locale;

import static org.complitex.pspoffice.ownership.entity.OwnershipFormImportFile.OWNERSHIP_FORM;

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
                final Long objectId = strategy.getObjectId(externalId);
                if (objectId != null) {
                    DomainObject oldObject = strategy.findById(objectId, true);
                    if (oldObject != null) {
                        // нашли, обновляем (или дополняем) значения атрибутов и сохраняем.
                        DomainObject newObject = CloneUtil.cloneObject(oldObject);
                        newObject.setStringValue(OwnershipFormStrategy.NAME, name, locale);

                        strategy.update(oldObject, newObject, DateUtil.getCurrentDate());
                    }
                } else {
                    // не нашли, создаём объект заполняем его атрибуты и сохраняем.
                    DomainObject object = strategy.newInstance();
                    object.setExternalId(externalId);
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
