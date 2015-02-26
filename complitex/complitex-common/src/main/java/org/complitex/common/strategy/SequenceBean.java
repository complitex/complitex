package org.complitex.common.strategy;

import org.apache.wicket.util.string.Strings;
import org.complitex.common.service.AbstractBean;

import javax.ejb.Stateless;
/**
 *
 * @author Artem
 */
@Stateless
public class SequenceBean extends AbstractBean {
    public static final String NS = SequenceBean.class.getName();

    public long nextStringId(String entityName) {
        long nextStringId;

        if (Strings.isEmpty(entityName)) {
            nextStringId = sqlSession().selectOne(NS + ".nextStringIdForDescriptionData");
            sqlSession().update(NS + ".incrementStringIdForDescriptionData");
        } else {
            nextStringId = sqlSession().selectOne(NS + ".nextStringId", entityName);
            sqlSession().update(NS + ".incrementStringId", entityName);
        }

        return nextStringId;
    }


    public long nextId(String entityName) {
        long nextId = sqlSession().selectOne(NS + ".nextId", entityName);
        sqlSession().update(NS + ".incrementId", entityName);

        return nextId;
    }


    public long nextIdOrInit(String entityName) {
        try {
            return nextId(entityName);
        } catch (Throwable th) {
            if (!create(entityName)) {
                throw th;
            }

            return nextId(entityName);
        }
    }

    private boolean create(String entityName) {
        if (sqlSession().selectOne(NS + ".exists", entityName) == null) {
            sqlSession().insert(NS + ".create", entityName);
            return true;
        }

        return false;
    }
}
