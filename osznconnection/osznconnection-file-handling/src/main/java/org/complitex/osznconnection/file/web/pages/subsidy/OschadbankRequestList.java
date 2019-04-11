package org.complitex.osznconnection.file.web.pages.subsidy;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.complitex.common.entity.FilterWrapper;
import org.complitex.common.util.StringUtil;
import org.complitex.common.web.component.PanelWrapper;
import org.complitex.common.web.component.TextLabel;
import org.complitex.osznconnection.file.entity.RequestStatus;
import org.complitex.osznconnection.file.entity.StatusDetail;
import org.complitex.osznconnection.file.entity.StatusDetailInfo;
import org.complitex.osznconnection.file.entity.subsidy.OschadbankRequest;
import org.complitex.osznconnection.file.entity.subsidy.OschadbankRequestField;
import org.complitex.osznconnection.file.service.StatusRenderUtil;
import org.complitex.osznconnection.file.service.status.details.IStatusDetailRenderer;
import org.complitex.osznconnection.file.service.status.details.StatusDetailBean;
import org.complitex.osznconnection.file.service.subsidy.OschadbankRequestBean;
import org.complitex.osznconnection.file.web.AuthorizedListPage;
import org.complitex.osznconnection.file.web.component.StatusDetailInfoPanel;
import org.complitex.osznconnection.file.web.component.StatusRenderer;

import javax.ejb.EJB;
import java.util.Arrays;
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
        return new OschadbankRequest(pageParameters.get("request_file_id").toLongObject());
    }

    @Override
    protected List<OschadbankRequest> getList(FilterWrapper<OschadbankRequest> filterWrapper) {
        filterWrapper.setCamelToUnderscore(false);

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
    protected void onPopulateFilter(ListItem<String> item) {
        if (item.getModelObject().equals("STATUS")){
            item.add(PanelWrapper.of(FILTER_FIELD, new DropDownChoice<>(PanelWrapper.ID, new PropertyModel<>(
                    getFilterWrapper(), "object.status"), Arrays.asList(RequestStatus.PROCESSED,
                    RequestStatus.ACCOUNT_NUMBER_NOT_FOUND, RequestStatus.MORE_ONE_ACCOUNTS,
                    RequestStatus.PROCESSED_WITH_ERROR), new StatusRenderer()), PanelWrapper.TYPE.SELECT));
        }else{
            super.onPopulateFilter(item);
        }
    }

    @Override
    protected void onPopulateData(ListItem<OschadbankRequest> row, ListItem<String> column) {
        if (column.getModelObject().equals("STATUS")){
            column.add(new TextLabel(DATA_FIELD, StatusRenderUtil.displayStatus(row.getModelObject().getStatus(), getLocale())));
        }else{
            super.onPopulateData(row, column);
        }
    }

    @Override
    protected WebMarkupContainer newStatusDetailPanel() {
        return new StatusDetailInfoPanel(STATUS_DETAIL_PANEL, new IStatusDetailRenderer() {
            @Override
            public String displayStatusDetail(StatusDetail statusDetail, RequestStatus status, Locale locale) {
                return String.join(" ", statusDetail.getDetail("OSCHADBANK_ACCOUNT") ,
                        statusDetail.getDetail("FIO"), statusDetail.getDetail("SERVICE_ACCOUNT"));
            }
        }) {
            @Override
            public void onStatusDetailInfo(AjaxRequestTarget target, StatusDetailInfo statusDetailInfo) {
                getFilterWrapper().getObject().setStatus(statusDetailInfo.getStatus());

                target.add(getFilterForm());
            }

            @Override
            public void onStatusDetail(AjaxRequestTarget target, StatusDetailInfo statusDetailInfo, StatusDetail statusDetail) {
                OschadbankRequest oschadbankRequest = getFilterWrapper().getObject();
                oschadbankRequest.putField(OschadbankRequestField.OSCHADBANK_ACCOUNT, statusDetail.getDetail("OSCHADBANK_ACCOUNT"));
                oschadbankRequest.putField(OschadbankRequestField.FIO, statusDetail.getDetail("FIO"));
                oschadbankRequest.putField(OschadbankRequestField.SERVICE_ACCOUNT, statusDetail.getDetail("SERVICE_ACCOUNT"));

                target.add(getFilterForm());
            }

            @Override
            public List<StatusDetailInfo> loadStatusDetails() {
                return statusDetailBean.getOschadbankRequestDetails(requestFileId);
            }
        };
    }
}
