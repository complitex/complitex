/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.complitex.pspoffice.person.strategy.web.list.person;

import com.google.common.collect.ImmutableList;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.authorization.UnauthorizedInstantiationException;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.Strings;
import ru.complitex.common.entity.DomainObjectFilter;
import ru.complitex.common.strategy.StringLocaleBean;
import ru.complitex.common.strategy.StringValueBean;
import ru.complitex.common.util.StringUtil;
import ru.complitex.common.web.component.ShowMode;
import ru.complitex.common.web.component.datatable.ArrowOrderByBorder;
import ru.complitex.common.web.component.datatable.DataProvider;
import ru.complitex.common.web.component.domain.DomainObjectAccessUtil;
import ru.complitex.common.web.component.paging.PagingNavigator;
import ru.complitex.common.web.component.scroll.ScrollBookmarkablePageLink;
import ru.complitex.common.web.component.search.CollapsibleSearchPanel;
import ru.complitex.pspoffice.person.strategy.PersonStrategy;
import ru.complitex.pspoffice.person.strategy.entity.Person;
import ru.complitex.template.web.component.toolbar.AddItemButton;
import ru.complitex.template.web.component.toolbar.ToolbarButton;
import ru.complitex.template.web.component.toolbar.search.CollapsibleSearchToolbarButton;
import ru.complitex.template.web.pages.ScrollListPage;
import ru.complitex.template.web.security.SecurityRole;

import javax.ejb.EJB;
import java.util.List;
import java.util.Locale;

/**
 *
 * @author Artem
 */
@AuthorizeInstantiation(SecurityRole.AUTHORIZED)
public final class PersonList extends ScrollListPage {
    @EJB
    private PersonStrategy personStrategy;

    @EJB
    private StringLocaleBean stringLocaleBean;

    @EJB
    private StringValueBean stringBean;

    private CollapsibleSearchPanel searchPanel;

    private class ColumnLabelModel extends AbstractReadOnlyModel<String> {

        private long entityAttributeId;

        private ColumnLabelModel(long entityAttributeId) {
            this.entityAttributeId = entityAttributeId;
        }

        @Override
        public String getObject() {
            return personStrategy.getEntity().getName(entityAttributeId, getLocale()).toLowerCase();
        }
    }

    public PersonList() {
        init();
    }

    public PersonList(PageParameters params) {
        super(params);
        init();
    }

