package org.complitex.osznconnection.file.service.subsidy.task;

import org.complitex.common.exception.ExecuteException;
import org.complitex.common.util.DateUtil;
import org.complitex.common.util.StringUtil;
import org.complitex.osznconnection.file.entity.RequestFile;
import org.complitex.osznconnection.file.entity.RequestFileStatus;
import org.complitex.osznconnection.file.entity.subsidy.Subsidy;
import org.complitex.osznconnection.file.service.AbstractRequestTaskBean;
import org.complitex.osznconnection.file.service.RequestFileBean;
import org.complitex.osznconnection.file.service.exception.SaveException;
import org.complitex.osznconnection.file.service.process.RequestFileDirectoryType;
import org.complitex.osznconnection.file.service.process.RequestFileStorage;
import org.complitex.osznconnection.file.service.subsidy.SubsidyBean;
import org.complitex.osznconnection.file.service_provider.ServiceProviderAdapter;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import java.io.BufferedWriter;
import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

import static org.complitex.osznconnection.file.entity.subsidy.SubsidyDBF.*;

/**
 * @author Ivanov Anatoliy
 */
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class SubsidyDownloadTaskBean extends AbstractRequestTaskBean<RequestFile> {
    @EJB
    private RequestFileBean requestFileBean;

    @EJB
    private SubsidyBean subsidyBean;

    @EJB
    private ServiceProviderAdapter serviceProviderAdapter;

    @Override
    public boolean execute(RequestFile requestFile, Map<?, ?> commandParameters) throws ExecuteException {
        try {
            requestFile.setStatus(RequestFileStatus.SAVING);
            requestFileBean.save(requestFile);

            List<Subsidy> subsidies = subsidyBean.getSubsidies(requestFile.getId());

            String path = RequestFileStorage.INSTANCE.getRequestFilesStorageDirectory(requestFile.getUserOrganizationId(), requestFile.getOrganizationId(),
                    RequestFileDirectoryType.SAVE_SUBSIDY_DIR);

            BufferedWriter bufferedWriter = Files.newBufferedWriter(new File(path, requestFile.getName().substring(0, requestFile.getName().indexOf('.')) + ".cvs").toPath());

            bufferedWriter.write("N,NUMB,FIO,CAT_V,NAME_V,BLD,CORP,FLAT,RASH,DEBT");
            bufferedWriter.newLine();

            for (int i = 0; i < subsidies.size(); i++) {
                Subsidy subsidy = subsidies.get(i);

                BigDecimal debtHope = serviceProviderAdapter.getDebtHope(requestFile.getUserOrganizationId(), subsidy.getAccountNumber(),
                        DateUtil.getCurrentDate(), 3);

                bufferedWriter.write(String.join(",", i + "", subsidy.getStringField(NUMB), subsidy.getStringField(FIO),
                        subsidy.getStringField(CAT_V), subsidy.getStringField(NAME_V), subsidy.getStringField(BLD), subsidy.getStringField(CORP), subsidy.getStringField(FLAT),
                        StringUtil.emptyOnNull(subsidy.getAccountNumber()), debtHope.compareTo(BigDecimal.ZERO) > 0 ? debtHope.toPlainString() : "0"));
                bufferedWriter.newLine();

                bufferedWriter.flush();
            }

            bufferedWriter.close();

            requestFile.setStatus(RequestFileStatus.SAVED);
            requestFileBean.save(requestFile);
        } catch (Exception e) {
            requestFile.setStatus(RequestFileStatus.SAVE_ERROR);
            requestFileBean.save(requestFile);

            throw new SaveException(e, requestFile);
        }

        return true;
    }
}
