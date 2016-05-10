package org.complitex.osznconnection.file.service.subsidy.task;

import org.complitex.common.entity.IExecutorObject;
import org.complitex.common.entity.Log;
import org.complitex.common.service.executor.AbstractTaskBean;
import org.complitex.common.service.executor.ExecuteException;
import org.complitex.osznconnection.file.Module;
import org.complitex.osznconnection.file.entity.AbstractRequest;
import org.complitex.osznconnection.file.entity.RequestFile;
import org.complitex.osznconnection.file.entity.RequestFileStatus;
import org.complitex.osznconnection.file.entity.subsidy.ActualPayment;
import org.complitex.osznconnection.file.entity.subsidy.ActualPaymentDBF;
import org.complitex.osznconnection.file.service.RequestFileBean;
import org.complitex.osznconnection.file.service.process.LoadRequestFileBean;
import org.complitex.osznconnection.file.service.subsidy.ActualPaymentBean;

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
public class ActualPaymentLoadTaskBean extends AbstractTaskBean {
    @EJB
    private RequestFileBean requestFileBean;

    @EJB
    private LoadRequestFileBean loadRequestFileBean;

    @EJB
    private ActualPaymentBean actualPaymentBean;


    @Override
    public boolean execute(IExecutorObject executorObject, Map commandParameters) throws ExecuteException {
        RequestFile requestFile = (RequestFile) executorObject;

        requestFile.setStatus(RequestFileStatus.LOADING);

        boolean noSkip = loadRequestFileBean.load(requestFile, new LoadRequestFileBean.AbstractLoadRequestFile() {

            @Override
            public Enum[] getFieldNames() {
                return ActualPaymentDBF.values();
            }

            @Override
            public AbstractRequest newObject() {
                return new ActualPayment();
            }

            @Override
            public void save(List<AbstractRequest> batch) {
                actualPaymentBean.insert(batch);

                batch.forEach(r -> onRequest(r));
            }
        });

        if (!noSkip){
            requestFile.setStatus(RequestFileStatus.SKIPPED);

            return false; //skip - file already loaded
        }

        requestFile.setStatus(RequestFileStatus.LOADED);
        requestFileBean.save(requestFile);

        return true;
    }

    @Override
    public void onError(IExecutorObject executorObject) {
        RequestFile requestFile = (RequestFile) executorObject;
        requestFileBean.delete(requestFile);
    }

    @Override
    public String getModuleName() {
        return Module.NAME;
    }

    @Override
    public Class<?> getControllerClass() {
        return ActualPaymentLoadTaskBean.class;
    }

    @Override
    public Log.EVENT getEvent() {
        return Log.EVENT.CREATE;
    }
}
