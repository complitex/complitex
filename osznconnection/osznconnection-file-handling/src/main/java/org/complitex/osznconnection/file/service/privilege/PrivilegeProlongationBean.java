package org.complitex.osznconnection.file.service.privilege;

import com.google.common.collect.ImmutableMap;
import org.complitex.address.entity.AddressEntity;
import org.complitex.osznconnection.file.entity.example.PrivilegeExample;
import org.complitex.osznconnection.file.entity.privilege.PrivilegeProlongation;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import java.util.List;

/**
 * @author inheaven on 27.06.16.
 */
public class PrivilegeProlongationBean extends AbstractPrivilegeBean{
    public static final String NS = PrivilegeProlongationBean.class.getName();

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void insertPrivilegeProlongation(List<PrivilegeProlongation> privilegeProlongationList){
        if (privilegeProlongationList.isEmpty()) {
            return;
        }

        sqlSession().insert(NS + ".insertPrivilegeProlongationList", privilegeProlongationList);
    }

    public Long getPrivilegeProlongationsCount(PrivilegeExample privilegeExample){
        return sqlSession().selectOne(NS + ".selectPrivilegeProlongationsCount", privilegeExample);
    }

    public List<PrivilegeProlongation> getPrivilegeProlongations(PrivilegeExample privilegeExample){
        List<PrivilegeProlongation> list = sqlSession().selectList(NS + ".selectPrivilegeProlongations", privilegeExample);

        loadFacilityStreet(list);

        return list;
    }

    public void updatePrivilegeProlongation(PrivilegeProlongation privilegeProlongation){
        sqlSession().update(NS + ".updatePrivilegeProlongation", privilegeProlongation);
    }

    public void updatePrivilegeProlongationAccountNumber(PrivilegeProlongation privilegeProlongation){
        sqlSession().update(NS + ".updatePrivilegeProlongationAccountNumber", privilegeProlongation);
    }

    public List<Long> getPrivilegeProlongationIds(Long requestFileId){
        return sqlSession().selectList(NS + ".selectPrivilegeProlongationIds", requestFileId);
    }

    public List<PrivilegeProlongation> getPrivilegeProlongationForOperation(Long requestFileId, List<Long> ids){
        List<PrivilegeProlongation> list = sqlSession().selectList(NS + ".selectPrivilegeProlongationForOperation",
                ImmutableMap.of("requestFileId", requestFileId, "ids", ids));

        loadFacilityStreet(list);

        return list;
    }

    public void clearPrivilegeProlongationBound(Long requestFileId){
        sqlSession().update(NS + ".clearPrivilegeProlongationBound", requestFileId);
    }

    public void markPrivilegeProlongationCorrected(PrivilegeProlongation privilegeProlongation, AddressEntity addressEntity){
        markPrivilegeCorrected(NS + ".markPrivilegeProlongationCorrected", privilegeProlongation, addressEntity);
    }

    public void delete(Long requestFileId){
        sqlSession().delete(NS + ".deletePrivilegeProlongation", requestFileId);
    }
}
