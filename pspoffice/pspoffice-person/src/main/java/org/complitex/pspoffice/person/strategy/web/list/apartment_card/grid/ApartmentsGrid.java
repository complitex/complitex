/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.strategy.web.list.apartment_card.grid;

import com.google.common.collect.ImmutableList;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.SharedResourceReference;
import org.apache.wicket.util.string.Strings;
import org.complitex.address.menu.AddressMenu;
import org.complitex.address.service.AddressRendererBean;
import org.complitex.address.strategy.apartment.ApartmentStrategy;
import org.complitex.address.strategy.building.BuildingStrategy;
import org.complitex.address.strategy.room.RoomStrategy;
import org.complitex.dictionary.entity.DomainObject;
import org.complitex.dictionary.service.LocaleBean;
import org.complitex.dictionary.service.SessionBean;
import org.complitex.dictionary.strategy.IStrategy;
import org.complitex.dictionary.strategy.organization.IOrganizationStrategy;
import org.complitex.dictionary.util.StringUtil;
import org.complitex.dictionary.web.component.back.BackInfo;
import org.complitex.dictionary.web.component.back.BackInfoManager;
import org.complitex.dictionary.web.component.datatable.DataProvider;
import org.complitex.dictionary.web.component.paging.PagingNavigator;
import org.complitex.organization.web.OrganizationMenu;
import org.complitex.pspoffice.person.menu.OperationMenu;
import org.complitex.pspoffice.person.strategy.entity.ApartmentCard;
import org.complitex.pspoffice.person.strategy.entity.Person;
import org.complitex.pspoffice.person.strategy.entity.grid.ApartmentsGridEntity;
import org.complitex.pspoffice.person.strategy.entity.grid.ApartmentsGridFilter;
import org.complitex.pspoffice.person.strategy.service.ApartmentsGridBean;
import org.complitex.pspoffice.person.strategy.web.component.ApartmentCreateDialog;
import org.complitex.pspoffice.person.strategy.web.component.RoomCreateDialog;
import org.complitex.pspoffice.person.strategy.web.edit.apartment_card.ApartmentCardEdit;
import org.complitex.pspoffice.person.strategy.web.list.apartment_card.ApartmentCardSearch;
import org.complitex.template.web.component.toolbar.ToolbarButton;
import org.complitex.template.web.security.SecurityRole;
import org.complitex.template.web.template.MenuManager;
import org.complitex.template.web.template.TemplatePage;

import javax.ejb.EJB;
import java.util.List;
import java.util.Locale;

/**
 *
 * @author Artem
 */
@AuthorizeInstantiation(SecurityRole.AUTHORIZED)
public final class ApartmentsGrid extends TemplatePage {

    private static final String PAGE_SESSION_KEY = "apartments_grid_page";

    @EJB
    private ApartmentStrategy apartmentStrategy;

    @EJB
    private BuildingStrategy buildingStrategy;

    @EJB
    private RoomStrategy roomStrategy;

    @EJB
    private ApartmentsGridBean apartmentGridBean;

    @EJB(name = IOrganizationStrategy.BEAN_NAME)
    private IOrganizationStrategy organizationStrategy;

    @EJB
    private AddressRendererBean addressRendererBean;

    @EJB
    private LocaleBean localeBean;

    @EJB
    private SessionBean sessionBean;

    private final Locale systemLocale = localeBean.getSystemLocale();
    private final long buildingId;
    private final ApartmentCreateDialog apartmentCreateDialog;
    private final RoomCreateDialog roomCreateDialog;

    private static class ApartmentsGridBackInfo extends BackInfo {

        final long buildingId;
        final String backInfoSessionKey;

        ApartmentsGridBackInfo(long buildingId, String backInfoSessionKey) {
            this.buildingId = buildingId;
            this.backInfoSessionKey = backInfoSessionKey;
        }

        @Override
        public void back(Component pageComponent) {
            MenuManager.setMenuItem(OperationMenu.REGISTRATION_MENU_ITEM);
            pageComponent.setResponsePage(new ApartmentsGrid(buildingId, backInfoSessionKey));
        }
    }

    public ApartmentsGrid(long buildingId) {
        this(buildingId, null);
    }

