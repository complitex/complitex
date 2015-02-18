package org.complitex.keconnection.heatmeter.service;

import org.complitex.address.strategy.building.entity.BuildingCode;
import org.complitex.common.entity.DomainObject;
import org.complitex.common.entity.FilterWrapper;
import org.complitex.common.mybatis.XmlMapper;
import org.complitex.common.service.AbstractBean;
import org.complitex.common.util.IDiffFunction;
import org.complitex.common.util.IdListUtil;
import org.complitex.keconnection.heatmeter.entity.ServiceContract;
import org.complitex.keconnection.heatmeter.entity.ServiceContractBuilding;
import org.complitex.keconnection.heatmeter.entity.ServiceContractService;
import org.complitex.keconnection.heatmeter.strategy.ServiceStrategy;

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

    public void save(ServiceContractBuilding serviceContractBuilding){
        sqlSession().insert("insertServiceContractBuilding", serviceContractBuilding);
    }

    public void delete(ServiceContractBuilding serviceContractBuilding){
        sqlSession().delete("deleteServiceContractBuilding", serviceContractBuilding);
    }

    public void updateBuilding(final ServiceContract serviceContract){
        ServiceContract db = getServiceContract(serviceContract.getId());

        IdListUtil.iterateDiff(serviceContract.getBuildingCodes(), db.getBuildingCodes(), new IDiffFunction<BuildingCode>() {
            @Override
            public void onSave(BuildingCode object) {
                save(new ServiceContractBuilding(object.getId(), serviceContract.getId()));
            }

            @Override
            public void onDelete(BuildingCode object) {
                delete(new ServiceContractBuilding(object.getId(), serviceContract.getId()));
            }
        });
    }

    public void save(ServiceContractService serviceContractService){
        sqlSession().insert("insertServiceContractService", serviceContractService);
    }

    public void delete(ServiceContractService serviceContractService){
        sqlSession().delete("deleteServiceContractService", serviceContractService);
    }

    public void updateServices(final ServiceContract serviceContract){
        ServiceContract db = getServiceContract(serviceContract.getId());

        IdListUtil.iterateDiff(serviceContract.getServices(), db.getServices(), new IDiffFunction<DomainObject>() {
            @Override
            public void onSave(DomainObject object) {
                save(new ServiceContractService(object.getObjectId(), serviceContract.getId()));
            }

            @Override
            public void onDelete(DomainObject object) {
                delete(new ServiceContractService(object.getObjectId(), serviceContract.getId()));
            }
        });
    }
}
