package ru.complitex.osznconnection.file.web.pages.payment;

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
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.Strings;
import ru.complitex.address.entity.AddressEntity;
import ru.complitex.address.entity.LocalAddress;
import ru.complitex.common.entity.PreferenceKey;
import ru.complitex.common.service.SessionBean;
import ru.complitex.common.strategy.organization.IOrganizationStrategy;
import ru.complitex.common.util.ExceptionUtil;
import ru.complitex.common.web.component.datatable.ArrowOrderByBorder;
import ru.complitex.common.web.component.datatable.DataProvider;
import ru.complitex.common.web.component.paging.PagingNavigator;
import ru.complitex.correction.web.address.component.AddressCorrectionDialog;
import ru.complitex.osznconnection.file.entity.RequestFile;
import ru.complitex.osznconnection.file.entity.RequestStatus;
import ru.complitex.osznconnection.file.entity.StatusDetailInfo;
import ru.complitex.osznconnection.file.entity.example.PaymentExample;
import ru.complitex.osznconnection.file.entity.subsidy.Payment;
import ru.complitex.osznconnection.file.entity.subsidy.PaymentDBF;
import ru.complitex.osznconnection.file.service.AddressService;
import ru.complitex.osznconnection.file.service.RequestFileBean;
import ru.complitex.osznconnection.file.service.StatusRenderUtil;
import ru.complitex.osznconnection.file.service.status.details.PaymentBenefitStatusDetailRenderer;
import ru.complitex.osznconnection.file.service.status.details.PaymentExampleConfigurator;
import ru.complitex.osznconnection.file.service.status.details.StatusDetailBean;
import ru.complitex.osznconnection.file.service.subsidy.PaymentBean;
import ru.complitex.osznconnection.file.service.subsidy.RequestFileGroupBean;
import ru.complitex.osznconnection.file.service.subsidy.task.GroupBindTaskBean;
import ru.complitex.osznconnection.file.service.warning.WebWarningRenderer;
import ru.complitex.osznconnection.file.web.component.DataRowHoverBehavior;
import ru.complitex.osznconnection.file.web.component.StatusDetailPanel;
import ru.complitex.osznconnection.file.web.component.StatusRenderer;
import ru.complitex.osznconnection.file.web.pages.subsidy.GroupList;
import ru.complitex.osznconnection.organization.strategy.OsznOrganizationStrategy;
import ru.complitex.template.web.template.TemplatePage;
import ru.complitex.template.web.template.TemplateSession;

import javax.ejb.EJB;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@AuthorizeInstantiation("SUBSIDY_GROUP")
public final class PaymentList extends TemplatePage {

    public static final String FILE_ID = "request_file_id";

    @EJB
    private PaymentBean paymentBean;

    @EJB
    private RequestFileBean requestFileBean;

    @EJB
    private WebWarningRenderer webWarningRenderer;

    @EJB
    private StatusDetailBean statusDetailBean;

    @EJB
    private AddressService addressService;

    @EJB
    private SessionBean osznSessionBean;

    @EJB
    private GroupBindTaskBean groupBindTaskBean;

    @EJB
    private RequestFileGroupBean requestFileGroupBean;

    @EJB(name = IOrganizationStrategy.BEAN_NAME, beanInterface = IOrganizationStrategy.class)
    private OsznOrganizationStrategy organizationStrategy;

    private IModel<PaymentExample> example;
    private long fileId;

    public PaymentList(PageParameters params) {
        this.fileId = params.get(FILE_ID).toLong();
        init();
    }

    private void clearExample() {
        example.setObject(newExample());
    }

    private PaymentExample newExample() {
        PaymentExample paymentExample = new PaymentExample();
        paymentExample.setRequestFileId(fileId);
        return paymentExample;
    }

