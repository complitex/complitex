package org.complitex.keconnection.heatmeter.web.consumption;

import com.google.common.collect.ImmutableList;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.complitex.address.entity.AddressEntity;
import org.complitex.address.entity.ExternalAddress;
import org.complitex.address.entity.LocalAddress;
import org.complitex.common.entity.FilterWrapper;
import org.complitex.common.util.ResourceUtil;
import org.complitex.common.web.component.ajax.AjaxFeedbackPanel;
import org.complitex.common.web.component.ajax.AjaxLinkLabel;
import org.complitex.common.web.component.datatable.Action;
import org.complitex.common.web.component.datatable.FilteredDataTable;
import org.complitex.correction.web.component.AddressCorrectionDialog;
import org.complitex.keconnection.heatmeter.entity.ConsumptionStatusFilter;
import org.complitex.keconnection.heatmeter.entity.consumption.CentralHeatingConsumption;
import org.complitex.keconnection.heatmeter.entity.consumption.ConsumptionFile;
import org.complitex.keconnection.heatmeter.entity.consumption.ConsumptionStatus;
import org.complitex.keconnection.heatmeter.entity.cursor.ComMeter;
import org.complitex.keconnection.heatmeter.entity.cursor.ComMeterCursor;
import org.complitex.keconnection.heatmeter.service.consumption.CentralHeatingConsumptionBean;
import org.complitex.keconnection.heatmeter.service.consumption.CentralHeatingConsumptionService;
import org.complitex.keconnection.heatmeter.service.consumption.ConsumptionFileBean;
import org.complitex.template.web.security.SecurityRole;
import org.complitex.template.web.template.TemplatePage;

import javax.ejb.EJB;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author inheaven on 17.03.2015 23:36.
 */
@AuthorizeInstantiation(SecurityRole.AUTHORIZED)
public class CentralHeatingConsumptionList extends TemplatePage{
    private final static String[] FIELDS = {"id", "number", "districtCode", "organizationCode", "buildingCode", "accountNumber",
            "street", "buildingNumber", "apartmentRange", "commonVolume", "beginDate", "endDate", "status", "message"};

    @EJB
    private ConsumptionFileBean consumptionFileBean;

    @EJB
    private CentralHeatingConsumptionBean centralHeatingConsumptionBean;

    @EJB
    private CentralHeatingConsumptionService centralHeatingConsumptionService;

    private FilteredDataTable<CentralHeatingConsumption> filteredDataTable;

    private IModel<Long> editRowModel = Model.of(-1L);

    public CentralHeatingConsumptionList(PageParameters pageParameters) {
        ConsumptionFile consumptionFile = consumptionFileBean.getConsumptionFile(pageParameters.get("id").toLongObject());

        add(new Label("title", new ResourceModel("title")));

        //messages
        AjaxFeedbackPanel messages = new AjaxFeedbackPanel("messages");
        add(messages);

        //status
        WebMarkupContainer statusContainer = new WebMarkupContainer("statusContainer");
        statusContainer.setOutputMarkupId(true);
        add(statusContainer);

        statusContainer.add(new ListView<ConsumptionStatusFilter>("statusFilter",
                new LoadableDetachableModel<List<? extends ConsumptionStatusFilter>>() {
                    @Override
                    protected List<? extends ConsumptionStatusFilter> load() {
                        return centralHeatingConsumptionBean.getStatusFilters(consumptionFile.getId());
                    }}) {
            @Override
            protected void populateItem(ListItem<ConsumptionStatusFilter> item) {
                ConsumptionStatusFilter statusFilter = item.getModelObject();

                String status = ResourceUtil.getString(ConsumptionStatus.class.getName(),
                        statusFilter.getStatus().name(), getLocale()) + " " +
                        Optional.ofNullable(statusFilter.getMessage()).orElse("") +
                        "  (" + statusFilter.getCount() + ") ";

                item.add(new AjaxLinkLabel("linkLabel", Model.of(status)) {
                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        filteredDataTable.getFilterWrapper().getObject().setStatus(statusFilter.getStatus());
                        target.add(filteredDataTable);
                    }
                });
            }
        });

