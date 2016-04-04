package org.complitex.osznconnection.file.service.privilege;

import org.complitex.common.service.AbstractBean;
import org.complitex.osznconnection.file.entity.RequestFileFilter;
import org.complitex.osznconnection.file.entity.privilege.PrivilegeFileGroup;

import java.util.List;

/**
 * inheaven on 04.04.2016.
 */
public class PrivilegeFileGroupBean extends AbstractBean{
    public List<PrivilegeFileGroup> getPrivilegeFileGroups(RequestFileFilter filter){
        return sqlSession().selectList("selectPrivilegeFilesGroups", filter);
    }

    public Long etPrivilegeFileGroupsCount(RequestFileFilter filter){
        return sqlSession().selectOne("selectPrivilegeFilesGroupsCount", filter);
    }
}
