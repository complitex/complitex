package ru.complitex.keconnection.organization.strategy.web.list;

import com.google.common.collect.ImmutableList;
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
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.Strings;
import ru.complitex.common.entity.AttributeFilter;
import ru.complitex.common.entity.DomainObjectFilter;
import ru.complitex.common.strategy.StringLocaleBean;
import ru.complitex.common.strategy.organization.IOrganizationStrategy;
import ru.complitex.common.util.StringUtil;
import ru.complitex.common.web.component.ShowMode;
import ru.complitex.common.web.component.datatable.ArrowOrderByBorder;
import ru.complitex.common.web.component.datatable.DataProvider;
import ru.complitex.common.web.component.domain.DomainObjectAccessUtil;
import ru.complitex.common.web.component.paging.PagingNavigator;
import ru.complitex.common.web.component.scroll.ScrollBookmarkablePageLink;
import ru.complitex.common.web.component.search.CollapsibleSearchPanel;
import ru.complitex.keconnection.organization.strategy.KeOrganizationStrategy;
import ru.complitex.keconnection.organization.strategy.entity.KeOrganization;
import ru.complitex.template.web.component.toolbar.AddItemButton;
import ru.complitex.template.web.component.toolbar.ToolbarButton;
import ru.complitex.template.web.component.toolbar.search.CollapsibleSearchToolbarButton;
import ru.complitex.template.web.pages.ScrollListPage;
import ru.complitex.template.web.security.SecurityRole;

import javax.ejb.EJB;
import java.util.List;

import static ru.complitex.common.strategy.organization.IOrganizationStrategy.NAME;
import static ru.complitex.common.strategy.organization.IOrganizationStrategy.SHORT_NAME;

/**
 *
 * @author Artem
 */
@AuthorizeInstantiation(SecurityRole.ADDRESS_MODULE_VIEW)
public class OrganizationList extends ScrollListPage {
    @EJB
    private StringLocaleBean stringLocaleBean;

    private DomainObjectFilter example;
    private WebMarkupContainer content;
    private DataView<KeOrganization> dataView;
    private CollapsibleSearchPanel searchPanel;

    @EJB(name = IOrganizationStrategy.BEAN_NAME, beanInterface = IOrganizationStrategy.class)
    private KeOrganizationStrategy organizationStrategy;

    public OrganizationList() {
        init();
    }

    public OrganizationList(PageParameters params) {
        super(params);
        init();
    }

    public DomainObjectFilter getExample() {
        return example;
    }

    public void refreshContent(AjaxRequestTarget target) {
        content.setVisible(true);
        if (target != null) {
            dataView.setCurrentPage(0);
            target.add(content);
        }
    }

    private DomainObjectFilter newExample() {
        DomainObjectFilter e = new DomainObjectFilter();
        e.addAttributeFilter(new AttributeFilter(NAME));
        e.addAttributeFilter(new AttributeFilter(KeOrganizationStrategy.CODE));
        e.addAttributeFilter(new AttributeFilter(SHORT_NAME));
        return e;
    }

