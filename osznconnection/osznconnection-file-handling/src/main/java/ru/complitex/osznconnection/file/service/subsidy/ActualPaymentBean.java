package ru.complitex.osznconnection.file.service.subsidy;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import ru.complitex.address.entity.AddressEntity;
import ru.complitex.organization.strategy.ServiceStrategy;
import ru.complitex.osznconnection.file.entity.*;
import ru.complitex.osznconnection.file.entity.example.ActualPaymentExample;
import ru.complitex.osznconnection.file.entity.subsidy.ActualPayment;
import ru.complitex.osznconnection.file.entity.subsidy.ActualPaymentDBF;
import ru.complitex.osznconnection.file.service.AbstractRequestBean;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import java.util.*;

/**
 *
 * @author Artem
 */
@Stateless
public class ActualPaymentBean extends AbstractRequestBean {

    public static final String MAPPING_NAMESPACE = ActualPaymentBean.class.getName();
    // service provider type id <-> set of actual payment fields that should be updated.
    private static final Map<Long, Set<ActualPaymentDBF>> UPDATE_FIELD_MAP =
            ImmutableMap.<Long, Set<ActualPaymentDBF>>builder().
            put(ServiceStrategy.APARTMENT_FEE, ImmutableSet.of(ActualPaymentDBF.P1, ActualPaymentDBF.N1)).
            put(ServiceStrategy.HEATING, ImmutableSet.of(ActualPaymentDBF.P2, ActualPaymentDBF.N2)).
            put(ServiceStrategy.HOT_WATER_SUPPLY, ImmutableSet.of(ActualPaymentDBF.P3, ActualPaymentDBF.N3)).
            put(ServiceStrategy.COLD_WATER_SUPPLY, ImmutableSet.of(ActualPaymentDBF.P4, ActualPaymentDBF.N4)).
            put(ServiceStrategy.GAS_SUPPLY, ImmutableSet.of(ActualPaymentDBF.P5, ActualPaymentDBF.N5)).
            put(ServiceStrategy.POWER_SUPPLY, ImmutableSet.of(ActualPaymentDBF.P6, ActualPaymentDBF.N6)).
            put(ServiceStrategy.GARBAGE_DISPOSAL, ImmutableSet.of(ActualPaymentDBF.P7, ActualPaymentDBF.N7)).
            put(ServiceStrategy.DRAINAGE, ImmutableSet.of(ActualPaymentDBF.P8, ActualPaymentDBF.N8)).
            build();

    public enum OrderBy {

        OWN_NUM(ActualPaymentDBF.OWN_NUM.name()),
        FIRST_NAME(ActualPaymentDBF.F_NAM.name()),
        MIDDLE_NAME(ActualPaymentDBF.M_NAM.name()),
        LAST_NAME(ActualPaymentDBF.SUR_NAM.name()),
        CITY(ActualPaymentDBF.N_NAME.name()),
        STREET(ActualPaymentDBF.VUL_NAME.name()),
        BUILDING(ActualPaymentDBF.BLD_NUM.name()),
        CORP(ActualPaymentDBF.CORP_NUM.name()),
        APARTMENT(ActualPaymentDBF.FLAT.name()),
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
        sqlSession().delete(MAPPING_NAMESPACE + ".deleteActualPayments", requestFileId);
    }


    public Long getCount(ActualPaymentExample example) {
        return sqlSession().selectOne(MAPPING_NAMESPACE + ".count", example);
    }


    @SuppressWarnings("unchecked")
    public List<ActualPayment> find(ActualPaymentExample example) {
        return sqlSession().selectList(MAPPING_NAMESPACE + ".find", example);
    }


    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void insert(List<ActualPayment> abstractRequests) {
        if (abstractRequests.isEmpty()) {
            return;
        }
        sqlSession().insert(MAPPING_NAMESPACE + ".insertActualPaymentList", abstractRequests);
    }

    public List<AbstractAccountRequest> getActualPayments(long requestFileId) {
        return sqlSession().selectList(MAPPING_NAMESPACE + ".selectActualPayments", requestFileId);
    }


    public void insert(ActualPayment actualPayment) {
        sqlSession().insert(MAPPING_NAMESPACE + ".insertActualPayment", actualPayment);
    }


    public void update(ActualPayment actualPayment) {
        sqlSession().update(MAPPING_NAMESPACE + ".update", actualPayment);
    }


    public void update(ActualPayment actualPayment, Set<Long> serviceProviderTypeIds) {
        Map<String, Object> updateFieldMap = null;
        if (serviceProviderTypeIds != null && !serviceProviderTypeIds.isEmpty()) {
            updateFieldMap = Maps.newHashMap();
            for (ActualPaymentDBF field : getUpdateableFields(serviceProviderTypeIds)) {
                updateFieldMap.put(field.name(), actualPayment.getStringField(field));
            }
        }
        actualPayment.setUpdateFieldMap(updateFieldMap);
        update(actualPayment);
    }

