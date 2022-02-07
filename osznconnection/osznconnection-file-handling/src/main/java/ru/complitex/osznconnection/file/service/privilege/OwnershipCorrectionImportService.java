package ru.complitex.osznconnection.file.service.privilege;

import au.com.bytecode.opencsv.CSVReader;
import ru.complitex.common.exception.ImportFileNotFoundException;
import ru.complitex.common.exception.ImportFileReadException;
import ru.complitex.common.exception.ImportObjectLinkException;
import ru.complitex.common.service.AbstractImportService;
import ru.complitex.common.service.IImportListener;
import ru.complitex.correction.entity.Correction;
import ru.complitex.correction.service.CorrectionBean;
import ru.complitex.osznconnection.file.strategy.OwnershipStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.io.IOException;

import static ru.complitex.osznconnection.file.entity.CorrectionImportFile.OWNERSHIP_CORRECTION;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 02.03.11 15:22
 */
@Stateless
public class OwnershipCorrectionImportService extends AbstractImportService {

    private final Logger log = LoggerFactory.getLogger(OwnershipCorrectionImportService.class);
    @EJB
    private OwnershipStrategy ownershipStrategy;

    @EJB
    private OwnershipCorrectionBean ownershipCorrectionBean;

    @EJB
    private CorrectionBean correctionBean;

    /**
     * C_OWNERSHIP_ID	OWNERSHIP_ID	Код	Название формы собственности
     * @param listener
     * @throws ImportFileNotFoundException
     * @throws ImportFileReadException
     * @throws ImportObjectLinkException
     */
    public void process(long orgId, long intOrgId, IImportListener listener)
            throws ImportFileNotFoundException, ImportFileReadException, ImportObjectLinkException {
        listener.beginImport(OWNERSHIP_CORRECTION, getRecordCount(OWNERSHIP_CORRECTION));

        CSVReader reader = getCsvReader(OWNERSHIP_CORRECTION);

        int recordIndex = 0;

        try {
            String[] line;

            while ((line = reader.readNext()) != null) {
                recordIndex++;

                //OWNERSHIP_ID
                Long objectId = null;//ownershipStrategy.getObjectId(line[1].trim());
                if (objectId == null) {
                    throw new ImportObjectLinkException(OWNERSHIP_CORRECTION.getFileName(), recordIndex, line[1]);
                }

                correctionBean.save(new Correction(OwnershipStrategy.OWNERSHIP_ENTITY.getEntityName(),
                        Long.valueOf(line[2].trim()), objectId, line[3].trim(), orgId,
                        null));

                listener.recordProcessed(OWNERSHIP_CORRECTION, recordIndex);
            }

            listener.completeImport(OWNERSHIP_CORRECTION, recordIndex);
        } catch (IOException | NumberFormatException e) {
            throw new ImportFileReadException(e, OWNERSHIP_CORRECTION.getFileName(), recordIndex);
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                log.error("Ошибка закрытия потока", e);
            }
        }
    }
}
