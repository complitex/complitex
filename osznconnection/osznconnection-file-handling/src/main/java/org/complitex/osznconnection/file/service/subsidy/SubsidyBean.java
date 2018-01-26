package org.complitex.osznconnection.file.service.subsidy;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.complitex.address.entity.AddressEntity;
import org.complitex.common.service.SessionBean;
import org.complitex.osznconnection.file.entity.RequestFileType;
import org.complitex.osznconnection.file.entity.RequestStatus;
import org.complitex.osznconnection.file.entity.example.SubsidyExample;
import org.complitex.osznconnection.file.entity.subsidy.Subsidy;
import org.complitex.osznconnection.file.entity.subsidy.SubsidyDBF;
import org.complitex.osznconnection.file.service.AbstractRequestBean;
import org.complitex.osznconnection.organization.strategy.OsznOrganizationStrategy;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Stateless
public class SubsidyBean extends AbstractRequestBean {
    public static final String NS = SubsidyBean.class.getName();
    private static final Map<Long, Set<SubsidyDBF>> UPDATE_FIELD_MAP = ImmutableMap.of();

    @EJB
    private OsznOrganizationStrategy organizationStrategy;

    @EJB
    private SessionBean sessionBean;

    public enum OrderBy {

        RASH(SubsidyDBF.RASH.name()),
        FIRST_NAME("first_name"),
        MIDDLE_NAME("middle_name"),
        LAST_NAME("last_name"),
        CITY(SubsidyDBF.NP_NAME.name()),
        STREET(SubsidyDBF.NAME_V.name()),
        BUILDING(SubsidyDBF.BLD.name()),
        CORP(SubsidyDBF.CORP.name()),
        APARTMENT(SubsidyDBF.FLAT.name()),
        DAT1(SubsidyDBF.DAT1.name()),
        DAT2(SubsidyDBF.DAT2.name()),
        NUMM(SubsidyDBF.NUMM.name()),
        NM_PAY(SubsidyDBF.NM_PAY.name()),
        SUMMA(SubsidyDBF.SUMMA.name()),
        SUBS(SubsidyDBF.SUBS.name()),
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
        sqlSession().delete(NS + ".deleteSubsidies", requestFileId);
    }


    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void insert(List<Subsidy> abstractRequests) {
        if (abstractRequests.isEmpty()) {
            return;
        }

        sqlSession().insert(NS + ".insertSubsidyList", abstractRequests);
    }


    public Long getCount(SubsidyExample example) {
        return sqlSession().selectOne(NS + ".count", example);
    }


    public List<Subsidy> find(SubsidyExample example) {
        return sqlSession().selectList(NS + ".find", example);
    }


    public void updateAccountNumberForSimilarSubs(Subsidy subsidy) {
        sqlSession().update(NS + ".updateAccountNumberForSimislarSubs", subsidy);
    }

    public boolean isSubsidyFileBound(long fileId) {
        return countByFile(fileId, RequestStatus.unboundStatuses()) == 0;
    }

    public boolean isSubsidyFileFilled(Long requestFileId){
        return countByFile(requestFileId, RequestStatus.UNPROCESSED_SET_STATUSES) == 0;
    }

    private int countByFile(long fileId, Set<RequestStatus> statuses) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("requestFileId", fileId);
        params.put("statuses", statuses);
        return sqlSession().selectOne(NS + ".countByFile", params);
    }


    public void update(Subsidy subsidy) {
        sqlSession().update(NS + ".update", subsidy);
    }


    public List<Long> findIdsForBinding(long fileId) {
        return findIdsForOperation(fileId);
    }


    private List<Long> findIdsForOperation(long fileId) {
        return sqlSession().selectList(NS + ".findIdsForOperation", fileId);
    }


    public List<Subsidy> findForOperation(long fileId, List<Long> ids) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("requestFileId", fileId);
        params.put("ids", ids);
        return sqlSession().selectList(NS + ".findForOperation", params);
    }


    public void clearBeforeBinding(long fileId, Set<Long> serviceProviderTypeIds) {
        Map<String, String> updateFieldMap = Maps.newHashMap();;
        if (serviceProviderTypeIds != null && !serviceProviderTypeIds.isEmpty()) {
            for (SubsidyDBF field : getUpdatableFields(serviceProviderTypeIds)) {
                updateFieldMap.put(field.name(), "-1");
            }
        }

        sqlSession().update(NS + ".clearBeforeBinding",
                ImmutableMap.of("status", RequestStatus.LOADED, "fileId", fileId, "updateFieldMap", updateFieldMap));
        clearWarnings(fileId, RequestFileType.SUBSIDY);
    }

    private Set<SubsidyDBF> getUpdatableFields(Set<Long> serviceProviderTypeIds) {
        final Set<SubsidyDBF> updatableFields = Sets.newHashSet();

        for (long serviceProviderTypeId : serviceProviderTypeIds) {
            Set<SubsidyDBF> fields = UPDATE_FIELD_MAP.get(serviceProviderTypeId);
            if (fields != null) {
                updatableFields.addAll(fields);
            }
        }

        return Collections.unmodifiableSet(updatableFields);
    }


    public void markCorrected(Subsidy subsidy, AddressEntity entity) {
        Map<String, Object> params = Maps.newHashMap();

        params.put("fileId", subsidy.getRequestFileId());

        switch (entity){
            case BUILDING:
                params.put("cityId", subsidy.getCityId());
                params.put("streetId", subsidy.getStreetId());
                params.put("buildingNumber", subsidy.getBuildingNumber());
                params.put("buildingCorp", subsidy.getBuildingCorp());
                break;
            case STREET:
                params.put("cityId", subsidy.getCityId());
                params.put("streetTypeId", subsidy.getStreetTypeId());
                params.put("street", subsidy.getStreet());
                break;
            case CITY:
                params.put("city", subsidy.getCity());
                break;
            case STREET_TYPE:
                params.put("streetType", subsidy.getStreetType());
                break;
        }

        sqlSession().update(NS + ".markCorrected", params);
    }

    public List<Subsidy> getSubsidies(Long requestFileId) {
        return sqlSession().selectList(NS + ".selectSubsidies", requestFileId);
    }

    public Subsidy getSubsidy(Long id){
        return sqlSession().selectOne(NS + ".selectSubsidy", id);
    }
}
