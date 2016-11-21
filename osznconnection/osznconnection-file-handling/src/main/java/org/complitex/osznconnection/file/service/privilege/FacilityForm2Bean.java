package org.complitex.osznconnection.file.service.privilege;

import org.complitex.common.entity.FilterWrapper;
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

        sqlSession().insert(NS + ".insertFacilityForm2List", facilityForm2List);
    }

    public List<FacilityForm2> getFacilityForm2List(Long requestFileId) {
        return sqlSession().selectList(NS + ".selectFacilityForm2List", requestFileId); //todo filter wrapper
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
