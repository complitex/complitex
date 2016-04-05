package org.complitex.osznconnection.file.service.privilege.task;

import org.complitex.common.service.executor.AbstractTaskBean;
import org.complitex.common.service.executor.ExecuteException;
import org.complitex.osznconnection.file.entity.privilege.PrivilegeFileGroup;

import java.util.Map;

/**
 * inheaven on 05.04.2016.
 */
public class PrivilegeGroupBindTaskBean extends AbstractTaskBean<PrivilegeFileGroup>{
    @Override
    public boolean execute(PrivilegeFileGroup object, Map commandParameters) throws ExecuteException {
        return false;
    }

    @Override
    public void onError(PrivilegeFileGroup object) {

    }
}