    private void init() {
        IModel<String> labelModel = new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                return organizationStrategy.getPluralEntityLabel(getLocale());
            }
        };

        add(new Label("title", labelModel));
        add(new Label("label", labelModel));

        content = new WebMarkupContainer("content");
        content.setOutputMarkupPlaceholderTag(true);

        //Example
        example = getFilterObject(newExample());

        //Search
        final List<String> searchFilters = organizationStrategy.getSearchFilters();
        content.setVisible(searchFilters == null || searchFilters.isEmpty());
        add(content);

        final IModel<ShowMode> showModeModel = new Model<ShowMode>(ShowMode.ACTIVE);
        searchPanel = new CollapsibleSearchPanel("searchPanel", getTemplateSession().getGlobalSearchComponentState(),
                searchFilters, organizationStrategy.getSearchCallback(), ShowMode.ALL, true, showModeModel);
        add(searchPanel);
        searchPanel.initialize();

        //Form
        final Form<Void> filterForm = new Form<Void>("filterForm");
        content.add(filterForm);

        //Data Provider
        final DataProvider<KeOrganization> dataProvider = new DataProvider<KeOrganization>() {

            @Override
            protected Iterable<? extends KeOrganization> getData(long first, long count) {
                //store preference, but before clear data order related properties.
                {
                    example.setAsc(false);
                    example.setOrderByAttributeTypeId(null);
                    setFilterObject(example);
                }

                //store state
                getTemplateSession().storeGlobalSearchComponentState();

                boolean asc = getSort().isAscending();
                String sortProperty = getSort().getProperty();

                if (!Strings.isEmpty(sortProperty)) {
                    example.setOrderByAttributeTypeId(Long.valueOf(sortProperty));
                }
                example.setStatus(showModeModel.getObject().name());
                example.setLocaleId(stringLocaleBean.convert(getLocale()).getId());
                example.setAsc(asc);
                example.setFirst(first);
                example.setCount(count);

                return organizationStrategy.getList(example);
            }

            @Override
            protected Long getSize() {
                example.setStatus(showModeModel.getObject().name());
                example.setLocaleId(stringLocaleBean.convert(getLocale()).getId());
                return organizationStrategy.getCount(example);
            }
        };
        dataProvider.setSort(String.valueOf(organizationStrategy.getDefaultSortAttributeTypeId()), SortOrder.ASCENDING);

        //Filters
        filterForm.add(new TextField<>("nameFilter", new Model<String>() {

            @Override
            public String getObject() {
                return example.getAttributeExample(NAME).getValue();
            }

            @Override
            public void setObject(String name) {
                example.getAttributeExample(NAME).setValue(name);
            }
        }));
        filterForm.add(new TextField<>("codeFilter", new Model<String>() {

            @Override
            public String getObject() {
                return example.getAttributeExample(KeOrganizationStrategy.CODE).getValue();
            }

            @Override
            public void setObject(String code) {
                example.getAttributeExample(KeOrganizationStrategy.CODE).setValue(code);
            }
        }));
        filterForm.add(new TextField<>("shortNameFilter", new Model<String>() {

            @Override
            public String getObject() {
                return example.getAttributeExample(SHORT_NAME).getValue();
            }

            @Override
            public void setObject(String shortName) {
                example.getAttributeExample(SHORT_NAME).setValue(shortName);
            }
        }));
        filterForm.add(new TextField<>("parentShortNameFilter", new Model<String>() {

            @Override
            public String getObject() {
                return example.getAdditionalParam(KeOrganizationStrategy.PARENT_SHORT_NAME_FILTER);
            }

            @Override
            public void setObject(String parentShortName) {
                example.addAdditionalParam(KeOrganizationStrategy.PARENT_SHORT_NAME_FILTER, parentShortName);
            }
        }));

        final SetReadyCloseOperatingMonthDialog setReadyCloseOperatingMonthDialog =
                new SetReadyCloseOperatingMonthDialog("setReadyCloseOperatingMonthDialog") {

                    @Override
                    protected void onSet(KeOrganization organization, AjaxRequestTarget target) {
                        target.add(content);
                    }
                };
        add(setReadyCloseOperatingMonthDialog);

        //Data View
        dataView = new DataView<KeOrganization>("data", dataProvider) {

            @Override
            protected void populateItem(Item<KeOrganization> item) {
                final KeOrganization organization = item.getModelObject();

                item.add(new Label("order", StringUtil.valueOf(getFirstItemOffset() + item.getIndex() + 1)));
                item.add(new Label("name", organization.getStringValue(NAME, getLocale())));
                item.add(new Label("code", organizationStrategy.getCode(organization)));
                item.add(new Label("shortName",organization.getStringValue(SHORT_NAME, getLocale())));
                item.add(new Label("parentShortName", organization.getParentShortName()));
                item.add(new Label("om", organization.getOperatingMonth(getLocale())));

                ScrollBookmarkablePageLink<WebPage> detailsLink = new ScrollBookmarkablePageLink<WebPage>("detailsLink",
                        organizationStrategy.getEditPage(),
                        organizationStrategy.getEditPageParams(organization.getObjectId(), null, null),
                        String.valueOf(organization.getObjectId()));
                detailsLink.add(new Label("editMessage", new AbstractReadOnlyModel<String>() {

                    @Override
                    public String getObject() {
                        if (DomainObjectAccessUtil.canAddNew(organizationStrategy, "organization")) {
                            return getString("edit");
                        } else {
                            return getString("view");
                        }
                    }
                }));
                item.add(detailsLink);

                //ready close operating month flag
                {
                    final Boolean readyCloseFlag = organization.isReadyCloseOperatingMonth();
                    AjaxLink<Void> setReadyCloseOperatingMonthLink = new AjaxLink<Void>("setReadyCloseOperatingMonthLink") {

                        @Override
                        public void onClick(AjaxRequestTarget target) {
                            setReadyCloseOperatingMonthDialog.open(target, organization);
                        }
                    };
                    setReadyCloseOperatingMonthLink.setVisibilityAllowed(readyCloseFlag != null && !readyCloseFlag);
                    item.add(setReadyCloseOperatingMonthLink);
                }

                //close operating month
                {
                    final Boolean readyCloseFlag = organization.isReadyCloseOperatingMonth();
                    AjaxLink<Void> closeOperatingMonthLink = new AjaxLink<Void>("closeOperatingMonthLink") {

                        @Override
                        public void onClick(AjaxRequestTarget target) {
                            organizationStrategy.closeOperatingMonth(organization);
                            target.add(content);
                        }
                    };
                    closeOperatingMonthLink.setVisibilityAllowed(readyCloseFlag != null && readyCloseFlag);
                    item.add(closeOperatingMonthLink);
                }
            }
        };
        filterForm.add(dataView);

        filterForm.add(new ArrowOrderByBorder("nameHeader",
                String.valueOf(NAME), dataProvider, dataView, content));
        filterForm.add(new ArrowOrderByBorder("codeHeader",
                String.valueOf(KeOrganizationStrategy.CODE), dataProvider, dataView, content));
        filterForm.add(new ArrowOrderByBorder("shortNameHeader",
                String.valueOf(SHORT_NAME), dataProvider, dataView, content));

        //Reset Action
        AjaxLink<Void> reset = new AjaxLink<Void>("reset") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                filterForm.clearInput();
                example.setObjectId(null);
                example.getAttributeExample(NAME).setValue(null);
                example.getAttributeExample(KeOrganizationStrategy.CODE).setValue(null);
                example.getAttributeExample(SHORT_NAME).setValue(null);
                example.addAdditionalParam(KeOrganizationStrategy.PARENT_SHORT_NAME_FILTER, null);
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

        //Navigator
        content.add(new PagingNavigator("navigator", dataView, getPreferencesPage(), content));
    }

    @Override
    protected List<ToolbarButton> getToolbarButtons(String id) {
        return ImmutableList.of(new AddItemButton(id) {

            @Override
            protected void onClick() {
                //DomainObjectList.onAddObject(this.getPage(), organizationStrategy, getTemplateSession());
            }

            @Override
            protected void onBeforeRender() {
                if (!DomainObjectAccessUtil.canAddNew(organizationStrategy, "organization")) {
                    setVisible(false);
                }
                super.onBeforeRender();
            }
        }, new CollapsibleSearchToolbarButton(id, searchPanel));
    }
}
