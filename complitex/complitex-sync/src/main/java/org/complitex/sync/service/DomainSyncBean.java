package org.complitex.sync.service;

import org.complitex.common.entity.FilterWrapper;
import org.complitex.common.service.AbstractBean;
import org.complitex.sync.entity.DomainSync;
import org.complitex.sync.entity.DomainSyncFilter;
import org.complitex.sync.entity.SyncEntity;

import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 28.04.2014 23:45
 */
@Stateless
public class DomainSyncBean extends AbstractBean {
    public final static String NS = DomainSyncBean.class.getName();

    public void save(DomainSync domainSync){
        sqlSession().insert("insertDomainSync", domainSync);
    }

    @Asynchronous
    public void saveAsync(DomainSync domainSync){
        save(domainSync);
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
