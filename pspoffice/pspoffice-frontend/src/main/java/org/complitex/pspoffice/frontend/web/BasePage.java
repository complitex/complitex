package org.complitex.pspoffice.frontend.web;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.complitex.pspoffice.frontend.web.address.AddressListPage;
import org.complitex.pspoffice.frontend.web.address.building.BuildingListPage;
import org.complitex.pspoffice.frontend.web.person.PersonListPage;
import org.complitex.pspoffice.frontend.web.person.PersonPage;

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

        add(new BookmarkablePageLink("personsLink", PersonListPage.class));
        add(new BookmarkablePageLink("person", PersonPage.class));

        add(new BookmarkablePageLink("country", AddressListPage.class, new PageParameters().add("entity", "country")));
        add(new BookmarkablePageLink("region", AddressListPage.class, new PageParameters().add("entity", "region")));
        add(new BookmarkablePageLink("city", AddressListPage.class, new PageParameters().add("entity", "city")));
        add(new BookmarkablePageLink("district", AddressListPage.class, new PageParameters().add("entity", "district")));
        add(new BookmarkablePageLink("street", AddressListPage.class, new PageParameters().add("entity", "street")));
        add(new BookmarkablePageLink("building", BuildingListPage.class));
    }

    protected IModel<String> getTitleModel(){
        return Model.of("");
    }
}
