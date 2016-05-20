package org.complitex.osznconnection.file.web.component.lookup;

import com.google.common.collect.ImmutableList;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.string.Strings;
import org.complitex.address.strategy.building.BuildingStrategy;
import org.complitex.address.strategy.street.StreetStrategy;
import org.complitex.common.entity.Cursor;
import org.complitex.common.entity.DomainObject;
import org.complitex.common.strategy.IStrategy;
import org.complitex.common.strategy.StrategyFactory;
import org.complitex.common.strategy.organization.IOrganizationStrategy;
import org.complitex.common.util.CloneUtil;
import org.complitex.common.util.ExceptionUtil;
import org.complitex.common.web.component.ShowMode;
import org.complitex.common.web.component.search.SearchComponentState;
import org.complitex.common.web.component.search.WiQuerySearchComponent;
import org.complitex.common.web.component.wiquery.ExtendedDialog;
import org.complitex.osznconnection.file.entity.AbstractAccountRequest;
import org.complitex.osznconnection.file.entity.AccountDetail;
import org.complitex.osznconnection.file.entity.RequestFile;
import org.complitex.osznconnection.file.service.AddressService;
import org.complitex.osznconnection.file.service.LookupBean;
import org.complitex.osznconnection.file.service.RequestFileBean;
import org.complitex.osznconnection.file.service.StatusRenderUtil;
import org.complitex.osznconnection.file.service_provider.exception.UnknownAccountNumberTypeException;
import org.complitex.osznconnection.file.web.component.account.AccountNumberPickerPanel;
import org.complitex.osznconnection.organization.strategy.OsznOrganizationStrategy;
import org.odlabs.wiquery.ui.accordion.Accordion;
import org.odlabs.wiquery.ui.options.HeightStyleEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import java.util.List;

import static org.apache.wicket.util.string.Strings.isEmpty;
import static org.complitex.osznconnection.file.entity.RequestStatus.*;

public abstract class AbstractLookupPanel<T extends AbstractAccountRequest> extends Panel {
    @EJB
    private StrategyFactory strategyFactory;

    @EJB
    private LookupBean lookupBean;

    @EJB(name = "OsznAddressService")
    private AddressService addressService;

    @EJB
    private StreetStrategy streetStrategy;

    @EJB
    private BuildingStrategy buildingStrategy;

    @EJB(name = IOrganizationStrategy.BEAN_NAME, beanInterface = IOrganizationStrategy.class)
    private OsznOrganizationStrategy organizationStrategy;

    @EJB
    private RequestFileBean requestFileBean;

    private IModel<String> apartmentModel;
    private IModel<List<AccountDetail>> accountDetailsModel;
    private IModel<AccountDetail> accountDetailModel;
    private AccountNumberPickerPanel accountNumberPickerPanel;
    private FeedbackPanel messages;
    private ExtendedDialog dialog;
    private Label header;
    private SearchComponentState addressSearchComponentState;
    private WiQuerySearchComponent addressSearchComponent;
    private T request;
    private T initialRequest;
    private IModel<String> accountNumberModel = new Model<>();

    private WebMarkupContainer addressContainer;
    private WebMarkupContainer accountContainer;
    private WebMarkupContainer fioContainer;

    private IModel<String> firstNameModel = Model.of("");
    private IModel<String> middleNameModel = Model.of("");
    private IModel<String> lastNameModel = Model.of("");
    private IModel<String> innModel = Model.of("");
    private IModel<String> passportModel = Model.of("");

    private Logger log = LoggerFactory.getLogger(getClass());

    public AbstractLookupPanel(String id, Component... toUpdate) {
        super(id);
        init(toUpdate);
    }

    private void init(final Component... toUpdate) {
        dialog = new ExtendedDialog("dialog"){
            @Override
            protected void onClose(AjaxRequestTarget target) {
                AbstractLookupPanel.this.onClose(target);
            }
        };
        dialog.setOutputMarkupId(true);
        dialog.setModal(true);
        dialog.setWidth(1000);
        add(dialog);

        messages = new FeedbackPanel("messages");
        messages.setOutputMarkupId(true);
        dialog.add(messages);

        header = new Label("header", new LoadableDetachableModel<String>() {

            @Override
            protected String load() {
                return request != null ? getTitle(request) : "";
            }
        });
        header.setOutputMarkupId(true);
        dialog.add(header);

        Form form = new Form("form");
        dialog.add(form);

        Accordion accordion = new Accordion("accordion").setHeightStyle(HeightStyleEnum.CONTENT).setActive(0);
        form.add(accordion);

        //lookup by address
        addressContainer = new WebMarkupContainer("address_container");
        addressContainer.setOutputMarkupId(true);
        accordion.add(addressContainer);

        addressSearchComponentState = new SearchComponentState();
        apartmentModel = new Model<>();
        TextField<String> apartment = new TextField<>("apartment", apartmentModel, String.class);
        apartment.setOutputMarkupId(true);
        apartment.add(new AjaxFormComponentUpdatingBehavior("change") {

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
            }
        });
        addressContainer.add(apartment);


