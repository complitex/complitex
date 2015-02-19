package org.complitex.keconnection.heatmeter.web.service;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.complitex.common.web.component.LabelDateField;
import org.complitex.common.web.component.domain.DomainMultiselectPanel;
import org.complitex.common.web.component.organization.OrganizationIdPicker;
import org.complitex.keconnection.heatmeter.entity.ServiceContract;
import org.complitex.keconnection.heatmeter.entity.ServiceContractService;
import org.complitex.keconnection.heatmeter.service.ServiceContractBean;
import org.complitex.keconnection.heatmeter.strategy.ServiceStrategy;
import org.complitex.template.web.security.SecurityRole;
import org.complitex.template.web.template.FormTemplatePage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import java.util.Date;
import java.util.List;

import static org.complitex.keconnection.organization_type.strategy.KeConnectionOrganizationTypeStrategy.SERVICE_PROVIDER;
import static org.complitex.organization_type.strategy.OrganizationTypeStrategy.SERVICING_ORGANIZATION_TYPE;


/**
 * inheaven on 17.11.2014 20:52.
 */
@AuthorizeInstantiation(SecurityRole.ADMIN_MODULE_EDIT)
public class ServiceContractEdit extends FormTemplatePage {
    private final Logger log = LoggerFactory.getLogger(ServiceContractEdit.class);

    @EJB
    private ServiceContractBean serviceContractBean;

    @EJB
    private ServiceStrategy serviceStrategy;

    public ServiceContractEdit(PageParameters pageParameters) {
        Long id = pageParameters.get("id").toOptionalLong();

        add(new Label("title", new ResourceModel("title")));
        add(new FeedbackPanel("messages"));

        ServiceContract serviceContract = id != null
                ? serviceContractBean.getServiceContract(id)
                : new ServiceContract();

        final IModel<ServiceContract> model = new CompoundPropertyModel<>(serviceContract);

        Form<ServiceContract> form = new Form<>("form", model);
        add(form);

        form.add(new LabelDateField("beginDate", new PropertyModel<Date>(model, "beginDate"), true));
        form.add(new LabelDateField("endDate", new PropertyModel<Date>(model, "endDate"), true));
        form.add(new RequiredTextField<>("number"));
        form.add(new OrganizationIdPicker("organizationId", new PropertyModel<Long>(model, "organizationId"), SERVICE_PROVIDER));
        form.add(new OrganizationIdPicker("servicingOrganizationId", new PropertyModel<Long>(model, "servicingOrganizationId"), SERVICING_ORGANIZATION_TYPE));

        form.add(new DomainMultiselectPanel<ServiceContractService>("services", "service",
                new PropertyModel<List<ServiceContractService>>(model, "serviceContractServices"), "serviceObjectId") {
            @Override
            protected ServiceContractService newModelObject() {
                return new ServiceContractService(model.getObject().getId());
            }
        });

        form.add(new Button("save"){
            @Override
            public void onSubmit() {
                ServiceContract serviceContract = model.getObject();

                if (serviceContract.getOrganizationId() == null){
                    error(getString("error_organizationId"));
                    return;
                }else if (serviceContract.getServicingOrganizationId() == null){
                    error(getString("error_servicingOrganizationId"));
                    return;
                }else if (serviceContract.getBeginDate() == null){
                    error(getString("error_beginDate"));
                    return;
                }

                serviceContractBean.save(serviceContract);

                info(getStringFormat("info_added"));
                setResponsePage(ServiceContractList.class);
            }
        });

        form.add(new Link("cancel") {

            @Override
            public void onClick() {
                setResponsePage(ServiceContractList.class);
            }
        });
    }
}
