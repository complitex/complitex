package org.complitex.osznconnection.file.service.privilege.task;

import org.complitex.common.exception.ExecuteException;
import org.complitex.common.service.executor.AbstractTaskBean;
import org.complitex.osznconnection.file.entity.RequestFile;
import org.complitex.osznconnection.file.service_provider.ServiceProviderAdapter;

import javax.ejb.EJB;
import java.util.Map;

/**
 * @author inheaven on 016 16.11.16.
 */
public class FacilityForm2LoadTaskBean extends AbstractTaskBean<RequestFile>{
    @EJB
    private ServiceProviderAdapter serviceProviderAdapter;

    @Override
    public boolean execute(RequestFile object, Map commandParameters) throws ExecuteException {
        //todo load


        return true;
    }
}
