package ru.complitex.osznconnection.file.service.privilege;

import au.com.bytecode.opencsv.CSVReader;
import ru.complitex.common.exception.ImportFileNotFoundException;
import ru.complitex.common.exception.ImportFileReadException;
import ru.complitex.common.exception.ImportObjectLinkException;
import ru.complitex.common.service.AbstractImportService;
import ru.complitex.common.service.IImportListener;
import ru.complitex.correction.entity.Correction;
import ru.complitex.osznconnection.file.strategy.PrivilegeStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.io.IOException;

import static ru.complitex.osznconnection.file.entity.CorrectionImportFile.PRIVILEGE_CORRECTION;


/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 02.03.11 15:47
 */
@Stateless
public class PrivilegeCorrectionImportService extends AbstractImportService {

    private final Logger log = LoggerFactory.getLogger(PrivilegeCorrectionImportService.class);
    @EJB
    private PrivilegeStrategy privilegeStrategy;
    @EJB
    private PrivilegeCorrectionBean privilegeCorrectionBean;

    /**
     * C_PRIVILEGE_ID	PRIVILEGE_ID	Код	Название привилегии
     * @param listener
     * @throws ImportFileNotFoundException
     * @throws ImportFileReadException
     * @throws ImportObjectLinkException
     */
    public void process(long orgId, long intOrgId, IImportListener listener)
            throws ImportFileNotFoundException, ImportFileReadException, ImportObjectLinkException {
        listener.beginImport(PRIVILEGE_CORRECTION, getRecordCount(PRIVILEGE_CORRECTION));

        CSVReader reader = getCsvReader(PRIVILEGE_CORRECTION);

        int recordIndex = 0;

        try {
            String[] line;

            while ((line = reader.readNext()) != null) {
                recordIndex++;

                //PRIVILEGE_ID
                Long objectId = null;//privilegeStrategy.getObjectId(line[1].trim());
                if (objectId == null) {
                    throw new ImportObjectLinkException(PRIVILEGE_CORRECTION.getFileName(), recordIndex, line[1].trim());
                }

                privilegeCorrectionBean.save(new Correction("privilege", Long.valueOf(line[2].trim()), objectId, line[3].trim(), orgId,
                        intOrgId));

                listener.recordProcessed(PRIVILEGE_CORRECTION, recordIndex);
            }

            listener.completeImport(PRIVILEGE_CORRECTION, recordIndex);
        } catch (IOException | NumberFormatException e) {
            throw new ImportFileReadException(e, PRIVILEGE_CORRECTION.getFileName(), recordIndex);
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                log.error("Ошибка закрытия потока", e);
            }
        }
    }
}
