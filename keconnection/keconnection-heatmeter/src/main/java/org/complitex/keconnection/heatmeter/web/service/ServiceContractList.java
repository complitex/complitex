package org.complitex.keconnection.heatmeter.web.service;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.complitex.common.entity.FilterWrapper;
import org.complitex.common.web.component.ajax.AjaxFeedbackPanel;
import org.complitex.common.web.component.datatable.Action;
import org.complitex.common.web.component.datatable.FilteredDataTable;
import org.complitex.common.web.component.domain.DomainObjectFilteredColumn;
import org.complitex.keconnection.heatmeter.entity.ServiceContract;
import org.complitex.keconnection.heatmeter.service.ServiceContractBean;
import org.complitex.template.web.component.toolbar.AddItemButton;
import org.complitex.template.web.component.toolbar.ToolbarButton;
import org.complitex.template.web.security.SecurityRole;
import org.complitex.template.web.template.TemplatePage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import java.util.*;

/**
 * inheaven on 13.11.2014 20:48.
 */
@AuthorizeInstantiation(SecurityRole.ADMIN_MODULE_EDIT)
public class ServiceContractList extends TemplatePage {
    private Logger log = LoggerFactory.getLogger(ServiceContractList.class);

    private final String[] FIELDS = {"id", "beginDate", "endDate", "number", "serviceProviderId", "organizationId"};

    @EJB
    private ServiceContractBean serviceContractBean;

    public ServiceContractList() {
        //Title
        add(new Label("title", new ResourceModel("title")));

        //Feedback Panel
        final AjaxFeedbackPanel messages = new AjaxFeedbackPanel("messages");
        add(messages);

        List<Action<ServiceContract>> actions = new ArrayList<>();
        actions.add(new Action<ServiceContract>("edit") {
            @Override
            public void onAction(AjaxRequestTarget target, IModel<ServiceContract> model) {
                setResponsePage(ServiceContractEdit.class, new PageParameters().add("id", model.getObject().getId()));
            }
        });

        Map<String, IColumn<ServiceContract, String>> columnMap = new HashMap<>();

        columnMap.put("serviceProviderId", new DomainObjectFilteredColumn<>(
                "organization", "serviceProviderId", getLocale()));
        columnMap.put("organizationId", new DomainObjectFilteredColumn<>(
                "organization", "organizationId", getLocale()));

        add(new FilteredDataTable<ServiceContract>("dataTable", ServiceContract.class, columnMap, actions, FIELDS) {
            @Override
            public List<ServiceContract> getList(FilterWrapper<ServiceContract> filterWrapper) {
                filterWrapper.setCamelToUnderscore(true);

                return serviceContractBean.getServiceContracts(filterWrapper);
            }

            @Override
            public Long getCount(FilterWrapper<ServiceContract> filterWrapper) {
                return serviceContractBean.getServiceContractsCount(filterWrapper);
            }
        });
    }

    @Override
    protected List<ToolbarButton> getToolbarButtons(String id) {
        return Arrays.asList(
                new AddItemButton(id) {

                    @Override
                    protected void onClick() {
                        setResponsePage(ServiceContractEdit.class);
                    }
                });
    }
}
