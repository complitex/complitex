package ru.complitex.osznconnection.file.service.privilege;

import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.complitex.address.entity.AddressEntity;
import ru.complitex.osznconnection.file.entity.AbstractAccountRequest;
import ru.complitex.osznconnection.file.entity.privilege.FacilityStreet;
import ru.complitex.osznconnection.file.service.AbstractRequestBean;

import javax.ejb.EJB;
import java.util.List;
import java.util.Map;

/**
 * @author inheaven on 28.06.16.
 */
public abstract class AbstractPrivilegeBean extends AbstractRequestBean {
    private Logger log = LoggerFactory.getLogger(AbstractPrivilegeBean.class);

    @EJB
    private FacilityReferenceBookBean facilityReferenceBookBean;

    public void loadFacilityStreet(List<? extends AbstractAccountRequest> list){
        try {
            for (AbstractAccountRequest request : list){
                if (request.getStreetCode() != null) {
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
        } catch (Exception e) {
            log.error("error loadFacilityStreet ", e);

            throw e;
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
