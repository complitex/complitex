package org.complitex.keconnection.heatmeter.service.consumption;

import org.complitex.common.entity.FilterWrapper;
import org.complitex.common.service.AbstractBean;
import org.complitex.keconnection.heatmeter.entity.consumption.CentralHeatingConsumption;

import javax.ejb.Stateless;
import java.util.List;

/**
 * @author inheaven on 016 16.03.15 20:18
 */
@Stateless
public class CentralHeatingConsumptionBean extends AbstractBean{
    public final static String NS = CentralHeatingConsumptionBean.class.getName();

    public List<CentralHeatingConsumption> getCentralHeatingConsumptions(FilterWrapper<CentralHeatingConsumption> filterWrapper){
        return sqlSession().selectList(NS + ".selectCentralHeatingConsumptions", filterWrapper);
    }

    public Long getCentralHeatingConsumptionsCount(FilterWrapper<CentralHeatingConsumption> filterWrapper){
        return sqlSession().selectOne(NS +".selectCentralHeatingConsumptionsCount", filterWrapper);
    }

    public void save(CentralHeatingConsumption centralHeatingConsumption){
        sqlSession().insert(NS + ".insertCentralHeatingConsumption", centralHeatingConsumption);
    }
}
