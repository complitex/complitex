package org.complitex.osznconnection.file.service.privilege.task;

import org.complitex.common.service.ConfigBean;
import org.complitex.common.service.executor.ITaskBean;
import org.complitex.osznconnection.file.entity.FileHandlingConfig;
import org.complitex.osznconnection.file.entity.RequestFile;
import org.complitex.osznconnection.file.entity.privilege.Debt;
import org.complitex.osznconnection.file.entity.privilege.DebtDBF;
import org.complitex.osznconnection.file.service.privilege.DebtBean;
import org.complitex.osznconnection.file.service.process.AbstractSaveTaskBean;
import org.complitex.osznconnection.file.service.process.RequestFileDirectoryType;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.List;

/**
 * @author Anatoly Ivanov
 * 16.09.2020 20:33
 */
@Stateless
public class DebtSaveTaskBean extends AbstractSaveTaskBean implements ITaskBean<RequestFile> {
    @EJB
    private DebtBean debtBean;

    @EJB
    private ConfigBean configBean;

    @Override
    public Class<?> getControllerClass() {
        return DebtSaveTaskBean.class;
    }

    @Override
    protected List<Debt> getAbstractRequests(RequestFile requestFile) {
        return debtBean.getDebts(requestFile.getId());
    }

    @Override
    protected String getPuAccountFieldName() {
        return DebtDBF.IDPIL.name();
    }

    @Override
    protected RequestFileDirectoryType getSaveDirectoryType() {
        return RequestFileDirectoryType.SAVE_DEBT_DIR;
    }

    @Override
    protected String getOutputFileName(String inputFileName) {
        int lastDotIndex = inputFileName.lastIndexOf(".");

        String extension = inputFileName.substring(lastDotIndex + 1);
        String baseFileName = inputFileName.substring(0, lastDotIndex);
        String number = extension.substring(1, 3);

        String dwellingCharacteristicsOutputFileExtensionPrefix = configBean.getString(FileHandlingConfig.DEBT_OUTPUT_FILE_EXTENSION_PREFIX, true);

        return baseFileName + "." + dwellingCharacteristicsOutputFileExtensionPrefix + number;
    }
}
