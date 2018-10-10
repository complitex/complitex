package org.complitex.osznconnection.file.web.pages.privilege;

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
import org.complitex.address.entity.AddressEntity;
import org.complitex.common.entity.FilterWrapper;
import org.complitex.common.entity.PreferenceKey;
import org.complitex.common.service.SessionBean;
import org.complitex.common.strategy.organization.IOrganizationStrategy;
import org.complitex.common.util.ExceptionUtil;
import org.complitex.common.web.component.datatable.ArrowOrderByBorder;
import org.complitex.common.web.component.datatable.DataProvider;
import org.complitex.common.web.component.paging.PagingNavigator;
import org.complitex.correction.web.address.component.AddressCorrectionDialog;
import org.complitex.osznconnection.file.entity.RequestFile;
import org.complitex.osznconnection.file.entity.RequestStatus;
import org.complitex.osznconnection.file.entity.StatusDetailInfo;
import org.complitex.osznconnection.file.entity.example.PrivilegeExample;
import org.complitex.osznconnection.file.entity.privilege.*;
import org.complitex.osznconnection.file.service.RequestFileBean;
import org.complitex.osznconnection.file.service.StatusRenderUtil;
import org.complitex.osznconnection.file.service.privilege.*;
import org.complitex.osznconnection.file.service.privilege.FacilityServiceTypeBean.OrderBy;
import org.complitex.osznconnection.file.service.privilege.task.DwellingCharacteristicsBindTaskBean;
import org.complitex.osznconnection.file.service.privilege.task.FacilityServiceTypeBindTaskBean;
import org.complitex.osznconnection.file.service.status.details.PrivilegeExampleConfigurator;
import org.complitex.osznconnection.file.service.status.details.PrivilegeStatusDetailRenderer;
import org.complitex.osznconnection.file.service.status.details.StatusDetailBean;
import org.complitex.osznconnection.file.service.warning.RequestWarningBean;
import org.complitex.osznconnection.file.service.warning.WebWarningRenderer;
import org.complitex.osznconnection.file.web.component.DataRowHoverBehavior;
import org.complitex.osznconnection.file.web.component.StatusDetailPanel;
import org.complitex.osznconnection.file.web.component.StatusRenderer;
import org.complitex.osznconnection.organization.strategy.OsznOrganizationStrategy;
import org.complitex.template.web.template.TemplatePage;
import org.complitex.template.web.template.TemplateSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.complitex.common.util.StringUtil.emptyOnNull;
import static org.complitex.osznconnection.file.entity.privilege.FacilityServiceTypeDBF.CDUL;

@AuthorizeInstantiation("PRIVILEGE_GROUP")
public final class FacilityServiceTypeList extends TemplatePage {
    private Logger log = LoggerFactory.getLogger(FacilityServiceTypeList.class);

    @EJB
    private FacilityServiceTypeBean facilityServiceTypeBean;

    @EJB
    private DwellingCharacteristicsBean dwellingCharacteristicsBean;

    @EJB
    private RequestFileBean requestFileBean;

    @EJB
    private WebWarningRenderer webWarningRenderer;

    @EJB
    private StatusDetailBean statusDetailBean;

    @EJB
    private SessionBean sessionBean;

    @EJB(name = IOrganizationStrategy.BEAN_NAME, beanInterface = IOrganizationStrategy.class)
    private OsznOrganizationStrategy organizationStrategy;

    @EJB
    private FacilityServiceTypeBindTaskBean facilityServiceTypeBindTaskBean;

    @EJB
    private RequestWarningBean requestWarningBean;


    @EJB
    private DwellingCharacteristicsBindTaskBean dwellingCharacteristicsBindTaskBean;

    @EJB
    private PrivilegeFileGroupBean privilegeFileGroupBean;

    @EJB
    private PrivilegeGroupService privilegeGroupService;

    @EJB
    private FacilityReferenceBookBean facilityReferenceBookBean;

    private IModel<PrivilegeExample> example;
    private long fileId;

    private IModel<PrivilegeFileGroup> privilegeFileGroupModel;


