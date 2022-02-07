package ru.complitex.osznconnection.file.service.privilege;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import ru.complitex.address.entity.AddressEntity;
import ru.complitex.common.stream.StreamUtils;
import ru.complitex.osznconnection.file.entity.RequestStatus;
import ru.complitex.osznconnection.file.entity.example.PrivilegeExample;
import ru.complitex.osznconnection.file.entity.privilege.Debt;

import javax.ejb.Stateless;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Anatoly Ivanov
 * 16.09.2020 20:44
 */
@Stateless
public class DebtBean extends AbstractPrivilegeBean {
    public static final String NS = DebtBean.class.getName();

    public void save(List<Debt> facilityLocalList) {
        if (facilityLocalList.isEmpty()) {
            return;
        }

        //noinspection ResultOfMethodCallIgnored
        facilityLocalList.stream().collect(StreamUtils.batchCollector(1, l -> {
            if (l.size() > 0) {
                sqlSession().insert(NS + ".insertDebtList", l);
            }
        }));
    }

    public List<Debt> getDebts(Long requestFileId) {
        return sqlSession().selectList(NS + ".selectDebt", requestFileId);
    }

    public List<Debt> getDebts(PrivilegeExample privilegeExample){
        List<Debt> list = sqlSession().selectList(NS + ".selectDebts", privilegeExample);

        loadFacilityStreet(list);

        return list;
    }

    public Long getDebtsCount(PrivilegeExample privilegeExample){
        return sqlSession().selectOne(NS + ".selectDebtsCount", privilegeExample);
    }

    public void delete(Long requestFileId) {
        sqlSession().delete(NS + ".deleteDebt", requestFileId);
    }

    public void update(Debt debt){
        sqlSession().update(NS + ".updateDebt", debt);
    }

    public List<Long> getDebtIds(Long requestFileId){
        return sqlSession().selectList(NS + ".selectDebtIds", requestFileId);
    }

    public List<Debt> getDebtForOperation(Long requestFileId, List<Long> ids){
        List<Debt> list = sqlSession().selectList(NS + ".selectDebtForOperation",
                ImmutableMap.of("requestFileId", requestFileId, "ids", ids));

        loadFacilityStreet(list);

        return list;
    }

    public void clearDebtBound(Long requestFileId){
        sqlSession().update(NS + ".clearDebtBound",
                ImmutableMap.of("fileId", requestFileId, "status", RequestStatus.LOADED));
    }

    public void clearDebtBound(Debt debt){
        sqlSession().update(NS + ".clearDebtBound",
                ImmutableMap.of("fileId", debt.getRequestFileId(), "id", debt.getId(),
                        "status", RequestStatus.LOADED));
    }

    public void markDebtCorrected(Debt debt, AddressEntity addressEntity){
        markPrivilegeCorrected(NS + ".markDebtCorrected", debt, addressEntity);
    }

    private Integer countByFile(Long fileId, Set<RequestStatus> statuses) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("requestFileId", fileId);
        params.put("statuses", statuses);

        return sqlSession().selectOne(NS + ".countByFile", params);
    }

    public boolean isDebtBound(Long fileId) {
        return countByFile(fileId, RequestStatus.unboundStatuses()) == 0;
    }

    public boolean isDebtFileFilled(Long requestFileId){
        return countByFile(requestFileId, RequestStatus.UNPROCESSED_SET_STATUSES) == 0;
    }

    public void updateDebtAccountNumber(Debt debt){
        sqlSession().update(NS + ".updateDebtAccountNumber", debt);
    }

}
