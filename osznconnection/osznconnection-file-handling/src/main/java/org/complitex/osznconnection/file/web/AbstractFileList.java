package org.complitex.osznconnection.file.web;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.ResourceModel;
import org.complitex.osznconnection.file.entity.RequestFile;
import org.complitex.osznconnection.file.entity.RequestFileSubType;
import org.complitex.osznconnection.file.entity.RequestFileType;
import org.complitex.osznconnection.file.service.RequestFileBean;
import org.complitex.osznconnection.file.service.process.ProcessType;
import org.complitex.osznconnection.file.web.component.load.RequestFileLoadPanel;
import org.complitex.template.web.component.toolbar.ToolbarButton;
import org.complitex.template.web.template.TemplatePage;

import javax.ejb.EJB;
import java.util.List;
import java.util.Map;

/**
 * @author inheaven 09.12.16
 */
public abstract class AbstractFileList extends TemplatePage {
    @EJB
    private RequestFileBean requestFileBean;

    private AbstractFileListPanel fileListPanel;

    public AbstractFileList(RequestFileType requestFileType, RequestFileSubType requestFileSubType,
                            ProcessType loadProcessType, ProcessType bindProcessType,
                            ProcessType fillProcessType, ProcessType saveProcessType, Long[] osznOrganizationTypes) {
        add(new Label("title", new ResourceModel("title")));

        add(fileListPanel = new AbstractFileListPanel("fileListPanel", requestFileType, requestFileSubType,
                loadProcessType, bindProcessType, fillProcessType, saveProcessType, osznOrganizationTypes) {

            @Override
            protected String getPreferencePage() {
                return AbstractFileList.this.getClass().getName();
            }

            @Override
            protected Class<? extends WebPage> getItemListPageClass() {
                return AbstractFileList.this.getItemListPageClass();
            }

            @Override
            protected void load(Long serviceProviderId, Long userOrganizationId, Long organizationId,
                                int year, int monthFrom, int monthTo) {
                AbstractFileList.this.load(serviceProviderId, userOrganizationId, organizationId, year, monthFrom, monthTo);
            }

            @Override
            protected void bind(List<Long> selectedFileIds, Map<Enum<?>, Object> parameters) {
                AbstractFileList.this.bind(selectedFileIds, parameters);
            }

            @Override
            protected void fill(List<Long> selectedFileIds, Map<Enum<?>, Object> parameters) {
                AbstractFileList.this.fill(selectedFileIds, parameters);
            }

            @Override
            protected void save(List<Long> selectedFileIds, Map<Enum<?>, Object> parameters) {
                AbstractFileList.this.save(selectedFileIds, parameters);
            }

            @Override
            protected void export(AjaxRequestTarget target, List<Long> selectedFileIds) {
                AbstractFileList.this.export(target, selectedFileIds);
            }

            @Override
            protected void download(AjaxRequestTarget target, List<Long> selectedFileIds) {
                AbstractFileList.this.download(target, selectedFileIds);
            }

            @Override
            protected void delete(RequestFile requestFile) {
                AbstractFileList.this.delete(requestFile);
            }

            @Override
            protected RequestFileLoadPanel.MonthParameterViewMode getLoadMonthParameterViewMode() {
                return RequestFileLoadPanel.MonthParameterViewMode.EXACT;
            }

            @Override
            protected boolean isBindVisible() {
                return AbstractFileList.this.isBindVisible();
            }

            @Override
            protected boolean isFillVisible() {
                return AbstractFileList.this.isFillVisible();
            }

            @Override
            protected boolean isSaveVisible() {
                return AbstractFileList.this.isSaveVisible();
            }

            @Override
            protected boolean isExportVisible() {
                return AbstractFileList.this.isExportVisible();
            }

            @Override
            protected boolean isDownloadVisible() {
                return AbstractFileList.this.isDownloadVisible();
            }
        });
    }

    protected boolean isBindVisible() {
        return false;
    }

    protected boolean isFillVisible() {
        return false;
    }

    protected boolean isSaveVisible() {
        return true;
    }

    protected boolean isExportVisible(){
        return false;
    }

    protected boolean isDownloadVisible(){
        return false;
    }

    protected List<ToolbarButton> getToolbarButtons(String id) {
        return fileListPanel.getToolbarButtons(id);
    }

    protected abstract Class<? extends WebPage> getItemListPageClass();

    protected void load(Long serviceProviderId, Long userOrganizationId, Long organizationId,
                        int year, int monthFrom, int monthTo) {
    }

    protected void bind(List<Long> selectedFileIds, Map<Enum<?>, Object> parameters) {
    }

    protected void fill(List<Long> selectedFileIds, Map<Enum<?>, Object> parameters) {
    }

    protected void save(List<Long> selectedFileIds, Map<Enum<?>, Object> parameters) {
    }

    protected void export(AjaxRequestTarget target, List<Long> selectedFileIds) {
    }

    protected void download(AjaxRequestTarget target, List<Long> selectedFileIds) {
    }

    protected void delete(RequestFile requestFile){
        requestFileBean.delete(requestFile);
    }
}
