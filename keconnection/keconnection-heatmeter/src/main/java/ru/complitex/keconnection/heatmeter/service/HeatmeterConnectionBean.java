package ru.complitex.keconnection.heatmeter.service;

import ru.complitex.common.mybatis.XmlMapper;
import ru.complitex.keconnection.heatmeter.entity.HeatmeterConnection;
import ru.complitex.keconnection.heatmeter.entity.HeatmeterPeriodType;

import javax.ejb.Stateless;
import java.util.Date;
import java.util.List;

import static com.google.common.collect.ImmutableMap.of;
import static ru.complitex.keconnection.heatmeter.entity.HeatmeterPeriodType.CONNECTION;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 02.10.12 16:37
 */
@XmlMapper
@Stateless
public class HeatmeterConnectionBean extends HeatmeterPeriodBean<HeatmeterConnection> {
    @Override
    public HeatmeterPeriodType getType() {
        return CONNECTION;
    }

    public List<HeatmeterConnection> getList(Long heatmeterId, Date om){
        return sqlSession().selectList("selectHeatmeterConnectionsByOm", of("heatmeterId", heatmeterId, "om", om));
    }

    public List<HeatmeterConnection> getHeatmeterConnections(Long heatmeterId){
        return sqlSession().selectList("selectHeatmeterConnectionsByHeatmeterId", heatmeterId);
    }
}
