package ru.complitex.keconnection.heatmeter.web;

import com.google.common.io.ByteStreams;
import org.apache.wicket.Component;
import org.apache.wicket.ThreadContext;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.AjaxSelfUpdatingTimerBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.feedback.ContainerFeedbackMessageFilter;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.util.time.Duration;
import ru.complitex.common.entity.DomainObject;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.common.service.ContextProcessListener;
import ru.complitex.common.service.IProcessListener;
import ru.complitex.common.service.SessionBean;
import ru.complitex.common.strategy.organization.IOrganizationStrategy;
import ru.complitex.common.web.component.EnumDropDownChoice;
import ru.complitex.common.web.component.MonthDropDownChoice;
import ru.complitex.common.web.component.ShowMode;
import ru.complitex.common.web.component.ajax.AjaxFeedbackPanel;
import ru.complitex.common.web.component.datatable.DataProvider;
import ru.complitex.common.web.component.dateinput.MaskedDateInput;
import ru.complitex.common.web.component.image.StaticImage;
import ru.complitex.common.web.component.paging.PagingNavigator;
import ru.complitex.common.web.component.search.ISearchCallback;
import ru.complitex.common.web.component.search.SearchComponentState;
import ru.complitex.common.web.component.search.WiQuerySearchComponent;
import ru.complitex.keconnection.heatmeter.entity.*;
import ru.complitex.keconnection.heatmeter.service.HeatmeterBean;
import ru.complitex.keconnection.heatmeter.service.HeatmeterBindService;
import ru.complitex.keconnection.heatmeter.service.HeatmeterBindingStatusRenderer;
import ru.complitex.keconnection.heatmeter.service.HeatmeterImportService;
import ru.complitex.keconnection.heatmeter.web.component.heatmeter.bind.HeatmeterBindError;
import ru.complitex.keconnection.heatmeter.web.component.heatmeter.bind.HeatmeterBindPanel;
import ru.complitex.keconnection.heatmeter.web.component.heatmeter.list.ActivateHeatmeterDialog;
import ru.complitex.keconnection.heatmeter.web.component.heatmeter.list.DeactivateHeatmeterDialog;
import ru.complitex.keconnection.heatmeter.web.component.heatmeter.list.HeatmeterItemPanel;
import ru.complitex.keconnection.organization.strategy.KeOrganizationStrategy;
import ru.complitex.template.web.component.toolbar.AddItemButton;
import ru.complitex.template.web.component.toolbar.ToolbarButton;
import ru.complitex.template.web.component.toolbar.UploadButton;
import ru.complitex.template.web.security.SecurityRole;
import ru.complitex.template.web.template.TemplatePage;
import org.odlabs.wiquery.ui.dialog.Dialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.google.common.collect.ImmutableList.of;
import static ru.complitex.common.util.DateUtil.*;
import static ru.complitex.common.util.PageUtil.newSorting;
import static ru.complitex.common.util.PageUtil.newTextFields;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 04.09.12 15:25
 */
@AuthorizeInstantiation(SecurityRole.AUTHORIZED)
public class HeatmeterList extends TemplatePage {
    private final Logger log = LoggerFactory.getLogger(HeatmeterList.class);

    private static final int IMPORT_AJAX_TIMER = 2;
    private static final int BIND_ALL_AJAX_TIMER = 10;

    @EJB
    private HeatmeterBean heatmeterBean;

    @EJB
    private HeatmeterImportService heatmeterImportService;

    @EJB(name = IOrganizationStrategy.BEAN_NAME, beanInterface = IOrganizationStrategy.class)
    private KeOrganizationStrategy organizationStrategy;

    @EJB
    private HeatmeterBindingStatusRenderer heatmeterBindingStatusRenderer;

    @EJB
    private HeatmeterBindService heatmeterBindService;

    @EJB
    private SessionBean sessionBean;

