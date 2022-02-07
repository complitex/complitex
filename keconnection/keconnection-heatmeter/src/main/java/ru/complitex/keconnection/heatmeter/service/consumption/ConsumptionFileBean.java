package ru.complitex.keconnection.heatmeter.service.consumption;

import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.common.service.AbstractBean;
import ru.complitex.keconnection.heatmeter.entity.consumption.ConsumptionFile;

import javax.ejb.Stateless;
import java.util.List;

/**
 * @author inheaven on 016 16.03.15 19:25
 */
@Stateless
public class ConsumptionFileBean extends AbstractBean{
    public ConsumptionFile getConsumptionFile(Long id){
        return selectOne("selectConsumptionFile", id);
    }

//    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public List<ConsumptionFile> getConsumptionFiles(FilterWrapper<ConsumptionFile> filterWrapper){
        return selectList("selectConsumptionFiles", filterWrapper);
    }

//    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Long getConsumptionFilesCount(FilterWrapper<ConsumptionFile> filterWrapper){
        return selectOne("selectConsumptionFilesCount", filterWrapper);
    }

//    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void save(ConsumptionFile consumptionFile){
        if (consumptionFile.getId() == null) {
            insert("insertConsumptionFile", consumptionFile);
        }else{
            update("updateConsumptionFile", consumptionFile);
        }
    }

    public void delete(Long consumptionFileId){
        delete("deleteConsumptionFile", consumptionFileId);
    }
}
