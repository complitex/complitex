package ru.complitex.keconnection.heatmeter.service;

import ru.complitex.common.mybatis.XmlMapper;
import ru.complitex.common.service.AbstractBean;
import ru.complitex.keconnection.heatmeter.entity.HeatmeterPeriod;
import ru.complitex.keconnection.heatmeter.entity.HeatmeterPeriodType;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static ru.complitex.common.util.DateUtil.isSameMonth;
import static ru.complitex.common.util.DateUtil.previousMonth;
import static ru.complitex.common.util.IdListUtil.getDiff;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 19.09.12 15:12
 */
@XmlMapper
public abstract class HeatmeterPeriodBean<T extends HeatmeterPeriod> extends AbstractBean {
    public abstract HeatmeterPeriodType getType();

    public abstract List<T> getList(Long heatmeterId, Date om);


    public void save(T object, Date om) {
        if (object.getId() == null) {
            sqlSession().insert("insertHeatmeterPeriod", object);
            insertAdditionalInfo(object, om);
        } else {
            sqlSession().update("updateHeatmeterPeriod", object);
            updateAdditionalInfo(object, om);
        }
    }


    public void insertAdditionalInfo(T o, Date om) {
    }


    public void updateAdditionalInfo(T o, Date om) {
    }


    public void delete(Long id) {
        sqlSession().delete("deleteHeatmeterPeriod", id);
    }


    public void save(Long heatmeterId, Date om, List<T> list) {
        List<T> db = om != null ? getList(heatmeterId, om) : new ArrayList<T>();

        //remove or fix end om
        for (T o : getDiff(db, list)) {
            if (isSameMonth(om, o.getBeginOm())) {
                delete(o.getId());
            }else {
                o.setEndOm(previousMonth(om));
                list.add(o);
            }
        }

        //save
        for (T o : list) {
            o.setHeatmeterId(heatmeterId);

            //changed
            for (T d : db){
                if (d.getId().equals(o.getId()) && !isSameMonth(om, d.getBeginOm()) && !o.isSame(d)){
                    d.setEndOm(previousMonth(om));
                    save(d, om);

                    o.setId(null);
                    o.setBeginOm(om);
                }
            }

            save(o, om);
        }
    }
}
