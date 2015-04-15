package org.complitex.keconnection.heatmeter.web.consumption;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.complitex.common.entity.FilterWrapper;
import org.complitex.common.web.component.ajax.AjaxFeedbackPanel;
import org.complitex.common.web.component.datatable.Action;
import org.complitex.common.web.component.datatable.FilteredDataTable;
import org.complitex.keconnection.heatmeter.entity.consumption.CentralHeatingConsumption;
import org.complitex.keconnection.heatmeter.entity.consumption.ConsumptionStatus;
import org.complitex.keconnection.heatmeter.service.consumption.CentralHeatingConsumptionBean;
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
            "street", "buildingNumber", "commonVolume", "apartmentRange", "beginDate", "endDate", "status"};

    @EJB
    private CentralHeatingConsumptionBean centralHeatingConsumptionBean;

    public CentralHeatingConsumptionList(PageParameters pageParameters) {
        add(new Label("title", new ResourceModel("title")));

        //Feedback Panel
        AjaxFeedbackPanel messages = new AjaxFeedbackPanel("messages");
        add(messages);

//        AddressCorrectionDialog addressCorrectionDialog = new AddressCorrectionDialog() {
//            @Override
//            protected void correctAddress(Object request, AddressEntity entity, Long cityId, Long streetTypeId,
//                                          Long streetId, Long buildingId, Long apartmentId, Long roomId,
//                                          Long userOrganizationId) throws DuplicateCorrectionException,
//                    MoreOneCorrectionException, NotFoundCorrectionException {
//
//            }
//        }


        List<Action<CentralHeatingConsumption>> actions = new ArrayList<>();
        actions.add(new Action<CentralHeatingConsumption>("correct") {
            @Override
            public void onAction(AjaxRequestTarget target, IModel<CentralHeatingConsumption> model) {
                super.onAction(target, model);
            }

            @Override
            public boolean isVisible(IModel<CentralHeatingConsumption> model) {
                return ConsumptionStatus.LOCAL_UNRESOLVED.contains(model.getObject().getStatus());
            }
        });

        FilteredDataTable<CentralHeatingConsumption> filteredDataTable;

        add(filteredDataTable = new FilteredDataTable<CentralHeatingConsumption>("dataTable",
                CentralHeatingConsumption.class, null, actions, FIELDS) {
            @Override
            public List<CentralHeatingConsumption> getList(FilterWrapper<CentralHeatingConsumption> filterWrapper) {
                filterWrapper.getObject().setConsumptionFileId(pageParameters.get("id").toLongObject());

                return centralHeatingConsumptionBean.getCentralHeatingConsumptions(filterWrapper);
            }

            @Override
            public Long getCount(FilterWrapper<CentralHeatingConsumption> filterWrapper) {
                filterWrapper.getObject().setConsumptionFileId(pageParameters.get("id").toLongObject());

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
