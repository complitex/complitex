package ru.complitex.eirc.service_provider_account.web.list;

import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.Component;
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
import ru.complitex.address.entity.AddressEntity;
import ru.complitex.address.util.AddressRenderer;
import ru.complitex.common.entity.FilterWrapper;
import ru.complitex.common.entity.StringLocale;
import ru.complitex.common.strategy.StringLocaleBean;
import ru.complitex.common.web.component.ShowMode;
import ru.complitex.common.web.component.datatable.DataProvider;
import ru.complitex.common.web.component.organization.OrganizationIdPicker;
import ru.complitex.common.web.component.paging.PagingNavigator;
import ru.complitex.common.web.component.scroll.ScrollBookmarkablePageLink;
import ru.complitex.common.web.component.search.CollapsibleSearchPanel;
import ru.complitex.common.web.component.search.ISearchCallback;
import ru.complitex.common.web.component.search.IToggleCallback;
import ru.complitex.template.web.component.toolbar.AddItemButton;
import ru.complitex.template.web.component.toolbar.ToolbarButton;
import ru.complitex.template.web.component.toolbar.search.CollapsibleSearchToolbarButton;
import ru.complitex.template.web.security.SecurityRole;
import ru.complitex.template.web.template.TemplatePage;
import ru.complitex.eirc.dictionary.entity.Address;
import ru.complitex.eirc.dictionary.entity.OrganizationType;
import ru.complitex.eirc.dictionary.entity.Person;
import ru.complitex.eirc.eirc_account.entity.EircAccount;
import ru.complitex.eirc.service.entity.Service;
import ru.complitex.eirc.service.service.ServiceBean;
import ru.complitex.eirc.service.web.component.ServicePicker;
import ru.complitex.eirc.service_provider_account.entity.ServiceProviderAccount;
import ru.complitex.eirc.service_provider_account.service.ServiceProviderAccountBean;
import ru.complitex.eirc.service_provider_account.web.edit.ServiceProviderAccountEdit;

import javax.ejb.EJB;
import java.util.List;
import java.util.Map;

import static ru.complitex.common.util.PageUtil.newSorting;

/**
 * @author Pavel Sknar
 */
@AuthorizeInstantiation(SecurityRole.AUTHORIZED)
public class ServiceProviderAccountList extends TemplatePage {

    private static final List<String> searchFilters = ImmutableList.of("country", "region", "city", "street", "building", "apartment", "room");

    @EJB
    private ServiceProviderAccountBean serviceProviderAccountBean;

    @EJB
    private StringLocaleBean stringLocaleBean;

    @EJB
    private ServiceBean serviceBean;

    private WebMarkupContainer container;
    private DataView<ServiceProviderAccount> dataView;
    private CollapsibleSearchPanel searchPanel;

    private ServiceProviderAccount filterObject = new ServiceProviderAccount(new EircAccount());
    private Address filterAddress;

    private Boolean toggle = false;

