package org.complitex.osznconnection.file.web.pages.subsidy;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.CancelEventIfAjaxListener;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.authorization.UnauthorizedInstantiationException;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.datetime.markup.html.basic.DateLabel;
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
import org.complitex.address.util.AddressRenderer;
import org.complitex.common.service.SessionBean;
import org.complitex.common.util.ExceptionUtil;
import org.complitex.common.web.component.datatable.ArrowOrderByBorder;
import org.complitex.common.web.component.datatable.DataProvider;
import org.complitex.common.web.component.paging.PagingNavigator;
import org.complitex.correction.service.exception.DuplicateCorrectionException;
import org.complitex.correction.service.exception.MoreOneCorrectionException;
import org.complitex.correction.service.exception.NotFoundCorrectionException;
import org.complitex.correction.web.component.AddressCorrectionDialog;
import org.complitex.osznconnection.file.entity.*;
import org.complitex.osznconnection.file.entity.example.SubsidyExample;
import org.complitex.osznconnection.file.service.*;
import org.complitex.osznconnection.file.service.status.details.StatusDetailBean;
import org.complitex.osznconnection.file.service.status.details.SubsidyExampleConfigurator;
import org.complitex.osznconnection.file.service.status.details.SubsidyStatusDetailRenderer;
import org.complitex.osznconnection.file.service.warning.WebWarningRenderer;
import org.complitex.osznconnection.file.web.SubsidyFileList;
import org.complitex.osznconnection.file.web.component.DataRowHoverBehavior;
import org.complitex.osznconnection.file.web.component.StatusDetailPanel;
import org.complitex.osznconnection.file.web.component.StatusRenderer;
import org.complitex.template.web.security.SecurityRole;
import org.complitex.template.web.template.TemplatePage;

import javax.ejb.EJB;
import java.io.File;
import java.util.*;

import static org.complitex.common.util.StringUtil.decimal;

@AuthorizeInstantiation(SecurityRole.AUTHORIZED)
public final class SubsidyList extends TemplatePage {
    public static final String FILE_ID = "request_file_id";

    @EJB
    private SubsidyBean subsidyBean;

    @EJB
    private SubsidyService subsidyService;

    @EJB
    private RequestFileBean requestFileBean;

    @EJB
    private StatusRenderService statusRenderService;

    @EJB
    private WebWarningRenderer webWarningRenderer;

    @EJB
    private StatusDetailBean statusDetailBean;

    @EJB(name = "OsznAddressService")
    private AddressService addressService;

    @EJB
    private SessionBean sessionBean;

    private IModel<SubsidyExample> example;
    private long fileId;

    public SubsidyList(PageParameters params) {
        this.fileId = params.get(FILE_ID).toLong();
        init();
    }

    private void clearExample() {
        example.setObject(newExample());
    }

    private SubsidyExample newExample() {
        final SubsidyExample e = new SubsidyExample();
        e.setRequestFileId(fileId);
        return e;
    }

