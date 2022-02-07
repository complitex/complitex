package ru.complitex.sync.service;

import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.common.service.AbstractBean;
import ru.complitex.sync.entity.DomainSync;
import ru.complitex.sync.entity.DomainSyncFilter;
import ru.complitex.sync.entity.SyncEntity;

import javax.ejb.Stateless;
import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 28.04.2014 23:45
 */
@Stateless
public class DomainSyncBean extends AbstractBean {
    public final static String NS = DomainSyncBean.class.getName();

    public void insert(DomainSync domainSync){
        sqlSession().insert("insertDomainSync", domainSync);
    }

    public void updateStatus(DomainSync domainSync){
        sqlSession().update("updateDomainSyncStatus", domainSync);
    }

    public DomainSync getObject(Long id){
        return sqlSession().selectOne(NS + ".selectDomainSync", id);
    }

    public List<DomainSync> getList(FilterWrapper<DomainSync> filterWrapper){
        return sqlSession().selectList(NS + ".selectDomainSyncList", filterWrapper);
    }

    public Long getCount(FilterWrapper<DomainSync> filterWrapper){
        return sqlSession().selectOne(NS + ".selectDomainSyncCount", filterWrapper);
    }

    public boolean isExist(DomainSync domainSync){
        return getCount(FilterWrapper.of(domainSync)) > 0;
    }

    public void delete(Long id){
        sqlSession().delete(NS + ".deleteDomainSync", id);
    }

    public void delete(SyncEntity syncEntity){
        sqlSession().delete(NS + ".deleteDomainSyncBySyncEntity", syncEntity);
    }

    public List<DomainSyncFilter> getDomainSyncFilters(SyncEntity syncEntity){
        return sqlSession().selectList("selectDomainSyncFilters", syncEntity);
    }
}
