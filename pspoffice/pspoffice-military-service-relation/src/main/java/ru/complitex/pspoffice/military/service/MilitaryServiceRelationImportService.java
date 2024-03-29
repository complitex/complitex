package ru.complitex.pspoffice.military.service;

import au.com.bytecode.opencsv.CSVReader;
import ru.complitex.common.entity.DomainObject;
import ru.complitex.common.exception.ImportFileNotFoundException;
import ru.complitex.common.exception.ImportFileReadException;
import ru.complitex.common.service.AbstractImportService;
import ru.complitex.common.service.IImportListener;
import ru.complitex.common.strategy.StringLocaleBean;
import ru.complitex.common.util.CloneUtil;
import ru.complitex.common.util.DateUtil;
import ru.complitex.pspoffice.military.strategy.MilitaryServiceRelationStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.io.IOException;
import java.util.Locale;

import static ru.complitex.pspoffice.military.entity.MilitaryServiceRelationImportFile.MILITARY_SERVICE_RELATION;

@Stateless
public class MilitaryServiceRelationImportService extends AbstractImportService {
    private final Logger log = LoggerFactory.getLogger(MilitaryServiceRelationImportService.class);

    @EJB
    private MilitaryServiceRelationStrategy strategy;

    @EJB
    private StringLocaleBean stringLocaleBean;

    /**
     * MILITARY_SERVICE_RELATION_ID	Код	Название
     * @param listener
     * @throws ImportFileNotFoundException
     * @throws ImportFileReadException
     */
    public void process(IImportListener listener, Locale locale)
            throws ImportFileNotFoundException, ImportFileReadException {
        listener.beginImport(MILITARY_SERVICE_RELATION, getRecordCount(MILITARY_SERVICE_RELATION));

        CSVReader reader = getCsvReader(MILITARY_SERVICE_RELATION);

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
                        newObject.setStringValue(MilitaryServiceRelationStrategy.NAME, name, locale);
                        strategy.update(oldObject, newObject, DateUtil.getCurrentDate());
                    }
                } else {
                    // не нашли, создаём объект заполняем его атрибуты и сохраняем.
                    DomainObject object = strategy.newInstance();

                    object.setStringValue(MilitaryServiceRelationStrategy.CODE, code);
                    object.setStringValue(MilitaryServiceRelationStrategy.NAME, name, locale);

                    strategy.insert(object, DateUtil.getCurrentDate());
                }
                listener.recordProcessed(MILITARY_SERVICE_RELATION, recordIndex);
            }
            listener.completeImport(MILITARY_SERVICE_RELATION, recordIndex);
        } catch (IOException | NumberFormatException e) {
            throw new ImportFileReadException(e, MILITARY_SERVICE_RELATION.getFileName(), recordIndex);
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                log.error("Ошибка закрытия потока", e);
            }
        }
    }
}
