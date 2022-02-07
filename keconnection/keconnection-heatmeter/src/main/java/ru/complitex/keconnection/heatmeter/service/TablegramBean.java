package ru.complitex.keconnection.heatmeter.service;

import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.common.mybatis.XmlMapper;
import ru.complitex.common.service.AbstractBean;
import ru.complitex.keconnection.heatmeter.entity.Tablegram;

import javax.ejb.Stateless;
import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 08.10.12 16:34
 */
@XmlMapper
@Stateless
public class TablegramBean extends AbstractBean{
    public void save(Tablegram tablegram){
        if (tablegram.getId() == null){
            sqlSession().insert("insertTablegram", tablegram);
        }else {
            sqlSession().update("updateTablegram", tablegram);
        }
    }

    public Tablegram getTablegram(Long id){
        return sqlSession().selectOne("selectTablegram", id);
    }

    public List<Tablegram> getTablegrams(FilterWrapper<Tablegram> filterWrapper){
        return sqlSession().selectList("selectTablegrams", filterWrapper);
    }

    public Long getTablegramsCount(FilterWrapper<Tablegram> filterWrapper){
        return sqlSession().selectOne("selectTablegramsCount", filterWrapper);
    }

    public boolean isExist(Tablegram tablegram){
        return sqlSession().selectOne("isExistTablegram", tablegram);
    }

    public void delete(Long id){
        sqlSession().delete("deleteTablegram", id);
    }
}
