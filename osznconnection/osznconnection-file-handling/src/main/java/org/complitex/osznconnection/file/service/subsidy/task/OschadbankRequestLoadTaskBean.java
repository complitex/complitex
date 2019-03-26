package org.complitex.osznconnection.file.service.subsidy.task;

import org.complitex.common.exception.ExecuteException;
import org.complitex.osznconnection.file.entity.RequestFile;
import org.complitex.osznconnection.file.service.AbstractRequestTaskBean;

import java.util.Map;

/**
 * @author Anatoly A. Ivanov
 * 26.03.2019 15:17
 */
public class OschadbankRequestLoadTaskBean extends AbstractRequestTaskBean<RequestFile> {
    @Override
    public boolean execute(RequestFile object, Map commandParameters) throws ExecuteException {
        return false;
    }
}
