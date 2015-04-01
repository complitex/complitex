package org.complitex.keconnection.heatmeter.service.consumption;

import org.complitex.common.entity.FilterWrapper;
import org.complitex.common.service.AbstractBean;
import org.complitex.keconnection.heatmeter.entity.consumption.ConsumptionFile;

import javax.ejb.Stateless;
import java.util.List;

/**
 * @author inheaven on 016 16.03.15 19:25
 */
@Stateless
public class ConsumptionFileBean extends AbstractBean{
    public static final String NS = ConsumptionFileBean.class.getName();

    public List<ConsumptionFile> getConsumptionFiles(FilterWrapper<ConsumptionFile> filterWrapper){
        return sqlSession().selectList(NS + ".selectConsumptionFiles", filterWrapper);
    }

    public Long getConsumptionFilesCount(FilterWrapper<ConsumptionFile> filterWrapper){
        return sqlSession().selectOne(NS + ".selectConsumptionFilesCount", filterWrapper);
    }

    public void save(ConsumptionFile consumptionFile){
        sqlSession().insert(NS + ".insertConsumptionFile", consumptionFile);
    }

    public void delete(Long consumptionFileId){
        sqlSession().delete(NS + ".deleteConsumptionFile", consumptionFileId);
    }
}
