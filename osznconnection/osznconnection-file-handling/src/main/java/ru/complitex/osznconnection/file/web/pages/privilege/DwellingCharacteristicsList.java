package ru.complitex.osznconnection.file.web.pages.privilege;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.authorization.UnauthorizedInstantiationException;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxLink;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.complitex.address.entity.AddressEntity;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.common.entity.PreferenceKey;
import ru.complitex.common.service.SessionBean;
import ru.complitex.common.strategy.organization.IOrganizationStrategy;
import ru.complitex.common.util.ExceptionUtil;
import ru.complitex.common.web.component.datatable.ArrowOrderByBorder;
import ru.complitex.common.web.component.datatable.DataProvider;
import ru.complitex.common.web.component.paging.PagingNavigator;
import ru.complitex.correction.web.address.component.AddressCorrectionDialog;
import ru.complitex.osznconnection.file.entity.AbstractAddressRequest;
import ru.complitex.osznconnection.file.entity.RequestFile;
import ru.complitex.osznconnection.file.entity.RequestStatus;
import ru.complitex.osznconnection.file.entity.StatusDetailInfo;
import ru.complitex.osznconnection.file.entity.example.PrivilegeExample;
import ru.complitex.osznconnection.file.entity.privilege.*;
import ru.complitex.osznconnection.file.service.AddressService;
import ru.complitex.osznconnection.file.service.RequestFileBean;
import ru.complitex.osznconnection.file.service.StatusRenderUtil;
import ru.complitex.osznconnection.file.service.privilege.*;
import ru.complitex.osznconnection.file.service.privilege.task.DwellingCharacteristicsBindTaskBean;
import ru.complitex.osznconnection.file.service.privilege.task.FacilityServiceTypeBindTaskBean;
import ru.complitex.osznconnection.file.service.status.details.PrivilegeExampleConfigurator;
import ru.complitex.osznconnection.file.service.status.details.PrivilegeStatusDetailRenderer;
import ru.complitex.osznconnection.file.service.status.details.StatusDetailBean;
import ru.complitex.osznconnection.file.service.warning.RequestWarningBean;
import ru.complitex.osznconnection.file.service.warning.WebWarningRenderer;
import ru.complitex.osznconnection.file.web.component.DataRowHoverBehavior;
import ru.complitex.osznconnection.file.web.component.StatusDetailPanel;
import ru.complitex.osznconnection.file.web.component.StatusRenderer;
import ru.complitex.osznconnection.organization.strategy.OsznOrganizationStrategy;
import ru.complitex.template.web.template.TemplatePage;
import ru.complitex.template.web.template.TemplateSession;

import javax.ejb.EJB;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static ru.complitex.osznconnection.file.entity.privilege.DwellingCharacteristicsDBF.CDUL;

@AuthorizeInstantiation("PRIVILEGE_GROUP")
public final class DwellingCharacteristicsList extends TemplatePage {
    private Logger log = LoggerFactory.getLogger(DwellingCharacteristicsList.class);

    @EJB
    private DwellingCharacteristicsBean dwellingCharacteristicsBean;

    @EJB
    private FacilityServiceTypeBean facilityServiceTypeBean;

    @EJB
    private RequestFileBean requestFileBean;

    @EJB
    private WebWarningRenderer webWarningRenderer;

    @EJB
    private StatusDetailBean statusDetailBean;

    @EJB
    private AddressService addressService;

    @EJB
    private SessionBean sessionBean;

    @EJB
    private DwellingCharacteristicsBindTaskBean dwellingCharacteristicsBindTaskBean;

    @EJB
    private RequestWarningBean requestWarningBean;


    @EJB
    private FacilityServiceTypeBindTaskBean facilityServiceTypeBindTaskBean;

    @EJB(name = IOrganizationStrategy.BEAN_NAME, beanInterface = IOrganizationStrategy.class)
    private OsznOrganizationStrategy organizationStrategy;

    @EJB
    private FacilityReferenceBookBean facilityReferenceBookBean;

    @EJB
    private PrivilegeFileGroupBean privilegeFileGroupBean;

    @EJB
    private PrivilegeGroupService privilegeGroupService;

    private IModel<PrivilegeExample> example;

    private Long fileId;

    private IModel<PrivilegeFileGroup> privilegeFileGroupModel;

    public DwellingCharacteristicsList(PageParameters params) {
        this.fileId = params.get("request_file_id").toLong();
        init();
    }

    private void clearExample() {
        example.setObject(newExample());
    }

    private PrivilegeExample newExample() {
        final PrivilegeExample e = new PrivilegeExample();
        e.setRequestFileId(fileId);
        return e;
    }

