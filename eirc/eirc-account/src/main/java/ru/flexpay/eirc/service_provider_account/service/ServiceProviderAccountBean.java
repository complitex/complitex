package ru.flexpay.eirc.service_provider_account.service;

import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import ru.complitex.common.entity.Attribute;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.common.mybatis.SqlSessionFactoryBean;
import ru.complitex.common.service.AbstractBean;
import ru.complitex.common.strategy.SequenceBean;
import ru.complitex.common.strategy.StringLocaleBean;
import ru.complitex.common.util.DateUtil;
import ru.flexpay.eirc.eirc_account.service.EircAccountBean;
import ru.flexpay.eirc.organization.entity.EircOrganization;
import ru.flexpay.eirc.organization.strategy.EircOrganizationStrategy;
import ru.flexpay.eirc.service_provider_account.entity.ServiceNotAllowableException;
import ru.flexpay.eirc.service_provider_account.entity.ServiceProviderAccount;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Pavel Sknar
 */
@Stateless
public class ServiceProviderAccountBean extends AbstractBean {

    private static final String NS = ServiceProviderAccountBean.class.getPackage().getName() + ".ServiceProviderAccountBean";
    public static final String ENTITY = "service_provider_account";

    public static final String FILTER_MAPPING_ATTRIBUTE_NAME = "serviceProviderAccount";

    @EJB
    private SequenceBean sequenceBean;

    @EJB
    private StringLocaleBean stringLocaleBean;

    @EJB
    private EircOrganizationStrategy eircOrganizationStrategy;

    @EJB
    private EircAccountBean eircAccountBean;

    public void archive(ServiceProviderAccount object) {
        if (object.getEndDate() == null) {
            object.setEndDate(DateUtil.getCurrentDate());
        }
        sqlSession().update(NS + ".updateServiceProviderAccountEndDate", object);
    }

    public void restore(ServiceProviderAccount object) {
        if (object.getBeginDate().equals(object.getEndDate())) {
            delete(object);
            object = getServiceProviderAccount(object.getId());
        } else {
            object.setRegistryRecordContainerId(null);
        }
        if (object.getEndDate() != null) {
            object.setEndDate(null);
        }
        sqlSession().update(NS + ".updateServiceProviderAccountEndDate", object);
        if (object.getEircAccount().getEndDate() != null) {
            eircAccountBean.restore(object.getEircAccount());
        }
    }

    public ServiceProviderAccount getServiceProviderAccount(long id) {
        List<ServiceProviderAccount> resultOrderByDescData = sqlSession().selectList(NS + ".selectServiceProviderAccount", id);
        return resultOrderByDescData.size() > 0? resultOrderByDescData.get(0): null;
    }

    public List<ServiceProviderAccount> getServiceProviderAccounts(FilterWrapper<ServiceProviderAccount> filter) {
        ServiceProviderAccountUtil.addFilterMappingObject(filter);
        if (filter != null && StringUtils.equals(filter.getSortProperty(), "id")) {
            filter.setSortProperty("spa_object_id");
        }
        return sqlSession().selectList(NS + ".selectServiceProviderAccounts", filter);
    }

    public boolean serviceProviderAccountsExists(Long eircAccountId) {
        return sqlSession().selectOne(NS + ".serviceProviderAccountsExists", eircAccountId) != null;
    }

    public Long getCount(FilterWrapper<ServiceProviderAccount> filter) {
        ServiceProviderAccountUtil.addFilterMappingObject(filter);
        return sqlSession().selectOne(NS + ".countServiceProviderAccounts", filter);
    }

    public void save(ServiceProviderAccount serviceProviderAccount) throws ServiceNotAllowableException {
        validate(serviceProviderAccount);

        if (serviceProviderAccount.getId() == null) {
            saveNew(serviceProviderAccount);
        } else {
            update(serviceProviderAccount);
        }
    }

