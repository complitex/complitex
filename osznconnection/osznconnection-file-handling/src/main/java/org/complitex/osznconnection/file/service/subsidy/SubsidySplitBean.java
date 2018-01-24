package org.complitex.osznconnection.file.service.subsidy;

import org.complitex.common.service.AbstractBean;
import org.complitex.osznconnection.file.entity.subsidy.SubsidySplit;

import javax.ejb.Stateless;
import java.util.List;

/**
 * @author Anatoly A. Ivanov
 * 24.01.2018 20:17
 */
@Stateless
public class SubsidySplitBean extends AbstractBean{
    public final static String NS = SubsidySplitBean.class.getName();

    public void save(SubsidySplit subsidySplit){
        if (subsidySplit.getId() == null){
            sqlSession().insert(NS + ".insertSubsidySplit", subsidySplit);
        }else {
            sqlSession().update(NS + ".updateSubsidySplit", subsidySplit);
        }
    }

    public SubsidySplit getSubsidySplit(Long id){
        return sqlSession().selectOne(NS + ".selectSubsidySplit", id);
    }

    public List<SubsidySplit> getSubsidySplits(Long subsidyId){
        return sqlSession().selectList(NS + ".selectSubsidySplits", subsidyId);
    }

    public void clearSubsidySplits(Long subsidyId){
        sqlSession().delete(NS + ".deleteSubsidySplits", subsidyId);
    }
}
