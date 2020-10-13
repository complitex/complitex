package org.complitex.osznconnection.file.service.privilege.task;

import org.complitex.common.exception.CanceledByUserException;
import org.complitex.common.exception.ExecuteException;
import org.complitex.common.util.DateUtil;
import org.complitex.osznconnection.file.entity.RequestFile;
import org.complitex.osznconnection.file.entity.RequestFileStatus;
import org.complitex.osznconnection.file.entity.RequestStatus;
import org.complitex.osznconnection.file.entity.privilege.Debt;
import org.complitex.osznconnection.file.entity.privilege.DebtDBF;
import org.complitex.osznconnection.file.service.AbstractRequestTaskBean;
import org.complitex.osznconnection.file.service.RequestFileBean;
import org.complitex.osznconnection.file.service.exception.AlreadyProcessingException;
import org.complitex.osznconnection.file.service.exception.FillException;
import org.complitex.osznconnection.file.service.privilege.DebtBean;
import org.complitex.osznconnection.file.service.process.ProcessType;
import org.complitex.osznconnection.file.service.subsidy.task.SubsidyFillTaskBean;
import org.complitex.osznconnection.file.service_provider.ServiceProviderAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author Anatoly Ivanov
 * 23.09.2020 17:20
 */
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class DebtFillTaskBean extends AbstractRequestTaskBean<RequestFile> {
    private final Logger log = LoggerFactory.getLogger(SubsidyFillTaskBean.class);

    @EJB
    private RequestFileBean requestFileBean;

    @EJB
    private ServiceProviderAdapter serviceProviderAdapter;

    @EJB
    private DebtBean debtBean;

    @Override
    public boolean execute(RequestFile requestFile, Map<?, ?> commandParameters) throws ExecuteException {
        try {
            //проверяем что не обрабатывается в данный момент
            if (requestFileBean.getRequestFileStatus(requestFile.getId()).isProcessing()) {
                throw new FillException(new AlreadyProcessingException(requestFile.getFullName()), true, requestFile);
            }

            requestFile.setStatus(RequestFileStatus.FILLING);
            requestFileBean.save(requestFile);

            //Обработка
            List<Debt> debts = debtBean.getDebts(requestFile.getId());

            for (Debt debt : debts){
                if (requestFile.isCanceled()){
                    throw new FillException(new CanceledByUserException(), true, requestFile);
                }

                fill(requestFile, debt);
                onRequest(debt, ProcessType.FILL_DEBT);
            }

            //проверить все ли записи в файле субсидии обработались
            if (!debtBean.isDebtFileFilled(requestFile.getId())) {
                throw new FillException(true, requestFile);
            }

            requestFile.setStatus(RequestFileStatus.FILLED);
            requestFileBean.save(requestFile);

            return true;
        } catch (Exception e) {
            requestFile.setStatus(RequestFileStatus.FILL_ERROR);
            requestFileBean.save(requestFile);

            throw e;
        }
    }

    public void fill(RequestFile requestFile, Debt debt) {
        if (debt.getAccountNumber() == null){
            debt.setStatus(RequestStatus.PROCESSED_WITH_ERROR);

            debtBean.update(debt);

            log.error("debt fill error: account number is not resolved {}", debt);

            return;
        }

        BigDecimal hope = serviceProviderAdapter.getDebtHope(requestFile.getUserOrganizationId(), debt.getAccountNumber(),
                DateUtil.newDate(debt.getIntegerField(DebtDBF.YEARZV), debt.getIntegerField(DebtDBF.MONTHZV)), 3);

        if (hope.compareTo(BigDecimal.ZERO) >= 0){
            debt.putUpdateField(DebtDBF.SUM_BORG, hope);
            debt.setStatus(RequestStatus.PROCESSED);

            debtBean.update(debt);
        }else if (hope.compareTo(BigDecimal.TEN.negate()) == 0){
            debt.setStatus(RequestStatus.PROCESSED_WITH_ERROR);

            debtBean.update(debt);

            log.error("debt fill error: -10 {}", debt);
        }
    }
}
