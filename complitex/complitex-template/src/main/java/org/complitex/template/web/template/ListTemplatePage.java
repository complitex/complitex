package org.complitex.template.web.template;

import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.complitex.common.entity.FilterWrapper;
import org.complitex.common.entity.ILongId;
import org.complitex.common.entity.PreferenceKey;
import org.complitex.common.web.component.TextLabel;
import org.complitex.common.web.component.ajax.AjaxFeedbackPanel;
import org.complitex.common.web.component.datatable.ArrowOrderByBorder;
import org.complitex.common.web.component.datatable.DataProvider;
import org.complitex.common.web.component.paging.PagingNavigator;
import org.complitex.template.web.component.InputPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static org.complitex.common.util.StringUtil.lowerCamelToUnderscore;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 17.10.12 15:39
 */
public abstract class ListTemplatePage<T extends ILongId> extends TemplatePage{
    protected transient Logger log;

    public static final String FILTER_FIELD = "filter_field";
    public static final String DATA_FIELD = "data_field";
    public static final String STATUS_DETAIL_PANEL = "status_detail_panel";

    private String prefix = "";

    private boolean camelToUnderscore = false;

    private Class<? extends Page> backPage;

    private Form filterForm;

    private FilterWrapper<T> filterWrapper;

    public ListTemplatePage(final PageParameters pageParameters) {
        //Authorize
        authorize(pageParameters);

        //Title
        add(new Label("title", new ResourceModel("title"))); //todo add file name

        //Feedback Panel
        AjaxFeedbackPanel messages = new AjaxFeedbackPanel("messages");
        add(messages);

        //StatusDetail Panel
        add(newStatusDetailPanel());

        //Filter Model
        String pageKey = pageParameters.toString();

        filterWrapper = getTemplateSession().getPreferenceFilter(getClass().getName() + pageKey,
                FilterWrapper.of(newFilterObject(pageParameters)));

        //Filter Form
        filterForm = new Form<>("filter_form");
        filterForm.setOutputMarkupId(true);
        add(filterForm);

        //Filter Reset Button
        AjaxLink filterReset = new AjaxLink("filter_reset") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                filterWrapper.setObject(newFilterObject(pageParameters));

                target.add(filterForm);
            }
        };
        filterForm.add(filterReset);

        //Filter Find
        AjaxButton filterFind = new AjaxButton("filter_find") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                target.add(filterForm);
                target.add(messages);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                target.add(messages);
            }
        };
        filterForm.add(filterFind);

        //Filter Fields
        filterForm.add(new ListView<String>("filter_list", getProperties()){
            @Override
            protected void populateItem(ListItem<String> item) {
                onPopulateFilter(item);
            }
        });

        //Data Provider
        DataProvider<T> dataProvider = new DataProvider<T>() {
            @Override
            protected Iterable<T> getData(long first, long count) {
                ((TemplateSession)getSession()).putPreferenceObject(getPreferencesPage() + pageKey,
                        PreferenceKey.FILTER_OBJECT, filterWrapper);

                filterWrapper.setFirst(first);
                filterWrapper.setCount(count);
                filterWrapper.setSortProperty(getSort().getProperty());
                filterWrapper.setAscending(getSort().isAscending());

                return getList(filterWrapper);
            }

            @Override
            protected Long getSize() {
                return getCount(filterWrapper);
            }

            @Override
            public IModel<T> model(T object) {
                return Model.of(object);
            }
        };
        dataProvider.setSort("id", SortOrder.DESCENDING);

        //Data Container
        WebMarkupContainer dataContainer = new WebMarkupContainer("data_container");
        dataContainer.setOutputMarkupId(true);
        filterForm.add(dataContainer);

        //Data View
        DataView dataView = new DataView<T>("data_view", dataProvider) {
            @Override
            protected void populateItem(Item<T> row) {
                row.add(new ListView<String>("data_list", getProperties()) {

                    @Override
                    protected void populateItem(ListItem<String> column) {
                        onPopulateData(row, column);
                    }
                });

                row.add(new ListView<Component>("data_action_list", getActionComponents("data_action",
                        row.getModelObject())) {
                    @Override
                    protected void populateItem(ListItem<Component> item) {
                        item.add(item.getModelObject());
                    }
                });
            }
        };
        dataContainer.add(dataView);

        //Paging Navigator
        PagingNavigator paging = new PagingNavigator("paging", dataView, getClass().getName(), filterForm);
        filterForm.add(paging);

        //Headers
        filterForm.add(new ListView<String>("header_list", getProperties()) {
            @Override
            protected void populateItem(ListItem<String> item) {
                String property = item.getModelObject();

                ArrowOrderByBorder border = new ArrowOrderByBorder("header_border",
                        camelToUnderscore ? lowerCamelToUnderscore(property) : property,
                        dataProvider, dataView, filterForm);
                border.add(new Label("header_label", onPopulateHeader(property)));

                item.add(border);
            }
        });

        //Назад
        filterForm.add(new Link("back") {
            @Override
            public void onClick() {
                setResponsePage(backPage);
            }

            @Override
            public boolean isVisible() {
                return backPage != null;
            }
        });
    }

    public ListTemplatePage(final PageParameters pageParameters, String prefix, Class<? extends Page> backPage) {
        this(pageParameters);

        setPrefix(prefix);
        setBackPage(backPage);
    }

    protected abstract T newFilterObject(PageParameters pageParameters);

    protected abstract List<T> getList(FilterWrapper<T> filterWrapper);

    protected abstract Long getCount(FilterWrapper<T> filterWrapper);

    protected abstract List<String> getProperties();

    protected String onPopulateHeader(String property) {
        return getStringOrKey(property);
    }

    protected void onPopulateFilter(ListItem<String> item){
        String p = "object." + getPrefix() + item.getModelObject();

        item.add(new InputPanel<>(FILTER_FIELD, new PropertyModel<>(filterWrapper, p)));
    }

    protected void onPopulateData(ListItem<T> row, ListItem<String> column){
        column.add(new TextLabel(DATA_FIELD, new PropertyModel<>(row.getModel(), getPrefix() + column.getModelObject())));
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public boolean isCamelToUnderscore() {
        return camelToUnderscore;
    }

    public void setCamelToUnderscore(boolean camelToUnderscore) {
        this.camelToUnderscore = camelToUnderscore;
    }

    public Logger getLog(){
        if (log == null){
            log = LoggerFactory.getLogger(getClass());
        }

        return log;
    }

    public Class<? extends Page> getBackPage() {
        return backPage;
    }

    public void setBackPage(Class<? extends Page> backPage) {
        this.backPage = backPage;
    }

    protected List<Component> getActionComponents(String id, T object){
        return new ArrayList<>();
    }

    protected void authorize(PageParameters pageParameters){

    }

    protected WebMarkupContainer newStatusDetailPanel(){
        return new EmptyPanel(STATUS_DETAIL_PANEL);
    }

    public Form getFilterForm() {
        return filterForm;
    }

    public FilterWrapper<T> getFilterWrapper() {
        return filterWrapper;
    }
}
