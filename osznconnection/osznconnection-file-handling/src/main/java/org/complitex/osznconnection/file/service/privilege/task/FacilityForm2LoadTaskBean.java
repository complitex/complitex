package org.complitex.osznconnection.file.service.privilege.task;

import org.complitex.address.strategy.district.DistrictStrategy;
import org.complitex.common.entity.Cursor;
import org.complitex.common.exception.ExecuteException;
import org.complitex.common.service.executor.AbstractTaskBean;
import org.complitex.common.util.ExceptionUtil;
import org.complitex.common.util.ResourceUtil;
import org.complitex.osznconnection.file.entity.RequestFile;
import org.complitex.osznconnection.file.entity.RequestFileStatus;
import org.complitex.osznconnection.file.entity.privilege.FacilityForm2;
import org.complitex.osznconnection.file.service.RequestFileBean;
import org.complitex.osznconnection.file.service.exception.LoadException;
import org.complitex.osznconnection.file.service.privilege.FacilityForm2Bean;
import org.complitex.osznconnection.file.service_provider.ServiceProviderAdapter;
import org.complitex.osznconnection.organization.strategy.OsznOrganizationStrategy;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.Map;

/**
 * @author inheaven on 016 16.11.16.
 */
@Stateless
public class FacilityForm2LoadTaskBean extends AbstractTaskBean<RequestFile>{
    private final static Class RESOURCE = FacilityForm2LoadTaskBean.class;

    @EJB
    private ServiceProviderAdapter serviceProviderAdapter;

    @EJB
    private OsznOrganizationStrategy osznOrganizationStrategy;

    @EJB
    private DistrictStrategy districtStrategy;

    @EJB
    private RequestFileBean requestFileBean;

    @EJB
    private FacilityForm2Bean facilityForm2Bean;

    @Override
    public boolean execute(RequestFile requestFile, Map commandParameters) throws ExecuteException {
        String edrpou = osznOrganizationStrategy.getEdrpou(requestFile.getOrganizationId(), requestFile.getUserOrganizationId());

        requestFile.setStatus(RequestFileStatus.LOADING);
        requestFile.setName(edrpou + ".DBF");
        requestFile.setDirectory("");
        requestFileBean.save(requestFile);

        String zheuCode =  osznOrganizationStrategy.getServiceProviderCode(edrpou, requestFile.getOrganizationId(),
                requestFile.getUserOrganizationId());

        String district = osznOrganizationStrategy.getDistrict(requestFile.getOrganizationId());

        Cursor<FacilityForm2> cursor = serviceProviderAdapter.getFacilityForm2(requestFile.getUserOrganizationId(),
                district, zheuCode, requestFile.getBeginDate());

        if (cursor.getResultCode() > 0){
            try {
                cursor.getData().forEach(f -> f.setRequestFileId(requestFile.getId()));

                facilityForm2Bean.save(cursor.getData());

                requestFile.setStatus(RequestFileStatus.LOADED);
                requestFileBean.save(requestFile);
            } catch (Exception e) {
                throw new LoadException(ExceptionUtil.getCauseMessage(e));
            }
        }else {
            requestFile.setStatus(RequestFileStatus.LOAD_ERROR);
            requestFileBean.save(requestFile);

            //noinspection Duplicates
            switch (cursor.getResultCode()){
                case -1: //Не найден р-он
                    throw new LoadException(ResourceUtil.getString(RESOURCE, "error_district_not_found"), district);
                case -5: //Не указан месяц файла
                    throw new LoadException(ResourceUtil.getString(RESOURCE, "error_null_month"));
                case -7: //Не определена организация
                    throw new LoadException(ResourceUtil.getString(RESOURCE, "error_organization_undefined"), zheuCode);
            }
        }

        return true;
    }
}