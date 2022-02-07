package ru.complitex.osznconnection.file.web.pages.subsidy;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.ResourceModel;
import ru.complitex.osznconnection.file.service.process.ProcessManagerService;
import ru.complitex.osznconnection.file.web.AbstractFileListPanel;
import ru.complitex.osznconnection.file.web.pages.actualpayment.ActualPaymentList;
import ru.complitex.osznconnection.organization_type.strategy.OsznOrganizationTypeStrategy;
import ru.complitex.template.web.component.toolbar.ToolbarButton;
import ru.complitex.template.web.template.TemplatePage;

import javax.ejb.EJB;
import java.util.List;
import java.util.Map;

import static ru.complitex.osznconnection.file.entity.RequestFileType.ACTUAL_PAYMENT;
import static ru.complitex.osznconnection.file.service.process.ProcessType.*;

@AuthorizeInstantiation("SUBSIDY_ACTUAL")
public class ActualPaymentFileList extends TemplatePage {

    @EJB
    private ProcessManagerService processManagerService;

    private final AbstractFileListPanel fileListPanel;

    public ActualPaymentFileList() {
        super();

        add(new Label("title", new ResourceModel("title")));
        add(fileListPanel = new AbstractFileListPanel("fileListPanel", ACTUAL_PAYMENT, LOAD_ACTUAL_PAYMENT,
                BIND_ACTUAL_PAYMENT, FILL_ACTUAL_PAYMENT, SAVE_ACTUAL_PAYMENT,
                new Long[]{OsznOrganizationTypeStrategy.SUBSIDY_DEPARTMENT_TYPE}) {

            @Override
            protected String getPreferencePage() {
                return ActualPaymentFileList.class.getName();
            }

            @Override
            protected Class<? extends WebPage> getItemListPageClass() {
                return ActualPaymentList.class;
            }

            @Override
            protected void bind(List<Long> selectedFileIds, Map<Enum<?>, Object> parameters) {
                processManagerService.bindActualPayment(selectedFileIds, parameters);
            }

            @Override
            protected void fill(List<Long> selectedFileIds, Map<Enum<?>, Object> parameters) {
                processManagerService.fillActualPayment(selectedFileIds, parameters);
            }

            @Override
            protected void save(List<Long> selectedFileIds, Map<Enum<?>, Object> parameters) {
                processManagerService.saveActualPayment(selectedFileIds, parameters);
            }

            @Override
            protected void load(Long serviceProviderId, Long userOrganizationId, Long organizationId,
                                int year, int monthFrom, int monthTo) {
                processManagerService.loadActualPayment(userOrganizationId, organizationId, year, monthFrom, monthTo);
            }
        });
    }

    @Override
    protected List<ToolbarButton> getToolbarButtons(String id) {
        return fileListPanel.getToolbarButtons(id);
    }
}
