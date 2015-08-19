package org.complitex.address.service;

import org.complitex.address.entity.AddressEntity;
import org.complitex.address.entity.AddressSync;
import org.complitex.address.entity.AddressSyncFilter;
import org.complitex.common.entity.FilterWrapper;
import org.complitex.common.service.AbstractBean;

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

    public AddressSync getObject(Long id){
        return sqlSession().selectOne(NS + ".selectAddressSync", id);
    }

    public List<AddressSync> getList(FilterWrapper<AddressSync> filterWrapper){
        return sqlSession().selectList(NS + ".selectAddressSyncList", filterWrapper);
    }

    public Long getCount(FilterWrapper<AddressSync> filterWrapper){
        return sqlSession().selectOne(NS + ".selectAddressSyncCount", filterWrapper);
    }

    public boolean isExist(AddressSync addressSync){
        return getCount(FilterWrapper.of(addressSync)) == 0;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void delete(Long id){
        sqlSession().delete(NS + ".deleteAddressSync", id);
    }

    public List<AddressSyncFilter> getAddressSyncFilters(AddressEntity addressEntity){
        return sqlSession().selectList("selectAddressSyncFilters", addressEntity);
    }
}
