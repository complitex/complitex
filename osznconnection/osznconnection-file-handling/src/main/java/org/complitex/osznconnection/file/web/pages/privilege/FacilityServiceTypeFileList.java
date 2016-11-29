package org.complitex.osznconnection.file.web.pages.privilege;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.complitex.osznconnection.file.service.RequestFileBean;
import org.complitex.osznconnection.file.service.process.ProcessManagerBean;
import org.complitex.osznconnection.file.web.AbstractFileListPanel;
import org.complitex.osznconnection.file.web.component.load.RequestFileLoadPanel.MonthParameterViewMode;
import org.complitex.osznconnection.organization_type.strategy.OsznOrganizationTypeStrategy;
import org.complitex.template.web.component.toolbar.ToolbarButton;
import org.complitex.template.web.pages.ScrollListPage;
import org.complitex.template.web.security.SecurityRole;

import javax.ejb.EJB;
import java.util.List;
import java.util.Map;

import static org.complitex.osznconnection.file.entity.RequestFileType.FACILITY_SERVICE_TYPE;
import static org.complitex.osznconnection.file.service.process.ProcessType.*;

/**
 *
 * @author Artem
 */
@AuthorizeInstantiation(SecurityRole.AUTHORIZED)
public final class FacilityServiceTypeFileList extends ScrollListPage {

    @EJB
    private ProcessManagerBean processManagerBean;

    @EJB
    private RequestFileBean requestFileBean;

    private final AbstractFileListPanel fileListPanel;

    public FacilityServiceTypeFileList(PageParameters parameters) {
        super(parameters);

        add(new Label("title", new ResourceModel("title")));
        add(fileListPanel = new AbstractFileListPanel("fileListPanel", FACILITY_SERVICE_TYPE, LOAD_FACILITY_SERVICE_TYPE,
                BIND_FACILITY_SERVICE_TYPE, FILL_FACILITY_SERVICE_TYPE, SAVE_FACILITY_SERVICE_TYPE) {

            @Override
            protected String getPreferencePage() {
                return FacilityServiceTypeFileList.class.getName();
            }

            @Override
            protected Class<? extends WebPage> getItemListPageClass() {
                return FacilityServiceTypeList.class;
            }

            @Override
            protected void bind(List<Long> selectedFileIds, Map<Enum<?>, Object> parameters) {
                processManagerBean.bindFacilityServiceType(selectedFileIds, parameters);
            }

            @Override
            protected void fill(List<Long> selectedFileIds, Map<Enum<?>, Object> parameters) {
                processManagerBean.fillFacilityServiceType(selectedFileIds, parameters);
            }

            @Override
            protected void save(List<Long> selectedFileIds, Map<Enum<?>, Object> parameters) {
                processManagerBean.saveFacilityServiceType(selectedFileIds, parameters);
            }

            @Override
            protected void load(Long serviceProviderId, Long userOrganizationId, Long organizationId, int year, int monthFrom, int monthTo) {
                processManagerBean.loadFacilityServiceType(userOrganizationId, organizationId, year, monthFrom);
            }

            @Override
            protected MonthParameterViewMode getLoadMonthParameterViewMode() {
                return MonthParameterViewMode.EXACT;
            }

            @Override
            protected Long[] getOsznOrganizationTypes() {
                return new Long[]{OsznOrganizationTypeStrategy.PRIVILEGE_DEPARTMENT_TYPE};
            }
        });
    }

    @Override
    protected List<ToolbarButton> getToolbarButtons(String id) {
        return fileListPanel.getToolbarButtons(id);
    }
}
