package org.complitex.osznconnection.file.service.subsidy;

import org.complitex.common.entity.FilterWrapper;
import org.complitex.common.service.AbstractBean;
import org.complitex.osznconnection.file.entity.subsidy.OschadbankRequest;

import javax.ejb.Stateless;
import java.util.List;

/**
 * @author Anatoly A. Ivanov
 * 26.03.2019 15:42
 */
@Stateless
public class OschadbankRequestBean extends AbstractBean {
    public List<OschadbankRequest> getOschadbankRequests(FilterWrapper<OschadbankRequest> filterWrapper){
        return null;
    }

    public Long getOschadbankRequestsCount(FilterWrapper<OschadbankRequest> filterWrapper){
        return 0L;
    }
}
