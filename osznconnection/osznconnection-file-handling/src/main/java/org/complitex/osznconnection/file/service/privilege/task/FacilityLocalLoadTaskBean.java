package org.complitex.osznconnection.file.service.privilege.task;

import org.complitex.address.strategy.district.DistrictStrategy;
import org.complitex.common.entity.Cursor;
import org.complitex.common.exception.ExecuteException;
import org.complitex.common.service.executor.AbstractTaskBean;
import org.complitex.common.util.ResourceUtil;
import org.complitex.osznconnection.file.entity.RequestFile;
import org.complitex.osznconnection.file.entity.RequestFileStatus;
import org.complitex.osznconnection.file.entity.privilege.FacilityForm2;
import org.complitex.osznconnection.file.service.RequestFileBean;
import org.complitex.osznconnection.file.service.exception.SaveException;
import org.complitex.osznconnection.file.service_provider.ServiceProviderAdapter;
import org.complitex.osznconnection.organization.strategy.OsznOrganizationStrategy;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.Map;

/**
 * @author inheaven on 22.11.2016.
 */
@Stateless
public class FacilityLocalLoadTaskBean extends AbstractTaskBean<RequestFile> {
    private final static Class RESOURCE = FacilityLocalLoadTaskBean.class;

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
        String edrpou = osznOrganizationStrategy.getEdrpou(requestFile.getOrganizationId(), requestFile.getUserOrganizationId());

        requestFile.setStatus(RequestFileStatus.LOADING);
        requestFile.setName(edrpou + ".DBF");
        requestFileBean.save(requestFile);

        String zheuCode = osznOrganizationStrategy.getServiceProviderCode(edrpou, requestFile.getOrganizationId(),
                requestFile.getUserOrganizationId());

        String district = osznOrganizationStrategy.getDistrict(requestFile.getOrganizationId());

        Cursor<FacilityForm2> cursor = serviceProviderAdapter.getFacilityLocal(requestFile.getUserOrganizationId(),
                district, zheuCode, requestFile.getBeginDate());

        if (cursor.getResultCode() > 0){
            //todo save list
        }else {
            requestFile.setStatus(RequestFileStatus.LOAD_ERROR);
            requestFileBean.save(requestFile);

            //noinspection Duplicates
            switch (cursor.getResultCode()){
                case -1: //Не найден р-он
                    throw new SaveException(ResourceUtil.getString(RESOURCE, "error_district_not_found"), district);
                case -5: //Не указан месяц файла
                    throw new SaveException(ResourceUtil.getString(RESOURCE, "error_null_month"));
                case -7: //Не определена организация
                    throw new SaveException(ResourceUtil.getString(RESOURCE, "error_organization_undefined"), zheuCode);
            }
        }

        return true;
    }
}
