package ru.complitex.keconnection.heatmeter.service;

import ru.complitex.common.mybatis.XmlMapper;
import ru.complitex.keconnection.heatmeter.entity.HeatmeterPayload;
import ru.complitex.keconnection.heatmeter.entity.HeatmeterPeriodType;

import javax.ejb.Stateless;
import java.util.Date;
import java.util.List;

import static com.google.common.collect.ImmutableMap.of;
import static ru.complitex.keconnection.heatmeter.entity.HeatmeterPeriodType.PAYLOAD;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 24.09.12 18:36
 */
@XmlMapper
@Stateless
public class HeatmeterPayloadBean extends HeatmeterPeriodBean<HeatmeterPayload> {

    @Override
    public HeatmeterPeriodType getType() {
        return PAYLOAD;
    }


    @Override
    public void insertAdditionalInfo(HeatmeterPayload payload, Date om) {
        sqlSession().insert("insertHeatmeterPayload", payload);
    }


    @Override
    public void updateAdditionalInfo(HeatmeterPayload payload, Date om) {
        sqlSession().update("updateHeatmeterPayload", payload);
    }

    public boolean isExist(Long heatmeterId) {
        return sqlSession().selectOne("isExistHeatmeterPayload", heatmeterId);
    }

    public void deleteByTablegramId(Long tablegramId) {
        sqlSession().delete("deletePayloadByTablegramId", tablegramId);
    }

    public List<HeatmeterPayload> getList(Long heatmeterId, Date om) {
        return sqlSession().selectList("selectHeatmeterPayloadsByOm", of("heatmeterId", heatmeterId, "om", om));
    }
}
