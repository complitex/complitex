package org.complitex.keconnection.heatmeter.web.consumption;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.complitex.common.entity.FilterWrapper;
import org.complitex.common.web.component.ajax.AjaxFeedbackPanel;
import org.complitex.common.web.component.datatable.FilteredDataTable;
import org.complitex.keconnection.heatmeter.entity.consumption.CentralHeatingConsumption;
import org.complitex.keconnection.heatmeter.service.consumption.CentralHeatingConsumptionBean;
import org.complitex.template.web.security.SecurityRole;
import org.complitex.template.web.template.TemplatePage;

import javax.ejb.EJB;
import java.util.List;

/**
 * @author inheaven on 17.03.2015 23:36.
 */
@AuthorizeInstantiation(SecurityRole.AUTHORIZED)
public class CentralHeatingConsumptionList extends TemplatePage{
    private final static String[] FIELDS = {"id", "number", "districtCode", "organizationCode", "buildingCode", "accountNumber",
            "street", "buildingNumber", "commonVolume", "apartmentRange", "beginDate", "endDate", "commonArea",
            "meterVolume", "meterArea", "commonRentArea", "meterRentVolume", "meterRentArea", "noMeterArea",
            "noMeterRate", "rate", "noMeterVolume"};

    @EJB
    private CentralHeatingConsumptionBean centralHeatingConsumptionBean;

    public CentralHeatingConsumptionList(PageParameters pageParameters) {
        add(new Label("title", new ResourceModel("title")));

        //Feedback Panel
        final AjaxFeedbackPanel messages = new AjaxFeedbackPanel("messages");
        add(messages);

        add(new FilteredDataTable<CentralHeatingConsumption>("dataTable", CentralHeatingConsumption.class, FIELDS) {
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

        add(new AjaxLink("back") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                setResponsePage(CentralHeatingConsumptionFileList.class);
            }
        });
    }
}
