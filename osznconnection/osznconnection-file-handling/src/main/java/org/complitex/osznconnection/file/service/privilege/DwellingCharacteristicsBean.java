package org.complitex.osznconnection.file.service.privilege;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.complitex.address.entity.AddressEntity;
import org.complitex.osznconnection.file.entity.AbstractAccountRequest;
import org.complitex.osznconnection.file.entity.RequestFileType;
import org.complitex.osznconnection.file.entity.RequestStatus;
import org.complitex.osznconnection.file.entity.example.PrivilegeExample;
import org.complitex.osznconnection.file.entity.privilege.DwellingCharacteristics;
import org.complitex.osznconnection.file.entity.privilege.DwellingCharacteristicsDBF;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import java.util.*;

import static com.google.common.collect.ImmutableMap.of;
import static org.complitex.osznconnection.file.entity.privilege.DwellingCharacteristicsDBF.CDUL;

@Stateless
public class DwellingCharacteristicsBean extends AbstractPrivilegeBean {
    public static final String NS = DwellingCharacteristicsBean.class.getName();

    private static final Map<Long, Set<DwellingCharacteristicsDBF>> UPDATE_FIELD_MAP = of();

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
        sqlSession().delete(NS + ".deleteDwellingCharacteristics", requestFileId);
    }


    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void insert(List<DwellingCharacteristics> abstractRequests) {
        if (abstractRequests.isEmpty()) {
            return;
        }

        sqlSession().insert(NS + ".insertDwellingCharacteristicsList", abstractRequests);
    }


    public Long getCount(PrivilegeExample example) {
        return sqlSession().selectOne(NS + ".count", example);
    }


    public List<DwellingCharacteristics> find(PrivilegeExample example) {
        List<DwellingCharacteristics> list = sqlSession().selectList(NS + ".find", example);

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
        return sqlSession().selectOne(NS + ".countByFile", params);
    }

    public void update(DwellingCharacteristics dwellingCharacteristics) {
        sqlSession().update(NS + ".update", dwellingCharacteristics);
    }


    public void updateAccountNumber(DwellingCharacteristics dwellingCharacteristics) {
        sqlSession().update(NS + ".updateAccountNumber", dwellingCharacteristics);
    }


    public List<Long> findIdsForBinding(long fileId) {
        return findIdsForOperation(fileId);
    }


    public List<Long> findIdsForOperation(long fileId) {
        return sqlSession().selectList(NS + ".findIdsForOperation", fileId);
    }

    public List<DwellingCharacteristics> findForOperation(long fileId, List<Long> ids) {
        List<DwellingCharacteristics> list = sqlSession().selectList(NS + ".findForOperation",
                of("requestFileId", fileId, "ids", ids));

        loadFacilityStreet(list);

        return list;
    }

    public void clearBeforeBinding(long fileId, Set<Long> serviceProviderTypeIds) {
        Map<String, String> updateFieldMap = new HashMap<>();

        if (serviceProviderTypeIds != null && !serviceProviderTypeIds.isEmpty()) {
            updateFieldMap = Maps.newHashMap();
            for (DwellingCharacteristicsDBF field : getUpdateableFields(serviceProviderTypeIds)) {
                updateFieldMap.put(field.name(), "-1");
            }
        }

        sqlSession().update(NS + ".clearBeforeBinding",
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

    public void markCorrected(Long requestFileId, AbstractAccountRequest request, AddressEntity addressEntity) {
        markPrivilegeCorrected(NS + ".markCorrected", requestFileId, request, addressEntity);
    }

    public void markCorrected(DwellingCharacteristics dwellingCharacteristics, AddressEntity addressEntity) {
        markPrivilegeCorrected(NS + ".markCorrected", dwellingCharacteristics, addressEntity);
    }

    public List<DwellingCharacteristics> getDwellingCharacteristics(long requestFileId) {
        return sqlSession().selectList(NS + ".selectDwellingCharacteristics", requestFileId);
    }

    public List<DwellingCharacteristics> getDwellingCharacteristicsListByGroup(Long groupId){
        List<DwellingCharacteristics> list = sqlSession().selectList("selectDwellingCharacteristicsListByGroup", groupId);
        loadFacilityStreet(list);

        return list;
    }
}
