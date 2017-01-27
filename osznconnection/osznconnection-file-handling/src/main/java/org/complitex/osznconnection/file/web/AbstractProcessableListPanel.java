package org.complitex.osznconnection.file.web;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.datetime.PatternDateConverter;
import org.apache.wicket.datetime.markup.html.basic.DateLabel;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.ISortStateLocator;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.*;
import org.apache.wicket.protocol.ws.api.WebSocketRequestHandler;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.complitex.common.entity.DomainObject;
import org.complitex.common.entity.FilterWrapper;
import org.complitex.common.entity.PreferenceKey;
import org.complitex.common.service.AbstractFilter;
import org.complitex.common.service.ModuleBean;
import org.complitex.common.service.executor.AbstractTaskBean;
import org.complitex.common.service.executor.ExecutorService;
import org.complitex.common.strategy.organization.IOrganizationStrategy;
import org.complitex.common.util.DateUtil;
import org.complitex.common.util.ExceptionUtil;
import org.complitex.common.util.StringUtil;
import org.complitex.common.web.component.DatePicker;
import org.complitex.common.web.component.MonthDropDownChoice;
import org.complitex.common.web.component.YearDropDownChoice;
import org.complitex.common.web.component.ajax.AjaxFeedbackPanel;
import org.complitex.common.web.component.ajax.AjaxLinkLabel;
import org.complitex.common.web.component.datatable.ArrowOrderByBorder;
import org.complitex.common.web.component.datatable.DataProvider;
import org.complitex.common.web.component.organization.OrganizationIdPicker;
import org.complitex.common.web.component.organization.OrganizationPicker;
import org.complitex.common.web.component.organization.OrganizationPickerDialog;
import org.complitex.common.wicket.BroadcastBehavior;
import org.complitex.correction.entity.OrganizationCorrection;
import org.complitex.correction.service.OrganizationCorrectionBean;
import org.complitex.organization_type.strategy.OrganizationTypeStrategy;
import org.complitex.osznconnection.file.entity.AbstractRequest;
import org.complitex.osznconnection.file.entity.AbstractRequestFile;
import org.complitex.osznconnection.file.entity.RequestFile;
import org.complitex.osznconnection.file.service.process.LoadRequestFileBean;
import org.complitex.osznconnection.file.service.process.Process;
import org.complitex.osznconnection.file.service.process.ProcessManagerBean;
import org.complitex.osznconnection.file.service.process.ProcessType;
import org.complitex.osznconnection.file.web.component.DataRowHoverBehavior;
import org.complitex.osznconnection.file.web.component.LoadButton;
import org.complitex.osznconnection.file.web.component.RequestFileHistoryPanel;
import org.complitex.osznconnection.file.web.component.load.RequestFileLoadPanel;
import org.complitex.osznconnection.file.web.component.load.RequestFileLoadPanel.MonthParameterViewMode;
import org.complitex.osznconnection.file.web.component.process.*;
import org.complitex.osznconnection.file.web.pages.util.GlobalOptions;
import org.complitex.osznconnection.organization.strategy.OsznOrganizationStrategy;
import org.complitex.osznconnection.organization_type.strategy.OsznOrganizationTypeStrategy;
import org.complitex.template.web.component.toolbar.ToolbarButton;
import org.complitex.template.web.template.TemplateSession;
import org.odlabs.wiquery.ui.effects.HighlightEffectJavaScriptResourceReference;

import javax.ejb.EJB;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.MessageFormat;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

import static org.complitex.organization_type.strategy.OrganizationTypeStrategy.SERVICE_PROVIDER_TYPE;

public abstract class AbstractProcessableListPanel<R extends AbstractRequestFile, F extends AbstractFilter> extends Panel {

    protected abstract class Column implements Serializable {

        public abstract Component head(ISortStateLocator stateLocator, DataView<?> dataView, Component refresh);

        public abstract Component filter();

        public abstract Component field(Item<R> item);
    }

    @EJB
    private ProcessManagerBean processManagerBean;


    @EJB
    private ModuleBean moduleBean;

