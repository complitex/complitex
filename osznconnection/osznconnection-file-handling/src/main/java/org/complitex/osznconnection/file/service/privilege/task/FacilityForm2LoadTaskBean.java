package org.complitex.osznconnection.file.service.privilege.task;

import org.complitex.address.strategy.district.DistrictStrategy;
import org.complitex.common.exception.ExecuteException;
import org.complitex.common.service.executor.AbstractTaskBean;
import org.complitex.osznconnection.file.entity.RequestFile;
import org.complitex.osznconnection.file.entity.RequestFileStatus;
import org.complitex.osznconnection.file.service.RequestFileBean;
import org.complitex.osznconnection.file.service_provider.ServiceProviderAdapter;
import org.complitex.osznconnection.organization.strategy.OsznOrganizationStrategy;

import javax.ejb.EJB;
import java.util.Map;

/**
 * @author inheaven on 016 16.11.16.
 */
public class FacilityForm2LoadTaskBean extends AbstractTaskBean<RequestFile>{
    @EJB
    private ServiceProviderAdapter serviceProviderAdapter;

    @EJB
    private OsznOrganizationStrategy osznOrganizationStrategy;

    @EJB
    private DistrictStrategy districtStrategy;


    @EJB
    private RequestFileBean requestFileBean;

    @Override
    public boolean execute(RequestFile requestFile, Map commandParameters) throws ExecuteException {
        requestFile.setStatus(RequestFileStatus.LOADING);
        requestFile.setName(null); //todo
        requestFileBean.save(requestFile);






        return true;
    }
}