        addressSearchComponent = new WiQuerySearchComponent("addressSearchComponent", addressSearchComponentState,
                ImmutableList.of("city", "street", "building"), null, ShowMode.ACTIVE, true);
        addressSearchComponent.setOutputMarkupPlaceholderTag(true);
        addressSearchComponent.setVisible(false);
        addressContainer.add(addressSearchComponent);

        addressContainer.add(new IndicatingAjaxLink<Void>("lookupByAddress") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                lookupByAddress(target);
            }
        });

        //lookup by account number
        accountContainer = new WebMarkupContainer("account_container");
        accountContainer.setOutputMarkupId(true);
        accordion.add(accountContainer);

        accountContainer.add(new TextField<>("accountNumber", accountNumberModel, String.class)
                .add(new AjaxFormComponentUpdatingBehavior("change") {

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
            }
        }));

        accountContainer.add(new IndicatingAjaxLink("lookupByAccount") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                lookupByAccount(target);
            }
        });

        //lookup by person
        fioContainer = new WebMarkupContainer("fio_container");
        fioContainer.setOutputMarkupId(true);
        accordion.add(fioContainer);

        fioContainer.add(new TextField<>("lastName", lastNameModel));
        fioContainer.add(new TextField<>("firstName", firstNameModel));
        fioContainer.add(new TextField<>("middleName", middleNameModel));
        fioContainer.add(new TextField<>("inn", innModel));
        fioContainer.add(new TextField<>("passport", passportModel));

        fioContainer.add(new IndicatingAjaxButton("lookupByFio") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                accountDetailsModel.setObject(null);

                String district = resolveOutgoingDistrict(request);

                try {
                    Cursor<AccountDetail> accountDetails = lookupBean.getAccountDetailsByPerson(request.getUserOrganizationId(),
                            district, getServiceProviderCode(request), lastNameModel.getObject(),
                            firstNameModel.getObject(), middleNameModel.getObject(), innModel.getObject(),
                            passportModel.getObject(), request.getDate());

                    if (accountDetails.isEmpty()){
                        cursorError(accountDetails);
                    }else{
                        accountDetailsModel.setObject(accountDetails.getData());
                    }

                    target.add(messages);

                    accountNumberPickerPanel.setVisible(accountDetailsModel.getObject() != null
                            && !accountDetailsModel.getObject().isEmpty());
                    target.add(accountNumberPickerPanel);
                } catch (Exception e) {
                    error(ExceptionUtil.getCauseMessage(e));
                    target.add(messages);
                    e.printStackTrace();
                }
            }
        });


        //account number picker panel
        accountDetailModel = new Model<>();
        accountDetailsModel = Model.ofList(null);
        accountNumberPickerPanel = new AccountNumberPickerPanel("accountNumberPickerPanel", accountDetailsModel, accountDetailModel);
        accountNumberPickerPanel.setOutputMarkupPlaceholderTag(true);
        accountNumberPickerPanel.setVisible(false);
        form.add(accountNumberPickerPanel);

        // save/cancel
        form.add(new AjaxLink<Void>("save") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                if (accountDetailModel.getObject() != null && !isEmpty(accountDetailModel.getObject().getAccCode())) {
                    try {
                        updateAccountNumber(initialRequest, accountDetailModel.getObject().getAccCode());
                        for (Component component : toUpdate) {
                            target.add(component);
                        }
                        closeDialog(target);
                    } catch (Exception e) {
                        error(getString("db_error"));
                        LoggerFactory.getLogger(getClass()).error("", e);
                        target.add(messages);
                    }
                } else {
                    error(getString("account_number_not_chosen"));
                    target.add(messages);
                }
            }
        });

        form.add(new AjaxLink<Void>("cancel") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                closeDialog(target);
            }
        });
    }

    private void closeDialog(AjaxRequestTarget target) {
        dialog.close(target);
    }

    protected void onClose(AjaxRequestTarget target){
        addressSearchComponent.setVisible(false);
        target.add(addressSearchComponent);
    }

    protected final Long getObjectId(DomainObject object) {
        return object == null ? null : object.getObjectId();
    }

    protected Long getStreetType(DomainObject streetObject) {
        return streetObject == null ? null : streetStrategy.getStreetType(streetObject);
    }

    protected abstract void initInternalAddress(T request, Long cityId, Long streetId, Long streetTypeId, Long buildingId, String apartment);

    private void initSearchComponentState() {
        addressSearchComponentState.clear();

        if (request.getCityId() == null || request.getStreetId() == null || request.getBuildingId() == null) {
            try {
                addressService.resolveLocalAddress(request);
            } catch (Exception e) {
                log.warn("error init address component", e);
            }
        }

        if (request.getCityId() != null) {
            addressSearchComponentState.put("city", findObject(request.getCityId(), "city"));
        }
        if (request.getStreetId() != null) {
            addressSearchComponentState.put("street", findObject(request.getStreetId(), "street"));
        }
        if (request.getBuildingId() != null) {
            addressSearchComponentState.put("building", findObject(request.getBuildingId(), "building"));
        }
    }

    private DomainObject findObject(Long objectId, String entity) {
        IStrategy strategy = strategyFactory.getStrategy(entity);
        return strategy.getDomainObject(objectId, true);
    }

    protected abstract boolean isInternalAddressCorrect(T request);

    protected String getTitle(T request){
        return "";
    }

    public void open(AjaxRequestTarget target, T request, String serviceProviderAccountNumber) {
        this.request = CloneUtil.cloneObject(request);
        this.initialRequest = request;

        accountDetailModel.setObject(null);
        accountDetailsModel.setObject(null);
        accountNumberPickerPanel.setVisible(false);

        //lookup by person
        if (Strings.isEmpty(request.getInn()) && Strings.isEmpty(request.getPassport())){
            firstNameModel.setObject(request.getFirstName());
            middleNameModel.setObject(request.getMiddleName());
            lastNameModel.setObject(request.getLastName());
        }else{
            firstNameModel.setObject(null);
            middleNameModel.setObject(null);
            lastNameModel.setObject(null);
        }

        innModel.setObject(request.getInn());
        passportModel.setObject(request.getPassport());

        //lookup by address
        apartmentModel.setObject(request.getApartment());

        initSearchComponentState();

        addressSearchComponent.reinitialize(target);
        addressSearchComponent.setVisible(true);

        //lookup by account number
        if (!Strings.isEmpty(serviceProviderAccountNumber) && !"0".equals(serviceProviderAccountNumber)) {
            accountNumberModel.setObject(serviceProviderAccountNumber);
        } else {
            accountNumberModel.setObject(null);
        }

        //immediately search
        if (request.getStatus().isImmediatelySearchByAddress()){
            lookupByAddress(target);
        }else if (serviceProviderAccountNumber != null){
            lookupByAccount(target);
        }

        target.add(accountNumberPickerPanel, addressContainer, accountContainer, fioContainer, messages, header);

        dialog.open(target);
    }

    protected void resolveOutgoingAddress(T request){
        lookupBean.resolveOutgoingAddress(request);
    }

    protected Cursor<AccountDetail> getAccountDetails(T request) {
        return lookupBean.getAccountDetails( request.getOutgoingDistrict(), getServiceProviderCode(request),
                request.getOutgoingStreetType(), request.getOutgoingStreet(),
                request.getOutgoingBuildingNumber(), request.getOutgoingBuildingCorp(),
                request.getOutgoingApartment(), request.getDate(), request.getUserOrganizationId());
    }

    protected abstract void updateAccountNumber(T request, String accountNumber);

    private String resolveOutgoingDistrict(T request) {
        return addressService.resolveOutgoingDistrict(request.getOrganizationId(), request.getUserOrganizationId());
    }

    protected String getServiceProviderCode(T request){
        RequestFile requestFile = requestFileBean.getRequestFile(request.getRequestFileId());

        return organizationStrategy.getServiceProviderCode(requestFile.getEdrpou(), requestFile.getOrganizationId(),
                requestFile.getUserOrganizationId());
    }

    private void lookupByAddress(AjaxRequestTarget target) {
        boolean wasVisible = accountNumberPickerPanel.isVisible();
        accountDetailsModel.setObject(null);
        accountDetailModel.setObject(null);

        initInternalAddress(request, getObjectId(addressSearchComponentState.get("city")),
                getObjectId(addressSearchComponentState.get("street")), getStreetType(addressSearchComponentState.get("street")),
                getObjectId(addressSearchComponentState.get("building")), apartmentModel.getObject());

        if (isInternalAddressCorrect(request)) {
            boolean outgoingAddressResolved = false;
            try {
                resolveOutgoingAddress(request);
                outgoingAddressResolved = true;
            } catch (Exception e) {
                error(getString("db_error"));
                LoggerFactory.getLogger(getClass()).error("", e);
            }
            if (outgoingAddressResolved) {
                if (request.getStatus() == ACCOUNT_NUMBER_NOT_FOUND) {
                    try {
                        Cursor<AccountDetail> accountDetails = getAccountDetails(request);

                        if (accountDetails.isEmpty()) {
                            cursorError(accountDetails);
                        } else {
                            accountDetailsModel.setObject(accountDetails.getData());
                            if (accountDetails.getData().size() == 1) {
                                accountDetailModel.setObject(accountDetails.getData().get(0));
                            }
                        }
                    } catch (Exception e) {
                        error(getString("remote_db_error"));
                        LoggerFactory.getLogger(getClass()).error("", e);
                    }
                } else {
                    error(StatusRenderUtil.displayStatus(request.getStatus(), getLocale()));
                }
            }
        } else {
            error(getString("address_required"));
        }

        target.add(messages);
        boolean becameVisible = accountDetailsModel.getObject() != null && !accountDetailsModel.getObject().isEmpty();
        accountNumberPickerPanel.setVisible(becameVisible);
        if (wasVisible || becameVisible) {
            target.add(accountNumberPickerPanel);
        }
    }

    private void cursorError(Cursor<AccountDetail> accountDetails) {
        switch (accountDetails.getResultCode()){
            case 0:
                error(StatusRenderUtil.displayStatus(ACCOUNT_NUMBER_NOT_FOUND, getLocale()));
                break;
            case -2:
                error(StatusRenderUtil.displayStatus(APARTMENT_NOT_FOUND, getLocale()));
                break;
            case -3:
                error(StatusRenderUtil.displayStatus(BUILDING_CORP_NOT_FOUND, getLocale()));
                break;
            case -4:
                error(StatusRenderUtil.displayStatus(BUILDING_NOT_FOUND, getLocale()));
                break;
            case -5:
                error(StatusRenderUtil.displayStatus(STREET_NOT_FOUND, getLocale()));
                break;
            case -6:
                error(StatusRenderUtil.displayStatus(STREET_TYPE_NOT_FOUND, getLocale()));
                break;
            case -7:
                error(StatusRenderUtil.displayStatus(DISTRICT_NOT_FOUND, getLocale()));
                break;
            case -8:
                error(StatusRenderUtil.displayStatus(SERVICING_ORGANIZATION_NOT_FOUND, getLocale()));
                break;
        }
    }

    private void lookupByAccount(AjaxRequestTarget target) {
        boolean wasVisible = accountNumberPickerPanel.isVisible();
        accountDetailsModel.setObject(null);
        accountDetailModel.setObject(null);

        if (!isEmpty(accountNumberModel.getObject())) {
            String outgoingDistrict = null;
            try {
                outgoingDistrict = resolveOutgoingDistrict(request);
            } catch (Exception e) {
                LoggerFactory.getLogger(getClass()).error("", e);
                error(getString("db_error"));
            }
            if (!isEmpty(outgoingDistrict)) {
                try {
                    List<AccountDetail> accountDetails = lookupBean.acquireAccountDetailsByAccount(request,
                            outgoingDistrict, getServiceProviderCode(request), accountNumberModel.getObject());

                    if (accountDetails == null || accountDetails.isEmpty()) {
                        error(StatusRenderUtil.displayStatus(request.getStatus(), getLocale()));
                    } else {
                        accountDetailsModel.setObject(accountDetails);
                        if (accountDetails.size() == 1) {
                            accountDetailModel.setObject(accountDetails.get(0));
                        }
                    }
                } catch (UnknownAccountNumberTypeException e) {
                    error(getString("unknown_account_number_type"));
                } catch (Exception e) {
                    error(getString("remote_db_error"));
                    LoggerFactory.getLogger(getClass()).error("", e);
                }
            } else {
                error(StatusRenderUtil.displayStatus(request.getStatus(), getLocale()));
            }
        } else {
            error(getString("lookup_by_account_required"));
        }

        target.add(messages);
        boolean becameVisible = accountDetailsModel.getObject() != null && !accountDetailsModel.getObject().isEmpty();
        accountNumberPickerPanel.setVisible(becameVisible);
        if (wasVisible || becameVisible) {
            target.add(accountNumberPickerPanel);
        }
    }


}

