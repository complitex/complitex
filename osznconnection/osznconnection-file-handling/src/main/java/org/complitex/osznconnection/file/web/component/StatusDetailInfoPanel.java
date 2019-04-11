package org.complitex.osznconnection.file.web.component;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.complitex.osznconnection.file.entity.StatusDetail;
import org.complitex.osznconnection.file.entity.StatusDetailInfo;
import org.complitex.osznconnection.file.service.StatusRenderUtil;
import org.complitex.osznconnection.file.service.status.details.IStatusDetailRenderer;
import org.complitex.osznconnection.file.service.status.details.StatusDetailRenderUtil;

import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 11.04.19 17:37
 */
public abstract class StatusDetailInfoPanel extends Panel {
    private ListView<StatusDetailInfo> statusDetailsInfo;

    public StatusDetailInfoPanel(String id, IStatusDetailRenderer statusDetailRenderer, Component... update) {
        super(id);
        setOutputMarkupId(true);

        WebMarkupContainer container = new WebMarkupContainer("container");
        add(container);

        IModel<List<StatusDetailInfo>> model = new LoadableDetachableModel<List<StatusDetailInfo>>() {

            @Override
            protected List<StatusDetailInfo> load() {
                return loadStatusDetails();
            }
        };

        statusDetailsInfo = new ListView<StatusDetailInfo>("statusDetailsInfo", model) {

            @Override
            protected void populateItem(ListItem<StatusDetailInfo> item) {
                StatusDetailInfo statusDetailInfo = item.getModelObject();

                WebMarkupContainer statusDetailsContainer = new WebMarkupContainer("statusDetailsContainer");
                statusDetailsContainer.setOutputMarkupId(true);
                statusDetailsContainer.setOutputMarkupPlaceholderTag(true);
                item.add(statusDetailsContainer);

                AjaxLink expand = new AjaxLink("expand") {

                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        for (Component component : update) {
                            target.add(component);
                        }

                        onStatusDetailInfo(statusDetailInfo);

                        if (statusDetailInfo.getStatusDetails() != null && !statusDetailInfo.getStatusDetails().isEmpty()) {
                            statusDetailsContainer.setVisible(!statusDetailsContainer.isVisible());
                            target.add(statusDetailsContainer);
                        }
                    }
                };
                item.add(expand);

                String info = StatusRenderUtil.displayStatus(statusDetailInfo.getStatus(), getLocale()) +
                        StatusDetailRenderUtil.displayCount(statusDetailInfo.getCount());
                expand.add(new Label("info", info));

                ListView<StatusDetail> statusDetails = new ListView<StatusDetail>("statusDetails",
                        statusDetailInfo.getStatusDetails()) {

                    @Override
                    protected void populateItem(ListItem<StatusDetail> item) {
                        final StatusDetail statusDetail = item.getModelObject();

                        AjaxLink filter = new AjaxLink("filter") {

                            @Override
                            public void onClick(AjaxRequestTarget target) {
                                onStatusDetail(statusDetailInfo, statusDetail);

                                for (Component component : update) {
                                    target.add(component);
                                }
                            }
                        };
                        item.add(filter);

                        filter.add(new Label("name", StatusDetailRenderUtil.displayStatusDetail(
                                statusDetailInfo.getStatus(), statusDetail, statusDetailRenderer, getLocale())));
                    }
                };
                statusDetailsContainer.setVisible(false);
                statusDetailsContainer.add(statusDetails);
            }
        };

        statusDetailsInfo.setReuseItems(true);
        container.add(statusDetailsInfo);
    }

    protected void onStatusDetailInfo(StatusDetailInfo statusDetailInfo) {

    }

    protected void onStatusDetail(StatusDetailInfo statusDetailInfo, StatusDetail statusDetail) {

    }

    public abstract List<StatusDetailInfo> loadStatusDetails();

    public void rebuild(){
        statusDetailsInfo.removeAll();
    }
}
