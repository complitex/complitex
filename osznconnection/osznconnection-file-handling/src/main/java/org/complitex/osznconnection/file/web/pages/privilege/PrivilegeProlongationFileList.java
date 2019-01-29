package org.complitex.osznconnection.file.web.pages.privilege;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.complitex.osznconnection.file.entity.RequestFileSubType;
import org.complitex.osznconnection.file.service.process.ProcessManagerService;
import org.complitex.osznconnection.file.web.AbstractFileListPanel;
import org.complitex.osznconnection.file.web.component.load.RequestFileLoadPanel;
import org.complitex.osznconnection.organization_type.strategy.OsznOrganizationTypeStrategy;
import org.complitex.template.web.component.toolbar.ToolbarButton;
import org.complitex.template.web.template.TemplatePage;

import javax.ejb.EJB;
import java.util.List;
import java.util.Map;

import static org.complitex.osznconnection.file.entity.RequestFileType.PRIVILEGE_PROLONGATION;
import static org.complitex.osznconnection.file.service.process.ProcessType.*;

/**
 * @author inheaven on 28.06.16.
 */
@AuthorizeInstantiation({"PRIVILEGE_PROLONGATION_S", "PRIVILEGE_PROLONGATION_P"})
public class PrivilegeProlongationFileList extends TemplatePage {
    @EJB
    private ProcessManagerService processManagerService;

    private AbstractFileListPanel fileListPanel;

    public PrivilegeProlongationFileList(PageParameters parameters) {
        RequestFileSubType subType;

        switch (parameters.get("type").toString("s")){
            case "p" :
                subType = RequestFileSubType.PRIVILEGE_PROLONGATION_P;
                break;

            default:
                subType = RequestFileSubType.PRIVILEGE_PROLONGATION_S;
        }

        add(new Label("title", new ResourceModel("title")));
        add(fileListPanel = new AbstractFileListPanel("fileListPanel", PRIVILEGE_PROLONGATION, subType,
                LOAD_PRIVILEGE_PROLONGATION, BIND_PRIVILEGE_PROLONGATION, FILL_PRIVILEGE_PROLONGATION,
                SAVE_PRIVILEGE_PROLONGATION, new Long[]{OsznOrganizationTypeStrategy.PRIVILEGE_DEPARTMENT_TYPE}) {

            @Override
            protected String getPreferencePage() {
                return PrivilegeProlongationFileList.class.getName() + subType;
            }

            @Override
            protected Class<? extends WebPage> getItemListPageClass() {
                return PrivilegeProlongationList.class;
            }

            @Override
            protected void bind(List<Long> selectedFileIds, Map<Enum<?>, Object> parameters) {
                processManagerService.bindPrivilegeProlongation(selectedFileIds, parameters);
            }

            @Override
            protected void fill(List<Long> selectedFileIds, Map<Enum<?>, Object> parameters) {
            }

            @Override
            protected void save(List<Long> selectedFileIds, Map<Enum<?>, Object> parameters) {

            }

            @Override
            protected void load(Long serviceProviderId, Long userOrganizationId, Long organizationId, int year, int monthFrom, int monthTo) {
                processManagerService.loadPrivilegeProlongation(subType, userOrganizationId, organizationId, year, monthFrom);
            }

            @Override
            protected RequestFileLoadPanel.MonthParameterViewMode getLoadMonthParameterViewMode() {
                return RequestFileLoadPanel.MonthParameterViewMode.EXACT;
            }

            @Override
            protected void export(AjaxRequestTarget target, List<Long> selectedFileIds) {
                processManagerService.exportPrivilegeProlongation(selectedFileIds, null);
            }

            @Override
            protected boolean isExportVisible() {
                return true;
            }
        });
    }

    protected List<ToolbarButton> getToolbarButtons(String id) {
        return fileListPanel.getToolbarButtons(id);
    }
}