    private void init() {
        final RequestFile subsidyFile = requestFileBean.findById(fileId);

        //Проверка доступа к данным
        if (!sessionBean.isAuthorized(subsidyFile.getOrganizationId(), subsidyFile.getUserOrganizationId())) {
            throw new UnauthorizedInstantiationException(this.getClass());
        }

        final DataRowHoverBehavior dataRowHoverBehavior = new DataRowHoverBehavior();
        add(dataRowHoverBehavior);

        String label = getStringFormat("label", subsidyFile.getDirectory(), File.separator, subsidyFile.getName(),
                subsidyService.displayServicingOrganization(subsidyFile, getLocale()));

        add(new Label("title", label));
        add(new Label("label", label));

        final WebMarkupContainer content = new WebMarkupContainer("content");
        content.setOutputMarkupId(true);
        add(content);

        content.add(new FeedbackPanel("messages"));

        final Form<Void> filterForm = new Form<>("filterForm");
        content.add(filterForm);
        example = new Model<>(newExample());

        StatusDetailPanel<SubsidyExample> statusDetailPanel = new StatusDetailPanel<SubsidyExample>("statusDetailsPanel", example,
                new SubsidyExampleConfigurator(), new SubsidyStatusDetailRenderer(), content) {

            @Override
            public List<StatusDetailInfo> loadStatusDetails() {
                return statusDetailBean.getSubsidyStatusDetails(fileId);
            }
        };
        content.add(statusDetailPanel);

        final DataProvider<Subsidy> dataProvider = new DataProvider<Subsidy>() {

            @Override
            protected Iterable<? extends Subsidy> getData(long first, long count) {
                example.getObject().setAsc(getSort().isAscending());
                if (!Strings.isEmpty(getSort().getProperty())) {
                    example.getObject().setOrderByClause(getSort().getProperty());
                }
                example.getObject().setFirst(first);
                example.getObject().setCount(count);
                return subsidyBean.find(example.getObject());
            }

            @Override
            protected Long getSize() {
                example.getObject().setAsc(getSort().isAscending());

                return subsidyBean.getCount(example.getObject());
            }
        };
        dataProvider.setSort("", SortOrder.ASCENDING);

        filterForm.add(new TextField<>("rashFilter", new PropertyModel<>(example, "rash")));
        filterForm.add(new TextField<>("firstNameFilter", new PropertyModel<>(example, "firstName")));
        filterForm.add(new TextField<>("middleNameFilter", new PropertyModel<>(example, "middleName")));
        filterForm.add(new TextField<>("lastNameFilter", new PropertyModel<>(example, "lastName")));
        filterForm.add(new TextField<>("cityFilter", new PropertyModel<>(example, "city")));
        filterForm.add(new TextField<>("streetFilter", new PropertyModel<>(example, "street")));
        filterForm.add(new TextField<>("buildingFilter", new PropertyModel<>(example, "building")));
        filterForm.add(new TextField<>("corpFilter", new PropertyModel<>(example, "corp")));
        filterForm.add(new TextField<>("apartmentFilter", new PropertyModel<>(example, "apartment")));

        filterForm.add(new TextField<>("DAT1", new PropertyModel<>(example, "DAT1")));
        filterForm.add(new TextField<>("DAT2", new PropertyModel<>(example, "DAT2")));
        filterForm.add(new TextField<>("NUMM", new PropertyModel<>(example, "NUMM")));
        filterForm.add(new TextField<>("NM_PAY", new PropertyModel<>(example, "NM_PAY")));
        filterForm.add(new TextField<>("SUMMA", new PropertyModel<>(example, "SUMMA")));
        filterForm.add(new TextField<>("SUBS", new PropertyModel<>(example, "SUBS")));

        filterForm.add(new DropDownChoice<>("statusFilter", new PropertyModel<>(example, "status"),
                Arrays.asList(RequestStatus.values()), new StatusRenderer()).setNullValid(true));

        AjaxLink reset = new AjaxLink("reset") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                filterForm.clearInput();
                clearExample();
                target.add(content);
            }
        };
        filterForm.add(reset);
        final AjaxButton submit = new AjaxButton("submit", filterForm) {

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
        final AddressCorrectionDialog<Subsidy> addressCorrectionDialog = new AddressCorrectionDialog<Subsidy>("addressCorrectionPanel",
                subsidyFile.getUserOrganizationId(), content, statusDetailPanel) {

            @Override
            protected void correctAddress(Subsidy subsidy, AddressEntity entity, Long cityId, Long streetTypeId, Long streetId,
                    Long buildingId, Long apartmentId, Long roomId, Long userOrganizationId)
                    throws DuplicateCorrectionException, MoreOneCorrectionException, NotFoundCorrectionException {
                addressService.correctLocalAddress(subsidy, entity, cityId, streetTypeId, streetId, buildingId, userOrganizationId);
                subsidyBean.markCorrected(subsidy, entity);
            }

            @Override
            protected void closeDialog(AjaxRequestTarget target) {
                super.closeDialog(target);
                dataRowHoverBehavior.deactivateDataRow(target);
            }
        };
        add(addressCorrectionDialog);

        //Панель поиска
        final SubsidyLookupPanel lookupPanel = new SubsidyLookupPanel("lookupPanel", subsidyFile.getUserOrganizationId(),
                content, statusDetailPanel) {

            @Override
            protected void onClose(AjaxRequestTarget target) {
                super.onClose(target);
                dataRowHoverBehavior.deactivateDataRow(target);
            }
        };
        add(lookupPanel);

        //Диалог редактирования
        final SubsidyEditDialog editPanel = new SubsidyEditDialog("edit_panel", content);
        add(editPanel);

        final CheckGroup<Subsidy> checkGroup = new CheckGroup<>("checkGroup", new ArrayList<Subsidy>());
        filterForm.add(checkGroup);

        filterForm.add(new CheckGroupSelector("checkAll", checkGroup));

        final DataView<Subsidy> data = new DataView<Subsidy>("data", dataProvider, 1) {

            @Override
            protected void populateItem(final Item<Subsidy> item) {
                final Subsidy subsidy = item.getModelObject();
                item.setOutputMarkupId(true);

                item.add(new AjaxEventBehavior("onclick") {
                    @Override
                    protected void onEvent(AjaxRequestTarget target) {
                        editPanel.open(target, subsidy);

                        target.add(item.add(AttributeModifier.append("class", "data-row-hover")));
                    }
                });

                item.add(new Check<>("check", Model.of(subsidy), checkGroup));
                item.add(new Label("rash", subsidy.getStringField(SubsidyDBF.RASH)));
                item.add(new Label("firstName", subsidy.getFirstName()));
                item.add(new Label("middleName", subsidy.getMiddleName()));
                item.add(new Label("lastName", subsidy.getLastName()));
                item.add(new Label("city", subsidy.getStringField(SubsidyDBF.NP_NAME, "_CYR")));
                item.add(new Label("street", AddressRenderer.displayStreet(subsidy.getStringField(SubsidyDBF.CAT_V, "_CYR"),
                        subsidy.getStringField(SubsidyDBF.NAME_V, "_CYR"), getLocale())));
                item.add(new Label("building", subsidy.getStringField(SubsidyDBF.BLD, "_CYR")));
                item.add(new Label("corp", subsidy.getStringField(SubsidyDBF.CORP, "_CYR")));
                item.add(new Label("apartment", subsidy.getStringField(SubsidyDBF.FLAT, "_CYR")));
                item.add(DateLabel.forShortStyle("DAT1", Model.of((Date)subsidy.getField(SubsidyDBF.DAT1))));
                item.add(DateLabel.forShortStyle("DAT2", Model.of((Date) subsidy.getField(SubsidyDBF.DAT2))));
                item.add(new Label("NUMM", subsidy.getStringField(SubsidyDBF.NUMM)));
                item.add(new Label("NM_PAY", decimal(subsidy.getStringField(SubsidyDBF.NM_PAY))));
                item.add(new Label("SUMMA", decimal(subsidy.getStringField(SubsidyDBF.SUMMA))));
                item.add(new Label("SUBS", decimal(subsidy.getStringField(SubsidyDBF.SUBS))));

                item.add(new Label("status", statusRenderService.displayStatus(subsidy.getStatus(), getLocale())));
                item.add(new Label("statusDetails", webWarningRenderer.display(subsidy.getWarnings(), getLocale())));

                AjaxLink addressCorrectionLink = new AjaxLink("addressCorrectionLink") {

                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        addressCorrectionDialog.open(target, subsidy, subsidy.getFirstName(),
                                subsidy.getMiddleName(), subsidy.getLastName(),
                                subsidy.getStringField(SubsidyDBF.NP_NAME,"_CYR"), subsidy.getStringField(SubsidyDBF.CAT_V,"_CYR"),
                                subsidy.getStringField(SubsidyDBF.NAME_V,"_CYR"), subsidy.getStringField(SubsidyDBF.BLD,"_CYR"),
                                subsidy.getStringField(SubsidyDBF.CORP,"_CYR"), subsidy.getStringField(SubsidyDBF.FLAT,"_CYR"),
                                subsidy.getCityObjectId(), subsidy.getStreetTypeObjectId(), subsidy.getStreetObjectId(),
                                subsidy.getBuildingObjectId(), null);

                        target.add(item.add(AttributeModifier.append("class", "data-row-hover")));
                    }

                    @Override
                    protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
                        super.updateAjaxAttributes(attributes);

                        attributes.getAjaxCallListeners().add(new CancelEventIfAjaxListener());
                    }

                };
                addressCorrectionLink.setVisible(subsidy.getStatus().isAddressCorrectable());
                item.add(addressCorrectionLink);

                AjaxLink lookup = new AjaxLink("lookup") {

                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        lookupPanel.open(target, subsidy, subsidy.getCityObjectId(), subsidy.getStreetObjectId(),
                                subsidy.getBuildingObjectId(), subsidy.getStringField(SubsidyDBF.FLAT),
                                subsidy.getStringField(SubsidyDBF.RASH),
                                subsidy.getStatus().isImmediatelySearchByAddress());

                        target.add(item.add(AttributeModifier.append("class", "data-row-hover")));
                    }

                    @Override
                    protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
                        super.updateAjaxAttributes(attributes);

                        attributes.getAjaxCallListeners().add(new CancelEventIfAjaxListener());
                    }
                };
                item.add(lookup);

                item.add(new AjaxLink("master_data") {

                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        setResponsePage(SubsidyMasterDataList.class, new PageParameters()
                                .add("subsidy_id", subsidy.getId()).add("request_file_id", subsidy.getRequestFileId()));
                    }

                    @Override
                    protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
                        super.updateAjaxAttributes(attributes);

                        attributes.getAjaxCallListeners().add(new CancelEventIfAjaxListener());
                    }
                });
            }
        };
        checkGroup.add(data);
        checkGroup.add(new PagingNavigator("navigator", data, getPreferencesPage() + fileId, content));

        filterForm.add(new ArrowOrderByBorder("rashHeader", SubsidyBean.OrderBy.RASH.getOrderBy(), dataProvider, data, content));
        filterForm.add(new ArrowOrderByBorder("firstNameHeader", SubsidyBean.OrderBy.FIRST_NAME.getOrderBy(), dataProvider, data, content));
        filterForm.add(new ArrowOrderByBorder("middleNameHeader", SubsidyBean.OrderBy.MIDDLE_NAME.getOrderBy(), dataProvider, data, content));
        filterForm.add(new ArrowOrderByBorder("lastNameHeader", SubsidyBean.OrderBy.LAST_NAME.getOrderBy(), dataProvider, data, content));
        filterForm.add(new ArrowOrderByBorder("cityHeader", SubsidyBean.OrderBy.CITY.getOrderBy(), dataProvider, data, content));
        filterForm.add(new ArrowOrderByBorder("streetHeader", SubsidyBean.OrderBy.STREET.getOrderBy(), dataProvider, data, content));
        filterForm.add(new ArrowOrderByBorder("buildingHeader", SubsidyBean.OrderBy.BUILDING.getOrderBy(), dataProvider, data, content));
        filterForm.add(new ArrowOrderByBorder("corpHeader", SubsidyBean.OrderBy.CORP.getOrderBy(), dataProvider, data, content));
        filterForm.add(new ArrowOrderByBorder("apartmentHeader", SubsidyBean.OrderBy.APARTMENT.getOrderBy(), dataProvider, data, content));

        filterForm.add(new ArrowOrderByBorder("DAT1_header", SubsidyBean.OrderBy.DAT1.getOrderBy(), dataProvider, data, content));
        filterForm.add(new ArrowOrderByBorder("DAT2_header", SubsidyBean.OrderBy.DAT2.getOrderBy(), dataProvider, data, content));
        filterForm.add(new ArrowOrderByBorder("NUMM_header", SubsidyBean.OrderBy.NUMM.getOrderBy(), dataProvider, data, content));
        filterForm.add(new ArrowOrderByBorder("NM_PAY_header", SubsidyBean.OrderBy.NM_PAY.getOrderBy(), dataProvider, data, content));
        filterForm.add(new ArrowOrderByBorder("SUMMA_header", SubsidyBean.OrderBy.SUMMA.getOrderBy(), dataProvider, data, content));
        filterForm.add(new ArrowOrderByBorder("SUBS_header", SubsidyBean.OrderBy.SUBS.getOrderBy(), dataProvider, data, content));

        filterForm.add(new ArrowOrderByBorder("statusHeader", SubsidyBean.OrderBy.STATUS.getOrderBy(), dataProvider, data, content));

        //Назад
        filterForm.add(new Link("back") {

            @Override
            public void onClick() {
                PageParameters params = new PageParameters();
                params.set(SubsidyFileList.SCROLL_PARAMETER, fileId);
                setResponsePage(SubsidyFileList.class, params);
            }
        });

        //Фильтр сумм
        final SubsidyFilterDialog filterDialog = new SubsidyFilterDialog("sum_filter_dialog",
                new PropertyModel<>(example, "sumFilter"), filterForm);
        add(filterDialog);

        filterForm.add(new AjaxLink("sum_filter") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                filterDialog.open(target);
            }
        });

        //Связать
        filterForm.add(new AjaxSubmitLink("bind") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form form) {
                Collection<Subsidy> subsidies = checkGroup.getModelObject();

                for (Subsidy subsidy : subsidies){
                    try {
                        subsidyService.bind(subsidy);

                        if (subsidy.getStatus().isAddressResolved()){
                            info(getStringFormat("info_bound", subsidy.getFio()));
                        }else {
                            error(getStringFormat("error_bound", subsidy.getFio(),
                                    statusRenderService.displayStatus(subsidy.getStatus(), getLocale())));

                        }
                    } catch (Exception e) {
                        error(ExceptionUtil.getCauseMessage(e));
                    }
                }

                checkGroup.getModelObject().clear();
                target.add(content);
            }
        });
    }
}