    @EJB
    private OrganizationCorrectionBean organizationCorrectionBean;

    @EJB(name = IOrganizationStrategy.BEAN_NAME, beanInterface = IOrganizationStrategy.class)
    private OsznOrganizationStrategy organizationStrategy;

    private RequestFileLoadPanel requestFileLoadPanel;
    private RequestFileHistoryPanel requestFileHistoryPanel;
    private ProcessingManager processingManager;
    private Form<F> form;
    private DataView<R> dataView;
    private DataProvider<R> dataProvider;
    private final List<Column> columns = new ArrayList<Column>();
    private WebMarkupContainer dataViewContainer;
    private AjaxFeedbackPanel messages;

    private SelectManager selectManager;

    private IModel<F> model;

    private ProcessType loadProcessType;
    private ProcessType bindProcessType;
    private ProcessType fillProcessType;
    private ProcessType saveProcessType;

    private Long[] osznOrganizationTypes;

    public AbstractProcessableListPanel(String id, ProcessType loadProcessType, ProcessType bindProcessType,
                                        ProcessType fillProcessType, ProcessType saveProcessType,
                                        Long[] osznOrganizationTypes) {
        super(id);

        this.loadProcessType = loadProcessType;
        this.bindProcessType = bindProcessType;
        this.fillProcessType = fillProcessType;
        this.saveProcessType = saveProcessType;

        this.osznOrganizationTypes = osznOrganizationTypes;

        init();
    }

    protected abstract void load(Long serviceProviderId, Long userOrganizationId, Long organizationId, int year, int monthFrom, int monthTo);

    protected abstract void bind(List<Long> selectedFileIds, Map<Enum<?>, Object> parameters);

    protected abstract void fill(List<Long> selectedFileIds, Map<Enum<?>, Object> parameters);

    protected abstract void save(List<Long> selectedFileIds, Map<Enum<?>, Object> parameters);

    protected void export(AjaxRequestTarget target, List<Long> selectedFileIds){
        //override me
    }

    protected boolean isBindVisible(){
        return true;
    }

    protected boolean isFillVisible(){
        return true;
    }

    protected boolean isExportVisible(){
        return false;
    }

    protected ProcessType getExportProcessType(){
        return ProcessType.EXPORT_SUBSIDY_MASTER_DATA;
    }

    protected abstract MonthParameterViewMode getLoadMonthParameterViewMode();

    protected Class<F> getFilterClass() {
        return (Class<F>) (findParameterizedSuperclass()).getActualTypeArguments()[1];
    }

    private ParameterizedType findParameterizedSuperclass() {
        Type t = getClass().getGenericSuperclass();
        while (!(t instanceof ParameterizedType)) {
            t = ((Class) t).getGenericSuperclass();
        }
        return (ParameterizedType) t;
    }

    protected abstract String getPreferencePage();

