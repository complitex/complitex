package org.complitex.pspoffice.departure_reason.service;

import au.com.bytecode.opencsv.CSVReader;
import org.complitex.dictionary.entity.Attribute;
import org.complitex.dictionary.entity.DomainObject;
import org.complitex.dictionary.entity.StringCulture;
import org.complitex.dictionary.service.AbstractImportService;
import org.complitex.dictionary.service.IImportListener;
import org.complitex.dictionary.service.LocaleBean;
import org.complitex.dictionary.service.exception.ImportFileNotFoundException;
import org.complitex.dictionary.service.exception.ImportFileReadException;
import org.complitex.dictionary.util.CloneUtil;
import org.complitex.dictionary.util.DateUtil;
import org.complitex.pspoffice.departure_reason.strategy.DepartureReasonStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.io.IOException;
import java.util.Locale;

import static org.complitex.pspoffice.departure_reason.entity.DepartureReasonImportFile.DEPARTURE_REASON;

@Stateless
public class DepartureReasonImportService extends AbstractImportService {
    private final Logger log = LoggerFactory.getLogger(DepartureReasonImportService.class);

    @EJB
    private DepartureReasonStrategy strategy;


    /**
     * DEPARTURE_REASON_ID	Код	Название
     * @param listener
     * @throws ImportFileNotFoundException
     * @throws ImportFileReadException
     */
    public void process(IImportListener listener, Locale locale)
            throws ImportFileNotFoundException, ImportFileReadException {
        listener.beginImport(DEPARTURE_REASON, getRecordCount(DEPARTURE_REASON));

        CSVReader reader = getCsvReader(DEPARTURE_REASON);

        int recordIndex = 0;

        try {
            String[] line;

            while ((line = reader.readNext()) != null) {
                recordIndex++;

                final String externalId = line[0].trim();
                final String code = line[1].trim();
                final String name = line[2].trim();

                // Ищем по externalId в базе.
                final Long objectId = strategy.getObjectId(externalId);
                if (objectId != null) {
                    DomainObject oldObject = strategy.findById(objectId, true);
                    if (oldObject != null) {
                        // нашли, обновляем (или дополняем) значения атрибутов и сохраняем.
                        DomainObject newObject = CloneUtil.cloneObject(oldObject);
                        newObject.setStringValue(DepartureReasonStrategy.NAME, name, locale);

                        strategy.update(oldObject, newObject, DateUtil.getCurrentDate());
                    }
                } else {
                    // не нашли, создаём объект заполняем его атрибуты и сохраняем.
                    DomainObject object = strategy.newInstance();
                    object.setExternalId(externalId);
                    object.setStringValue(DepartureReasonStrategy.CODE, code);
                    object.setStringValue(DepartureReasonStrategy.NAME, name, locale);

                    strategy.insert(object, DateUtil.getCurrentDate());
                }
                listener.recordProcessed(DEPARTURE_REASON, recordIndex);
            }
            listener.completeImport(DEPARTURE_REASON, recordIndex);
        } catch (IOException | NumberFormatException e) {
            throw new ImportFileReadException(e, DEPARTURE_REASON.getFileName(), recordIndex);
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                log.error("Ошибка закрытия потока", e);
            }
        }
    }
}
