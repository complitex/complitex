package ru.complitex.osznconnection.file.service.subsidy;

import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.common.service.AbstractBean;
import ru.complitex.osznconnection.file.entity.subsidy.OschadbankRequest;

import javax.ejb.Stateless;
import java.util.List;

/**
 * @author Anatoly A. Ivanov
 * 26.03.2019 15:42
 */
@Stateless
public class OschadbankRequestBean extends AbstractBean {
    public static final String NS = OschadbankRequestBean.class.getName();

    public List<OschadbankRequest> getOschadbankRequests(FilterWrapper<OschadbankRequest> filterWrapper){
        return sqlSession().selectList(NS + ".selectOschadbankRequests", filterWrapper);
    }

    public Long getOschadbankRequestsCount(FilterWrapper<OschadbankRequest> filterWrapper){
        return sqlSession().selectOne(NS + ".selectOschadbankRequestsCount", filterWrapper);
    }

    public void save(OschadbankRequest oschadbankRequest){
        if (oschadbankRequest.getId() == null){
            sqlSession().insert(NS + ".insertOschadbankRequest", oschadbankRequest);
        }else{
            sqlSession().update(NS + ".updateOschadbankRequest", oschadbankRequest);
        }
    }

    public void delete(Long requestFileId){
        sqlSession().delete(NS + ".deleteOschadbankRequests", requestFileId);
    }

    public boolean isOschadbankRequestFileFilled(Long requestFileId){
        return sqlSession().selectOne(NS + ".selectOschadbankRequestFileFilled", requestFileId);
    }
}
