package ru.complitex.eirc.service_provider_account.service;

import org.apache.commons.lang3.builder.EqualsBuilder;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.common.service.AbstractBean;
import ru.complitex.common.strategy.SequenceBean;
import ru.complitex.common.util.DateUtil;
import ru.complitex.eirc.service_provider_account.entity.Exemption;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.List;

/**
 * @author Pavel Sknar
 */
@Stateless
public class ExemptionBean extends AbstractBean {

    private static final String NS = ExemptionBean.class.getPackage().getName() + ".ExemptionBean";
    public static final String ENTITY = "exemption";

    @EJB
    private SequenceBean sequenceBean;

    public void archive(Exemption object) {
        if (object.getEndDate() == null) {
            object.setEndDate(DateUtil.getCurrentDate());
        }
        sqlSession().update(NS + ".updateExemptionEndDate", object);
    }

    public Exemption getExemption(long id) {
        List<Exemption> resultOrderByDescData = sqlSession().selectList(NS + ".selectExemption", id);
        return resultOrderByDescData.size() > 0? resultOrderByDescData.get(0): null;
    }

    public List<Exemption> getExemptions(FilterWrapper<Exemption> filter) {
        return sqlSession().selectList(NS + ".selectExemptions", filter);
    }

    public int count(FilterWrapper<Exemption> filter) {
        return sqlSession().selectOne(NS + ".countExemptions", filter);
    }

    public void save(Exemption exemption) {
        if (exemption.getId() == null) {
            saveNew(exemption);
        } else {
            update(exemption);
        }
    }

    private void saveNew(Exemption exemption) {
        exemption.setId(sequenceBean.nextId(ENTITY));
        sqlSession().insert(NS + ".insertExemption", exemption);
    }

    private void update(Exemption exemption) {
        Exemption oldObject = getExemptionByPkId(exemption.getPkId());
        if (EqualsBuilder.reflectionEquals(oldObject, exemption)) {
            return;
        }
        archive(oldObject);
        exemption.setBeginDate(oldObject.getEndDate());
        sqlSession().insert(NS + ".insertExemption", exemption);
    }

    public Exemption getExemptionByPkId(long pkId) {
        return sqlSession().selectOne(NS + ".selectExemptionByPkId", pkId);
    }
    
}
