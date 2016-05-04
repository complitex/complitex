package org.complitex.osznconnection.file.web.pages.subsidy;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.complitex.osznconnection.file.service.RequestFileBean;
import org.complitex.osznconnection.file.service.process.ProcessManagerBean;
import org.complitex.osznconnection.file.web.AbstractFileListPanel;
import org.complitex.osznconnection.file.web.component.load.DateParameter;
import org.complitex.osznconnection.file.web.pages.actualpayment.ActualPaymentList;
import org.complitex.osznconnection.organization_type.strategy.OsznOrganizationTypeStrategy;
import org.complitex.template.web.component.toolbar.ToolbarButton;
import org.complitex.template.web.pages.ScrollListPage;
import org.complitex.template.web.security.SecurityRole;

import javax.ejb.EJB;
import java.util.List;
import java.util.Map;

import static org.complitex.osznconnection.file.entity.RequestFileType.ACTUAL_PAYMENT;
import static org.complitex.osznconnection.file.service.process.ProcessType.*;

@AuthorizeInstantiation(SecurityRole.AUTHORIZED)
public class ActualPaymentFileList extends ScrollListPage {

    @EJB
    private ProcessManagerBean processManagerBean;

    @EJB
    private RequestFileBean requestFileBean;

    private final AbstractFileListPanel fileListPanel;

    public ActualPaymentFileList(PageParameters parameters) {
        super(parameters);

        add(new Label("title", new ResourceModel("title")));
        add(fileListPanel = new AbstractFileListPanel("fileListPanel", ACTUAL_PAYMENT, LOAD_ACTUAL_PAYMENT,
                BIND_ACTUAL_PAYMENT, FILL_ACTUAL_PAYMENT, SAVE_ACTUAL_PAYMENT) {

            @Override
            protected String getPreferencePage() {
                return ActualPaymentFileList.class.getName();
            }

            @Override
            protected Class<? extends WebPage> getItemListPageClass() {
                return ActualPaymentList.class;
            }

            @Override
            protected void bind(List<Long> selectedFileIds, Map<Enum<?>, Object> commandParameters) {
                processManagerBean.bindActualPayment(selectedFileIds, commandParameters);
            }

            @Override
            protected void fill(List<Long> selectedFileIds, Map<Enum<?>, Object> commandParameters) {
                processManagerBean.fillActualPayment(selectedFileIds, commandParameters);
            }

            @Override
            protected void save(List<Long> selectedFileIds, Map<Enum<?>, Object> commandParameters) {
                processManagerBean.saveActualPayment(selectedFileIds, commandParameters);
            }

            @Override
            protected void load(long userOrganizationId, long osznId, DateParameter dateParameter) {
                processManagerBean.loadActualPayment(userOrganizationId, osznId,
                        dateParameter.getMonthFrom(), dateParameter.getMonthTo(), dateParameter.getYear());
            }

            @Override
            protected Long[] getOsznOrganizationTypes() {
                return new Long[]{OsznOrganizationTypeStrategy.SUBSIDY_DEPARTMENT_TYPE};
            }
        });
    }

    @Override
    protected List<ToolbarButton> getToolbarButtons(String id) {
        return fileListPanel.getToolbarButtons(id);
    }
}