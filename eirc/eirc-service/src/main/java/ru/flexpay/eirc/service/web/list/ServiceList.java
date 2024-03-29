package ru.complitex.eirc.service.web.list;

import com.google.common.collect.ImmutableList;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.*;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.common.entity.StringLocale;
import ru.complitex.common.strategy.StringLocaleBean;
import ru.complitex.common.web.component.datatable.DataProvider;
import ru.complitex.common.web.component.paging.PagingNavigator;
import ru.complitex.common.web.component.scroll.ScrollBookmarkablePageLink;
import ru.complitex.template.web.component.toolbar.AddItemButton;
import ru.complitex.template.web.component.toolbar.ToolbarButton;
import ru.complitex.template.web.security.SecurityRole;
import ru.complitex.template.web.template.TemplatePage;
import ru.complitex.eirc.service.entity.Service;
import ru.complitex.eirc.service.service.ServiceBean;
import ru.complitex.eirc.service.web.edit.ServiceEdit;

import javax.ejb.EJB;
import java.util.List;

import static ru.complitex.common.util.PageUtil.newSorting;

/**
 * @author Pavel Sknar
 */
@AuthorizeInstantiation(SecurityRole.AUTHORIZED)
public class ServiceList extends TemplatePage {

    @EJB
    private ServiceBean serviceBean;

    @EJB
    private StringLocaleBean stringLocaleBean;

    private WebMarkupContainer container;
    private DataView<Service> dataView;

    private Service filterObject = new Service();

    public ServiceList() {
        init();
    }

    public void refreshContent(AjaxRequestTarget target) {
        container.setVisible(true);
        if (target != null) {
            dataView.setCurrentPage(0);
            target.add(container);
        }
    }

    private void init() {

        IModel<String> labelModel = new ResourceModel("label");

        add(new Label("title", labelModel));
        add(new Label("label", labelModel));

        final FeedbackPanel messages = new FeedbackPanel("messages");
        messages.setOutputMarkupId(true);
        add(messages);

        container = new WebMarkupContainer("container");
        container.setOutputMarkupPlaceholderTag(true);
        container.setVisible(true);
        add(container);

        final StringLocale stringLocale = stringLocaleBean.convert(getLocale());

        //Form
        final Form filterForm = new Form("filterForm");
        container.add(filterForm);

        //Data Provider
        final DataProvider<Service> dataProvider = new DataProvider<Service>() {

            @Override
            protected Iterable<? extends Service> getData(long first, long count) {
                FilterWrapper<Service> filterWrapper = FilterWrapper.of(filterObject, first, count);
                filterWrapper.setAscending(getSort().isAscending());
                filterWrapper.setSortProperty(getSort().getProperty());
                filterWrapper.setStringLocale(stringLocaleBean.convert(getLocale()));
                filterWrapper.setLike(true);

                return serviceBean.getServices(filterWrapper);
            }

            @Override
            protected Long getSize() {
                FilterWrapper<Service> filterWrapper = FilterWrapper.of(new Service());
                return serviceBean.getCount(filterWrapper);
            }
        };
        dataProvider.setSort("service_name", SortOrder.ASCENDING);

        //Data View
        dataView = new DataView<Service>("data", dataProvider, 1) {

            @Override
            protected void populateItem(Item<Service> item) {
                final Service service = item.getModelObject();

                item.add(new Label("code", service.getCode()));
                item.add(new Label("name", service.getName(stringLocale)));

                ScrollBookmarkablePageLink<WebPage> detailsLink = new ScrollBookmarkablePageLink<WebPage>("detailsLink",
                        getEditPage(), getEditPageParams(service.getId()),
                        String.valueOf(service.getId()));
                detailsLink.add(new Label("editMessage", new AbstractReadOnlyModel<String>() {

                    @Override
                    public String getObject() {
                        return getString("edit");
                    }
                }));
                item.add(detailsLink);
            }
        };
        filterForm.add(dataView);

        //Sorting
        filterForm.add(newSorting("header.", dataProvider, dataView, filterForm, true, "serviceCode", "serviceName"));

        //Filters
        filterForm.add(new TextField<>("codeFilter", new PropertyModel<String>(filterObject, "code")));

        filterForm.add(new TextField<>("nameFilter", new Model<String>() {

            @Override
            public String getObject() {
                return filterObject.getName(stringLocale);
            }

            @Override
            public void setObject(String name) {
                filterObject.addName(stringLocale, name);
            }
        }));

        //Reset Action
        AjaxLink reset = new AjaxLink("reset") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                filterForm.clearInput();
                filterObject.setCode(null);
                filterObject.setParentId(null);
                filterObject.getNames().clear();

                target.add(container);
            }
        };
        filterForm.add(reset);

        //Submit Action
        AjaxButton submit = new AjaxButton("submit", filterForm) {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {

                target.add(container);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
            }
        };
        filterForm.add(submit);

        //Navigator
        container.add(new PagingNavigator("navigator", dataView, getPreferencesPage(), container));
    }

    @Override
    protected List<ToolbarButton> getToolbarButtons(String id) {
        return ImmutableList.of(new AddItemButton(id) {

            @Override
            protected void onClick() {
                this.getPage().setResponsePage(getEditPage(), getEditPageParams(null));
            }

        });
    }

    private Class<? extends Page> getEditPage() {
        return ServiceEdit.class;
    }

    private PageParameters getEditPageParams(Long id) {
        PageParameters parameters = new PageParameters();
        if (id != null) {
            parameters.add("serviceId", id);
        }
        return parameters;
    }
}

