package ru.complitex.eirc.registry.web.list;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.googlecode.wicket.jquery.ui.plugins.datepicker.DateRange;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.ajax.markup.html.AjaxLazyLoadPanel;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackHeadersToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.*;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.*;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.SharedResourceReference;
import org.apache.wicket.util.string.StringValue;
import ru.complitex.address.entity.AddressEntity;
import ru.complitex.address.entity.ExternalAddress;
import ru.complitex.address.entity.LocalAddress;
import ru.complitex.common.entity.DomainObject;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.common.entity.ILocalizedType;
import ru.complitex.common.entity.PersonalName;
import ru.complitex.common.service.ConfigBean;
import ru.complitex.common.util.AttributeUtil;
import ru.complitex.common.web.component.TextLabel;
import ru.complitex.common.web.component.ajax.AjaxFeedbackPanel;
import ru.complitex.common.web.component.ajax.AjaxFilterToolbar;
import ru.complitex.common.web.component.ajax.AjaxLinkLabel;
import ru.complitex.common.web.component.datatable.DataProvider;
import ru.complitex.common.web.component.paging.AjaxNavigationToolbar;
import ru.complitex.correction.service.AddressService;
import ru.complitex.correction.web.address.component.AddressCorrectionDialog;
import ru.complitex.template.web.component.toolbar.ToolbarButton;
import ru.complitex.template.web.security.SecurityRole;
import ru.complitex.template.web.template.TemplatePage;
import ru.complitex.eirc.dictionary.entity.EircConfig;
import ru.complitex.eirc.dictionary.entity.Person;
import ru.complitex.eirc.dictionary.strategy.ModuleInstanceStrategy;
import ru.complitex.eirc.dictionary.web.RangeDatePickerTextField;
import ru.complitex.eirc.registry.entity.*;
import ru.complitex.eirc.registry.service.*;
import ru.complitex.eirc.registry.service.handle.RegistryHandler;
import ru.complitex.eirc.registry.service.link.RegistryLinker;
import ru.complitex.eirc.registry.web.component.ColumnsPropertiesDialog;
import ru.complitex.eirc.registry.web.component.ContainerListPanel;
import ru.complitex.eirc.registry.web.component.IMessengerContainer;
import ru.complitex.eirc.registry.web.component.StatusDetailPanel;
import ru.complitex.eirc.service_provider_account.web.component.AbstractFilter;
import ru.complitex.eirc.service_provider_account.web.component.AjaxGoAndClearFilter;

import javax.ejb.EJB;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;

import static ru.complitex.eirc.dictionary.web.util.DateRangeUtil.getAllDateRange;
import static ru.complitex.eirc.dictionary.web.util.DateRangeUtil.setDate;

/**
 * @author Pavel Sknar
 */
@AuthorizeInstantiation(SecurityRole.AUTHORIZED)
public class RegistryRecordList extends TemplatePage {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
    private static final SimpleDateFormat FILTER_DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");

