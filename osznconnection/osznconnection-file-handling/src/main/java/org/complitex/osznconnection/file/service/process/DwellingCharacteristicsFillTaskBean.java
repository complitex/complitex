package org.complitex.osznconnection.file.service.process;

import org.complitex.common.entity.Log;
import org.complitex.common.service.executor.ExecuteException;
import org.complitex.common.service.executor.ITaskBean;
import org.complitex.osznconnection.file.Module;
import org.complitex.osznconnection.file.entity.DwellingCharacteristics;
import org.complitex.osznconnection.file.entity.RequestFile;
import org.complitex.osznconnection.file.entity.RequestFileStatus;
import org.complitex.osznconnection.file.service.RequestFileBean;
import org.complitex.osznconnection.file.service.exception.AlreadyProcessingException;
import org.complitex.osznconnection.file.service.exception.BindException;

import javax.ejb.EJB;
import java.util.Map;

/**
 * inheaven on 25.02.2016.
 */
public class DwellingCharacteristicsFillTaskBean implements ITaskBean<RequestFile>{
    @EJB
    private RequestFileBean requestFileBean;

    @Override
    public boolean execute(RequestFile requestFile, Map commandParameters) throws ExecuteException {
        if (requestFileBean.getRequestFileStatus(requestFile.getId()).isProcessing()){
            throw new BindException(new AlreadyProcessingException(requestFile), true, requestFile);
        }

        requestFile.setStatus(RequestFileStatus.FILLING);
        requestFileBean.save(requestFile);

        //todo clear before filling










        return false;
    }

    @Override
    public void onError(RequestFile requestFile) {
        requestFile.setStatus(RequestFileStatus.FILL_ERROR);
        requestFileBean.save(requestFile);
    }

    @Override
    public String getModuleName() {
        return  Module.NAME;
    }

    @Override
    public Class getControllerClass() {
        return DwellingCharacteristicsFillTaskBean.class;
    }

    @Override
    public Log.EVENT getEvent() {
        return  Log.EVENT.EDIT;
    }
}
