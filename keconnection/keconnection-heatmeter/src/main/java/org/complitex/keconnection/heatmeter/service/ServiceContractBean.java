package org.complitex.keconnection.heatmeter.service;

import org.complitex.address.strategy.building.entity.BuildingCode;
import org.complitex.common.entity.FilterWrapper;
import org.complitex.common.mybatis.XmlMapper;
import org.complitex.common.service.AbstractBean;
import org.complitex.keconnection.heatmeter.entity.ServiceContract;
import org.complitex.keconnection.heatmeter.entity.ServiceContractBuilding;

import javax.ejb.Stateless;
import java.util.List;

/**
 * inheaven on 13.11.2014 17:10.
 */
@Stateless
@XmlMapper
public class ServiceContractBean extends AbstractBean {
    public void save(ServiceContract serviceContract){
        if (serviceContract.getId() == null){
            sqlSession().insert("insertServiceContract", serviceContract);
        }else{
            sqlSession().update("updateServiceContract", serviceContract);
        }

        updateBuilding(serviceContract);
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

    public void updateBuilding(ServiceContract serviceContract){
        ServiceContract db = getServiceContract(serviceContract.getId());

        //delete
        for (BuildingCode dbBuildingCode : db.getBuildingCodes()){
            boolean has = false;

            for (BuildingCode buildingCode : serviceContract.getBuildingCodes()){
                if (dbBuildingCode.getId().equals(buildingCode.getId())){
                    has = true;
                    break;
                }
            }

            if (!has){
                delete(new ServiceContractBuilding(dbBuildingCode.getId(), serviceContract.getId()));
            }
        }

        //insert
        for (BuildingCode buildingCode : serviceContract.getBuildingCodes()){
            boolean has = false;

            for (BuildingCode dbBuildingCode : db.getBuildingCodes()){
                if (dbBuildingCode.getId().equals(buildingCode.getId())){
                    has = true;
                    break;
                }
            }

            if (has){
                save(new ServiceContractBuilding(buildingCode.getId(), serviceContract.getId()));
            }
        }
    }


}
