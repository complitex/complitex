package ru.complitex.osznconnection.file.service.privilege;

import ru.complitex.common.service.AbstractBean;
import ru.complitex.common.service.SessionBean;
import ru.complitex.osznconnection.file.entity.RequestFileFilter;
import ru.complitex.osznconnection.file.entity.privilege.PrivilegeFileGroup;

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
