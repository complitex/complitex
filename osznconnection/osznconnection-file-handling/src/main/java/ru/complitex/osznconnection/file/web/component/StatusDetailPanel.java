package ru.complitex.osznconnection.file.web.component;

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
import ru.complitex.osznconnection.file.entity.RequestStatus;
import ru.complitex.osznconnection.file.entity.StatusDetail;
import ru.complitex.osznconnection.file.entity.StatusDetailInfo;
import ru.complitex.osznconnection.file.entity.example.AbstractRequestExample;
import ru.complitex.osznconnection.file.service.StatusRenderUtil;
import ru.complitex.osznconnection.file.service.status.details.ExampleConfigurator;
import ru.complitex.osznconnection.file.service.status.details.IStatusDetailRenderer;
import ru.complitex.osznconnection.file.service.status.details.StatusDetailRenderUtil;

import java.lang.reflect.ParameterizedType;
import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 24.11.10 15:49
 */
public abstract class StatusDetailPanel<T extends AbstractRequestExample> extends Panel {
    private ListView<StatusDetailInfo> statusDetailsInfo;

    public StatusDetailPanel(String id, IModel<T> exampleModel, ExampleConfigurator<T> exampleConfigurator,
                             IStatusDetailRenderer statusDetailRenderer, Component... update) {
        super(id);
        setOutputMarkupId(true);

        @SuppressWarnings("unchecked")
        Class<T> exampleClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];

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

                //Контейнер для ajax обновления вложенного списка
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

                        filterByStatusDetailInfo(statusDetailInfo, exampleClass, exampleModel, exampleConfigurator);

                        if (statusDetailInfo.getStatusDetails() != null && !statusDetailInfo.getStatusDetails().isEmpty()) {
                            statusDetailsContainer.setVisible(!statusDetailsContainer.isVisible());
                            target.add(statusDetailsContainer);
                        }
                    }
                };
                item.add(expand);

                String info = StatusRenderUtil.displayStatus(statusDetailInfo.getStatus(), getLocale())
                        + StatusDetailRenderUtil.displayCount(statusDetailInfo.getCount());
                expand.add(new Label("info", info));

                ListView<StatusDetail> statusDetails = new ListView<StatusDetail>("statusDetails",
                        statusDetailInfo.getStatusDetails()) {

                    @Override
                    protected void populateItem(ListItem<StatusDetail> item) {
                        final StatusDetail statusDetail = item.getModelObject();

                        AjaxLink filter = new AjaxLink("filter") {

                            @Override
                            public void onClick(AjaxRequestTarget target) {
                                filterByStatusDetail(statusDetail, statusDetailInfo.getStatus(), exampleModel, exampleConfigurator);

                                for (Component component : update) {
                                    target.add(component);
                                }
                            }
                        };
                        item.add(filter);

                        filter.add(new Label("name",
                                StatusDetailRenderUtil.displayStatusDetail(statusDetailInfo.getStatus(),
                                        statusDetail, statusDetailRenderer, getLocale())));
                    }
                };
                statusDetailsContainer.setVisible(false);
                statusDetailsContainer.add(statusDetails);
            }
        };

        statusDetailsInfo.setReuseItems(true);
        container.add(statusDetailsInfo);
    }

    protected void filterByStatusDetailInfo(StatusDetailInfo statusDetailInfo, Class<T> exampleClass, IModel<T> exampleModel,
                                            ExampleConfigurator<T> exampleConfigurator) {
        T example = exampleConfigurator.createExample(exampleClass, statusDetailInfo);
        example.setRequestFileId(exampleModel.getObject().getRequestFileId());
        exampleModel.setObject(example);
    }

    protected void filterByStatusDetail(StatusDetail statusDetail, RequestStatus requestStatus, IModel<T> exampleModel,
                                        ExampleConfigurator<T> exampleConfigurator) {
        T example = exampleConfigurator.createExample(statusDetail);
        example.setRequestFileId(exampleModel.getObject().getRequestFileId());
        example.setStatus(requestStatus);
        exampleModel.setObject(example);
    }

    public abstract List<StatusDetailInfo> loadStatusDetails();

    public void rebuild(){
        statusDetailsInfo.removeAll();
    }
}
