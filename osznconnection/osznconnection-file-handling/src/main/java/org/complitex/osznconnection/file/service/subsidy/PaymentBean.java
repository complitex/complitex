package org.complitex.osznconnection.file.service.subsidy;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.complitex.address.entity.AddressEntity;
import org.complitex.organization.strategy.ServiceStrategy;
import org.complitex.osznconnection.file.entity.*;
import org.complitex.osznconnection.file.entity.example.PaymentExample;
import org.complitex.osznconnection.file.entity.subsidy.Payment;
import org.complitex.osznconnection.file.entity.subsidy.PaymentDBF;
import org.complitex.osznconnection.file.service.AbstractRequestBean;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import java.math.BigDecimal;
import java.util.*;

/**
 * Обработка записей файла запроса начислений
 *
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 24.08.2010 18:57:00
 */
@Stateless
public class PaymentBean extends AbstractRequestBean {

    public static final String MAPPING_NAMESPACE = PaymentBean.class.getName();
    // service provider type id <-> set of actual payment fields that should be updated.
    private static final Map<Long, Set<PaymentDBF>> UPDATE_FIELD_MAP =
            ImmutableMap.<Long, Set<PaymentDBF>>builder().
            put(ServiceStrategy.APARTMENT_FEE, ImmutableSet.of(PaymentDBF.CODE2_1, PaymentDBF.CODE3_1)).
            put(ServiceStrategy.HEATING, ImmutableSet.of(PaymentDBF.NORM_F_2, PaymentDBF.CODE2_2, PaymentDBF.CODE3_2)).
            put(ServiceStrategy.HOT_WATER_SUPPLY, ImmutableSet.of(PaymentDBF.NORM_F_3, PaymentDBF.CODE2_3, PaymentDBF.CODE3_3)).
            put(ServiceStrategy.COLD_WATER_SUPPLY, ImmutableSet.of(PaymentDBF.NORM_F_4, PaymentDBF.CODE2_4, PaymentDBF.CODE3_4)).
            put(ServiceStrategy.GAS_SUPPLY, ImmutableSet.of(PaymentDBF.NORM_F_5, PaymentDBF.CODE2_5, PaymentDBF.CODE3_5)).
            put(ServiceStrategy.POWER_SUPPLY, ImmutableSet.of(PaymentDBF.NORM_F_6, PaymentDBF.CODE2_6, PaymentDBF.CODE3_6)).
            put(ServiceStrategy.GARBAGE_DISPOSAL, ImmutableSet.of(PaymentDBF.NORM_F_7, PaymentDBF.CODE2_7, PaymentDBF.CODE3_7)).
            put(ServiceStrategy.DRAINAGE, ImmutableSet.of(PaymentDBF.NORM_F_8, PaymentDBF.CODE2_8, PaymentDBF.CODE3_8)).
            build();

    public enum OrderBy {

        ACCOUNT(PaymentDBF.OWN_NUM_SR.name()),
        FIRST_NAME(PaymentDBF.F_NAM.name()),
        MIDDLE_NAME(PaymentDBF.M_NAM.name()),
        LAST_NAME(PaymentDBF.SUR_NAM.name()),
        CITY(PaymentDBF.N_NAME.name()),
        STREET(PaymentDBF.VUL_NAME.name()),
        BUILDING(PaymentDBF.BLD_NUM.name()),
        CORP(PaymentDBF.CORP_NUM.name()),
        APARTMENT(PaymentDBF.FLAT.name()),
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
        sqlSession().delete(MAPPING_NAMESPACE + ".deletePayments", requestFileId);
    }


    public Long getCount(PaymentExample example) {
        return sqlSession().selectOne(MAPPING_NAMESPACE + ".count", example);
    }


    public List<Payment> find(PaymentExample example) {
        return sqlSession().selectList(MAPPING_NAMESPACE + ".find", example);
    }


    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void insert(List<AbstractRequest> abstractRequests) {
        if (abstractRequests.isEmpty()) {
            return;
        }

        sqlSession().insert(MAPPING_NAMESPACE + ".insertPaymentList", abstractRequests);
    }

