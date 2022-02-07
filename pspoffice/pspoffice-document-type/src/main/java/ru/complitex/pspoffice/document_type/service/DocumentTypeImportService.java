package ru.complitex.pspoffice.document_type.service;

import au.com.bytecode.opencsv.CSVReader;
import ru.complitex.common.entity.DomainObject;
import ru.complitex.common.entity.StringValue;
import ru.complitex.common.exception.ImportFileNotFoundException;
import ru.complitex.common.exception.ImportFileReadException;
import ru.complitex.common.service.AbstractImportService;
import ru.complitex.common.service.IImportListener;
import ru.complitex.common.util.CloneUtil;
import ru.complitex.common.util.DateUtil;
import ru.complitex.pspoffice.document_type.strategy.DocumentTypeStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.io.IOException;
import java.util.Collection;
import java.util.Locale;

import static ru.complitex.pspoffice.document_type.entity.DocumentTypeImportFile.DOCUMENT_TYPE;

@Stateless
public class DocumentTypeImportService extends AbstractImportService {
    private final Logger log = LoggerFactory.getLogger(DocumentTypeImportService.class);

    @EJB
    private DocumentTypeStrategy strategy;

    /**
     * DOCUMENT_TYPE_ID     Название
     * @param listener
     * @throws ImportFileNotFoundException
     * @throws ImportFileReadException
     */
    public void process(IImportListener listener, Locale locale)
            throws ImportFileNotFoundException, ImportFileReadException {
        listener.beginImport(DOCUMENT_TYPE, getRecordCount(DOCUMENT_TYPE));

        CSVReader reader = getCsvReader(DOCUMENT_TYPE);

        int recordIndex = 0;

        try {
            String[] line;

            final Collection<StringValue> reservedObjectNames = strategy.reservedNames();

            while ((line = reader.readNext()) != null) {
                recordIndex++;

                final String externalId = line[0].trim();
                final String name = line[1].trim();

                // Сначала ищем среди предопределенных системой объектов.
                boolean isReserved = false;
                for (StringValue string : reservedObjectNames) {
                    final String reservedName = string.getValue();
                    if (reservedName != null && reservedName.equalsIgnoreCase(name)) {
                        // нашли
                        isReserved = true;
                        break;
                    }
                }
                if (!isReserved) {
                    // Ищем по externalId в базе.
                    final Long objectId = null;//strategy.getObjectId(externalId);
                    if (objectId != null) {
                        DomainObject oldObject = strategy.getDomainObject(objectId, true);
                        if (oldObject != null) {
                            // нашли, обновляем (или дополняем) значения атрибутов и сохраняем.
                            DomainObject newObject = CloneUtil.cloneObject(oldObject);
                            newObject.setStringValue(DocumentTypeStrategy.NAME, name, locale);

                            strategy.update(oldObject, newObject, DateUtil.getCurrentDate());
                        }
                    } else {
                        // не нашли, создаём объект заполняем его атрибуты и сохраняем.
                        DomainObject object = strategy.newInstance();

                        object.setStringValue(DocumentTypeStrategy.NAME, name, locale);
                        strategy.insert(object, DateUtil.getCurrentDate());
                    }
                    listener.recordProcessed(DOCUMENT_TYPE, recordIndex);
                }
            }
            listener.completeImport(DOCUMENT_TYPE, recordIndex);
        } catch (IOException | NumberFormatException e) {
            throw new ImportFileReadException(e, DOCUMENT_TYPE.getFileName(), recordIndex);
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                log.error("Ошибка закрытия потока", e);
            }
        }
    }
}