    public ServiceProviderAccountList() {
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

        final StringLocale stringLocale = stringLocaleBean.convert(getLocale());

        //Search
        final List<String> searchFilters = getSearchFilters();
        container.setVisible(true);
        add(container);

        final IModel<ShowMode> showModeModel = new Model<>(ShowMode.ACTIVE);
        searchPanel = new CollapsibleSearchPanel("searchPanel", getTemplateSession().getGlobalSearchComponentState(),
                searchFilters, new ISearchCallback() {
            @Override
            public void found(Component component, Map<String, Long> ids, AjaxRequestTarget target) {
                AddressEntity addressEntity = null;
                Long filterValue = null;
                for (int i = searchFilters.size() - 1; i >= 0; i--) {
                    String filterField = searchFilters.get(i);
                    filterValue = ids.get(filterField);
                    if (filterValue != null && filterValue > -1L) {
                        addressEntity = AddressEntity.valueOf(StringUtils.upperCase(filterField));
                        break;
                    }
                }
                if (addressEntity != null) {
                    filterAddress = new Address(filterValue, addressEntity);
                    filterObject.getEircAccount().setAddress(filterAddress);
                } else {
                    filterObject.getEircAccount().setAddress(null);
                }
            }
        }, ShowMode.ALL, true, showModeModel, new IToggleCallback() {
            @Override
            public void visible(boolean newState, AjaxRequestTarget target) {
                toggle = newState;
                target.add(container);
            }
        });
        add(searchPanel);
        searchPanel.initialize();

        //Form
        final Form filterForm = new Form("filterForm");
        container.add(filterForm);

        //Data Provider
        final DataProvider<ServiceProviderAccount> dataProvider = new DataProvider<ServiceProviderAccount>() {

            @Override
            protected Iterable<? extends ServiceProviderAccount> getData(long first, long count) {
                FilterWrapper<ServiceProviderAccount> filterWrapper = FilterWrapper.of(filterObject, first, count);
                filterWrapper.setAscending(getSort().isAscending());
                filterWrapper.setSortProperty(getSort().getProperty());
                filterWrapper.getMap().put("address", Boolean.TRUE);
                filterWrapper.setStringLocale(stringLocale);

                return serviceProviderAccountBean.getServiceProviderAccounts(filterWrapper);
            }

            @Override
            protected Long getSize() {
                FilterWrapper<ServiceProviderAccount> filterWrapper = FilterWrapper.of(filterObject);
                return serviceProviderAccountBean.getCount(filterWrapper);
            }
        };
        dataProvider.setSort("spa_account_number", SortOrder.ASCENDING);

        //Data View
        dataView = new DataView<ServiceProviderAccount>("data", dataProvider, 1) {

            @Override
            protected void populateItem(Item<ServiceProviderAccount> item) {
                final ServiceProviderAccount serviceProviderAccount = item.getModelObject();

                item.add(new Label("accountNumber", serviceProviderAccount.getAccountNumber()));
                item.add(new Label("service", serviceProviderAccount.getService().getName(stringLocale) + " (" + serviceProviderAccount.getService().getCode() + ")" ));
                item.add(new Label("serviceProvider", serviceProviderAccount.getOrganizationName()));
                item.add(new Label("person", serviceProviderAccount.getPerson() != null? serviceProviderAccount.getPerson().toString(): ""));
                item.add(new Label("address", new AbstractReadOnlyModel<String>() {

                    @Override
                    public String getObject() {
                        return serviceProviderAccount.getEircAccount().getAddress() != null?
                                (
                                        toggle?
                                                AddressRenderer.displayAddress(
                                                        serviceProviderAccount.getEircAccount().getAddress().getCityType(), serviceProviderAccount.getEircAccount().getAddress().getCity(),
                                                        serviceProviderAccount.getEircAccount().getAddress().getStreetType(), serviceProviderAccount.getEircAccount().getAddress().getStreet(),
                                                        serviceProviderAccount.getEircAccount().getAddress().getBuilding(), null, serviceProviderAccount.getEircAccount().getAddress().getApartment(),
                                                        serviceProviderAccount.getEircAccount().getAddress().getRoom(), getLocale())
                                                :
                                                AddressRenderer.displayAddress(
                                                        serviceProviderAccount.getEircAccount().getAddress().getStreetType(), serviceProviderAccount.getEircAccount().getAddress().getStreet(),
                                                        serviceProviderAccount.getEircAccount().getAddress().getBuilding(), null, serviceProviderAccount.getEircAccount().getAddress().getApartment(),
                                                        serviceProviderAccount.getEircAccount().getAddress().getRoom(), getLocale())
                                ): "";
                    }
                }));

                ScrollBookmarkablePageLink<WebPage> detailsLink = new ScrollBookmarkablePageLink<WebPage>("detailsLink",
                        getEditPage(), getEditPageParams(serviceProviderAccount.getId()),
                        String.valueOf(serviceProviderAccount.getId()));
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
        filterForm.add(newSorting("header.", dataProvider, dataView, filterForm, true, "spaAccountNumber", "serviceName", "spaOrganizationName", "spaPerson", "eircAccountAddress"));

        //Filters
        filterForm.add(new TextField<>("accountNumberFilter", new PropertyModel<String>(filterObject, "accountNumber")));

        filterForm.add(new TextField<>("personFilter", new Model<String>() {

            @Override
            public String getObject() {
                return filterObject.getPerson() != null? filterObject.getPerson().toString() : "";
            }

            @Override
            public void setObject(String fio) {
                if (StringUtils.isBlank(fio)) {
                    filterObject.setPerson(null);
                } else {
                    fio = fio.trim();
                    String[] personFio = fio.split(" ", 3);

                    Person person = new Person();

                    if (personFio.length > 0) {
                        person.setLastName(personFio[0]);
                    }
                    if (personFio.length > 1) {
                        person.setFirstName(personFio[1]);
                    }
                    if (personFio.length > 2) {
                        person.setMiddleName(personFio[2]);
                    }

                    filterObject.setPerson(person);
                }
            }
        }));

        filterForm.add(new TextField<>("addressFilter", new Model<String>()));

        filterForm.add(new ServicePicker("serviceFilter", new PropertyModel<Service>(filterObject, "service")));

        filterForm.add(new OrganizationIdPicker("organizationId", new PropertyModel<Long>(filterObject, "organizationId"),
                Long.valueOf(OrganizationType.SERVICE_PROVIDER.getId())));

        //Reset Action
        AjaxLink reset = new AjaxLink("reset") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                filterForm.clearInput();
                filterObject.getEircAccount().setAddress(null);
                filterObject.setPerson(null);
                filterObject.setAccountNumber(null);
                filterObject.setService(null);
                filterObject.setOrganizationId(null);
                filterObject.setOrganizationName(null);

                target.add(container);
            }
        };
        filterForm.add(reset);

        //Submit Action
        AjaxButton submit = new AjaxButton("submit", filterForm) {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                filterObject.getEircAccount().setAddress(filterAddress);

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
        }, new CollapsibleSearchToolbarButton(id, searchPanel));
    }

    private Class<? extends Page> getEditPage() {
        return ServiceProviderAccountEdit.class;
    }

    private PageParameters getEditPageParams(Long id) {
        PageParameters parameters = new PageParameters();
        if (id != null) {
            parameters.add("serviceProviderAccountId", id);
        }
        return parameters;
    }

    private List<String> getSearchFilters() {
        return searchFilters;
    }
}

