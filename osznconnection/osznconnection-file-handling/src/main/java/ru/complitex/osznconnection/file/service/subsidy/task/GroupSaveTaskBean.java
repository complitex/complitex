package ru.complitex.osznconnection.file.service.subsidy.task;

import ru.complitex.common.exception.ExecuteException;
import ru.complitex.common.service.executor.ITaskBean;
import ru.complitex.osznconnection.file.entity.*;
import ru.complitex.osznconnection.file.entity.subsidy.PaymentDBF;
import ru.complitex.osznconnection.file.entity.subsidy.RequestFileGroup;
import ru.complitex.osznconnection.file.service.process.AbstractSaveTaskBean;
import ru.complitex.osznconnection.file.service.process.RequestFileDirectoryType;
import ru.complitex.osznconnection.file.service.subsidy.BenefitBean;
import ru.complitex.osznconnection.file.service.subsidy.PaymentBean;
import ru.complitex.osznconnection.file.service.subsidy.RequestFileGroupBean;
import ru.complitex.osznconnection.file.service.exception.AlreadyProcessingException;
import ru.complitex.osznconnection.file.service.exception.SaveException;
import ru.complitex.osznconnection.file.web.pages.util.GlobalOptions;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import java.util.List;
import java.util.Map;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 01.11.10 12:58
 */
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class GroupSaveTaskBean extends AbstractSaveTaskBean implements ITaskBean<RequestFileGroup> {

    @EJB
    private PaymentBean paymentBean;
    @EJB
    private BenefitBean benefitBean;
    @EJB
    private RequestFileGroupBean requestFileGroupBean;

    @Override
    public boolean execute(RequestFileGroup group, Map commandParameters) throws ExecuteException {
        try {
            // получаем значение опции и параметров комманды
            // опция перезаписи номера л/с поставщика услуг номером л/с модуля начислений при выгрузке файла запроса
            final boolean updatePuAccount = (Boolean) commandParameters.get(GlobalOptions.UPDATE_PU_ACCOUNT);

            if (requestFileGroupBean.getRequestFileStatus(group).isProcessing()) { //проверяем что не обрабатывается в данный момент
                throw new SaveException(new AlreadyProcessingException(group.getFullName()), true, group);
            }

            group.setStatus(RequestFileStatus.SAVING);
            requestFileGroupBean.save(group);

            //сохранение начислений
            save(group.getPaymentFile(), updatePuAccount);
            save(group.getBenefitFile(), updatePuAccount);

            group.setStatus(RequestFileStatus.SAVED);
            requestFileGroupBean.save(group);

            return true;
        } catch (Exception e) {
            group.setStatus(RequestFileStatus.SAVE_ERROR);
            requestFileGroupBean.save(group);

            throw e;
        }
    }

    @Override
    public Class<?> getControllerClass() {
        return GroupSaveTaskBean.class;
    }

    @Override
    protected List<? extends AbstractAccountRequest> getAbstractRequests(RequestFile requestFile) {
        switch (requestFile.getType()) {
            case BENEFIT:
                return benefitBean.getBenefits(requestFile.getId());
            case PAYMENT:
                return paymentBean.getPayments(requestFile.getId());
            default:
                throw new IllegalArgumentException(requestFile.getType().name());
        }
    }

    @Override
    protected String getPuAccountFieldName() {
        return PaymentDBF.OWN_NUM_SR.name();
    }

    @Override
    protected RequestFileDirectoryType getSaveDirectoryType() {
        return RequestFileDirectoryType.SAVE_PAYMENT_BENEFIT_FILES_DIR;
    }
}