    private Dialog importDialog;
    private final AtomicBoolean stopBindingAllCondition = new AtomicBoolean(true);

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);

        response.render(CssHeaderItem.forReference(new PackageResourceReference(HeatmeterList.class, "HeatmeterList.css")));
    }

    public HeatmeterList() {
        //Title
        add(new Label("title", new ResourceModel("title")));

        //Feedback Panel
        final AjaxFeedbackPanel messages = new AjaxFeedbackPanel("messages");
        add(messages);

        //Filter Model
        FilterWrapper<Heatmeter> filterWrapper = getTemplateSession().getPreferenceFilter(HeatmeterList.class.getName(),
                FilterWrapper.of(new Heatmeter()));
        final IModel<FilterWrapper<Heatmeter>> filterModel = new CompoundPropertyModel<>(filterWrapper);

        final SearchComponentState searchComponentState = heatmeterBean.restoreSearchState(getTemplateSession());

        final WiQuerySearchComponent citySearch = new WiQuerySearchComponent("city_search_panel", searchComponentState,
                of("country", "region", "city"), new ISearchCallback() {
            @Override
            public void found(Component component, Map<String, Long> ids, AjaxRequestTarget target) {
                filterModel.getObject().getMap().putAll(ids);

                heatmeterBean.storeSearchState(getTemplateSession(), searchComponentState);

            }}, ShowMode.ACTIVE, true);
        citySearch.setVisible(searchComponentState.isEmptyState());
        citySearch.setUserPermissionString(sessionBean.getPermissionString("building_address"));

        add(citySearch);

        //Filter Form
        final Form filterForm = new Form<>("filter_form", filterModel);
        filterForm.setOutputMarkupId(true);
        add(filterForm);

        //Filter Find
        AjaxButton filterFind = new AjaxButton("filter_find") {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                target.add(filterForm);
                target.add(messages);

//                getTemplateSession().putPreferenceFilter(HeatmeterList.class.getName(), filterModel.getObject());
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                target.add(messages);
            }
        };
        filterForm.add(filterFind);

        //Filter Fields
        filterForm.add(newTextFields("object.", "ls"));
        filterForm.add(new EnumDropDownChoice<>("object.type", HeatmeterType.class, true));
        filterForm.add(new EnumDropDownChoice<>("object.status", HeatmeterStatus.class, true));
        filterForm.add(new DropDownChoice<>("object.bindingStatus",
                Arrays.asList(HeatmeterBindingStatus.class.getEnumConstants()),
                new IChoiceRenderer<HeatmeterBindingStatus>() {

                    @Override
                    public String getDisplayValue(HeatmeterBindingStatus status) {
                        return heatmeterBindingStatusRenderer.render(status, getLocale());
                    }

                    @Override
                    public String getIdValue(HeatmeterBindingStatus object, int index) {
                        return object.name();
                    }

                    @Override
                    public HeatmeterBindingStatus getObject(String id, IModel<? extends List<? extends HeatmeterBindingStatus>> choices) {
                        return choices.getObject().stream().filter(c -> id.equals(c.name())).findAny().get();
                    }
                }).setNullValid(true));
        filterForm.add(new DropDownChoice<>("object.calculating", Arrays.asList(Boolean.TRUE, Boolean.FALSE),
                new IChoiceRenderer<Boolean>() {

                    @Override
                    public Object getDisplayValue(Boolean object) {
                        return getString(Boolean.class.getSimpleName() + "." + object.toString().toUpperCase());
                    }

                    @Override
                    public String getIdValue(Boolean object, int index) {
                        return String.valueOf(object);
                    }

                    @Override
                    public Boolean getObject(String id, IModel<? extends List<? extends Boolean>> choices) {
                        return Boolean.valueOf(id);
                    }
                }).setNullValid(true));

        filterForm.add(new TextField<>("map.organizationCode"));

        final WiQuerySearchComponent buildingSearch = new WiQuerySearchComponent("building_search_panel", searchComponentState,
                of("street", "building"), new ISearchCallback() {
            @Override
            public void found(Component component, Map<String, Long> ids, AjaxRequestTarget target) {
                filterModel.getObject().getMap().putAll(ids);

                heatmeterBean.storeSearchState(getTemplateSession(), searchComponentState);

            }}, ShowMode.ACTIVE, true, false){
            @Override
            protected Map<String, DomainObject> getState(int index) {
                Map<String, DomainObject> map =  super.getState(index);

                map.put("country", citySearch.getModelObject("country"));
                map.put("region", citySearch.getModelObject("region"));
                map.put("city", citySearch.getModelObject("city"));

                return map;
            }
        };
        buildingSearch.setUserPermissionString(sessionBean.getPermissionString("building_address"));
        filterForm.add(buildingSearch);

        //Filter Reset Button
        AjaxButton filterReset = new AjaxButton("filter_reset") {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                filterModel.setObject(FilterWrapper.of(new Heatmeter()));
                filterForm.clearInput();

                searchComponentState.clear();
                buildingSearch.reinitialize(target);

                heatmeterBean.storeSearchState(getTemplateSession(), searchComponentState);
//                getTemplateSession().putPreferenceFilter(HeatmeterList.class.getName(), filterModel.getObject());

                target.add(filterForm);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                //skip
            }
        };
        filterReset.setDefaultFormProcessing(false);
        filterForm.add(filterReset);

        filterForm.add(new TextField<>("map.buildingCode"));

        IModel<String> tg1FilterModel = new Model<String>() {

            @Override
            public String getObject() {
                Map<String, Object> map = filterModel.getObject().getMap();
                return map != null ? (String) map.get(HeatmeterBean.PAYLOAD1_FILTER_PARAM) : null;
            }

            @Override
            public void setObject(String object) {
                filterModel.getObject().put(HeatmeterBean.PAYLOAD1_FILTER_PARAM, object);
            }
        };
        filterForm.add(new TextField<>("tg1Filter", tg1FilterModel));

        IModel<String> tg2FilterModel = new Model<String>() {

            @Override
            public String getObject() {
                Map<String, Object> map = filterModel.getObject().getMap();
                return map != null ? (String) map.get(HeatmeterBean.PAYLOAD2_FILTER_PARAM) : null;
            }

            @Override
            public void setObject(String object) {
                filterModel.getObject().put(HeatmeterBean.PAYLOAD2_FILTER_PARAM, object);
            }
        };
        filterForm.add(new TextField<>("tg2Filter", tg2FilterModel));

        IModel<String> tg3FilterModel = new Model<String>() {

            @Override
            public String getObject() {
                Map<String, Object> map = filterModel.getObject().getMap();
                return map != null ? (String) map.get(HeatmeterBean.PAYLOAD3_FILTER_PARAM) : null;
            }

            @Override
            public void setObject(String object) {
                filterModel.getObject().put(HeatmeterBean.PAYLOAD3_FILTER_PARAM, object);
            }
        };
        filterForm.add(new TextField<>("tg3Filter", tg3FilterModel));

        IModel<String> inputFilterModel = new Model<String>() {

            @Override
            public String getObject() {
                Map<String, Object> map = filterModel.getObject().getMap();
                return map != null ? (String) map.get(HeatmeterBean.INPUT_FILTER_PARAM) : null;
            }

            @Override
            public void setObject(String object) {
                filterModel.getObject().put(HeatmeterBean.INPUT_FILTER_PARAM, object);
            }
        };
        filterForm.add(new TextField<>("inputFilter", inputFilterModel));

        IModel<String> consumption1FilterModel = new Model<String>() {

            @Override
            public String getObject() {
                Map<String, Object> map = filterModel.getObject().getMap();
                return map != null ? (String) map.get(HeatmeterBean.CONSUMPTION1_FILTER_PARAM) : null;
            }

            @Override
            public void setObject(String object) {
                filterModel.getObject().put(HeatmeterBean.CONSUMPTION1_FILTER_PARAM, object);
            }
        };
        filterForm.add(new TextField<>("consumption1Filter", consumption1FilterModel));

        IModel<Date> beginDateFilterModel = new Model<Date>() {

            @Override
            public Date getObject() {
                Map<String, Object> map = filterModel.getObject().getMap();
                return map != null ? (Date) map.get(HeatmeterBean.PAYLOAD_BEGIN_DATE_FILTER_PARAM) : null;
            }

            @Override
            public void setObject(Date date) {
                filterModel.getObject().put(HeatmeterBean.PAYLOAD_BEGIN_DATE_FILTER_PARAM, date);
            }
        };
        filterForm.add(new MaskedDateInput("beginDateFilter", beginDateFilterModel));

        IModel<Date> readoutDateFilterModel = new Model<Date>() {

            @Override
            public Date getObject() {
                Map<String, Object> map = filterModel.getObject().getMap();
                return map != null ? (Date) map.get(HeatmeterBean.INPUT_READOUT_DATE_FILTER_PARAM) : null;
            }

            @Override
            public void setObject(Date date) {
                filterModel.getObject().put(HeatmeterBean.INPUT_READOUT_DATE_FILTER_PARAM, date);
            }
        };
        filterForm.add(new MaskedDateInput("readoutDateFilter", readoutDateFilterModel));

        //Data Provider
        DataProvider<Heatmeter> dataProvider = new DataProvider<Heatmeter>() {

            @Override
            protected Iterable<Heatmeter> getData(long first, long count) {
                FilterWrapper<Heatmeter> filter = filterModel.getObject();

                filter.setFirst(first);
                filter.setCount(count);
                filter.setSortProperty(getSort().getProperty());
                filter.setAscending(getSort().isAscending());

                if (!sessionBean.isAdmin()){
                    filter.put("organizations", sessionBean.getUserOrganizationTreeString());
                }

                List<Heatmeter> heatmeters = heatmeterBean.getHeatmeters(filter);

                for (Heatmeter heatmeter : heatmeters) {
                    //add new empty payload
                    heatmeter.getPayloads().add(new HeatmeterPayload(heatmeter.getId(), heatmeter.getOm()));

                    //add new empty input
                    HeatmeterInput i = new HeatmeterInput(heatmeter.getId(), heatmeter.getOm());

                    if (heatmeter.getOm() != null) {
                        Date om = heatmeter.getOm();
                        i.setEndDate(getLastDayOfMonth(getYear(om), getMonth(om)+1));
                    }

                    heatmeter.getInputs().add(i);
                }

                return heatmeters;
            }

            @Override
            protected Long getSize() {
                return heatmeterBean.getHeatmeterCount(filterModel.getObject());
            }

            @Override
            public IModel<Heatmeter> model(Heatmeter object) {
                return new Model<>(object);
            }
        };

        dataProvider.setSort("h.id", SortOrder.DESCENDING);

        //Data Container
        final WebMarkupContainer dataContainer = new WebMarkupContainer("data_container");
        dataContainer.setOutputMarkupId(true);
        filterForm.add(dataContainer);

        final HeatmeterBindPanel heatmeterBindPanel = new HeatmeterBindPanel("heatmeterBindPanel") {

            @Override
            protected void onBind(Heatmeter heatmeter, AjaxRequestTarget target) {
                if (stopBindingAllCondition.get()) {
                    target.add(dataContainer);
                }
            }
        };
        add(heatmeterBindPanel);

        final ActivateHeatmeterDialog activateHeatmeterDialog = new ActivateHeatmeterDialog("activateHeatmeterDialog") {

            @Override
            protected void onActivate(Heatmeter heatmeter, AjaxRequestTarget target) {
                if (stopBindingAllCondition.get()) {
                    target.add(dataContainer);
                }
            }
        };
        add(activateHeatmeterDialog);

        final DeactivateHeatmeterDialog deactivateHeatmeterDialog = new DeactivateHeatmeterDialog("deactivateHeatmeterDialog") {

            @Override
            protected void onDeactivate(Heatmeter heatmeter, AjaxRequestTarget target) {
                if (stopBindingAllCondition.get()) {
                    target.add(dataContainer);
                }
            }
        };
        add(deactivateHeatmeterDialog);

        //Data View
        DataView<Heatmeter> dataView = new DataView<Heatmeter>("data_view", dataProvider) {

            @Override
            protected void populateItem(Item<Heatmeter> item) {
                final Heatmeter heatmeter = item.getModelObject();

                item.add(new HeatmeterItemPanel("heatmeterItemPanel", heatmeter) {

                    @Override
                    protected boolean isEditable() {
                        return stopBindingAllCondition.get();
                    }

                    @Override
                    protected void onBindHeatmeter(Heatmeter heatmeter, AjaxRequestTarget target) {
                        heatmeterBindPanel.open(heatmeter, null, target);
                    }

                    @Override
                    protected void onDeactivateHeatmeter(Heatmeter heatmeter, AjaxRequestTarget target) {
                        deactivateHeatmeterDialog.open(heatmeter, target);
                    }

                    @Override
                    protected void onActivateHeatmeter(Heatmeter heatmeter, AjaxRequestTarget target) {
                        activateHeatmeterDialog.open(heatmeter, target);
                    }
                });
            }
        };
        dataContainer.add(dataView);

        //Paging Navigator
        final PagingNavigator paging = new PagingNavigator("paging", dataView, HeatmeterList.class.getName(), filterForm);
        filterForm.add(paging);

        //Sorting
        filterForm.add(newSorting("header.", dataProvider, dataView, filterForm, "address", "org_sc.value", "bc.code", "h.ls",
                "h.type_id", "h.status", "hp.begin_date", "hcons.readout_date"));

        //Import Dialog
        final WebMarkupContainer importDialogContainer = new WebMarkupContainer("import_dialog_container");
        importDialogContainer.setOutputMarkupId(true);
        add(importDialogContainer);

        importDialog = new Dialog("import_dialog") {

            {
                getOptions().putLiteral("width", "auto");
            }
        };
        importDialog.setTitle(getString("import_dialog_title"));
        importDialog.setMinHeight(0);
        importDialogContainer.add(importDialog);

        Form<?> uploadForm = new Form<>("upload_form");
        importDialog.add(uploadForm);

        final FeedbackPanel uploadFormMessages = new FeedbackPanel("uploadFormMessages",
                new ContainerFeedbackMessageFilter(uploadForm));
        uploadFormMessages.setOutputMarkupId(true);
        uploadForm.add(uploadFormMessages);

        final IModel<Integer> beginOmModel = new Model<Integer>(getMonth(getCurrentDate()) + 1);
        MonthDropDownChoice beginOm = new MonthDropDownChoice("beginOm", beginOmModel);
        beginOm.setRequired(true);
        uploadForm.add(beginOm);

        final IModel<Date> beginDateModel = new Model<>(getCurrentDate());
        final WebMarkupContainer beginDateContainer = new WebMarkupContainer("beginDateContainer");
        MaskedDateInput beginDate = new MaskedDateInput("beginDate", beginDateModel);
        beginDate.setRequired(true);
        beginDateContainer.add(beginDate);
        uploadForm.add(beginDateContainer);

        final FileUploadField fileUploadField = new FileUploadField("file_upload_field");
        uploadForm.add(fileUploadField);

        uploadForm.add(new AjaxButton("upload") {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form form) {
                FileUpload fileUpload = fileUploadField.getFileUpload();

                if (fileUpload == null || fileUpload.getClientFileName() == null) {
                    return;
                }

                importDialog.close(target);

                try {
                    InputStream is = fileUpload.getInputStream();
                    ByteArrayInputStream inputStream = new ByteArrayInputStream(ByteStreams.toByteArray(is));

                    final AtomicBoolean stopTimer = new AtomicBoolean(false);

                    ContextProcessListener<HeatmeterWrapper> listener = new ContextProcessListener<HeatmeterWrapper>() {

                        @Override
                        public void onProcessed(HeatmeterWrapper object) {
                            //nothing
                        }

                        @Override
                        public void onSkip(HeatmeterWrapper object) {
                            getSession().info(getStringFormat("info_skipped", object.getLs(), object.getAddress()));
                        }

                        @Override
                        public void onError(HeatmeterWrapper object, Exception e) {
                            getSession().error(getStringFormat("error_upload", e.getMessage()));
                        }

                        @Override
                        public void onDone() {
                            getSession().info(getStringFormat("info_done", getProcessed(), getSkipped(), getErrors()));

                            stopTimer.set(true);
                        }
                    };

                    dataContainer.add(new AjaxSelfUpdatingTimerBehavior(Duration.seconds(IMPORT_AJAX_TIMER)) {

                        @Override
                        protected void onPostProcessTarget(AjaxRequestTarget target) {
                            target.add(messages);
                            target.add(paging);

                            if (stopTimer.get()) {
                                stop(target);
                            }
                        }
                    });
                    target.add(dataContainer);

                    heatmeterImportService.asyncUploadHeatmeters(fileUpload.getClientFileName(), inputStream,
                            newDate(getYear(getCurrentDate()), beginOmModel.getObject()),
                            beginDateModel.getObject(), listener);
                } catch (IOException e) {
                    log.error("Ошибка чтения файла", e);
                }
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                target.add(uploadFormMessages);
            }
        });

        //Bind all section
        final WebMarkupContainer bindAllIndicator = new WebMarkupContainer("bindAllIndicator");
        bindAllIndicator.setOutputMarkupId(true);
        final Image bindAllIndicatorImage = new StaticImage("bindAllIndicatorImage",
                AbstractDefaultAjaxBehavior.INDICATOR);
        bindAllIndicatorImage.setVisible(false);
        bindAllIndicator.add(bindAllIndicatorImage);
        filterForm.add(bindAllIndicator);

        class BindAllTimerBehavior extends AjaxSelfUpdatingTimerBehavior {

            final AtomicBoolean stopCondition;
            final Component bindAll;

            BindAllTimerBehavior(AtomicBoolean stopCondition, Component bindAll) {
                super(Duration.seconds(BIND_ALL_AJAX_TIMER));
                this.stopCondition = stopCondition;
                this.bindAll = bindAll;
            }

            @Override
            protected void onPostProcessTarget(AjaxRequestTarget target) {
                target.add(messages);
                target.add(paging);

                if (stopCondition.get()) {
                    stop(target);
                    getComponent().remove(this);
                    bindAllIndicatorImage.setVisible(false);
                    bindAll.setEnabled(true);
                    target.add(bindAll);
                    target.add(bindAllIndicator);
                }
            }
        }

        AjaxLink<Void> bindAll = new AjaxLink<Void>("bindAll") {

            @Override
            public boolean isVisible() {
                return sessionBean.isAdmin();
            }

            @Override
            public void onClick(AjaxRequestTarget target) {
                if (heatmeterBindService.isProcessing()) {
                    return;
                }

                bindAllIndicatorImage.setVisible(true);
                this.setEnabled(false);
                target.add(bindAllIndicator);
                target.add(this);

                stopBindingAllCondition.getAndSet(false);

                heatmeterBindService.bindAll(new IProcessListener<Heatmeter>() {

                    private int processedCount;
                    private int errorCount;
                    private ThreadContext threadContext = ThreadContext.get(false);

                    @Override
                    public void processed(Heatmeter object) {
                        processedCount++;
                    }

                    @Override
                    public void skip(Heatmeter object) {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }

                    @Override
                    public void error(Heatmeter object, Exception ex) {
                        ThreadContext.restore(threadContext);
                        errorCount++;

                        getSession().error(HeatmeterBindError.message(object, ex, getLocale()));
                    }

                    @Override
                    public void done() {
                        ThreadContext.restore(threadContext);
                        getSession().info(getStringFormat("heatmeter_bind_done", processedCount, errorCount));
                        stopTimer();
                    }

                    private void stopTimer() {
                        stopBindingAllCondition.getAndSet(true);
                    }
                });

                dataContainer.add(new BindAllTimerBehavior(stopBindingAllCondition, this));
                target.add(dataContainer);
            }
        };
        bindAll.setOutputMarkupId(true);
        filterForm.add(bindAll);
    }

    @Override
    protected List<ToolbarButton> getToolbarButtons(String id) {
        return Arrays.asList(
                new AddItemButton(id) {

                    @Override
                    protected void onClick() {
                        setResponsePage(HeatmeterEdit.class);
                    }
                },
                new UploadButton(id, true) {

                    @Override
                    protected void onClick(AjaxRequestTarget target) {
                        importDialog.open(target);
                    }
                });
    }
}
