package ru.complitex.osznconnection.file.service.subsidy;

import ru.complitex.common.service.AbstractBean;
import ru.complitex.osznconnection.file.entity.subsidy.OschadbankRequestFile;

import javax.ejb.Stateless;

/**
 * @author Anatoly A. Ivanov
 * 27.03.2019 18:54
 */
@Stateless
public class OschadbankRequestFileBean extends AbstractBean {
    public static final String NS = OschadbankRequestFileBean.class.getName();

    public OschadbankRequestFile getOschadbankRequestFile(Long requestFileId){
        return sqlSession().selectOne(NS + ".selectOschadbankRequestFile", requestFileId);
    }

    public void save(OschadbankRequestFile oschadbankRequestFile){
        if (oschadbankRequestFile.getId() == null){
            sqlSession().insert(NS + ".insertOschadbankRequestFile", oschadbankRequestFile);
        }
    }

    public void delete(Long requestFileId){
        sqlSession().delete(NS + ".deleteOschadbankRequestFile", requestFileId);
    }
}
