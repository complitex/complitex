package ru.complitex.keconnection.heatmeter.service;

import ru.complitex.common.mybatis.XmlMapper;
import ru.complitex.common.service.AbstractBean;

import javax.ejb.Stateless;
import java.util.List;

/**
 *
 * @author Artem
 */
@XmlMapper
@Stateless
public class HeatmeterBindBean extends AbstractBean {

    private static final String MAPPING_NAMESPACE = HeatmeterBindBean.class.getName();


    public void fillHeatmeterBind() {
        delete();
        sqlSession().insert(MAPPING_NAMESPACE + ".fill");
    }


    public void delete() {
        sqlSession().delete(MAPPING_NAMESPACE + ".delete");
    }

    public List<Long> getBatch(int count) {
        return sqlSession().selectList(MAPPING_NAMESPACE + ".getBatch", count);
    }


    public void markProcessed(List<Long> batch) {
        if (batch != null && !batch.isEmpty()) {
            sqlSession().update(MAPPING_NAMESPACE + ".markProcessed", batch);
        }
    }
}