    private void init() {
        RequestFile requestFile = requestFileBean.getRequestFile(fileId);

        //Проверка доступа к данным
        if (!osznSessionBean.isAuthorized(requestFile.getUserOrganizationId())) {
            throw new UnauthorizedInstantiationException(this.getClass());
        }

        final DataRowHoverBehavior dataRowHoverBehavior = new DataRowHoverBehavior();
        add(dataRowHoverBehavior);

        String label = getStringFormat("label", requestFile.getDirectory(), File.separator, requestFile.getName());

        add(new Label("title", label));
        add(new Label("label", label));

        final WebMarkupContainer content = new WebMarkupContainer("content");
        content.setOutputMarkupId(true);
        add(content);

        content.add(new FeedbackPanel("messages"));

        final Form<Void> filterForm = new Form<>("filterForm");
        content.add(filterForm);
        example = new Model<>(((TemplateSession) getSession()).getPreferenceObject(getPreferencesPage() + fileId,
                PreferenceKey.FILTER_OBJECT, newExample()));

        StatusDetailPanel<PaymentExample> statusDetailPanel = new StatusDetailPanel<PaymentExample>("statusDetailsPanel", example,
                new PaymentExampleConfigurator(), new PaymentBenefitStatusDetailRenderer(), content) {

            @Override
            public List<StatusDetailInfo> loadStatusDetails() {
                return statusDetailBean.getPaymentStatusDetails(fileId);
            }
        };
        add(statusDetailPanel);

        final DataProvider<Payment> dataProvider = new DataProvider<Payment>() {

            @Override
            protected Iterable<? extends Payment> getData(long first, long count) {
                ((TemplateSession)getSession()).putPreferenceObject(getPreferencesPage() + fileId,
                        PreferenceKey.FILTER_OBJECT, example.getObject());

                example.getObject().setAsc(getSort().isAscending());
                if (!Strings.isEmpty(getSort().getProperty())) {
                    example.getObject().setOrderByClause(getSort().getProperty());
                }
                example.getObject().setFirst(first);
                example.getObject().setCount(count);
                return paymentBean.find(example.getObject());
            }

            @Override
            protected Long getSize() {
                example.getObject().setAsc(getSort().isAscending());

                return paymentBean.getCount(example.getObject());
            }
        };
        dataProvider.setSort("", SortOrder.ASCENDING);

        filterForm.add(new TextField<>("accountNumberFilter", new PropertyModel<>(example, "accountNumber")));
        filterForm.add(new TextField<>("puAccountNumberFilter", new PropertyModel<>(example, "puAccountNumber")));
        filterForm.add(new TextField<>("firstNameFilter", new PropertyModel<>(example, "firstName")));
        filterForm.add(new TextField<>("middleNameFilter", new PropertyModel<>(example, "middleName")));
        filterForm.add(new TextField<>("lastNameFilter", new PropertyModel<>(example, "lastName")));
        filterForm.add(new TextField<>("cityFilter", new PropertyModel<>(example, "city")));
        filterForm.add(new TextField<>("streetFilter", new PropertyModel<>(example, "street")));
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
        final AddressCorrectionDialog<Payment> addressCorrectionDialog = new AddressCorrectionDialog<Payment>("addressCorrectionPanel") {
            @Override
            protected void onCorrect(AjaxRequestTarget target, IModel<Payment> model, AddressEntity addressEntity) {
                paymentBean.markCorrected(model.getObject(), addressEntity);

                target.add(content, statusDetailPanel);
                statusDetailPanel.rebuild();
                dataRowHoverBehavior.deactivateDataRow(target);
            }
        };
        add(addressCorrectionDialog);

        //Панель поиска
        final PaymentLookupPanel lookupPanel = new PaymentLookupPanel("lookupPanel", content, statusDetailPanel) {

            @Override
            protected void onClose(AjaxRequestTarget target) {
                super.onClose(target);
                dataRowHoverBehavior.deactivateDataRow(target);
            }
        };
        add(lookupPanel);

        final CheckGroup<Payment> checkGroup = new CheckGroup<>("checkGroup", new ArrayList<>());
        filterForm.add(checkGroup);

        DataView<Payment> data = new DataView<Payment>("data", dataProvider, 1) {

            @Override
            protected void populateItem(Item<Payment> item) {
                final Payment payment = item.getModelObject();

                item.add(new Check<>("check", Model.of(payment), checkGroup));
                item.add(new Label("accountNumber", payment.getAccountNumber()));
                item.add(new Label("puAccountNumber", payment.getStringField(PaymentDBF.OWN_NUM_SR)));
                item.add(new Label("firstName", payment.getFirstName()));
                item.add(new Label("middleName", payment.getMiddleName()));
                item.add(new Label("lastName", payment.getLastName()));
                item.add(new Label("city", payment.getCity()));
                item.add(new Label("street", payment.getStreet()));
                item.add(new Label("building", payment.getBuildingNumber()));
                item.add(new Label("corp", payment.getBuildingCorp()));
                item.add(new Label("apartment", payment.getApartment()));
                item.add(new Label("status", StatusRenderUtil.displayStatus(payment.getStatus(), getLocale())));
                item.add(new Label("statusDetails", webWarningRenderer.display(payment.getWarnings(), getLocale())));

                AjaxLink addressCorrectionLink = new AjaxLink("addressCorrectionLink") {

                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        LocalAddress localAddress = payment.getLocalAddress();

                        addressCorrectionDialog.open(target, item.getModel(), payment.getPersonalName(),
                                localAddress.getFirstEmptyAddressEntity(false),
                                payment.getExternalAddress(), localAddress);
                    }
                };
                addressCorrectionLink.setVisible(payment.getStatus().isAddressCorrectable());
                item.add(addressCorrectionLink);

                AjaxLink lookup = new IndicatingAjaxLink("lookup") {

                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        lookupPanel.open(target, payment, payment.getStringField(PaymentDBF.OWN_NUM_SR));
                    }
                };
                item.add(lookup);
            }
        };
        checkGroup.add(data);

        filterForm.add(new CheckGroupSelector("checkAll", checkGroup));
        filterForm.add(new ArrowOrderByBorder("accountNumberHeader", PaymentBean.OrderBy.ACCOUNT_NUMBER.getOrderBy(), dataProvider, data, content));
        filterForm.add(new ArrowOrderByBorder("puAccountNumberHeader", PaymentBean.OrderBy.PU_ACCOUNT_NUMBER.getOrderBy(), dataProvider, data, content));
        filterForm.add(new ArrowOrderByBorder("firstNameHeader", PaymentBean.OrderBy.FIRST_NAME.getOrderBy(), dataProvider, data, content));
        filterForm.add(new ArrowOrderByBorder("middleNameHeader", PaymentBean.OrderBy.MIDDLE_NAME.getOrderBy(), dataProvider, data, content));
        filterForm.add(new ArrowOrderByBorder("lastNameHeader", PaymentBean.OrderBy.LAST_NAME.getOrderBy(), dataProvider, data, content));
        filterForm.add(new ArrowOrderByBorder("cityHeader", PaymentBean.OrderBy.CITY.getOrderBy(), dataProvider, data, content));
        filterForm.add(new ArrowOrderByBorder("streetHeader", PaymentBean.OrderBy.STREET.getOrderBy(), dataProvider, data, content));
        filterForm.add(new ArrowOrderByBorder("buildingHeader", PaymentBean.OrderBy.BUILDING.getOrderBy(), dataProvider, data, content));
        filterForm.add(new ArrowOrderByBorder("corpHeader", PaymentBean.OrderBy.CORP.getOrderBy(), dataProvider, data, content));
        filterForm.add(new ArrowOrderByBorder("apartmentHeader", PaymentBean.OrderBy.APARTMENT.getOrderBy(), dataProvider, data, content));
        filterForm.add(new ArrowOrderByBorder("statusHeader", PaymentBean.OrderBy.STATUS.getOrderBy(), dataProvider, data, content));

        Button back = new Button("back") {

            @Override
            public void onSubmit() {
                PageParameters params = new PageParameters();
//                params.set(GroupList.SCROLL_PARAMETER, fileId);
                setResponsePage(GroupList.class, params);
            }
        };
        back.setDefaultFormProcessing(false);
        filterForm.add(back);

        content.add(new PagingNavigator("navigator", data, getPreferencesPage(), content));

        //Связать
        filterForm.add(new IndicatingAjaxButton("bind") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form form) {
                Collection<Payment> payments = checkGroup.getModelObject();

                payments.forEach(payment -> {
                    //noinspection Duplicates
                    try {
                        String serviceProviderCode = organizationStrategy.getServiceProviderCode(requestFile.getEdrpou(),
                                requestFile.getOrganizationId(), requestFile.getUserOrganizationId());

                        Long billingId = organizationStrategy.getBillingId(requestFile.getUserOrganizationId());

                        groupBindTaskBean.bind(serviceProviderCode, billingId, payment);

                        if (payment.getStatus().equals(RequestStatus.ACCOUNT_NUMBER_RESOLVED)){
                            info(getStringFormat("info_bound", payment.getFio()));
                        }else {
                            error(getStringFormat("error_bound", payment.getFio(),
                                    StatusRenderUtil.displayStatus(payment.getStatus(), getLocale())));

                        }
                    } catch (Exception e) {
                        error(ExceptionUtil.getCauseMessage(e, true));
                    }

                    requestFileGroupBean.updateIfBound(payment.getGroupId());
                });

                checkGroup.getModelObject().clear();
                target.add(content);
            }
        });
    }
}
