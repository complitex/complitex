package org.complitex.pspoffice.registration_type.service;

import au.com.bytecode.opencsv.CSVReader;
import org.complitex.common.entity.DomainObject;
import org.complitex.common.entity.StringValue;
import org.complitex.common.exception.ImportFileNotFoundException;
import org.complitex.common.exception.ImportFileReadException;
import org.complitex.common.service.AbstractImportService;
import org.complitex.common.service.IImportListener;
import org.complitex.common.util.CloneUtil;
import org.complitex.common.util.DateUtil;
import org.complitex.pspoffice.registration_type.strategy.RegistrationTypeStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.io.IOException;
import java.util.Collection;
import java.util.Locale;

import static org.complitex.pspoffice.registration_type.entity.RegistrationTypeImportFile.REGISTRATION_TYPE;

@Stateless
public class RegistrationTypeImportService extends AbstractImportService {
    private final Logger log = LoggerFactory.getLogger(RegistrationTypeImportService.class);

    @EJB
    private RegistrationTypeStrategy strategy;


    /**
     * REGISTRATION_TYPE_ID	Код	Название
     * @param listener
     * @throws ImportFileNotFoundException
     * @throws ImportFileReadException
     */
    public void process(IImportListener listener, Locale locale)
            throws ImportFileNotFoundException, ImportFileReadException {
        listener.beginImport(REGISTRATION_TYPE, getRecordCount(REGISTRATION_TYPE));

        CSVReader reader = getCsvReader(REGISTRATION_TYPE);

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
                if (isReserved) {
                    // это зарезервированный системой объект, пропускаем его.
                } else {
                    // Ищем по externalId в базе.
                    final Long objectId = null; //strategy.getObjectId(externalId);
                    if (objectId != null) {
                        DomainObject oldObject = strategy.getDomainObject(objectId, true);
                        if (oldObject != null) {
                            // нашли, обновляем (или дополняем) значения атрибутов и сохраняем.
                            DomainObject newObject = CloneUtil.cloneObject(oldObject);
                            newObject.setStringValue(RegistrationTypeStrategy.NAME, name, locale);

                            strategy.update(oldObject, newObject, DateUtil.getCurrentDate());
                        }
                    } else {
                        // не нашли, создаём объект заполняем его атрибуты и сохраняем.
                        DomainObject object = strategy.newInstance();

                        object.setStringValue(RegistrationTypeStrategy.NAME, name, locale);

                        strategy.insert(object, DateUtil.getCurrentDate());
                    }
                }
                listener.recordProcessed(REGISTRATION_TYPE, recordIndex);
            }

            listener.completeImport(REGISTRATION_TYPE, recordIndex);
        } catch (IOException e) {
            throw new ImportFileReadException(e, REGISTRATION_TYPE.getFileName(), recordIndex);
        } catch (NumberFormatException e) {
            throw new ImportFileReadException(e, REGISTRATION_TYPE.getFileName(), recordIndex);
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                log.error("Ошибка закрытия потока", e);
            }
        }
    }
}
