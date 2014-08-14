package org.complitex.pspoffice.person.strategy.web.list.apartment_card.grid;

import com.google.common.collect.ImmutableList;
import org.apache.wicket.Component;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.Strings;
import org.complitex.address.service.AddressRendererBean;
import org.complitex.dictionary.entity.DomainObject;
import org.complitex.dictionary.strategy.organization.IOrganizationStrategy;
import org.complitex.dictionary.util.StringUtil;
import org.complitex.dictionary.web.component.back.BackInfo;
import org.complitex.dictionary.web.component.back.BackInfoManager;
import org.complitex.organization.web.OrganizationMenu;
import org.complitex.pspoffice.person.menu.OperationMenu;
import org.complitex.pspoffice.person.strategy.entity.grid.ApartmentCardsGridEntity;
import org.complitex.pspoffice.person.strategy.entity.grid.ApartmentCardsGridFilter;
import org.complitex.pspoffice.person.strategy.service.ApartmentCardsGridBean;
import org.complitex.pspoffice.person.strategy.web.component.AddApartmentCardButton;
import org.complitex.pspoffice.person.strategy.web.edit.apartment_card.ApartmentCardEdit;
import org.complitex.pspoffice.person.strategy.web.list.apartment_card.ApartmentCardSearch;
import org.complitex.template.web.component.toolbar.ToolbarButton;
import org.complitex.template.web.security.SecurityRole;
import org.complitex.template.web.template.MenuManager;
import org.complitex.template.web.template.TemplatePage;

import javax.ejb.EJB;
import java.util.List;

/**
 *
 * @author Artem
 */
@AuthorizeInstantiation(SecurityRole.AUTHORIZED)
public final class ApartmentCardsGrid extends TemplatePage {

    private static final String PAGE_SESSION_KEY = "apartment_cards_grid_page";

    @EJB
    private ApartmentCardsGridBean apartmentCardsGridBean;

    @EJB(name = IOrganizationStrategy.BEAN_NAME)
    private IOrganizationStrategy organizationStrategy;

    @EJB
    private AddressRendererBean addressRendererBean;

    private final long apartmentId;
    private final String backInfoSessionKey;

    private static class ApartmentCardsGridBackInfo extends BackInfo {

        final long apartmentId;
        final String backInfoSessionKey;

        ApartmentCardsGridBackInfo(long apartmentId, String backInfoSessionKey) {
            this.apartmentId = apartmentId;
            this.backInfoSessionKey = backInfoSessionKey;
        }

        @Override
        public void back(Component pageComponent) {
            MenuManager.setMenuItem(OperationMenu.REGISTRATION_MENU_ITEM);
            pageComponent.setResponsePage(new ApartmentCardsGrid(apartmentId, backInfoSessionKey));
        }
    }

    public ApartmentCardsGrid(long apartmentId){
        this(apartmentId, null);
    }

