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

import static org.complitex.osznconnection.file.entity.RequestFileType.FACILITY_LOCAL;
import static org.complitex.osznconnection.file.service.process.ProcessType.*;

/**
 * @author inheaven on 017 17.11.16.
 */
@AuthorizeInstantiation("PRIVILEGE_LOCAL")
public class FacilityLocalFileList extends TemplatePage{
    @EJB
    private ProcessManagerBean processManagerBean;

    private AbstractFileListPanel fileListPanel;

    public FacilityLocalFileList() {
        add(new Label("title", new ResourceModel("title")));

        add(fileListPanel = new AbstractFileListPanel("fileListPanel", FACILITY_LOCAL,
                LOAD_FACILITY_LOCAL, BIND_FACILITY_LOCAL, FILL_FACILITY_LOCAL, SAVE_FACILITY_LOCAL) {

            @Override
            protected String getPreferencePage() {
                return FacilityLocalFileList.class.getName();
            }

            @Override
            protected Class<? extends WebPage> getItemListPageClass() {
                return FacilityLocalList.class;
            }

            @Override
            protected void load(Long serviceProviderId, Long userOrganizationId, Long organizationId, int year, int monthFrom, int monthTo) {
                processManagerBean.loadFacilityLocal(userOrganizationId, organizationId, year, monthFrom);
            }

            @Override
            protected void bind(List<Long> selectedFileIds, Map<Enum<?>, Object> parameters) {
            }

            @Override
            protected void fill(List<Long> selectedFileIds, Map<Enum<?>, Object> parameters) {
            }

            @Override
            protected void save(List<Long> selectedFileIds, Map<Enum<?>, Object> parameters) {
                processManagerBean.saveFacilityLocal(selectedFileIds, parameters);
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
