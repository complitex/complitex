package org.complitex.osznconnection.file.service.privilege;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.complitex.address.entity.AddressEntity;
import org.complitex.osznconnection.file.entity.AbstractAccountRequest;
import org.complitex.osznconnection.file.entity.AbstractRequest;
import org.complitex.osznconnection.file.entity.RequestFileType;
import org.complitex.osznconnection.file.entity.RequestStatus;
import org.complitex.osznconnection.file.entity.example.PrivilegeExample;
import org.complitex.osznconnection.file.entity.privilege.DwellingCharacteristics;
import org.complitex.osznconnection.file.entity.privilege.DwellingCharacteristicsDBF;
import org.complitex.osznconnection.file.entity.privilege.FacilityStreet;
import org.complitex.osznconnection.file.service.AbstractRequestBean;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import java.util.*;

import static com.google.common.collect.ImmutableMap.of;
import static org.complitex.osznconnection.file.entity.privilege.DwellingCharacteristicsDBF.CDUL;

/**
 *
 * @author Artem
 */
@Stateless
public class DwellingCharacteristicsBean extends AbstractRequestBean {

    public static final String MAPPING_NAMESPACE = DwellingCharacteristicsBean.class.getName();
    private static final Map<Long, Set<DwellingCharacteristicsDBF>> UPDATE_FIELD_MAP = of();

    @EJB
    private FacilityReferenceBookBean facilityReferenceBookBean;

    public enum OrderBy {
        IDPIL(DwellingCharacteristicsDBF.IDPIL.name()),
        FIRST_NAME("first_name"),
        MIDDLE_NAME("middle_name"),
        LAST_NAME("last_name"),
        STREET_CODE(CDUL.name()),
        STREET_REFERENCE("street_reference"),
        BUILDING(DwellingCharacteristicsDBF.HOUSE.name()),
        CORP(DwellingCharacteristicsDBF.BUILD.name()),
        APARTMENT(DwellingCharacteristicsDBF.APT.name()),
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
        sqlSession().delete(MAPPING_NAMESPACE + ".deleteDwellingCharacteristics", requestFileId);
    }


    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void insert(List<AbstractRequest> abstractRequests) {
        if (abstractRequests.isEmpty()) {
            return;
        }
        sqlSession().insert(MAPPING_NAMESPACE + ".insertDwellingCharacteristicsList", abstractRequests);
    }


    public Long getCount(PrivilegeExample example) {
        return sqlSession().selectOne(MAPPING_NAMESPACE + ".count", example);
    }


    public List<DwellingCharacteristics> find(PrivilegeExample example) {
        List<DwellingCharacteristics> list = sqlSession().selectList(MAPPING_NAMESPACE + ".find", example);

        loadFacilityStreet(list);

        return list;
    }


    public boolean isDwellingCharacteristicsFileBound(long fileId) {
        return countByFile(fileId, RequestStatus.unboundStatuses()) == 0;
    }

    public boolean isDwellingCharacteristicsFileFilled(Long requestFileId){
        return countByFile(requestFileId, RequestStatus.UNPROCESSED_SET_STATUSES) == 0;
    }

    private Integer countByFile(long fileId, Set<RequestStatus> statuses) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("requestFileId", fileId);
        params.put("statuses", statuses);
        return sqlSession().selectOne(MAPPING_NAMESPACE + ".countByFile", params);
    }

    public void update(DwellingCharacteristics dwellingCharacteristics) {
        sqlSession().update(MAPPING_NAMESPACE + ".update", dwellingCharacteristics);
    }


    public void updateAccountNumber(DwellingCharacteristics dwellingCharacteristics) {
        sqlSession().update(MAPPING_NAMESPACE + ".updateAccountNumber", dwellingCharacteristics);
    }


    public List<Long> findIdsForBinding(long fileId) {
        return findIdsForOperation(fileId);
    }


    public List<Long> findIdsForOperation(long fileId) {
        return sqlSession().selectList(MAPPING_NAMESPACE + ".findIdsForOperation", fileId);
    }

    public List<DwellingCharacteristics> findForOperation(long fileId, List<Long> ids) {
        List<DwellingCharacteristics> list = sqlSession().selectList(MAPPING_NAMESPACE + ".findForOperation",
                of("requestFileId", fileId, "ids", ids));

        loadFacilityStreet(list);

        return list;
    }

    public void loadFacilityStreet(List<DwellingCharacteristics> list){
        for (DwellingCharacteristics d : list){
            FacilityStreet facilityStreet = facilityReferenceBookBean.getFacilityStreet(d.getRequestFileId(), d.getStreetCode());

            if (facilityStreet != null) {
                d.setStreet(facilityStreet.getStreet());
                if (facilityStreet.getStreetType() != null) {
                    d.setStreetType(facilityStreet.getStreetType().replace(".", ""));
                }
                d.setStreetTypeCode(facilityStreet.getStreetTypeCode());
            }
        }
    }


    public void clearBeforeBinding(long fileId, Set<Long> serviceProviderTypeIds) {
        Map<String, String> updateFieldMap = new HashMap<>();

        if (serviceProviderTypeIds != null && !serviceProviderTypeIds.isEmpty()) {
            updateFieldMap = Maps.newHashMap();
            for (DwellingCharacteristicsDBF field : getUpdateableFields(serviceProviderTypeIds)) {
                updateFieldMap.put(field.name(), "-1");
            }
        }

        sqlSession().update(MAPPING_NAMESPACE + ".clearBeforeBinding",
                of("status", RequestStatus.LOADED, "fileId", fileId, "updateFieldMap", updateFieldMap));
        clearWarnings(fileId, RequestFileType.DWELLING_CHARACTERISTICS);
    }

    private Set<DwellingCharacteristicsDBF> getUpdateableFields(Set<Long> serviceProviderTypeIds) {
        final Set<DwellingCharacteristicsDBF> updateableFields = Sets.newHashSet();

        for (long serviceProviderTypeId : serviceProviderTypeIds) {
            Set<DwellingCharacteristicsDBF> fields = UPDATE_FIELD_MAP.get(serviceProviderTypeId);
            if (fields != null) {
                updateableFields.addAll(fields);
            }
        }

        return Collections.unmodifiableSet(updateableFields);
    }


    @SuppressWarnings("Duplicates")
    public void markCorrected(AbstractAccountRequest request, AddressEntity addressEntity) {
        Map<String, Object> params = Maps.newHashMap();

        params.put("fileId", request.getRequestFileId());

        switch (addressEntity){
            case BUILDING:
                params.put("buildingNumber", request.getBuildingNumber());
                params.put("buildingCorp", request.getBuildingCorp());
           case STREET:
               params.put("streetCode", request.getStreetCode());
            case STREET_TYPE:
                params.put("streetTypeCode", request.getStreetTypeCode());
        }

        sqlSession().update(MAPPING_NAMESPACE + ".markCorrected", params);
    }

    public List<DwellingCharacteristics> getDwellingCharacteristics(long requestFileId) {
        return sqlSession().selectList(MAPPING_NAMESPACE + ".selectDwellingCharacteristics", requestFileId);
    }

    public List<DwellingCharacteristics> getDwellingCharacteristicsListByGroup(Long groupId){
        List<DwellingCharacteristics> list = sqlSession().selectList("selectDwellingCharacteristicsListByGroup", groupId);
        loadFacilityStreet(list);

        return list;
    }
}
