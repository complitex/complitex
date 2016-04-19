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
import org.complitex.common.service.SessionBean;
import org.complitex.common.strategy.organization.IOrganizationStrategy;
import org.complitex.common.util.ExceptionUtil;
import org.complitex.common.web.component.datatable.ArrowOrderByBorder;
import org.complitex.common.web.component.datatable.DataProvider;
import org.complitex.common.web.component.paging.PagingNavigator;
import org.complitex.correction.web.component.AddressCorrectionDialog;
import org.complitex.osznconnection.file.entity.*;
import org.complitex.osznconnection.file.entity.example.DwellingCharacteristicsExample;
import org.complitex.osznconnection.file.entity.privilege.DwellingCharacteristics;
import org.complitex.osznconnection.file.entity.privilege.DwellingCharacteristicsDBF;
import org.complitex.osznconnection.file.entity.privilege.FacilityStreet;
import org.complitex.osznconnection.file.entity.privilege.FacilityStreetDBF;
import org.complitex.osznconnection.file.service.*;
import org.complitex.osznconnection.file.service.privilege.DwellingCharacteristicsBean;
import org.complitex.osznconnection.file.service.privilege.FacilityReferenceBookBean;
import org.complitex.osznconnection.file.service.privilege.task.DwellingCharacteristicsBindTaskBean;
import org.complitex.osznconnection.file.service.status.details.DwellingCharacteristicsExampleConfigurator;
import org.complitex.osznconnection.file.service.status.details.DwellingCharacteristicsStatusDetailRenderer;
import org.complitex.osznconnection.file.service.status.details.StatusDetailBean;
import org.complitex.osznconnection.file.service.warning.WebWarningRenderer;
import org.complitex.osznconnection.file.web.component.DataRowHoverBehavior;
import org.complitex.osznconnection.file.web.component.StatusDetailPanel;
import org.complitex.osznconnection.file.web.component.StatusRenderer;
import org.complitex.osznconnection.organization.strategy.OsznOrganizationStrategy;
import org.complitex.template.web.security.SecurityRole;
import org.complitex.template.web.template.TemplatePage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.complitex.common.util.StringUtil.emptyOnNull;
import static org.complitex.osznconnection.file.entity.privilege.DwellingCharacteristicsDBF.CDUL;

@AuthorizeInstantiation(SecurityRole.AUTHORIZED)
public final class DwellingCharacteristicsList extends TemplatePage {
    private Logger log = LoggerFactory.getLogger(DwellingCharacteristicsList.class);

    @EJB
    private DwellingCharacteristicsBean dwellingCharacteristicsBean;

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

    @EJB(name = IOrganizationStrategy.BEAN_NAME, beanInterface = IOrganizationStrategy.class)
    private OsznOrganizationStrategy organizationStrategy;

    @EJB
    private FacilityReferenceBookBean facilityReferenceBookBean;

    private IModel<DwellingCharacteristicsExample> example;
    private long fileId;


    public DwellingCharacteristicsList(PageParameters params) {
        this.fileId = params.get("request_file_id").toLong();
        init();
    }

    private void clearExample() {
        example.setObject(newExample());
    }

    private DwellingCharacteristicsExample newExample() {
        final DwellingCharacteristicsExample e = new DwellingCharacteristicsExample();
        e.setRequestFileId(fileId);
        return e;
    }

    private void init() {
        RequestFile requestFile = requestFileBean.getRequestFile(fileId);
        String serviceProviderCode = organizationStrategy.getServiceProviderCode(requestFile.getEdrpou(),
                requestFile.getOrganizationId(), requestFile.getUserOrganizationId());


        //Проверка доступа к данным
        if (!sessionBean.isAuthorized(requestFile.getOrganizationId(),
                requestFile.getUserOrganizationId())) {
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
        example = new Model<>(newExample());

        StatusDetailPanel<DwellingCharacteristicsExample> statusDetailPanel =
                new StatusDetailPanel<DwellingCharacteristicsExample>("statusDetailsPanel", example,
                new DwellingCharacteristicsExampleConfigurator(), new DwellingCharacteristicsStatusDetailRenderer(), content) {

                    @Override
                    public List<StatusDetailInfo> loadStatusDetails() {
                        return statusDetailBean.getDwellingCharacteristicsStatusDetails(fileId);
                    }
                };
        add(statusDetailPanel);

        final DataProvider<DwellingCharacteristics> dataProvider = new DataProvider<DwellingCharacteristics>() {

            @Override
            protected Iterable<? extends DwellingCharacteristics> getData(long first, long count) {
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

        filterForm.add(new TextField<>("idCodeFilter", new PropertyModel<>(example, "idCode")));
        filterForm.add(new TextField<>("firstNameFilter", new PropertyModel<>(example, "firstName")));
        filterForm.add(new TextField<>("middleNameFilter", new PropertyModel<>(example, "middleName")));
        filterForm.add(new TextField<>("lastNameFilter", new PropertyModel<>(example, "lastName")));
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
        }));

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
                item.add(new Label("idCode", dwellingCharacteristics.getInn()));
                item.add(new Label("firstName", dwellingCharacteristics.getFirstName()));
                item.add(new Label("middleName", dwellingCharacteristics.getMiddleName()));
                item.add(new Label("lastName", dwellingCharacteristics.getLastName()));
                item.add(new Label("streetReference", emptyOnNull(dwellingCharacteristics.getStreetType()) + " "
                        + emptyOnNull(dwellingCharacteristics.getStreet())));
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
                                dwellingCharacteristics.getExternalAddress(), dwellingCharacteristics.getLocalAddress());
                    }
                };
                addressCorrectionLink.setVisible(dwellingCharacteristics.getStatus().isAddressCorrectable());
                item.add(addressCorrectionLink);

                AjaxLink lookup = new IndicatingAjaxLink("lookup") {

                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        lookupPanel.open(target, dwellingCharacteristics, dwellingCharacteristics.getAccountNumber());
                    }
                };
                item.add(lookup);
            }
        };
        checkGroup.add(data);

        filterForm.add(new CheckGroupSelector("checkAll", checkGroup));
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

        content.add(new PagingNavigator("navigator", data, getPreferencesPage() + fileId, content));

        //Связать
        filterForm.add(new IndicatingAjaxButton("bind") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form form) {
                Collection<DwellingCharacteristics> list = checkGroup.getModelObject();

                list.forEach(request -> {
                    //noinspection Duplicates
                    try {
                        dwellingCharacteristicsBindTaskBean.bind(serviceProviderCode, request);

                        if (request.getStatus().equals(RequestStatus.ACCOUNT_NUMBER_RESOLVED)){
                            info(getStringFormat("info_bound", request.getInn(), request.getFio()));
                        }else {
                            error(getStringFormat("error_bound", request.getFio(),
                                    StatusRenderUtil.displayStatus(request.getStatus(), getLocale())));

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
