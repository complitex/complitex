package ru.flexpay.eirc.registry.service.handle.changing;

import org.complitex.common.service.AbstractBean;
import ru.flexpay.eirc.registry.entity.changing.ServiceProviderAccountAttrChanging;

import javax.ejb.Stateless;

/**
 * @author Pavel Sknar
 */
@Stateless
public class ServiceProviderAccountAttrChangingBean extends AbstractBean {
    private static final String NS = ServiceProviderAccountAttrChangingBean.class.getName();

    public void create(ServiceProviderAccountAttrChanging changing) {
        sqlSession().insert(NS + ".insertSPAAttrChanging", changing);
    }

    public void delete(ServiceProviderAccountAttrChanging changing) {
        sqlSession().insert(NS + ".deleteSPAAttrChanging", changing);
    }

    public ServiceProviderAccountAttrChanging findChanging(Long containerId) {
        return sqlSession().selectOne(NS + ".selectSPAAttrChanging", containerId);
    }
}