    public List<Payment> getPayments(long requestFileId) {
        return sqlSession().selectList(MAPPING_NAMESPACE + ".selectPayments", requestFileId);
    }


    public void insert(Payment payment) {
        sqlSession().insert(MAPPING_NAMESPACE + ".insertPayment", payment);
    }


    public void update(Payment payment) {
        sqlSession().update(MAPPING_NAMESPACE + ".update", payment);
    }


    public void update(Payment payment, List<Long> serviceProviderTypeIds) {
        Map<String, Object> updateFieldMap = null;
        if (serviceProviderTypeIds != null && !serviceProviderTypeIds.isEmpty()) {
            updateFieldMap = Maps.newHashMap();
            for (PaymentDBF field : getUpdatableFields(serviceProviderTypeIds)) {
                updateFieldMap.put(field.name(), payment.getStringField(field));
            }
        }
        payment.setUpdateFieldMap(updateFieldMap);
        update(payment);
    }

    private Set<PaymentDBF> getUpdatableFields(List<Long> serviceProviderTypeIds) {
        final Set<PaymentDBF> updatableFields = Sets.newHashSet();

        for (long serviceProviderTypeId : serviceProviderTypeIds) {
            Set<PaymentDBF> fields = UPDATE_FIELD_MAP.get(serviceProviderTypeId);
            if (fields != null) {
                updatableFields.addAll(fields);
            }
        }

        return Collections.unmodifiableSet(updatableFields);
    }

    /**
     * Получить все payment записи в файле с id из списка ids
     * @param fileId fileId
     * @param ids ids
     * @return все payment записи в файле с id из списка ids
     */

