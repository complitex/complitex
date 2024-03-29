package ru.complitex.osznconnection.file.service.subsidy.task;

import ru.complitex.common.entity.Log;
import ru.complitex.common.exception.ExecuteException;
import ru.complitex.osznconnection.file.Module;
import ru.complitex.osznconnection.file.entity.RequestFile;
import ru.complitex.osznconnection.file.entity.RequestFileStatus;
import ru.complitex.osznconnection.file.entity.subsidy.ActualPayment;
import ru.complitex.osznconnection.file.entity.subsidy.ActualPaymentDBF;
import ru.complitex.osznconnection.file.service.AbstractRequestTaskBean;
import ru.complitex.osznconnection.file.service.RequestFileBean;
import ru.complitex.osznconnection.file.service.process.AbstractLoadRequestFile;
import ru.complitex.osznconnection.file.service.process.LoadRequestFileBean;
import ru.complitex.osznconnection.file.service.process.ProcessType;
import ru.complitex.osznconnection.file.service.subsidy.ActualPaymentBean;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import java.util.List;
import java.util.Map;

/**
 * User: Anatoly A. Ivanov java@inhell.ru
 * Date: 12.01.11 19:25
 */
@Stateless(name = "ActualPaymentLoadTaskBean")
@TransactionManagement(TransactionManagementType.BEAN)
public class ActualPaymentLoadTaskBean extends AbstractRequestTaskBean<RequestFile> {
    @EJB
    private RequestFileBean requestFileBean;

    @EJB
    private LoadRequestFileBean loadRequestFileBean;

    @EJB
    private ActualPaymentBean actualPaymentBean;


    @Override
    public boolean execute(RequestFile requestFile, Map commandParameters) throws ExecuteException {
        try {
            requestFile.setStatus(RequestFileStatus.LOADING);

            boolean noSkip = loadRequestFileBean.load(requestFile, new AbstractLoadRequestFile<ActualPayment>() {

                @Override
                public Enum[] getFieldNames() {
                    return ActualPaymentDBF.values();
                }

                @Override
                public ActualPayment newObject() {
                    return new ActualPayment();
                }

                @Override
                public void save(List<ActualPayment> batch) {
                    actualPaymentBean.insert(batch);

                    batch.forEach(r -> onRequest(r, ProcessType.LOAD_ACTUAL_PAYMENT));
                }
            });

            if (!noSkip){
                requestFile.setStatus(RequestFileStatus.SKIPPED);

                return false; //skip - file already loaded
            }

            requestFile.setStatus(RequestFileStatus.LOADED);
            requestFileBean.save(requestFile);

            return true;
        } catch (Exception e) {
            requestFileBean.delete(requestFile);

            throw e;
        }
    }

    @Override
    public String getModuleName() {
        return Module.NAME;
    }

    @Override
    public Log.EVENT getEvent() {
        return Log.EVENT.CREATE;
    }
}
