package org.complitex.osznconnection.file.web.pages.privilege;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.complitex.osznconnection.file.entity.RequestFile;
import org.complitex.osznconnection.file.entity.RequestFileFilter;
import org.complitex.osznconnection.file.entity.privilege.PrivilegeProlongation;
import org.complitex.osznconnection.file.service.RequestFileBean;
import org.complitex.osznconnection.file.service.process.ProcessManagerBean;
import org.complitex.osznconnection.file.web.AbstractFileListPanel;
import org.complitex.osznconnection.file.web.component.load.DateParameter;
import org.complitex.osznconnection.file.web.component.load.RequestFileLoadPanel;
import org.complitex.osznconnection.organization_type.strategy.OsznOrganizationTypeStrategy;
import org.complitex.template.web.component.toolbar.ToolbarButton;
import org.complitex.template.web.security.SecurityRole;
import org.complitex.template.web.template.TemplatePage;

import javax.ejb.EJB;
import java.util.List;
import java.util.Map;

import static org.complitex.osznconnection.file.entity.RequestFileType.PRIVILEGE_PROLONGATION;
import static org.complitex.osznconnection.file.service.process.ProcessType.*;

/**
 * @author inheaven on 28.06.16.
 */
@AuthorizeInstantiation(SecurityRole.AUTHORIZED)
public class PrivilegeProlongationFileList extends TemplatePage {
    @EJB
    private ProcessManagerBean processManagerBean;

    @EJB
    private RequestFileBean requestFileBean;

    private AbstractFileListPanel fileListPanel;

    public PrivilegeProlongationFileList(PageParameters parameters) {
        PrivilegeProlongation.TYPE type;

        switch (parameters.get("type").toString("s")){
            case "s":
                type = PrivilegeProlongation.TYPE.S;
                break;
            case "p" :
                type = PrivilegeProlongation.TYPE.P;
                break;

            default:
                type = PrivilegeProlongation.TYPE.S;
        }

        add(new Label("title", new ResourceModel("title")));
        add(fileListPanel = new AbstractFileListPanel("fileListPanel", PRIVILEGE_PROLONGATION,
                LOAD_PRIVILEGE_PROLONGATION, BIND_PRIVILEGE_PROLONGATION, FILL_PRIVILEGE_PROLONGATION,
                SAVE_PRIVILEGE_PROLONGATION) {

            @Override
            protected String getPreferencePage() {
                return PrivilegeProlongationFileList.class.getName();
            }

            @Override
            protected Class<? extends WebPage> getItemListPageClass() {
                return PrivilegeProlongationList.class;
            }

            @Override
            protected void bind(List<Long> selectedFileIds, Map<Enum<?>, Object> commandParameters) {
                processManagerBean.bindPrivilegeProlongation(selectedFileIds, commandParameters);
            }

            @Override
            protected void fill(List<Long> selectedFileIds, Map<Enum<?>, Object> commandParameters) {
            }

            @Override
            protected void save(List<Long> selectedFileIds, Map<Enum<?>, Object> commandParameters) {

            }

            @Override
            protected void load(long userOrganizationId, long osznId, DateParameter dateParameter) {
                processManagerBean.loadPrivilegeProlongation(type, userOrganizationId, osznId,
                        dateParameter.getMonth(), dateParameter.getYear());
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
            protected Long getCount(RequestFileFilter filter) {
                filter.setSubType(type.name());

                return super.getCount(filter);
            }

            @Override
            protected List<RequestFile> getObjects(RequestFileFilter filter) {
                filter.setSubType(type.name());

                return super.getObjects(filter);
            }
        });
    }

    protected List<ToolbarButton> getToolbarButtons(String id) {
        return fileListPanel.getToolbarButtons(id);
    }
}
