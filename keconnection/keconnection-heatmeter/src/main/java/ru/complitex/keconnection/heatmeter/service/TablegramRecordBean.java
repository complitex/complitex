package ru.complitex.keconnection.heatmeter.service;

import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.common.mybatis.XmlMapper;
import ru.complitex.common.service.AbstractBean;
import ru.complitex.keconnection.heatmeter.entity.TablegramRecord;

import javax.ejb.Stateless;
import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 15.10.12 16:56
 */
@XmlMapper
@Stateless
public class TablegramRecordBean extends AbstractBean{
    public void save(TablegramRecord tablegramRecord){
        if (tablegramRecord.getId() == null){
            sqlSession().insert("insertTablegramRecord", tablegramRecord);
        }else {
            sqlSession().update("updateTablegramRecord", tablegramRecord);
        }
    }

    public List<TablegramRecord> getTablegramRecords(FilterWrapper<TablegramRecord> filterWrapper){
        return sqlSession().selectList("selectTablegramRecords", filterWrapper);
    }

    public Long getTablegramRecordsCount(FilterWrapper<TablegramRecord> filterWrapper){
        return sqlSession().selectOne("selectTablegramRecordsCount", filterWrapper);
    }

    public List<TablegramRecord> getTablegramRecords(Long tablegramId){
        return sqlSession().selectList("selectTablegramRecordIdByTablegramId", tablegramId);
    }

    public void rollback(Long tablegramId){
        sqlSession().update("rollbackTablegramStatus", tablegramId);
    }

}