        AddressCorrectionDialog<CentralHeatingConsumption> addressCorrectionDialog =
                new AddressCorrectionDialog<CentralHeatingConsumption>("addressCorrectionDialog") {
                    @Override
                    protected void onCorrect(AjaxRequestTarget target, IModel<CentralHeatingConsumption> model,
                                             AddressEntity addressEntity) {
                        CentralHeatingConsumption consumption = model.getObject();

                        //todo async rebind
                        centralHeatingConsumptionBean.getCentralHeatingConsumptions(FilterWrapper.of(
                                new CentralHeatingConsumption(consumption.getConsumptionFileId(), consumption.getStatus())))
                                .parallelStream()
                                .forEach(c -> centralHeatingConsumptionService.bind(consumptionFile, c));

                        target.add(filteredDataTable, statusContainer);
                    }

                    @Override
                    protected List<String> getFilters(AddressEntity addressEntity) {
                        switch (addressEntity) {
                            case STREET:
                                return ImmutableList.of("street");
                            case BUILDING:
                                return ImmutableList.of("street", "building");
                            case APARTMENT:
                                return ImmutableList.of("street", "building", "apartment");
                        }

                        return super.getFilters(addressEntity);
                    }
                };
        add(addressCorrectionDialog);

        ComMeterDialog comMeterDialog = new ComMeterDialog("comMeterDialog"){
            @Override
            protected void onSelect(ComMeter comMeter) {
                System.out.println(comMeter.getMId());
            }
        };
        add(comMeterDialog);

        //actions
        List<Action<CentralHeatingConsumption>> actions = new ArrayList<>();

        actions.add(new Action<CentralHeatingConsumption>("correct") {
            @Override
            public void onAction(AjaxRequestTarget target, IModel<CentralHeatingConsumption> model) {
                ExternalAddress externalAddress = model.getObject().getExternalAddress();
                externalAddress.setOrganizationId(consumptionFile.getServiceProviderId());
                externalAddress.setUserOrganizationId(consumptionFile.getUserOrganizationId());

                LocalAddress localAddress = model.getObject().getLocalAddress();

                AddressEntity addressEntity = localAddress.getFirstEmptyAddressEntity();

                if (addressEntity.equals(AddressEntity.CITY)){
                    addressEntity = AddressEntity.STREET;
                }

                addressCorrectionDialog.open(target, model, null, addressEntity, externalAddress, localAddress);
            }

            @Override
            public boolean isVisible(IModel<CentralHeatingConsumption> model) {
                return ConsumptionStatus.LOCAL_UNRESOLVED.contains(model.getObject().getStatus());
            }
        });

        actions.add(new Action<CentralHeatingConsumption>("search") {
            @Override
            public void onAction(AjaxRequestTarget target, IModel<CentralHeatingConsumption> model) {
                comMeterDialog.open(target, new ComMeterCursor());
            }
        });

        actions.add(new Action<CentralHeatingConsumption>("edit") {
            @Override
            public void onAction(AjaxRequestTarget target, IModel<CentralHeatingConsumption> model) {
                editRowModel.setObject(model.getObject().getId());

                target.add(filteredDataTable);
            }

            @Override
            public boolean isVisible(IModel<CentralHeatingConsumption> model) {
                return editRowModel.getObject().equals(-1L);
            }
        });

        actions.add(new Action<CentralHeatingConsumption>("save") {
            @Override
            public void onAction(AjaxRequestTarget target, IModel<CentralHeatingConsumption> model) {
                centralHeatingConsumptionBean.save(model.getObject());

                editRowModel.setObject(-1L);
                target.add(filteredDataTable);
            }

            @Override
            public boolean isVisible(IModel<CentralHeatingConsumption> model) {
                return editRowModel.getObject().equals(model.getObject().getId());
            }
        });


        actions.add(new Action<CentralHeatingConsumption>("cancel") {
            @Override
            public void onAction(AjaxRequestTarget target, IModel<CentralHeatingConsumption> model) {
                editRowModel.setObject(-1L);
                target.add(filteredDataTable);
            }

            @Override
            public boolean isVisible(IModel<CentralHeatingConsumption> model) {
                return editRowModel.getObject().equals(model.getObject().getId());
            }
        });

        //table
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

            @Override
            protected boolean isEdit(String field, IModel<CentralHeatingConsumption> rowModel) {
                if (field.equals("id")) {
                    return false;
                }

                return editRowModel.getObject().equals(rowModel.getObject().getId());
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
