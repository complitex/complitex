package org.complitex.pspoffice.document_type.service;

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
import org.complitex.pspoffice.document_type.strategy.DocumentTypeStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.io.IOException;
import java.util.Collection;
import java.util.Locale;

import static org.complitex.pspoffice.document_type.entity.DocumentTypeImportFile.DOCUMENT_TYPE;

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

            final Collection<StringCulture> reservedObjectNames = strategy.reservedNames();

            while ((line = reader.readNext()) != null) {
                recordIndex++;

                final String externalId = line[0].trim();
                final String name = line[1].trim();

                // Сначала ищем среди предопределенных системой объектов.
                boolean isReserved = false;
                for (StringCulture string : reservedObjectNames) {
                    final String reservedName = string.getValue();
                    if (reservedName != null && reservedName.equalsIgnoreCase(name)) {
                        // нашли
                        isReserved = true;
                        break;
                    }
                }
                if (!isReserved) {
                    // Ищем по externalId в базе.
                    final Long objectId = strategy.getObjectId(externalId);
                    if (objectId != null) {
                        DomainObject oldObject = strategy.findById(objectId, true);
                        if (oldObject != null) {
                            // нашли, обновляем (или дополняем) значения атрибутов и сохраняем.
                            DomainObject newObject = CloneUtil.cloneObject(oldObject);
                            newObject.setStringValue(DocumentTypeStrategy.NAME, name, locale);

                            strategy.update(oldObject, newObject, DateUtil.getCurrentDate());
                        }
                    } else {
                        // не нашли, создаём объект заполняем его атрибуты и сохраняем.
                        DomainObject object = strategy.newInstance();
                        object.setExternalId(externalId);
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
