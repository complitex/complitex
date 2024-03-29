package ru.complitex.keconnection.heatmeter.service;

import ru.complitex.common.mybatis.XmlMapper;
import ru.complitex.keconnection.heatmeter.entity.HeatmeterConsumption;
import ru.complitex.keconnection.heatmeter.entity.HeatmeterInput;
import ru.complitex.keconnection.heatmeter.entity.HeatmeterPeriodType;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.Date;
import java.util.List;

import static com.google.common.collect.ImmutableMap.of;
import static ru.complitex.keconnection.heatmeter.entity.HeatmeterPeriodType.INPUT;

/**
 *
 * @author Artem
 */
@XmlMapper
@Stateless
public class HeatmeterInputBean extends HeatmeterPeriodBean<HeatmeterInput> {

    @EJB
    private HeatmeterConsumptionBean heatmeterConsumptionBean;

    @Override
    public HeatmeterPeriodType getType() {
        return INPUT;
    }


    @Override
    public void insertAdditionalInfo(HeatmeterInput input, Date om) {
        sqlSession().insert("insertHeatmeterInput", input);

        updateConsumptions(input.getId(), om, input.getConsumptions());
    }


    @Override
    public void updateAdditionalInfo(HeatmeterInput input, Date om) {
        sqlSession().update("updateHeatmeterInput", input);

        updateConsumptions(input.getId(), om, input.getConsumptions());
    }

    private void updateConsumptions(Long heatmeterInputId, Date om, List<HeatmeterConsumption> consumptions) {
        List<HeatmeterConsumption> db = heatmeterConsumptionBean.getList(heatmeterInputId, om);

        for (HeatmeterConsumption d : db){
            boolean remove = true;

            for (HeatmeterConsumption c : consumptions){
                if (c.isPeriodEquals(d)){
                    c.setId(d.getId());

                    remove = false;
                }
            }

            if (remove){
                heatmeterConsumptionBean.delete(d.getId());
            }
        }

        for (HeatmeterConsumption consumption : consumptions) {
            consumption.setHeatmeterInputId(heatmeterInputId);

            heatmeterConsumptionBean.save(consumption);
        }
    }

    public List<HeatmeterInput> getList(Long heatmeterId, Date om) {
        return sqlSession().selectList("selectHeatmeterInputsByOm", of("heatmeterId", heatmeterId, "om", om));
    }
}