    public FacilityServiceTypeList(PageParameters params) {
        this.fileId = params.get("request_file_id").toLong();
        init();
    }

    private void clearExample() {
        example.setObject(newExample());
    }

    private PrivilegeExample newExample() {
        PrivilegeExample e = new PrivilegeExample();
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

        final WebMarkupContainer content = new WebMarkupContainer("content");
        content.setOutputMarkupId(true);
        add(content);

        content.add(new FeedbackPanel("messages"));

        final Form filterForm = new Form("filterForm");
        content.add(filterForm);
        example = new Model<>(((TemplateSession) getSession()).getPreferenceObject(getPreferencesPage() + fileId,
                PreferenceKey.FILTER_OBJECT, newExample()));

        StatusDetailPanel<PrivilegeExample> statusDetailPanel =
                new StatusDetailPanel<PrivilegeExample>("statusDetailsPanel", example,
                new PrivilegeExampleConfigurator(), new PrivilegeStatusDetailRenderer(), content) {

                    @Override
                    public List<StatusDetailInfo> loadStatusDetails() {
                        return statusDetailBean.getFacilityServiceTypeStatusDetails(fileId);
                    }
                };
        add(statusDetailPanel);

        final DataProvider<FacilityServiceType> dataProvider = new DataProvider<FacilityServiceType>() {

            @Override
            protected Iterable<? extends FacilityServiceType> getData(long first, long count) {
                ((TemplateSession)getSession()).putPreferenceObject(getPreferencesPage() + fileId,
                        PreferenceKey.FILTER_OBJECT, example.getObject());

                example.getObject().setAsc(getSort().isAscending());
                if (!Strings.isEmpty(getSort().getProperty())) {
                    example.getObject().setOrderByClause(getSort().getProperty());
                }
                example.getObject().setFirst(first);
                example.getObject().setCount(count);
                return facilityServiceTypeBean.find(example.getObject());
            }

            @Override
            protected Long getSize() {
                example.getObject().setAsc(getSort().isAscending());
                return facilityServiceTypeBean.getCount(example.getObject());
            }
        };
        dataProvider.setSort("", SortOrder.ASCENDING);

        filterForm.add(new TextField<>("accountFilter", new PropertyModel<String>(example, "accountNumber")));
        filterForm.add(new TextField<>("idCodeFilter", new PropertyModel<String>(example, "inn")));
        filterForm.add(new TextField<>("firstNameFilter", new PropertyModel<String>(example, "firstName")));
        filterForm.add(new TextField<>("middleNameFilter", new PropertyModel<String>(example, "middleName")));
        filterForm.add(new TextField<>("lastNameFilter", new PropertyModel<String>(example, "lastName")));

        filterForm.add(new TextField<>("streetReferenceFilter", new Model<String>(){
            @Override
            public String getObject() {
                String streetCode = example.getObject().getStreetCode();

                if (streetCode != null) {
                    FacilityStreet facilityStreet = facilityReferenceBookBean.getFacilityStreet(streetCode,
                            requestFile.getOrganizationId(), requestFile.getUserOrganizationId());

                    if (facilityStreet != null){
                        return facilityStreet.getStreet();
                    }
                }

                return null;
            }

            @Override
            public void setObject(String object) {
                FacilityStreet facilityStreet = new FacilityStreet();
                facilityStreet.putField(FacilityStreetDBF.KL_NAME, object);

                List<FacilityStreet> facilityStreets = facilityReferenceBookBean.getFacilityStreets(FilterWrapper.of(facilityStreet));

                if (!facilityStreets.isEmpty()){
                    example.getObject().setStreetCode(facilityStreets.get(0).getStreetCode());
                }
            }
        }, String.class));

        filterForm.add(new TextField<>("buildingFilter", new PropertyModel<String>(example, "building")));
        filterForm.add(new TextField<>("corpFilter", new PropertyModel<String>(example, "corp")));
        filterForm.add(new TextField<>("apartmentFilter", new PropertyModel<String>(example, "apartment")));
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
        final AddressCorrectionDialog<FacilityServiceType> addressCorrectionDialog =
                new AddressCorrectionDialog<FacilityServiceType>("addressCorrectionPanel") {

                    @Override
                    protected void onCorrect(AjaxRequestTarget target, IModel<FacilityServiceType> model, AddressEntity addressEntity) {
                        facilityServiceTypeBean.markCorrected(model.getObject(), addressEntity);

                        PrivilegeFileGroup group = privilegeFileGroupModel.getObject();
                        if (group != null && group.getDwellingCharacteristicsRequestFile() != null){
                            dwellingCharacteristicsBean.markCorrected(group.getDwellingCharacteristicsRequestFile().getId(), model.getObject(), addressEntity);
                        }

                        target.add(content, statusDetailPanel);
                        dataRowHoverBehavior.deactivateDataRow(target);
                        statusDetailPanel.rebuild();
                    }
                };
        add(addressCorrectionDialog);

        //Панель поиска
        final FacilityServiceTypeLookupPanel lookupPanel =
                new FacilityServiceTypeLookupPanel("lookupPanel", content, statusDetailPanel) {

                    @Override
                    protected void onClose(AjaxRequestTarget target) {
                        super.onClose(target);
                        dataRowHoverBehavior.deactivateDataRow(target);
                    }
                };
        add(lookupPanel);

        CheckGroup<FacilityServiceType> checkGroup = new CheckGroup<>("checkGroup", new ArrayList<>());
        filterForm.add(checkGroup);

        DataView<FacilityServiceType> data = new DataView<FacilityServiceType>("data", dataProvider, 1) {

            @Override
            protected void populateItem(Item<FacilityServiceType> item) {
                FacilityServiceType facilityServiceType = item.getModelObject();

                item.add(new Check<>("check", Model.of(facilityServiceType), checkGroup));
                item.add(new Label("account", facilityServiceType.getAccountNumber()));
                item.add(new Label("idCode", facilityServiceType.getInn()));
                item.add(new Label("firstName", facilityServiceType.getFirstName()));
                item.add(new Label("middleName", facilityServiceType.getMiddleName()));
                item.add(new Label("lastName", facilityServiceType.getLastName()));
                item.add(new Label("streetReference", emptyOnNull(facilityServiceType.getStreetType()) + " "
                        + emptyOnNull(facilityServiceType.getStreet())));
                item.add(new Label("building", facilityServiceType.getStringField(FacilityServiceTypeDBF.HOUSE)));
                item.add(new Label("corp", facilityServiceType.getStringField(FacilityServiceTypeDBF.BUILD)));
                item.add(new Label("apartment", facilityServiceType.getStringField(FacilityServiceTypeDBF.APT)));
                item.add(new Label("status", StatusRenderUtil.displayStatus(facilityServiceType.getStatus(), getLocale())));
                item.add(new Label("statusDetails", webWarningRenderer.display(facilityServiceType.getWarnings(), getLocale())));

                AjaxLink addressCorrectionLink = new IndicatingAjaxLink("addressCorrectionLink") {

                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        facilityServiceType.setStreetType(facilityServiceType.getStreetType() != null
                                ? facilityServiceType.getStreetType()
                                : getString("streetTypeNotFound"));

                        facilityServiceType.setStreet(facilityServiceType.getStreet() != null
                                ? facilityServiceType.getStreet()
                                : getString("streetCodePrefix") + " " + facilityServiceType.getStringField(CDUL));


                        addressCorrectionDialog.open(target, item.getModel(), facilityServiceType.getPersonalName(),
                                facilityServiceType.getExternalAddress(), facilityServiceType.getLocalAddress());
                    }
                };
                addressCorrectionLink.setVisible(facilityServiceType.getStatus().isAddressCorrectable());
                item.add(addressCorrectionLink);

                AjaxLink lookup = new IndicatingAjaxLink("lookup") {

                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        lookupPanel.open(target, facilityServiceType, null);
                    }
                };
                item.add(lookup);
            }
        };
        checkGroup.add(data);

        filterForm.add(new CheckGroupSelector("checkAll", checkGroup));
        filterForm.add(new ArrowOrderByBorder("accountHeader", OrderBy.RAH.getOrderBy(), dataProvider, data, content));
        filterForm.add(new ArrowOrderByBorder("idCodeHeader", OrderBy.IDPIL.getOrderBy(), dataProvider, data, content));
        filterForm.add(new ArrowOrderByBorder("firstNameHeader", OrderBy.FIRST_NAME.getOrderBy(), dataProvider, data, content));
        filterForm.add(new ArrowOrderByBorder("middleNameHeader", OrderBy.MIDDLE_NAME.getOrderBy(), dataProvider, data, content));
        filterForm.add(new ArrowOrderByBorder("lastNameHeader", OrderBy.LAST_NAME.getOrderBy(), dataProvider, data, content));
        filterForm.add(new ArrowOrderByBorder("streetReferenceHeader", OrderBy.STREET_REFERENCE.getOrderBy(), dataProvider, data, content));
        filterForm.add(new ArrowOrderByBorder("buildingHeader", OrderBy.BUILDING.getOrderBy(), dataProvider, data, content));
        filterForm.add(new ArrowOrderByBorder("corpHeader", OrderBy.CORP.getOrderBy(), dataProvider, data, content));
        filterForm.add(new ArrowOrderByBorder("apartmentHeader", OrderBy.APARTMENT.getOrderBy(), dataProvider, data, content));
        filterForm.add(new ArrowOrderByBorder("statusHeader", OrderBy.STATUS.getOrderBy(), dataProvider, data, content));

        Link<Void> back = new Link<Void>("back") {

            @Override
            public void onClick() {
                PageParameters params = new PageParameters();
                setResponsePage(PrivilegeFileGroupList.class, params);
            }
        };
        filterForm.add(back);

        content.add(new PagingNavigator("navigator", data, getPreferencesPage() + fileId, content));

        //Связать
        filterForm.add(new IndicatingAjaxButton("bind") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form form) {
                Collection<FacilityServiceType> list = checkGroup.getModelObject();

                String serviceProviderCode = organizationStrategy.getServiceProviderCode(requestFile.getEdrpou(),
                        requestFile.getOrganizationId(), requestFile.getUserOrganizationId());

                Long billingId = organizationStrategy.getBillingId(requestFile.getUserOrganizationId());

                //noinspection Duplicates
                list.forEach(facilityServiceType -> {
                    try {
                        requestWarningBean.delete(facilityServiceType);
                        facilityServiceTypeBindTaskBean.bind(serviceProviderCode, billingId, facilityServiceType);

                        if (facilityServiceType.getStatus().equals(RequestStatus.ACCOUNT_NUMBER_RESOLVED)){
                            info(getStringFormat("info_bound", facilityServiceType.getInn(), facilityServiceType.getFio()));
                        }else {
                            error(getStringFormat("error_bound", facilityServiceType.getFio(),
                                    StatusRenderUtil.displayStatus(facilityServiceType.getStatus(), getLocale())));
                        }

                        PrivilegeGroup privilegeGroup = privilegeGroupService.getPrivilegeGroup(
                                facilityServiceType.getRequestFileId(),
                                facilityServiceType.getStringField(FacilityServiceTypeDBF.IDPIL),
                                facilityServiceType.getStringField(FacilityServiceTypeDBF.PASPPIL),
                                facilityServiceType.getStringField(FacilityServiceTypeDBF.FIO),
                                facilityServiceType.getStringField(FacilityServiceTypeDBF.CDUL),
                                facilityServiceType.getStringField(FacilityServiceTypeDBF.HOUSE),
                                facilityServiceType.getStringField(FacilityServiceTypeDBF.BUILD),
                                facilityServiceType.getStringField(FacilityServiceTypeDBF.APT));

                        if (privilegeGroup.getDwellingCharacteristics() != null){
                            requestWarningBean.delete(privilegeGroup.getDwellingCharacteristics());
                            dwellingCharacteristicsBindTaskBean.bind(serviceProviderCode, billingId,
                                    privilegeGroup.getDwellingCharacteristics());
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
