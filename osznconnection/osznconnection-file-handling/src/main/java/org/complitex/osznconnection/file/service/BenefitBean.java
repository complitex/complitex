package org.complitex.osznconnection.file.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.complitex.osznconnection.file.entity.*;
import org.complitex.osznconnection.file.entity.example.BenefitExample;
import org.complitex.osznconnection.file.service_provider.ServiceProviderAdapter;
import org.complitex.osznconnection.file.service_provider.exception.DBException;
import org.complitex.osznconnection.organization.strategy.OsznOrganizationStrategy;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import java.util.*;

/**
 * Обработка записей файла запроса возмещения по льготам 
 *
 * @author Artem
 * @author Anatoly A. Ivanov java@inheaven.ru
 */
@Stateless(name = "BenefitBean")
public class BenefitBean extends AbstractRequestBean {

    public static final String MAPPING_NAMESPACE = BenefitBean.class.getName();
    @EJB
    private PaymentBean paymentBean;

    @EJB
    private PrivilegeCorrectionBean privilegeCorrectionBean;

    @EJB
    private RequestFileGroupBean requestFileGroupBean;

    @EJB
    private RequestFileBean requestFileBean;

    @EJB
    private ServiceProviderAdapter adapter;

    @EJB
    private OsznOrganizationStrategy organizationStrategy;

    public enum OrderBy {

        ACCOUNT(BenefitDBF.OWN_NUM_SR.name()),
        FIRST_NAME(BenefitDBF.F_NAM.name()),
        MIDDLE_NAME(BenefitDBF.M_NAM.name()),
        LAST_NAME(BenefitDBF.SUR_NAM.name()),
        CITY("city"),
        STREET("street"),
        BUILDING("building_number"),
        CORP("building_corp"),
        APARTMENT("apartment"),
        PRIVILEGE(BenefitDBF.PRIV_CAT.name()),
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
        sqlSession().delete(MAPPING_NAMESPACE + ".deleteBenefits", requestFileId);
    }


    public Long getCount(BenefitExample example) {
        return sqlSession().selectOne(MAPPING_NAMESPACE + ".count", example);
    }


    @SuppressWarnings("unchecked")
    public List<Benefit> find(BenefitExample example) {
        return sqlSession().selectList(MAPPING_NAMESPACE + ".find", example);
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
     * @param fileId fileId
     * @return Связан ли файл
     */

    public boolean isBenefitFileBound(long fileId) {
        return unboundCount(fileId) == 0;
    }


    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void insert(List<AbstractRequest> abstractRequests) {
        if (abstractRequests.isEmpty()) {
            return;
        }
        sqlSession().insert(MAPPING_NAMESPACE + ".insertBenefitList", abstractRequests);
    }

    public List<Benefit> getBenefits(long requestFileId) {
        return sqlSession().selectList(MAPPING_NAMESPACE + ".selectBenefits", requestFileId);
    }


    public void insert(Benefit benefit) {
        sqlSession().insert(MAPPING_NAMESPACE + ".insertBenefit", benefit);
    }

    /**
     * Когда у payment записи в UI вручную меняют адрес, у этой записи и у всех соответствующих benefit записей
     * статус проставляется в ADDRESS_CORRECTED.
     * Данный метод устанавливает статус для benefit записей.
     */

    public void markCorrected(long paymentFileId) {
        sqlSession().update(MAPPING_NAMESPACE + ".markCorrected", paymentFileId);
    }

    /**
     * Обновляет номер л/c для всех benefit записей которые соответствуют payment записи c paymentId.
     * @param paymentId id payment записи
     * @param accountNumber номер л/c
     */

    public void updateAccountNumber(long paymentId, String accountNumber) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("paymentId", paymentId);
        params.put("accountNumber", accountNumber);
        params.put("status", RequestStatus.ACCOUNT_NUMBER_RESOLVED);
        sqlSession().update(MAPPING_NAMESPACE + ".updateAccountNumber", params);
    }

    /**
     * Обновляет статус для всех benefit записей из файла, а именно копирует статус из соответствующей payment записи.
     * @param fileId fileId
     */

