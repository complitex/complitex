package ru.complitex.osznconnection.file.service.privilege.task;

import ru.complitex.common.service.ConfigBean;
import ru.complitex.osznconnection.file.entity.AbstractAccountRequest;
import ru.complitex.osznconnection.file.entity.FileHandlingConfig;
import ru.complitex.osznconnection.file.entity.RequestFile;
import ru.complitex.osznconnection.file.entity.privilege.DwellingCharacteristicsDBF;
import ru.complitex.osznconnection.file.service.privilege.DwellingCharacteristicsBean;
import ru.complitex.osznconnection.file.service.process.AbstractRequestFileSaveTaskBean;
import ru.complitex.osznconnection.file.service.process.RequestFileDirectoryType;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import java.util.List;

@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class DwellingCharacteristicsSaveTaskBean extends AbstractRequestFileSaveTaskBean {
    @EJB
    private DwellingCharacteristicsBean dwellingCharacteristicsBean;

    @EJB
    private ConfigBean configBean;

    @Override
    public List<? extends AbstractAccountRequest> getAbstractRequests(RequestFile requestFile) {
        return dwellingCharacteristicsBean.getDwellingCharacteristics(requestFile.getId());
    }

    @Override
    public String getPuAccountFieldName() {
        return DwellingCharacteristicsDBF.IDPIL.name();
    }

    @Override
    public Class<?> getControllerClass() {
        return DwellingCharacteristicsSaveTaskBean.class;
    }

    @Override
    public RequestFileDirectoryType getSaveDirectoryType() {
        return RequestFileDirectoryType.SAVE_DWELLING_CHARACTERISTICS_DIR;
    }

    @Override
    protected String getOutputFileName(String inputFileName) {
        int lastDotIndex = inputFileName.lastIndexOf(".");
        String extension = inputFileName.substring(lastDotIndex + 1, inputFileName.length());
        String baseFileName = inputFileName.substring(0, lastDotIndex);
        String number = extension.substring(1, 3);
        String dwellingCharacteristicsOutputFileExtensionPrefix =
                configBean.getString(FileHandlingConfig.DWELLING_CHARACTERISTICS_OUTPUT_FILE_EXTENSION_PREFIX, true);
        return baseFileName + "." + dwellingCharacteristicsOutputFileExtensionPrefix + number;
    }
}
