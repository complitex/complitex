package org.complitex.osznconnection.file.service.privilege;

import org.complitex.common.entity.FilterWrapper;
import org.complitex.common.stream.StreamUtils;
import org.complitex.osznconnection.file.entity.privilege.FacilityForm2;
import org.complitex.osznconnection.file.service.AbstractRequestBean;

import javax.ejb.Stateless;
import java.util.List;

@Stateless
public class FacilityForm2Bean extends AbstractRequestBean {
    public static final String NS = FacilityForm2Bean.class.getName();

    public void save(List<FacilityForm2> facilityForm2List) {
        if (facilityForm2List.isEmpty()) {
            return;
        }

        //noinspection ResultOfMethodCallIgnored
        facilityForm2List.stream().collect(StreamUtils.batchCollector(1, l -> {
            if (l.size() > 0) {
                sqlSession().insert(NS + ".insertFacilityForm2List", l);
            }
        }));
    }

    public List<FacilityForm2> getFacilityForm2List(FilterWrapper<FacilityForm2> filterWrapper){
        return sqlSession().selectList(NS + ".selectFacilityForm2List", filterWrapper);
    }

    public Long getFacilityForm2ListCount(FilterWrapper<FacilityForm2> filterWrapper){
        return sqlSession().selectOne(NS + ".selectFacilityForm2ListCount", filterWrapper);
    }

    public void delete(Long requestFileId) {
        sqlSession().delete(NS + ".deleteFacilityForm2", requestFileId);
    }
}
