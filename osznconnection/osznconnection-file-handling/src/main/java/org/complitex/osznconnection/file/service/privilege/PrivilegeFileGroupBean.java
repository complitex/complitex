package org.complitex.osznconnection.file.service.privilege;

import org.complitex.common.service.AbstractBean;
import org.complitex.osznconnection.file.entity.RequestFileFilter;
import org.complitex.osznconnection.file.entity.privilege.PrivilegeFileGroup;

import javax.ejb.Stateless;
import java.util.List;

/**
 * inheaven on 04.04.2016.
 */
@Stateless
public class PrivilegeFileGroupBean extends AbstractBean{
    public PrivilegeFileGroup getPrivilegeFileGroup(Long id){
        return sqlSession().selectOne("selectPrivilegeFileGroup", id);
    }

    public List<PrivilegeFileGroup> getPrivilegeFileGroups(RequestFileFilter filter){
        return sqlSession().selectList("selectPrivilegeFilesGroups", filter);
    }

    public Long getPrivilegeFileGroupsCount(RequestFileFilter filter){
        return sqlSession().selectOne("selectPrivilegeFilesGroupsCount", filter);
    }

    public void delete(PrivilegeFileGroup privilegeFileGroup){
        //todo delete
    }

}
