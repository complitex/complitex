package org.complitex.osznconnection.file.service.subsidy;

import org.complitex.common.service.AbstractBean;
import org.complitex.osznconnection.file.entity.subsidy.OschadbankRequestFile;

import javax.ejb.Stateless;

/**
 * @author Anatoly A. Ivanov
 * 27.03.2019 18:54
 */
@Stateless
public class OschadbankRequestFileBean extends AbstractBean {
    public static final String NS = OschadbankRequestFileBean.class.getName();

    public void save(OschadbankRequestFile oschadbankRequestFile){
        if (oschadbankRequestFile.getId() == null){
            sqlSession().insert(NS + ".insertOschadbankRequestFile", oschadbankRequestFile);
        }
    }

    public void delete(Long requestFileId){
        sqlSession().delete(NS + ".deleteOschadbankRequestFile", requestFileId);
    }
}
