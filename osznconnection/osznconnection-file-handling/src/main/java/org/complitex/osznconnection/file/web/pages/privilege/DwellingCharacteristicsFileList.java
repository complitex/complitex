package org.complitex.osznconnection.file.web.pages.privilege;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.ResourceModel;
import org.complitex.osznconnection.file.service.RequestFileBean;
import org.complitex.osznconnection.file.service.process.ProcessManagerBean;
import org.complitex.osznconnection.file.web.AbstractFileListPanel;
import org.complitex.osznconnection.file.web.component.load.RequestFileLoadPanel.MonthParameterViewMode;
import org.complitex.osznconnection.organization_type.strategy.OsznOrganizationTypeStrategy;
import org.complitex.template.web.component.toolbar.ToolbarButton;
import org.complitex.template.web.security.SecurityRole;
import org.complitex.template.web.template.TemplatePage;

import javax.ejb.EJB;
import java.util.List;
import java.util.Map;

import static org.complitex.osznconnection.file.entity.RequestFileType.DWELLING_CHARACTERISTICS;
import static org.complitex.osznconnection.file.service.process.ProcessType.*;

@AuthorizeInstantiation(SecurityRole.AUTHORIZED)
public final class DwellingCharacteristicsFileList extends TemplatePage {

    @EJB
    private ProcessManagerBean processManagerBean;

    @EJB
    private RequestFileBean requestFileBean;

    private final AbstractFileListPanel fileListPanel;

    public DwellingCharacteristicsFileList() {
        add(new Label("title", new ResourceModel("title")));
        add(fileListPanel = new AbstractFileListPanel("fileListPanel", DWELLING_CHARACTERISTICS, LOAD_DWELLING_CHARACTERISTICS,
                BIND_DWELLING_CHARACTERISTICS, FILL_DWELLING_CHARACTERISTICS, SAVE_DWELLING_CHARACTERISTICS,
                new Long[]{OsznOrganizationTypeStrategy.PRIVILEGE_DEPARTMENT_TYPE}) {

            @Override
            protected String getPreferencePage() {
                return DwellingCharacteristicsFileList.class.getName();
            }

            @Override
            protected Class<? extends WebPage> getItemListPageClass() {
                return DwellingCharacteristicsList.class;
            }

            @Override
            protected void bind(List<Long> selectedFileIds, Map<Enum<?>, Object> parameters) {
                processManagerBean.bindDwellingCharacteristics(selectedFileIds, parameters);
            }

            @Override
            protected void fill(List<Long> selectedFileIds, Map<Enum<?>, Object> parameters) {
                processManagerBean.fillDwellingCharacteristics(selectedFileIds, parameters);
            }

            @Override
            protected void save(List<Long> selectedFileIds, Map<Enum<?>, Object> parameters) {
                processManagerBean.saveDwellingCharacteristics(selectedFileIds, parameters);
            }

            @Override
            protected void load(Long serviceProviderId, Long userOrganizationId, Long organizationId, int year, int monthFrom, int monthTo) {
                processManagerBean.loadDwellingCharacteristics(userOrganizationId, organizationId, year, monthFrom);
            }

            @Override
            protected MonthParameterViewMode getLoadMonthParameterViewMode() {
                return MonthParameterViewMode.EXACT;
            }
        });
    }

    @Override
    protected List<ToolbarButton> getToolbarButtons(String id) {
        return fileListPanel.getToolbarButtons(id);
    }
}
