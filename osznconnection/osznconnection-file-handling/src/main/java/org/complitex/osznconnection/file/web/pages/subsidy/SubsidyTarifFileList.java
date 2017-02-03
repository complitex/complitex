package org.complitex.osznconnection.file.web.pages.subsidy;

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
import org.apache.wicket.model.*;
import org.apache.wicket.protocol.ws.api.WebSocketRequestHandler;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.complitex.common.service.executor.ExecutorService;
import org.complitex.common.util.ExceptionUtil;
import org.complitex.common.util.StringUtil;
import org.complitex.common.web.component.BookmarkablePageLinkPanel;
import org.complitex.common.web.component.DatePicker;
import org.complitex.common.web.component.YearDropDownChoice;
import org.complitex.common.web.component.ajax.AjaxFeedbackPanel;
import org.complitex.common.web.component.datatable.ArrowOrderByBorder;
import org.complitex.common.web.component.organization.OrganizationIdPicker;
import org.complitex.common.wicket.BroadcastBehavior;
import org.complitex.organization_type.strategy.OrganizationTypeStrategy;
import org.complitex.osznconnection.file.entity.RequestFile;
import org.complitex.osznconnection.file.entity.RequestFileFilter;
import org.complitex.osznconnection.file.service.file_description.RequestFileDescriptionBean;
import org.complitex.osznconnection.file.service.process.LoadRequestFileBean;
import org.complitex.osznconnection.file.service.process.Process;
import org.complitex.osznconnection.file.service.process.ProcessManagerService;
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
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.complitex.common.util.DateUtil.getYear;
import static org.complitex.common.util.StringUtil.currentTime;
import static org.complitex.osznconnection.file.entity.RequestFileType.SUBSIDY_TARIF;
import static org.complitex.osznconnection.file.service.process.ProcessType.LOAD_SUBSIDY_TARIF;

/**
 */
@AuthorizeInstantiation(SecurityRole.AUTHORIZED)
public class SubsidyTarifFileList extends TemplatePage {
    @EJB
    private ProcessManagerService processManagerService;

    @EJB
    private RequestFileDescriptionBean requestFileDescriptionBean;

    private RequestFileLoadPanel requestFileLoadPanel;

    public SubsidyTarifFileList() {
        init();
    }

    private boolean hasFieldDescription() {
        return requestFileDescriptionBean.getFileDescription(SUBSIDY_TARIF) != null;
    }

    private RequestFileFilter newFilter() {
        final RequestFileFilter filter = new RequestFileFilter();
        filter.setType(SUBSIDY_TARIF);
        return filter;
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        AbstractProcessableListPanel.renderResources(response);
    }

