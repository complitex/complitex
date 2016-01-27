package org.complitex.address.service;

import org.complitex.address.entity.AddressSync;
import org.complitex.address.entity.AddressSyncFilter;
import org.complitex.address.entity.SyncEntity;
import org.complitex.common.entity.FilterWrapper;
import org.complitex.common.service.AbstractBean;

import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 28.04.2014 23:45
 */
@Stateless
public class AddressSyncBean extends AbstractBean {
    public final static String NS = AddressSyncBean.class.getName();

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void save(AddressSync addressSync){
        if (addressSync.getId() == null){
            sqlSession().insert("insertAddressSync", addressSync);
        }
    }

    @Asynchronous
    public void saveAsync(AddressSync addressSync){
        save(addressSync);
    }

    public AddressSync getObject(Long id){
        return sqlSession().selectOne(NS + ".selectAddressSync", id);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public List<AddressSync> getList(FilterWrapper<AddressSync> filterWrapper){
        return sqlSession().selectList(NS + ".selectAddressSyncList", filterWrapper);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Long getCount(FilterWrapper<AddressSync> filterWrapper){
        return sqlSession().selectOne(NS + ".selectAddressSyncCount", filterWrapper);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public boolean isExist(AddressSync addressSync){
        return getCount(FilterWrapper.of(addressSync)) > 0;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void delete(Long id){
        sqlSession().delete(NS + ".deleteAddressSync", id);
    }

    public List<AddressSyncFilter> getAddressSyncFilters(SyncEntity syncEntity){
        return sqlSession().selectList("selectAddressSyncFilters", syncEntity);
    }
}