    protected F newFilter() {
        try {
            F filter = getFilterClass().newInstance();
            filter.setSortProperty("loaded");

            return filter;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected abstract Long getCount(F filter);

    protected abstract List<R> getObjects(F filter);

    protected abstract R getObject(long id);

    protected abstract void delete(R object);

    protected abstract RequestFile getRequestFile(R object);

    protected void logSuccessfulDeletion(R object) {
    }

    protected void logFailDeletion(R object, Exception e) {
    }

    protected final void addColumn(Column column) {
        columns.add(column);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        //Дополнительные колонки

        //дополнительные фильтры
        for (Column column : columns) {
            form.add(column.filter());
        }

        //дополнительные заголовки
        for (Column column : columns) {
            form.add(column.head(dataProvider, dataView, form));
        }
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        renderResources(response);
    }

    public static void renderResources(IHeaderResponse response) {
        response.render(JavaScriptHeaderItem.forReference(HighlightEffectJavaScriptResourceReference.get()));
        response.render(JavaScriptHeaderItem.forReference(new PackageResourceReference(AbstractProcessableListPanel.class,
                AbstractProcessableListPanel.class.getSimpleName() + ".js")));
        response.render(CssHeaderItem.forReference(new PackageResourceReference(AbstractProcessableListPanel.class,
                AbstractProcessableListPanel.class.getSimpleName() + ".css")));
    }

    private void init() {
        if (osznOrganizationTypes == null){
            osznOrganizationTypes = new Long[]{
                    OsznOrganizationTypeStrategy.SUBSIDY_DEPARTMENT_TYPE,
                    OsznOrganizationTypeStrategy.PRIVILEGE_DEPARTMENT_TYPE
            };
        }

        add(new DataRowHoverBehavior());

        processingManager = new ProcessingManager(loadProcessType, bindProcessType, fillProcessType, saveProcessType);

        messages = new AjaxFeedbackPanel("messages");
        add(messages);

        //Фильтр модель
        F filter = getSession().getPreferenceObject(getPreferencePage(), PreferenceKey.FILTER_OBJECT, newFilter()); //todo preference sort test
        model = new CompoundPropertyModel<>(filter);

        //Фильтр форма
        form = new Form<>("form", model);
        form.setOutputMarkupId(true);
        add(form);

        AjaxLink<Void> filter_reset = new AjaxLink<Void>("filter_reset") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                F filterObject = newFilter();
                model.setObject(filterObject);
                target.add(form);
            }
        };
        form.add(filter_reset);

        AjaxSubmitLink find = new AjaxSubmitLink("find", form) {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                target.add(form);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                target.add(messages);
            }
        };
        form.add(find);

        //Select all checkbox
        form.add(new SelectAllCheckBoxPanel("selectAllCheckBoxPanel", processingManager).setOutputMarkupId(true));

        //Id
        form.add(new TextField<String>("id"));

