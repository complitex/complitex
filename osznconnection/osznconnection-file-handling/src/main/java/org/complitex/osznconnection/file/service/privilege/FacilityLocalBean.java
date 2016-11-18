package org.complitex.osznconnection.file.service.privilege;

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

    public List<FacilityLocal> getFacilityLocal(Long requestFileId) {
        return sqlSession().selectList(NS + ".selectFacilityLocal", requestFileId);
    }

    public void insert(List<FacilityLocal> facilityLocals) {
        if (facilityLocals.isEmpty()) {
            return;
        }

        sqlSession().insert(NS + ".insertFacilityLocalList", facilityLocals);
    }

    public void delete(Long requestFileId) {
        sqlSession().delete(NS + ".deleteFacilityLocal", requestFileId);
    }




}
