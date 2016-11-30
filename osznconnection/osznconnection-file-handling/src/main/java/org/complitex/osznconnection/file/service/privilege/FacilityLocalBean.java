package org.complitex.osznconnection.file.service.privilege;

import org.complitex.common.entity.FilterWrapper;
import org.complitex.common.stream.StreamUtils;
import org.complitex.osznconnection.file.entity.privilege.FacilityLocal;
import org.complitex.osznconnection.file.service.AbstractRequestBean;

import javax.ejb.Stateless;
import java.util.List;

/**
 * @author inheaven on 017 17.11.16.
 */
@Stateless
public class FacilityLocalBean extends AbstractRequestBean{
    public static final String NS = FacilityLocalBean.class.getName();

    public void save(List<FacilityLocal> facilityLocalList) {
        if (facilityLocalList.isEmpty()) {
            return;
        }

        //noinspection ResultOfMethodCallIgnored
        facilityLocalList.stream().collect(StreamUtils.batchCollector(1000, l -> {
            sqlSession().insert(NS + ".insertFacilityLocalList", l);
        }));
    }

    public List<FacilityLocal> getFacilityLocal(Long requestFileId) {
        return sqlSession().selectList(NS + ".selectFacilityLocal", requestFileId);
    }

    public List<FacilityLocal> getFacilityLocals(FilterWrapper<FacilityLocal> filterWrapper){
        return sqlSession().selectList(NS + ".selectFacilityLocalList", filterWrapper);
    }

    public Long getFacilityLocalsCount(FilterWrapper<FacilityLocal> filterWrapper){
        return sqlSession().selectOne(NS + ".selectFacilityLocalListCount", filterWrapper);
    }

    public void delete(Long requestFileId) {
        sqlSession().delete(NS + ".deleteFacilityLocal", requestFileId);
    }
}
