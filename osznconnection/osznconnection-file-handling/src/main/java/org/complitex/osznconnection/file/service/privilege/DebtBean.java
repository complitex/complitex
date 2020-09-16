package org.complitex.osznconnection.file.service.privilege;

import org.complitex.common.entity.FilterWrapper;
import org.complitex.common.stream.StreamUtils;
import org.complitex.osznconnection.file.entity.privilege.Debt;
import org.complitex.osznconnection.file.service.AbstractRequestBean;

import javax.ejb.Stateless;
import java.util.List;

/**
 * @author Anatoly Ivanov
 * 16.09.2020 20:44
 */
@Stateless
public class DebtBean extends AbstractRequestBean {
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

    public List<Debt> getDebts(FilterWrapper<Debt> filterWrapper){
        return sqlSession().selectList(NS + ".selectDebtList", filterWrapper);
    }

    public Long getDebtsCount(FilterWrapper<Debt> filterWrapper){
        return sqlSession().selectOne(NS + ".selectDebtListCount", filterWrapper);
    }

    public void delete(Long requestFileId) {
        sqlSession().delete(NS + ".deleteDebt", requestFileId);
    }
}
