package org.complitex.osznconnection.file.web.pages.subsidy;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.complitex.common.entity.FilterWrapper;
import org.complitex.common.util.StringUtil;
import org.complitex.osznconnection.file.entity.RequestStatus;
import org.complitex.osznconnection.file.entity.StatusDetail;
import org.complitex.osznconnection.file.entity.StatusDetailInfo;
import org.complitex.osznconnection.file.entity.subsidy.OschadbankRequest;
import org.complitex.osznconnection.file.entity.subsidy.OschadbankRequestField;
import org.complitex.osznconnection.file.service.status.details.IStatusDetailRenderer;
import org.complitex.osznconnection.file.service.status.details.StatusDetailBean;
import org.complitex.osznconnection.file.service.subsidy.OschadbankRequestBean;
import org.complitex.osznconnection.file.web.AuthorizedListPage;
import org.complitex.osznconnection.file.web.component.StatusDetailInfoPanel;

import javax.ejb.EJB;
import java.util.List;
import java.util.Locale;

/**
 * @author Anatoly A. Ivanov
 * 26.03.2019 15:09
 */
public class OschadbankRequestList extends AuthorizedListPage<OschadbankRequest> {
    @EJB
    private OschadbankRequestBean oschadbankRequestBean;

    @EJB
    private StatusDetailBean statusDetailBean;

    private Long requestFileId;

    public OschadbankRequestList(PageParameters pageParameters) {
        super(pageParameters, "dbfFields.", OschadbankRequestFileList.class);

        requestFileId = pageParameters.get("request_file_id").toLongObject();
    }

    @Override
    protected OschadbankRequest newFilterObject(PageParameters pageParameters) {
        return new OschadbankRequest(requestFileId);
    }

    @Override
    protected List<OschadbankRequest> getList(FilterWrapper<OschadbankRequest> filterWrapper) {
        List<OschadbankRequest> list = oschadbankRequestBean.getOschadbankRequests(filterWrapper);

        list.forEach(r -> r.putField("STATUS", r.getStatus().name()));

        return list;
    }

    @Override
    protected Long getCount(FilterWrapper<OschadbankRequest> filterWrapper) {
        return oschadbankRequestBean.getOschadbankRequestsCount(filterWrapper);
    }

    @Override
    protected List<String> getProperties() {
        List<String> list =  StringUtil.asList(OschadbankRequestField.class);

        list.add("STATUS");

        return list;
    }

    @Override
    protected WebMarkupContainer newStatusDetailPanel() {
        return new StatusDetailInfoPanel(STATUS_DETAIL_PANEL, new IStatusDetailRenderer() {
            @Override
            public String displayStatusDetail(StatusDetail statusDetail, RequestStatus status, Locale locale) {
                return statusDetail.getDetail("FIO");
            }
        }, getFilterForm()) {
            @Override
            public List<StatusDetailInfo> loadStatusDetails() {
                return statusDetailBean.getOschadbankRequestDetails(requestFileId);
            }
        };
    }
}
