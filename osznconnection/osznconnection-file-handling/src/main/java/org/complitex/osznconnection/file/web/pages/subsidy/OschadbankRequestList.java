package org.complitex.osznconnection.file.web.pages.subsidy;

import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.complitex.common.entity.FilterWrapper;
import org.complitex.common.util.StringUtil;
import org.complitex.osznconnection.file.entity.subsidy.OschadbankRequest;
import org.complitex.osznconnection.file.entity.subsidy.OschadbankRequestField;
import org.complitex.osznconnection.file.service.subsidy.OschadbankRequestBean;
import org.complitex.osznconnection.file.web.AuthorizedListPage;

import javax.ejb.EJB;
import java.util.List;

/**
 * @author Anatoly A. Ivanov
 * 26.03.2019 15:09
 */
public class OschadbankRequestList extends AuthorizedListPage<OschadbankRequest> {
    @EJB
    private OschadbankRequestBean oschadbankRequestBean;

    public OschadbankRequestList(PageParameters pageParameters) {
        super(pageParameters, "dbfFields.", OschadbankRequestFileList.class);
    }

    @Override
    protected OschadbankRequest newFilterObject(PageParameters pageParameters) {
        return new OschadbankRequest(pageParameters.get("request_file_id").toLongObject());
    }

    @Override
    protected List<OschadbankRequest> getList(FilterWrapper<OschadbankRequest> filterWrapper) {
        return oschadbankRequestBean.getOschadbankRequests(filterWrapper);
    }

    @Override
    protected Long getCount(FilterWrapper<OschadbankRequest> filterWrapper) {
        return oschadbankRequestBean.getOschadbankRequestsCount(filterWrapper);
    }

    @Override
    protected List<String> getProperties() {
        return StringUtil.asList(OschadbankRequestField.class);
    }
}
