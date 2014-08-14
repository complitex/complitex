/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.strategy.web.list.person;

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
import org.complitex.dictionary.entity.description.EntityAttributeType;
import org.complitex.dictionary.entity.example.DomainObjectExample;
import org.complitex.dictionary.service.LocaleBean;
import org.complitex.dictionary.service.StringCultureBean;
import org.complitex.dictionary.strategy.web.DomainObjectAccessUtil;
import org.complitex.dictionary.util.StringUtil;
import org.complitex.dictionary.web.component.ShowMode;
import org.complitex.dictionary.web.component.datatable.ArrowOrderByBorder;
import org.complitex.dictionary.web.component.datatable.DataProvider;
import org.complitex.dictionary.web.component.paging.PagingNavigator;
import org.complitex.dictionary.web.component.scroll.ScrollBookmarkablePageLink;
import org.complitex.dictionary.web.component.search.CollapsibleSearchPanel;
import org.complitex.pspoffice.person.strategy.PersonStrategy;
import org.complitex.pspoffice.person.strategy.entity.Person;
import org.complitex.template.web.component.toolbar.AddItemButton;
import org.complitex.template.web.component.toolbar.ToolbarButton;
import org.complitex.template.web.component.toolbar.search.CollapsibleSearchToolbarButton;
import org.complitex.template.web.pages.ScrollListPage;
import org.complitex.template.web.security.SecurityRole;

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
    private LocaleBean localeBean;
    @EJB
    private StringCultureBean stringBean;
    private CollapsibleSearchPanel searchPanel;

    private class ColumnLabelModel extends AbstractReadOnlyModel<String> {

        private long attributeTypeId;

        private ColumnLabelModel(long attributeTypeId) {
            this.attributeTypeId = attributeTypeId;
        }

        @Override
        public String getObject() {
            EntityAttributeType attributeType = personStrategy.getEntity().getAttributeType(attributeTypeId);
            return Strings.capitalize(stringBean.displayValue(attributeType.getAttributeNames(), getLocale()).toLowerCase(getLocale()));
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
        final DomainObjectExample example = (DomainObjectExample) getFilterObject(new DomainObjectExample());

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
                example.setLocaleId(localeBean.convert(getLocale()).getId());
                example.setAsc(asc);
                example.setStart(first);
                example.setSize(count);
                return personStrategy.find(example);
            }

            @Override
            protected int getSize() {
                example.setStatus(showModeModel.getObject().name());
                example.setLocaleId(localeBean.convert(getLocale()).getId());
                return personStrategy.count(example);
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

        final Locale systemLocale = localeBean.getSystemLocale();
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
                        personStrategy.getEditPage(), personStrategy.getEditPageParams(person.getId(), null, null),
                        String.valueOf(person.getId()));
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
                example.setId(null);
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
    protected List<? extends ToolbarButton> getToolbarButtons(String id) {
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
