package org.complitex.correction.service;

import org.complitex.common.entity.FilterWrapper;
import org.complitex.correction.entity.ServiceCorrection;

import javax.ejb.Stateless;
import java.util.List;

/**
 * @author inheaven on 23.06.2015 16:24.
 */
@Stateless
public class ServiceCorrectionBean extends CorrectionBean {
    public final static String NS = ServiceCorrectionBean.class.getName();
    
    public ServiceCorrection geServiceCorrection(Long id){
        return sqlSession().selectOne(NS + ".selectServiceCorrection", id);
    }

    public List<ServiceCorrection> getServiceCorrections(FilterWrapper<ServiceCorrection> filterWrapper){
        return sqlSession().selectList(NS + ".selectServiceCorrections", filterWrapper);
    }

    public List<ServiceCorrection> getServiceCorrections(String dataSource, FilterWrapper<ServiceCorrection> filterWrapper){
        return sqlSession(dataSource).selectList(NS + ".selectServiceCorrections", filterWrapper);
    }

    public Long getServiceCorrectionsCount(FilterWrapper<ServiceCorrection> filterWrapper){
        return sqlSession().selectOne(NS + ".selectServiceCorrectionsCount", filterWrapper);
    }

}
