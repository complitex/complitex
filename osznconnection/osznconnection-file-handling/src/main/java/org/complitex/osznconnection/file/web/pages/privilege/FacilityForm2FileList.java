package org.complitex.osznconnection.file.web.pages.privilege;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.ResourceModel;
import org.complitex.osznconnection.file.service.process.ProcessManagerBean;
import org.complitex.osznconnection.file.web.AbstractFileListPanel;
import org.complitex.osznconnection.file.web.component.load.RequestFileLoadPanel;
import org.complitex.osznconnection.organization_type.strategy.OsznOrganizationTypeStrategy;
import org.complitex.template.web.component.toolbar.ToolbarButton;
import org.complitex.template.web.template.TemplatePage;

import javax.ejb.EJB;
import java.util.List;
import java.util.Map;

import static org.complitex.osznconnection.file.entity.RequestFileType.FACILITY_FORM2;
import static org.complitex.osznconnection.file.service.process.ProcessType.*;

@AuthorizeInstantiation("PRIVILEGE_FORM_2")
public final class FacilityForm2FileList extends TemplatePage {
    @EJB
    private ProcessManagerBean processManagerBean;

    private AbstractFileListPanel fileListPanel;

    public FacilityForm2FileList() {
        add(new Label("title", new ResourceModel("title")));

        add(fileListPanel = new AbstractFileListPanel("fileListPanel", FACILITY_FORM2,
                LOAD_FACILITY_FORM2, BIND_FACILITY_FORM2, FILL_FACILITY_FORM2, SAVE_FACILITY_FORM2) {

            @Override
            protected String getPreferencePage() {
                return FacilityForm2FileList.class.getName();
            }

            @Override
            protected Class<? extends WebPage> getItemListPageClass() {
                return FacilityForm2List.class;
            }

            @Override
            protected void load(Long serviceProviderId, Long userOrganizationId, Long organizationId, int year, int monthFrom, int monthTo) {
                processManagerBean.loadFacilityForm2(serviceProviderId, userOrganizationId, organizationId, year, monthFrom);
            }

            @Override
            protected void bind(List<Long> selectedFileIds, Map<Enum<?>, Object> parameters) {
            }

            @Override
            protected void fill(List<Long> selectedFileIds, Map<Enum<?>, Object> parameters) {
            }

            @Override
            protected void save(List<Long> selectedFileIds, Map<Enum<?>, Object> parameters) {
                processManagerBean.saveFacilityForm2(selectedFileIds, parameters);
            }

            @Override
            protected RequestFileLoadPanel.MonthParameterViewMode getLoadMonthParameterViewMode() {
                return RequestFileLoadPanel.MonthParameterViewMode.EXACT;
            }

            @Override
            protected Long[] getOsznOrganizationTypes() {
                return new Long[]{OsznOrganizationTypeStrategy.PRIVILEGE_DEPARTMENT_TYPE};
            }

            @Override
            protected boolean isBindVisible() {
                return false;
            }

            @Override
            protected boolean isFillVisible() {
                return false;
            }
        });
    }

    protected List<ToolbarButton> getToolbarButtons(String id) {
        return fileListPanel.getToolbarButtons(id);
    }
}