    public void validate(ServiceProviderAccount serviceProviderAccount) throws ServiceNotAllowableException {
        if (serviceProviderAccount.getService() != null && serviceProviderAccount.getService().getId() != null) {
            // check allowable services
            EircOrganization organization = eircOrganizationStrategy.getDomainObject(serviceProviderAccount.getOrganizationId(), true);
            List<Attribute> allowableServices = organization.getAttributes(EircOrganizationStrategy.SERVICE);
            boolean check = false;
            for (Attribute allowableService : allowableServices) {
                 if (serviceProviderAccount.getService().getId().equals(allowableService.getValueId())) {
                     check = true;
                     break;
                }
            }
            if (!check) {
                throw new ServiceNotAllowableException("Service {0} do not allowable for organization {1}",
                        serviceProviderAccount.getService(), serviceProviderAccount.getId());
            }
        }
    }

    private void saveNew(ServiceProviderAccount serviceProviderAccount) {
        serviceProviderAccount.setId(sequenceBean.nextId(ENTITY));
        sqlSession().insert(NS + ".insertServiceProviderAccount", serviceProviderAccount);
    }

    private void update(ServiceProviderAccount serviceProviderAccount) {
        ServiceProviderAccount oldObject = getServiceProviderAccountByPkId(serviceProviderAccount.getPkId());
        if (EqualsBuilder.reflectionEquals(oldObject, serviceProviderAccount)) {
            return;
        }
        archive(oldObject);
        serviceProviderAccount.setBeginDate(oldObject.getEndDate());
        sqlSession().insert(NS + ".insertServiceProviderAccount", serviceProviderAccount);
    }

    public ServiceProviderAccount getServiceProviderAccountByPkId(long pkId) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("pkId", pkId);
        params.put("locale", stringLocaleBean.convert(stringLocaleBean.getSystemLocale()));
        List<ServiceProviderAccount> serviceProviderAccounts =  sqlSession().selectList(NS + ".selectServiceProviderAccountByPkId", params);
        return serviceProviderAccounts.size() > 0? serviceProviderAccounts.get(0) : null;
    }

    public ServiceProviderAccount getServiceProviderAccountByRRContainerId(long registryRecordContainerId) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("registryRecordContainerId", registryRecordContainerId);
        params.put("locale", stringLocaleBean.convert(stringLocaleBean.getSystemLocale()));
        List<ServiceProviderAccount> serviceProviderAccounts = sqlSession().selectList(NS + ".selectServiceProviderAccountByRRContainerId", params);
        return serviceProviderAccounts.size() > 0? serviceProviderAccounts.get(0) : null;
    }

    /**
     * Use only for rollback
     *
     * @param serviceProviderAccount
     */
    public void delete(ServiceProviderAccount serviceProviderAccount) {
        sqlSession().delete(NS + ".deleteServiceProviderAccount", serviceProviderAccount);
    }

    public void close(ServiceProviderAccount serviceProviderAccount) throws ServiceNotAllowableException {
        List<ServiceProviderAccount> serviceProviderAccounts =
                getServiceProviderAccounts(
                        new FilterWrapper<>(new ServiceProviderAccount(serviceProviderAccount.getEircAccount())));
        ServiceProviderAccount oldObject = getServiceProviderAccountByPkId(serviceProviderAccount.getPkId());
        if (oldObject.getRegistryRecordContainerId() != null) {
            // Для того чтобы можно было сделать откат создания
            Date currentDate = DateUtil.getCurrentDate();
            serviceProviderAccount.setBeginDate(currentDate);
            serviceProviderAccount.setEndDate(currentDate);
            update(serviceProviderAccount);
        } else {
            archive(serviceProviderAccount);
        }
        if (serviceProviderAccounts.size() == 0 ||
                (serviceProviderAccounts.size() == 1 &&
                        serviceProviderAccounts.get(0).getId().equals(serviceProviderAccount.getId()))) {
            // if service provider accounts list is empty then close EIRC account
            eircAccountBean.archive(serviceProviderAccount.getEircAccount());
        }
    }

    @Override
    public void setSqlSessionFactoryBean(SqlSessionFactoryBean sqlSessionFactoryBean) {
        super.setSqlSessionFactoryBean(sqlSessionFactoryBean);
        sequenceBean.setSqlSessionFactoryBean(sqlSessionFactoryBean);
        stringLocaleBean.setSqlSessionFactoryBean(sqlSessionFactoryBean);
        eircOrganizationStrategy.setSqlSessionFactoryBean(sqlSessionFactoryBean);
    }
}