    @SuppressWarnings("unchecked")
    public List<Payment> findForOperation(long fileId, List<Long> ids) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("requestFileId", fileId);
        params.put("ids", ids);
        return sqlSession().selectList(MAPPING_NAMESPACE + ".findForOperation", params);
    }

    /**
     * Получить все id payment записей в файле
     * @param fileId fileId
     * @return все id payment записей в файле
     */

    @SuppressWarnings("unchecked")
    private List<Long> findIdsForOperation(long fileId) {
        return sqlSession().selectList(MAPPING_NAMESPACE + ".findIdsForOperation", fileId);
    }

    /**
     * Получить все id payment записей в файле для связывания
     * @param fileId fileId
     * @return все id payment записей в файле для связывания
     */

    public List<Long> findIdsForBinding(long fileId) {
        return findIdsForOperation(fileId);
    }

    /**
     * Получить все id payment записей в файле для обработки
     * @param fileId fileId
     * @return все id payment записей в файле для обработки
     */

    public List<Long> findIdsForProcessing(long fileId) {
        return findIdsForOperation(fileId);
    }

    /**
     * Возвращает кол-во несвязанных записей в файле.
     * @param fileId fileId
     * @return кол-во несвязанных записей в файле.
     */
    private int unboundCount(long fileId) {
        return countByFile(fileId, RequestStatus.unboundStatuses());
    }

    /**
     * Возвращает кол-во необработанных записей
     * @param fileId fileId
     * @return кол-во необработанных записей
     */
    private int unprocessedCount(long fileId) {
        return countByFile(fileId, RequestStatus.UNPROCESSED_SET_STATUSES);
    }

    /**
     * Возвращает кол-во записей со статусами из списка statuses
     * @param fileId fileId
     * @param statuses statuses
     * @return кол-во записей со статусами из списка statuses
     */
    public int countByFile(long fileId, Set<RequestStatus> statuses) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("requestFileId", fileId);
        params.put("statuses", statuses);
        return sqlSession().selectOne(MAPPING_NAMESPACE + ".countByFile", params);
    }

    /**
     * @param fileId fileId
     * @return Связан ли файл
     */

    public boolean isPaymentFileBound(long fileId) {
        return unboundCount(fileId) == 0;
    }

    /**
     *
     * @param fileId fileId
     * @return Обработан ли файл
     */

    public boolean isPaymentFileProcessed(long fileId) {
        return unprocessedCount(fileId) == 0;
    }


    public void markCorrected(Payment payment, AddressEntity entity) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("fileId", payment.getRequestFileId());

        switch (entity){
            case BUILDING:
                params.put("cityId", payment.getCityId());
                params.put("streetId", payment.getStreetId());
                params.put("buildingNumber", payment.getBuildingNumber());
                params.put("buildingCorp", payment.getBuildingCorp());
                break;
            case STREET:
                params.put("cityId", payment.getCityId());
                params.put("streetTypeId", payment.getStreetTypeId());
                params.put("street", payment.getStreet());
                break;
            case CITY:
                params.put("city", payment.getCity());
                break;
        }

        sqlSession().update(MAPPING_NAMESPACE + ".markCorrected", params);
    }


    public void updateAccountNumber(Payment payment) {
        sqlSession().update(MAPPING_NAMESPACE + ".updateAccountNumber", payment);
    }

    /**
     * очищает колонки которые заполняются во время связывания и обработки для записей payment
     * @param fileId fileId
     */

    public void clearBeforeBinding(long fileId, List<Long> serviceProviderTypeIds) {
        Map<String, String> updateFieldMap = null;
        if (serviceProviderTypeIds != null && !serviceProviderTypeIds.isEmpty()) {
            updateFieldMap = Maps.newHashMap();
            for (PaymentDBF field : getUpdatableFields(serviceProviderTypeIds)) {
                updateFieldMap.put(field.name(), null);
            }
        }

        sqlSession().update(MAPPING_NAMESPACE + ".clearBeforeBinding",
                ImmutableMap.of("status", RequestStatus.LOADED, "fileId", fileId, "updateFieldMap", updateFieldMap));
        clearWarnings(fileId, RequestFileType.PAYMENT);
    }

    /**
     * очищает колонки которые заполняются во время обработки для записей payment
     * @param fileId fileId
     */

    public void clearBeforeProcessing(long fileId, List<Long> serviceProviderTypeIds) {
        Map<String, String> updateFieldMap = null;
        if (serviceProviderTypeIds != null && !serviceProviderTypeIds.isEmpty()) {
            updateFieldMap = Maps.newHashMap();
            for (PaymentDBF field : getUpdatableFields(serviceProviderTypeIds)) {
                updateFieldMap.put(field.name(), null);
            }
        }

        sqlSession().update(MAPPING_NAMESPACE + ".clearBeforeProcessing",
                ImmutableMap.of("statuses", RequestStatus.unboundStatuses(), "fileId", fileId, "updateFieldMap", updateFieldMap));
        clearWarnings(fileId, RequestFileType.PAYMENT);
    }

    /**
     * Получает дату из поля DAT1 в записи payment, у которой account number = accountNumber и
     * кроме того поле FROG больше 0(только benefit записи соответствующие таким payment записям нужно обрабатывать).
     * @param accountNumber accountNumber
     * @param benefitFileId benefitFileId
     * @return дату из поля DAT1 в записи payment, у которой account number = accountNumber и
     * кроме того поле FROG больше 0
     */

    public Date findDat1(String accountNumber, long benefitFileId) {
        @SuppressWarnings("unchecked")
        List<Payment> payments = sqlSession().selectList(MAPPING_NAMESPACE + ".findDat1",
                ImmutableMap.of("accountNumber", accountNumber, "benefitFileId", benefitFileId));

        final Set<Date> dat1Set = Sets.newHashSet();

        for (Payment p : payments) {
            Object frog = p.getField(PaymentDBF.FROG);

            if (frog != null) {
                if (frog instanceof Long && (Long) frog > 0) {
                    dat1Set.add((Date) p.getField(PaymentDBF.DAT1));
                } else if (frog instanceof BigDecimal && ((BigDecimal) frog).compareTo(BigDecimal.ZERO) > 0) {
                    dat1Set.add((Date) p.getField(PaymentDBF.DAT1));
                }
            }
        }

        if (dat1Set.isEmpty()){
            return null;
        }else if (dat1Set.size() == 1) {
            return dat1Set.iterator().next();
        } else {
            throw new IllegalStateException("Found more one payment's dat1 values.");
        }
    }
}
