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

    //todo locale
    private List<Long> getObjectIds(String entity, String correction, Long entityAttributeId){
        return sqlSession().selectList(CORRECTION_NS + ".selectObjectIds", ImmutableMap.of("entity", entity, "correction",
                correction, "entityAttributeId", entityAttributeId));
    }
}
