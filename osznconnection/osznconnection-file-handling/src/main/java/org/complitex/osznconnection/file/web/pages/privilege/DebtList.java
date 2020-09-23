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
import org.complitex.correction.entity.Correction;
import org.complitex.correction.service.CorrectionBean;
import org.complitex.correction.web.address.component.AddressCorrectionDialog;
import org.complitex.osznconnection.file.entity.*;
import org.complitex.osznconnection.file.entity.example.PrivilegeExample;
import org.complitex.osznconnection.file.entity.privilege.Debt;
import org.complitex.osznconnection.file.entity.privilege.DebtDBF;
import org.complitex.osznconnection.file.entity.privilege.FacilityStreet;
import org.complitex.osznconnection.file.entity.privilege.FacilityStreetDBF;
import org.complitex.osznconnection.file.service.RequestFileBean;
import org.complitex.osznconnection.file.service.StatusRenderUtil;
import org.complitex.osznconnection.file.service.privilege.DebtBean;
import org.complitex.osznconnection.file.service.privilege.FacilityReferenceBookBean;
import org.complitex.osznconnection.file.service.privilege.task.DebtBindTaskBean;
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
import java.util.stream.Collectors;

/**
 * @author Anatoly Ivanov
 * 18.09.2020 20:26
 */
@AuthorizeInstantiation({"PRIVILEGE_DEBT"})
public class DebtList extends TemplatePage {
    private Logger log = LoggerFactory.getLogger(DebtList.class);

    @EJB
    private DebtBean debtBean;

    @EJB
    private RequestFileBean requestFileBean;

    @EJB
    private WebWarningRenderer webWarningRenderer;

    @EJB
    private StatusDetailBean statusDetailBean;

    @EJB
    private CorrectionBean correctionBean;

    @EJB
    private SessionBean sessionBean;

    @EJB
    private DebtBindTaskBean debtBindTaskBean;

    @EJB(name = IOrganizationStrategy.BEAN_NAME, beanInterface = IOrganizationStrategy.class)
    private OsznOrganizationStrategy organizationStrategy;

    @EJB
    private FacilityReferenceBookBean facilityReferenceBookBean;

    @EJB
    private RequestWarningBean requestWarningBean;

    private IModel<PrivilegeExample> example;

    private Long fileId;