    private void init() {
        if (!hasAnyRole(personStrategy.getListRoles())) {
            throw new UnauthorizedInstantiationException(getClass());
        }

        IModel<String> labelModel = new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                return personStrategy.getPluralEntityLabel(getLocale());
            }
        };

        add(new Label("title", labelModel));
        add(new Label("label", labelModel));

        final IModel<ShowMode> showModeModel = new Model<ShowMode>(ShowMode.ACTIVE);
        searchPanel = new CollapsibleSearchPanel("searchPanel", showModeModel);
        add(searchPanel);
        searchPanel.initialize();

        final WebMarkupContainer content = new WebMarkupContainer("content");
        content.setOutputMarkupPlaceholderTag(true);
        add(content);

        //Example
        final DomainObjectFilter example = (DomainObjectFilter) getFilterObject(new DomainObjectFilter());

        //Form
        final Form<Void> filterForm = new Form<Void>("filterForm");
        content.add(filterForm);

        //Data Provider
        final DataProvider<Person> dataProvider = new DataProvider<Person>() {

            @Override
            protected Iterable<Person> getData(long first, long count) {
                //store preference, but before clear data order related properties.
                {
                    example.setAsc(false);
                    example.setOrderByAttributeTypeId(null);
                    setFilterObject(example);
                }

                final boolean asc = getSort().isAscending();
                final String sortProperty = getSort().getProperty();

                if (!Strings.isEmpty(sortProperty)) {
                    example.setOrderByAttributeTypeId(Long.valueOf(sortProperty));
                }
                example.setStatus(showModeModel.getObject().name());
                example.setLocaleId(stringLocaleBean.convert(getLocale()).getId());
                example.setAsc(asc);
                example.setFirst(first);
                example.setCount(count);
                return personStrategy.getList(example);
            }

            @Override
            protected Long getSize() {
                example.setStatus(showModeModel.getObject().name());
                example.setLocaleId(stringLocaleBean.convert(getLocale()).getId());
                return personStrategy.getCount(example);
            }
        };
        dataProvider.setSort(String.valueOf(personStrategy.getDefaultSortAttributeTypeId()), SortOrder.ASCENDING);

        //Filters
        filterForm.add(new TextField<String>("lastNameFilter", new Model<String>() {

            @Override
            public String getObject() {
                return example.getAdditionalParam(PersonStrategy.LAST_NAME_FILTER);
            }

            @Override
            public void setObject(String lastName) {
                example.addAdditionalParam(PersonStrategy.LAST_NAME_FILTER, lastName);
            }
        }));
        filterForm.add(new TextField<String>("firstNameFilter", new Model<String>() {

            @Override
            public String getObject() {
                return example.getAdditionalParam(PersonStrategy.FIRST_NAME_FILTER);
            }

            @Override
            public void setObject(String firstName) {
                example.addAdditionalParam(PersonStrategy.FIRST_NAME_FILTER, firstName);
            }
        }));
        filterForm.add(new TextField<String>("middleNameFilter", new Model<String>() {

            @Override
            public String getObject() {
                return example.getAdditionalParam(PersonStrategy.MIDDLE_NAME_FILTER);
            }

            @Override
            public void setObject(String middleName) {
                example.addAdditionalParam(PersonStrategy.MIDDLE_NAME_FILTER, middleName);
            }
        }));

        final Locale systemLocale = stringLocaleBean.getSystemLocale();
        //Data View
        DataView<Person> dataView = new DataView<Person>("data", dataProvider, 1) {

            @Override
            protected void populateItem(Item<Person> item) {
                Person person = item.getModelObject();

                item.add(new Label("order", StringUtil.valueOf(getFirstItemOffset() + item.getIndex() + 1)));
                item.add(new Label("lastName", person.getLastName(getLocale(), systemLocale)));
                item.add(new Label("firstName", person.getFirstName(getLocale(), systemLocale)));
                item.add(new Label("middleName", person.getMiddleName(getLocale(), systemLocale)));

                ScrollBookmarkablePageLink<WebPage> detailsLink = new ScrollBookmarkablePageLink<WebPage>("detailsLink",
                        personStrategy.getEditPage(), personStrategy.getEditPageParams(person.getObjectId(), null, null),
                        String.valueOf(person.getObjectId()));
                detailsLink.add(new Label("editMessage", new AbstractReadOnlyModel<String>() {

                    @Override
                    public String getObject() {
                        if (DomainObjectAccessUtil.canAddNew(personStrategy, "person")) {
                            return getString("edit");
                        } else {
                            return getString("view");
                        }
                    }
                }));
                item.add(detailsLink);
            }
        };
        filterForm.add(dataView);

        filterForm.add(new ArrowOrderByBorder("lastNameHeader", String.valueOf(PersonStrategy.OrderBy.LAST_NAME.getOrderByAttributeId()), dataProvider,
                dataView, content).add(new Label("last_name", new ColumnLabelModel(PersonStrategy.LAST_NAME))));
        filterForm.add(new ArrowOrderByBorder("firstNameHeader", String.valueOf(PersonStrategy.OrderBy.FIRST_NAME.getOrderByAttributeId()), dataProvider,
                dataView, content).add(new Label("first_name", new ColumnLabelModel(PersonStrategy.FIRST_NAME))));
        filterForm.add(new ArrowOrderByBorder("middleNameHeader", String.valueOf(PersonStrategy.OrderBy.MIDDLE_NAME.getOrderByAttributeId()),
                dataProvider, dataView, content).add(new Label("middle_name", new ColumnLabelModel(PersonStrategy.MIDDLE_NAME))));

        //Reset Action
        AjaxLink<Void> reset = new AjaxLink<Void>("reset") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                filterForm.clearInput();
                example.setObjectId(null);
                example.addAdditionalParam(PersonStrategy.LAST_NAME_FILTER, null);
                example.addAdditionalParam(PersonStrategy.FIRST_NAME_FILTER, null);
                example.addAdditionalParam(PersonStrategy.MIDDLE_NAME_FILTER, null);
                target.add(content);
            }
        };
        filterForm.add(reset);

        //Submit Action
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

        content.add(new PagingNavigator("navigator", dataView, getPreferencesPage(), content));
    }

    @Override
    protected List<ToolbarButton> getToolbarButtons(String id) {
        return ImmutableList.of(new AddItemButton(id) {

            @Override
            protected void onClick() {
                setResponsePage(personStrategy.getEditPage(), personStrategy.getEditPageParams(null, null, null));
            }

            @Override
            protected void onBeforeRender() {
                if (!DomainObjectAccessUtil.canAddNew(personStrategy, "person")) {
                    setVisible(false);
                }
                super.onBeforeRender();
            }
        }, new CollapsibleSearchToolbarButton(id, searchPanel));
    }
}
