package org.complitex.correction.service;

import com.google.common.collect.ImmutableMap;
import org.complitex.common.entity.FilterWrapper;
import org.complitex.common.service.AbstractBean;
import org.complitex.correction.entity.Correction;

import javax.ejb.Stateless;
import java.util.List;

/**
 * @author inheaven on 002 02.02.15 18:11
 */
@Stateless
public class CorrectionBean extends AbstractBean{
    public static final String CORRECTION_NS = CorrectionBean.class.getName();

    //todo check duplicate
    public void save(Correction correction){
        if (correction.getId() == null){
            sqlSession().insert(CORRECTION_NS + ".insertCorrection", correction);
        }else {
            sqlSession().update(CORRECTION_NS + ".updateCorrection", correction);
        }
    }

    public void delete(Correction correction){
        sqlSession().delete(CORRECTION_NS + ".deleteCorrection", correction);
    }

    public Correction getCorrection(String entityName, Long id){
        return sqlSession().selectOne(CORRECTION_NS + ".selectCorrection", new Correction(entityName, id));
    }

    public List<Correction> getCorrections(FilterWrapper<Correction> filterWrapper){
        return sqlSession().selectList(CORRECTION_NS + ".selectCorrections", filterWrapper);
    }

    public Long getCorrectionsCount(FilterWrapper<Correction> filterWrapper){
        return sqlSession().selectOne(CORRECTION_NS + ".selectCorrectionsCount", filterWrapper);
    }

    public List<Long> getObjectIds(String entityName, String correction, Long entityAttributeId){
        return sqlSession().selectList(CORRECTION_NS + ".selectObjectIds", ImmutableMap.of("entityName", entityName,
                "correction", correction, "entityAttributeId", entityAttributeId));
    }

    public List<Correction> getCorrections(String entityName,  String correction, Long organizationId, Long userOrganizationId){
        return getCorrections(FilterWrapper.of(new Correction(entityName, null, null, null,
                correction, organizationId, userOrganizationId)));
    }

    public List<Correction> getCorrections(String entityName, Long parentId, Long additionalParentId, String correction,
                                           Long organizationId, Long userOrganizationId){
        return getCorrections(FilterWrapper.of(new Correction(entityName, parentId, additionalParentId, null, null,
                correction, null, organizationId, userOrganizationId)));
    }

    public List<Correction> getCorrections(String entityName, Long parentId, String correction, String additionalCorrection,
                                           Long organizationId, Long userOrganizationId){
        return getCorrections(FilterWrapper.of(new Correction(entityName, parentId, null, null, null,
                correction, additionalCorrection, organizationId, userOrganizationId)));
    }

    public List<Correction> getCorrections(String entityName, Long parentId, String correction,
                                           Long organizationId, Long userOrganizationId){
        return getCorrections(FilterWrapper.of(new Correction(entityName, parentId, null, null, null,
                correction, null, organizationId, userOrganizationId)));
    }


}
