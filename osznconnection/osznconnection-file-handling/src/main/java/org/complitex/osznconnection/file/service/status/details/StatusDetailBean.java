package org.complitex.osznconnection.file.service.status.details;

import org.complitex.common.service.AbstractBean;
import org.complitex.osznconnection.file.entity.StatusDetail;
import org.complitex.osznconnection.file.entity.StatusDetailInfo;
import org.complitex.osznconnection.file.entity.privilege.FacilityStreet;
import org.complitex.osznconnection.file.service.privilege.FacilityReferenceBookBean;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 24.11.10 16:51
 */
@Stateless
public class StatusDetailBean extends AbstractBean {
    public static final String NS = StatusDetailBean.class.getName();

    @EJB
    private FacilityReferenceBookBean facilityReferenceBookBean;

    public List<StatusDetailInfo> getPaymentStatusDetails(Long requestFileId) {
        return sqlSession().selectList(NS + ".getPaymentStatusDetailInfo", requestFileId);
    }

    public List<StatusDetailInfo> getBenefitStatusDetails(Long requestFileId) {
        return sqlSession().selectList(NS + ".getBenefitStatusDetailInfo", requestFileId);
    }

    public List<StatusDetailInfo> getActualPaymentStatusDetails(Long requestFileId) {
        return sqlSession().selectList(NS + ".getActualPaymentStatusDetailInfo", requestFileId);
    }

    public List<StatusDetailInfo> getSubsidyStatusDetails(Long requestFileId) {
        return sqlSession().selectList(NS + ".getSubsidyStatusDetailInfo", requestFileId);
    }

    public List<StatusDetailInfo> getDwellingCharacteristicsStatusDetails(Long requestFileId) {
        List<StatusDetailInfo> list =  sqlSession().selectList(NS + ".getDwellingCharacteristicsStatusDetailInfo", requestFileId);

        loadFacilityStreet(requestFileId, list);

        return list;
    }
    
    public List<StatusDetailInfo> getFacilityServiceTypeStatusDetails(Long requestFileId) {
        List<StatusDetailInfo> list = sqlSession().selectList(NS + ".getFacilityServiceTypeStatusDetailInfo", requestFileId);

        loadFacilityStreet(requestFileId, list);

        return list;
    }

    public List<StatusDetailInfo> getPrivilegeProlongationStatusDetails(Long requestFileId) {
        List<StatusDetailInfo>  list =  sqlSession().selectList(NS + ".getPrivilegeProlongationStatusDetailInfo", requestFileId);

        loadFacilityStreet(requestFileId, list);

        return list;
    }

    private void loadFacilityStreet(Long requestFileId, List<StatusDetailInfo> list){
        for (StatusDetailInfo info : list){
            for (StatusDetail detail : info.getStatusDetails()){
                if (detail.getDetail("streetCode") != null && detail.getDetail("street") == null){
                    FacilityStreet facilityStreet = facilityReferenceBookBean.getFacilityStreet(requestFileId, detail.getDetail("streetCode"));

                    if (facilityStreet != null) {
                        detail.putDetail("street", facilityStreet.getStreet());

                        if (facilityStreet.getStreetType() != null) {
                            detail.putDetail("streetType", facilityStreet.getStreetType().replace(".", ""));
                        }
                    }
                }
            }
        }
    }

    public List<StatusDetailInfo> getOschadbankRequestDetails(Long requestFileId) {
        return sqlSession().selectList(NS + ".getOschadbankRequestStatusDetailInfo", requestFileId);
    }

    public List<StatusDetailInfo> getOschadbankResponseDetails(Long requestFileId) {
        return sqlSession().selectList(NS + ".getOschadbankResponseStatusDetailInfo", requestFileId);
    }

}
