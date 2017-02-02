package org.complitex.osznconnection.file.web.pages.facility;

import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.datetime.PatternDateConverter;
import org.apache.wicket.datetime.markup.html.basic.DateLabel;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.protocol.ws.api.WebSocketRequestHandler;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.complitex.common.service.executor.ExecutorService;
import org.complitex.common.util.DateUtil;
import org.complitex.common.util.ExceptionUtil;
import org.complitex.common.util.StringUtil;
import org.complitex.common.web.component.BookmarkablePageLinkPanel;
import org.complitex.common.web.component.DatePicker;
import org.complitex.common.web.component.MonthDropDownChoice;
import org.complitex.common.web.component.YearDropDownChoice;
import org.complitex.common.web.component.ajax.AjaxFeedbackPanel;
import org.complitex.common.web.component.datatable.ArrowOrderByBorder;
import org.complitex.common.web.component.organization.OrganizationIdPicker;
import org.complitex.common.wicket.BroadcastBehavior;
import org.complitex.organization_type.strategy.OrganizationTypeStrategy;
import org.complitex.osznconnection.file.entity.AbstractRequestFile;
import org.complitex.osznconnection.file.entity.RequestFile;
import org.complitex.osznconnection.file.entity.RequestFileFilter;
import org.complitex.osznconnection.file.entity.RequestFileType;
import org.complitex.osznconnection.file.service.file_description.RequestFileDescriptionBean;
import org.complitex.osznconnection.file.service.process.LoadRequestFileBean;
import org.complitex.osznconnection.file.service.process.Process;
import org.complitex.osznconnection.file.service.process.ProcessManagerService;
import org.complitex.osznconnection.file.service.process.ProcessType;
import org.complitex.osznconnection.file.web.AbstractProcessableListPanel;
import org.complitex.osznconnection.file.web.component.LoadButton;
import org.complitex.osznconnection.file.web.component.load.RequestFileLoadPanel;
import org.complitex.osznconnection.file.web.component.load.RequestFileLoadPanel.MonthParameterViewMode;
import org.complitex.osznconnection.file.web.component.process.*;
import org.complitex.osznconnection.organization_type.strategy.OsznOrganizationTypeStrategy;
import org.complitex.template.web.component.toolbar.ToolbarButton;
import org.complitex.template.web.security.SecurityRole;
import org.complitex.template.web.template.TemplatePage;

import javax.ejb.EJB;
import java.time.LocalTime;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Artem
 */
@AuthorizeInstantiation(SecurityRole.AUTHORIZED)
public abstract class AbstractReferenceBookFileList extends TemplatePage {

    @EJB
    private RequestFileDescriptionBean requestFileDescriptionBean;

    private RequestFileLoadPanel requestFileLoadPanel;

    protected AbstractReferenceBookFileList() {
        init();
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        AbstractProcessableListPanel.renderResources(response);
    }

    protected abstract RequestFileType getRequestFileType();

    protected abstract void load(Long userOrganizationId, Long organizationId, int year, int monthFrom, int monthTo);

    protected abstract ProcessType getLoadProcessType();

    protected abstract Class<? extends Page> getItemsPage();

    private boolean hasFieldDescription() {
        return requestFileDescriptionBean.getFileDescription(getRequestFileType()) != null;
    }

