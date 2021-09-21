package org.complitex.common.strategy;

import org.complitex.common.service.AbstractBean;

import javax.ejb.Stateless;
/**
 *
 * @author Artem
 */
@Stateless
public class SequenceBean extends AbstractBean {
    public static final String NS = SequenceBean.class.getName();

    public long nextId(String entityName) {
        return sqlSession().selectOne(NS + ".nextId", entityName + "_object_id_sequence");
    }

    public long nextStringId(String entityName) {
        return sqlSession().selectOne(NS + ".nextId", entityName + "_string_value_id_sequence");
    }
}
