package ru.complitex.eirc.service_provider_account.service;

import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.common.service.AbstractBean;
import ru.complitex.correction.entity.Correction;
import ru.complitex.eirc.service_provider_account.entity.ServiceProviderAccountCorrection;

import javax.ejb.Stateless;
import java.util.List;

/**
 * @author Pavel Sknar
 */
@Stateless
public class ServiceProviderAccountCorrectionBean extends AbstractBean{
    public final static String NS = ServiceProviderAccountCorrectionBean.class.getName();
    private static final String NS_CORRECTION = Correction.class.getName();

    public ServiceProviderAccountCorrection geServiceProviderAccountCorrection(Long id){
        return sqlSession().selectOne(NS + ".selectServiceProviderAccountCorrection", id);
    }

    public List<ServiceProviderAccountCorrection> getServiceProviderAccountCorrections(FilterWrapper<ServiceProviderAccountCorrection> filterWrapper){
        return sqlSession().selectList(NS + ".selectServiceProviderAccountCorrections", filterWrapper);
    }

    public Long getServiceProviderAccountCorrectionsCount(FilterWrapper<ServiceProviderAccountCorrection> filterWrapper){
        return sqlSession().selectOne(NS + ".selectServiceProviderAccountCorrectionsCount", filterWrapper);
    }

    public void save(ServiceProviderAccountCorrection ServiceProviderAccountCorrection){
        if (ServiceProviderAccountCorrection.getId() == null) {
            sqlSession().insert(NS_CORRECTION + ".insertCorrection", ServiceProviderAccountCorrection);
        } else{
            sqlSession().update(NS_CORRECTION + ".updateCorrection", ServiceProviderAccountCorrection);
        }
    }

    public void delete(ServiceProviderAccountCorrection ServiceProviderAccountCorrection){
        sqlSession().delete(NS_CORRECTION + ".deleteCorrection", ServiceProviderAccountCorrection);
    }
}
