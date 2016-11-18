package org.complitex.osznconnection.file.service.privilege;

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

    public List<FacilityForm2> getFacilityForm2(Long requestFileId) {
        return sqlSession().selectList(NS + ".selectFacilityForm2List", requestFileId);
    }

    public void delete(Long requestFileId) {
        sqlSession().delete(NS + ".deleteFacilityForm2", requestFileId);
    }
}
