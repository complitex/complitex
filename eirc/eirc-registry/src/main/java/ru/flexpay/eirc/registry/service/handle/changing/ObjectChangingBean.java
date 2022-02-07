package ru.flexpay.eirc.registry.service.handle.changing;

import ru.complitex.common.service.AbstractBean;
import ru.flexpay.eirc.registry.entity.changing.ObjectChanging;

import javax.ejb.Stateless;

/**
 * @author Pavel Sknar
 */
@Stateless
public class ObjectChangingBean extends AbstractBean {
    private static final String NS = ObjectChangingBean.class.getName();

    public void create(ObjectChanging changing) {
        sqlSession().insert(NS + ".insertObjectChanging", changing);
    }

    public void delete(ObjectChanging changing) {
        sqlSession().insert(NS + ".deleteObjectChanging", changing);
    }

    public ObjectChanging findChanging(Long containerId) {
        return sqlSession().selectOne(NS + ".selectObjectChanging", containerId);
    }
}