    private Set<ActualPaymentDBF> getUpdateableFields(Set<Long> serviceProviderTypeIds) {
        final Set<ActualPaymentDBF> updateableFields = Sets.newHashSet();

        for (long serviceProviderTypeId : serviceProviderTypeIds) {
            Set<ActualPaymentDBF> fields = UPDATE_FIELD_MAP.get(serviceProviderTypeId);
            if (fields != null) {
                updateableFields.addAll(fields);
            }
        }

        return Collections.unmodifiableSet(updateableFields);
    }


    @SuppressWarnings("unchecked")
    public List<ActualPayment> findForOperation(long fileId, List<Long> ids) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("requestFileId", fileId);
        params.put("ids", ids);
        return sqlSession().selectList(MAPPING_NAMESPACE + ".findForOperation", params);
    }


    @SuppressWarnings("unchecked")
    private List<Long> findIdsForOperation(long fileId) {
        return sqlSession().selectList(MAPPING_NAMESPACE + ".findIdsForOperation", fileId);
    }


    public List<Long> findIdsForBinding(long fileId) {
        return findIdsForOperation(fileId);
    }


    public List<Long> findIdsForProcessing(long fileId) {
        return findIdsForOperation(fileId);
    }

    private int unboundCount(long fileId) {
        return countByFile(fileId, RequestStatus.unboundStatuses());
    }

    //todo а не проще просчитать записи которые PROCESSED?
    private int unprocessedCount(long fileId) {
        return countByFile(fileId, RequestStatus.UNPROCESSED_SET_STATUSES);
    }

    private int countByFile(long fileId, Set<RequestStatus> statuses) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("requestFileId", fileId);
        params.put("statuses", statuses);
        return (Integer) sqlSession().selectOne(MAPPING_NAMESPACE + ".countByFile", params);
    }


    public boolean isActualPaymentFileBound(long fileId) {
        return unboundCount(fileId) == 0;
    }


    public boolean isActualPaymentFileProcessed(long fileId) {
        return unprocessedCount(fileId) == 0;
    }


    public void markCorrected(ActualPayment actualPayment, AddressEntity addressEntity) {
        Map<String, Object> params = Maps.newHashMap();

        params.put("fileId", actualPayment.getRequestFileId());

        switch (addressEntity) {
            case BUILDING:
                params.put("buildingNumber", actualPayment.getBuildingNumber());
                params.put("buildingCorp", actualPayment.getBuildingCorp());
            case STREET:
                params.put("streetCode", actualPayment.getStreetCode());
            case STREET_TYPE:
                params.put("streetType", actualPayment.getStreetType());
            case CITY:
                params.put("city", actualPayment.getCity());
        }

        sqlSession().update(MAPPING_NAMESPACE + ".markCorrected", params);
    }


    public void updateAccountNumber(ActualPayment actualPayment) {
        sqlSession().update(MAPPING_NAMESPACE + ".updateAccountNumber", actualPayment);
    }


    public void clearBeforeBinding(long fileId, Set<Long> serviceProviderTypeIds) {
        Map<String, String> updateFieldMap = null;
        if (serviceProviderTypeIds != null && !serviceProviderTypeIds.isEmpty()) {
            updateFieldMap = Maps.newHashMap();
            for (ActualPaymentDBF field : getUpdateableFields(serviceProviderTypeIds)) {
                updateFieldMap.put(field.name(), "-1");
            }
        }

        sqlSession().update(MAPPING_NAMESPACE + ".clearBeforeBinding",
                ImmutableMap.of("status", RequestStatus.LOADED, "fileId", fileId, "updateFieldMap", updateFieldMap));
        clearWarnings(fileId, RequestFileType.ACTUAL_PAYMENT);
    }


    public void clearBeforeProcessing(long fileId, Set<Long> serviceProviderTypeIds) {
        Map<String, String> updateFieldMap = null;
        if (serviceProviderTypeIds != null && !serviceProviderTypeIds.isEmpty()) {
            updateFieldMap = Maps.newHashMap();
            for (ActualPaymentDBF field : getUpdateableFields(serviceProviderTypeIds)) {
                updateFieldMap.put(field.name(), "-1");
            }
        }

        sqlSession().update(MAPPING_NAMESPACE + ".clearBeforeProcessing",
                ImmutableMap.of("statuses", RequestStatus.unboundStatuses(), "fileId", fileId, "updateFieldMap", updateFieldMap));
        clearWarnings(fileId, RequestFileType.ACTUAL_PAYMENT);
    }

    public Date getFirstDay(ActualPayment actualPayment, RequestFile actualPaymentFile) {
        Date beginDate = (Date) actualPayment.getField(ActualPaymentDBF.DAT_BEG);

        return beginDate != null ? beginDate : actualPaymentFile.getBeginDate();
    }
}
