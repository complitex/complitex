package org.complitex.correction.service;

import org.complitex.common.service.AbstractBean;
import org.complitex.correction.entity.Correction;

/**
 * @author inheaven on 002 02.02.15 18:11
 */
public class CorrectionBean extends AbstractBean{
    public static final String CORRECTION_NS = CorrectionBean.class.getName();

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
}
