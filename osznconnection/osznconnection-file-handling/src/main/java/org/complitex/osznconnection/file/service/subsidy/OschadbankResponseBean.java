package org.complitex.osznconnection.file.service.subsidy;

import org.complitex.common.entity.FilterWrapper;
import org.complitex.common.service.AbstractBean;
import org.complitex.osznconnection.file.entity.subsidy.OschadbankResponse;

import javax.ejb.Stateless;
import java.util.List;

/**
 * @author Anatoly A. Ivanov
 * 25.04.2019 23:06
 */
@Stateless
public class OschadbankResponseBean extends AbstractBean {
    public static final String NS = OschadbankResponseBean.class.getName();

    public List<OschadbankResponse> getOschadbankResponses(FilterWrapper<OschadbankResponse> filterWrapper){
        return sqlSession().selectList(NS + ".selectOschadbankResponses", filterWrapper);
    }

    public Long getOschadbankResponsesCount(FilterWrapper<OschadbankResponse> filterWrapper){
        return sqlSession().selectOne(NS + ".selectOschadbankResponsesCount", filterWrapper);
    }

    public void save(OschadbankResponse oschadbankResponse){
        if (oschadbankResponse.getId() == null){
            sqlSession().insert(NS + ".insertOschadbankResponse", oschadbankResponse);
        }else{
            sqlSession().update(NS + ".updateOschadbankResponse", oschadbankResponse);
        }
    }

    public void delete(Long requestFileId){
        sqlSession().delete(NS + ".deleteOschadbankResponses", requestFileId);
    }

    public boolean isOschadbankResponseFileFilled(Long requestFileId){
        return sqlSession().selectOne(NS + ".selectOschadbankResponseFileFilled", requestFileId);
    }
}