        //Дата загрузки
        form.add(new DatePicker("loaded").add(new OnChangeAjaxBehavior() {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                //update model
            }
        }));

        Model<R> selectedRequestFileModel = new Model<>();

        OrganizationPickerDialog serviceProviderDialog = new OrganizationPickerDialog("organization_correction_dialog",
                Model.of(new DomainObject()), OsznOrganizationTypeStrategy.SERVICE_PROVIDER_TYPE) {
            @Override
            protected void onSelect(AjaxRequestTarget target) {
                R requestFile = selectedRequestFileModel.getObject();
                DomainObject organization = getOrganizationModel().getObject();

                OrganizationCorrection correction = new OrganizationCorrection(null, organization.getObjectId(),
                        requestFile.getEdrpou(), requestFile.getOrganizationId(), requestFile.getUserOrganizationId(),
                        moduleBean.getModuleId());

                if (organizationCorrectionBean.getOrganizationCorrectionsCount(FilterWrapper.of(correction)) == 0){
                    organizationCorrectionBean.save(correction);

                    getSession().info(String.format(getString("info_correction_added"),
                            organizationStrategy.displayShortNameAndCode(organization, getLocale())));
                }else {
                    getSession().error(getString("error_correction_exist"));
                }

                target.add(getMessages(), getDataViewContainer());
            }
        };
        add(serviceProviderDialog);

        //ПУ
        form.add(new OrganizationPicker("serviceProvider",
                new PropertyModel<>(getModel(), "serviceProvider"),
                SERVICE_PROVIDER_TYPE){
            @Override
            protected void onSelect(AjaxRequestTarget target) {
                AbstractProcessableListPanel.this.getModel().getObject()
                        .setEdrpou(getOrganizationModel().getObject().getStringValue(OsznOrganizationStrategy.EDRPOU));
            }
        });

        //ОСЗН
        form.add(new OrganizationIdPicker("organization",
                new PropertyModel<>(model, "organizationId"),
                osznOrganizationTypes));

        // Организация пользователя
        form.add(new OrganizationIdPicker("userOrganization",
                new PropertyModel<>(model, "userOrganizationId"),
                OrganizationTypeStrategy.USER_ORGANIZATION_TYPE));

        //Месяц
        form.add(new MonthDropDownChoice("month").setNullValid(true));

        //Год
        form.add(new YearDropDownChoice("year").setNullValid(true));

        //Статус
        form.add(new RequestFileStatusFilter("status"));

        //Модель выбранных элементов списка.
        selectManager = new SelectManager();

        //Модель данных списка
        dataProvider = new DataProvider<R>() {

            @Override
            protected Iterable<R> getData(long first, long count) {
                final F filter = model.getObject();

                getSession().putPreferenceObject(getPreferencePage(), PreferenceKey.FILTER_OBJECT, filter);

                //prepare filter object
                filter.setFirst(first);
                filter.setCount(count);
                filter.setSortProperty(getSort().getProperty());
                filter.setAscending(getSort().isAscending());

                List<R> objects = getObjects(filter);

                selectManager.initializeSelectModels(objects);

                return objects;
            }

            @Override
            protected Long getSize() {
                return AbstractProcessableListPanel.this.getCount(model.getObject());
            }
        };
        dataProvider.setSort(filter.getSortProperty(), SortOrder.DESCENDING);

        //Контейнер для ajax
        dataViewContainer = new WebMarkupContainer("objects_container");
        dataViewContainer.setOutputMarkupId(true);
        form.add(dataViewContainer);

        //Таблица файлов запросов
        dataView = new DataView<R>("objects", dataProvider) {
            @Override
            protected Item<R> newItem(String id, int index, IModel<R> model) {
                Item<R> item =  super.newItem("item" + model.getObject().getId(), index, model);

                item.setOutputMarkupId(true);

                return item;
            }

            @Override
            protected void populateItem(Item<R> item) {
                item.add(new ItemCheckBoxPanel<R>("itemCheckBoxPanel", processingManager, selectManager, item.getModel()));

                //Идентификатор файла
                item.add(new Label("id", new PropertyModel<>(item.getModel(), "id")));

                //Дата загрузки
                item.add(new DateLabel("loaded", new PropertyModel<>(item.getModel(), "loaded"),
                        new PatternDateConverter("dd.MM.yy HH:mm:ss", true)));

                //ПУ
                item.add(new AjaxLinkLabel("service_provider", new LoadableDetachableModel<String>() {
                    @Override
                    protected String load() {
                        R rf = item.getModelObject();

                        Long organizationId = organizationStrategy.getServiceProviderId(rf.getEdrpou(),
                                rf.getOrganizationId(), rf.getUserOrganizationId());

                        if (organizationId != null){
                            return organizationStrategy.displayDomainObject(
                                    organizationStrategy.getDomainObject(organizationId), getLocale());
                        }else {
                            return rf.getEdrpou();
                        }
                    }
                }) {
                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        serviceProviderDialog.open(target);
                        selectedRequestFileModel.setObject(item.getModelObject());
                    }

                    @Override
                    public boolean isEnabled() {
                        return true;
//                        return organizationStrategy.getServiceProviderId(rf.getEdrpou(),
//                                rf.getOrganizationId(), rf.getUserOrganizationId()) == null;
                    }
                });

                //ОСЗН
                item.add(new Label("organization", new LoadableDetachableModel<String>() {
                    @Override
                    protected String load() {
                        return organizationStrategy.displayDomainObject(item.getModelObject().getOrganizationId(), getLocale());
                    }
                }));

                //Организация пользователя
                item.add(new Label("userOrganization", new LoadableDetachableModel<String>() {
                    @Override
                    protected String load() {
                        return organizationStrategy.displayDomainObject(item.getModelObject().getUserOrganizationId(), getLocale());
                    }
                }));

                item.add(new Label("month", new LoadableDetachableModel<String>() {

                    @Override
                    protected String load() {
                        return DateUtil.displayMonth(item.getModelObject().getMonth(), getLocale());
                    }
                }));
                item.add(new Label("year", new LoadableDetachableModel<String>() {
                    @Override
                    protected String load() {
                        return StringUtil.valueOf(item.getModelObject().getYear());
                    }
                }));

                //Количество загруженных записей
                item.add(new Label("loaded_record_count", new PropertyModel<String>(item.getModel(), "loadedRecordCount")));

                //Количество связанных записей
                item.add(new Label("binded_record_count", new PropertyModel<String>(item.getModel(), "bindedRecordCount")));

                //Количество обработанных записей
                item.add(new Label("filled_record_count", new PropertyModel<String>(item.getModel(), "filledRecordCount")));

                //Статус
                AjaxLink history = new AjaxLink("history") {
                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        requestFileHistoryPanel.open(target, getRequestFile(item.getModelObject()));

                    }
                };
                item.add(history);

                history.add(new ItemStatusLabel("status", processingManager));

                //Дополнительные поля
                for (Column column : columns) {
                    item.add(column.field(item));
                }
            }
        };
        dataViewContainer.add(dataView);

        //Постраничная навигация
        dataViewContainer.add(new ProcessPagingNavigator("paging", dataView, getPreferencePage(), selectManager, form));

        //Сортировка
        form.add(new ArrowOrderByBorder("header.service_provider", "service_provider", dataProvider, dataView, form));
        form.add(new ArrowOrderByBorder("header.id", "id", dataProvider, dataView, form));
        form.add(new ArrowOrderByBorder("header.loaded", "loaded", dataProvider, dataView, form));
        form.add(new ArrowOrderByBorder("header.organization", "organization_id", dataProvider, dataView, form));
        form.add(new ArrowOrderByBorder("header.user_organization", "user_organization_id", dataProvider, dataView, form));
        form.add(new ArrowOrderByBorder("header.month", "month", dataProvider, dataView, form));
        form.add(new ArrowOrderByBorder("header.year", "year", dataProvider, dataView, form));
        form.add(new ArrowOrderByBorder("header.status", "status", dataProvider, dataView, form));

        //Контейнер чекбокса "Переписать л/с ПУ" для ajax
        WebMarkupContainer optionContainer = new WebMarkupContainer("options");
        optionContainer.setVisibilityAllowed(true);
        form.add(optionContainer);

        optionContainer.add(new CheckBox("update_pu_account", new Model<>(
                getSessionParameter(GlobalOptions.UPDATE_PU_ACCOUNT))).add(new AjaxFormComponentUpdatingBehavior("change") {

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                putSessionParameter(GlobalOptions.UPDATE_PU_ACCOUNT, !getSessionParameter(GlobalOptions.UPDATE_PU_ACCOUNT));
            }
        }));

        //Контейнер кнопок для ajax
        WebMarkupContainer buttons = new WebMarkupContainer("buttons");
        buttons.setOutputMarkupId(true);
        form.add(buttons);

        //Загрузить
        buttons.add(new AjaxSubmitLink("load") {

            @Override
            public void onSubmit(AjaxRequestTarget target, Form form) {
                requestFileLoadPanel.open(target);
            }
        });

        //Связать
        buttons.add(new SubmitLink("bind") {

            @Override
            public void onSubmit() {
                bind(selectManager.getSelectedFileIds(), buildCommandParameters());
                selectManager.clearSelection();
            }

            @Override
            public boolean isVisible() {
                return isBindVisible();
            }
        });

        //Обработать
        buttons.add(new SubmitLink("process") {

            @Override
            public void onSubmit() {
                fill(selectManager.getSelectedFileIds(), buildCommandParameters());
                selectManager.clearSelection();
            }

            @Override
            public boolean isVisible() {
                return isFillVisible();
            }
        });

        //Выгрузить
        buttons.add(new SubmitLink("save") {

            @Override
            public void onSubmit() {
                save(selectManager.getSelectedFileIds(), buildCommandParameters());
                selectManager.clearSelection();
            }
        });

        //Экспортировать
        buttons.add(new AjaxSubmitLink("export") {

            @Override
            public void onSubmit(AjaxRequestTarget target, Form form) {
                export(target, selectManager.getSelectedFileIds());
                selectManager.clearSelection();
            }

            @Override
            public boolean isVisible() {
                return isExportVisible();
            }
        });

        //Удалить
        buttons.add(new DeleteButton("delete") {

            @Override
            public void onSubmit(AjaxRequestTarget target, Form form) {
                for (long objectId : selectManager.getSelectedFileIds()) {
                    final R object = getObject(objectId);

                    if (object != null) {
                        try {
                            delete(object);

                            selectManager.remove(objectId);

                            info(MessageFormat.format(getString("info.deleted"), object.getObjectName()));
                            logSuccessfulDeletion(object);
                        } catch (Exception e) {
                            error(MessageFormat.format(getString("error.delete"), object.getObjectName()));
                            logFailDeletion(object, e);
                            break;
                        }
                    }
                }
                target.add(form);
                target.add(messages);
            }
        });

        //Отменить загрузку
        buttons.add(new AjaxLink("load_cancel") {

            @Override
            public boolean isVisible() {
                return processManagerBean.isProcessing(loadProcessType);
            }

            @Override
            public void onClick(AjaxRequestTarget target) {
                processManagerBean.cancel(loadProcessType);
                info(getString("load_process.canceling"));
                target.add(form);
            }
        });

        //Отменить связывание
        buttons.add(new AjaxLink("bind_cancel") {

            @Override
            public boolean isVisible() {
                return processManagerBean.isProcessing(bindProcessType);
            }

            @Override
            public void onClick(AjaxRequestTarget target) {
                processManagerBean.cancel(bindProcessType);
                info(getString("bind_process.canceling"));
                target.add(form);
            }
        });

        //Отменить обработку
        buttons.add(new AjaxLink("fill_cancel") {

            @Override
            public boolean isVisible() {
                return processManagerBean.isProcessing(fillProcessType);
            }

            @Override
            public void onClick(AjaxRequestTarget target) {
                processManagerBean.cancel(fillProcessType);
                info(getString("fill_process.canceling"));
                target.add(form);
            }
        });

        //Отменить выгрузку
        buttons.add(new AjaxLink("save_cancel") {

            @Override
            public boolean isVisible() {
                return processManagerBean.isProcessing(saveProcessType);
            }

            @Override
            public void onClick(AjaxRequestTarget target) {
                processManagerBean.cancel(saveProcessType);
                info(getString("save_process.canceling"));
                target.add(form);
            }
        });

        //Отменить экспорт
        buttons.add(new AjaxLink("export_cancel") {

            @Override
            public boolean isVisible() {
                return processManagerBean.isProcessing(getExportProcessType());
            }

            @Override
            public void onClick(AjaxRequestTarget target) {
                processManagerBean.cancel(getExportProcessType());
                info(getString("export_process.canceling"));
                target.add(form);
            }
        });

        //Диалог загрузки
        requestFileLoadPanel = new RequestFileLoadPanel("load_panel", new ResourceModel("load_panel_title"),
                getLoadMonthParameterViewMode(), osznOrganizationTypes) {
            @Override
            protected void load(Long serviceProviderId, Long userOrganizationId, Long organizationId,
                                int year, int monthFrom, int monthTo, AjaxRequestTarget target) {
                AbstractProcessableListPanel.this.load(serviceProviderId, userOrganizationId, organizationId,
                        year, monthFrom, monthTo);
            }
        };
        add(requestFileLoadPanel);

        add(requestFileHistoryPanel = new RequestFileHistoryPanel("history_panel"));

        //Messages
        add(new BroadcastBehavior(ExecutorService.class) {
            @Override
            protected void onBroadcast(WebSocketRequestHandler handler, String key, Object payload) {
                String time = LocalTime.now().toString() + " ";

                if (payload instanceof Process){
                    Process process = (Process) payload;
                    String prefix = process.getProcessType().name().split("_")[0].toLowerCase();

                    switch (key){
                        case "onBegin":
                            info(time + getString(prefix + "_process.begin"));

                            break;
                        case "onComplete":
                            info(time + getString(prefix + "_process.completed", process.getSuccessCount(), process.getSkippedCount(), process.getErrorCount()));

                            break;
                        case "onCancel":
                            info(time + getString(prefix + "_process.canceled", process.getSuccessCount(), process.getSkippedCount(), process.getErrorCount()));

                            break;
                        case "onCriticalError":
                            error(process.getErrorMessage());
                            info(time + getString(prefix + "_process.critical_error", process.getSuccessCount(), process.getSkippedCount(), process.getErrorCount()));

                            break;
                    }

                    handler.add(messages, dataViewContainer, buttons);
                }

                if (payload instanceof AbstractRequestFile){
                    AbstractRequestFile object = (AbstractRequestFile) payload;

                    switch (key){
                        case "onSuccess":
                        case "onSkip":
                            handler.add(messages);

                            break;
                        case "onError":
                            error(time + object.getErrorMessage());
                            handler.add(messages);

                            break;
                    }

                    Item item = (Item) dataView.get("item" + object.getId());

                    if (item != null){
                        R rf = getRequestFile(object.getId()); //todo add service to update processed count

                        if (rf != null){
                            //noinspection unchecked
                            item.setModelObject(rf);
                        }

                        handler.add(item);
                    }
                }
            }
        });

        add(new BroadcastBehavior(AbstractTaskBean.class) {
            private AtomicLong lastUpdate = new AtomicLong(System.currentTimeMillis());

            @Override
            protected void onBroadcast(WebSocketRequestHandler handler, String key, Object payload) {
                if ("onRequest".equals(key) && System.currentTimeMillis() - lastUpdate.get() > 250){
                    lastUpdate.set(System.currentTimeMillis());

                    AbstractRequest request = (AbstractRequest) payload;

                    Long id = request.getGroupId() != null ? request.getGroupId() : request.getRequestFileId();

                    Item item = (Item) dataView.get("item" + id);

                    if (item != null){
                        R rf = getRequestFile(id);

                        if (rf != null){
                            //noinspection unchecked
                            item.setModelObject(rf);
                        }

                        handler.add(item);
                    }
                }
            }
        });

        add(new BroadcastBehavior(ProcessManagerBean.class) {
            @Override
            protected void onBroadcast(WebSocketRequestHandler handler, String key, Object payload) {
                if (payload instanceof Exception){
                    error(ExceptionUtil.getCauseMessage((Exception) payload));

                    handler.add(messages);
                }
            }
        });

        add(new BroadcastBehavior(LoadRequestFileBean.class) {
            @Override
            protected void onBroadcast(WebSocketRequestHandler handler, String key, Object payload) {
                if ("onUpdate".equals(key)){
                    handler.add(messages, dataViewContainer);
                }
            }
        });
    }

    private Boolean getSessionParameter(Enum<?> key) {
        return getSession().getPreferenceBoolean(TemplateSession.GLOBAL_PAGE, key, false);
    }

    private void putSessionParameter(Enum<?> key, Boolean value) {
        getSession().putPreference(TemplateSession.GLOBAL_PAGE, key, value, false);
    }

    @Override
    public TemplateSession getSession() {
        return (TemplateSession) super.getSession();
    }

    private Map<Enum<?>, Object> buildCommandParameters() {
        Map<Enum<?>, Object> commandParameters = new HashMap<>();
        commandParameters.put(GlobalOptions.UPDATE_PU_ACCOUNT, getSessionParameter(GlobalOptions.UPDATE_PU_ACCOUNT));
        return commandParameters;
    }

    protected void showMessages(AjaxRequestTarget target) {
    }

    public List<ToolbarButton> getToolbarButtons(String id) {
        return Collections.singletonList(new LoadButton(id) {
            @Override
            protected void onClick(AjaxRequestTarget target) {
                requestFileLoadPanel.open(target);
            }
        });
    }


    protected WebMarkupContainer getDataViewContainer() {
        return dataViewContainer;
    }

    protected AjaxFeedbackPanel getMessages() {
        return messages;
    }

    public IModel<F> getModel() {
        return model;
    }

    public RequestFileLoadPanel getRequestFileLoadPanel(){
        return requestFileLoadPanel;
    }

    private String getString(String key, Object... parameters) {
        return MessageFormat.format(getString(key), parameters);
    }

    private R getRequestFile(Long id){
        F filter = newFilter();
        filter.setId(id);
        filter.setCount(1);

        List<R> list =  getObjects(filter);

        return list != null && !list.isEmpty() ? list.get(0) : null;
    }
}
