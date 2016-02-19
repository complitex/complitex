package org.complitex.keconnection.heatmeter.web.consumption;

import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.protocol.ws.api.WebSocketBehavior;
import org.apache.wicket.protocol.ws.api.WebSocketRequestHandler;
import org.apache.wicket.protocol.ws.api.message.IWebSocketPushMessage;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.complitex.common.entity.FilterWrapper;
import org.complitex.common.entity.WebSocketPushMessage;
import org.complitex.common.web.component.ajax.AjaxFeedbackPanel;
import org.complitex.common.web.component.datatable.BookmarkablePageLinkColumn;
import org.complitex.common.web.component.datatable.FilteredDataTable;
import org.complitex.common.web.component.datatable.column.CheckColumn;
import org.complitex.common.web.component.domain.DomainObjectFilteredColumn;
import org.complitex.common.web.component.organization.OrganizationFilteredColumn;
import org.complitex.keconnection.heatmeter.entity.consumption.ConsumptionFile;
import org.complitex.keconnection.heatmeter.service.consumption.CentralHeatingConsumptionService;
import org.complitex.keconnection.heatmeter.service.consumption.ConsumptionFileBean;
import org.complitex.template.web.component.toolbar.DeleteItemButton;
import org.complitex.template.web.component.toolbar.ToolbarButton;
import org.complitex.template.web.component.toolbar.UploadButton;
import org.complitex.template.web.template.TemplatePage;

import javax.ejb.EJB;
import java.util.*;

import static org.complitex.organization_type.strategy.OrganizationTypeStrategy.SERVICE_PROVIDER_TYPE;
import static org.complitex.organization_type.strategy.OrganizationTypeStrategy.USER_ORGANIZATION_TYPE;

/**
 * @author inheaven on 17.03.2015 23:16.
 */
public abstract class AbstractConsumptionFileList extends TemplatePage{
    private final static String[] FIELDS = {"select", "name", "om", "serviceProviderId", "serviceId", "userOrganizationId",
            "loaded", "status"};

    @EJB
    private ConsumptionFileBean consumptionFileBean;

    private ConsumptionFileUploadDialog consumptionFileUploadDialog;

    private FilteredDataTable<ConsumptionFile> filteredDataTable;

    private AjaxFeedbackPanel messages;

    public AbstractConsumptionFileList() {
        add(new Label("title", new ResourceModel("title")));

        //messages
        messages = new AjaxFeedbackPanel("messages");
        add(messages);

        Map<String, IColumn<ConsumptionFile, String>> columnMap = new HashMap<>();

        columnMap.put("select", new CheckColumn<>());
        columnMap.put("name", new BookmarkablePageLinkColumn<ConsumptionFile>("name", getViewPage()) {
            @Override
            protected PageParameters getPageParameters(IModel<ConsumptionFile> rowModel) {
                return AbstractConsumptionFileList.this.getViewPageParameters(rowModel);
            }
        });
        columnMap.put("serviceProviderId", new OrganizationFilteredColumn<>("serviceProviderId", getLocale(), SERVICE_PROVIDER_TYPE));
        columnMap.put("serviceId", new DomainObjectFilteredColumn<>("service", "serviceId", getLocale()));
        columnMap.put("userOrganizationId", new OrganizationFilteredColumn<>("userOrganizationId", getLocale(), USER_ORGANIZATION_TYPE));

        add(filteredDataTable = new FilteredDataTable<ConsumptionFile>("dataTable", ConsumptionFile.class, columnMap, null, FIELDS) {
            @Override
            public List<ConsumptionFile> getList(FilterWrapper<ConsumptionFile> filterWrapper) {
                return AbstractConsumptionFileList.this.getList(filterWrapper);
            }

            @Override
            public Long getCount(FilterWrapper<ConsumptionFile> filterWrapper) {
                return AbstractConsumptionFileList.this.getCount(filterWrapper);
            }
        });

        add(consumptionFileUploadDialog = new ConsumptionFileUploadDialog("consumptionFileUploadDialog"){
            @Override
            protected void onUpload(AjaxRequestTarget target,Date om, Long serviceProviderId, Long serviceId,
                                    Long userOrganizationId, FileUploadField fileUploadField) {
                AbstractConsumptionFileList.this.onUpload(target, om, serviceProviderId, serviceId,
                        userOrganizationId, fileUploadField);
            }
        });

        add(new AjaxLink("bind") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                onBind(target, filteredDataTable.getCheckGroupModel().getObject());
            }
        });

        //broadcast
        add(new WebSocketBehavior() {
            @Override
            protected void onPush(WebSocketRequestHandler handler, IWebSocketPushMessage message) {
                if (message instanceof WebSocketPushMessage){
                    WebSocketPushMessage m = (WebSocketPushMessage) message;

                    if (CentralHeatingConsumptionService.class.getName().equals(m.getService())){
                        if (m.getPayload() instanceof ConsumptionFile){
                            ConsumptionFile consumptionFile = (ConsumptionFile) m.getPayload();
                            String name = consumptionFile.getName();

                            switch (consumptionFile.getStatus()){
                                case LOADED:
                                    info(getStringFormat("info_loaded", name));
                                    break;
                                case LOADING:
                                    info(getStringFormat("info_loading", name));
                                    break;
                                case LOAD_ERROR:
                                    error(getStringFormat("error_load", name));
                                    break;
                                case BINDING:
                                    info(getStringFormat("info_binding", name));
                                    break;
                                case BOUND:
                                    info(getStringFormat("info_bind", name));
                                    break;
                                case BIND_ERROR:
                                    info(getStringFormat("error_bind", name));
                                    break;
                            }

                            handler.add(messages);
                            handler.add(filteredDataTable);

                            filteredDataTable.getCheckGroupModel().getObject().remove(consumptionFile);
                        }
                    }
                }
            }
        });
    }

    @Override
    protected List<ToolbarButton> getToolbarButtons(String id) {
        return Arrays.asList(
                new UploadButton(id) {

                    @Override
                    protected void onClick(AjaxRequestTarget target) {
                        consumptionFileUploadDialog.open(target);
                    }
                },
                new DeleteItemButton(id, true){
                    @Override
                    protected void onClick(AjaxRequestTarget target) {
                        filteredDataTable.getCheckGroupModel().getObject().forEach(c -> {
                            try {
                                consumptionFileBean.delete(c.getId());

                                info(getStringFormat("info_delete", c.getName()));
                            } catch (Exception e) {
                                error(getStringFormat("error_delete", c.getName()));
                            }
                        });

                        target.add(getFilteredDataTable());
                        target.add(getMessages());
                    }
                });
    }

    public FilteredDataTable getFilteredDataTable() {
        return filteredDataTable;
    }

    public AjaxFeedbackPanel getMessages() {
        return messages;
    }

    protected abstract Class<? extends Page> getViewPage();

    protected abstract PageParameters getViewPageParameters(IModel<ConsumptionFile> model);

    protected abstract List<ConsumptionFile> getList(FilterWrapper<ConsumptionFile> filterWrapper);

    protected abstract Long getCount(FilterWrapper<ConsumptionFile> filterWrapper);

    protected abstract void onUpload(AjaxRequestTarget target, Date om, Long serviceProviderId, Long serviceId,
                                     Long userOrganizationId, FileUploadField fileUploadField);

    protected abstract void onBind(AjaxRequestTarget target, List<ConsumptionFile> consumptionFiles);
}
