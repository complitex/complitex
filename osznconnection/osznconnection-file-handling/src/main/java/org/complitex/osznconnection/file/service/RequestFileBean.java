package org.complitex.osznconnection.file.service;

import com.google.common.collect.ImmutableMap;
import org.complitex.common.exception.ExecuteException;
import org.complitex.common.service.AbstractBean;
import org.complitex.common.service.SessionBean;
import org.complitex.common.util.DateUtil;
import org.complitex.osznconnection.file.entity.*;
import org.complitex.osznconnection.file.service.privilege.*;
import org.complitex.osznconnection.file.service.subsidy.*;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 25.08.2010 12:15:53
 *
 * Работа с базой данных для файла запроса.
 * Поиск, сохранение, обновление, удаление, проверка на наличие в базе.
 * Изменение статуса при отмене процесса загрузки и сохранения.
 *
 * @see org.complitex.osznconnection.file.entity.RequestFile
 */
@Stateless
public class RequestFileBean extends AbstractBean {
    public static final String NS = RequestFileBean.class.getName();

    @EJB
    private SessionBean sessionBean;

    @EJB
    private SubsidyTarifBean subsidyTarifBean;

    @EJB
    private PaymentBean paymentBean;

    @EJB
    private BenefitBean benefitBean;

    @EJB
    private ActualPaymentBean actualPaymentBean;

    @EJB
    private SubsidyBean subsidyBean;

    @EJB
    private DwellingCharacteristicsBean dwellingCharacteristicsBean;

    @EJB
    private FacilityServiceTypeBean facilityServiceTypeBean;

    @EJB
    private FacilityForm2Bean facilityForm2Bean;

    @EJB
    private FacilityReferenceBookBean facilityReferenceBookBean;

    @EJB
    private RequestFileHistoryBean requestFileHistoryBean;

    @EJB
    private PrivilegeProlongationBean privilegeProlongationBean;

    public RequestFile getRequestFile(long fileId) {
        return sqlSession().selectOne(NS + ".findById", fileId);
    }

    //todo move methods to own bean
    public List<RequestFile> getRequestFiles(RequestFileFilter filter) {
        sessionBean.prepareFilterForPermissionCheck(filter);

        switch (filter.getType()) {
            case SUBSIDY_TARIF:
            case FACILITY_STREET_TYPE_REFERENCE:
            case FACILITY_STREET_REFERENCE:
            case FACILITY_TARIF_REFERENCE:
                return getLoadedRequestFiles(filter);

            case ACTUAL_PAYMENT:
            case DWELLING_CHARACTERISTICS:
            case FACILITY_SERVICE_TYPE:
            case FACILITY_FORM2:
            case PRIVILEGE_PROLONGATION:
                return getProcessedRequestFiles(filter);

            case SUBSIDY:
                return getSubsidyFiles(filter);
        }
        throw new IllegalStateException("Unexpected request file type detected: '" + filter.getType() + "'.");
    }


    private List<RequestFile> getLoadedRequestFiles(RequestFileFilter filter) {
        return sqlSession().selectList(NS + ".selectLoadedRequestFiles", filter);
    }

    private List<RequestFile> getProcessedRequestFiles(RequestFileFilter filter) {
        return sqlSession().selectList(NS + ".selectProcessedRequestFiles", filter);
    }

    private List<RequestFile> getSubsidyFiles(RequestFileFilter filter) {
        return sqlSession().selectList(NS + ".selectSubsidyFiles", filter);
    }

    public Long getCount(RequestFileFilter filter) {
        sessionBean.prepareFilterForPermissionCheck(filter);
        return sqlSession().selectOne(NS + ".selectRequestFilesCount", filter);
    }


    public void save(RequestFile requestFile) {
        if (requestFile.getId() == null) {
            sqlSession().insert(NS + ".insertRequestFile", requestFile);

            //history
            requestFileHistoryBean.save(new RequestFileHistory(requestFile.getId(), requestFile.getStatus(), DateUtil.getCurrentDate()));
        } else {
            sqlSession().update(NS + ".updateRequestFile", requestFile);

            //history
            RequestFileHistory last = requestFileHistoryBean.getLastRequestFileHistory(requestFile.getId());

            if (last != null && !last.getStatus().equals(requestFile.getStatus())){
                requestFileHistoryBean.save(new RequestFileHistory(requestFile.getId(), requestFile.getStatus(), DateUtil.getCurrentDate()));
            }

        }
    }

