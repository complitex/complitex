package org.complitex.osznconnection.file.service.privilege;

import org.complitex.common.service.AbstractBean;
import org.complitex.common.service.SessionBean;
import org.complitex.osznconnection.file.entity.RequestFileFilter;
import org.complitex.osznconnection.file.entity.privilege.PrivilegeFileGroup;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.List;

/**
 * inheaven on 04.04.2016.
 */
@Stateless
public class PrivilegeFileGroupBean extends AbstractBean{
    @EJB
    private SessionBean sessionBean;

    public PrivilegeFileGroup getPrivilegeFileGroup(Long id){
        return sqlSession().selectOne("selectPrivilegeFileGroup", id);
    }

    public List<PrivilegeFileGroup> getPrivilegeFileGroups(RequestFileFilter filter){
        sessionBean.authorize(filter);

        return sqlSession().selectList("selectPrivilegeFilesGroups", filter);
    }

    public Long getPrivilegeFileGroupsCount(RequestFileFilter filter){
        sessionBean.authorize(filter);

        return sqlSession().selectOne("selectPrivilegeFilesGroupsCount", filter);
    }
}