    private void init() {
        final ProcessingManager processingManager = new ProcessingManager(LOAD_SUBSIDY_TARIF);

        add(new Label("title", getString("title")));

        final AjaxFeedbackPanel messages = new AjaxFeedbackPanel("messages");
        add(messages);

        //Фильтр модель
        final RequestFileFilter filter = getFilterObject(newFilter());
        final IModel<RequestFileFilter> model = new CompoundPropertyModel<RequestFileFilter>(filter);

        //Фильтр форма
        final Form<RequestFileFilter> form = new Form<RequestFileFilter>("form", model);
        form.setOutputMarkupId(true);
        add(form);

        AjaxLink<Void> filter_reset = new AjaxLink<Void>("filter_reset") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                RequestFileFilter filterObject = newFilter();
                model.setObject(filterObject);
                target.add(form);
            }
        };
        form.add(filter_reset);

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
                OsznOrganizationTypeStrategy.SUBSIDY_DEPARTMENT_TYPE));

        // Организация пользователя
        form.add(new OrganizationIdPicker("userOrganization",
                new PropertyModel<>(model, "userOrganizationId"),
                OrganizationTypeStrategy.USER_ORGANIZATION_TYPE));

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
        final DataView<RequestFile> dataView = new DataView<RequestFile>("request_files", dataProvider) {
            @Override
            protected Item<RequestFile> newItem(String id, int index, IModel<RequestFile> model) {
                Item<RequestFile> item =  super.newItem("item" + model.getObject().getId(), index, model);
                item.setOutputMarkupId(true);

                return item;
            }

            @Override
            protected void populateItem(final Item<RequestFile> item) {
                //Выбор файлов
                item.add(new ItemCheckBoxPanel<RequestFile>("itemCheckBoxPanel", selectManager, item.getModel()));

                //Идентификатор файла
                item.add(new Label("id", new PropertyModel<>(item.getModel(), "id")));

                //Дата загрузки
                item.add(new DateLabel("loaded", new PropertyModel<>(item.getModel(), "loaded"),
                        new PatternDateConverter("dd.MM.yy HH:mm:ss", true)));

                item.add(new BookmarkablePageLinkPanel("name", new PropertyModel<String>(item.getModel(), "fullName"),
                        SubsidyTarifList.class, new PageParameters().add("request_file_id", item.getModelObject().getId()))); //todo page parameter model

                //ОСЗН
                item.add(new ItemOrganizationLabel("organization", item.getModelObject().getOrganizationId()));

                //Организация пользователя
                item.add(new ItemOrganizationLabel("userOrganization", item.getModelObject().getUserOrganizationId()));

                item.add(new Label("year", getYear(item.getModelObject().getBeginDate()) + ""));

                item.add(new Label("dbf_record_count", StringUtil.valueOf(item.getModelObject().getDbfRecordCount())));

                //Количество загруженных записей
                item.add(new Label("loaded_record_count", new LoadableDetachableModel<String>() {

                    @Override
                    protected String load() {
                        return String.valueOf(item.getModelObject().getLoadedRecordCount());
                    }
                }));

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
        form.add(new ArrowOrderByBorder("header.year", "year", dataProvider, dataView, form));

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
                return SubsidyTarifFileList.class;
            }

            @Override
            protected void logSuccess(RequestFile requestFile) {
                log().info("Request file (ID : {}, full name: '{}') has been deleted.", requestFile.getId(),
                        requestFile.getFullName());
            }

            @Override
            protected void logError(RequestFile requestFile, Exception e) {
                log().error("Cannot delete request file (ID : " + requestFile.getId() + ", full name: '"
                        + requestFile.getFullName() + "').", e);
            }
        });

        //Диалог загрузки
        requestFileLoadPanel = new RequestFileLoadPanel("load_panel", new ResourceModel("load_panel_title"),
                MonthParameterViewMode.HIDDEN, new Long[]{OsznOrganizationTypeStrategy.SUBSIDY_DEPARTMENT_TYPE}) {
            @Override
            protected void load(Long serviceProviderId, Long userOrganizationId, Long organizationId, int year, int monthFrom, int monthTo, AjaxRequestTarget target) {
                processManagerService.loadSubsidyTarif(userOrganizationId, organizationId, year, monthFrom);

                selectManager.clearSelection();
                target.add(form);
            }
        };

        add(requestFileLoadPanel);

        //Messages
        //noinspection Duplicates
        add(new BroadcastBehavior<Process>(ExecutorService.class, Process.class) {
            @Override
            protected void onBroadcast(WebSocketRequestHandler handler, String key, Process process) {
                String prefix = process.getProcessType().name().split("_")[0].toLowerCase();

                //noinspection Duplicates
                switch (key){
                    case "onBegin":
                        info(currentTime() + getString(prefix + "_process.begin"));

                        break;
                    case "onComplete":
                        info(currentTime() + getStringFormat(prefix + "_process.completed", process.getSuccessCount(),
                                process.getSkippedCount(), process.getErrorCount()));

                        break;
                    case "onCancel":
                        info(currentTime() + getStringFormat(prefix + "_process.canceled", process.getSuccessCount(),
                                process.getSkippedCount(), process.getErrorCount()));

                        break;
                    case "onCriticalError":
                        error(process.getErrorMessage());
                        info(currentTime() + getStringFormat(prefix + "_process.critical_error", process.getSuccessCount(),
                                process.getSkippedCount(), process.getErrorCount()));

                        break;
                }

                handler.add(messages, dataViewContainer, buttons);
            }

            @Override
            protected boolean filter(Process payload) {
                return LOAD_SUBSIDY_TARIF.equals(payload.getProcessType());
            }
        });

        //noinspection Duplicates
        add(new BroadcastBehavior<RequestFile>(ExecutorService.class, RequestFile.class) {
            @Override
            protected void onBroadcast(WebSocketRequestHandler handler, String key, RequestFile requestFile) {
                if ("onError".equals(key)){
                    error(currentTime() + requestFile.getErrorMessage());
                }

                handler.add(messages, dataViewContainer, buttons);
            }

            @Override
            protected boolean filter(RequestFile requestFile) {
                return dataView.get("item" + requestFile.getId()) != null;
            }
        });

        //noinspection Duplicates
        add(new BroadcastBehavior<RequestFile>(LoadRequestFileBean.class, RequestFile.class) {
            @Override
            protected void onBroadcast(WebSocketRequestHandler handler, String key, RequestFile requestFile) {
                if ("onUpdate".equals(key)){
                    handler.add(messages, dataViewContainer);
                }
            }
            @Override
            protected boolean filter(RequestFile requestFile) {
                return SUBSIDY_TARIF.equals(requestFile.getType());
            }
        });

        //noinspection Duplicates
        add(new BroadcastBehavior<Exception>(ProcessManagerService.class, Exception.class) {
            @Override
            protected void onBroadcast(WebSocketRequestHandler handler, String key, Exception e) {
                error(ExceptionUtil.getCauseMessage(e));

                handler.add(messages);
            }
        });
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