    public ApartmentsGrid(final long buildingId, final String backInfoSessionKey) {
        this.buildingId = buildingId;

        IModel<String> labelModel = new StringResourceModel("label", null,
                new Object[]{addressRendererBean.displayAddress("building", buildingId, getLocale())});

        add(new Label("title", labelModel));
        add(new Label("label", labelModel));

        final WebMarkupContainer content = new WebMarkupContainer("content");
        content.setOutputMarkupPlaceholderTag(true);
        add(content);

        //Filter
        final ApartmentsGridFilter filter = apartmentGridBean.newFilter(buildingId, getLocale());

        //Form
        final Form<Void> filterForm = new Form<Void>("filterForm");
        content.add(filterForm);

        //Data Provider
        final DataProvider<ApartmentsGridEntity> dataProvider = new DataProvider<ApartmentsGridEntity>() {

            @Override
            protected Iterable<ApartmentsGridEntity> getData(long first, long count) {
                filter.setStart(first);
                filter.setSize(count);
                return apartmentGridBean.find(filter);
            }

            @Override
            protected int getSize() {
                return apartmentGridBean.count(filter);
            }
        };

        //Filters
        //number
        filterForm.add(new TextField<String>("numberFilter", new PropertyModel<String>(filter, "number")));

        //Data View
        DataView<ApartmentsGridEntity> apartments = new DataView<ApartmentsGridEntity>("apartments", dataProvider, 1) {

            @Override
            protected void populateItem(Item<ApartmentsGridEntity> item) {
                final ApartmentsGridEntity apartmentsGridEntity = item.getModelObject();

                //order
                item.add(new Label("order", StringUtil.valueOf(getFirstItemOffset() + item.getIndex() + 1)));

                //apartment/room
                Link<Void> objectLink = new Link<Void>("objectLink") {

                    @Override
                    public void onClick() {
                        IStrategy strategy = "apartment".equals(apartmentsGridEntity.getEntity()) ? apartmentStrategy
                                : roomStrategy;
                        MenuManager.setMenuItem(strategy.getEntityTable() + AddressMenu.ADDRESS_MENU_ITEM_SUFFIX);
                        PageParameters params = strategy.getEditPageParams(apartmentsGridEntity.getObjectId(), null, null);
                        BackInfoManager.put(this, PAGE_SESSION_KEY, gridBackInfo(buildingId, backInfoSessionKey));
                        params.set(BACK_INFO_SESSION_KEY, PAGE_SESSION_KEY);
                        setResponsePage(strategy.getEditPage(), params);
                    }
                };
                item.add(objectLink);
                objectLink.add(new Label("number", apartmentsGridEntity.getNumber()));

                //rooms
                item.add(new ListView<DomainObject>("rooms", apartmentsGridEntity.getRooms()) {

                    @Override
                    protected void populateItem(ListItem<DomainObject> item) {
                        final DomainObject room = item.getModelObject();

                        Link<Void> roomLink = new Link<Void>("roomLink") {

                            @Override
                            public void onClick() {
                                MenuManager.setMenuItem("room" + AddressMenu.ADDRESS_MENU_ITEM_SUFFIX);
                                PageParameters params = roomStrategy.getEditPageParams(room.getId(), null, null);
                                BackInfoManager.put(this, PAGE_SESSION_KEY, gridBackInfo(buildingId, backInfoSessionKey));
                                params.set(BACK_INFO_SESSION_KEY, PAGE_SESSION_KEY);
                                setResponsePage(roomStrategy.getEditPage(), params);
                            }
                        };
                        item.add(roomLink);
                        roomLink.add(new Label("room", roomStrategy.displayDomainObject(room, getLocale())));
                    }
                });

                //apartment cards
                final boolean hasApartmentCards = !apartmentsGridEntity.getApartmentCards().isEmpty();
                WebMarkupContainer apartmentCardsContainer = new WebMarkupContainer("apartmentCardsContainer");
                apartmentCardsContainer.setVisible(hasApartmentCards);
                item.add(apartmentCardsContainer);
                apartmentCardsContainer.add(new ListView<ApartmentCard>("apartmentCards",
                        apartmentsGridEntity.getApartmentCards()) {

                    @Override
                    protected void populateItem(ListItem<ApartmentCard> item) {
                        final ApartmentCard apartmentCard = item.getModelObject();

                        Link<Void> apartmentCardLink = new Link<Void>("apartmentCardLink") {

                            @Override
                            public void onClick() {
                                BackInfoManager.put(this, PAGE_SESSION_KEY, gridBackInfo(buildingId, backInfoSessionKey));
                                setResponsePage(new ApartmentCardEdit(apartmentCard.getId(), PAGE_SESSION_KEY));
                            }
                        };
                        item.add(apartmentCardLink);
                        apartmentCardLink.add(new Label("apartmentCard", apartmentCardInfo(apartmentCard, getLocale(), systemLocale)));
                    }
                });
                Link<Void> apartmentCardsGridLink = new Link<Void>("apartmentCardsGridLink") {

                    @Override
                    public void onClick() {
                        BackInfoManager.put(this, PAGE_SESSION_KEY, gridBackInfo(buildingId, backInfoSessionKey));
                        setResponsePage(new ApartmentCardsGrid(apartmentsGridEntity.getObjectId(), PAGE_SESSION_KEY));
                    }
                };
                apartmentCardsGridLink.setVisible(!hasApartmentCards && "apartment".equals(apartmentsGridEntity.getEntity()));
                item.add(apartmentCardsGridLink);

                //registered
                item.add(new Label("registered", String.valueOf(apartmentsGridEntity.getRegistered())));

                //organizations
                item.add(new ListView<DomainObject>("organizations", apartmentsGridEntity.getOrganizations()) {

                    @Override
                    protected void populateItem(ListItem<DomainObject> item) {
                        final DomainObject organization = item.getModelObject();

                        Link<Void> organizationLink = new Link<Void>("organizationLink") {

                            @Override
                            public void onClick() {
                                MenuManager.setMenuItem(OrganizationMenu.ORGANIZATION_MENU_ITEM);
                                PageParameters params = organizationStrategy.getEditPageParams(organization.getId(), null, null);
                                BackInfoManager.put(this, PAGE_SESSION_KEY, gridBackInfo(buildingId, backInfoSessionKey));
                                params.set(BACK_INFO_SESSION_KEY, PAGE_SESSION_KEY);
                                setResponsePage(organizationStrategy.getEditPage(), params);
                            }
                        };
                        item.add(organizationLink);
                        organizationLink.add(new Label("organizationCode",
                                organizationStrategy.getCode(organization)));
                    }
                });
            }
        };
        filterForm.add(apartments);

        //Reset Action
        AjaxLink<Void> reset = new AjaxLink<Void>("reset") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                filterForm.clearInput();
                filter.reset();
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

        content.add(new PagingNavigator("navigator", apartments, content));

        Link<Void> backSearch = new Link<Void>("backSearch") {

            @Override
            public void onClick() {
                setResponsePage(ApartmentCardSearch.class);
            }
        };
        content.add(backSearch);

        final BackInfo backInfo = !Strings.isEmpty(backInfoSessionKey) ? BackInfoManager.get(getPage(), backInfoSessionKey) : null;
        Link<Void> back = new Link<Void>("back") {

            @Override
            public void onClick() {
                backInfo.back(this);
            }
        };
        back.setVisible(backInfo != null);
        content.add(back);

        final List<Long> userOrganizationObjectIds = sessionBean.getUserOrganizationObjectIds();

        apartmentCreateDialog = new ApartmentCreateDialog("apartmentCreateDialog", userOrganizationObjectIds) {

            @Override
            protected void onCreate(AjaxRequestTarget target, DomainObject object) {
                target.add(content);
            }

            @Override
            protected void afterBulkSave(AjaxRequestTarget target, String numbers, boolean operationSuccessed) {
                super.afterBulkSave(target, numbers, operationSuccessed);
                target.add(content);
            }
        };
        add(apartmentCreateDialog);

        roomCreateDialog = new RoomCreateDialog("roomCreateDialog", userOrganizationObjectIds) {

            @Override
            protected void onCreate(AjaxRequestTarget target, DomainObject object) {
                target.add(content);
            }

            @Override
            protected void afterBulkSave(AjaxRequestTarget target, String numbers, boolean operationSuccessed) {
                super.afterBulkSave(target, numbers, operationSuccessed);
                target.add(content);
            }
        };
        add(roomCreateDialog);
    }