    public void updateBindingStatus(long fileId) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("fileId", fileId);
        params.put("statuses", RequestStatus.unboundStatuses());
        sqlSession().update(MAPPING_NAMESPACE + ".updateBindingStatus", params);
    }

    /**
     * Заполняет некоторые поля в benefit записях, соответствующих payment c id = paymentId.
     * Вызывается при обработке payment файла.
     * См. ProcessingRequestBean.process().
     * @param benefit benefit
     */

    public void populateBenefit(Benefit benefit) {
        sqlSession().update(MAPPING_NAMESPACE + ".populateBenefit", benefit);
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
     *
     * @param fileId fileId
     * @return Обработан ли файл
     */

    public boolean isBenefitFileProcessed(long fileId) {
        return unprocessedCount(fileId) == 0;
    }

    /**
     * Возвращает кол-во записей со статусами из списка statuses
     * @param fileId fileId
     * @param statuses statuses
     * @return кол-во записей
     */
    private int countByFile(long fileId, Set<RequestStatus> statuses) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("requestFileId", fileId);
        params.put("statuses", statuses);
        return sqlSession().selectOne(MAPPING_NAMESPACE + ".countByFile", params);
    }

    /**
     * Получает все не null account numbers в файле.
     * @param fileId fileId
     * @return все не null account numbers в файле
     */

    @SuppressWarnings("unchecked")
    public List<String> getAllAccountNumbers(long fileId) {
        return sqlSession().selectList(MAPPING_NAMESPACE + ".allAccountNumbers", fileId);
    }

    /**
     * Достает все записи benefit по номеру л/c из файла.
     * @param accountNumber accountNumber
     * @param fileId fileId
     * @return все записи benefit по номеру л/c из файла.
     */

    @SuppressWarnings("unchecked")
    public List<Benefit> findByAccountNumber(String accountNumber, long fileId) {
        Map<String, Object> params = Maps.newHashMap();

        params.put("fileId", fileId);
        params.put("accountNumber", accountNumber);

        return sqlSession().selectList(MAPPING_NAMESPACE + ".findByAccountNumber", params);
    }


    public void update(Benefit benefit) {
        sqlSession().update(MAPPING_NAMESPACE + ".update", benefit);
    }


    public void updateStatusByAccountNumber(long fileId, String accountNumber, RequestStatus status) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("accountNumber", accountNumber);
        params.put("fileId", fileId);
        params.put("status", status);
        sqlSession().update(MAPPING_NAMESPACE + ".updateStatusByAccountNumber", params);
    }

    @SuppressWarnings("unchecked")
    public List<Benefit> findByOZN(Payment payment) {
        return sqlSession().selectList(MAPPING_NAMESPACE + ".findByOZN", payment);
    }

    /**
     * очищает колонки которые заполняются во время связывания и обработки для записей benefit
     * @param fileId fileId
     */

    public void clearBeforeBinding(long fileId) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("status", RequestStatus.LOADED);
        params.put("fileId", fileId);
        sqlSession().update(MAPPING_NAMESPACE + ".clearBeforeBinding", params);
        clearWarnings(fileId, RequestFileType.BENEFIT);
    }

    /**
     * очищает колонки которые заполняются во время обработки для записей benefit
     * @param fileId fileId
     */

    public void clearBeforeProcessing(long fileId) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("statuses", RequestStatus.unboundStatuses());
        params.put("fileId", fileId);
        sqlSession().update(MAPPING_NAMESPACE + ".clearBeforeProcessing", params);
        clearWarnings(fileId, RequestFileType.BENEFIT);
    }

    public Collection<BenefitData> getBenefitData(Benefit benefit) throws DBException {
        Long billingId = organizationStrategy.getBillingId(benefit.getUserOrganizationId());
        long osznId = benefit.getOrganizationId();
        RequestFile benefitRequestFile = requestFileBean.findById(benefit.getRequestFileId());

        final Date dat1 = paymentBean.findDat1(benefit.getAccountNumber(), benefitRequestFile.getId());

        Collection<BenefitData> benefitData = adapter.getBenefitData(benefit, dat1);

        Collection<BenefitData> notConnectedBenefitData = null;
        if (benefitData != null && !benefitData.isEmpty()) {
            notConnectedBenefitData = Lists.newArrayList();
            List<Benefit> benefits = findByAccountNumber(benefit.getAccountNumber(), benefit.getRequestFileId());

            for (BenefitData benefitDataItem : benefitData) {
                boolean suitable = true;

                String benefitOrderFam = benefitDataItem.getOrderFamily();
                Integer benefitOrderFamAsInt = null;
                try {
                    benefitOrderFamAsInt = Integer.valueOf(benefitOrderFam);
                } catch (NumberFormatException e) {
                    //hello catch
                }

                for (Benefit benefitItem : benefits) {
                    Integer benefitItemOrderFam = (Integer) benefitItem.getField(BenefitDBF.ORD_FAM);
                    if (benefitItemOrderFam != null && benefitItemOrderFam.equals(benefitOrderFamAsInt)) {
                        suitable = false;
                    }
                }

                if (suitable) {
                    String osznBenefitCode = null;
                    Long internalPrivilege = privilegeCorrectionBean.findInternalPrivilege(benefitDataItem.getCode(), billingId);
                    if (internalPrivilege != null) {
                        osznBenefitCode = privilegeCorrectionBean.findPrivilegeCode(internalPrivilege, osznId,
                                benefit.getUserOrganizationId());
                    }
                    benefitDataItem.setPrivilegeObjectId(internalPrivilege);
                    benefitDataItem.setOsznPrivilegeCode(osznBenefitCode);
                    benefitDataItem.setCalcCenterId(billingId);
                    notConnectedBenefitData.add(benefitDataItem);
                }
            }
        }
        return notConnectedBenefitData;
    }

    public void connectBenefit(Benefit benefit, final BenefitData selectedBenefitData, boolean checkBenefitData) throws DBException {
        String osznBenefitCode = selectedBenefitData.getOsznPrivilegeCode();
        benefit.setField(BenefitDBF.PRIV_CAT, Integer.valueOf(osznBenefitCode));
        benefit.setField(BenefitDBF.ORD_FAM, Integer.valueOf(selectedBenefitData.getOrderFamily()));
        benefit.setStatus(RequestStatus.PROCESSED);

        update(benefit);

        if (checkBenefitData) {
            Collection<BenefitData> leftBenefitData = getBenefitData(benefit);
            if (leftBenefitData == null || leftBenefitData.isEmpty()) {
                updateStatusByAccountNumber(benefit.getRequestFileId(), benefit.getAccountNumber(), RequestStatus.PROCESSED);
            }
        } else {
            updateStatusByAccountNumber(benefit.getRequestFileId(), benefit.getAccountNumber(), RequestStatus.PROCESSED);
        }

        long benefitFileId = benefit.getRequestFileId();
        long paymentFileId = requestFileGroupBean.getPaymentFileId(benefit.getRequestFileId());
        if (isBenefitFileProcessed(benefitFileId) && paymentBean.isPaymentFileProcessed(paymentFileId)) {
            requestFileGroupBean.updateStatus(benefitFileId, RequestFileStatus.FILLED);
        }
    }
}
