package ru.complitex.pspoffice.frontend.web;

import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.PackageResourceReference;
import ru.complitex.pspoffice.frontend.web.card.ApartmentCardListPage;
import ru.complitex.pspoffice.frontend.web.domain.DomainListPage;
import ru.complitex.pspoffice.frontend.web.entity.EntityListPage;
import ru.complitex.pspoffice.frontend.web.person.PersonListPage;

/**
 * @author Anatoly A. Ivanov
 * 26.06.2017 13:49
 */
public abstract class BasePage extends WebPage{
    protected BasePage() {
        add(new Label("title"){
            @Override
            protected void onConfigure() {
                setDefaultModel(getTitleModel());
            }
        });

        add(new BookmarkablePageLink("headerLink", HomePage.class));

        add(new BookmarkablePageLink("personsLink", PersonListPage.class));
        add(new BookmarkablePageLink("apartmentCards", ApartmentCardListPage.class));

        add(new BookmarkablePageLink("country", DomainListPage.class, new PageParameters().add("entity", "country")));
        add(new BookmarkablePageLink("region", DomainListPage.class, new PageParameters().add("entity", "region")));
        add(new BookmarkablePageLink("city", DomainListPage.class, new PageParameters().add("entity", "city")));
        add(new BookmarkablePageLink("district", DomainListPage.class, new PageParameters().add("entity", "district")));
        add(new BookmarkablePageLink("street", DomainListPage.class, new PageParameters().add("entity", "street")));
        add(new BookmarkablePageLink("building_address", DomainListPage.class, new PageParameters().add("entity", "building_address")));

        add(new BookmarkablePageLink("organization", DomainListPage.class, new PageParameters().add("entity", "organization")));
        add(new BookmarkablePageLink("organization_type", DomainListPage.class, new PageParameters().add("entity", "organization_type")));
        add(new BookmarkablePageLink("owner_relationship", DomainListPage.class, new PageParameters().add("entity", "owner_relationship")));
        add(new BookmarkablePageLink("ownership_form", DomainListPage.class, new PageParameters().add("entity", "ownership_form")));
        add(new BookmarkablePageLink("registration_type", DomainListPage.class, new PageParameters().add("entity", "registration_type")));
        add(new BookmarkablePageLink("document_type", DomainListPage.class, new PageParameters().add("entity", "document_type")));
        add(new BookmarkablePageLink("military_service_relation", DomainListPage.class, new PageParameters().add("entity", "military_service_relation")));
        add(new BookmarkablePageLink("departure_reason", DomainListPage.class, new PageParameters().add("entity", "departure_reason")));
        add(new BookmarkablePageLink("housing_rights", DomainListPage.class, new PageParameters().add("entity", "housing_rights")));

        add(new BookmarkablePageLink("entity", EntityListPage.class));
    }

    protected IModel<String> getTitleModel(){
        return Model.of("");
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);

        response.render(CssHeaderItem.forReference(new PackageResourceReference(BasePage.class, "/css/pspoffice.css")));
    }
}