    public ApartmentCardsGrid(final long apartmentId, final String backInfoSessionKey) {
        this.apartmentId = apartmentId;
        this.backInfoSessionKey = backInfoSessionKey;

        IModel<String> labelModel = new StringResourceModel("label", null,
                new Object[]{addressRendererBean.displayAddress("apartment", apartmentId, getLocale())});

        add(new Label("title", labelModel));
        add(new Label("label", labelModel));

        //Filter
        final ApartmentCardsGridFilter filter = apartmentCardsGridBean.newFilter(apartmentId, getLocale());

        final List<ApartmentCardsGridEntity> apartmentCardsGridEntities = apartmentCardsGridBean.find(filter);

        //List View
        ListView<ApartmentCardsGridEntity> apartmentCards =
                new ListView<ApartmentCardsGridEntity>("apartmentCards", apartmentCardsGridEntities) {

                    @Override
                    protected void populateItem(ListItem<ApartmentCardsGridEntity> item) {
                        final ApartmentCardsGridEntity apartmentCardsGridEntity = item.getModelObject();

                        //order
                        item.add(new Label("order", StringUtil.valueOf(item.getIndex() + 1)));

                        //apartment card
                        Link<Void> apartmentCardLink = new Link<Void>("apartmentCardLink") {

                            @Override
                            public void onClick() {
                                BackInfoManager.put(this, PAGE_SESSION_KEY, gridBackInfo(apartmentId, backInfoSessionKey));
                                setResponsePage(new ApartmentCardEdit(apartmentCardsGridEntity.getApartmentCardId(),
                                        PAGE_SESSION_KEY));
                            }
                        };
                        item.add(apartmentCardLink);
                        apartmentCardLink.add(new Label("apartmentCard", apartmentCardsGridEntity.getApartmentCard()));

                        //owner
                        item.add(new Label("owner", apartmentCardsGridEntity.getOwner()));

                        //ownership form
                        item.add(new Label("ownershipForm", apartmentCardsGridEntity.getOwnershipForm()));

                        //registered
                        final int registered = apartmentCardsGridEntity.getRegistered();
                        Link<Void> registeredLink = new Link<Void>("registeredLink") {

                            @Override
                            public void onClick() {
                                BackInfoManager.put(this, PAGE_SESSION_KEY, gridBackInfo(apartmentId, backInfoSessionKey));
                                setResponsePage(new RegistrationsGrid(apartmentCardsGridEntity.getApartmentCardId(),
                                        PAGE_SESSION_KEY));
                            }
                        };
                        registeredLink.setVisible(registered > 0);
                        item.add(registeredLink);
                        registeredLink.add(new Label("registered", String.valueOf(registered)));

                        //organization
                        final DomainObject organization = apartmentCardsGridEntity.getOrganization();
                        Link<Void> organizationLink = new Link<Void>("organizationLink") {

                            @Override
                            public void onClick() {
                                MenuManager.setMenuItem(OrganizationMenu.ORGANIZATION_MENU_ITEM);
                                PageParameters params = organizationStrategy.getEditPageParams(organization.getId(), null, null);
                                BackInfoManager.put(this, PAGE_SESSION_KEY, gridBackInfo(apartmentId, backInfoSessionKey));
                                params.set(BACK_INFO_SESSION_KEY, PAGE_SESSION_KEY);
                                setResponsePage(organizationStrategy.getEditPage(), params);
                            }
                        };
                        organizationLink.setVisible(organization != null);
                        item.add(organizationLink);
                        organizationLink.add(new Label("organizationCode", new AbstractReadOnlyModel<String>() {

                            @Override
                            public String getObject() {
                                return organizationStrategy.getCode(organization);
                            }
                        }));
                    }
                };
        add(apartmentCards);

        Link<Void> backSearch = new Link<Void>("backSearch") {

            @Override
            public void onClick() {
                setResponsePage(ApartmentCardSearch.class);
            }
        };
        add(backSearch);

        final BackInfo backInfo = !Strings.isEmpty(backInfoSessionKey) ? BackInfoManager.get(getPage(), backInfoSessionKey) : null;
        Link<Void> back = new Link<Void>("back") {

            @Override
            public void onClick() {
                backInfo.back(this);
            }
        };
        back.setVisible(backInfo != null);
        add(back);
    }

    private static BackInfo gridBackInfo(long apartmentId, String backInfoSessionKey) {
        return new ApartmentCardsGridBackInfo(apartmentId, backInfoSessionKey);
    }

    @Override
    protected List<? extends ToolbarButton> getToolbarButtons(String id) {
        return ImmutableList.of(new AddApartmentCardButton(id) {

            @Override
            protected void onClick() {
                BackInfoManager.put(this, PAGE_SESSION_KEY, gridBackInfo(apartmentId, backInfoSessionKey));
                setResponsePage(new ApartmentCardEdit("apartment", apartmentId, PAGE_SESSION_KEY));
            }
        });
    }
}
