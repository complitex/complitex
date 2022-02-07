package ru.complitex.keconnection.heatmeter.service.consumption;

import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.common.service.AbstractBean;
import ru.complitex.keconnection.heatmeter.entity.ConsumptionStatusFilter;
import ru.complitex.keconnection.heatmeter.entity.consumption.CentralHeatingConsumption;

import javax.ejb.Stateless;
import java.util.List;

/**
 * @author inheaven on 016 16.03.15 20:18
 */
@Stateless
public class CentralHeatingConsumptionBean extends AbstractBean{
    public List<CentralHeatingConsumption> getCentralHeatingConsumptions(FilterWrapper<CentralHeatingConsumption> filterWrapper){
        return selectList("selectCentralHeatingConsumptions", filterWrapper);
    }

    public Long getCentralHeatingConsumptionsCount(FilterWrapper<CentralHeatingConsumption> filterWrapper){
        return selectOne("selectCentralHeatingConsumptionsCount", filterWrapper);
    }

    public void save(CentralHeatingConsumption centralHeatingConsumption){
        if (centralHeatingConsumption.getId() == null) {
            insert("insertCentralHeatingConsumption", centralHeatingConsumption);
        }else {
            update("updateCentralHeatingConsumption", centralHeatingConsumption);
        }
    }

    public List<ConsumptionStatusFilter> getStatusFilters(Long consumptionFileId){
        return selectList("selectStatusFilters", consumptionFileId);
    }
}