    static {
        FILTER_DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    @EJB
    private RegistryRecordBean registryRecordBean;

    @EJB
    private RegistryBean registryBean;

    @EJB
    private RegistryWorkflowManager registryWorkflowManager;

    @EJB
    private AddressService addressService;

    @EJB
    private RegistryLinker registryLinker;

    @EJB
    private RegistryHandler handler;

    @EJB
    private RegistryMessenger imessengerService;

    private AbstractMessenger imessenger;

    @EJB
    private RegistryFinishCallback finishCallbackService;

    private AbstractFinishCallback finishCallback;

    @EJB
    private ConfigBean configBean;

    @EJB
    private ModuleInstanceStrategy moduleInstanceStrategy;

    private CompoundPropertyModel<RegistryRecordData> filterModel = new CompoundPropertyModel<RegistryRecordData>(new RegistryRecord());

    private Registry registry;

    private IMessengerContainer container;

    private ColumnsPropertiesDialog columnsPropertiesDialog;

    public RegistryRecordList(PageParameters params) throws ExecutionException, InterruptedException {
        imessenger = imessengerService.getInstance();
        finishCallback = finishCallbackService.getInstance();

        StringValue registryIdParam = params.get("registryId");
        if (registryIdParam == null || registryIdParam.isEmpty()) {
            getSession().error(getString("error_registryId_not_found"));
            setResponsePage(RegistryList.class);
            return;
        }
        List<Registry> registries = registryBean.getRegistries(FilterWrapper.of(new Registry(registryIdParam.toLong())));
        if (registries.size() == 0) {
            getSession().error(getString("error_registry_not_found"));
            setResponsePage(RegistryList.class);
            return;
        }
        registry = registries.get(0);
        ((RegistryRecord)filterModel.getObject()).setRegistryId(registry.getId());
        init();
    }

    private void init() throws ExecutionException, InterruptedException {
        final IModel<DateRange> operationDateModel = new Model<>(getAllDateRange());

        IModel<String> labelModel = new ResourceModel("label");

        add(new Label("title", labelModel));
        add(new Label("label", labelModel));

        container = new IMessengerContainer("container", imessenger, finishCallback) {
            @Override
            protected boolean isCompleted() {
                return !registryWorkflowManager.isInWork(registry) || super.isCompleted();
            }
        };
        container.setOutputMarkupPlaceholderTag(true);
        container.setVisible(true);
        add(container);

        final AjaxFeedbackPanel messages = new AjaxFeedbackPanel("messages");
        container.add(messages);

        final IFilterStateLocator<RegistryRecordData> locator = new IFilterStateLocator<RegistryRecordData>() {
            @Override
            public RegistryRecordData getFilterState() {
                return filterModel.getObject();
            }

            @Override
            public void setFilterState(RegistryRecordData state) {
                filterModel.setObject(state);
            }
        };

        //Form
        final FilterForm<RegistryRecordData> filterForm = new FilterForm<>("filterForm", locator);
        filterForm.setOutputMarkupId(true);
        container.add(filterForm);

        AjaxLazyLoadPanel statusDetailPanel = new AjaxLazyLoadPanel("statusDetailPanel") {
            @Override
            public Component getLazyLoadComponent(String markupId) {
                return new StatusDetailPanel(markupId, filterModel, filterForm);
            }
        };
        statusDetailPanel.setOutputMarkupId(true);
        container.add(statusDetailPanel);

        Long moduleId = null;
        try {
            moduleId = configBean.getInteger(EircConfig.MODULE_ID, true).longValue();
        } catch (Exception e) {
            log().error("Can not get {} from config: {}", EircConfig.MODULE_ID, e.toString());
        }

        DomainObject module = moduleInstanceStrategy.getDomainObject(moduleId, true);

        final Integer userOrganizationId = AttributeUtil.getIntegerValue(module, ModuleInstanceStrategy.ORGANIZATION);

        //Панель коррекции адреса
        final AddressCorrectionDialog<RegistryRecordData> addressCorrectionDialog =
                new AddressCorrectionDialog<RegistryRecordData>("addressCorrectionPanel") {

                    @Override
                    protected void onCorrect(AjaxRequestTarget target, IModel<RegistryRecordData> model, AddressEntity addressEntity) {
                        registryLinker.linkAfterCorrection(model.getObject(), imessenger, finishCallback);

                        target.add(container);
                        showIMessages(target);
                    }
                };
        add(addressCorrectionDialog);


        final List<AbstractColumn<RegistryRecordData, String>> columns = Lists.newArrayList();
        columns.add(buildTextColumn("registry_record_service_code", "serviceCode"));
        columns.add(buildTextColumn("registry_record_personal_account_ext", "personalAccountExt"));
        columns.add(buildTextColumn("registry_record_city_type", "cityType"));
        columns.add(buildTextColumn("registry_record_city", "city"));
        columns.add(buildTextColumn("registry_record_street_type", "streetType"));
        columns.add(buildTextColumn("registry_record_street", "street"));
        columns.add(buildTextColumn("registry_record_building_number", "buildingNumber"));
        columns.add(buildTextColumn("registry_record_building_corp", "buildingCorp"));
        columns.add(buildTextColumn("registry_record_apartment", "apartment"));
        columns.add(buildTextColumn("registry_record_room", "room"));
        columns.add(buildFioColumn("registry_record_fio"));
        columns.add(buildDateColumn("registry_record_operation_date", "operationDate", operationDateModel));
        columns.add(buildTextColumn("registry_record_amount", "amount"));
        columns.add(buildContainerColumn("registry_record_containers"));
        columns.add(buildChoicesColumn("registry_record_import_error_type", "importErrorType", Arrays.asList(ImportErrorType.values())));
        columns.add(buildChoicesColumn("registry_record_status", "status", Arrays.asList(RegistryRecordStatus.values())));

        columns.add(
                new FilteredAbstractColumn<RegistryRecordData, String>(new StringResourceModel("empty", this, null), "action_skip") {
                    @Override
                    public boolean isSortable() {
                        return false;
                    }

                    @Override
                    public Component getFilter(String s, FilterForm<?> components) {
                        return new AjaxGoAndClearFilter(s, components, new ResourceModel("find"), new ResourceModel("reset")) {
                            @Override
                            public void onGoSubmit(AjaxRequestTarget target, Form<?> form) {
                                target.add(container);
                            }

                            @Override
                            public void onClearSubmit(AjaxRequestTarget target, Form<?> form) {
                                filterForm.clearInput();
                                Long registryId = filterModel.getObject().getRegistryId();

                                filterModel.setObject(new RegistryRecord(registryId));
                                operationDateModel.setObject(getAllDateRange());

                                target.add(container);
                            }
                        };
                    }

                    @Override
                    public void populateItem(Item<ICellPopulator<RegistryRecordData>> components, String s,
                                             IModel<RegistryRecordData> serviceProviderAccountIModel) {

                        final RegistryRecordData registryRecord = serviceProviderAccountIModel.getObject();
                        if (registryRecord.getStatus() == RegistryRecordStatus.LINKED_WITH_ERROR &&
                                registryRecord.getImportErrorType() != null &&
                                (registryRecord.getImportErrorType().getId() < 17 || registryRecord.getImportErrorType().getId() > 18) &&
                                registryWorkflowManager.canLink(registry)) {
                            AjaxLinkLabel addressCorrectionLink = new AjaxLinkLabel(s, new ResourceModel("correctAddress")) {

                                @Override
                                public void onClick(AjaxRequestTarget target) {
                                    ExternalAddress externalAddress = new ExternalAddress(registryRecord.getCity(),
                                            registryRecord.getStreetType(), registryRecord.getStreet(),
                                            registryRecord.getBuildingNumber(), registryRecord.getBuildingCorp(),
                                            registryRecord.getApartment(), registryRecord.getRoom(), -1L, -1L);

                                    LocalAddress localAddress = new LocalAddress(registryRecord.getCityId(),
                                            registryRecord.getStreetTypeId(), registryRecord.getStreetId(),
                                            registryRecord.getBuildingId(), registryRecord.getApartmentId(),
                                            registryRecord.getRoomId());

                                    PersonalName personalName = new PersonalName(registryRecord.getFirstName(),
                                            registryRecord.getMiddleName(), registryRecord.getLastName());

                                    addressCorrectionDialog.open(target, serviceProviderAccountIModel, personalName,
                                            localAddress.getFirstEmptyAddressEntity(), externalAddress, localAddress);
                                }
                            };
                            components.add(addressCorrectionLink);
                        } else if (registryRecord.getStatus() == RegistryRecordStatus.PROCESSED &&
                                registryWorkflowManager.canRollback(registry)) {
                            AjaxLinkLabel rollbackLink = new AjaxLinkLabel(s, new ResourceModel("rollback")) {

                                @Override
                                public void onClick(AjaxRequestTarget target) {
                                    initTimerBehavior();
                                    target.add(container);
                                    try {
                                        handler.rollbackRegistryRecords(registry, ImmutableList.of(registryRecord), imessenger, finishCallback);
                                    } catch (Exception e) {
                                        imessenger.addMessageError(e.getLocalizedMessage());
                                    } finally {
                                        showIMessages(target);
                                    }
                                }
                            };
                            components.add(rollbackLink);
                        } else {
                            components.add(new TextLabel(s, "").setVisible(false));
                        }

                    }
                });

        //Data Provider
        final DataProvider<RegistryRecordData> provider = new DataProvider<RegistryRecordData>() {

            @Override
            protected Iterable<? extends RegistryRecordData> getData(long first, long count) {
                FilterWrapper<RegistryRecordData> filterWrapper = FilterWrapper.of(filterModel.getObject(), first, count);
                filterWrapper.setAscending(getSort().isAscending());
                filterWrapper.setSortProperty(getSort().getProperty());
                setDate(filterWrapper, RegistryRecordBean.OPERATION_DATE_RANGE, operationDateModel.getObject());

                return registryRecordBean.getRegistryRecords(filterWrapper);
            }

            @Override
            protected Long getSize() {
                FilterWrapper<RegistryRecordData> filterWrapper = FilterWrapper.of(filterModel.getObject());
                setDate(filterWrapper, RegistryRecordBean.OPERATION_DATE_RANGE, operationDateModel.getObject());

                return registryRecordBean.getCount(filterWrapper);
            }
        };
        provider.setSort("registry_record_id", SortOrder.ASCENDING);

        final DataTable<RegistryRecordData, String> table = new DataTable<>("datatable", columns, provider, 10);
        table.setOutputMarkupId(true);
        table.setVersioned(false);
        table.addTopToolbar(new AjaxFallbackHeadersToolbar<>(table, provider));
        table.addTopToolbar(new AjaxFilterToolbar(table, filterForm, locator));
        table.addBottomToolbar(new AjaxNavigationToolbar(table));
        container.add(filterForm);
        filterForm.add(table);

        add(new Link("back") {
            @Override
            public void onClick() {
                setResponsePage(RegistryList.class);
            }
        });

        columnsPropertiesDialog = new ColumnsPropertiesDialog("columnsPropertiesDialog", table);
        add(columnsPropertiesDialog);
    }

    private FilteredAbstractColumn<RegistryRecordData, String> buildTextColumn(final String sortColumn, final String propertyName) {
        return new FilteredAbstractColumn<RegistryRecordData, String>(new StringResourceModel(sortColumn, this, null), sortColumn) {
            @Override
            public Component getFilter(String s, FilterForm<?> components) {
                return new TextFilter<>(s, filterModel.bind(propertyName), components);
            }

            @Override
            public void populateItem(Item<ICellPopulator<RegistryRecordData>> components, String s,
                                     IModel<RegistryRecordData> registryRecordIModel) {
                components.add(new Label(s, new PropertyModel<RegistryRecordData>(registryRecordIModel.getObject(), propertyName)));
            }
        };
    }

    private <T extends ILocalizedType> FilteredAbstractColumn<RegistryRecordData, String> buildChoicesColumn(final String sortColumn, final String propertyName, final List<T> choices) {
        return new FilteredAbstractColumn<RegistryRecordData, String>(new StringResourceModel(sortColumn, this, null), sortColumn) {
            @Override
            public Component getFilter(String s, FilterForm<?> components) {
                return new ChoiceFilter<T>(s, filterModel.<T>bind(propertyName), components, choices, new ChoiceRenderer<T>() {
                    @Override
                    public Object getDisplayValue(T object) {
                        return object.getLabel(getLocale());
                    }
                }, false);
            }

            @Override
            public void populateItem(Item<ICellPopulator<RegistryRecordData>> components, String s,
                                     IModel<RegistryRecordData> registryRecordIModel) {
                T object = new CompoundPropertyModel<>(registryRecordIModel.getObject()).<T>bind(propertyName).getObject();
                components.add(new Label(s, object != null? object.getLabel(getLocale()) : ""));
            }
        };
    }

    private FilteredAbstractColumn<RegistryRecordData, String> buildDateColumn(final String sortColumn, final String propertyName,
                                                                               final IModel<DateRange> dateRangeModel) {
        return new FilteredAbstractColumn<RegistryRecordData, String>(new StringResourceModel(sortColumn, this, null), sortColumn) {
            @Override
            public Component getFilter(String s, FilterForm<?> components) {
                return new AbstractFilter<DateRange>(s, components, dateRangeModel) {

                    @Override
                    protected Component createFilterComponent(String id, IModel<DateRange> model) {
                        return new RangeDatePickerTextField(id, model);
                    }
                };
            }

            @Override
            public void populateItem(Item<ICellPopulator<RegistryRecordData>> components, String s,
                                     IModel<RegistryRecordData> registryRecordIModel) {
                Date date = new CompoundPropertyModel<>(registryRecordIModel.getObject()).<Date>bind(propertyName).getObject();
                components.add(new Label(s, date != null ? DATE_FORMAT.format(date) : ""));
            }
        };
    }

    private FilteredAbstractColumn<RegistryRecordData, String> buildFioColumn(final String sortColumn) {
        return new FilteredAbstractColumn<RegistryRecordData, String>(new StringResourceModel(sortColumn, this, null), sortColumn) {
            @Override
            public Component getFilter(String s, FilterForm<?> components) {
                return new TextFilter<>(s, new Model<String>() {

                    @Override
                    public String getObject() {
                        Person person = filterModel.getObject().getPerson();
                        return person != null ? person.toString() : "";
                    }

                    @Override
                    public void setObject(String fio) {
                        RegistryRecord registryRecord = (RegistryRecord) filterModel.getObject();
                        if (StringUtils.isBlank(fio)) {
                            registryRecord.setLastName(null);
                            registryRecord.setFirstName(null);
                            registryRecord.setMiddleName(null);
                        } else {
                            fio = fio.trim();
                            String[] personFio = fio.split(" ", 3);

                            if (personFio.length > 0) {
                                registryRecord.setLastName(personFio[0]);
                            }
                            if (personFio.length > 1) {
                                registryRecord.setFirstName(personFio[1]);
                            } else {
                                registryRecord.setFirstName(null);
                            }
                            if (personFio.length > 2) {
                                registryRecord.setMiddleName(personFio[2]);
                            } else {
                                registryRecord.setMiddleName(null);
                            }

                        }
                    }
                }, components);
            }

            @Override
            public void populateItem(Item<ICellPopulator<RegistryRecordData>> components, String s,
                                     IModel<RegistryRecordData> registryRecordIModel) {
                Person person = registryRecordIModel.getObject().getPerson();
                components.add(new Label(s, person != null? person.toString() : ""));
            }
        };
    }

    private AbstractColumn<RegistryRecordData, String> buildContainerColumn(final String sortColumn) {
        return new AbstractColumn<RegistryRecordData, String>(new StringResourceModel(sortColumn, this, null), sortColumn) {

            @Override
            public void populateItem(Item<ICellPopulator<RegistryRecordData>> components, String s,
                                     IModel<RegistryRecordData> registryRecordIModel) {

                components.add(new ContainerListPanel<>(s, registryRecordIModel));
            }

            @Override
            public boolean isSortable() {
                return false;
            }
        };
    }

    private void initTimerBehavior() {
        container.initTimerBehavior();
    }

    private void showIMessages(AjaxRequestTarget target) {
        container.showIMessages(target);
    }

    @Override
    protected List<ToolbarButton> getToolbarButtons(String id) {
        return ImmutableList.of(
                new ToolbarButton(id, new SharedResourceReference("images/gear_blue.png"), "image.title.columns_properties", true) {
                    @Override
                    protected void onClick(AjaxRequestTarget target) {
                        columnsPropertiesDialog.open(target);
                    }
                }
        );
    }
}
