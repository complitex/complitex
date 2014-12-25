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

    public long nextStringId(String entityTable) {
        long nextStringId;

        if (Strings.isEmpty(entityTable)) {
            nextStringId = sqlSession().selectOne(NS + ".nextStringIdForDescriptionData");
            sqlSession().update(NS + ".incrementStringIdForDescriptionData");
        } else {
            nextStringId = sqlSession().selectOne(NS + ".nextStringId", entityTable);
            sqlSession().update(NS + ".incrementStringId", entityTable);
        }

        return nextStringId;
    }


    public long nextId(String entityTable) {
        long nextId = sqlSession().selectOne(NS + ".nextId", entityTable);
        sqlSession().update(NS + ".incrementId", entityTable);

        return nextId;
    }


    public long nextIdOrInit(String entityTable) {
        try {
            return nextId(entityTable);
        } catch (Throwable th) {
            if (!create(entityTable)) {
                throw th;
            }

            return nextId(entityTable);
        }
    }

    private boolean create(String entityTable) {
        if (sqlSession().selectOne(NS + ".exists", entityTable) == null) {
            sqlSession().insert(NS + ".create", entityTable);
            return true;
        }

        return false;
    }
}
