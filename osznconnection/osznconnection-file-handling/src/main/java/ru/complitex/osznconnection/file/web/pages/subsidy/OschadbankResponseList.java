package ru.complitex.osznconnection.file.web.pages.subsidy;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.common.util.StringUtil;
import ru.complitex.common.web.component.PanelWrapper;
import ru.complitex.common.web.component.TextLabel;
import ru.complitex.osznconnection.file.entity.RequestStatus;
import ru.complitex.osznconnection.file.entity.StatusDetail;
import ru.complitex.osznconnection.file.entity.StatusDetailInfo;
import ru.complitex.osznconnection.file.entity.subsidy.OschadbankResponse;
import ru.complitex.osznconnection.file.entity.subsidy.OschadbankResponseField;
import ru.complitex.osznconnection.file.service.StatusRenderUtil;
import ru.complitex.osznconnection.file.service.status.details.IStatusDetailRenderer;
import ru.complitex.osznconnection.file.service.status.details.StatusDetailBean;
import ru.complitex.osznconnection.file.service.subsidy.OschadbankResponseBean;
import ru.complitex.osznconnection.file.web.AuthorizedListPage;
import ru.complitex.osznconnection.file.web.component.StatusDetailInfoPanel;
import ru.complitex.osznconnection.file.web.component.StatusRenderer;

import javax.ejb.EJB;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * @author Anatoly A. Ivanov
 * 25.04.2019 18:37
 */
public class OschadbankResponseList extends AuthorizedListPage<OschadbankResponse> {
    @EJB
    private OschadbankResponseBean oschadbankResponseBean;

    @EJB
    private StatusDetailBean statusDetailBean;

    private Long requestFileId;

    public OschadbankResponseList(PageParameters pageParameters) {
        super(pageParameters, "dbfFields.", OschadbankResponseFileList.class);

        requestFileId = pageParameters.get("request_file_id").toLongObject();
    }

    @Override
    protected OschadbankResponse newFilterObject(PageParameters pageParameters) {
        return new OschadbankResponse(pageParameters.get("request_file_id").toLongObject());
    }

    @Override
    protected List<OschadbankResponse> getList(FilterWrapper<OschadbankResponse> filterWrapper) {
        filterWrapper.setCamelToUnderscore(false);


        List<OschadbankResponse> list = oschadbankResponseBean.getOschadbankResponses(filterWrapper);

        list.forEach(r -> r.putField("STATUS", r.getStatus().name()));

        return list;
    }

    @Override
    protected Long getCount(FilterWrapper<OschadbankResponse> filterWrapper) {
        return oschadbankResponseBean.getOschadbankResponsesCount(filterWrapper);
    }

    @Override
    protected List<String> getProperties() {
        List<String> list =  StringUtil.asList(OschadbankResponseField.class);

        list.add("STATUS");

        return list;
    }

    @SuppressWarnings("Duplicates")
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
    protected void onPopulateData(ListItem<OschadbankResponse> row, ListItem<String> column) {
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
                OschadbankResponse oschadbankResponse = getFilterWrapper().getObject();
                oschadbankResponse.putField(OschadbankResponseField.OSCHADBANK_ACCOUNT, statusDetail.getDetail("OSCHADBANK_ACCOUNT"));
                oschadbankResponse.putField(OschadbankResponseField.FIO, statusDetail.getDetail("FIO"));
                oschadbankResponse.putField(OschadbankResponseField.SERVICE_ACCOUNT, statusDetail.getDetail("SERVICE_ACCOUNT"));

                target.add(getFilterForm());
            }

            @Override
            public List<StatusDetailInfo> loadStatusDetails() {
                return statusDetailBean.getOschadbankResponseDetails(requestFileId);
            }
        };
    }
}
