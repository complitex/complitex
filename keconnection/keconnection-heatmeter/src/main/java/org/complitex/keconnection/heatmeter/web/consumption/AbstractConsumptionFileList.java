package org.complitex.keconnection.heatmeter.web.consumption;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.complitex.common.entity.FilterWrapper;
import org.complitex.common.web.component.ajax.AjaxFeedbackPanel;
import org.complitex.common.web.component.datatable.Action;
import org.complitex.common.web.component.datatable.FilteredDataTable;
import org.complitex.common.web.component.domain.DomainObjectFilteredColumn;
import org.complitex.keconnection.heatmeter.entity.consumption.ConsumptionFile;
import org.complitex.template.web.component.toolbar.ToolbarButton;
import org.complitex.template.web.component.toolbar.UploadButton;
import org.complitex.template.web.template.TemplatePage;

import java.util.*;

/**
 * @author inheaven on 17.03.2015 23:16.
 */
public abstract class AbstractConsumptionFileList extends TemplatePage{
    private final static String[] FIELDS = {"name", "om", "serviceProviderId", "userOrganizationId", "status", "loaded"};

    private ConsumptionFileUploadDialog consumptionFileUploadDialog;

    private FilteredDataTable filteredDataTable;

    private AjaxFeedbackPanel messages;

    public AbstractConsumptionFileList() {
        add(new Label("title", new ResourceModel("title")));

        //Feedback Panel
        messages = new AjaxFeedbackPanel("messages");
        add(messages);

        List<Action<ConsumptionFile>> actions = new ArrayList<>();
        actions.add(new Action<ConsumptionFile>("view") {
            @Override
            public void onAction(AjaxRequestTarget target, IModel<ConsumptionFile> model) {
                onView(target, model);
            }
        });

        Map<String, IColumn<ConsumptionFile, String>> columnMap = new HashMap<>();

        columnMap.put("serviceProviderId", new DomainObjectFilteredColumn<>(
                "organization", "serviceProviderId", getLocale()));
        //todo user organization panel

        add(filteredDataTable = new FilteredDataTable<ConsumptionFile>("dataTable", ConsumptionFile.class, columnMap, actions, FIELDS) {
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
            protected void onUpload(AjaxRequestTarget target,Date om, Long serviceProviderId, Long serviceId, FileUploadField fileUploadField) {
                AbstractConsumptionFileList.this.onUpload(target, om, serviceProviderId, serviceId, fileUploadField);
            }
        });
    }

    @Override
    protected List<? extends ToolbarButton> getToolbarButtons(String id) {
        return Arrays.asList(
                new UploadButton(id) {

                    @Override
                    protected void onClick(AjaxRequestTarget target) {
                        consumptionFileUploadDialog.open(target);
                    }
                });
    }

    public FilteredDataTable getFilteredDataTable() {
        return filteredDataTable;
    }

    public AjaxFeedbackPanel getMessages() {
        return messages;
    }

    protected abstract void onView(AjaxRequestTarget target, IModel<ConsumptionFile> model);

    protected abstract List<ConsumptionFile> getList(FilterWrapper<ConsumptionFile> filterWrapper);

    protected abstract Long getCount(FilterWrapper<ConsumptionFile> filterWrapper);

    protected abstract void onUpload(AjaxRequestTarget target, Date om, Long serviceProviderId, Long serviceId, FileUploadField fileUploadField);
}