    protected void init() {
        IModel<String> titleModel = new ResourceModel("title");
        add(new Label("title", titleModel));
        add(new Label("header", titleModel));

        final AjaxFeedbackPanel messages = new AjaxFeedbackPanel("messages");
        add(messages);

        //Фильтр модель
        final RequestFileFilter filter = getFilterObject(newFilter());
        final IModel<RequestFileFilter> model = new CompoundPropertyModel<>(filter);

        //Фильтр форма
        final Form<RequestFileFilter> form = new Form<>("form", model);
        form.setOutputMarkupId(true);
        add(form);

        AjaxLink<Void> reset = new AjaxLink<Void>("reset") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                model.setObject(newFilter());
                target.add(form);
            }
        };
        form.add(reset);

        AjaxButton find = new AjaxButton("find", form) {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                target.add(form);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
            }
        };
        form.add(find);

        //Select all checkbox
        form.add(new SelectAllCheckBoxPanel("selectAllCheckBoxPanel"));

        //Id
        form.add(new TextField<String>("id"));

        //Дата загрузки
        form.add(new DatePicker<Date>("loaded"));

        //Имя
        form.add(new TextField<String>("name"));

        //Осзн
        form.add(new OrganizationIdPicker("organization",
                new PropertyModel<>(model, "organizationId"),
                new Long[]{OsznOrganizationTypeStrategy.PRIVILEGE_DEPARTMENT_TYPE}));

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
        final SelectManager selectManager = new SelectManager();

        //Модель данных списка
        final RequestFileDataProvider dataProvider = new RequestFileDataProvider(this, model, selectManager);
        dataProvider.setSort("loaded", SortOrder.DESCENDING);

        //Контейнер для ajax
        final WebMarkupContainer dataViewContainer = new WebMarkupContainer("request_files_container");
        dataViewContainer.setOutputMarkupId(true);
        form.add(dataViewContainer);

        //Таблица файлов запросов
        DataView<RequestFile> dataView = new DataView<RequestFile>("request_files", dataProvider) {

            @Override
            protected void populateItem(Item<RequestFile> item) {
                //Выбор файлов
                item.add(new ItemCheckBoxPanel<>("itemCheckBoxPanel", selectManager, item.getModel()));

                //Идентификатор файла
                item.add(new Label("id", new PropertyModel<>(item.getModel(), "id")));

                //Дата загрузки
                item.add(new DateLabel("loaded", new PropertyModel<>(item.getModel(), "loaded"),
                        new PatternDateConverter("dd.MM.yy HH:mm:ss", true)));

                item.add(new BookmarkablePageLinkPanel<>("name", item.getModelObject().getFullName(), getItemsPage(),
                        new PageParameters().set("request_file_id", item.getModelObject().getId()))); //todo model

                //ОСЗН
                item.add(new ItemOrganizationLabel("organization", item.getModelObject().getOrganizationId()));

                //Организация пользователя
                item.add(new ItemOrganizationLabel("userOrganization", item.getModelObject().getUserOrganizationId()));

                item.add(new Label("month", DateUtil.displayMonth(item.getModelObject().getBeginDate(), getLocale())));
                item.add(new Label("year", DateUtil.getYear(item.getModelObject().getBeginDate()) + ""));

                item.add(new Label("dbf_record_count", StringUtil.valueOf(item.getModelObject().getDbfRecordCount())));

                //Количество загруженных записей
                item.add(new Label("loaded_record_count", new PropertyModel<>(item.getModel(), "loadedRecordCount")));

                //Статус
                item.add(new ItemStatusLabel("status"));
            }
        };
        dataViewContainer.add(dataView);

        //Постраничная навигация
        ProcessPagingNavigator pagingNavigator = new ProcessPagingNavigator("paging", dataView, getPreferencesPage(),
                selectManager, form);
        form.add(pagingNavigator);

        //Сортировка
        form.add(new ArrowOrderByBorder("header.id", "id", dataProvider, dataView, form));
        form.add(new ArrowOrderByBorder("header.loaded", "loaded", dataProvider, dataView, form));
        form.add(new ArrowOrderByBorder("header.name", "name", dataProvider, dataView, form));
        form.add(new ArrowOrderByBorder("header.organization", "organization_id", dataProvider, dataView, form));
        form.add(new ArrowOrderByBorder("header.user_organization", "user_organization_id", dataProvider, dataView, form));
        form.add(new ArrowOrderByBorder("header.month", "month", dataProvider, dataView, form));
        form.add(new ArrowOrderByBorder("header.year", "year", dataProvider, dataView, form));
        form.add(new ArrowOrderByBorder("header.status", "status", dataProvider, dataView, form));

        WebMarkupContainer buttons = new WebMarkupContainer("buttons");
        buttons.setOutputMarkupId(true);
        form.add(buttons);

        //Загрузить
        buttons.add(new AjaxLink<Void>("load") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                requestFileLoadPanel.open(target);
            }
        });

        //Удалить
        buttons.add(new RequestFileDeleteButton("delete", selectManager, form, messages) {

            @Override
            protected Class<?> getLoggerControllerClass() {
                return AbstractReferenceBookFileList.this.getClass();
            }

            @Override
            protected void logSuccess(RequestFile requestFile) {
                log().info("Request file of type {} (ID : {}, full name: '{}') has been deleted.",
                        new Object[]{getRequestFileType(), requestFile.getId(), requestFile.getFullName()});
            }

            @Override
            protected void logError(RequestFile requestFile, Exception e) {
                log().error("Couldn't delete request file of type " + getRequestFileType()
                        + " (ID: " + requestFile.getId() + ", full name: '" + requestFile.getFullName() + "').", e);
            }
        });

        //Диалог загрузки
        requestFileLoadPanel = new RequestFileLoadPanel("load_panel", new ResourceModel("load_panel_title"),
                MonthParameterViewMode.EXACT, new Long[]{OsznOrganizationTypeStrategy.PRIVILEGE_DEPARTMENT_TYPE}) {
            @Override
            protected void load(Long serviceProviderId, Long userOrganizationId, Long organizationId, int year, int monthFrom, int monthTo, AjaxRequestTarget target) {
                selectManager.clearSelection();
                target.add(form);

                AbstractReferenceBookFileList.this.load(userOrganizationId, organizationId, year, monthFrom, monthTo);
            }
        };

        add(requestFileLoadPanel);

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
                            info(time + getStringFormat(prefix + "_process.completed", process.getSuccessCount(),
                                    process.getSkippedCount(), process.getErrorCount()));

                            break;
                        case "onCancel":
                            info(time + getStringFormat(prefix + "_process.canceled", process.getSuccessCount(),
                                    process.getSkippedCount(), process.getErrorCount()));

                            break;
                        case "onCriticalError":
                            error(process.getErrorMessage());
                            info(time + getStringFormat(prefix + "_process.critical_error", process.getSuccessCount(),
                                    process.getSkippedCount(), process.getErrorCount()));

                            break;
                    }

                    handler.add(messages, dataViewContainer, buttons);
                }

                if (payload instanceof AbstractRequestFile){
                    AbstractRequestFile object = (AbstractRequestFile) payload;

                    if ("onError".equals(key)){
                        error(time + object.getErrorMessage());
                    }

                    handler.add(messages, dataViewContainer, buttons);
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

        add(new BroadcastBehavior(ProcessManagerService.class) {
            @Override
            protected void onBroadcast(WebSocketRequestHandler handler, String key, Object payload) {
                if (payload instanceof Exception){
                    error(ExceptionUtil.getCauseMessage((Exception) payload));

                    handler.add(messages);
                }
            }
        });
    }

    private RequestFileFilter newFilter() {
        final RequestFileFilter filter = new RequestFileFilter();
        filter.setType(getRequestFileType());
        return filter;
    }

    @Override
    protected List<ToolbarButton> getToolbarButtons(String id) {
        return Collections.singletonList(new LoadButton(id) {
            @Override
            protected void onClick(AjaxRequestTarget target) {
                requestFileLoadPanel.open(target);
            }
        });
    }
}
