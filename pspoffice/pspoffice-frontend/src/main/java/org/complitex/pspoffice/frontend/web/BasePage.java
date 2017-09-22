package org.complitex.pspoffice.frontend.web;

import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.complitex.pspoffice.frontend.web.address.AddressListPage;
import org.complitex.pspoffice.frontend.web.address.building.BuildingListPage;
import org.complitex.pspoffice.frontend.web.entity.EntityListPage;
import org.complitex.pspoffice.frontend.web.person.PersonListPage;

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

        add(new BookmarkablePageLink("country", AddressListPage.class, new PageParameters().add("entity", "country")));
        add(new BookmarkablePageLink("region", AddressListPage.class, new PageParameters().add("entity", "region")));
        add(new BookmarkablePageLink("city", AddressListPage.class, new PageParameters().add("entity", "city")));
        add(new BookmarkablePageLink("district", AddressListPage.class, new PageParameters().add("entity", "district")));
        add(new BookmarkablePageLink("street", AddressListPage.class, new PageParameters().add("entity", "street")));
        add(new BookmarkablePageLink("building", BuildingListPage.class));

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