    public DebtList(PageParameters params) {
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
                        return statusDetailBean.getDebtStatusDetails(fileId);
                    }
                };
        add(statusDetailPanel);

        final DataProvider<Debt> dataProvider = new DataProvider<Debt>() {

            @Override
            protected Iterable<Debt> getData(long first, long count) {
                ((TemplateSession)getSession()).putPreferenceObject(getPreferencesPage() + fileId,
                        PreferenceKey.FILTER_OBJECT, example.getObject());

                example.getObject().setAsc(getSort().isAscending());
                if (!Strings.isEmpty(getSort().getProperty())) {
                    example.getObject().setOrderByClause(getSort().getProperty());
                }
                example.getObject().setFirst(first);
                example.getObject().setCount(count);

                return debtBean.getDebts(example.getObject());
            }

            @Override
            protected Long getSize() {
                example.getObject().setAsc(getSort().isAscending());

                return debtBean.getDebtsCount(example.getObject());
            }
        };
        dataProvider.setSort("", SortOrder.ASCENDING);

        filterForm.add(new TextField<>("accountFilter", new PropertyModel<String>(example, "accountNumber")));
        filterForm.add(new TextField<>("spAccountFilter", new PropertyModel<String>(example, "spAccountNumber")));
        filterForm.add(new TextField<>("idCodeFilter", new PropertyModel<>(example, "inn")));
        filterForm.add(new TextField<>("passportFilter", new PropertyModel<>(example, "passport")));
        filterForm.add(new TextField<>("firstNameFilter", new PropertyModel<>(example, "firstName")));
        filterForm.add(new TextField<>("middleNameFilter", new PropertyModel<>(example, "middleName")));
        filterForm.add(new TextField<>("lastNameFilter", new PropertyModel<>(example, "lastName")));
        filterForm.add(new TextField<>("streetReferenceFilter", new Model<String>() {
            @Override
            public String getObject() {
                String streetCode = example.getObject().getStreetCode();

                if (streetCode != null) {
                    FacilityStreet facilityStreet = facilityReferenceBookBean.getFacilityStreet(streetCode,
                            requestFile.getOrganizationId(), requestFile.getUserOrganizationId());

                    if (facilityStreet != null) {
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

                if (!facilityStreets.isEmpty()) {
                    example.getObject().setStreetCode(facilityStreets.get(0).getStreetCode());
                }
            }
        }, String.class));

        filterForm.add(new TextField<>("buildingFilter", new PropertyModel<>(example, "building")));
        filterForm.add(new TextField<>("corpFilter", new PropertyModel<>(example, "corp")));
        filterForm.add(new TextField<>("apartmentFilter", new PropertyModel<>(example, "apartment")));
        filterForm.add(new TextField<>("debtFilter", Model.of("")));
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
        final AddressCorrectionDialog<Debt> addressCorrectionDialog =
                new AddressCorrectionDialog<Debt>("addressCorrectionPanel") {
                    @Override
                    protected void onCorrect(AjaxRequestTarget target, IModel<Debt> model, AddressEntity addressEntity) {
                        debtBean.markDebtCorrected(model.getObject(), addressEntity);

                        target.add(content, statusDetailPanel);
                        dataRowHoverBehavior.deactivateDataRow(target);
                        statusDetailPanel.rebuild();
                    }
                };
        add(addressCorrectionDialog);

        //Панель поиска
        final DebtLookupPanel lookupPanel =
                new DebtLookupPanel("lookupPanel", content, statusDetailPanel) {

                    @Override
                    protected void onClose(AjaxRequestTarget target) {
                        super.onClose(target);
                        dataRowHoverBehavior.deactivateDataRow(target);
                    }
                };
        add(lookupPanel);

        CheckGroup<Debt> checkGroup = new CheckGroup<>("checkGroup", new ArrayList<>());
        filterForm.add(checkGroup);

        DataView<Debt> data = new DataView<Debt>("data", dataProvider, 1) {

            @Override
            protected void populateItem(Item<Debt> item) {
                final Debt debt = item.getModelObject(); //todo update to property label

                item.add(new Check<>("check", Model.of(debt), checkGroup));
                item.add(new Label("account", debt.getAccountNumber()));
                item.add(new Label("spAccount", debt.getStringField(DebtDBF.RAH)));
                item.add(new Label("idCode", debt.getInn()));
                item.add(new Label("passport", debt.getPassport()));
                item.add(new Label("firstName", debt.getFirstName()));
                item.add(new Label("middleName", debt.getMiddleName()));
                item.add(new Label("lastName", debt.getLastName()));

                String streetType = debt.getStreetType() != null
                        ? debt.getStreetType()
                        : "";

                String street = debt.getStreet();

                if (street == null){
                    try {
                        List<Correction> streetCorrection = correctionBean.getCorrectionsByExternalId(AddressEntity.STREET,
                                Long.valueOf(debt.getStreetCode()), debt.getOrganizationId(),
                                debt.getUserOrganizationId());

                        if (streetCorrection.size() == 1){
                            street = streetCorrection.get(0).getCorrection() + " [" + debt.getStreetCode() + "]";
                        }else {
                            street = "[" + debt.getStreetCode() + "]";
                        }
                    } catch (NumberFormatException e) {
                        log.error("error strret correction {}", debt, e);
                    }
                }

                item.add(new Label("streetReference", streetType + " " + street));
                item.add(new Label("building", debt.getBuildingNumber()));
                item.add(new Label("corp", debt.getBuildingCorp()));
                item.add(new Label("apartment", debt.getApartment()));
                item.add(new Label("debt", debt.getBigDecimalField(DebtDBF.SUM_BORG)));
                item.add(new Label("status", StatusRenderUtil.displayStatus(debt.getStatus(), getLocale())));
                item.add(new Label("statusDetails", webWarningRenderer.display(debt.getWarnings(), getLocale())));

                AjaxLink addressCorrectionLink = new IndicatingAjaxLink("addressCorrectionLink") {

                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        addressCorrectionDialog.open(target, item.getModel(), debt.getPersonalName(),
                                debt.getExternalAddress(), debt.getLocalAddress(),
                                debt.getStreetId() != null);
                    }
                };
                addressCorrectionLink.setVisible(debt.getStatus().isAddressCorrectable());
                item.add(addressCorrectionLink);

                AjaxLink lookup = new IndicatingAjaxLink("lookup") {

                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        lookupPanel.open(target, debt, debt.getPuAccountNumber());
                    }
                };
                item.add(lookup);
            }
        };
        checkGroup.add(data);

        filterForm.add(new CheckGroupSelector("checkAll", checkGroup));
        filterForm.add(new ArrowOrderByBorder("accountHeader", "account_number", dataProvider, data, content));
        filterForm.add(new ArrowOrderByBorder("spAccountHeader", "RAH", dataProvider, data, content));
        filterForm.add(new ArrowOrderByBorder("idCodeHeader", "IDPIL", dataProvider, data, content));
        filterForm.add(new ArrowOrderByBorder("passportHeader", "PASPPIL", dataProvider, data, content));
        filterForm.add(new ArrowOrderByBorder("firstNameHeader", "first_name", dataProvider, data, content));
        filterForm.add(new ArrowOrderByBorder("middleNameHeader", "middle_name", dataProvider, data, content));
        filterForm.add(new ArrowOrderByBorder("lastNameHeader", "last_name", dataProvider, data, content));
        filterForm.add(new ArrowOrderByBorder("streetReferenceHeader", "street_reference", dataProvider, data, content));
        filterForm.add(new ArrowOrderByBorder("buildingHeader", "HOUSE", dataProvider, data, content));
        filterForm.add(new ArrowOrderByBorder("corpHeader", "CORP", dataProvider, data, content));
        filterForm.add(new ArrowOrderByBorder("apartmentHeader", "APT", dataProvider, data, content));
        filterForm.add(new ArrowOrderByBorder("debtHeader", "SUM_BORG", dataProvider, data, content));
        filterForm.add(new ArrowOrderByBorder("statusHeader", "status", dataProvider, data, content));

        Link<Void> back = new Link<Void>("back") {

            @Override
            public void onClick() {
                boolean profit = requestFile.getName().matches(".*\\.(S|s).*");

                PageParameters params = new PageParameters();
                params.set("type", profit ? "s" : "p");

                setResponsePage(DebtFileList.class, params);
            }
        };
        filterForm.add(back);

        content.add(new PagingNavigator("navigator", data, getPreferencesPage(), content));

        //Связать
        filterForm.add(new IndicatingAjaxButton("bind") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form form) {
                Collection<Debt> select = checkGroup.getModelObject();

                select.forEach(p -> {
                    debtBean.clearDebtBound(p);
                    requestWarningBean.delete(p);
                });

                List<Debt> list = debtBean
                        .getDebtForOperation(requestFile.getId(),
                                select.stream().map(AbstractRequest::getId).collect(Collectors.toList()));

                String serviceProviderCode = organizationStrategy.getServiceProviderCode(requestFile.getEdrpou(),
                        requestFile.getOrganizationId(), requestFile.getUserOrganizationId());

                Long billingId = organizationStrategy.getBillingId(requestFile.getUserOrganizationId());

                list.forEach(debt -> {
                    //noinspection Duplicates
                    try {


                        debtBindTaskBean.bind(serviceProviderCode, billingId, debt);

                        if (debt.getStatus().equals(RequestStatus.ACCOUNT_NUMBER_RESOLVED)) {
                            info(getStringFormat("info_bound", debt.getInn(), debt.getFio()));
                        } else {
                            error(getStringFormat("error_bound", debt.getFio(),
                                    StatusRenderUtil.displayStatus(debt.getStatus(), getLocale())));
                        }
                    } catch (Exception e) {
                        error(ExceptionUtil.getCauseMessage(e, true));
                        log.error("error debt bind", e);
                    }
                });

                if (debtBean.isDebtBound(requestFile.getId())) {
                    requestFile.setStatus(RequestFileStatus.BOUND);
                    requestFileBean.save(requestFile);
                }

                checkGroup.getModelObject().clear();
                target.add(content);
            }
        });
    }
}
