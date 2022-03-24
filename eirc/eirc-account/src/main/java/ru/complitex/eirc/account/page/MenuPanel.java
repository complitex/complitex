package ru.complitex.eirc.account.page;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;
import ru.complitex.address.page.*;
import ru.complitex.correction.page.*;
import ru.complitex.organization.page.OrganizationPage;
import ru.complitex.organization.page.OrganizationTypePage;

/**
 * @author Ivanov Anatoliy
 */
public class MenuPanel extends Panel {
    public MenuPanel(String id) {
        super(id);

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
    }
}
