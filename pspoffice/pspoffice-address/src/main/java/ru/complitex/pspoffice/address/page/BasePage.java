package ru.complitex.pspoffice.address.page;

import de.agilecoders.wicket.webjars.request.resource.WebjarsCssResourceReference;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import ru.complitex.pspoffice.address.catalog.page.*;
import ru.complitex.pspoffice.address.correction.page.*;
import ru.complitex.pspoffice.address.resource.CssResourceReference;
import ru.complitex.pspoffice.address.sync.page.SyncPage;
import ru.complitex.pspoffice.address.user.entity.UserRoles;

/**
 * @author Ivanov Anatoliy
 */
@AuthorizeInstantiation(UserRoles.ADMINISTRATOR)
public class BasePage extends WebPage {
    private final static WebjarsCssResourceReference FONT = new WebjarsCssResourceReference("font-awesome/current/css/all.css");

    public BasePage() {
        WebMarkupContainer catalog = new WebMarkupContainer("catalog");
        add(catalog);

        catalog.add(new BookmarkablePageLink<>("country", CountryPage.class));
        catalog.add(new BookmarkablePageLink<>("region", RegionPage.class));
        catalog.add(new BookmarkablePageLink<>("cityType", CityTypePage.class));
        catalog.add(new BookmarkablePageLink<>("city", CityPage.class));
        catalog.add(new BookmarkablePageLink<>("district", DistrictPage.class));
        catalog.add(new BookmarkablePageLink<>("streetType", StreetTypePage.class));
        catalog.add(new BookmarkablePageLink<>("street", StreetPage.class));
        catalog.add(new BookmarkablePageLink<>("house", HousePage.class));
        catalog.add(new BookmarkablePageLink<>("flat", FlatPage.class));
        catalog.add(new BookmarkablePageLink<>("organizationType", OrganizationTypePage.class));
        catalog.add(new BookmarkablePageLink<>("organization", OrganizationPage.class));

        WebMarkupContainer correction = new WebMarkupContainer("correction");
        add(correction);

        correction.add(new BookmarkablePageLink<>("country", CountryCorrectionPage.class));
        correction.add(new BookmarkablePageLink<>("region", RegionCorrectionPage.class));
        correction.add(new BookmarkablePageLink<>("cityType", CityTypeCorrectionPage.class));
        correction.add(new BookmarkablePageLink<>("city", CityCorrectionPage.class));
        correction.add(new BookmarkablePageLink<>("district", DistrictCorrectionPage.class));
        correction.add(new BookmarkablePageLink<>("streetType", StreetTypeCorrectionPage.class));
        correction.add(new BookmarkablePageLink<>("street", StreetCorrectionPage.class));
        correction.add(new BookmarkablePageLink<>("house", HouseCorrectionPage.class));
        correction.add(new BookmarkablePageLink<>("flat", FlatCorrectionPage.class));

        WebMarkupContainer setting = new WebMarkupContainer("setting");
        add(setting);

        setting.add(new BookmarkablePageLink<>("synchronization", SyncPage.class));

        add(new Link<>("logout") {
            @Override
            public void onClick() {
                getSession().invalidate();
            }
        });
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);

        response.render(CssHeaderItem.forReference(CssResourceReference.INSTANCE));

        response.render(CssHeaderItem.forReference(FONT));
    }
}
