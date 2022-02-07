package ru.complitex.osznconnection.file.service.privilege.task;

import ru.complitex.common.service.ConfigBean;
import ru.complitex.osznconnection.file.entity.AbstractAccountRequest;
import ru.complitex.osznconnection.file.entity.FileHandlingConfig;
import ru.complitex.osznconnection.file.entity.RequestFile;
import ru.complitex.osznconnection.file.entity.privilege.FacilityServiceTypeDBF;
import ru.complitex.osznconnection.file.service.privilege.FacilityServiceTypeBean;
import ru.complitex.osznconnection.file.service.process.AbstractRequestFileSaveTaskBean;
import ru.complitex.osznconnection.file.service.process.RequestFileDirectoryType;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import java.util.List;

@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class FacilityServiceTypeSaveTaskBean extends AbstractRequestFileSaveTaskBean {

    @EJB
    private FacilityServiceTypeBean facilityServiceTypeBean;
    @EJB
    private ConfigBean configBean;

    @Override
    protected List<AbstractAccountRequest> getAbstractRequests(RequestFile requestFile) {
        return facilityServiceTypeBean.getFacilityServiceType(requestFile.getId());
    }

    @Override
    protected String getPuAccountFieldName() {
        return FacilityServiceTypeDBF.IDPIL.name();
    }

    @Override
    public Class<?> getControllerClass() {
        return FacilityServiceTypeSaveTaskBean.class;
    }

    @Override
    protected RequestFileDirectoryType getSaveDirectoryType() {
        return RequestFileDirectoryType.SAVE_FACILITY_SERVICE_TYPE_DIR;
    }

    @Override
    protected String getOutputFileName(String inputFileName) {
        int lastDotIndex = inputFileName.lastIndexOf(".");
        String extension = inputFileName.substring(lastDotIndex + 1, inputFileName.length());
        String baseFileName = inputFileName.substring(0, lastDotIndex);
        String number = extension.substring(1, 3);
        String facilityServiceTypeOutputFileExtensionPrefix =
                configBean.getString(FileHandlingConfig.FACILITY_SERVICE_TYPE_OUTPUT_FILE_EXTENSION_PREFIX, true);
        return baseFileName + "." + facilityServiceTypeOutputFileExtensionPrefix + number;
    }
}
