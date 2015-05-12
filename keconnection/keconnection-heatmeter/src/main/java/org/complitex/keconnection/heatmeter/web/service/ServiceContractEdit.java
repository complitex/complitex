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
import org.complitex.address.service.AddressRendererBean;
import org.complitex.address.strategy.building.BuildingStrategy;
import org.complitex.common.entity.DomainObjectFilter;
import org.complitex.common.web.component.LabelDateField;
import org.complitex.common.web.component.domain.DomainMultiselectPanel;
import org.complitex.common.web.component.organization.OrganizationIdPicker;
import org.complitex.keconnection.heatmeter.entity.ServiceContract;
import org.complitex.keconnection.heatmeter.entity.ServiceContractBuilding;
import org.complitex.keconnection.heatmeter.entity.ServiceContractService;
import org.complitex.keconnection.heatmeter.service.ServiceContractBean;
import org.complitex.organization.strategy.ServiceStrategy;
import org.complitex.template.web.security.SecurityRole;
import org.complitex.template.web.template.FormTemplatePage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;

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

    @EJB
    private BuildingStrategy buildingStrategy;

    @EJB
    private AddressRendererBean addressRendererBean;

    public ServiceContractEdit(PageParameters pageParameters) {
        Long id = pageParameters.get("id").toOptionalLong();

        add(new Label("title", new ResourceModel("title")));
        add(new FeedbackPanel("messages"));

        ServiceContract serviceContract = id != null
                ? serviceContractBean.getServiceContract(id)
                : new ServiceContract();

        //load buildingId
        serviceContract.getServiceContractBuildings()
                .forEach(b -> b.setBuildingObjectId(buildingStrategy.getBuildingCodeById(b.getBuildingCodeId()).getBuildingId()));

        final IModel<ServiceContract> model = new CompoundPropertyModel<>(serviceContract);

        Form<ServiceContract> form = new Form<>("form", model);
        add(form);

        form.add(new LabelDateField("beginDate", new PropertyModel<>(model, "beginDate"), true));
        form.add(new LabelDateField("endDate", new PropertyModel<>(model, "endDate"), true));
        form.add(new RequiredTextField<>("number"));
        form.add(new OrganizationIdPicker("serviceProviderId", new PropertyModel<>(model, "serviceProviderId"), SERVICE_PROVIDER));
        form.add(new OrganizationIdPicker("organizationId", new PropertyModel<>(model, "organizationId"),
                SERVICING_ORGANIZATION_TYPE));

        form.add(new DomainMultiselectPanel<ServiceContractService>("services", "service",
                new PropertyModel<>(model, "serviceContractServices"), "serviceObjectId") {
            @Override
            protected ServiceContractService newModelObject() {
                return new ServiceContractService(model.getObject().getId());
            }

            @Override
            protected String displayDomainObject(Long domainObjectId) {
                return domainObjectId != null
                        ? super.displayDomainObject(domainObjectId)
                        : getString("service_not_selected");
            }
        });

        form.add(new DomainMultiselectPanel<ServiceContractBuilding>("buildings", "building",
                new PropertyModel<>(model, "serviceContractBuildings"), "buildingObjectId") {
            @Override
            protected ServiceContractBuilding newModelObject() {
                return new ServiceContractBuilding(model.getObject().getId());
            }

            @Override
            protected void filter(DomainObjectFilter filter) {
                filter.addAdditionalParam(BuildingStrategy.P_SERVICING_ORGANIZATION_ID,
                        model.getObject().getOrganizationId());
            }

            @Override
            protected String displayDomainObject(Long domainObjectId) {

                return domainObjectId != null
                        ? addressRendererBean.displayBuildingSimple(domainObjectId, getLocale())
                        : getString("building_not_selected");
            }
        });

        form.add(new Button("save") {
            @Override
            public void onSubmit() {
                ServiceContract serviceContract = model.getObject();

                if (serviceContract.getServiceProviderId() == null) {
                    error(getString("error_serviceProviderId"));
                } else if (serviceContract.getOrganizationId() == null) {
                    error(getString("error_organizationId"));
                } else if (serviceContract.getBeginDate() == null) {
                    error(getString("error_beginDate"));
                }

                serviceContract.getServiceContractServices().stream()
                        .filter(s -> s.getServiceId() == null)
                        .findAny()
                        .ifPresent(s -> error(getString("error_service")));

                //building code
                serviceContract.getServiceContractBuildings().stream()
                        .filter(b -> b.getBuildingCodeId() == null)
                        .findAny()
                        .ifPresent(b -> {
                            if (b.getBuildingObjectId() != null){
                                b.setBuildingCodeId(buildingStrategy.getBuildingCodeId(
                                        serviceContract.getOrganizationId(), b.getBuildingObjectId()));
                            }

                            if (b.getBuildingCodeId() == null) {
                                error(getString("error_buildingCodeId"));
                            }
                        });

                if (!hasErrorMessage()) {
                    serviceContractBean.save(serviceContract);

                    info(getStringFormat("info_added"));
                    setResponsePage(ServiceContractList.class);
                }
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
