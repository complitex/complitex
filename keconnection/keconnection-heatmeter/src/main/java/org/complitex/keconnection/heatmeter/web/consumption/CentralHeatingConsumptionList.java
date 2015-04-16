package org.complitex.keconnection.heatmeter.web.consumption;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.complitex.address.entity.AddressEntity;
import org.complitex.address.entity.ExternalAddress;
import org.complitex.common.entity.FilterWrapper;
import org.complitex.common.web.component.ajax.AjaxFeedbackPanel;
import org.complitex.common.web.component.datatable.Action;
import org.complitex.common.web.component.datatable.FilteredDataTable;
import org.complitex.correction.web.component.AddressCorrectionDialog;
import org.complitex.keconnection.heatmeter.entity.consumption.CentralHeatingConsumption;
import org.complitex.keconnection.heatmeter.entity.consumption.ConsumptionFile;
import org.complitex.keconnection.heatmeter.entity.consumption.ConsumptionStatus;
import org.complitex.keconnection.heatmeter.service.consumption.CentralHeatingConsumptionBean;
import org.complitex.keconnection.heatmeter.service.consumption.CentralHeatingConsumptionService;
import org.complitex.keconnection.heatmeter.service.consumption.ConsumptionFileBean;
import org.complitex.template.web.security.SecurityRole;
import org.complitex.template.web.template.TemplatePage;

import javax.ejb.EJB;
import java.util.ArrayList;
import java.util.List;

/**
 * @author inheaven on 17.03.2015 23:36.
 */
@AuthorizeInstantiation(SecurityRole.AUTHORIZED)
public class CentralHeatingConsumptionList extends TemplatePage{
    private final static String[] FIELDS = {"id", "number", "districtCode", "organizationCode", "buildingCode", "accountNumber",
            "street", "buildingNumber", "commonVolume", "apartmentRange", "beginDate", "endDate", "status", "message"};

    @EJB
    private ConsumptionFileBean consumptionFileBean;

    @EJB
    private CentralHeatingConsumptionBean centralHeatingConsumptionBean;

    @EJB
    private CentralHeatingConsumptionService centralHeatingConsumptionService;

    private FilteredDataTable<CentralHeatingConsumption> filteredDataTable;

    public CentralHeatingConsumptionList(PageParameters pageParameters) {
        ConsumptionFile consumptionFile = consumptionFileBean.getConsumptionFile(pageParameters.get("id").toLongObject());

        add(new Label("title", new ResourceModel("title")));

        //Feedback Panel
        AjaxFeedbackPanel messages = new AjaxFeedbackPanel("messages");
        add(messages);

        AddressCorrectionDialog<CentralHeatingConsumption> addressCorrectionDialog =
                new AddressCorrectionDialog<CentralHeatingConsumption>("addressCorrectionDialog") {
            @Override
            protected void onCorrect(AjaxRequestTarget target, IModel<CentralHeatingConsumption> model,
                                     AddressEntity addressEntity) {
                centralHeatingConsumptionService.bind(consumptionFile, model.getObject());

                target.add(filteredDataTable);
            }
        };
        add(addressCorrectionDialog);

        List<Action<CentralHeatingConsumption>> actions = new ArrayList<>();
        actions.add(new Action<CentralHeatingConsumption>("correct") {
            @Override
            public void onAction(AjaxRequestTarget target, IModel<CentralHeatingConsumption> model) {
                ExternalAddress externalAddress = model.getObject().getExternalAddress();
                externalAddress.setOrganizationId(consumptionFile.getServiceProviderId());
                externalAddress.setUserOrganizationId(consumptionFile.getUserOrganizationId());

                addressCorrectionDialog.open(target, model, null, externalAddress, model.getObject().getLocalAddress());
            }

            @Override
            public boolean isVisible(IModel<CentralHeatingConsumption> model) {
                return ConsumptionStatus.LOCAL_UNRESOLVED.contains(model.getObject().getStatus());
            }
        });

        add(filteredDataTable = new FilteredDataTable<CentralHeatingConsumption>("dataTable",
                CentralHeatingConsumption.class, null, actions, FIELDS) {
            @Override
            protected void onInit(FilterWrapper<CentralHeatingConsumption> filterWrapper) {
                filterWrapper.getObject().setConsumptionFileId(consumptionFile.getId());
            }

            @Override
            public List<CentralHeatingConsumption> getList(FilterWrapper<CentralHeatingConsumption> filterWrapper) {
                return centralHeatingConsumptionBean.getCentralHeatingConsumptions(filterWrapper);
            }

            @Override
            public Long getCount(FilterWrapper<CentralHeatingConsumption> filterWrapper) {
                return centralHeatingConsumptionBean.getCentralHeatingConsumptionsCount(filterWrapper);
            }
        });

        //ascending order
        filteredDataTable.getFilterWrapper().setAscending(true);

        add(new AjaxLink("back") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                setResponsePage(CentralHeatingConsumptionFileList.class);
            }
        });
    }
}
