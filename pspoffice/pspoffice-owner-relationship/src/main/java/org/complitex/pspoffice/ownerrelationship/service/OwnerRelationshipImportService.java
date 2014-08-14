package org.complitex.pspoffice.ownerrelationship.service;

import au.com.bytecode.opencsv.CSVReader;
import org.complitex.common.entity.DomainObject;
import org.complitex.common.entity.StringCulture;
import org.complitex.common.service.AbstractImportService;
import org.complitex.common.service.IImportListener;
import org.complitex.common.service.exception.ImportFileNotFoundException;
import org.complitex.common.service.exception.ImportFileReadException;
import org.complitex.common.util.CloneUtil;
import org.complitex.common.util.DateUtil;
import org.complitex.pspoffice.ownerrelationship.strategy.OwnerRelationshipStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.io.IOException;
import java.util.Collection;
import java.util.Locale;

import static org.complitex.pspoffice.ownerrelationship.entity.OwnerRelationshipImportFile.OWNER_RELATIONSHIP;

@Stateless
public class OwnerRelationshipImportService extends AbstractImportService {

    private final Logger log = LoggerFactory.getLogger(OwnerRelationshipImportService.class);

    @EJB
    private OwnerRelationshipStrategy strategy;


    /**
     * OWNER_RELATIONSHIP_ID	Название
     * @param listener
     * @throws ImportFileNotFoundException
     * @throws ImportFileReadException
     */
    public void process(IImportListener listener, Locale locale)
            throws ImportFileNotFoundException, ImportFileReadException {
        listener.beginImport(OWNER_RELATIONSHIP, getRecordCount(OWNER_RELATIONSHIP));

        CSVReader reader = getCsvReader(OWNER_RELATIONSHIP);

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
                            newObject.setStringValue(OwnerRelationshipStrategy.NAME, name, locale);

                            strategy.update(oldObject, newObject, DateUtil.getCurrentDate());
                        }
                    } else {
                        // не нашли, создаём объект заполняем его атрибуты и сохраняем.
                        DomainObject object = strategy.newInstance();
                        object.setExternalId(externalId);
                        object.setStringValue(OwnerRelationshipStrategy.NAME, name, locale);
                        strategy.insert(object, DateUtil.getCurrentDate());
                    }
                }

                listener.recordProcessed(OWNER_RELATIONSHIP, recordIndex);
            }

            listener.completeImport(OWNER_RELATIONSHIP, recordIndex);
        } catch (IOException | NumberFormatException e) {
            throw new ImportFileReadException(e, OWNER_RELATIONSHIP.getFileName(), recordIndex);
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                log.error("Ошибка закрытия потока", e);
            }
        }
    }
}
