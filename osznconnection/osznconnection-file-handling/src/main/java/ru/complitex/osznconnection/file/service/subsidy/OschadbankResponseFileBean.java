package ru.complitex.osznconnection.file.service.subsidy;

import ru.complitex.common.service.AbstractBean;
import ru.complitex.osznconnection.file.entity.subsidy.OschadbankResponseFile;

import javax.ejb.Stateless;

/**
 * @author Anatoly A. Ivanov
 * 25.04.2019 23:05
 */
@Stateless
public class OschadbankResponseFileBean extends AbstractBean {
    public static final String NS = OschadbankResponseFileBean.class.getName();

    public OschadbankResponseFile getOschadbankResponseFile(Long requestFileId){
        return sqlSession().selectOne(NS + ".selectOschadbankResponseFile", requestFileId);
    }

    public void save(OschadbankResponseFile oschadbankResponseFile){
        if (oschadbankResponseFile.getId() == null){
            sqlSession().insert(NS + ".insertOschadbankResponseFile", oschadbankResponseFile);
        }
    }

    public void delete(Long requestFileId){
        sqlSession().delete(NS + ".deleteOschadbankResponseFile", requestFileId);
    }
}
