package org.complitex.osznconnection.file.service.privilege.task;

import org.complitex.common.entity.Cursor;
import org.complitex.osznconnection.file.entity.RequestFile;
import org.complitex.osznconnection.file.entity.RequestFileSubType;
import org.complitex.osznconnection.file.entity.privilege.FacilityLocal;
import org.complitex.osznconnection.file.service_provider.ServiceProviderAdapter;

import javax.ejb.EJB;
import javax.ejb.Stateless;

/**
 * @author inheaven on 009 09.12.16.
 */
@Stateless
public class FacilityLocalJanitorLoadTaskBean extends FacilityLocalLoadTaskBean{
    @EJB
    private ServiceProviderAdapter serviceProviderAdapter;

    @Override
    protected Cursor<FacilityLocal> getCursor(RequestFile requestFile, String zheuCode, String district) {
        return serviceProviderAdapter.getFacilityLocalJanitor(requestFile.getUserOrganizationId(),
                district, zheuCode, requestFile.getBeginDate());
    }

    @Override
    protected RequestFileSubType getSubType() {
        return RequestFileSubType.FACILITY_LOCAL_JANITOR;
    }
}
