package ru.complitex.keconnection.heatmeter.service;

import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.common.mybatis.XmlMapper;
import ru.complitex.common.service.AbstractBean;
import ru.complitex.common.util.IDiffFunction;
import ru.complitex.common.util.IdListUtil;
import ru.complitex.keconnection.heatmeter.entity.ServiceContract;
import ru.complitex.keconnection.heatmeter.entity.ServiceContractBuilding;
import ru.complitex.keconnection.heatmeter.entity.ServiceContractService;
import ru.complitex.organization.strategy.ServiceStrategy;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.List;

/**
 * inheaven on 13.11.2014 17:10.
 */
@Stateless
@XmlMapper
public class ServiceContractBean extends AbstractBean {
    @EJB
    private ServiceStrategy serviceStrategy;

    public void save(ServiceContract serviceContract){
        if (serviceContract.getId() == null){
            sqlSession().insert("insertServiceContract", serviceContract);
        }else{
            sqlSession().update("updateServiceContract", serviceContract);
        }

        updateBuilding(serviceContract);
        updateServices(serviceContract);
    }

    public ServiceContract getServiceContract(Long id){
        return sqlSession().selectOne("selectServiceContract", id);
    }

    public List<ServiceContract> getServiceContracts(FilterWrapper<ServiceContract> filterWrapper){
        return sqlSession().selectList("selectServiceContractList", filterWrapper);
    }

    public Long getServiceContractsCount(FilterWrapper<ServiceContract> filterWrapper){
        return sqlSession().selectOne("selectServiceContractListCount", filterWrapper);
    }

    public void updateServices(final ServiceContract serviceContract){
        ServiceContract db = getServiceContract(serviceContract.getId());

        IdListUtil.iterateDiff(serviceContract.getServiceContractServices(), db.getServiceContractServices(),
                new IDiffFunction<ServiceContractService>() {
                    @Override
                    public void onSave(ServiceContractService object) {
                        object.setServiceContractId(serviceContract.getId());

                        save(object);
                    }

                    @Override
                    public void onDelete(ServiceContractService object) {
                        delete(object);
                    }
                });
    }

    public void save(ServiceContractService serviceContractService){
        if (serviceContractService.getId() == null) {
            sqlSession().insert("insertServiceContractService", serviceContractService);
        }else{
            sqlSession().update("updateServiceContractService", serviceContractService);
        }
    }

    public void delete(ServiceContractService serviceContractService){
        sqlSession().delete("deleteServiceContractService", serviceContractService.getId());
    }

    public void updateBuilding(final ServiceContract serviceContract){
        ServiceContract db = getServiceContract(serviceContract.getId());

        IdListUtil.iterateDiff(serviceContract.getServiceContractBuildings(), db.getServiceContractBuildings(),
                new IDiffFunction<ServiceContractBuilding>() {
            @Override
            public void onSave(ServiceContractBuilding object) {
                object.setServiceContractId(serviceContract.getId());

                save(object);
            }

            @Override
            public void onDelete(ServiceContractBuilding object) {
                delete(object);
            }
        });
    }

    public void save(ServiceContractBuilding serviceContractBuilding){
        if (serviceContractBuilding.getId() == null) {
            sqlSession().insert("insertServiceContractBuilding", serviceContractBuilding);
        }else{
            sqlSession().update("updateServiceContractBuilding", serviceContractBuilding);
        }
    }

    public void delete(ServiceContractBuilding serviceContractBuilding){
        sqlSession().delete("deleteServiceContractBuilding", serviceContractBuilding.getId());
    }
}
