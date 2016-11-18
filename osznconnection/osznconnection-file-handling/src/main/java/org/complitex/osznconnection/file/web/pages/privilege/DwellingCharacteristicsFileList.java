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

import static org.complitex.osznconnection.file.entity.RequestFileType.DWELLING_CHARACTERISTICS;
import static org.complitex.osznconnection.file.service.process.ProcessType.*;

/**
 *
 * @author Artem
 */
@AuthorizeInstantiation(SecurityRole.AUTHORIZED)
public final class DwellingCharacteristicsFileList extends ScrollListPage {

    @EJB
    private ProcessManagerBean processManagerBean;

    @EJB
    private RequestFileBean requestFileBean;

    private final AbstractFileListPanel fileListPanel;

    public DwellingCharacteristicsFileList(PageParameters parameters) {
        super(parameters);

        add(new Label("title", new ResourceModel("title")));
        add(fileListPanel = new AbstractFileListPanel("fileListPanel", DWELLING_CHARACTERISTICS, LOAD_DWELLING_CHARACTERISTICS,
                BIND_DWELLING_CHARACTERISTICS, FILL_DWELLING_CHARACTERISTICS, SAVE_DWELLING_CHARACTERISTICS) {

            @Override
            protected String getPreferencePage() {
                return DwellingCharacteristicsFileList.class.getName();
            }

            @Override
            protected Class<? extends WebPage> getItemListPageClass() {
                return DwellingCharacteristicsList.class;
            }

            @Override
            protected void bind(List<Long> selectedFileIds, Map<Enum<?>, Object> commandParameters) {
                processManagerBean.bindDwellingCharacteristics(selectedFileIds, commandParameters);
            }

            @Override
            protected void fill(List<Long> selectedFileIds, Map<Enum<?>, Object> commandParameters) {
                processManagerBean.fillDwellingCharacteristics(selectedFileIds, commandParameters);
            }

            @Override
            protected void save(List<Long> selectedFileIds, Map<Enum<?>, Object> commandParameters) {
                processManagerBean.saveDwellingCharacteristics(selectedFileIds, commandParameters);
            }

            @Override
            protected void load(Long userOrganizationId, Long organizationId, int year, int monthFrom, int monthTo) {
                processManagerBean.loadDwellingCharacteristics(userOrganizationId, organizationId, year, monthFrom);
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
