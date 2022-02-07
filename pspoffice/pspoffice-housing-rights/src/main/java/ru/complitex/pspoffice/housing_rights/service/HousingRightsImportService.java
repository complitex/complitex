package ru.complitex.pspoffice.housing_rights.service;

import au.com.bytecode.opencsv.CSVReader;
import ru.complitex.common.entity.DomainObject;
import ru.complitex.common.exception.ImportFileNotFoundException;
import ru.complitex.common.exception.ImportFileReadException;
import ru.complitex.common.service.AbstractImportService;
import ru.complitex.common.service.IImportListener;
import ru.complitex.common.util.CloneUtil;
import ru.complitex.common.util.DateUtil;
import ru.complitex.pspoffice.housing_rights.strategy.HousingRightsStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.io.IOException;
import java.util.Locale;

import static ru.complitex.pspoffice.housing_rights.entity.HousingRightsImportFile.HOUSING_RIGHTS;

@Stateless
public class HousingRightsImportService extends AbstractImportService {
    private final Logger log = LoggerFactory.getLogger(HousingRightsImportService.class);

    @EJB
    private HousingRightsStrategy strategy;

    /**
     * HOUSING_RIGHTS_ID	Код	Название
     * @param listener
     * @throws ImportFileNotFoundException
     * @throws ImportFileReadException
     */
    public void process(IImportListener listener, Locale locale)
            throws ImportFileNotFoundException, ImportFileReadException {
        listener.beginImport(HOUSING_RIGHTS, getRecordCount(HOUSING_RIGHTS));

        CSVReader reader = getCsvReader(HOUSING_RIGHTS);

        int recordIndex = 0;

        try {
            String[] line;

            while ((line = reader.readNext()) != null) {
                recordIndex++;

                final String externalId = line[0].trim();
                final String code = line[1].trim();
                final String name = line[2].trim();

                // Ищем по externalId в базе.
                final Long objectId = null;//strategy.getObjectId(externalId);
                if (objectId != null) {
                    DomainObject oldObject = strategy.getDomainObject(objectId, true);
                    if (oldObject != null) {
                        // нашли, обновляем (или дополняем) значения атрибутов и сохраняем.
                        DomainObject newObject = CloneUtil.cloneObject(oldObject);
                        newObject.setStringValue(HousingRightsStrategy.NAME, name, locale);

                        strategy.update(oldObject, newObject, DateUtil.getCurrentDate());
                    }
                } else {
                    // не нашли, создаём объект заполняем его атрибуты и сохраняем.
                    DomainObject object = strategy.newInstance();

                    object.setStringValue(HousingRightsStrategy.CODE, code);
                    object.setStringValue(HousingRightsStrategy.NAME, name, locale);

                    strategy.insert(object, DateUtil.getCurrentDate());
                }
                listener.recordProcessed(HOUSING_RIGHTS, recordIndex);
            }
            listener.completeImport(HOUSING_RIGHTS, recordIndex);
        } catch (IOException | NumberFormatException e) {
            throw new ImportFileReadException(e, HOUSING_RIGHTS.getFileName(), recordIndex);
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                log.error("Ошибка закрытия потока", e);
            }
        }
    }
}
