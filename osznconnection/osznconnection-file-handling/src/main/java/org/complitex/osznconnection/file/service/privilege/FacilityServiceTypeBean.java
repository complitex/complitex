package org.complitex.osznconnection.file.service.privilege;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.complitex.address.entity.AddressEntity;
import org.complitex.osznconnection.file.entity.AbstractAccountRequest;
import org.complitex.osznconnection.file.entity.RequestFileType;
import org.complitex.osznconnection.file.entity.RequestStatus;
import org.complitex.osznconnection.file.entity.example.PrivilegeExample;
import org.complitex.osznconnection.file.entity.privilege.FacilityServiceType;
import org.complitex.osznconnection.file.entity.privilege.FacilityServiceTypeDBF;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import java.util.*;

import static com.google.common.collect.ImmutableMap.of;
import static org.complitex.osznconnection.file.entity.privilege.FacilityServiceTypeDBF.CDUL;

/**
 *
 * @author Artem
 */
@Stateless
public class FacilityServiceTypeBean extends AbstractPrivilegeBean {
    public static final String NS = FacilityServiceTypeBean.class.getName();
    private static final Map<Long, Set<FacilityServiceTypeDBF>> UPDATE_FIELD_MAP = of();

    @EJB
    private FacilityReferenceBookBean facilityReferenceBookBean;

    public enum OrderBy {

        RAH(FacilityServiceTypeDBF.RAH.name()),
        IDPIL(FacilityServiceTypeDBF.IDPIL.name()),
        FIRST_NAME("first_name"),
        MIDDLE_NAME("middle_name"),
        LAST_NAME("last_name"),
        STREET_CODE(CDUL.name()),
        STREET_REFERENCE("street_reference"),
        BUILDING(FacilityServiceTypeDBF.HOUSE.name()),
        CORP(FacilityServiceTypeDBF.BUILD.name()),
        APARTMENT(FacilityServiceTypeDBF.APT.name()),
        STATUS("status");
        private String orderBy;

        private OrderBy(String orderBy) {
            this.orderBy = orderBy;
        }

        public String getOrderBy() {
            return orderBy;
        }
    }


    public void delete(long requestFileId) {
        sqlSession().delete(NS + ".deleteFacilityServiceType", requestFileId);
    }


    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void insert(List<FacilityServiceType> abstractRequests) {
        if (abstractRequests.isEmpty()) {
            return;
        }

        sqlSession().insert(NS + ".insertFacilityServiceTypeList", abstractRequests);
    }


    public Long getCount(PrivilegeExample example) {
        return sqlSession().selectOne(NS + ".count", example);
    }


    public List<FacilityServiceType> find(PrivilegeExample example) {
        List<FacilityServiceType> list = sqlSession().selectList(NS + ".find", example);

        loadFacilityStreet(list);

        return list;
    }


    public boolean isFacilityServiceTypeFileBound(long fileId) {
        return unboundCount(fileId) == 0;
    }

    public boolean isFacilityServiceTypeFileFilled(Long requestFileId){
        return countByFile(requestFileId, RequestStatus.UNPROCESSED_SET_STATUSES) == 0;
    }

    private int unboundCount(long fileId) {
        return countByFile(fileId, RequestStatus.unboundStatuses());
    }

    private int countByFile(long fileId, Set<RequestStatus> statuses) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("requestFileId", fileId);
        params.put("statuses", statuses);
        return sqlSession().selectOne(NS + ".countByFile", params);
    }


    public void update(FacilityServiceType facilityServiceType) {
        sqlSession().update(NS + ".update", facilityServiceType);
    }


    public void updateAccountNumber(FacilityServiceType facilityServiceType) {
        sqlSession().update(NS + ".updateAccountNumber", facilityServiceType);
    }


    public List<Long> findIdsForBinding(Long fileId) {
        return findIdsForOperation(fileId);
    }


    public List<Long> findIdsForOperation(Long requestFileId) {
        return sqlSession().selectList(NS + ".findIdsForOperation", requestFileId);
    }


    public List<FacilityServiceType> findForOperation(Long requestFileId, List<Long> ids) {
        List<FacilityServiceType> list = sqlSession().selectList(NS + ".findForOperation",
                of("requestFileId", requestFileId, "ids", ids));

        loadFacilityStreet(list);

        return list;
    }


    public void clearBeforeBinding(Long requestFileId, Set<Long> serviceProviderTypeIds) {
        Map<String, String> updateFieldMap = new HashMap<>();
        if (serviceProviderTypeIds != null && !serviceProviderTypeIds.isEmpty()) {
            for (FacilityServiceTypeDBF field : getUpdatableFields(serviceProviderTypeIds)) {
                updateFieldMap.put(field.name(), "-1");
            }
        }

        sqlSession().update(NS + ".clearBeforeBinding", of("status", RequestStatus.LOADED, "fileId", requestFileId,
                "updateFieldMap", updateFieldMap));
        clearWarnings(requestFileId, RequestFileType.FACILITY_SERVICE_TYPE);
    }

    private Set<FacilityServiceTypeDBF> getUpdatableFields(Set<Long> serviceProviderTypeIds) {
        final Set<FacilityServiceTypeDBF> updatableFields = Sets.newHashSet();

        for (long serviceProviderTypeId : serviceProviderTypeIds) {
            Set<FacilityServiceTypeDBF> fields = UPDATE_FIELD_MAP.get(serviceProviderTypeId);
            if (fields != null) {
                updatableFields.addAll(fields);
            }
        }
        return Collections.unmodifiableSet(updatableFields);
    }

    public void markCorrected(FacilityServiceType facilityServiceType, AddressEntity addressEntity) {
        markPrivilegeCorrected(NS + ".markCorrected", facilityServiceType, addressEntity);
    }

    public void markCorrected(Long requestFileId, AbstractAccountRequest request, AddressEntity addressEntity) {
        markPrivilegeCorrected(NS + ".markCorrected", requestFileId, request, addressEntity);
    }

    public List<AbstractAccountRequest> getFacilityServiceType(long requestFileId) {
        return sqlSession().selectList(NS + ".selectFacilityServiceType", requestFileId);
    }

    public List<FacilityServiceType> getFacilityServiceTypeListByGroup(Long groupId){
        List<FacilityServiceType> list =  sqlSession().selectList("selectFacilityServiceTypeByGroup", groupId);
        loadFacilityStreet(list);

        return list;
    }
}
