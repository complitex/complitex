package ru.flexpay.eirc.service_provider_account.service;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.complitex.common.entity.FilterWrapper;
import org.complitex.common.service.AbstractBean;
import org.complitex.common.util.StringUtil;
import ru.flexpay.eirc.service_provider_account.entity.FinancialAttribute;

import java.util.List;

/**
 * @author Pavel Sknar
 */
public abstract class FinancialAttributeBean<T extends FinancialAttribute> extends AbstractBean {

    private static final String NS = ru.flexpay.eirc.service_provider_account.service.FinancialAttributeBean.class.getName();

    public void delete(T financialAttribute) {
        sqlSession().delete("delete", financialAttribute);
    }

    public T getFinancialAttribute(long id) {
        List<T> resultOrderByDescData = sqlSession().selectList(getNameSpace() + ".selectFinancialAttribute", id);
        return resultOrderByDescData.size() > 0? resultOrderByDescData.get(0): null;
    }

    public List<T> getFinancialAttributes(FilterWrapper<T> filter, boolean last) {
        if (filter.getSortProperty() != null && StringUtil.equal(filter.getSortProperty(), "id")) {
            filter.setSortProperty("fa_id");
        }
        FinancialAttributeUtil.addFilterMappingObject(filter);
        return last ?
                sqlSession().<T>selectList(getNameSpace() + ".selectLastDateFinancialAttributes", filter) :
                sqlSession().<T>selectList(getNameSpace() + ".selectAllPeriodDateFinancialAttributes", filter);
    }

    public int count(FilterWrapper<T> filter, boolean last) {
        ServiceProviderAccountUtil.addFilterMappingObject(filter, filter.getObject().getServiceProviderAccount());
        return last ?
                sqlSession().<Integer>selectOne(getNameSpace() + ".countLastDateFinancialAttributes", filter) :
                sqlSession().<Integer>selectOne(getNameSpace() + ".countAllPeriodDateFinancialAttributes", filter);
    }

    public void save(T financialAttribute) {
        if (financialAttribute.getId() == null) {
            insert(financialAttribute);
        } else {
            update(financialAttribute);
        }
    }

    public T getFinancialAttributeByRRContainerId(long registryRecordContainerId) {
        FilterWrapper<T> filter = FilterWrapper.of(getInstance());
        filter.getObject().setRegistryRecordContainerId(registryRecordContainerId);
        FinancialAttributeUtil.addFilterMappingObject(filter);
        List<T> resultOrderByDescData = sqlSession().selectList(getNameSpace() + ".selectFinancialAttributeByRRContainerId", filter);
        return resultOrderByDescData.size() > 0? resultOrderByDescData.get(0): null;
    }

    public boolean financialAttributeExists(long registryRecordContainerId) {
        return sqlSession().selectOne(getNameSpace() + ".financialAttributeExists", registryRecordContainerId) != null;
    }

    public void deleteByRRContainerId(long registryRecordContainerId) {
        sqlSession().delete(getNameSpace() + ".deleteFinancialAttributeByRRContainerId", registryRecordContainerId);
    }

    private void insert(T financialAttribute) {
        sqlSession().insert(getNameSpace() + ".insertFinancialAttribute", financialAttribute);
    }

    private void update(T financialAttribute) {
        // update financialAttribute
        T oldObject = getFinancialAttribute(financialAttribute.getId());
        if (EqualsBuilder.reflectionEquals(oldObject, financialAttribute)) {
            return;
        }
        sqlSession().update(getNameSpace() + ".updateFinancialAttribute", financialAttribute);
    }

    public abstract T getInstance();

    protected String getNameSpace() {
        return NS;
    }
}
                                             