    public void delete(RequestFile requestFile) {
        if (requestFile.getType() != null && requestFile.getId() != null) {
            switch (requestFile.getType()) {
                case BENEFIT:
                    benefitBean.delete(requestFile.getId());
                    break;
                case PAYMENT:
                    paymentBean.delete(requestFile.getId());
                    break;
                case SUBSIDY_TARIF:
                    subsidyTarifBean.delete(requestFile.getId());
                    break;
                case ACTUAL_PAYMENT:
                    actualPaymentBean.delete(requestFile.getId());
                    break;
                case SUBSIDY:
                    subsidyBean.delete(requestFile.getId());
                    break;
                case DWELLING_CHARACTERISTICS:
                    dwellingCharacteristicsBean.delete(requestFile.getId());
                    break;
                case FACILITY_SERVICE_TYPE:
                    facilityServiceTypeBean.delete(requestFile.getId());
                    break;
                case FACILITY_FORM2:
                    facilityForm2Bean.delete(requestFile.getId());
                    break;
                case FACILITY_STREET_TYPE_REFERENCE:
                case FACILITY_STREET_REFERENCE:
                case FACILITY_TARIF_REFERENCE:
                    facilityReferenceBookBean.delete(requestFile.getId(), requestFile.getType());
                    break;

                case PRIVILEGE_PROLONGATION:
                    privilegeProlongationBean.delete(requestFile.getId());
                    break;
            }
        }
        sqlSession().delete(NS + ".deleteRequestFile", requestFile.getId());
    }

    public Long getLoadedId(RequestFile requestFile) {
        return sqlSession().selectOne(NS + ".selectLoadedId", requestFile);
    }

    private boolean checkLoaded(RequestFile requestFile) {
        return sqlSession().selectOne(NS + ".selectIsLoaded", requestFile);
    }


    public void deleteSubsidyTarifFiles(Long organizationId, Long userOrganizationId) {
        List<RequestFile> subsidyTarifs = sqlSession().selectList(NS + ".findSubsidyTarifFiles",
                ImmutableMap.of("organizationId", organizationId, "userOrganizationId", userOrganizationId));

        for (RequestFile subsidyTarif : subsidyTarifs) {
            delete(subsidyTarif);
        }
    }

    public void deleteFacilityReferenceFiles(long osznId, long userOrganizationId, RequestFileType requestFileType) {
        List<RequestFile> facilityReferenceFiles = sqlSession().selectList(NS + ".getFacilityReferenceFiles",
                ImmutableMap.of("osznId", osznId, "userOrganizationId", userOrganizationId,
                        "requestFileType", requestFileType.name()));
        for (RequestFile facilityReferenceFile : facilityReferenceFiles) {
            delete(facilityReferenceFile);
        }
    }

    public RequestFileStatus getRequestFileStatus(Long requestFileId) {
        return sqlSession().selectOne(NS + ".selectRequestFileStatus", requestFileId);
    }

    public void fixProcessingOnInit() {
        sqlSession().update(NS + ".fixLoadingOnInit");
        sqlSession().update(NS + ".fixBingingOnInit");
        sqlSession().update(NS + ".fixFillingOnInit");
        sqlSession().update(NS + ".fixSavingOnInit");
    }

    private RequestFile getLastRequestFile(RequestFile requestFile){
        return sqlSession().selectOne(NS + ".selectLastRequestFile", requestFile);
    }

    private RequestFile getFirstRequestFile(RequestFile requestFile){
        return sqlSession().selectOne(NS + ".selectFirstRequestFile", requestFile);
    }

    public void updateDateRange(RequestFile requestFile) throws ExecuteException {
        RequestFile last = getLastRequestFile(requestFile);

        if (last != null){
            last.setEndDate(requestFile.getBeginDate());

            save(last);
        }else {
            RequestFile first = getFirstRequestFile(requestFile);

            if (first != null){
                requestFile.setEndDate(first.getBeginDate());
            } else if (checkLoaded(requestFile)){
                throw new ExecuteException("Файл {0} за месяц {1} уже загружен", requestFile.getFullName(), DateUtil.format(requestFile.getBeginDate()));
            }
        }
    }

    public void updateStatus(Long requestFileId, RequestFileStatus status){
        RequestFile requestFile = getRequestFile(requestFileId);
        requestFile.setStatus(status);

        save(requestFile);
    }
}
