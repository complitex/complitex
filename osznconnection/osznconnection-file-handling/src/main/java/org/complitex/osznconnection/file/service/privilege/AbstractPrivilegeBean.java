package org.complitex.osznconnection.file.service.privilege;

import com.google.common.collect.Maps;
import org.complitex.address.entity.AddressEntity;
import org.complitex.osznconnection.file.entity.AbstractAccountRequest;
import org.complitex.osznconnection.file.entity.privilege.FacilityStreet;
import org.complitex.osznconnection.file.service.AbstractRequestBean;

import javax.ejb.EJB;
import java.util.List;
import java.util.Map;

/**
 * @author inheaven on 28.06.16.
 */
public abstract class AbstractPrivilegeBean extends AbstractRequestBean {
    @EJB
    private FacilityReferenceBookBean facilityReferenceBookBean;

    protected void loadFacilityStreet(List<? extends AbstractAccountRequest> list){
        for (AbstractAccountRequest request : list){
            FacilityStreet facilityStreet = facilityReferenceBookBean.getFacilityStreet(request.getRequestFileId(), request.getStreetCode());

            if (facilityStreet != null) {
                request.setStreet(facilityStreet.getStreet());
                if (facilityStreet.getStreetType() != null) {
                    request.setStreetType(facilityStreet.getStreetType().replace(".", ""));
                }
                request.setStreetTypeCode(facilityStreet.getStreetTypeCode());
            }
        }
    }

    protected void markPrivilegeCorrected(String statement, AbstractAccountRequest request, AddressEntity addressEntity){
        markPrivilegeCorrected(statement, request.getRequestFileId(), request, addressEntity);
    }

    protected void markPrivilegeCorrected(String statement, Long requestFileId, AbstractAccountRequest request, AddressEntity addressEntity){
        Map<String, Object> params = Maps.newHashMap();

        params.put("fileId", requestFileId);

        switch (addressEntity){
            case BUILDING:
                params.put("buildingNumber", request.getBuildingNumber());
                params.put("buildingCorp", request.getBuildingCorp());
            case STREET:
                params.put("streetCode", request.getStreetCode());
            case STREET_TYPE:
                params.put("streetTypeCode", request.getStreetTypeCode());
        }

        sqlSession().update(statement, params);
    }

}