    private void init() {
        RequestFile requestFile = requestFileBean.getRequestFile(fileId);

        privilegeFileGroupModel = Model.of(privilegeFileGroupBean.getPrivilegeFileGroup(requestFile.getGroupId()));

        //Проверка доступа к данным
        if (!sessionBean.isAuthorized(requestFile.getUserOrganizationId())) {
            throw new UnauthorizedInstantiationException(this.getClass());
        }

        final DataRowHoverBehavior dataRowHoverBehavior = new DataRowHoverBehavior();
        add(dataRowHoverBehavior);

        String label = getStringFormat("label", requestFile.getDirectory(), File.separator,
                requestFile.getName());

        add(new Label("title", label));
        add(new Label("label", label));

        WebMarkupContainer content = new WebMarkupContainer("content");
        content.setOutputMarkupId(true);
        add(content);

        content.add(new FeedbackPanel("messages"));

        Form filterForm = new Form("filterForm");
        content.add(filterForm);
        example = new Model<>(((TemplateSession) getSession()).getPreferenceObject(getPreferencesPage() + fileId,
                PreferenceKey.FILTER_OBJECT, newExample()));

        StatusDetailPanel<PrivilegeExample> statusDetailPanel =
                new StatusDetailPanel<PrivilegeExample>("statusDetailsPanel", example,
                new PrivilegeExampleConfigurator(), new PrivilegeStatusDetailRenderer(), content) {

                    @Override
                    public List<StatusDetailInfo> loadStatusDetails() {
                        return statusDetailBean.getDwellingCharacteristicsStatusDetails(fileId);
                    }
                };
        add(statusDetailPanel);

        final DataProvider<DwellingCharacteristics> dataProvider = new DataProvider<DwellingCharacteristics>() {

            @Override
            protected Iterable<? extends DwellingCharacteristics> getData(long first, long count) {
                ((TemplateSession)getSession()).putPreferenceObject(getPreferencesPage() + fileId,
                        PreferenceKey.FILTER_OBJECT, example.getObject());

                example.getObject().setAsc(getSort().isAscending());
                if (!Strings.isEmpty(getSort().getProperty())) {
                    example.getObject().setOrderByClause(getSort().getProperty());
                }
                example.getObject().setFirst(first);
                example.getObject().setCount(count);
                return dwellingCharacteristicsBean.find(example.getObject());
            }

            @Override
            protected Long getSize() {
                example.getObject().setAsc(getSort().isAscending());
                return dwellingCharacteristicsBean.getCount(example.getObject());
            }
        };
        dataProvider.setSort("", SortOrder.ASCENDING);

        filterForm.add(new TextField<>("accountFilter", new PropertyModel<String>(example, "accountNumber")));
        filterForm.add(new TextField<>("idCodeFilter", new PropertyModel<>(example, "inn")));
        filterForm.add(new TextField<>("firstNameFilter", new PropertyModel<>(example, "firstName")));
        filterForm.add(new TextField<>("middleNameFilter", new PropertyModel<>(example, "middleName")));
        filterForm.add(new TextField<>("lastNameFilter", new PropertyModel<>(example, "lastName")));
        filterForm.add(new TextField<>("streetReferenceFilter", new Model<String>(){

            @Override
            public void setObject(String object) {
                FacilityStreet facilityStreet = new FacilityStreet();
                facilityStreet.putField(FacilityStreetDBF.KL_NAME, object);

                List<FacilityStreet> facilityStreets = facilityReferenceBookBean.getFacilityStreets(FilterWrapper.of(facilityStreet));

                if (!facilityStreets.isEmpty()){
                    example.getObject().setStreetCodes(facilityStreets.stream().map(AbstractAddressRequest::getStreetCode)
                            .collect(Collectors.toList()));
                }

                super.setObject(object);
            }
        }, String.class));

        filterForm.add(new TextField<>("buildingFilter", new PropertyModel<>(example, "building")));
        filterForm.add(new TextField<>("corpFilter", new PropertyModel<>(example, "corp")));
        filterForm.add(new TextField<>("apartmentFilter", new PropertyModel<>(example, "apartment")));
        filterForm.add(new DropDownChoice<>("statusFilter", new PropertyModel<>(example, "status"),
                Arrays.asList(RequestStatus.values()), new StatusRenderer()).setNullValid(true));

        AjaxLink<Void> reset = new AjaxLink<Void>("reset") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                filterForm.clearInput();
                clearExample();
                target.add(content);
            }
        };
        filterForm.add(reset);
        AjaxButton submit = new AjaxButton("submit", filterForm) {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                target.add(content);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
            }
        };
        filterForm.add(submit);

        //Панель коррекции адреса
        final AddressCorrectionDialog<DwellingCharacteristics> addressCorrectionDialog =
                new AddressCorrectionDialog<DwellingCharacteristics>("addressCorrectionPanel") {
                    @Override
                    protected void onCorrect(AjaxRequestTarget target, IModel<DwellingCharacteristics> model, AddressEntity addressEntity) {
                        dwellingCharacteristicsBean.markCorrected(model.getObject(), addressEntity);

                        PrivilegeFileGroup group = privilegeFileGroupModel.getObject();
                        if (group != null && group.getFacilityServiceTypeRequestFile() != null){
                            facilityServiceTypeBean.markCorrected(group.getFacilityServiceTypeRequestFile().getId(), model.getObject(), addressEntity);
                        }

                        target.add(content, statusDetailPanel);
                        dataRowHoverBehavior.deactivateDataRow(target);
                        statusDetailPanel.rebuild();
                    }
                };
        add(addressCorrectionDialog);

        //Панель поиска
        final DwellingCharacteristicsLookupPanel lookupPanel =
                new DwellingCharacteristicsLookupPanel("lookupPanel", content, statusDetailPanel) {

                    @Override
                    protected void onClose(AjaxRequestTarget target) {
                        super.onClose(target);
                        dataRowHoverBehavior.deactivateDataRow(target);
                    }
                };
        add(lookupPanel);

        CheckGroup<DwellingCharacteristics> checkGroup = new CheckGroup<>("checkGroup", new ArrayList<>());
        filterForm.add(checkGroup);

        DataView<DwellingCharacteristics> data = new DataView<DwellingCharacteristics>("data", dataProvider, 1) {

            @Override
            protected void populateItem(Item<DwellingCharacteristics> item) {
                final DwellingCharacteristics dwellingCharacteristics = item.getModelObject();

                item.add(new Check<>("check", Model.of(dwellingCharacteristics), checkGroup));
                item.add(new Label("account", dwellingCharacteristics.getAccountNumber()));
                item.add(new Label("idCode", dwellingCharacteristics.getInn()));
                item.add(new Label("firstName", dwellingCharacteristics.getFirstName()));
                item.add(new Label("middleName", dwellingCharacteristics.getMiddleName()));
                item.add(new Label("lastName", dwellingCharacteristics.getLastName()));

                String streetType = dwellingCharacteristics.getStreetType() != null
                        ? dwellingCharacteristics.getStreetType()
                        : "";

                String street = dwellingCharacteristics.getStreet() != null
                        ? dwellingCharacteristics.getStreet()
                        : "[" + dwellingCharacteristics.getStreetCode() + "]";

                item.add(new Label("streetReference", streetType + " " + street));
                item.add(new Label("building", dwellingCharacteristics.getStringField(DwellingCharacteristicsDBF.HOUSE)));
                item.add(new Label("corp", dwellingCharacteristics.getStringField(DwellingCharacteristicsDBF.BUILD)));
                item.add(new Label("apartment", dwellingCharacteristics.getStringField(DwellingCharacteristicsDBF.APT)));
                item.add(new Label("status", StatusRenderUtil.displayStatus(dwellingCharacteristics.getStatus(), getLocale())));
                item.add(new Label("statusDetails", webWarningRenderer.display(dwellingCharacteristics.getWarnings(), getLocale())));

                AjaxLink addressCorrectionLink = new IndicatingAjaxLink("addressCorrectionLink") {

                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        dwellingCharacteristics.setStreet(dwellingCharacteristics.getStreet() != null
                                ? dwellingCharacteristics.getStreet()
                                : getString("streetCodePrefix") + " " + dwellingCharacteristics.getStringField(CDUL));

                        dwellingCharacteristics.setStreetType(dwellingCharacteristics.getStreetType() != null
                                ? dwellingCharacteristics.getStreetType()
                                : getString("streetTypeNotFound"));

                        addressCorrectionDialog.open(target, item.getModel(), dwellingCharacteristics.getPersonalName(),
                                dwellingCharacteristics.getExternalAddress(), dwellingCharacteristics.getLocalAddress(),
                                dwellingCharacteristics.getStreetId() != null);
                    }
                };
                addressCorrectionLink.setVisible(dwellingCharacteristics.getStatus().isAddressCorrectable());
                item.add(addressCorrectionLink);

                AjaxLink lookup = new IndicatingAjaxLink("lookup") {

                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        lookupPanel.open(target, dwellingCharacteristics, null);
                    }
                };
                item.add(lookup);
            }
        };
        checkGroup.add(data);

        filterForm.add(new CheckGroupSelector("checkAll", checkGroup));
        filterForm.add(new ArrowOrderByBorder("accountHeader", DwellingCharacteristicsBean.OrderBy.ACCOUNT_NUMBER.getOrderBy(), dataProvider, data, content));
        filterForm.add(new ArrowOrderByBorder("idCodeHeader", DwellingCharacteristicsBean.OrderBy.IDPIL.getOrderBy(), dataProvider, data, content));
        filterForm.add(new ArrowOrderByBorder("firstNameHeader", DwellingCharacteristicsBean.OrderBy.FIRST_NAME.getOrderBy(), dataProvider, data, content));
        filterForm.add(new ArrowOrderByBorder("middleNameHeader", DwellingCharacteristicsBean.OrderBy.MIDDLE_NAME.getOrderBy(), dataProvider, data, content));
        filterForm.add(new ArrowOrderByBorder("lastNameHeader", DwellingCharacteristicsBean.OrderBy.LAST_NAME.getOrderBy(), dataProvider, data, content));
        filterForm.add(new ArrowOrderByBorder("streetReferenceHeader", DwellingCharacteristicsBean.OrderBy.STREET_REFERENCE.getOrderBy(), dataProvider, data, content));
        filterForm.add(new ArrowOrderByBorder("buildingHeader", DwellingCharacteristicsBean.OrderBy.BUILDING.getOrderBy(), dataProvider, data, content));
        filterForm.add(new ArrowOrderByBorder("corpHeader", DwellingCharacteristicsBean.OrderBy.CORP.getOrderBy(), dataProvider, data, content));
        filterForm.add(new ArrowOrderByBorder("apartmentHeader", DwellingCharacteristicsBean.OrderBy.APARTMENT.getOrderBy(), dataProvider, data, content));
        filterForm.add(new ArrowOrderByBorder("statusHeader", DwellingCharacteristicsBean.OrderBy.STATUS.getOrderBy(), dataProvider, data, content));

        Link<Void> back = new Link<Void>("back") {

            @Override
            public void onClick() {
                PageParameters params = new PageParameters();
                setResponsePage(PrivilegeFileGroupList.class, params);
            }
        };
        filterForm.add(back);

        content.add(new PagingNavigator("navigator", data, getPreferencesPage(), content));

        //Связать
        filterForm.add(new IndicatingAjaxButton("bind") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form form) {
                Collection<DwellingCharacteristics> list = checkGroup.getModelObject();

                list.forEach(dwellingCharacteristics -> {
                    //noinspection Duplicates
                    try {
                        String serviceProviderCode = organizationStrategy.getServiceProviderCode(requestFile.getEdrpou(),
                                requestFile.getOrganizationId(), requestFile.getUserOrganizationId());

                        Long billingId = organizationStrategy.getBillingId(requestFile.getUserOrganizationId());

                        requestWarningBean.delete(dwellingCharacteristics);
                        dwellingCharacteristicsBindTaskBean.bind(serviceProviderCode, billingId, dwellingCharacteristics);

                        if (dwellingCharacteristics.getStatus().equals(RequestStatus.ACCOUNT_NUMBER_RESOLVED)){
                            info(getStringFormat("info_bound", dwellingCharacteristics.getInn(), dwellingCharacteristics.getFio()));
                        }else {
                            error(getStringFormat("error_bound", dwellingCharacteristics.getFio(),
                                    StatusRenderUtil.displayStatus(dwellingCharacteristics.getStatus(), getLocale())));
                        }

                        PrivilegeGroup privilegeGroup = privilegeGroupService.getPrivilegeGroup(
                                dwellingCharacteristics.getRequestFileId(),
                                dwellingCharacteristics.getStringField(DwellingCharacteristicsDBF.IDPIL),
                                dwellingCharacteristics.getStringField(DwellingCharacteristicsDBF.PASPPIL),
                                dwellingCharacteristics.getStringField(DwellingCharacteristicsDBF.FIO),
                                dwellingCharacteristics.getStringField(DwellingCharacteristicsDBF.CDUL),
                                dwellingCharacteristics.getStringField(DwellingCharacteristicsDBF.HOUSE),
                                dwellingCharacteristics.getStringField(DwellingCharacteristicsDBF.BUILD),
                                dwellingCharacteristics.getStringField(DwellingCharacteristicsDBF.APT));

                        if (privilegeGroup.getFacilityServiceType() != null){
                            requestWarningBean.delete(privilegeGroup.getFacilityServiceType());
                            facilityServiceTypeBindTaskBean.bind(serviceProviderCode, billingId, privilegeGroup.getFacilityServiceType());
                        }
                    } catch (Exception e) {
                        error(ExceptionUtil.getCauseMessage(e, true));
                        log.error("error dwellingCharacteristics bind", e);
                    }
                });

                checkGroup.getModelObject().clear();
                target.add(content);
            }
        });
    }
}