    private static BackInfo gridBackInfo(long buildingId, String backInfoSessionKey) {
        return new ApartmentsGridBackInfo(buildingId, backInfoSessionKey);
    }

    private static String apartmentCardInfo(ApartmentCard apartmentCard, Locale locale, Locale systemLocale) {
        Person owner = apartmentCard.getOwner();
        return owner.getLastName(locale, systemLocale) + " "
                + owner.getFirstName(locale, systemLocale).substring(0, 1).toUpperCase(locale) + "."
                + owner.getMiddleName(locale, systemLocale).substring(0, 1).toUpperCase(locale) + ".";
    }

    @Override
    protected List<? extends ToolbarButton> getToolbarButtons(String id) {
        final DomainObject building = buildingStrategy.findById(buildingId, true);

        class AddApartmentRoomButton extends ToolbarButton {

            static final String IMAGE_SRC = "images/icon-addItem.gif";
            static final String TITLE_KEY = "add";

            AddApartmentRoomButton(String id, String entity) {
                super(id, new SharedResourceReference(IMAGE_SRC), TITLE_KEY + Strings.capitalize(entity), true);
            }
        }

        return ImmutableList.of(
                new AddApartmentRoomButton(id, "apartment") {

                    @Override
                    protected void onClick(AjaxRequestTarget target) {
                        apartmentCreateDialog.open(target, null, building);
                    }
                },
                new AddApartmentRoomButton(id, "room") {

                    @Override
                    protected void onClick(AjaxRequestTarget target) {
                        roomCreateDialog.open(target, null, "building", building);
                    }
                });
    }
}
